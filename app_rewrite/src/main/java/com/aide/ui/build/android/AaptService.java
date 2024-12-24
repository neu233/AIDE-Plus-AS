
// j3 EQ aM Mr

//
// Decompiled by Jadx - 858ms
//
package com.aide.ui.build.android;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import androidx.annotation.Keep;
import com.aide.common.AppLog;
import com.aide.engine.SyntaxError;
import com.aide.ui.ServiceContainer;
import com.aide.ui.project.AndroidProjectSupport;
import com.aide.ui.services.AssetInstallationService;
import com.aide.ui.services.ZeroAicyProjectService;
import com.aide.ui.util.FileSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import com.aide.ui.project.internal.GradleTools;
import java.util.Iterator;
import com.aide.ui.project.WearAppProjectSupport;
import com.aide.ui.util.BuildGradle;
import io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface;

public class AaptService {

    private static boolean initedAaptExecutable;

    private AaptService$FutureTask curFutureTask;

    private AaptRunCallback callback;

    private Context context;

	// 只有此类使用 j6 -> 
    private final ExecutorService executorsService;

    public AaptService(Context context) {
		this.context = context;
		this.executorsService =  ZeroAicyProjectService.getProjectServiceExecutorService();
    }

    static void FH(AaptService aaptService) {
        aaptService.QX();
    }

    static void Hw(AaptService aaptService, Throwable th) {
        aaptService.Ws(th);
    }

	private Map<String, List<SyntaxError>> resolvingError(String mainProjectPath, Map<String, String> androidManifestMap, String errorInfo) {
		HashMap<String, List<SyntaxError>> fileSyntaxErrorsMap = new HashMap<>();
		try {
			String[] lines = errorInfo.split("\n");

			for (String errorContent : lines) {
				if (TextUtils.isEmpty(errorContent)) {
					// 空行不显示
					continue;
				}
				// 开始解析
				// /storage/emulated/0/AppProjects1/.ZeroAicy/git/Termux内置版/main/src/main/res/layout/main.xml:6: error: resource string/hello_world2 (aka io.github.zeroaicy.termux:string/hello_world2) not found.\nerror: failed linking file resources.";
				// 文件路径 : 行数 :空格[error:]空格[错误内容]
				// [error: ] 错误内容
				int errorFilePathEnd = errorContent.indexOf(':');
				if (errorFilePathEnd <= 0) {
					// 未知格式没有 ':'
					putSyntaxError(fileSyntaxErrorsMap, mainProjectPath, errorContent);
					continue;
				}
				// 可能不是错误文件路径
				// 可能是 error: 等其它，所以判断是否是文件
				String errorFilePath = errorContent.substring(0, errorFilePathEnd);
				// 不是文件
				if (!FileSystem.KD(errorFilePath)) {
					putSyntaxError(fileSyntaxErrorsMap, mainProjectPath, errorContent);
					continue;
				}
				// 是文件 文件后面跟的是行号
				int errorLineNumberStart = errorFilePathEnd + 1;
				int errorLineNumberEnd = errorContent.indexOf(':', errorLineNumberStart);

				if (errorLineNumberEnd < 0) {
					errorLineNumberEnd = errorContent.indexOf(' ', errorLineNumberStart);
				}
				if (errorLineNumberEnd < 0) {
					continue;
				}

				int errorLineNumber;
				try {
					errorLineNumber = Integer.parseInt(errorContent.substring(errorLineNumberStart, errorLineNumberEnd));
				}
				catch (NumberFormatException unused) {
					errorLineNumber = 1;
				}

				String error = errorContent.substring(errorLineNumberEnd + 1, errorContent.length()).trim();

				while (error.toLowerCase().startsWith("error:")) {
					error = error.substring("error:".length(), error.length()).trim();
				}

				if (androidManifestMap.containsKey(errorFilePath)) {
					// 都添加就知道哪错了
					String errorFilePath2 = androidManifestMap.get(errorFilePath);
					String error2 = "in generated file: " + error;
					int errorLineNumber2 = 1;

					SyntaxError syntaxError2 = makeSyntaxError("aapt2", errorLineNumber2, error2);
					putSyntaxError(fileSyntaxErrorsMap, errorFilePath2, syntaxError2);

				}

				SyntaxError syntaxError = makeSyntaxError("aapt2", errorLineNumber, error);
				putSyntaxError(fileSyntaxErrorsMap, errorFilePath, syntaxError);
			}
		}				
		catch (Exception e) {
			AppLog.e(e);
		}
		return fileSyntaxErrorsMap;
	}

	public void putSyntaxError(HashMap<String, List<SyntaxError>> fileSyntaxErrorsMap, String errorFilePath, String line) {
		if (!fileSyntaxErrorsMap.containsKey(errorFilePath)) {
			fileSyntaxErrorsMap.put(errorFilePath, new ArrayList<SyntaxError>());
		}
		List<SyntaxError> syntaxErrors = fileSyntaxErrorsMap.get(errorFilePath);
		syntaxErrors.add(makeSyntaxError("aapt", 1, line));
	}

	public void putSyntaxError(Map<String, List<SyntaxError>> fileSyntaxErrorsMap, String errorFilePath, SyntaxError syntaxError) {
		if (!fileSyntaxErrorsMap.containsKey(errorFilePath)) {
			fileSyntaxErrorsMap.put(errorFilePath, new ArrayList<SyntaxError>());
		}
		List<SyntaxError> syntaxErrors = fileSyntaxErrorsMap.get(errorFilePath);
		syntaxErrors.add(syntaxError);
	}

	/**
	 * 会调用AndroidProjectBuildService$c::vJ
	 * 然后在切换到主线程运行AndroidProjectBuildService$c$c类
	 * 
	 */
    private void J8(boolean z) {
        if (this.callback != null) {
			this.callback.vJ(z);
		}
    }

    private void QX() {

		if (this.callback != null) {
			this.callback.J0();
		}
    }

    static boolean initedAaptExecutable() {
        return initedAaptExecutable;
    }

    private void Ws(Throwable th) {

		AppLog.e(th);
		if (this.callback != null) {
			this.callback.g3();
		}
    }

    private void XL(Map<String, List<SyntaxError>> map) {
		if (this.callback != null) {
			this.callback.Mz(map);
		}
    }

    static Context getContext(AaptService aaptService) {
        return aaptService.context;
    }



	// AaptService$d::done调用
    static void j6(AaptService aaptService, boolean z) {
        aaptService.J8(z);
    }

	// 对JavaGradle项目的兼容
	// AndroidProjectSupport::jO()
	public static Map<String, String> jO(Map<String, List<String>> resLibraryMap, String flavor) {
		HashMap<String, String> hashMap = new HashMap<>();
		for (String res_dir_path : resLibraryMap.keySet()) {
			if (GradleTools.isAndroidGradleProject(res_dir_path)) {
				if (existsAndroidManifestXmlFile(res_dir_path, flavor)) {
					hashMap.put(GradleTools.getGenDir(res_dir_path), AndroidProjectSupport.getProjectPackageName(res_dir_path, flavor));					
				}
			}
		}
		return hashMap;
    }

	private static boolean existsAndroidManifestXmlFile(String res_dir_path, String flavor) {

		return FileSystem.exists(res_dir_path + "/AndroidManifest.xml")
			|| FileSystem.exists(res_dir_path + "/src/main/AndroidManifest.xml")
			|| (flavor != null  && FileSystem.exists(res_dir_path + "/src/" + flavor + "/AndroidManifest.xml"));
	}
	public static Map<String, String> aq(String str, Map<String, List<String>> map, String flavor) {
		HashMap<String, String> hashMap = new HashMap<>();
		Iterator<Map.Entry<String, List<String>>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			String key = it.next().getKey();
			if (AndroidProjectSupport.isAndroidGradleProject(key)
			// 对JavaGradle项目的兼容
				&& existsAndroidManifestXmlFile(key, flavor)) {

				if (GradleTools.isAarEexplodedPath(key)) {
					String androidManifestPath = GradleTools.getAndroidManifestPath(key, flavor);
					String injectedAndroidManifestPath = GradleTools.getInjectedAndroidManifestPath(key);
					if (WearAppProjectSupport.J0(androidManifestPath, injectedAndroidManifestPath, (BuildGradle) null, AndroidProjectSupport.getProjectPackageName(str, flavor), flavor)) {
						hashMap.put(GradleTools.getGenDir(key), injectedAndroidManifestPath);
					} else {
						hashMap.put(GradleTools.getGenDir(key), androidManifestPath);
					}
				} else if (GradleTools.isGradleProject(key)) {
					BuildGradle configuration = ZeroAicyExtensionInterface.getBuildGradle().getConfiguration(GradleTools.getBuildGradlePath(key));
					String androidManifestPath2 = GradleTools.getAndroidManifestPath(key, flavor);
					String injectedAndroidManifestPath2 = GradleTools.getInjectedAndroidManifestPath(key);
					if (WearAppProjectSupport.J0(androidManifestPath2, injectedAndroidManifestPath2, configuration, AndroidProjectSupport.getProjectPackageName(str, flavor), flavor)) {
						hashMap.put(GradleTools.getGenDir(key), injectedAndroidManifestPath2);
					} else {
						hashMap.put(GradleTools.getGenDir(key), androidManifestPath2);
					}
				} else {
					hashMap.put(GradleTools.getGenDir(key), GradleTools.getAndroidManifestPath(key, flavor));
				}
			}
		}
		return hashMap;
    }
	public static List<String> jw(String mainProjectPath) {
		List<String> arrayList = new ArrayList<>();
		if (GradleTools.isGradleProject(mainProjectPath)) {
			Map<String, List<String>> libraryMapping = ServiceContainer.getProjectService().getLibraryMapping();
			for (String subProjectPath : libraryMapping.get(mainProjectPath)) {
				if (!existsAndroidManifestXmlFile(subProjectPath, null)) {
					// 不存在 AndroidManifest.xml
					// 对JavaGradle项目的兼容
					continue;
				}
				if (GradleTools.isGradleProject(subProjectPath) 
					|| GradleTools.isAarEexplodedPath(subProjectPath)) {
					arrayList.add(GradleTools.getGenDir(subProjectPath));
				}
			}
		}
		return arrayList;
    }
    private AaptService$Task makeTask(String mainProjectPath, boolean z, boolean isBuildRefresh, boolean isRrelease, String str2, String flavor, String aaptPath) {
		Map<String, List<String>> resLibraryMap = ServiceContainer.getProjectService().vy(mainProjectPath);

		String resourceApFilePath = AndroidProjectSupport.kf(mainProjectPath);

		Map<String, String> genPackageNameMap = AndroidProjectSupport.jO(resLibraryMap, flavor);

		Map<String, String> allResDirMap = AndroidProjectSupport.cT(resLibraryMap, flavor);
		Map<String, String> injectedAndroidManifestMap = AndroidProjectSupport.aq(mainProjectPath, resLibraryMap, flavor);
		Map<String, String> mergedAManifestMap = AndroidProjectSupport.FN(resLibraryMap, flavor);
		Map<String, List<String>> genResDirsMap = AndroidProjectSupport.oY(resLibraryMap, flavor);
		Map<String, String> androidManifestMap = AndroidProjectSupport.Z1(resLibraryMap, flavor);
		List<String> subProjectGens = jw(mainProjectPath);
		List<String> variantManifestPaths = AndroidProjectSupport.fY(mainProjectPath, flavor);

		String androidJarPath = ServiceContainer.getProjectService().getAndroidJarPath();
		String mainProjectGenDir = AndroidProjectSupport.Eq(mainProjectPath);
		List<String> assetDirPaths = AndroidProjectSupport.yO(mainProjectPath, str2, flavor);

		return new AaptService$Task(
			this, 
			aaptPath, mainProjectPath, flavor, 
			resLibraryMap, subProjectGens, variantManifestPaths, 
			androidJarPath, mainProjectGenDir, assetDirPaths, 
			resourceApFilePath, genPackageNameMap, allResDirMap, 
			injectedAndroidManifestMap, mergedAManifestMap, genResDirsMap, 
			androidManifestMap, z, isBuildRefresh, isRrelease);
    }

    private SyntaxError makeSyntaxError(String errorType, int errorLineNumber, String error) {
		SyntaxError syntaxError = new SyntaxError();
		syntaxError.jw = errorLineNumber;
		syntaxError.fY = 1;
		syntaxError.qp = errorLineNumber;
		syntaxError.k2 = 1000;
		syntaxError.zh = errorType + ": " + error;

		return syntaxError;
    }

    static Map<String, List<SyntaxError>> resolvingError(AaptService aaptService, String mainProjectPath, Map<String, String> androidManifestMap, String errorInfo) {
        return aaptService.resolvingError(mainProjectPath, androidManifestMap, errorInfo);
    }

    private String getAaptPath() {
		if (Build.VERSION.SDK_INT >= 29) {
			AppLog.d("Using aapt: " + ServiceContainer.getContext().getApplicationInfo().nativeLibraryDir + "/libaapt.so");
			return ServiceContainer.getContext().getApplicationInfo().nativeLibraryDir + "/libaapt.so";
		}
		return AssetInstallationService.DW("aapt", false);
    }



    static void DW(AaptService aaptService, Map<String, List<SyntaxError>> map) {
        aaptService.XL(map);
    }
    static boolean setInitedAaptExecutable(boolean initedAaptExecutable) {
        return AaptService.initedAaptExecutable = initedAaptExecutable;
    }

	@Keep
    public void runAapt(final String mainProjectPath, final String str2, final String flavor, final boolean z, final boolean isRrelease, final boolean z3) {
        try {
            final String aaptPath = getAaptPath();
            if (this.curFutureTask != null) {
                this.curFutureTask.cancel(true);
                this.curFutureTask = null;
            }
			// 改成异步
			AaptService$Callable.TaskFactory taskFactory = new AaptService$Callable.TaskFactory(){
				@Override
				public List<AaptService$Task> getTasks() {
					ArrayList<AaptService$Task> arrayList = new ArrayList<>();
					if (z3) {
						for (String mainAppOrWearAppPath : ServiceContainer.getProjectService().getMainAppWearApps()) {
							if (mainProjectPath.equals(mainAppOrWearAppPath)) {
								continue;
							}
							arrayList.add(makeTask(mainAppOrWearAppPath, true, false, false, str2, flavor, aaptPath));
						}
					}
					arrayList.add(makeTask(mainProjectPath, false, z, isRrelease, str2, flavor, aaptPath));
					return arrayList;
				}
			};
            ExecutorService executorService = this.executorsService;
            AaptService$FutureTask task = new AaptService$FutureTask(this, new AaptService$Callable(this, taskFactory));
            this.curFutureTask = task;
            executorService.execute(task);
        }
		catch (Throwable th2) {
        }
    }

	// AndroidProjectBuildService::yO -> Mr
    @Keep
	public void runAapt(final String flavor) {
        try {
            final String aaptPath = getAaptPath();
            if (this.curFutureTask != null) {
                this.curFutureTask.cancel(true);
                this.curFutureTask = null;
            }

			// 改成异步获取，在主线程获取会导致没有初始化完
			// 但此线程池用的是项目服务线程池
			AaptService$Callable.TaskFactory taskFactory = new AaptService$Callable.TaskFactory(){
				@Override
				public List<AaptService$Task> getTasks() {
					ArrayList<AaptService$Task> arrayList = new ArrayList<>();

					for (String mainAppOrWearAppPath : ServiceContainer.getProjectService().getMainAppWearApps()) {
						boolean isRrelease = false;
						arrayList.add(makeTask(mainAppOrWearAppPath, true, false, isRrelease, null, flavor, aaptPath));
					}
					return arrayList;
				}
			};
            ExecutorService executorService = this.executorsService;

            this.curFutureTask = new AaptService$FutureTask(this, new AaptService$Callable(this, taskFactory));
            executorService.execute(this.curFutureTask);
        }
		catch (Throwable th) {

        }
    }


	@Keep
    public void setCallback(AaptRunCallback callback) {
		this.callback = callback;
    }
	@Keep
    public void init(String flavour) {
		Map<String, String> Z1 = AndroidProjectSupport.Z1(ServiceContainer.getProjectService().getLibraryMapping(), flavour);
		for (String projectPath : ServiceContainer.getProjectService().getLibraryMapping().keySet()) {

			String ye = AndroidProjectSupport.ye(projectPath, flavour);
			FileSystem.ensureUpdatedFileLastModified(ye);

			if (Z1.containsKey(ye)) {
				FileSystem.ensureUpdatedFileLastModified((String) Z1.get(ye));
			}
			String Eq = AndroidProjectSupport.Eq(projectPath);
			try {
				FileSystem.deleteDirectory(Eq);
			}
			catch (Throwable e) {

			}
			new File(Eq).mkdirs();
		}
	}
}

