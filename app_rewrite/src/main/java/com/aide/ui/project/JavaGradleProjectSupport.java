
package com.aide.ui.project;

import abcd.xd;
import android.widget.Toast;
import androidx.annotation.Keep;
import com.aide.common.AddAndroidFiles;
import com.aide.common.AppLog;
import com.aide.common.MessageBox;
import com.aide.common.ValueRunnable;
import com.aide.engine.EngineSolution;
import com.aide.engine.EngineSolutionProject;
import com.aide.engine.service.CodeModelFactory;
import com.aide.ui.MainActivity;
import com.aide.ui.ServiceContainer;
import com.aide.ui.build.JavaGradleProjectBuildService;
import com.aide.ui.project.internal.GradleTools;
import com.aide.ui.rewrite.R;
import com.aide.ui.services.ProjectService;
import com.aide.ui.services.ProjectSupport;
import com.aide.ui.services.TemplateService;
import com.aide.ui.services.TemplateService.Template;
import com.aide.ui.services.TemplateService.TemplateGroup;
import com.aide.ui.util.BuildGradle;
import com.aide.ui.util.BuildGradle.Dependency;
import com.aide.ui.util.BuildGradle.FilesDependency;
import com.aide.ui.util.BuildGradleExt;
import com.aide.ui.util.ClassPath;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Java项目使用Gradle作为依赖管理
 * 项目根目录包含 build.gradle [插件使用java]
 * 且src/main目录下，没有AndroidManifest.xml
 * 必须优先AndroidProjectSupport判断*
 */
public class JavaGradleProjectSupport implements ProjectSupport {

	private static final String TAG = JavaGradleProjectSupport.class.getSimpleName();
	public static String[] aj(Map<String, List<String>> map) {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (String str2 : map.keySet()) {
			if (!GradleTools.isAarEexplodedPath(str2)) {
				for (ClassPath.Entry entry : getProjectClassPathEntrys(str2)) {
					if (entry.isSrcKind()) {
						arrayList.add(entry.resolveFilePath(str2));
					}
				}
			}
		}
		String[] strArr = new String[arrayList.size()];
		arrayList.toArray(strArr);
		return strArr;
	}
	public static String[] getClassFileRootDirs(Map<String, List<String>> vy, boolean isDebugAide) {
		ArrayList<String> arrayList = new ArrayList<>();
		Iterator<String> it = vy.keySet().iterator();
		while (it.hasNext()) {
			arrayList.add(GradleTools.u7(it.next(), isDebugAide));
		}
		String[] strArr = new String[arrayList.size()];
		arrayList.toArray(strArr);
		return strArr;
	}

	public static String Mz(String currentAppHome, boolean isDebugAide) {
		// 打包服务我重写了 ，不会用这个
		return null;
	}

	public static String getProjectOutputPath(String currentAppHome, boolean isDebugAide) {
		return GradleTools.getProjectOutputPath(currentAppHome);
	}

	// get jarFiles
	public static String[] cb(String currentAppHome) {
		Map<String, List<String>> libraryMapping = ServiceContainer.getProjectService().vy(currentAppHome);

		HashMap<String, String> hashMap = new HashMap<>();
		for (String str : libraryMapping.keySet()) {
			for (ClassPath.Entry entry : getProjectClassPathEntrys(currentAppHome)) {
				if (entry.isLibKind() && !hashMap.containsKey(entry.getId())) {
					hashMap.put(entry.getId(), entry.resolveFilePath(str));
				}
			}
		}
		String[] strArr = new String[hashMap.size()];
		hashMap.values().toArray(strArr);
		return strArr;
	}



	public static String Sf(String currentAppHome, boolean isDebugAide) {
		return GradleTools.getProjectOutputPath(currentAppHome) + "/classes.dex.zip";
	}

	/**
	 * 用于判断是否是Java项目
	 * 
	 */
	public static boolean isJavaGradleProject(String projectPath) {
		// 必须有 src build.gradle 

		/*if (FileSystem.isDirectory(projectPath)) {
		 // 后期添加对 apply plugin的判断 用BuildGradle解析并缓存，不用每次都解析
		 AppLog.d(projectPath , "isGradleProject %b isAndroidProject %b exists %b ", GradleTools.isGradleProject(projectPath), GradleTools.isGradleProject(projectPath), !FileSystem.exists(projectPath + "/src/main/AndroidManifest.xml"));
		 }*/

		return GradleTools.isGradleProject(projectPath) 
			&& !FileSystem.exists(projectPath + "/AndroidManifest.xml")
			&& !FileSystem.exists(projectPath + "/src/main/AndroidManifest.xml");
	}


	public static final JavaGradleProjectBuildService buildService = new JavaGradleProjectBuildService();
	@Override
	public void buildProject(boolean buildRefresh) {
		String buildType = ServiceContainer.getProjectService().er();
		buildService.buildProject(buildRefresh, buildType);
	}


	@Override
	public int getOpenProjectNameStringId(String string) {
		// 打开这个Java项目
		return R.string.command_files_open_java_project;
	}
	@Override
	public String getProjectAttributeHtmlString() {

		ProjectService projectService = ServiceContainer.getProjectService();


		// 顶层项目 与 主项目同级
		List mainAppWearApps = projectService.getMainAppWearApps();

		StringBuilder projectAttributeSb = new StringBuilder();
		Map<String, List<String>> libraryMapping = ServiceContainer.getProjectService().getLibraryMapping();

		// getLibraryMapping().keySet() 并去除主项目目录
		List<String> projectDirs = projectService.P8();
		for (String projectDir : projectDirs) {
			if (projectDir.endsWith(".aar")) {
				continue;
			}
			if (!mainAppWearApps.contains(projectDir)) {
				projectAttributeSb.append("<b>Library ")
					.append(projectDir)
					.append("</b><br/><br/>");
			} else {
				projectAttributeSb.append("<b>Java Project ")
					.append(projectDir)
					.append("</b><br/><br/>")
					.append("<i>Library Projects:</i><br/><br/>");
			}

			// ServiceContainer.getProjectService().getLibraryMapping()
			// 库目录以及aar依赖
			List<String> dependenciePaths = libraryMapping.get(projectDir);

			boolean hasAddLabelHeader = false;
			for (String dependenciePath : dependenciePaths) {
				if (dependenciePath.endsWith(".aar")) {
					continue;
				}
				if (!FileSystem.exists(dependenciePath)) {
					projectAttributeSb.append("(NOT FOUND) ");
				}
				projectAttributeSb
					.append(dependenciePath)
					.append("<br/><br/>");
				hasAddLabelHeader = true;
			}
			if (!hasAddLabelHeader) {
				projectAttributeSb.append("&lt;none&gt;<br/><br/>");
			}


			projectAttributeSb.append("<i>Libraries:</i><br/><br/>");

			//  getProjectLibPaths
			String[] projectLibPaths = getProjectLibPaths(projectDir);
			// AppLog.d(TAG, "projectLibPaths %s", Arrays.toString(projectLibPaths));
			//重置
			hasAddLabelHeader = false;

			for (String projectLibPath : projectLibPaths) {
				if (!FileSystem.exists(projectLibPath)) {
					projectAttributeSb.append("(NOT FOUND) ");
				}
				projectAttributeSb.append(projectLibPath)
					// 换行
					.append("<br/><br/>");

				hasAddLabelHeader = true;
			}

			// Java项目不添加
			/*for (String dependenciePath : dependenciePaths) {
			 if (dependenciePath.endsWith(".aar")) {
			 if (!FileSystem.exists(dependenciePath)) {
			 projectAttributeSb.append("(NOT FOUND) ");
			 }
			 if (dependenciePath.endsWith(".exploded.aar")) {
			 dependenciePath = dependenciePath.substring(0, dependenciePath.length() - 13) + ".aar";
			 }
			 projectAttributeSb.append(dependenciePath).append("<br/><br/>");
			 hasAddLabelHeader = true;
			 }
			 }*/

			if (!hasAddLabelHeader) {
				projectAttributeSb.append("&lt;none&gt;<br/><br/>");
			}

			projectAttributeSb.append("<br/>");
		}
		return projectAttributeSb.toString();
    }

	private static String[] empty = new String[0];
	public static String[] getProjectLibPaths(String projectDir) {
		HashMap<String, String>  hashMap = new HashMap<>();

		List<ClassPath.Entry> projectClassPathEntrys = getProjectClassPathEntrys(projectDir);
		if (projectClassPathEntrys == null) {
			return empty;
		}
		for (ClassPath.Entry entry : projectClassPathEntrys) {
			if (entry.isLibKind() 
				&& !hashMap.containsKey(entry.getId())) {
				hashMap.put(entry.getId(), entry.resolveFilePath(projectDir));
			}
		}

		String[] strArr = new String[hashMap.size()];
		hashMap.values().toArray(strArr);
		return strArr;
    }

	private static List<ClassPath.Entry> getProjectClassPathEntrys(String projectDir) {

		// jar类型
		if (!isJavaGradleProject(projectDir)) {
			return Collections.emptyList();
		}

		List<ClassPath.Entry> arrayList = new ArrayList<>();

		for (String sourceDir : GradleTools.getFlavourSourceDir(projectDir, null)) {
			arrayList.add(new ClassPath.Entry("src", FileSystem.removePrefix(projectDir, sourceDir), false));
		}
		arrayList.add(new ClassPath.Entry("src", FileSystem.removePrefix(projectDir, GradleTools.getGenDir(projectDir)), false));
		arrayList.add(ClassPath.Entry.AndroidFramework);
		arrayList.add(ClassPath.Entry.Libraries);

		for (BuildGradle.Dependency dependency : getProjectDependencies(projectDir)) {
			if (dependency instanceof BuildGradle.MavenDependency) {
				for (String mavenDependenciePath : ServiceContainer.getMavenService().resolveFullDependencyTree(null, (BuildGradle.MavenDependency) dependency)) {
					if (mavenDependenciePath.endsWith(".jar")) {
						arrayList.add(new ClassPath.Entry("lib", mavenDependenciePath, false, true));
					}
				}
			} else if (dependency instanceof BuildGradle.FileTreeDependency) {
				String dirPath = ((BuildGradle.FileTreeDependency) dependency).getDirPath(projectDir);
				if (dirPath != null) {
					addLibFileTree(dirPath, projectDir, arrayList, true);
				}
			} else if (dependency instanceof BuildGradle.FilesDependency) {
				FilesDependency filesDependency = (BuildGradle.FilesDependency)dependency;
				arrayList.add(new ClassPath.Entry("lib", filesDependency.getFilesPath(projectDir), false, true));
			}
		}
		arrayList.add(new ClassPath.Entry("output", FileSystem.removePrefix(projectDir, GradleTools.getBinPath(projectDir)), false));

		return arrayList;
	}


	private static void addLibFileTree(String str, String str2, List<ClassPath.Entry> list, boolean z) {
		if (FileSystem.getSuffixName(str).equals("jar")) {
			Iterator<ClassPath.Entry> it = list.iterator();
			while (it.hasNext()) {
				if (it.next().resolveFilePath(str2).equals(str)) {
					return;
				}
			}
			list.add(new ClassPath.Entry("lib", str, false, true, z));
			return;
		}
		if (FileSystem.isDirectory(str)) {
			try {
				List<String> listFiles = FileSystem.listFiles(str);
				Collections.sort(listFiles);
				Iterator<String> it2 = listFiles.iterator();
				while (it2.hasNext()) {
					addLibFileTree( it2.next(), str2, list, z);
				}
			}
			catch (Exception e) {
				AppLog.e(e);
			}
		}
    }


	/**
	 * subProjectMap key 项目路径 value 项目依赖[库项目(✓)，aar(×)]
	 */
	@Override
	public void init(String projectPath, Map<String, List<String>> subProjectMap, List<String> projectPaths) {
		// 建立项目与其依赖项

		// 清除maven解析缓存
		ServiceContainer.getMavenService().resetDepMap();
		// 预先解析一遍依赖 防止边检查边解析出现问题
		preResolving(projectPath);

		// 递归解析依赖，填充依赖版本
		resolvingChildProject(projectPath, new HashSet<String>());

		//添加主项目[一般来说就一个，除非是wearApp那种]
		// 顶层项目目录
		projectPaths.add(projectPath);

		// 建立依赖关系 项目 -> 依赖，库项目
		pN(projectPath, subProjectMap, projectPaths);
	}

	/**
	 * 用于填充projectPath的依赖，且递归填充子依赖的依赖
	 * 感觉subProjectMap只是 [项目 | 库项目 | aar] 与其依赖的映射
	 */
	private void pN(String projectPath, Map<String, List<String>> subProjectMap, List<String> projectPaths) {
		// 处理
		if (subProjectMap.containsKey(projectPath)) {
			return;
		}
		subProjectMap.put(projectPath, new ArrayList<String>());

		// 填充 projectPath 的依赖
		fillSubProjectDependency(projectPath, subProjectMap);

		// 遍历 projectPath的依赖并递归自己
		for (String dep : subProjectMap.get(projectPath)) {
			pN(dep, subProjectMap, projectPaths);
		}
	}

	/**
	 * 处理一个项目的库项目
	 */
	public static void resolvingChildProject(String projectPath, Set<String> resolvedProjects) {
		// AIDE是 resolvedProjects.contains(resolvedProjects)
		// 这应该是防止循环依赖的
		if (resolvedProjects.contains(projectPath)) {
			return;
		}
		// 标记已处理
		resolvedProjects.add(projectPath);

		if (isJavaGradleProject(projectPath)) {
			// 这个项目的所有依赖，包括库项目依赖
			List<Dependency> projectDependencies = getProjectDependencies(projectPath);

			// 优先解析此项目声明的依赖
			for (BuildGradle.Dependency dependency : projectDependencies) {
				if (dependency instanceof BuildGradle.MavenDependency) {
					// 解析maven依赖
					ServiceContainer.getMavenService().resolvingMavenDependency((BuildGradle.MavenDependency)dependency);
				}
			}

			// 解析库项目
			for (BuildGradle.Dependency dependency : projectDependencies) {
				if (dependency instanceof BuildGradle.ProjectDependency) {
					BuildGradle.ProjectDependency projectDependency = (BuildGradle.ProjectDependency)dependency;

					String settingsGradlePath = GradleTools.getSettingsGradlePath(projectPath);

					BuildGradleExt settingsGradleBuildGradleExt = buildGradleExt.getConfiguration(settingsGradlePath);

					// 计算库项目目录
					String projectDependencyPath = projectDependency.getProjectDependencyPath(projectPath, settingsGradleBuildGradleExt);

					if (FileSystem.isDirectory(projectDependencyPath)) {
						resolvingChildProject(projectDependencyPath, resolvedProjects);
					}
				}
			}
		}
	}

	static BuildGradle buildGradle = ZeroAicyExtensionInterface.getBuildGradle();
	static BuildGradleExt buildGradleExt = new BuildGradleExt();

	public static List<BuildGradle.Dependency> getProjectDependencies(String projectPath) {
		String buildGradlePath = GradleTools.getBuildGradlePath(projectPath);
		if (!FileSystem.isFileAndNotZip(buildGradlePath)) {
			return Collections.emptyList();
		}
		BuildGradle projectBuildGradle = buildGradle.getConfiguration(buildGradlePath);

		// 获得上一级目录的build.gradle
		String lastBuildGradlePath = GradleTools.getLastBuildGradlePath(projectPath);

		if (FileSystem.isFileAndNotZip(lastBuildGradlePath)) {
			// 添加全局依赖
			BuildGradle lastBuildGradle = buildGradle.getConfiguration(lastBuildGradlePath);
			if (lastBuildGradle.subProjectsDependencies.size() > 0 
				|| lastBuildGradle.allProjectsDependencies.size() > 0) {

				List<BuildGradle.Dependency> dependencies = new ArrayList<>();
				for (BuildGradle.Dependency dependency : lastBuildGradle.subProjectsDependencies) {
					if (dependency instanceof BuildGradle.MavenDependency) {
						dependencies.add(dependency);
					}
				}
				for (BuildGradle.Dependency dependency2 : lastBuildGradle.allProjectsDependencies) {
					if (dependency2 instanceof BuildGradle.MavenDependency) {
						dependencies.add(dependency2);
					}
				}
				dependencies.addAll(projectBuildGradle.dependencies);
				return dependencies;

			}
		}
		return projectBuildGradle.dependencies;
	}

	/**
	 * 填充项目依赖路径
	 */
	private static void fillSubProjectDependency(String projectPath, Map<String, List<String>> subProjectMap) {

		// 不会有jar 这是 库项目集合
		List<String> dependencys = subProjectMap.get(projectPath);
		Set<String> dependencySet = new HashSet<String>(dependencys);

		if (GradleTools.isAarEexplodedPath(projectPath)) {
			// Java项目不支持aar依赖
			// 应该报错的
			return;
		}

		if (! isJavaGradleProject(projectPath)) {
			return;
		}

		List<BuildGradle.Dependency> projectDependencies = getProjectDependencies(projectPath);

		for (BuildGradle.Dependency dependency : projectDependencies) {

			/* maven依赖
			 if (dependency instanceof BuildGradle.MavenDependency) {
			 BuildGradle.MavenDependency mavenDependency = ( BuildGradle.MavenDependency)dependency;

			 // 此maven依赖的所有依赖
			 List<String> resolveFullDependencyTree = ServiceContainer.getMavenService().resolveFullDependencyTree(null, mavenDependency);

			 for (String libFilePath : resolveFullDependencyTree) {
			 // List contains效率太慢
			 if (libFilePath.endsWith(".jar")
			 && !dependencySet.contains(libFilePath)
			 && !projectPath.equals(libFilePath)) {
			 // 添加依赖
			 dependencys.add(libFilePath);
			 dependencySet.add(libFilePath);
			 }
			 }
			 continue;
			 }
			 //*/

			// 处理库项目的依赖
			if (dependency instanceof BuildGradle.ProjectDependency) {
				BuildGradle.ProjectDependency projectDependency = (BuildGradle.ProjectDependency)dependency;

				String settingsGradlePath = GradleTools.getSettingsGradlePath(projectPath);
				String projectDependencyPath = projectDependency.getProjectDependencyPath(projectPath, buildGradleExt.getConfiguration(settingsGradlePath));

				// 添加库项目
				if (FileSystem.isDirectory(projectDependencyPath) 
					&& !dependencySet.contains(projectDependencyPath)) {
					// 将子库项目路径添加到集合
					dependencys.add(projectDependencyPath);
					dependencySet.add(projectDependencyPath);
				}
			}
		}
	}



	private void preResolving(String projectPath) {
		// 预解析
		// 可能我重写的MavenService有问题
		// 虽然看出一点问题，也修了，到没测试
	}

	/**
	 * 是否必须Premium版才可用
	 * 这个功能免费😂
	 */
	/**
	 * 此ProjectSupport是否付费版才能使用
	 */
	@Override
	public boolean isPremium() {
		return false;
	}


	/**
	 * 查询依赖是否包含文件路径
	 */
	@Override
	public boolean containJarLib(String path) {
		// 是否可以被添加成依赖
		// 先不实现
		return false;
	}
	/**
	 * 添加依赖
	 * 需要向build.gradle添加
	 */
	@Override
	public void addJarLib(String string) {
		// 先不实现

	}


	/**
	 * 验证此 ProjectSupport需要的资源有无需要下载
	 * 比如 C/Cpp项目的Ndk
	 * Gradle项目的maven依赖
	 */
	@Override
	public boolean verifyResourcesDownload() {
		// 先不实现
		ArrayList<BuildGradle.MavenDependency> arrayList = new ArrayList<>();
		arrayList.addAll(qp());
		if (!arrayList.isEmpty()) {
			ServiceContainer.getDownloadService().a8(ServiceContainer.getCurrentActivity(), arrayList, WB(ServiceContainer.getProjectService().getCurrentAppHome()), new Runnable(){
					@Override
					public void run() {
						ServiceContainer.getMavenService().resetDepPathMap();
						ServiceContainer.getProjectService().reloadingProject();
					}
				});
			return true;
		}
		return false;
	}
	private List<BuildGradle.RemoteRepository> WB(String str) {
		ArrayList<BuildGradle.RemoteRepository> arrayList = new ArrayList<>();
		if (GradleTools.isGradleProject(str)) {
			for (BuildGradle.Repository remoteRepository : ZeroAicyExtensionInterface.getBuildGradle().getConfiguration(GradleTools.getBuildGradlePath(str)).curProjectsRepositorys) {
				if (remoteRepository instanceof BuildGradle.RemoteRepository) {
					arrayList.add((BuildGradle.RemoteRepository)remoteRepository);
				}
			}
			String lastBuildGradlePath = GradleTools.getLastBuildGradlePath(str);
			if (FileSystem.isFileAndNotZip(lastBuildGradlePath)) {
				BuildGradle configuration = ZeroAicyExtensionInterface.getBuildGradle().getConfiguration(lastBuildGradlePath);
				for (BuildGradle.Repository remoteRepository2 : configuration.allProjectsRepositorys) {
					if (remoteRepository2 instanceof BuildGradle.RemoteRepository) {
						arrayList.add((BuildGradle.RemoteRepository)remoteRepository2);
					}
				}
				for (BuildGradle.Repository remoteRepository3 : configuration.subProjectsRepositorys) {
					if (remoteRepository3 instanceof BuildGradle.RemoteRepository) {
						arrayList.add((BuildGradle.RemoteRepository)remoteRepository3);
					}
				}
			}
		}
		return arrayList;
    }
	public List<BuildGradle.MavenDependency> qp() {
		ArrayList<BuildGradle.MavenDependency> arrayList = new ArrayList<>();
		for (String str : ServiceContainer.getProjectService().getLibraryMapping().keySet()) {
			if (GradleTools.isGradleProject(str)) {
				Iterator<BuildGradle.Dependency> it = getProjectDependencies(str).iterator();
				while (it.hasNext()) {
					BuildGradle.Dependency dependency = it.next();
					if (dependency instanceof BuildGradle.MavenDependency) {

						Iterator<BuildGradle.MavenDependency> it2 = ServiceContainer.getMavenService().getNotExistsLocalCache(null, (BuildGradle.MavenDependency)dependency).iterator();
						while (it2.hasNext()) {
							arrayList.add(it2.next());
						}
					}
				}
			}
		}
		return arrayList;
    }


	@Override
	public boolean J8() {

		return false;
	}

	@Override
	public void Mr() {

	}

	/**
	 * 返回EngineSolution
	 * 创建EngineSolution，用于代码分析进程处理依赖
	 */
	@Keep
	public EngineSolution makeEngineSolution() {
		// 向代码分析进程填充项目信息

		List engineSolutionProject = new ArrayList<EngineSolutionProject>();

		String androidJarPath = ServiceContainer.getProjectService().getAndroidJarPath();
		String annotationsJarPath = ServiceContainer.getAssetInstallationService().getAnnotationsJarPath();

		// BootClassPath
		EngineSolutionProject androidJarSolution = createAndroidJarEngineSolutionProject(androidJarPath, annotationsJarPath);
		engineSolutionProject.add(androidJarSolution);

		//
		String flavor = ServiceContainer.getProjectService().getFlavor();
		ProjectService projectService = ServiceContainer.getProjectService();
		for (String str : ServiceContainer.getProjectService().getMainAppWearApps()) {
			DW(engineSolutionProject, str, flavor, false, false, projectService.vy(str), new HashSet<String>());
		}

		List<String> hw = ServiceContainer.Hw();
		return new EngineSolution(engineSolutionProject, null, CodeModelFactory.findCodeModels(hw), hw);
	}

	// getBinPath(str) + "/classesrelease";
	public static String getOutputPath(String projectDir, boolean isDebug) {
		return  GradleTools.u7(projectDir, isDebug);
		/*
		 for (ClassPath.Entry entry : new ClassPath().getConfiguration(g3(projectDir)).Zo) {
		 if (entry.isOutputIKind()) {
		 String resolveFilePath = entry.resolveFilePath(projectDir);
		 if (isDebug) {
		 return resolveFilePath + "/debug";
		 }
		 return resolveFilePath + "/release";
		 }
		 }
		 if (isDebug) {
		 return projectDir + "/bin/debug";
		 }
		 return projectDir + "/bin/release";
		 //*/
    }

	private EngineSolutionProject Hw(String projectDir, List<ClassPath.Entry> classPathEntrys) {
		List<EngineSolution.File> engineSolutionFiles = new ArrayList<>();

		// 依赖标识符集合
		List<String> depProjectIds = new ArrayList<>();

		String debugOutputPath = getOutputPath(projectDir, true);
		String releaseOutputPath = getOutputPath(projectDir, false);

		for (ClassPath.Entry entry : classPathEntrys) {
			if (entry.isSrcKind()) {
				engineSolutionFiles.add(new EngineSolution.File(entry.resolveFilePath(projectDir), "Java", (String) null, false, false));
			}
			if (entry.isLibKind()) {
				depProjectIds.add(entry.getId());
			}
			if (entry.isOutputIKind()) {
				String resolveFilePath = entry.resolveFilePath(projectDir);
				debugOutputPath = resolveFilePath + "/debug";
				releaseOutputPath = resolveFilePath + "/release";
			}
		}
		depProjectIds.add(projectDir);
		depProjectIds.add("android.jar");
		return new EngineSolutionProject(projectDir, projectDir, projectDir, engineSolutionFiles, depProjectIds, true, "", debugOutputPath, releaseOutputPath, "1.5", false, false, false, false, "", new ArrayList(), new ArrayList(), new ArrayList());
    }

	private static void DW(List<EngineSolutionProject> engineSolutionProject, String projectDir, String flavor, boolean z, boolean z2, Map<String, List<String>> map, Set<String> handledSet) {
		if (handledSet.contains(projectDir)) {
			return;
		}
		handledSet.add(projectDir);

		List<ClassPath.Entry> projectClassPathEntrys = getProjectClassPathEntrys(projectDir);
		engineSolutionProject.add(createProjectEngineSolutionProject(projectDir, flavor, z, z2, projectClassPathEntrys, map));

		for (ClassPath.Entry entry : projectClassPathEntrys) {
			if (entry.isLibKind()) {
				boolean hasEngineSolutionProject = false;
				for (EngineSolutionProject next : engineSolutionProject) {
					if (next.projectName.equals(entry.getId())) {
						hasEngineSolutionProject = true;
						break;
					}
				}
				if (!hasEngineSolutionProject) {
					engineSolutionProject.add(createEntryEngineSolutionProject(projectDir, flavor, entry, projectClassPathEntrys, map));
				}
			}
		}

		for (String next : map.get(projectDir)) {
			DW(engineSolutionProject, next, flavor, z, true, map, handledSet);			
		}
    }
	private static EngineSolutionProject createEntryEngineSolutionProject(String projectDir, String flavor, ClassPath.Entry entry, List<ClassPath.Entry> projectClassPathEntrys, Map<String, List<String>> map) {

		List<EngineSolution.File> sourceSolutionFiles = new ArrayList<>();

		String resolveFilePath = entry.resolveFilePath(projectDir);
		sourceSolutionFiles.add(new EngineSolution.File(resolveFilePath, "Java Binary", "", false, true));

		ArrayList<String> depProjectIds = new ArrayList<>();
		for (ClassPath.Entry entry2 : projectClassPathEntrys) {
			if (entry2.isLibKind()) {
				depProjectIds.add(entry2.getId());
			}
			if (entry2.isAndroidFramework()) {
				depProjectIds.add("android.jar");
			}
		}
		for (String dependencieProjectDir : map.get(projectDir)) {
			depProjectIds.add(dependencieProjectDir);
			for (ClassPath.Entry entry3 : getProjectClassPathEntrys(dependencieProjectDir)) {
				if (entry3.isLibKind()) {
					depProjectIds.add(entry3.getId());
				}
				if (entry3.isAndroidFramework()) {
					depProjectIds.add("android.jar");
				}
			}
		}
		return new EngineSolutionProject(
			entry.getId(), resolveFilePath, 
			resolveFilePath, sourceSolutionFiles, 
			depProjectIds, false, "", "", "", "", 
			false, false, false, false, "", 
			new ArrayList<String>(), 
			new ArrayList<String>(),
			new ArrayList<String>());

	}

	private static EngineSolutionProject createProjectEngineSolutionProject(String projectDir, String flavor, boolean z, boolean z2, List<ClassPath.Entry> projectClassPathEntrys, Map<String, List<String>> map) {

		ArrayList<EngineSolution.File> arrayList = new ArrayList<>();
		for (ClassPath.Entry entry : projectClassPathEntrys) {
			if (entry.isSrcKind()) {
				String resolveFilePath = entry.resolveFilePath(projectDir);
				arrayList.add(new EngineSolution.File(resolveFilePath, "Java", (String) null, false, false));
				arrayList.add(new EngineSolution.File(resolveFilePath, "AIDL", (String) null, false, false));
			}
		}

		List<String> depProjectIds = new ArrayList<>();
		depProjectIds.add(projectDir);
		for (ClassPath.Entry entry2 : projectClassPathEntrys) {
			if (entry2.isLibKind()) {
				depProjectIds.add(entry2.getId());
			}
			if (entry2.isAndroidFramework()) {
				depProjectIds.add("android.jar");
			}
		}

		ArrayList<String> arrayList3 = new ArrayList<>();
		VH(xd.FH(projectDir, map), flavor, arrayList3);
		for (String next : arrayList3) {
			depProjectIds.add(next);

		}
		ArrayList<String> arrayList4 = new ArrayList<>();
		ArrayList<String> arrayList5 = new ArrayList<>();
		ArrayList<String> arrayList6 = new ArrayList<>();
		boolean checked = !GradleTools.isAarEexplodedPath(projectDir);
		String debugOutputPath = AndroidProjectSupport.et(projectDir, true);
		String releaseOutputPath = AndroidProjectSupport.et(projectDir, false);
		return new EngineSolutionProject(projectDir, projectDir, projectDir, arrayList, depProjectIds, checked, "", debugOutputPath, releaseOutputPath, "1.5", false, false, false, z2, "", arrayList5, arrayList4, arrayList6);

    }
	private static void VH(List<String> engineSolutionProject, String projectDir, List<String> list2) {
		for (String str2 : engineSolutionProject) {
			if (!list2.contains(str2)) {
				list2.add(str2);
			}
			for (ClassPath.Entry entry : getProjectClassPathEntrys(str2)) {
				if (entry.isLibKind()) {
					list2.add(entry.getId());
				}
			}
		}
    }


	private static EngineSolutionProject createAndroidJarEngineSolutionProject(String androidJarPath, String annotationsJarPath) {
		ArrayList<EngineSolution.File> files = new ArrayList<>();
		files.add(new EngineSolution.File(androidJarPath, "Java Binary", "", false, true));
		files.add(new EngineSolution.File(annotationsJarPath, "Java Binary", "", false, true));

		List<String> depProjectIds =Collections.singletonList("android.jar");
		return new EngineSolutionProject(
			"android.jar", androidJarPath, 
			"android.jar", files, 
			depProjectIds, false, 
			"", "", "", "", false, 
			false, false, false, "", 
			new ArrayList<String>(), 
			new ArrayList<String>(), 
			new ArrayList<String>());
    }

	@Override
	public boolean a8(String string) {
		return false;
	}

	@Override
	public boolean isInCurrentProjectDirectory(String path) {
		if (path == null) {
			return false;
		}

		ProjectService projectService = ServiceContainer.getProjectService();
		for (String projectDir : projectService.getMainAppWearApps()) {

			if (projectDir == null) {
				continue;
			}
			if (path.startsWith(projectDir)) {
				return true;
			}
		}
		for (String projectDir : projectService.getLibraryMapping().keySet()) {
			if (projectDir == null) {
				continue;
			}
			if (path.startsWith(projectDir)) {
				return true;
			}
		}
		return false;
	}


	@Override
	public void cn(List<String> list, boolean p) {
		// 新修改且保存的文件列表
		if (list == null) {
			return;
		}


		for (String path : list) {
			if (path == null) continue;
			String name = FileSystem.getName(path);
			// AppLog.d(TAG, cn name: %s path: %s ", name, path);

			if ("build.gradle".equals(name)) {
				// 刷新项目
				ServiceContainer.getProjectService().reloadingProject();
				return;
			}
		}

		// 刷新项目
		// ServiceContainer.getProjectService().reloadingProject();

	}

	@Override
	public void ei(String string) {

	}

	/**
	 * 安卓项目-判断依据 项目目录 src文件夹存在 build.gradle存在
	 * 或者 AndroidManifest.xml存在
	 * Java项目-判断依据 项目目录 .classpath文件存在
	 *
	 * 是否是支持此项目
	 * 此项目不支持渠道包 ？
	 * 
	 */
	@Override
	public boolean isSupport(String projectPath) {
		return JavaGradleProjectSupport.isJavaGradleProject(projectPath);
	}


	@Override
	public boolean gW() {
		return false;
	}

	/*
	 * 渠道包
	 */
	@Override
	public List<String> getProductFlavors(String path) {
		return null;
	}

	@Override
	public void gn() {

	}

	@Override
	public boolean isVersionSupport(String string) {
		return true;
	}

	@Override
	public void j6() {

	}

	@Override
	public boolean lg() {
		return true;
	}

	@Override
	public void nw(String string) {
	}


	// 添加到项目建议
	@Override
	public List<String> getAddToProjectAdvise(String string) {
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("com.badlogicgames.gdx:gdx:+");
		arrayList.add("com.badlogicgames.gdx:gdx-bullet:+");
		arrayList.add("com.badlogicgames.gdx:gdx-freetype:+");
		arrayList.add("com.badlogicgames.gdx-controllers:gdx-controllers-core:+");
		arrayList.add("com.badlogicgames.gdx:gdx-box2d:+");
		arrayList.add("com.badlogicgames.box2dlights:box2dlights:+");


		return arrayList;
	}

	@Override
	public String tp(String string) {
		return null;
	}

	@Override
	public boolean u7(String string) {
		return false;
	}

	/**
	 * debug com.adrt.SET_BREAKPOINTS
	 */

	/**
	 * 与 sh 互为逆运算
	 * 根据源码绝对路径找到相对路径 [全类名]
	 */
	@Override
	public String v5(String str) {
		ProjectService projectService = ServiceContainer.getProjectService();
		String Ev = Ev(projectService.getLibraryMapping(),
					   projectService.getFlavor(), 
					   FileSystem.getParent(str));

		if (Ev == null) {
			return str;
		}
		return Ev.replace('.', '/') + "/" + FileSystem.getName(str);
	}

	/**
	 * 与 v5 互为逆运算
	 * 根据源码相对路径[全类名]找到 绝对路径
	 */
	public String sh(String str) {
		String[] aj = aj(ServiceContainer.getProjectService().getLibraryMapping(), ServiceContainer.getProjectService().getFlavor());
		if (!str.startsWith("/")) {
			str = "/" + str;
		}
		for (String str2 : aj) {
			String str3 = str2 + str;
			if (FileSystem.exists(str3)) {
				return str3;
			}
		}
		return null;
    }

	public static String Ev(Map<String, List<String>> map, String flavor, String str2) {
		for (String str3 : aj(map, flavor)) {
			if (FileSystem.isPrefix(str3, str2)) {
				return FileSystem.getRelativePath(str3, str2).replace('/', '.');
			}
		}
		return null;
    }
	public static String[] aj(Map<String, List<String>> map, String flavor) {
		ArrayList<String> arrayList = new ArrayList<>();
		for (String projectDir : map.keySet()) {
			if (!GradleTools.isAarEexplodedPath(projectDir)) {
				for (ClassPath.Entry entry : getProjectClassPathEntrys(projectDir)) {
					if (entry.isSrcKind()) {
						arrayList.add(entry.resolveFilePath(projectDir));
					}
				}
			}
		}
		String[] strArr = new String[arrayList.size()];
		arrayList.toArray(strArr);
		return strArr;
    }
	// GradleTools.getAndroidMkPath
	@Override
	public boolean vy(String dirPath) {
		return GradleTools.getAndroidMkPath(dirPath);
	}

	@Override
	public String getProjectPackageName() {
		return ServiceContainer.getContext().getPackageName();
	}

	/**
	 * command_files_add_new_xxx isVisible
	 */
	@Override
	public boolean Zo(String dirPath) {
		return AddAndroidFiles.isJavaSourceDir(dirPath) && isInCurrentProjectDirectory(dirPath);
	}

	// 返回command_files_add_new_xxx字符串
	@Override
	public int rN(String dirPath) {
		return AddAndroidFiles.getAddTypeName(dirPath);
	}
	// 返回图片id
	@Override
	public int we(String dirPath) {
		return AddAndroidFiles.getDrawableId(dirPath);
	}
	@Override
	public void SI(String dirPath, ValueRunnable<String> valueRunnable) {
		AddAndroidFiles.DW(dirPath, valueRunnable);
	}

	/**
	 * 
	 */
	@Override
	public void P8(final String projectDir, final String str2) {
		MainActivity mainActivity = ServiceContainer.getMainActivity();
		MessageBox.rN(mainActivity, ServiceContainer.getString(R.string.dialog_add_to_project_new_library), ServiceContainer.getString(R.string.dialog_add_to_project_new_library_message, str2), new Runnable(){
				@Override
				public void run() {
					if (GradleTools.isGradleProject(projectDir)) {
						String buildGradlePath = GradleTools.getBuildGradlePath(projectDir);
						buildGradle.getConfiguration(buildGradlePath).addMavenDependency(str2);
						ServiceContainer.getProjectService().reloadingProject();
						MainActivity mainActivity = ServiceContainer.getMainActivity();
						Toast.makeText(mainActivity, "Library has been added", Toast.LENGTH_SHORT).show();
						return;
					}
				}
			}, null);
	}



	/**
	 * 模板
	 */
	@Override
	public TemplateService.TemplateGroup[] getTemplateGroups() {

		int templateGroupId = 3;
		String templateGroupName = "Java Application";

		boolean hasAppPackageName = false;
		Template template =
			new TemplateService.Template(
			// 可以为null
			JavaGradleProjectSupport.this, 
			// 组所在序号 2是Mobile Game 3是Java项目
			templateGroupId, 
			// 组的名称 Android App | Mobile Game | Java Application | ....
			// Id 与 name必须相同否则会另起一行
			templateGroupName, 
			// 使用了哪些特性
			"Gradle/Android SDK/Java", 
			// 项目默认名称
			"MyJavaGradleConsoleApp", 
			// 是否有App包名 一般只有Android App才需要
			hasAppPackageName, 
			false, 
			// 什么用都没有
			"com.aide.ui", 
			// 教程
			"JAVA", "course_java", 
			// 是否显示[猜测]
			true);

		// 修复创建后不打开项目的bug
		String templateName = "Java Gradle Application";
		TemplateGroup javaGradleApplicationTemplateGroup = 
			new TemplateService.TemplateGroup(
			// 模板名称
			templateName, 
			// 模板信息
			template, 
			// 模板图标
			R.drawable.ic_launcher_java, 
			// 模板资源相对路径
			"JavaGradleConsole.zip", 
			// 需要打开的文件
			new String[]{"Main.java", "build.gradle"}, 
			// 主项目相对路径
			"console");

		return new TemplateService.TemplateGroup[]{javaGradleApplicationTemplateGroup};
    }

	/*

	 @Override
	 public List<Course.File> getTrainerCourses() {
	 try {
	 if (parametersEnabled) {
	 Probelytics.printlnParameters(-1751453124824682264L, this);
	 }
	 return Arrays.asList(new Course.File("course_java", 1, new String[]{"com.aide.ui", "com.aide.trainer.java"}));
	 } catch (Throwable th) {
	 if (exceptionEnabled) {
	 Probelytics.printlnException(th, -1751453124824682264L, this);
	 }
	 throw th;
	 }
	 }
	 */

	/**
	 * 教程
	 */
	@Override
	public List<com.aide.ui.trainer.Course.File> getTrainerCourses() {
		return Collections.emptyList();
	}

}
