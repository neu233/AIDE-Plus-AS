package com.aide.codemodel.language.java;


import android.util.SparseArray;
import com.aide.codemodel.api.ErrorTable;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.FileSpace;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.collections.SetOfFileEntry;
import com.aide.common.AppLog;
import io.github.zeroaicy.util.IOUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblem;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;

@Deprecated
public class EclipseJavaCodeAnalyzer extends JavaCodeAnalyzer {


	Set<String> sourcePaths;
	@Override
	public void v5(SyntaxTree syntaxTree) {
		super.v5(syntaxTree);

		// if (!true) return;

		//*
		try {
			ErrorTable errorTable = _model.errorTable;
			FileEntry fileEntry = syntaxTree.getFile();
			// error count
			int count = errorTable.SI(fileEntry, syntaxTree.getLanguage());
			List<Main> mains = new ArrayList<>();

			for (int index = 0; index < count;index++) {
				FileEntry file = syntaxTree.getFile();
				Language language = syntaxTree.getLanguage();

				int startLine = errorTable.getErrorStartLine(file, language, index);
				int startColumn = errorTable.getErrorStartColumn(file, language, index);

				int endLine = errorTable.getErrorEndLine(file, language, index);
				int endColumn = errorTable.getErrorEndColumn(file, language, index);

				String msg = errorTable.getErrorText(file, language, index);

				int kind = errorTable.getErrorKind(file, language, index);

				// 静态方法
				if (kind == 300) {
					mains.add(new Main(file, language, startLine, startColumn, endLine, endColumn, msg));
				}  else {
					// System.out.println(String.format("kind %s，msg: %s，filepath: %s", kind, msg, file.getPathString()));
				}
			}

			_model.errorTable.clearNonParserErrors(syntaxTree.getFile(), syntaxTree.getLanguage());

			for (Main main : mains) {
				// Hw会 put compileErrors里导致 编译器不调用
				errorTable.lg(main.file, main.language, main.startLine, main.startColumn, main.endLine, main.endColumn, main.msg, 300);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		//*/

		// 会导致没有Java main[([String) 入口方法
		// _model.errorTable.clearNonParserErrors(syntaxTree.getFile(), syntaxTree.getLanguage());

		// resolve2
		try {
			ErrorTable errorTable = _model.errorTable;
			FileSpace fileSpace = _model.fileSpace;
			FileEntry file = syntaxTree.getFile();
			int assemblyId = fileSpace.getAssembly(file);
			SparseArray<ProjectEnvironment> projectEnvironments = javaCodeModelPro.projectEnvironments;
			if (projectEnvironments == null) {
				return;
			}
			ProjectEnvironment projectEnvironment = projectEnvironments.get(assemblyId);
			if (projectEnvironment == null) {
				return;
			}

			CompilationUnitDeclarationResolver2 resolver = projectEnvironment.resolver;

			/*if( sourcePaths == null || sourcePaths.isEmpty()){
			 sourcePaths = method(fileSpace, projectEnvironment, assemblyId);

			 }*/
			// sourcePaths.remove(syntaxTree.getFile());



			FileEntry fileEntry2 = syntaxTree.getFile();
			String pathString = fileEntry2.getPathString();
			CompilationUnitDeclaration result = resolver.resolve9999(pathString);
			if (result == null 
				|| result.compilationResult == null) {
				AppLog.println_d("没有解析 %s ", pathString);
				return;
			}
			CategorizedProblem[] problems = result.compilationResult.getAllProblems();

			if (problems == null) {
				return;
			}
			for (CategorizedProblem rawProblem : problems) {
				DefaultProblem problem = (DefaultProblem) rawProblem;
				int line = problem.getSourceLineNumber();
				int column = problem.column;
				int endColumn = (problem.column + problem.getSourceEnd() - problem.getSourceStart()) + 1;

				String msg = problem.getMessage();
				if (problem.isError()) {
					// AppLog.d("JavaCodeAnalyzer:: ECJ 错误文件(" + syntaxTree.getFile().getPathString() + ")");
					errorTable.Hw(syntaxTree.getFile(), syntaxTree.getLanguage(), line, column, line, endColumn, msg, 20);
				} else if (problem.isWarning()) {
					// AppLog.d("JavaCodeAnalyzer:: ECJ 警告文件(" + syntaxTree.getFile().getPathString() + ")");
					errorTable.addSemanticWarning(syntaxTree.getFile(), syntaxTree.getLanguage(), line, column, line, endColumn, msg, 49);
				}

				// AppLog.d("JavaCodeAnalyzer:: ECJ 位置(" + line + "," + column + "," + line + "," + endColumn + ")");
				// AppLog.d("JavaCodeAnalyzer:: ECJ 信息 " + msg);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

    private Model _model;
	JavaLanguagePro javaLanguagePro;
    JavaCodeModelPro javaCodeModelPro;

	public EclipseJavaCodeAnalyzer(Model _model, JavaCodeModelPro javaCodeModelPro,  JavaLanguagePro javaLanguagePro) {
        super(_model, javaLanguagePro);
        this._model = _model;
		this.javaLanguagePro = javaLanguagePro;
		this.javaCodeModelPro = javaCodeModelPro;
    }

    private boolean isJava8(SyntaxTree syntaxTree) {
        return true;
    }

//	public void v5_4(SyntaxTree syntaxTree) {
//		super.v5(syntaxTree);
//
//		ErrorTable errorTable = _model.errorTable;
//
//		// AppLog.d("JavaCodeAnalyzer:: analyzeErrors(" + syntaxTree.getFile().getPathString() + ")");
//
//		int index = 0;
//		FileEntry fileEntry = syntaxTree.getFile();
//		// error count
//		int count = errorTable.SI(fileEntry, syntaxTree.getLanguage());
//		List<Main> mains = new ArrayList<>();
//		while (index < count) {
//			int startLine = errorTable.getErrorStartLine(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
//			int startColumn = errorTable.getErrorStartColumn(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
//			int endLine = errorTable.getErrorEndLine(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
//			int endColumn = errorTable.getErrorEndColumn(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
//			String msg = errorTable.getErrorText(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
//			int kind = errorTable.getErrorKind(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
//
//			if (kind == 300) {
//				// AppLog.d("JavaCodeAnalyzer:: 找到 静态方法 " + msg + " 在文件 " + syntaxTree.getFile().getPathString());
//				// AppLog.d("JavaCodeAnalyzer:: 位置(" + startLine + "," + startColumn + "," + endLine + "," + endColumn + ")");
//				mains.add(new Main(syntaxTree.getFile(), syntaxTree.getLanguage(), startLine, startColumn, endLine, endColumn, msg));
//			} else {
//				// AppLog.d("JavaCodeAnalyzer:: 错误文件(" + syntaxTree.getFile().getPathString() + ")");
//				// AppLog.d("JavaCodeAnalyzer:: 位置(" + startLine + "," + startColumn + "," + endLine + "," + endColumn + ")");
//				// AppLog.d("JavaCodeAnalyzer:: 类型 " + kind);
//				// AppLog.d("JavaCodeAnalyzer:: 信息 " + msg);
//			}
//			index++;
//		}
//
//		//errorTable.DW(syntaxTree.getFile(), syntaxTree.getLanguage());
//		_model.errorTable.clearNonParserErrors(syntaxTree.getFile(), syntaxTree.getLanguage());
//		for (Main main : mains) {
//			errorTable.Hw(main.file, main.language, main.startLine, main.startColumn, main.endLine, main.endColumn, main.msg, 300);
//		}
//
//		int assemblyId = _model.fileSpace.getAssembly(fileEntry);
//
//		CompilationUnitDeclarationResolver resolver = this.javaLanguagePro.javaCodeModelPro.projectEnvironments.get(assemblyId).resolver;
//		CompilationUnitDeclaration result = resolver.resolve(fileEntry.getPathString());
//
//		CategorizedProblem[] problems = result.compilationResult.getAllProblems();
//
//		if (problems == null)
//			problems = new CategorizedProblem[0];
//
//		for (CategorizedProblem rawProblem : problems) {
//			DefaultProblem problem = (DefaultProblem) rawProblem;
//			int line = problem.getSourceLineNumber();
//			int column = problem.column;
//			int endColumn = (problem.column + problem.getSourceEnd() - problem.getSourceStart()) + 1;
//
//			String msg = problem.getMessage();
//			if (problem.isError()) {
//				// AppLog.d("JavaCodeAnalyzer:: ECJ 错误文件(" + syntaxTree.getFile().getPathString() + ")");
//				errorTable.Hw(syntaxTree.getFile(), syntaxTree.getLanguage(), line, column, line, endColumn, msg, 20);
//			} else {
//				// AppLog.d("JavaCodeAnalyzer:: ECJ 警告文件(" + syntaxTree.getFile().getPathString() + ")");
//				errorTable.Hw(syntaxTree.getFile(), syntaxTree.getLanguage(), line, column, line, endColumn, msg, 49);
//			}
//
//			// AppLog.d("JavaCodeAnalyzer:: ECJ 位置(" + line + "," + column + "," + line + "," + endColumn + ")");
//			// AppLog.d("JavaCodeAnalyzer:: ECJ 信息 " + msg);
//		}
//	}


	public void v5_111(SyntaxTree syntaxTree) {
		super.v5(syntaxTree);

		ErrorTable errorTable = _model.errorTable;

		// AppLog.d("JavaCodeAnalyzer:: analyzeErrors(" + syntaxTree.getFile().getPathString() + ")");
//		int index = 0;
//		int count = errorTable.SI(syntaxTree.getFile(), syntaxTree.getLanguage());
//
//		List<Main> mains = new ArrayList<>();
//		while (index < count) {
//			FileEntry file = syntaxTree.getFile();
//			int startLine = errorTable.getErrorStartLine(file, syntaxTree.getLanguage(), index);
//			int startColumn = errorTable.getErrorStartColumn(file, syntaxTree.getLanguage(), index);
//			int endLine = errorTable.getErrorEndLine(file, syntaxTree.getLanguage(), index);
//			int endColumn = errorTable.getErrorEndColumn(file, syntaxTree.getLanguage(), index);
//			String msg = errorTable.getErrorText(file, syntaxTree.getLanguage(), index);
//			int kind = errorTable.getErrorKind(file, syntaxTree.getLanguage(), index);
//			
//			System.out.println( String.format("kind %s，msg: %s，filepath: %s", kind, msg, file.getPathString()) );
//			if (kind == 300) {
//				// AppLog.d("JavaCodeAnalyzer:: 找到 静态方法 " + msg + " 在文件 " + syntaxTree.getFile().getPathString());
//				// AppLog.d("JavaCodeAnalyzer:: 位置(" + startLine + "," + startColumn + "," + endLine + "," + endColumn + ")");
//				mains.add(new Main(syntaxTree.getFile(), syntaxTree.getLanguage(), startLine, startColumn, endLine, endColumn, msg));
//			} else {
//				AppLog.d("JavaCodeAnalyzer:: 错误文件(" + syntaxTree.getFile().getPathString() + ")");
//				AppLog.d("JavaCodeAnalyzer:: 位置(" + startLine + "," + startColumn + "," + endLine + "," + endColumn + ")");
//				AppLog.d("JavaCodeAnalyzer:: 类型 " + kind);
//				AppLog.d("JavaCodeAnalyzer:: 信息 " + msg);
//			}
//			index++;
//		}
//
//		//errorTable.DW(syntaxTree.getFile(), syntaxTree.getLanguage());
//		_model.errorTable.clearNonParserErrors(syntaxTree.getFile(), syntaxTree.getLanguage());
//
//		for (Main main : mains) {
//			System.out.println(main);
//			errorTable.Hw(main.file, main.language, main.startLine, main.startColumn, main.endLine, main.endColumn, main.msg, 300);
//		}

//		try {
//			_model.errorTable.clearNonParserErrors(syntaxTree.getFile(), syntaxTree.getLanguage());
//
//			FileSpace fileSpace = _model.fileSpace;
//			FileEntry file = syntaxTree.getFile();
//			int assemblyId = fileSpace.getAssembly(file);
//			ProjectEnvironment projectEnvironment = this.javaLanguagePro.javaCodeModelPro.projectEnvironments.get(assemblyId);
//			CompilationUnitDeclarationResolver resolver = projectEnvironment.resolver;
//			if (sourcePaths == null || sourcePaths.isEmpty()) {
//				sourcePaths = method(fileSpace, projectEnvironment, assemblyId);
//
//			}
//			// sourcePaths.remove(syntaxTree.getFile());
//
//
//
//			String pathString = syntaxTree.getFile().getPathString();
//			CompilationUnitDeclaration result = resolver.updateSourceFile(sourcePaths, pathString, null);
//			if (result == null || result.compilationResult == null) {
//				AppLog.println_d("没有解析 %s ", pathString);
//				return;
//			}
//			CategorizedProblem[] problems = result.compilationResult.getAllProblems();
//
//			if (problems == null) {
//				return;
//			}
//			for (CategorizedProblem rawProblem : problems) {
//				DefaultProblem problem = (DefaultProblem) rawProblem;
//				int line = problem.getSourceLineNumber();
//				int column = problem.column;
//				int endColumn = (problem.column + problem.getSourceEnd() - problem.getSourceStart()) + 1;
//
//				String msg = problem.getMessage();
//				if (problem.isError()) {
//					// AppLog.d("JavaCodeAnalyzer:: ECJ 错误文件(" + syntaxTree.getFile().getPathString() + ")");
//					errorTable.Hw(syntaxTree.getFile(), syntaxTree.getLanguage(), line, column, line, endColumn, msg, 20);
//				} else {
//					// AppLog.d("JavaCodeAnalyzer:: ECJ 警告文件(" + syntaxTree.getFile().getPathString() + ")");
//					// errorTable.Hw(syntaxTree.getFile(), syntaxTree.getLanguage(), line, column, line, endColumn, msg, 49);
//				}
//
//				// AppLog.d("JavaCodeAnalyzer:: ECJ 位置(" + line + "," + column + "," + line + "," + endColumn + ")");
//				// AppLog.d("JavaCodeAnalyzer:: ECJ 信息 " + msg);
//			}
//
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}

    }




	static class Main {

		public FileEntry file;
		public Language language;
		public int startLine;
		public int startColumn;
		public int endLine;
		public int endColumn;
		public String msg;

		public Main(FileEntry file, Language language, int startLine, int startColumn, int endLine, int endColumn, String msg) {
			this.file = file;
			this.language = language;
			this.startLine = startLine;
			this.startColumn = startColumn;
			this.endLine = endLine;
			this.endColumn = endColumn;
			this.msg = msg;
		}

		@Override
		public String toString() {
			return String.format(" %s -> %s", msg, file.getPathString());
		}

	}

	//@Override
//	public void v5_3(SyntaxTree syntaxTree) {
//		super.v5(syntaxTree);
//
//		ErrorTable errorTable = _model.errorTable;
//
//		// AppLog.d("JavaCodeAnalyzer:: analyzeErrors(" + syntaxTree.getFile().getPathString() + ")");
//		int index = 0;
//		FileEntry file = syntaxTree.getFile();
//		int count = errorTable.SI(file, syntaxTree.getLanguage());
//
//		List<Main> mains = new ArrayList<>();
//		while (index < count) {
//			int startLine = errorTable.getErrorStartLine(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
//			int startColumn = errorTable.getErrorStartColumn(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
//			int endLine = errorTable.getErrorEndLine(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
//			int endColumn = errorTable.getErrorEndColumn(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
//			String msg = errorTable.getErrorText(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
//			int kind = errorTable.getErrorKind(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
//
//			if (kind == 300) {
//				// AppLog.d("JavaCodeAnalyzer:: 找到 静态方法 " + msg + " 在文件 " + syntaxTree.getFile().getPathString());
//				// AppLog.d("JavaCodeAnalyzer:: 位置(" + startLine + "," + startColumn + "," + endLine + "," + endColumn + ")");
//				mains.add(new Main(syntaxTree.getFile(), syntaxTree.getLanguage(), startLine, startColumn, endLine, endColumn, msg));
//			} else {
//				// AppLog.d("JavaCodeAnalyzer:: 错误文件(" + syntaxTree.getFile().getPathString() + ")");
//				// AppLog.d("JavaCodeAnalyzer:: 位置(" + startLine + "," + startColumn + "," + endLine + "," + endColumn + ")");
//				// AppLog.d("JavaCodeAnalyzer:: 类型 " + kind);
//				// AppLog.d("JavaCodeAnalyzer:: 信息 " + msg);
//			}
//			index++;
//		}
//
//		//errorTable.DW(syntaxTree.getFile(), syntaxTree.getLanguage());
//		_model.errorTable.clearNonParserErrors(syntaxTree.getFile(), syntaxTree.getLanguage());
//		for (Main main : mains) {
//			errorTable.Hw(main.file, main.language, main.startLine, main.startColumn, main.endLine, main.endColumn, main.msg, 300);
//		}
//
//
//		int assemblyId = _model.fileSpace.getAssembly(file);
//
//		CompilationUnitDeclarationResolver resolver = this.javaLanguagePro.javaCodeModelPro.projectEnvironments.get(assemblyId).resolver;
//		CompilationUnitDeclaration result = resolver.resolve(file.getPathString());
//
//		CategorizedProblem[] problems = result.compilationResult.getAllProblems();
//
//		if (problems == null)
//			problems = new CategorizedProblem[0];
//
//		for (CategorizedProblem rawProblem : problems) {
//			DefaultProblem problem = (DefaultProblem) rawProblem;
//			int line = problem.getSourceLineNumber();
//			int column = problem.column;
//			int endColumn = (problem.column + problem.getSourceEnd() - problem.getSourceStart()) + 1;
//
//			String msg = problem.getMessage();
//			if (problem.isError()) {
//				AppLog.d("JavaCodeAnalyzer:: ECJ 错误文件(" + syntaxTree.getFile().getPathString() + ")");
//				errorTable.Hw(syntaxTree.getFile(), syntaxTree.getLanguage(), line, column, line, endColumn, msg, 20);
//			} else {
//				AppLog.d("JavaCodeAnalyzer:: ECJ 警告文件(" + syntaxTree.getFile().getPathString() + ")");
//				errorTable.Hw(syntaxTree.getFile(), syntaxTree.getLanguage(), line, column, line, endColumn, msg, 49);
//			}
//
//			AppLog.d("JavaCodeAnalyzer:: ECJ 位置(" + line + "," + column + "," + line + "," + endColumn + ")");
//			AppLog.d("JavaCodeAnalyzer:: ECJ 信息 " + msg);
//		}
//	}




    public void v5_2(SyntaxTree syntaxTree) {

        if (isJava8(syntaxTree)) {
            super.v5(syntaxTree);

			ErrorTable errorTable = _model.errorTable;

            AppLog.d("JavaCodeAnalyzer:: analyzeErrors(" + syntaxTree.getFile().getPathString() + ")");
            int index = 0;
            int count = errorTable.SI(syntaxTree.getFile(), syntaxTree.getLanguage());

			List<Main> mains = new ArrayList<>();
            while (index < count) {
                int startLine = errorTable.getErrorStartLine(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
                int startColumn = errorTable.getErrorStartColumn(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
                int endLine = errorTable.getErrorEndLine(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
                int endColumn = errorTable.getErrorEndColumn(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
                String msg = errorTable.getErrorText(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
                int kind = errorTable.getErrorKind(syntaxTree.getFile(), syntaxTree.getLanguage(), index);

                if (kind == 300) {
                    AppLog.d("JavaCodeAnalyzer:: 找到 静态方法 " + msg + " 在文件 " + syntaxTree.getFile().getPathString());
                    AppLog.d("JavaCodeAnalyzer:: 位置(" + startLine + "," + startColumn + "," + endLine + "," + endColumn + ")");
                    mains.add(new Main(syntaxTree.getFile(), syntaxTree.getLanguage(), startLine, startColumn, endLine, endColumn, msg));
                } else {
                    AppLog.d("JavaCodeAnalyzer:: 错误文件(" + syntaxTree.getFile().getPathString() + ")");
                    AppLog.d("JavaCodeAnalyzer:: 位置(" + startLine + "," + startColumn + "," + endLine + "," + endColumn + ")");
                    AppLog.d("JavaCodeAnalyzer:: 类型 " + kind);
                    AppLog.d("JavaCodeAnalyzer:: 信息 " + msg);
                }
                index++;
            }

            //errorTable.DW(syntaxTree.getFile(), syntaxTree.getLanguage());
            _model.errorTable.clearNonParserErrors(syntaxTree.getFile(), syntaxTree.getLanguage());
            for (Main main : mains) {
                errorTable.Hw(main.file, main.language, main.startLine, main.startColumn, main.endLine, main.endColumn, main.msg, 300);
            }

            try {
                CompilerOptions compilerOptions = new CompilerOptions();
                compilerOptions.sourceLevel = ClassFileConstants.JDK17;
                compilerOptions.complianceLevel = ClassFileConstants.JDK17;
                compilerOptions.originalComplianceLevel = ClassFileConstants.JDK17;
                compilerOptions.originalSourceLevel = ClassFileConstants.JDK17;

                compilerOptions.reportUnavoidableGenericTypeProblems = true;
                compilerOptions.reportUnusedParameterWhenImplementingAbstract = true;
                compilerOptions.reportUnusedParameterWhenOverridingConcrete = true;
                compilerOptions.reportUnusedParameterIncludeDocCommentReference = true;
                compilerOptions.reportUnusedDeclaredThrownExceptionWhenOverriding = true;
                compilerOptions.reportUnusedDeclaredThrownExceptionIncludeDocCommentReference = true;
                compilerOptions.reportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable = true;
                compilerOptions.reportDeprecationInsideDeprecatedCode = true;
                compilerOptions.preserveAllLocalVariables = true;
                compilerOptions.produceReferenceInfo = true;
                compilerOptions.pessimisticNullAnalysisForFreeTypeVariablesEnabled = true;
                compilerOptions.enableSyntacticNullAnalysisForFields = true;
                compilerOptions.complainOnUninternedIdentityComparison = true;

                compilerOptions.reportDeprecationWhenOverridingDeprecatedMethod = true;
                compilerOptions.reportMissingOverrideAnnotationForInterfaceMethodImplementation = true;
                compilerOptions.reportMissingEnumCaseDespiteDefault = true;
                compilerOptions.reportSpecialParameterHidingField = true;
                compilerOptions.reportDeadCodeInTrivialIfStatement = true;
                compilerOptions.reportUnlikelyCollectionMethodArgumentTypeStrict = true;
                compilerOptions.suppressOptionalErrors = true;
                compilerOptions.suppressWarnings = true;
                compilerOptions.analyseResourceLeaks = true;
                compilerOptions.performMethodsFullRecovery = true;
                compilerOptions.performStatementsRecovery = true;

                char[] contents = IOUtils.readAllChars(syntaxTree.getFile().getReader(), true);

                ICompilationUnit compilationUnit = new CompilationUnit(contents, name(syntaxTree.getFile().getPathString()), "UTF-8");
                CompilationResult compilationResult = new CompilationResult(compilationUnit, 0, 0, 100);

                Set<String> sourcePaths = new HashSet<>();
                HashSet<String> classPaths = new HashSet<>();
                HashSet<String> platformClassPaths = new HashSet<>();

				// coreLambdaStubsJarPath
				platformClassPaths.add(Project.coreLambdaStubsJarPath);
				sourcePaths.add(syntaxTree.getFile().getPathString());

                SetOfFileEntry solutionFiles = _model.fileSpace.getSolutionFiles();
				SetOfFileEntry.Iterator solutionFilesIterator = solutionFiles.default_Iterator;
				solutionFilesIterator.init();

				while (solutionFilesIterator.hasMoreElements()) {
                    FileEntry file = solutionFilesIterator.nextKey();
                    boolean iSyntaxTreerchiveEntry = file.isArchiveEntry();
                    boolean iSyntaxTreerchive = file.isArchive();
                    FileEntry parent = file.getParentArchive();

                    if (iSyntaxTreerchive || iSyntaxTreerchiveEntry) {
                        if (parent != null) {
                            String archivePath = parent.getPathString();
                            if (archivePath.endsWith("android.jar") ||
								archivePath.endsWith("rt.jar")) {
                                if (!platformClassPaths.contains(archivePath)) {
                                    platformClassPaths.add(archivePath);
                                    AppLog.d("JavaCompiler:: 添加平台类路径从 " + archivePath);
                                }
                            } else {
                                if (archivePath.endsWith(".jar") 
									&& !classPaths.contains(archivePath)) {
                                    classPaths.add(archivePath);
                                    AppLog.d("JavaCompiler:: 添加类路径从 " + archivePath);
                                }
                            }
                        }
                    } else {
                        String pathString = file.getPathString();
						String toLowerCase = pathString.toLowerCase();
						if (toLowerCase.endsWith(".java")) {
                            sourcePaths.add(pathString);
							//AppLog.d("JavaCompiler:: 添加源文件从 " + sourceParent);
                        } else if (toLowerCase.endsWith(".jar")) {
							classPaths.add(file.getPathString());
							// AppLog.d("JavaCompiler:: 添加外部类路径从 " + file.getPathString());
                        }
                    }
                }

                List<String> classpathNames = new ArrayList<>();
                classpathNames.addAll(platformClassPaths);
                classpathNames.addAll(classPaths);

				FileSystem environment = new FileSystem(classpathNames.toArray(new String[classpathNames.size()]) , sourcePaths.toArray(new String[classpathNames.size()]), "UTF-8");

                org.eclipse.jdt.internal.compiler.Compiler compiler = new org.eclipse.jdt.internal.compiler.Compiler(environment, new IErrorHandlingPolicy() {
						@Override
						public boolean proceedOnErrors() {
							return true;
						}

						@Override
						public boolean stopOnFirstError() {
							return false;
						}

						@Override
						public boolean ignoreAllErrors() {
							return false;
						}
					}, compilerOptions, new ICompilerRequestor() {
						@Override
						public void acceptResult(CompilationResult compilationResult) {

						}
					}, new DefaultProblemFactory());

                CompilationUnitDeclaration result = compiler.resolve(compilationResult.compilationUnit, true, true, false);
                CategorizedProblem[] problems = result.compilationResult.getAllProblems();

				if (problems == null)
                    problems = new CategorizedProblem[0];

                for (CategorizedProblem rawProblem : problems) {
                    DefaultProblem problem = (DefaultProblem) rawProblem;
                    int line = problem.getSourceLineNumber();
                    int column = problem.column;
                    int endColumn = (problem.column + problem.getSourceEnd() - problem.getSourceStart()) + 1;

                    String msg = problem.getMessage();
                    if (problem.isError()) {
                        AppLog.d("JavaCodeAnalyzer:: ECJ 错误文件(" + syntaxTree.getFile().getPathString() + ")");
                        errorTable.Hw(syntaxTree.getFile(), syntaxTree.getLanguage(), line, column, line, endColumn, msg, 20);
                    } else {
                        AppLog.d("JavaCodeAnalyzer:: ECJ 警告文件(" + syntaxTree.getFile().getPathString() + ")");
                        errorTable.Hw(syntaxTree.getFile(), syntaxTree.getLanguage(), line, column, line, endColumn, msg, 49);
                    }

                    AppLog.d("JavaCodeAnalyzer:: ECJ 位置(" + line + "," + column + "," + line + "," + endColumn + ")");
                    AppLog.d("JavaCodeAnalyzer:: ECJ 信息 " + msg);
                }
            }
			catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            super.v5(syntaxTree);
        }
    }

    public static String name(String filePath) {
        int index = filePath.lastIndexOf(File.separator);
        return index < 0 ? filePath : filePath.substring(index + 1);
    }



	public void v5_1(SyntaxTree syntaxTree) {

        if (isJava8(syntaxTree)) {
            super.v5(syntaxTree);

			ErrorTable errorTable = _model.errorTable;

            AppLog.d("JavaCodeAnalyzer:: analyzeErrors(" + syntaxTree.getFile().getPathString() + ")");
            int index = 0;
            int count = errorTable.SI(syntaxTree.getFile(), syntaxTree.getLanguage());

			List<Main> mains = new ArrayList<>();
            while (index < count) {
                int startLine = errorTable.getErrorStartLine(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
                int startColumn = errorTable.getErrorStartColumn(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
                int endLine = errorTable.getErrorEndLine(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
                int endColumn = errorTable.getErrorEndColumn(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
                String msg = errorTable.getErrorText(syntaxTree.getFile(), syntaxTree.getLanguage(), index);
                int kind = errorTable.getErrorKind(syntaxTree.getFile(), syntaxTree.getLanguage(), index);

                if (kind == 300) {
                    AppLog.d("JavaCodeAnalyzer:: 找到 静态方法 " + msg + " 在文件 " + syntaxTree.getFile().getPathString());
                    AppLog.d("JavaCodeAnalyzer:: 位置(" + startLine + "," + startColumn + "," + endLine + "," + endColumn + ")");
                    mains.add(new Main(syntaxTree.getFile(), syntaxTree.getLanguage(), startLine, startColumn, endLine, endColumn, msg));
                } else {
                    AppLog.d("JavaCodeAnalyzer:: 错误文件(" + syntaxTree.getFile().getPathString() + ")");
                    AppLog.d("JavaCodeAnalyzer:: 位置(" + startLine + "," + startColumn + "," + endLine + "," + endColumn + ")");
                    AppLog.d("JavaCodeAnalyzer:: 类型 " + kind);
                    AppLog.d("JavaCodeAnalyzer:: 信息 " + msg);
                }
                index++;
            }

            //errorTable.DW(syntaxTree.getFile(), syntaxTree.getLanguage());
            _model.errorTable.clearNonParserErrors(syntaxTree.getFile(), syntaxTree.getLanguage());
            for (Main main : mains) {
                errorTable.Hw(main.file, main.language, main.startLine, main.startColumn, main.endLine, main.endColumn, main.msg, 300);
            }

            try {
                CompilerOptions compilerOptions = new CompilerOptions();
                compilerOptions.sourceLevel = ClassFileConstants.JDK17;
                compilerOptions.complianceLevel = ClassFileConstants.JDK17;
                compilerOptions.originalComplianceLevel = ClassFileConstants.JDK17;
                compilerOptions.originalSourceLevel = ClassFileConstants.JDK17;


                sourcePaths.add(syntaxTree.getFile().getPathString());
				char[] contents = IOUtils.readAllChars(syntaxTree.getFile().getReader(), true);
                ICompilationUnit compilationUnit = new CompilationUnit(contents, name(syntaxTree.getFile().getPathString()), "UTF-8");
                CompilationResult compilationResult = new CompilationResult(compilationUnit, 0, 0, 100);

                HashSet<String> classPaths = new HashSet<>();
                HashSet<String> platformClassPaths = new HashSet<>();
				// coreLambdaStubsJarPath
				platformClassPaths.add(Project.coreLambdaStubsJarPath);

                SetOfFileEntry solutionFiles = _model.fileSpace.getSolutionFiles();
				SetOfFileEntry.Iterator solutionFilesIterator = solutionFiles.default_Iterator;
				solutionFilesIterator.init();

				while (solutionFilesIterator.hasMoreElements()) {
                    FileEntry file = solutionFilesIterator.nextKey();
                    boolean iSyntaxTreerchiveEntry = file.isArchiveEntry();
                    boolean iSyntaxTreerchive = file.isArchive();
                    FileEntry parent = file.getParentArchive();

                    if (iSyntaxTreerchive || iSyntaxTreerchiveEntry) {
                        if (parent != null) {
                            String archivePath = parent.getPathString();
                            if (archivePath.endsWith("android.jar") ||
								archivePath.endsWith("rt.jar")) {
                                if (!platformClassPaths.contains(archivePath)) {
                                    platformClassPaths.add(archivePath);
                                    // AppLog.d("JavaCompiler:: 添加平台类路径从 " + archivePath);
                                }
                            } else {
                                if (archivePath.endsWith(".jar") && !classPaths.contains(archivePath)) {
                                    classPaths.add(archivePath);
                                    // AppLog.d("JavaCompiler:: 添加类路径从 " + archivePath);
                                }
                            }
                        }
                    } else {
                        if (file.getPathString().toLowerCase().endsWith(".java")) {
                            String sourceParent = file.getPathString();
							if (!sourcePaths.contains(sourceParent)) {
                                sourcePaths.add(sourceParent);
								// AppLog.d("JavaCompiler:: 添加源文件从 " + sourceParent);
							}
                        } else if (file.getPathString().toLowerCase().endsWith(".jar")) {
                            if (!classPaths.contains(file.getPathString())) {
                                classPaths.add(file.getPathString());
                                AppLog.d("JavaCompiler:: 添加外部类路径从 " + file.getPathString());
                            }
                        }
                    }
                }

                List<String> classpathNames = new ArrayList<>();
                classpathNames.addAll(platformClassPaths);
                classpathNames.addAll(classPaths);

				/*for (String path : sourcePaths) {
				 classpathNames.add(path);
				 }*/


				FileSystem environment = new FileSystem(classpathNames.toArray(new String[0]) , sourcePaths.toArray(new String[0]), "UTF-8");

                org.eclipse.jdt.internal.compiler.Compiler compiler = new org.eclipse.jdt.internal.compiler.Compiler(environment, new IErrorHandlingPolicy() {
						@Override
						public boolean proceedOnErrors() {
							return true;
						}

						@Override
						public boolean stopOnFirstError() {
							return false;
						}

						@Override
						public boolean ignoreAllErrors() {
							return false;
						}
					}, compilerOptions, new ICompilerRequestor() {
						@Override
						public void acceptResult(CompilationResult compilationResult) {

						}
					}, new DefaultProblemFactory());

				resolveSources(sourcePaths);
                CompilationUnitDeclaration result = compiler.resolve(compilationResult.compilationUnit, true, true, false);

                CategorizedProblem[] problems = result.compilationResult.getAllProblems();

				if (problems == null)
                    problems = new CategorizedProblem[0];

                for (CategorizedProblem rawProblem : problems) {
                    DefaultProblem problem = (DefaultProblem) rawProblem;
                    int line = problem.getSourceLineNumber();
                    int column = problem.column;
                    int endColumn = (problem.column + problem.getSourceEnd() - problem.getSourceStart()) + 1;

                    String msg = problem.getMessage();
                    if (problem.isError()) {
                        // AppLog.d("JavaCodeAnalyzer:: ECJ 错误文件(" + syntaxTree.getFile().getPathString() + ")");
                        errorTable.Hw(syntaxTree.getFile(), syntaxTree.getLanguage(), line, column, line, endColumn, msg, 20);
                    } else {
                        // AppLog.d("JavaCodeAnalyzer:: ECJ 警告文件(" + syntaxTree.getFile().getPathString() + ")");
                        errorTable.addSemanticWarning(syntaxTree.getFile(), syntaxTree.getLanguage(), line, column, line, endColumn, msg, 49);
                    }

                    // AppLog.d("JavaCodeAnalyzer:: ECJ 位置(" + line + "," + column + "," + line + "," + endColumn + ")");
                    // AppLog.d("JavaCodeAnalyzer:: ECJ 信息 " + msg);
                }
            }
			catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            super.v5(syntaxTree);
        }
    }

	private void resolveSources(Set<String> sourcePaths) {
		// TODO: Implement this method
	}

}
