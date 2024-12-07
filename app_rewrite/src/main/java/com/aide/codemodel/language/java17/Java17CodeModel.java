package com.aide.codemodel.language.java17;

import android.util.SparseArray;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.FileSpace;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.SyntaxTreeStyles;
import com.aide.codemodel.api.abstraction.CodeCompiler;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.abstraction.Debugger;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.Preprocessor;
import com.aide.codemodel.api.collections.FunctionOfIntInt;
import com.aide.codemodel.api.collections.OrderedMapOfIntInt;
import com.aide.codemodel.api.collections.SetOfFileEntry;
import com.aide.codemodel.language.java.Project;
import com.aide.codemodel.language.java.ProjectEnvironment;
import io.github.zeroaicy.util.reflect.ReflectPie;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Java17CodeModel implements CodeModel {

	// 默认 17
	private final boolean disable;

	private String targetVersion = "17";

	private final Model model;
	private FileSpace fileSpace;
	private SetOfFileEntry solutionFiles;

	Java17Language java17Language;
	List<Language> java17Languages = new ArrayList<>();

	//Java17Compiler Java17Compiler;

	public Java17CodeModel(Model model) {
		this.model = model;

		if (model == null) {
			this.disable = true;
			return;

		} 
		this.disable = false;
		this.java17Language = new Java17Language(this.model);
		this.java17Languages.add(this.java17Language);
		//this.Java17Compiler = new Java17Compiler(this.model);

		this.fileSpace = this.model.fileSpace;
		this.fileSpaceReflect = ReflectPie.on(this.fileSpace);

		this.solutionFiles = this.fileSpace.getSolutionFiles();		
	}

	// gradle module环境
	SparseArray<ProjectEnvironment> projectEnvironments;

	private ReflectPie fileSpaceReflect;

	// model::J0() -> configure
	public void initEnv() {
		Model model = this.model;
		if (model == null || projectEnvironments != null) {
			return;
		}

		if (projectEnvironments == null) {
			projectEnvironments = new SparseArray<>();
		}
		
		ProjectEnvironment.init(model, projectEnvironments);

	}

	private void method() {
		SparseArray<Project> projects = new SparseArray<>();
		// 构建项目依赖信息
		// 构建库依赖信息

		// android.jar AssemblyId[路径为android.jar]
		String bootclasspath = null;

		int androidJarAssemblyId = 0;

		HashMap<Integer, FileSpace.Assembly> assemblyMap = getAssemblyMap();
		// 遍历创建项目
		for (Map.Entry<Integer, FileSpace.Assembly> entry : assemblyMap.entrySet()) {
			Integer assemblyId = entry.getKey();
			FileSpace.Assembly assembly = entry.getValue();

			String assemblyName = FileSpace.Assembly.VH(assembly);
			if ("rt.jar".equals(assemblyName)
				|| "android.jar".equals(assemblyName)) {
				androidJarAssemblyId = assemblyId;
				bootclasspath = FileSpace.Assembly.Zo(assembly);
				continue;
			}
			// 创建项目
			// System.out.printf("assemblyName %s id: %s\n", assemblyName, assemblyId);
			Project project = new Project(assemblyId, assembly);
			projects.put(assemblyId, project);
		}


		OrderedMapOfIntInt assemblyReferences = getAssemblyReferences();
		OrderedMapOfIntInt.Iterator referencesIterator = assemblyReferences.default_Iterator;
		referencesIterator.init();
		// 遍历所有 SolutionProject的 AssemblyId
		while (referencesIterator.hasMoreElements()) {
			int projectAssemblyId = referencesIterator.nextKey();
			int referencedProjectAssembly = referencesIterator.nextValue();

			// 自己会依赖自己，排除
			if (projectAssemblyId == referencedProjectAssembly
			// 过滤referencedProjectAssembly
			// 这个单独指定
				|| referencedProjectAssembly == androidJarAssemblyId) {
				continue;
			}

			Project project = projects.get(projectAssemblyId);
			Project referencedProject = projects.get(referencedProjectAssembly);
			if (referencedProject == null) {
				FileSpace.Assembly assembly = assemblyMap.get(referencedProjectAssembly);
				String assemblyName = FileSpace.Assembly.VH(assembly);
				System.out.printf("没有创建 assemblyName %s id: %s\n ", assemblyName, referencedProjectAssembly);
				continue;
			}
			project.addProjectReferences(referencedProject);
		}

		// 填充项目信息
		for (int i = 0, size = projects.size(); i < size; i++) {
			Project project = projects.valueAt(i);
			if (project.isJarProject()) {
				continue;
			}

			project.setBootClasspath(bootclasspath);
			project.initialize();
		}
	}

	/**
	 * AssemblyId -> Assembly[assemblyName，assembly路径，]
	 */
	public HashMap<Integer, FileSpace.Assembly> getAssemblyMap() {
		return this.fileSpaceReflect.get("assemblyMap");
	}

	/**
	 * assembly之间的依赖关系
	 * key -> value[被依赖]
	 */
	public OrderedMapOfIntInt getAssemblyReferences() {
		return fileSpaceReflect.get("assemblyReferences");
	}

	/**
	 * 文件与所在项目
	 */
	public FunctionOfIntInt getFileAssembles() {
		return this.fileSpaceReflect.get("fileAssembles");
	}

	/*
	 * 注册文件容器
	 */
	public SetOfFileEntry getRegisteredSolutionFiles() {
		return this.fileSpaceReflect.get("registeredSolutionFiles");
	}


	// 词法分析器 高亮
	@Override
	public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTreeStyles> syntaxTreeStylesMap) {

	}

	// 语法树填充
	@Override
	public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTree> syntaxTreeMap, boolean p) {

		if (!syntaxTreeMap.containsKey(this.java17Language)) {
			return;
		}
		// 所在 Assembly Id
		int assemblyId = this.fileSpace.getAssembly(fileEntry);
		
		ProjectEnvironment projectEnvironment = projectEnvironments.get(assemblyId);
		if( projectEnvironment == null ){
			return;
		}
		SyntaxTree syntaxTree = syntaxTreeMap.get(this.java17Language);
		// projectEnvironment.updateFile(fileEntry, reader, syntaxTree);
		

	}


	@Override
	public CodeCompiler getCodeCompiler() {
		return null; // this.Java17Compiler;
	}

	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{".java"};
	}

	@Override
	public String[] getExtendFilePatterns() {
		return new String[0];
	}

	@Override
	public List<Language> getLanguages() {
		return this.java17Languages;
	}

	@Override
	public String getName() {
		return "Java";
	}

	@Override
	public void update() {

	}


	/******************************no support start ******************************************/

	@Override
	public boolean isSupportArchiveFile() {
		return false;
	}

	@Override
	public void closeArchive() {

	}

	@Override
	public Reader getArchiveEntryReader(String string, String string1, String string2) {
		return null;
	}
	@Override
	public String[] getArchiveEntries(String string) {
		return null;
	}

	@Override
	public long getArchiveVersion(String string) {
		return 0;
	}
	@Override
	public Preprocessor getPreprocessor() {
		return null;
	}
	@Override
	public Debugger getDebugger() {
		return null;
	}

	@Override
	public void processVersion(FileEntry fileEntry, Language language) {

	}

	@Override
	public boolean u7() {
		return false;
	}

	/******************************no support end******************************************/




}
