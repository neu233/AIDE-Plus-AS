package com.aide.ui.build.android;

import abcd.wf;
import abcd.xf;
import android.os.Build;
import com.aide.common.AppLog;
import com.aide.common.StreamUtilities;
import com.aide.ui.build.android.AaptService;
import com.aide.ui.project.AndroidProjectSupport;
import com.aide.ui.util.FileSystem;
import com.probelytics.annotation.MethodMark;
import io.github.zeroaicy.aide.aapt2.Aapt2Task;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.util.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.text.TextUtils;
import io.github.zeroaicy.aide.utils.AndroidManifestParser;
import com.aide.ui.ServiceContainer;
import com.aide.ui.services.ProjectService;

public class AaptService$Task {
	// res的依赖map key res_dir_path-> value res_dir_path_list
    public final Map<String, List<String>> resLibraryMap;

    public final Map<String, String> genPackageNameMap;

	// aaptPath
    public final String aaptPath;

	//androidJar File
    public final String androidSdkFilePath;

    public final List<String> variantManifestPaths;


    final AaptService aaptService;

	// aManifestMap
	// key: injected_xml value: original_manifest_xml
    public final Map<String, String> androidManifestMap;
	// injectedAManifestMap
	// key: gen路径 value: injected_xml
	public final Map<String, String> injectedAndroidManifestMap;
	// mergedAManifestMap
	// key: gen路径 value: merged_xml
    public final Map<String, String> mergedAndroidManifestMap;

    public final List<String> assetDirPaths;

    private boolean XL;

	// mainProjectGenDir
    public final String mainProjectGenDir;

    public boolean isBuildRefresh;

	//resource.ap_
    public final String resourcesApPath;
    private boolean isRrelease;

    public final String mainProjectPath;
    //gen对应的 res(包含res依赖，[0]为gen所在res)
	public final Map<String, List<String>> genResDirsMap;

    //res -> bin的res(正好可以用于DataBinding存放脱糖的xml)
	public final Map<String, String> allResDirMap;
	// v5 -> flavor
    public final String flavor;

	// subProjectGens
    public final List<String> subProjectGens;

	//被Callable调用
    public AaptService$ErrorResult runTask() {
        try {

			if (ZeroAicySetting.isEnableAapt2()) {
				//aapt2实现
				return Aapt2Task.proxyAapt(this);
			}

            // aapt实现

            AndroidProjectSupport.Qq(this.resLibraryMap, this.flavor);

            AaptService$ErrorResult mergedAndroidManifestxml = mergedAndroidManifestxml();
            if (mergedAndroidManifestxml.errorInfo != null) {
                return mergedAndroidManifestxml;
            }

            for (String injectedAManifestFilePath : this.injectedAndroidManifestMap.keySet()) {
                File injectedAManifestFile = new File(injectedAManifestFilePath);
				if (!injectedAManifestFile.exists()) {
                    injectedAManifestFile.mkdirs();
                }
            }

            initedAaptExecutable();

            File resourcesApParentFile = new File(this.resourcesApPath).getParentFile();
            if (!resourcesApParentFile.exists()) {
                resourcesApParentFile.mkdirs();
            }

            AaptService$ErrorResult generateRJavaErrorResult = generateRJava();

            if (generateRJavaErrorResult.errorInfo != null) {
                return generateRJavaErrorResult;
            }

            generateBuildConfigJava();
            if (!this.XL) {
                if (this.isBuildRefresh) {
                    buildRefresh();
                }
                AaptService$ErrorResult Ws = Ws();
                return Ws.errorInfo != null ? Ws : QX();
            }
            return new AaptService$ErrorResult(false);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    public AaptService$Task( 
		AaptService aaptService, 
		String aaptPath, String mainProjectPath, String flavor, 
		Map<String, List<String>> map, List<String> subProjectGens, List<String> variantManifestPaths, 
		String androidJarFilePath, String mainProjectGenDir, List<String> assetDirPaths, 
		String resourceApFilePath, Map<String, String> genPackageNameMap, Map<String, String> allResDirMap, 

		Map<String, String> injectedAndroidManifestMap, Map<String, String> mergedAManifestMap, Map<String, List<String>> genResDirsMap, 
		Map<String, String> androidManifestMap, boolean z, boolean isBuildRefresh, boolean isRrelease) {

		this.aaptService = aaptService;
        this.mainProjectPath = mainProjectPath;
        this.resLibraryMap = map;
        this.flavor = flavor;
        this.aaptPath = aaptPath;
        this.subProjectGens = subProjectGens;
        this.variantManifestPaths = variantManifestPaths;
        this.androidSdkFilePath = androidJarFilePath;
        this.mainProjectGenDir = mainProjectGenDir;
        this.assetDirPaths = assetDirPaths;
        this.resourcesApPath = resourceApFilePath;

		//genDir -> packageName，但只有子项目，子项目的子项目没有
        this.genPackageNameMap = genPackageNameMap;

        this.allResDirMap = allResDirMap;
        this.mergedAndroidManifestMap = mergedAManifestMap;
        this.genResDirsMap = genResDirsMap;
        this.XL = z;
        this.isBuildRefresh = isBuildRefresh;
        this.isRrelease = isRrelease;
        this.injectedAndroidManifestMap = new HashMap<String, String>(injectedAndroidManifestMap);

        this.androidManifestMap = new HashMap<>();
        for (Map.Entry<String, String> entry : androidManifestMap.entrySet()) {
            this.androidManifestMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static String getMainProjectPath(AaptService$Task task) {
        return task.mainProjectPath;
    }

	// 合并清单文件
    public AaptService$ErrorResult mergedAndroidManifestxml() {
        try {
			// injectedAManifestMap
			String mainProjectInjectedManifestPath = this.injectedAndroidManifestMap.get(this.mainProjectGenDir);
			// 自动设置  android:debuggable="debuggable"属性
			settingAndroidDebugable(mainProjectInjectedManifestPath);
			
            if (this.subProjectGens.size() <= 0 
				&& this.variantManifestPaths.size() <= 0) {
				return new AaptService$ErrorResult(false);
			}
			
			// aManifestMap
			String mainProjectManifestPath = this.androidManifestMap.get(mainProjectInjectedManifestPath);
			// mergedAManifestMap
			String mainProjectMergedManifestPath = this.mergedAndroidManifestMap.get(this.mainProjectGenDir);

			// subProjectGens
			int subProjectGensSize = this.subProjectGens.size();

			String[] subProjectInjectedManifestPaths = new String[subProjectGensSize];
			for (int index = 0; index < subProjectGensSize; index++) {
				// 所有子项目的injectedManifest
				subProjectInjectedManifestPaths[index] = this.injectedAndroidManifestMap.get(this.subProjectGens.get(index));
			}

			int variantManifestPathsSize = this.variantManifestPaths.size();
			String[] variantManifestPaths = new String[variantManifestPathsSize];
			for (int index = 0; index < this.variantManifestPaths.size(); index++) {
				variantManifestPaths[index] = this.variantManifestPaths.get(index);
			}

			/**
			 * 将所有xmlmMap中的主项目manifest xml更新为 merged xml
			 */
			// key: gen路径 value: injected xml
			this.injectedAndroidManifestMap.put(this.mainProjectGenDir, mainProjectMergedManifestPath);
			// key: injected xml value: original manifest xml
			this.androidManifestMap.put(mainProjectMergedManifestPath, mainProjectManifestPath);

			ArrayList<File> manifestPaths = new ArrayList<>();
			// 添加 主项目的injected xml[属于合并的输入xml]
			manifestPaths.add(new File(mainProjectInjectedManifestPath));

			for (int index = 0; index < subProjectGensSize; index++) {
				manifestPaths.add(new File(subProjectInjectedManifestPaths[index]));
			}

			for (int index = 0; index < variantManifestPathsSize; index++) {
				manifestPaths.add(new File(variantManifestPaths[index]));
			}

			// merge目录
			File mainProjectMergedManifestFile = new File(mainProjectMergedManifestPath);
			File inputInfoFile = new File(mainProjectMergedManifestFile.getParent(), "merged-manifest-inputs-info");

			if (mainProjectMergedManifestFile.exists() 
				&& !isChangerInputs(manifestPaths, inputInfoFile) 
				&& u7(manifestPaths, Collections.singletonList(mainProjectMergedManifestFile))) {
				// 省略合并
				AppLog.d("Omitting merge " + mainProjectMergedManifestPath);
			} else {
				AppLog.d("Merging " + mainProjectMergedManifestPath);
				// 合并
				String mergedInfo = com.aide.ui.build.android.l.j6(AaptService.getContext(this.aaptService), mainProjectMergedManifestPath, mainProjectInjectedManifestPath, variantManifestPaths, subProjectInjectedManifestPaths);
				if (mergedInfo != null) {
					return new AaptService$ErrorResult(mergedInfo);
				}
			}

            return new AaptService$ErrorResult(false);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	private void settingAndroidDebugable(String mainProjectInjectedManifestPath) {
		ProjectService projectService = ServiceContainer.getProjectService();
		String currentVariant = projectService.getCurrentVariant();

		boolean debuggable = 
			currentVariant.endsWith("debug-aide")
			|| (ZeroAicySetting.enableADRT() && currentVariant.endsWith("debug"));

		// debuggable 与 xml中值不同才写入
		AndroidManifestParser.settingAndroidDebuggable(mainProjectInjectedManifestPath, debuggable);
	}

	/**
	 * 检查输入源是否改变
	 */
	private boolean isChangerInputs(List<File> inputFiles, File inputInfoFile) {

		Set<String> inputPaths = new HashSet<String>();
		for (File inputFile : inputFiles) {
			inputPaths.add(inputFile.getAbsolutePath());
		}

		boolean isChangerInputs = false;

		// 与inputInfoFile检查输入源
		if (!inputInfoFile.exists()) {
			// 已改变
			isChangerInputs = true;
		} else {
			// 从inputInfoFile读取上一次inputFiles
			Set<String> lastInputJarFilesSet = readInputInfoFile(inputInfoFile);
			//比较
			isChangerInputs = inputPaths.size() != lastInputJarFilesSet.size()
				|| !inputPaths.containsAll(lastInputJarFilesSet);			
		}

		// 只有已改变才重写写入
		if (isChangerInputs) {
			//写入inputJarFilesSet
			IOUtils.writeLines(inputPaths, inputInfoFile);
		}
		return false;

	}

	/**
	 * 读取输入源文件
	 */
	private Set<String> readInputInfoFile(File inputInfoFile) {
		Set<String> lastInputJarFilesSet = new HashSet<>();


		try {
			IOUtils.readLines(new FileInputStream(inputInfoFile), lastInputJarFilesSet);
		}
		catch (Exception e) {}

		return lastInputJarFilesSet;
	}

    public static Map<String, String> getAndroidManifestMap(AaptService$Task task) {
        return task.androidManifestMap;
    }

    @MethodMark(method = 2668790741732965889L)
    private void Hw(File file, String str, List<File> list) {
        try {
            File[] listFiles = file.listFiles();
            if (listFiles == null) {
                return;
            }
            for (File file2 : listFiles) {
                if (file2.isDirectory()) {
                    Hw(file2, str, list);
                } else if (str == null || str.equals(file2.getName())) {
                    list.add(file2);
                }
            }
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private AaptService$ErrorResult generateRJava() {
        try {
            for (Map.Entry<String, List<String>> entry : this.genResDirsMap.entrySet()) {

				if (Thread.interrupted()) {
					throw new InterruptedException();
				}

				String genDir = entry.getKey();
				List<String> resDirs = entry.getValue();
				if (!genDir.equals(this.mainProjectGenDir)) {
					AaptService$ErrorResult J8 = generateRJava(genDir, resDirs);
					if (J8.errorInfo != null) {
						return J8;
					}
				}

            }
            if (this.XL) {
                AaptService$ErrorResult generateRJavaErrorResult = generateRJava(this.mainProjectGenDir, this.genResDirsMap.get(this.mainProjectGenDir));
                if (generateRJavaErrorResult.errorInfo != null) {
                    return generateRJavaErrorResult;
                }
            }
            return new AaptService$ErrorResult(false);
        }
		catch (Throwable th) {
			throw new Error(th);
        }
    }

	/**
	 * aapt 生成R.java
	 */ 
    private AaptService$ErrorResult generateRJava(String genDir, List<String> list) {
        try {

            String str2 = this.injectedAndroidManifestMap.get(genDir);
            ArrayList<File> arrayList = new ArrayList<>();
            for (String str3 : list) {
                Hw(new File(str3), null, arrayList);
            }

            if (arrayList.isEmpty()) {
                return new AaptService$ErrorResult(false);
            }
            if (new File(str2).exists()) {
                arrayList.add(new File(str2));
            }

            ArrayList<File> arrayList2 = new ArrayList<>();
            Hw(new File(genDir), "R.java", arrayList2);
            if (!this.isBuildRefresh && !arrayList2.isEmpty() && u7(arrayList, arrayList2)) {
                AppLog.d("Omitting aapt call to regenerate R.java in " + genDir + " (is uptodate)");
                return new AaptService$ErrorResult(false);
            }
            ArrayList<String> arrayList3 = new ArrayList<>();
            if (genDir.equals(this.mainProjectGenDir)) {
                arrayList3.addAll(Arrays.asList(new String[]{this.aaptPath, "package", "--auto-add-overlay", "-m", "-J", genDir, "-M", str2, "-I", this.androidSdkFilePath, "--no-version-vectors"}));
            } else {
                arrayList3.addAll(Arrays.asList(new String[]{this.aaptPath, "package", "--non-constant-id", "--auto-add-overlay", "-m", "-J", genDir, "-M", str2, "-I", this.androidSdkFilePath, "--no-version-vectors"}));
            }
            for (String str4 : list) {
                if (new File(str4).exists()) {
                    arrayList3.addAll(Arrays.asList(new String[]{"-S", str4}));
                }
            }
            if (genDir.equals(this.mainProjectGenDir)) {
                String gn = gn();
                if (gn.length() != 0) {
                    arrayList3.add("--extra-packages");
                    arrayList3.add(gn.toString());
                }
            }
            tp(arrayList3);
            long currentTimeMillis = System.currentTimeMillis();
            wf j6 = xf.j6(arrayList3, (String) null, null, true, (OutputStream) null, (byte[]) null);
            AppLog.d("aapt call elapsed " + (System.currentTimeMillis() - currentTimeMillis));
            if (j6.DW() == 0) {
                for (File file : arrayList2) {
                    if (file.lastModified() < currentTimeMillis) {
                        FileSystem.ensureUpdatedFileLastModified(file.getPath());
                    }
                }
                return new AaptService$ErrorResult(false);
            }
            return new AaptService$ErrorResult(getAaptError(j6.j6(), j6.DW()));
        }
		catch (Throwable th) {
			throw new Error(th);
        }
    }

    private AaptService$ErrorResult QX() {
        try {

            if (!Thread.interrupted()) {
                List<String> list = this.genResDirsMap.get(this.mainProjectGenDir);
                String str = this.injectedAndroidManifestMap.get(this.mainProjectGenDir);
                ArrayList<File> arrayList = new ArrayList<>();
                ArrayList<File> arrayList2 = new ArrayList<>();
                for (String str2 : list) {
                    Hw(new File(str2), null, arrayList2);
                }
                if (new File(str).exists()) {
                    arrayList2.add(new File(str));
                }
                for (String str3 : this.assetDirPaths) {
                    if (new File(str3).exists()) {
                        Hw(new File(str3), null, arrayList2);
                    }
                }
                arrayList2.add(new File(this.androidSdkFilePath));
                Hw(new File(this.mainProjectGenDir), "R.java", arrayList);
                arrayList.add(new File(this.resourcesApPath));
                if (!this.isBuildRefresh && !arrayList.isEmpty() && new File(this.resourcesApPath).exists() && u7(arrayList2, arrayList)) {
                    AppLog.d("Omitting aapt package call (is uptodate)");
                    return new AaptService$ErrorResult(false);
                }
                ArrayList<String> arrayList3 = new ArrayList<>();
                if (this.isRrelease) {
                    arrayList3.addAll(Arrays.asList(new String[]{this.aaptPath, "package", "-f", "--no-crunch", "--auto-add-overlay", "-I", this.androidSdkFilePath, "-F", this.resourcesApPath}));
                } else {
                    arrayList3.addAll(Arrays.asList(new String[]{this.aaptPath, "package", "-f", "--no-crunch", "--auto-add-overlay", "--debug-mode", "-I", this.androidSdkFilePath, "-F", this.resourcesApPath}));
                }
                for (String assetDirPath : this.assetDirPaths) {
                    if (new File(assetDirPath).exists()) {
                        arrayList3.addAll(Arrays.asList(new String[]{"-A", assetDirPath}));
                    }
                }
                arrayList3.addAll(Arrays.asList(new String[]{"-M", str}));
                for (String resDir : list) {
                    if (new File(resDir).exists()) {
                        String binResDir = this.allResDirMap.get(resDir);
                        new File(binResDir).mkdirs();
                        arrayList3.addAll(Arrays.asList(new String[]{"-S", binResDir, "-S", resDir}));
                    }
                }
                arrayList3.addAll(Arrays.asList(new String[]{"-m", "-J", this.mainProjectGenDir, "--no-version-vectors"}));
                String gn = gn();
                if (gn.length() != 0) {
                    arrayList3.add("--extra-packages");
                    arrayList3.add(gn.toString());
                }
                tp(arrayList3);
                long currentTimeMillis = System.currentTimeMillis();
                wf j6 = xf.j6(arrayList3, (String) null, null, true, (OutputStream) null, (byte[]) null);
                AppLog.d("aapt call elapsed " + (System.currentTimeMillis() - currentTimeMillis));
                if (j6.DW() == 0) {
                    for (File file : arrayList) {
                        if (file.lastModified() < currentTimeMillis) {
                            FileSystem.ensureUpdatedFileLastModified(file.getPath());
                        }
                    }
                    return new AaptService$ErrorResult(true);
                }
                return new AaptService$ErrorResult(getAaptError(j6.j6(), j6.DW()));
            }
            throw new InterruptedException();
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    public String getAaptError(byte[] errorBytes, int i) {
        try {

			String error = StreamUtilities.readTextReader(new InputStreamReader(new ByteArrayInputStream(errorBytes)));

            error = error.trim();

            if (error.length() == 0) {
                return "aapt exited with code " + i;
            } else if (i != 1) {
                return error + "\naapt exited with code " + i;
            } else {
                return error;
            }
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private AaptService$ErrorResult Ws() {
        try {

            for (Map.Entry<String, String> entry : this.allResDirMap.entrySet()) {
                if (Thread.interrupted()) {
					throw new InterruptedException();
				}
				String key = entry.getKey();
				String value = entry.getValue();

				if (new File(key).exists()) {
					ArrayList<File> arrayList = new ArrayList<>();
					Hw(new File(key), null, arrayList);
					ArrayList<File> arrayList2 = new ArrayList<>();
					Hw(new File(value), null, arrayList2);
					if (u7(arrayList, arrayList2)) {
						AppLog.d("Omitting aapt crunch call (is uptodate)");
					} else {
						List<String> asList = Arrays.asList(new String[]{this.aaptPath, "crunch", "-S", key, "-C", value, "--no-version-vectors"});
						tp(asList);
						long currentTimeMillis = System.currentTimeMillis();
						wf j6 = xf.j6(asList, (String) null, null, true, (OutputStream) null, (byte[]) null);
						AppLog.d("aapt call elapsed " + (System.currentTimeMillis() - currentTimeMillis));
						if (j6.DW() != 0) {
							return new AaptService$ErrorResult(getAaptError(j6.j6(), j6.DW()));
						}
					}
				}
            }
            return new AaptService$ErrorResult(false);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	/**
	 * 生成 BuildConfig.java
	 */
    public void generateBuildConfigJava() {
        try {
            for (Map.Entry<String, String> entry : this.genPackageNameMap.entrySet()) {
                String packageName = entry.getValue();

				if (TextUtils.isEmpty(packageName)) {
					continue;
				}
				File buildConfigJavaParentDirectory = new File(entry.getKey(), packageName.replace('.', File.separatorChar));
                if (!buildConfigJavaParentDirectory.exists() 
					&& !buildConfigJavaParentDirectory.mkdirs()
					&& !buildConfigJavaParentDirectory.exists()) {
					throw new IOException("Could not create directory " + buildConfigJavaParentDirectory);
                }
                File buildConfigJavaFile = new File(buildConfigJavaParentDirectory, "BuildConfig.java");
                if (!this.XL || !buildConfigJavaFile.exists()) {
                    com.aide.ui.build.android.c.j6(buildConfigJavaFile, packageName, this.isRrelease);
                }
            }
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private String gn() {
        try {
            HashSet<String> hashSet = new HashSet<>();
            for (Map.Entry<String, String> entry : this.genPackageNameMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!value.equals(this.genPackageNameMap.get(this.mainProjectGenDir)) && !hashSet.contains(value) && new File(new File(key, value.replace('.', File.separatorChar)), "R.java").exists()) {
                    hashSet.add(value);
                }
            }
            if (FileSystem.getName(FileSystem.getParent(this.injectedAndroidManifestMap.get(this.mainProjectGenDir))).equals("AIDEExp")) {
                hashSet.add("com.aide.ui");
            }
            StringBuilder sb = new StringBuilder();
            for (String str : hashSet) {
                if (sb.length() != 0) {
                    sb.append(':');
                }
                sb.append(str);
            }
            return sb.toString();
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private void initedAaptExecutable() {
        try {
            if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			if (Build.VERSION.SDK_INT < 29 && !AaptService.initedAaptExecutable()) {
				File aaptFile = new File(this.aaptPath);

				boolean isSetExecutable = aaptFile.setReadable(true, false) 
					&& aaptFile.setExecutable(true, false);

				if (!isSetExecutable) {
					throw new IOException("Could not make " + this.aaptPath + " executable - exit code ");
				}
				AaptService.setInitedAaptExecutable(true);
				return;
			}
			return;

        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private void tp(List<String> list) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Running aapt2 ");
            for (int i = 1; i < list.size(); i++) {
                sb.append('\"');
                sb.append(list.get(i));
                sb.append('\"');
                if (i != list.size() - 1) {
                    sb.append(" ");
                }
            }
            AppLog.d(sb.toString());
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	/**
	 * 检查输出文件 是否新于输入文件
	 */
    private boolean u7(List<File> inputFiles, List<File> outputFiles) {
        try {
			// ! (inputFiles.isEmpty() || !outputFiles.isEmpty())
			if (!inputFiles.isEmpty() && outputFiles.isEmpty()) {
				return false;
			}
			// 获取最旧输出文件的时间戳
			long outputFileMinLastModified = Long.MAX_VALUE;
			for (File outputFile : outputFiles) {
				outputFileMinLastModified = Math.min(outputFileMinLastModified, outputFile.lastModified());
			}
			// 获取最新输入文件的时间戳
			long inputFileMaxLastModified = 0;
			for (File inputFile : inputFiles) {
				inputFileMaxLastModified = Math.max(inputFileMaxLastModified, inputFile.lastModified());
			}
			// 如果最旧输出文件的时间戳，仍新于最新输入文件的时间戳
			// 则为true，可忽略
			return outputFileMinLastModified > inputFileMaxLastModified;

        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    public void buildRefresh() {
        try {
            for (Map.Entry<String, String> entry : this.allResDirMap.entrySet()) {
                if (Thread.interrupted()) {
					throw new InterruptedException();

				}
				String value = entry.getValue();
				if (new File(value).exists()) {
					try {
						FileSystem.deleteDirectory(value);
					}
					catch (Throwable e) {

					}
				}
            }
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
}

