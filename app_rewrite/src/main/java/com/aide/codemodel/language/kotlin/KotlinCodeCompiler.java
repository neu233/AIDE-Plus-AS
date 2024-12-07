package com.aide.codemodel.language.kotlin;
import com.aide.codemodel.api.FileSpace;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.collections.OrderedMapOfIntInt;
import com.aide.codemodel.api.collections.SetOfInt;
import com.aide.common.AppLog;
import io.github.zeroaicy.util.reflect.ReflectPie;
import java.util.HashMap;
import java.util.List;
import com.aide.codemodel.api.FileSpace.Assembly;
import java.util.ArrayList;
import com.aide.codemodel.api.collections.SetOfFileEntry;
import com.aide.codemodel.api.collections.SetOfFileEntry.Iterator;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.collections.FunctionOfIntInt;
import java.util.Set;
import java.util.HashSet;
//import io.github.aide.kotlin.KotlinK2JVMCompile;

public class KotlinCodeCompiler implements com.aide.codemodel.api.abstraction.CodeCompiler {

	private String mainProjectPath;

	/**
	 * Kt编译需要什么?
	 * 1. javaSourceRoots || kotlinSourceRoots
	 *     AIDE中两者相同 source root找不到，.java .kt文件找得到
	 *
	 * 2. kotlin-stdlib 通过maven仓库缓存获取
	 *     或者有依赖有 kotlin-stdlib-xxx的库 ✔️
	 * 3. class依赖库  ✔️
	 * 4. 增量编译缓存目录 -> 主项目✔️
	 * 
	 */


	@Override
	public void init(CodeModel codeModel) {
		if( true ) return;
		if (!(codeModel instanceof KotlinCodeModel)) {
			return;
		}
		KotlinCodeModel kotlinCodeModel = (KotlinCodeModel)codeModel;

		Model model = kotlinCodeModel.model;
		if (model == null) return;

		FileSpace fileSpace = model.fileSpace;
		ReflectPie fileSpaceReflect = ReflectPie.on(fileSpace);


		/*
		 FunctionOfIntInt fileAssembles = fileSpaceReflect.get("fileAssembles");

		SetOfFileEntry registeredSolutionFiles = fileSpaceReflect.get("registeredSolutionFiles");
		SetOfFileEntry.Iterator registeredSolutionFilesIterator = registeredSolutionFiles.default_Iterator;
		registeredSolutionFilesIterator.init();
		while (registeredSolutionFilesIterator.hasMoreElements()) {
			FileEntry fileEntry = registeredSolutionFilesIterator.nextKey();
			int projectAssemblyId = fileAssembles.get(fileEntry.getId());
			AppLog.println_d("path: %s -> assemblyId: %s ", fileEntry.getPathString(), projectAssemblyId);

		}
		*/

		HashMap<Integer, FileSpace.Assembly> assemblyMap = fileSpaceReflect.get("assemblyMap");

		OrderedMapOfIntInt assemblyReferences = fileSpaceReflect.get("assemblyReferences");
		// AppLog.println_d("assemblyReferences -> %s",  assemblyReferences);

		// OrderedMapOfIntInt允许多个相同的key
		// 应该是 int int 对
		OrderedMapOfIntInt.Iterator default_Iterator = assemblyReferences.default_Iterator;

		// 被依赖的assemblyId
		int mainProjectAssemblyId = findMainProjectAssemblyId(default_Iterator, assemblyMap);
		if (mainProjectAssemblyId < 0) {
			AppLog.println_e("KotlinCodeCompiler找不到主项目");
			return;
		}

		Assembly mainProjectAssembly = assemblyMap.get(mainProjectAssemblyId);
		this.mainProjectPath = Assembly.Zo(mainProjectAssembly);

		default_Iterator.init();

		// 项目依赖映射 都是 项目路径 不是其内部目录依赖
		HashMap<String, Set<Integer>> referenceAssemblysMap = new HashMap<>();
		// 遍历所有 SolutionProject的 AssemblyId
		while (default_Iterator.hasMoreElements()) {
			int projectAssemblyId = default_Iterator.nextKey();
			int referencedProjectAssembly = default_Iterator.nextValue();

			// 自己会依赖自己，排除
			if (projectAssemblyId == referencedProjectAssembly) {
				continue;
			}

			Assembly projectAssembly =  assemblyMap.get(projectAssemblyId);

			String projectPath = Assembly.Zo(projectAssembly);
			
			Set<Integer> references = referenceAssemblysMap.get(projectPath);
			if (references == null) {
				references = new HashSet<>();
				referenceAssemblysMap.put(projectPath, references);
			}

			// Assembly referenceProjectAssembly =  assemblyMap.get(projectAssemblyId);
			// String referenceProjectPath = Assembly.Zo(referenceProjectAssembly);
			references.add(projectAssemblyId);

		}
		// 需要查找出 project的源码目录 // 不需要子依赖
		// 

	}


	private int findMainProjectAssemblyId(OrderedMapOfIntInt.Iterator default_Iterator, HashMap<Integer, FileSpace.Assembly> assemblyMap) {
		SetOfInt referencedSet = new SetOfInt();
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
			AppLog.println_d("主项目AssemblyId: ", assemblyId);

			return assemblyId;
		}
		return -1;
	}

	private Language language;
	private final Model model;

	public KotlinCodeCompiler(Model model, Language language) {
		this.model = model;
		this.language = language;
	}


	@Override
	public void compile(List<SyntaxTree> list, boolean p) {
		//List<File> files = new ArrayList<>();
		for (SyntaxTree syntaxTree : list) {
			if (syntaxTree.getLanguage() == this.language) {
				try {
					String pathString = syntaxTree.getFile().getPathString();
					System.out.println(pathString);
					//files.add(new File(pathString));
				}
				catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}
		/*
		 try {
		 KotlinK2JVMCompile kotlinK2JVMCompile = KotlinK2JVMCompile.getINSTANCE();
		 kotlinK2JVMCompile.setKotlinHomeDir(new File("/data/user/0/io.github.zeroaicy.aide/files"));
		 kotlinK2JVMCompile.addProjectSrcDir(files);
		 kotlinK2JVMCompile.run();
		 }
		 catch (Exception e) {
		 e.printStackTrace();
		 return;
		 }
		 //*/
	}
}
