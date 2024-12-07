package com.aide.codemodel.language.java;

import android.util.SparseArray;
import com.aide.codemodel.api.FileSpace;
import com.aide.codemodel.api.collections.SetOfInt;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import com.aide.ui.services.AssetInstallationService;
import java.util.Arrays;

public class Project {

	public static final List<String> generalArgs;
	public static final String coreLambdaStubsJarPath = AssetInstallationService.DW("core-lambda-stubs.jar", true);

	static{
		generalArgs = new ArrayList<>(6);
		generalArgs.add("-parameters");
		generalArgs.add("-g");

		int compileVersion = 17;

		generalArgs.add("-source");
		generalArgs.add(String.valueOf(compileVersion));
		generalArgs.add("-target");
		generalArgs.add(String.valueOf(compileVersion));
	}


	// 
	private final boolean isJarProject;
	private final String jarProjectPath;
	private final boolean isAarProject;

	// 
	private final String releaseOutputPath;


	// 当前项目id;
	private final int assemblyId;
	public final String assemblyName;
	// 项目
	public Project(int assemblyId, 
				   FileSpace.Assembly assembly) {

		// String assemblyName = FileSpace.Assembly.VH(assembly);
		this.assemblyId = assemblyId;
		this.assemblyName = FileSpace.Assembly.VH(assembly);

		String projectPath = FileSpace.Assembly.Zo(assembly);
		this.isAarProject = projectPath.endsWith(".aar");

		File projectFile = new File(projectPath);
		this.isJarProject = projectFile.isFile();
		if (isJarProject) {
			this.jarProjectPath = projectPath;
			this.releaseOutputPath = null;
			return;
		}

		this.jarProjectPath = null;
		this.releaseOutputPath = FileSpace.Assembly.getReleaseOutputPath(assembly);

	}


	public void initialize() {
		if (isJarProject()) {
			return;
		}
		// System.out.printf("assemblyName %s projectReferences: %s\n", assemblyName, this.projectReferences);

		// 添加统一配置
		this.projectArgs.addAll(generalArgs);

		// 输出路径
		this.projectArgs.add("-bootclasspath");
		this.projectArgs.add(bootclasspath);
		// 输出路径
		this.projectArgs.add("-d");
		this.projectArgs.add(this.releaseOutputPath);
		// 确保文件输出
		File file = new File(this.releaseOutputPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		Set<String> classpaths = new HashSet<>();
		// 添加 coreLambdaStubsJar
		classpaths.add(coreLambdaStubsJarPath);
		parserClassPath(new HashSet<Project>(), classpaths);

		StringJoiner stringJoiner = new StringJoiner(":");
		for (String path : classpaths) {
			stringJoiner.add(path);
		}
		this.projectArgs.add("-cp");
		this.projectArgs.add(stringJoiner.toString());

	}


	private void parserClassPath(Set<Project> handleProjects, Set<String> classpaths) {

		if (isJarProject()) {
			classpaths.add(this.jarProjectPath);
		} else if (isAarProject()) {

		} else {
			// 自己的缓存目录
			classpaths.add(this.releaseOutputPath);
		}
		handleProjects.add(this);

		for (Project project : projectReferences) {
			// 防止jar aar 循环依赖
			if (handleProjects.contains(project)) {
				// 已处理
				continue;
			}
			project.parserClassPath(handleProjects, classpaths);
		}
	}


	private final Set<Project> projectReferences = new HashSet<>();
	public void addProjectReferences(Project projectReference) {
		this.projectReferences.add(projectReference);
	}
	public Set<Project> getProjectReferences() {
		return projectReferences;
	}

	public boolean isJarProject() {
		return this.isJarProject;
	}
	public boolean isAarProject() {
		return this.isJarProject;
	}


	String bootclasspath;
	public void setBootClasspath(String bootclasspath) {
		this.bootclasspath = bootclasspath;
	}

	private List<String> projectArgs = new ArrayList<>();
	public List<String> getArgs() {
		return this.projectArgs;
	}

	final Set<String> compilerSourceFiles = new HashSet<>();
	public void addCompileFile(String compilerSourceFile) {
		this.compilerSourceFiles.add(compilerSourceFile);
	}
	public Set<String> getCompilerSourceFiles() {
		return compilerSourceFiles;
	}
	public boolean needCompile() {
		return !isAarProject()
			&& !isJarProject()
			&& !this.compilerSourceFiles.isEmpty() 
			;
	}

	public void completed() {
		// 编译完毕
		this.compilerSourceFiles.clear();
	}

	@Override
	public String toString() {
		return this.assemblyName;
	}


}
