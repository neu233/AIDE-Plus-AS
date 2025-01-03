package com.aide.codemodel.language.java;

import android.text.TextUtils;
import android.util.SparseArray;
import com.aide.codemodel.api.ErrorTable;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.FileSpace;
import com.aide.codemodel.api.HighlighterType;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.callback.HighlighterCallback;
import com.aide.codemodel.api.collections.FunctionOfIntInt;
import com.aide.codemodel.api.collections.OrderedMapOfIntInt;
import com.aide.codemodel.api.collections.SetOfFileEntry;
import com.aide.codemodel.api.collections.SetOfInt;
import com.aide.common.AppLog;
import com.aide.engine.EngineSolution;
import com.aide.engine.EngineSolutionProject;
import com.aide.ui.services.AssetInstallationService;
import io.github.zeroaicy.util.IOUtils;
import io.github.zeroaicy.util.reflect.ReflectPie;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblem;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import com.aide.codemodel.api.FileSpace.Assembly;
import io.github.zeroaicy.aide.utils.ZeroAicyBuildGradle;
import com.aide.codemodel.api.abstraction.Language;

/**
 * 使用 Eclipse JDT Compiler 进行增量语义分析
 * 此类是Module独立环境 因为R.java文件不止一个
 * 每一个Module会有 源码 库依赖 库项目依赖
 */
public class ProjectEnvironment {


	/**
	 * AssemblyId -> Assembly[assemblyName，assembly路径，]
	 */
	public static HashMap<Integer, Assembly> getAssemblyMap(ReflectPie fileSpaceReflect) {
		return fileSpaceReflect.get("assemblyMap");
	}

	/**
	 * assembly之间的依赖关系
	 * key -> value[被依赖]
	 */
	public static OrderedMapOfIntInt getAssemblyReferences(ReflectPie fileSpaceReflect) {
		return fileSpaceReflect.get("assemblyReferences");
	}

	/**
	 * 文件与所在项目
	 */
	public static FunctionOfIntInt getFileAssembles(ReflectPie fileSpaceReflect) {
		return fileSpaceReflect.get("fileAssembles");
	}

	/*
	 * 注册文件容器
	 */
	public static SetOfFileEntry getRegisteredSolutionFiles(ReflectPie fileSpaceReflect) {
		return fileSpaceReflect.get("registeredSolutionFiles");
	}

	public static void fillFileEntry(SparseArray<ProjectEnvironment> projectEnvironments, Model model, FileEntry fileEntry) {
		try {
			FileSpace fileSpace = model.fileSpace;
			int fileAssemblyId = fileSpace.getAssembly(fileEntry);
			for (int i = 0, size = projectEnvironments.size(); i < size; i++) {
				ProjectEnvironment projectEnvironment = projectEnvironments.valueAt(i);

				int rootAssemblyId = projectEnvironment.assemblyId;
				// R必须是projectEnvironment的 assemblyId
				// 相对 projectEnvironment是 
				if (fileSpace.isRJavaFileEntry(fileEntry) 
					&& fileAssemblyId != rootAssemblyId) {
					continue;
				}

				if (!projectEnvironment.containsId(fileAssemblyId)) {
					continue;
				}

				try {
					// Set<String> sourcePaths = getSourcePaths(fileSpace, projectEnvironment, fileEntry);
					// projectEnvironment.update(fileEntry);
				}
				catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void init(Model model, SparseArray<ProjectEnvironment> projectEnvironments) {

		FileSpace fileSpace = model.fileSpace;
		ReflectPie fileSpaceReflect = ReflectPie.on(fileSpace);

		// 置空
		SparseArray<SolutionProject> projects = new SparseArray<>();

		Map<Integer, Assembly> assemblyMap = getAssemblyMap(fileSpaceReflect);
		
		// OrderedMapOfIntInt允许多个相同的key
		// 应该是 int int 对
		OrderedMapOfIntInt assemblyReferences = getAssemblyReferences(fileSpaceReflect); // fileSpaceReflect.get("assemblyReferences");
		int mainProjectAssemblyId = findMainProjectAssemblyId(assemblyReferences, assemblyMap);
		
		// 构建项目依赖信息并返回 androidJarAssemblyId(bootclasspath)
		int androidJarAssemblyId = initSolutionProjects(mainProjectAssemblyId, projects, assemblyMap, fileSpaceReflect);
		
		// 填充项目依赖
		fillProjectReferences(androidJarAssemblyId, projects, assemblyMap, fileSpaceReflect);
		
		if (androidJarAssemblyId < 0) {
			throw new Error("not found [android.jar | rt.jar](bootclasspath)");
		}
		
		// android.jar AssemblyId[路径为android.jar]
		String bootclasspath = Assembly.Zo(assemblyMap.get(androidJarAssemblyId));
		
		Assembly mainModuleAssembly = assemblyMap.get(mainProjectAssemblyId);
		
		
		// 支持 CompilerOptions
		String mainModulePath = Assembly.Zo(mainModuleAssembly);
		String buildGradlePath = mainModulePath + "/build.gradle";
		
		// 默认Java23
		String sourceVersion = "23", targetVersion = "23";
		
		if ( com.aide.ui.util.FileSystem.exists(buildGradlePath) ){
			ZeroAicyBuildGradle configuration = ZeroAicyBuildGradle.getSingleton().getConfiguration(buildGradlePath);
			
			String sourceCompatibility = configuration.getSourceCompatibility();
			if( !TextUtils.isEmpty(sourceCompatibility)){
				sourceVersion = sourceCompatibility;				
			}
			
			String targetCompatibility = configuration.getTargetCompatibility();
			if( !TextUtils.isEmpty(targetCompatibility)){
				targetVersion = targetCompatibility;				
			}
		}
		AppLog.d("sourceVersion", sourceVersion);
		AppLog.d("sourceVersion", targetVersion);
		
		
		// compilerOptions是单例
		CompilerOptions compilerOptions = getCompilerOptions();
		Map<String, String> optionsMap = new HashMap<>();

		// 处理 build.gradle compileOptions{}
		optionsMap.put(CompilerOptions.OPTION_Source, sourceVersion);
		optionsMap.put(CompilerOptions.OPTION_Compliance, sourceVersion);
		optionsMap.put(CompilerOptions.OPTION_TargetPlatform, targetVersion);
		
		compilerOptions.set(optionsMap);
		
		// 填充项目信息
		for (int i = 0, size = projects.size(); i < size; i++) {
			SolutionProject project = projects.valueAt(i);
			
			if (!project.isModule) {
				// 排除非Module项目
				continue;
			}
			
			// gradle module
			// 创建 ProjectEnvironment
			int assemblyId = project.getAssemblyId();
			
			// 创建项目环境
			ProjectEnvironment projectEnvironment = new ProjectEnvironment(model, project, bootclasspath);
			
			// 缓存
			projectEnvironments.put(assemblyId, projectEnvironment);
		}

	}
	
	private static int findMainProjectAssemblyId(OrderedMapOfIntInt assemblyReferences, Map<Integer, Assembly> assemblyMap) {
		SetOfInt referencedSet = new SetOfInt();
		
		OrderedMapOfIntInt.Iterator default_Iterator = assemblyReferences.default_Iterator;
		// 重置
		default_Iterator.init();
		// 遍历
		while (default_Iterator.hasMoreElements()) {
			int key = default_Iterator.nextKey();
			int referenced = default_Iterator.nextValue();

			// 自己会依赖自己，排除
			if (key != referenced 
				&& !referencedSet.contains(referenced)) {
				referencedSet.put(referenced);
			}
		}


		for (Integer assemblyId : assemblyMap.keySet()) {
			// int assemblyId = assemblyIdInteger.intValue();
			if (referencedSet.contains(assemblyId)) {
				continue;
			}
			return assemblyId;
		}
		return -1;
	}
	
	
	private static int initSolutionProjects(int mainProjectAssemblyId, SparseArray<SolutionProject> projects, Map<Integer, Assembly> assemblyMap, ReflectPie fileSpaceReflect) {
		int androidJarAssemblyId = -1;
				
		for (Map.Entry<Integer, Assembly> entry : assemblyMap.entrySet()) {
			Integer assemblyId = entry.getKey();
			Assembly assembly = entry.getValue();

			String assemblyName = Assembly.VH(assembly);
			if ("rt.jar".equals(assemblyName)
				|| "android.jar".equals(assemblyName)) {
				androidJarAssemblyId = assemblyId;
				continue;
			}
			boolean isMainModule = assemblyId == mainProjectAssemblyId;
			// 创建项目
			SolutionProject project = new SolutionProject(assemblyId, assembly, isMainModule);
			
			projects.put(assemblyId, project);
		}
		
		
		return androidJarAssemblyId;
	}

	private static void fillProjectReferences(int androidJarAssemblyId, SparseArray<SolutionProject> projects, Map<Integer, Assembly> assemblyMap, ReflectPie fileSpaceReflect) {
		
		OrderedMapOfIntInt assemblyReferences = getAssemblyReferences(fileSpaceReflect);
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

			SolutionProject project = projects.get(projectAssemblyId);
			SolutionProject referencedProject = projects.get(referencedProjectAssembly);

			if (referencedProject == null) {
				Assembly assembly = assemblyMap.get(referencedProjectAssembly);
				String assemblyName = Assembly.VH(assembly);
				System.out.printf("没有创建 assemblyName %s id: %s\n ", assemblyName, referencedProjectAssembly);
				continue;
			}
			project.addProjectReferences(referencedProject);
		}
	}

	// core-lambda-stubs.jar
	public static final String coreLambdaStubsJarPath = AssetInstallationService.DW("core-lambda-stubs.jar", true);
	// 通用参数
	private static CompilerOptions compilerOptions;

	private static CompilerOptions getCompilerOptions() {
		if (compilerOptions == null) {
			compilerOptions = new CompilerOptions();
			compilerOptions.parseLiteralExpressionsAsConstants = false;
			compilerOptions.enablePreviewFeatures = true;
			
			// -g
			compilerOptions.produceDebugAttributes = 
				ClassFileConstants.ATTR_SOURCE
				| ClassFileConstants.ATTR_LINES 
				| ClassFileConstants.ATTR_VARS;
			
			// -parameters
			compilerOptions.produceMethodParameters = true;
		}

		return compilerOptions;
	}



	final SolutionProject solutionProject;
	final SetOfInt referenceIds = new SetOfInt();
	
	private final String bootclasspath;
	private final String releaseOutputPath;
	// 当前项目id;
	private final int assemblyId;
	public final String assemblyName;

	final Model model;
	final FileSpace fileSpace;
	final ErrorTable errorTable;
	final HighlighterCallback highlighterCallback;
	
	FileSystem environment;
	// 增量语义分析器实现以及增量编译器实现
	public final CompilationUnitDeclarationResolver2 resolver;
	
	
	// 项目
	public ProjectEnvironment(Model model, SolutionProject solutionProject, String bootclasspath) {
		this.model = model;
		this.fileSpace = model.fileSpace;
		this.errorTable = model.errorTable;
		this.highlighterCallback = model.highlighterCallback;
		
		this.solutionProject = solutionProject;
		this.bootclasspath = bootclasspath;

		this.assemblyId = solutionProject.assemblyId;
		this.assemblyName = solutionProject.assemblyName;

		this.releaseOutputPath = Assembly.getReleaseOutputPath(solutionProject.getAssembly());

		Set<String> classpaths = new HashSet<>();

		// 添加 bootclasspath
		classpaths.add(this.bootclasspath);
		// 添加 coreLambdaStubsJar
		classpaths.add(ProjectEnvironment.coreLambdaStubsJarPath);

		// 添加Jar依赖
		Set<SolutionProject> handleProjects = new HashSet<SolutionProject>();
		solutionProject.parserClassPath(handleProjects, classpaths);

		// 添加Module依赖Id
		handleProjects.clear();
		solutionProject.parserReferenceIds(handleProjects, referenceIds);

		// 添加源码路径
		EngineSolution engineSolution = model.getEngineSolution();
		if (engineSolution != null) {
			List<EngineSolutionProject> engineSolutionProjects = (List<EngineSolutionProject>)engineSolution.engineSolutionProjects;

			SetOfInt.Iterator default_Iterator = referenceIds.default_Iterator;
			default_Iterator.init();
			while (default_Iterator.hasMoreElements()) {
				int referenceId = default_Iterator.nextKey();
				EngineSolutionProject engineSolutionProject = engineSolutionProjects.get(referenceId);
				if (engineSolutionProject ==  null) {
					continue;
				}
				for (EngineSolution.File file : engineSolutionProject.fY) {
					if (!"Java".equals(EngineSolutionProject.getKind(file))) {
						continue;
					}
					// EngineSolution.File j6 -> WB
					String javaSrcDir = EngineSolutionProject.getPath(file);
					if (!TextUtils.isEmpty(javaSrcDir) &&
						new File(javaSrcDir).isDirectory()) {
						// AppLog.println_d(" 添加源码目录 -> %s ", javaSrcDir);
						classpaths.add(javaSrcDir);
					}
				}
			}
		}

		// 环境 
		environment = new FileSystem(classpaths.toArray(new String[classpaths.size()]) , null, "UTF-8");
		// 设置源码
		// environment.setSourceFiles(getSourceRootPaths(this, this.assemblyId));
		
		this.resolver = new CompilationUnitDeclarationResolver2(
			this,
			environment, 
			getHandlingPolicy(), 
			compilerOptions,
			getResolverRequestor(),
			getProblemFactory()
		);
	}


	public CompilationUnitDeclaration resolve3(SyntaxTree syntaxTree) {
		return resolve3(syntaxTree.getFile());
	}

	public CompilationUnitDeclaration resolve3(FileEntry fileEntry) {
		this.resolver.lookupEnvironment.reset();

		String pathString = fileEntry.getPathString();

		char[] data;
		try {
			data = IOUtils.readAllChars(fileEntry.getReader(), true);
		}
		catch ( Throwable e) {
			if (e instanceof Error) {
				throw (Error)e;
			}
			throw new Error(e);
		}
		CompilationUnitDeclaration result = this.resolver.resolve3(new CompilationUnit(data, pathString, "utf-8"));
		// 检查错误
		if (result == null || result.compilationResult == null) {
			AppLog.println_d("没有解析 %s ", pathString);
			return result;
		}
		return result;
	}
	
	public void compile(SyntaxTree syntaxTree) throws Throwable {
		
		FileEntry fileEntry = syntaxTree.getFile();
		if( this.fileSpace.isRJavaFileEntry(fileEntry) && !this.solutionProject.isMainModule){
			// 非MainModule的R不可编译
			return;
		}
		
		Language language = syntaxTree.getLanguage();
		
		if( !(language instanceof JavaLanguagePro)){
			return;
		}
		String pathString = fileEntry.getPathString();
		
		// 强制语义分析
		EclipseJavaCodeAnalyzer2 codeAnalyzer = ((JavaLanguagePro)language).getCodeAnalyzer();
		CompilationUnitDeclaration result = codeAnalyzer.semanticAnalysis(syntaxTree, true);
		
		// 生成代码
		result.generateCode();

		// 检查错误
		CompilationResult compilationResult = result.compilationResult;
		
		if (result == null 
			|| compilationResult == null) {
			AppLog.println_d("没有解析 %s ", pathString);
			AppLog.println_d("写入失败()");
			return;
		}
		
		boolean hasError = result.hasErrors();

		if (!hasError) {
			ClassFile[] classFiles = result.compilationResult.getClassFiles();
			writeClassFilesToDisk(fileEntry, classFiles, this.getReleaseOutputPath());
		}

	}
	
	public void compile3(SyntaxTree syntaxTree) throws Throwable {
		
		FileEntry fileEntry = syntaxTree.getFile();
		if( this.fileSpace.isRJavaFileEntry(fileEntry) && !this.solutionProject.isMainModule){
			// 非MainModule的R不可编译
			return;
		}
		
		this.resolver.lookupEnvironment.reset();

		String pathString = fileEntry.getPathString();

		char[] data;
		try {
			data = IOUtils.readAllChars(fileEntry.getReader(), true);
		}
		catch ( Throwable e) {
			if (e instanceof Error) {
				throw (Error)e;
			}
			throw new Error(e);
		}
		CompilationUnitDeclaration result = this.resolver.resolve3(new CompilationUnit(data, pathString, "utf-8"));

		// 生成代码
		result.generateCode();

		// 检查错误
		CompilationResult compilationResult = result.compilationResult;
		if (result == null 
			|| compilationResult == null) {
			AppLog.println_d("没有解析 %s ", pathString);
			AppLog.println_d("写入失败()");
			return;
		}
		boolean hasError = false;
		
		
		// clear
		// this.highlighterCallback.releaseSyntaxTree();
		
		CategorizedProblem[] problems = compilationResult.getAllProblems();
		int problemsLength = problems == null ? 0 : problems.length;

		for (int index = 0; index < problemsLength; index++) {
			CategorizedProblem rawProblem = problems[index];
			DefaultProblem problem = (DefaultProblem) rawProblem;
			if (problem.isError()) {
				// AppLog.d("JavaCodeAnalyzer:: ECJ 错误文件(" + syntaxTree.getFile().getPathString() + ")");
				hasError = true;
			}
		}
		// 完成
		// this.highlighterCallback.fileFinished(fileEntry);
		
		if (!hasError) {
			ClassFile[] classFiles = result.compilationResult.getClassFiles();
			writeClassFilesToDisk(fileEntry, classFiles, this.getReleaseOutputPath());
		}

	}

	private void writeClassFilesToDisk(FileEntry fileEntry, ClassFile[] classFiles, String currentDestinationPath) throws Throwable {

		for (ClassFile classFile : classFiles) {

			char[] filename = classFile.fileName();

			String packageName;
			String className = new String(filename);
			int indexOfPackageEnd = className.lastIndexOf('/');
			if (indexOfPackageEnd >= 0) {
				// 包含 /
				packageName = className.substring(0, indexOfPackageEnd + 1);
				className = className.substring(indexOfPackageEnd + 1);
			} else {
				packageName = "";
			}

			
			String releaseOutputPath = this.model.fileSpace.getReleaseOutputPath(fileEntry);
			
			// 强制更新
			File classCacheFile = new File(releaseOutputPath, packageName + className + ".class");
			classCacheFile.delete();
			
			OutputStream classFileOutput = null;
			BufferedOutputStream output = null;
			try {
				classFileOutput = this.model.j3.nw(fileEntry, packageName, className, true, false);
				
				output = new BufferedOutputStream(classFileOutput, 1024);
				// if no IOException occured, output cannot be null
				output.write(classFile.header, 0, classFile.headerOffset);
				output.write(classFile.contents, 0, classFile.contentsOffset);
				output.flush();
			}
			catch (Throwable e) {
				throw e;
			}
			finally {
				IOUtils.close(output);
				IOUtils.close(classFileOutput);
				
			}

			/*
			 char[] filename = classFile.fileName();
			 int length = filename.length;

			 char[] relativeName = new char[length + 6];
			 System.arraycopy(filename, 0, relativeName, 0, length);
			 System.arraycopy(SuffixConstants.SUFFIX_class, 0, relativeName, length, 6);
			 CharOperation.replace(relativeName, '/', File.separatorChar);

			 String relativeStringName = new String(relativeName);


			 org.eclipse.jdt.internal.compiler.util.Util.writeToDisk(
			 generateClasspathStructure,
			 currentDestinationPath,
			 relativeStringName,
			 classFile);
			 */
		}
	}

	public String getReleaseOutputPath() {
		return releaseOutputPath;
	}

	public static Set<String> getSourcePaths(ProjectEnvironment projectEnvironment, int rootAssemblyId) {
		FileSpace fileSpace = projectEnvironment.fileSpace;

		Set<String> sourcePaths = new HashSet<>();

		SetOfFileEntry solutionFiles = fileSpace.getSolutionFiles();
		SetOfFileEntry.Iterator solutionFilesIterator = solutionFiles.default_Iterator;
		solutionFilesIterator.init();
		while (solutionFilesIterator.hasMoreElements()) {
			FileEntry file = solutionFilesIterator.nextKey();
			int fileAssembly = fileSpace.getAssembly(file);

			if (!projectEnvironment.containsId(fileAssembly)) {
				continue;
			}
			// R必须是projectEnvironment的 assemblyId
			// 相对 projectEnvironment是 
			if (fileSpace.isRJavaFileEntry(file) 
				&& fileAssembly != rootAssemblyId) {
				continue;
			}


			String pathString = file.getPathString();
			String toLowerCase = pathString.toLowerCase();
			if (toLowerCase.endsWith(".java")) {
				sourcePaths.add(pathString);
			}
		}
		return sourcePaths;
	}

	public boolean containsId(int id) {
		return referenceIds.contains(id);
	}

	/*
	 * Answer the component to which will be handed back compilation results from the compiler
	 */
	public ICompilerRequestor getResolverRequestor() {
		return new ICompilerRequestor() {
			@Override
			public void acceptResult(CompilationResult compilationResult) {

			}
		};
	}

	public IErrorHandlingPolicy getHandlingPolicy() {

		// passes the initial set of files to the batch oracle (to avoid finding more than once the same units when case insensitive match)
		return new IErrorHandlingPolicy() {
			@Override
			public boolean proceedOnErrors() {
				return !true; // stop if there are some errors
			}
			@Override
			public boolean stopOnFirstError() {
				return false;
			}
			@Override
			public boolean ignoreAllErrors() {
				return false;
			}
		};
	}
	public IProblemFactory getProblemFactory() {
		return new DefaultProblemFactory();
	}

	/*
	 //	public FileSystem getLibraryAccess() {
	 //		FileSystem nameEnvironment = new FileSystem(
	 //			this.checkedClasspaths, 
	 //			this.filenames,
	 //			this.annotationsFromClasspath && CompilerOptions.ENABLED.equals(this.options.get(CompilerOptions.OPTION_AnnotationBasedNullAnalysis)),
	 //			this.limitedModules);
	 //			
	 //		nameEnvironment.module = this.module;
	 //		processAddonModuleOptions(nameEnvironment);
	 //		return nameEnvironment;
	 //	}
	 */

	public int getAssemblyId() {
		return assemblyId;
	}

	public String getAssemblyName() {
		return assemblyName;
	}


	public void reset() {

	}

	public static class ZeroAicyFileSystem extends FileSystem {
		// INameEnvironment
		INameEnvironment INameEnvironment;

		ClasspathSourceFiles classpathSourceFiles;
		public ZeroAicyFileSystem(String[] classpathNames, String[] initialFileNames, String encoding) {
			super(classpathNames, initialFileNames, encoding);
		}


		public void setSourceFiles(Set<String> sourcePaths) {
			classpathSourceFiles = new ClasspathSourceFiles(sourcePaths);
		}

		// INameEnvironment
		@Override
		public void cleanup() {
			super.cleanup();
		}

		public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {

			return findType(typeName, packageName);
		}

		@Override
		public NameEnvironmentAnswer findType(char[][] compoundTypeName) {

			return super.findType(compoundTypeName);
		}

		public boolean isPackage(char[][] parentPackageName, char[] packageName) {

			return super.isPackage(parentPackageName, packageName);
		}

	}


	public static class ClasspathSourceFiles {
		Set<String> sourcePaths;
		public ClasspathSourceFiles(Set<String> sourceDirPaths) {
			this.sourcePaths = sourceDirPaths;
		}

	}

	/**
	 * 
	 */
	public static class SolutionProject {

		final int assemblyId;
		final String assemblyName;
		final Assembly assembly;

		final String projectPath;
		
		final boolean isMainModule;
		final boolean isModule;
		final boolean isJar;
		final boolean isAar;

		final String releaseOutputPath;

		final Set<SolutionProject> projectReferences = new HashSet<>();
		
		public SolutionProject(int assemblyId, Assembly assembly, boolean isMainModule) {
			this.assemblyId = assemblyId;
			this.assembly = assembly;
			this.assemblyName = Assembly.VH(assembly);
			this.projectPath = Assembly.Zo(assembly);

			File projectFile = new File(projectPath);
			boolean isFile = projectFile.isFile();

			this.isAar = !isFile && projectPath.endsWith(".aar");
			this.isJar = isFile && projectPath.endsWith(".jar");
			// 非文件，aar，jar才是 gradle module
			this.isModule = !isAar && !isJar && !isFile;
			this.isMainModule = isMainModule;
			this.releaseOutputPath = Assembly.getReleaseOutputPath(assembly);

		}

		public void parserReferenceIds(Set<SolutionProject> handleProjects, SetOfInt referenceIds) {
			if (!isModule) {
				return;
			}
			referenceIds.put(this.assemblyId);

			// 已处理
			handleProjects.add(this);
			for (SolutionProject project : projectReferences) {
				// 防止jar aar 循环依赖
				if (handleProjects.contains(project)) {
					// 已处理
					continue;
				}
				project.parserReferenceIds(handleProjects, referenceIds);
			}
		}

		public void parserClassPath(Set<SolutionProject> handleProjects, Set<String> classpaths) {

			if (this.isJar) {
				classpaths.add(this.projectPath);
			}
			// 已处理
			handleProjects.add(this);

			for (SolutionProject project : projectReferences) {
				// 防止jar aar 循环依赖
				if (handleProjects.contains(project)) {
					// 已处理
					continue;
				}
				project.parserClassPath(handleProjects, classpaths);
			}
		}
		public void addProjectReferences(SolutionProject referencedProject) {
			// 此时 referencedProject也未填充完毕
			// 因此只能缓存起来
			this.projectReferences.add(referencedProject);
		}

		public int getAssemblyId() {
			return assemblyId;
		}

		public Assembly getAssembly() {
			return assembly;
		}
	}
}
