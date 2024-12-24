package com.aide.codemodel.language.java;

import android.util.SparseArray;
import com.aide.codemodel.api.ErrorTable;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.FileSpace;
import com.aide.codemodel.api.HighlighterType;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.callback.HighlighterCallback;
import com.aide.codemodel.api.collections.HashtableOfInt;
import com.aide.codemodel.api.collections.MapOfIntLong;
import io.github.zeroaicy.util.reflect.ReflectPie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblem;

public class EclipseJavaCodeAnalyzer2 extends JavaCodeAnalyzer {


	final JavaCodeModelPro javaCodeModel;

	final Model model;
	final JavaLanguage javaLanguage;
	final MapOfIntLong semanticParserVersionMap;

	HighlighterCallback highlighterCallback;
	ErrorTable errorTable;
	HashtableOfInt<ErrorTable.d> errors;
	FileSpace fileSpace;



	Map<String, List<ErrorInfo>> ecjSemanticAnalysisMap = new HashMap<>();
	// 语义高亮信息
	Map<String, List<HighlighterInfo>> ecjSemanticHighlighterMap = new HashMap<>();


	public EclipseJavaCodeAnalyzer2( JavaCodeModelPro codeModel, Model model, JavaLanguage javaLanguage ) {
		super(model, javaLanguage);

		this.javaCodeModel = codeModel;
		this.model = model;
		this.javaLanguage = javaLanguage;
		this.semanticParserVersionMap = new MapOfIntLong();

		if ( model == null ) {
			return;
		}

		this.highlighterCallback = model.highlighterCallback;

		this.errorTable = model.errorTable;

		ReflectPie errorTableReflectPie = ReflectPie.on(errorTable);
		// 反射
		this.errors = errorTableReflectPie.get("errors");

		this.fileSpace = model.fileSpace;
	}

	public void fillSemanticHighlighter( FileEntry file ) {
		HighlighterCallback highlighterCallback = this.highlighterCallback;
		if ( highlighterCallback == null ) {
			return;
		}
		List<HighlighterInfo> highlighterInfos = this.ecjSemanticHighlighterMap.get(file.getPathString());
		if ( highlighterInfos == null ) {
			return;
		}
		// 添加 扩展(ecj)高亮信息
		for ( HighlighterInfo highlighterInfo : highlighterInfos ) {
			highlighterCallback.found(highlighterInfo.highlighterType, highlighterInfo.startLine, highlighterInfo.startColumn, highlighterInfo.endLine, highlighterInfo.endColumn);
		}

	}

	/**
	 * 查找 ProjectEnvironment
	 */
	private ProjectEnvironment getProjectEnvironment( FileEntry file ) {
		int assemblyId = fileSpace.getAssembly(file);
		SparseArray<ProjectEnvironment> projectEnvironments = javaCodeModel.projectEnvironments;
		if ( projectEnvironments == null ) {
			return null;
		}
		ProjectEnvironment projectEnvironment = projectEnvironments.get(assemblyId);
		if ( projectEnvironment == null ) {
			return null;
		}
		return projectEnvironment;
	}

	/**
	 * 强制获得resolveUnit
	 */
	private CompilationUnitDeclaration forceResolveUnit( FileEntry file ) {
		ProjectEnvironment projectEnvironment = getProjectEnvironment(file);
		if ( projectEnvironment == null ) {
			return null;
		}
		// return resolve Unit
		return projectEnvironment.resolve3(file);
	}

	// resolve
	@Override
	public void v5( SyntaxTree syntaxTree ) {

		// 当前文件error count
		FileEntry fileEntry = syntaxTree.getFile();
		Language language = syntaxTree.getLanguage();

		// 根据文件版本原则更新解析
		int fileId = fileEntry.getId();
		// get
		long oldVersion = semanticParserVersionMap.v5(fileId);
		long nowVersion = fileEntry.getVersion();

		String filePath = fileEntry.getPathString();

		// 必须调用 codemodel需要符号表信息
		List<ErrorInfo> aideSemanticAnalysis = aideSemanticAnalysis(syntaxTree);


		if ( oldVersion == nowVersion ) {
			// 复用解析结果

			// 添加 ecj 从缓存中 
			List<ErrorInfo> ecjSemanticAnalysis = ecjSemanticAnalysisMap.get(filePath);
			// 添加 ecjSemanticAnalysis 
			addErrorInfo(ecjSemanticAnalysis, fileEntry, language);				

			// 添加 aideSemanticAnalysis 
			addErrorInfo(aideSemanticAnalysis, fileEntry, language);
		} else {
			// 更新版本 put
			semanticParserVersionMap.VH(fileId, nowVersion);

			// 使用 ProjectEnvironment 增量分析
			// 并保存结果以便复用

			// 解析

			// resolve 可能为null
			CompilationUnitDeclaration resolveUnit = forceResolveUnit(fileEntry);

			// 计算并缓存 ecj信息
			ecjSemanticAnalysis(resolveUnit, fileEntry, language);

			List<ErrorInfo> ecjSemanticAnalysis = ecjSemanticAnalysisMap.get(filePath);

			// 添加 ecjSemanticAnalysis 
			addErrorInfo(ecjSemanticAnalysis, fileEntry, language);				

			// 添加 aideSemanticAnalysis 
			addErrorInfo(aideSemanticAnalysis, fileEntry, language);

		}

		// AIDE的语义分析
	}


	SimpleHighlighterASTVisitor highlighterASTVisitor = new SimpleHighlighterASTVisitor();
	/**
	 * 计算ecj错误 警告信息并缓存 以及 高亮信息
	 */
	private void ecjSemanticAnalysis( CompilationUnitDeclaration resolveUnit, FileEntry fileEntry, Language language ) {
		String filePath = fileEntry.getPathString();
		
		List<ErrorInfo> ecjSemanticAnalysis = ecjSemanticAnalysisMap.get(filePath);
		if( ecjSemanticAnalysis == null ){
			ecjSemanticAnalysis = new ArrayList<>();
			// 缓存ecj语义分析信息
			this.ecjSemanticAnalysisMap.put(filePath, ecjSemanticAnalysis);
		}else{
			ecjSemanticAnalysis.clear();
		}
		
		if ( resolveUnit  == null ) {
			return;
		}
		
		List<HighlighterInfo> ecjSemanticHighlighter = new ArrayList<>();
		this.ecjSemanticHighlighterMap.put(filePath, ecjSemanticHighlighter);
		
		// 可以遍历Ast，提取高亮信息
		highlighterASTVisitor.init(resolveUnit, ecjSemanticHighlighter);
		resolveUnit.traverse(highlighterASTVisitor, resolveUnit.scope);
		
		CompilationResult compilationResult = resolveUnit.compilationResult;
		if( compilationResult == null ){
			return;
		}
		CategorizedProblem[] problems = compilationResult.getAllProblems();
		if ( problems == null ) {
			return;
		}

		for ( CategorizedProblem rawProblem : problems ) {
			DefaultProblem problem = (DefaultProblem) rawProblem;

			if ( !problem.isError() && !problem.isWarning() ) {
				continue;
			}

			int startLine = problem.getSourceLineNumber();
			int endLine = startLine;
			int startColumn = problem.column;
			int endColumn = ( problem.column + problem.getSourceEnd() - problem.getSourceStart() ) + 1;

			String msg = problem.getMessage();

			// 错误 | 警告
			int kind = problem.isError() ?
				20 : 49;

			ErrorInfo errorInfo = new ErrorInfo(fileEntry, language, startLine, startColumn, endLine, endColumn, msg, kind);
			ecjSemanticAnalysis.add(errorInfo);

			if ( problem.isWarning() ) {
				// 添加 
				switch ( problem.getID() ) {
					case IProblem.UnusedPrivateField:
					case IProblem.LocalVariableIsNeverUsed:
					case IProblem.ArgumentIsNeverUsed:
					case IProblem.ExceptionParameterIsNeverUsed:
					case IProblem.UnusedObjectAllocation:
						ecjSemanticHighlighter.add(new HighlighterInfo(HighlighterType.UnUsed, startLine, startColumn, endLine, endColumn));
						break;
					default:
						// this.highlighterCallback.found(HighlighterType.UnUsed, line, column, line, endColumn);
						break;
				}
			}
		}
	}

	// 计算AIDE 语义分析信息
	private List<ErrorInfo> aideSemanticAnalysis( SyntaxTree syntaxTree ) {
		super.v5(syntaxTree);
		//  保存AIDE语义分析的结果
		List<ErrorInfo> allErrorInfos = getAllErrors(syntaxTree);
		// 清除AIDE语义分析器错误
		clearErrors(syntaxTree);
		return allErrorInfos;
	}


	private void addErrorInfo( List<ErrorInfo> semanticAnalysis, FileEntry fileEntry, Language language ) {
		if ( semanticAnalysis == null ) {
			return;
		}
		for ( ErrorInfo errorInfo : semanticAnalysis ) {
			// Hw会 put compileErrors里导致 编译器不调用
			errorTable.lg(errorInfo.file, errorInfo.language, errorInfo.startLine, errorInfo.startColumn, errorInfo.endLine, errorInfo.endColumn, errorInfo.msg, errorInfo.kind);

			ErrorTable.d fileEntryErrorPack = getFileEntryErrorPack(fileEntry, language);
			if ( fileEntryErrorPack == null ) {
				continue;
			}
			Vector<ErrorTable.Error> analysisErrors = fileEntryErrorPack.analysisErrors;
			if ( analysisErrors == null ) {
				continue;
			}
			analysisErrors.get(analysisErrors.size() - 1).fixes = errorInfo.fixes;
		}
	}




	@Deprecated
	private List<ErrorInfo> getErrorInfos( SyntaxTree syntaxTree ) {
		try {
			FileEntry fileEntry = syntaxTree.getFile();
			Language language = syntaxTree.getLanguage();

			int count = errorTable.SI(fileEntry, language);
			List<ErrorInfo> errorInfos = new ArrayList<>();


			for ( int index = 0; index < count;index++ ) {
				ErrorTable.Error error = getError(fileEntry, language, index);

				int kind = error.kind;

				// 静态方法
				if ( kind == 300 ) {
					continue;
				}

				int startLine = error.startLine;
				int startColumn = error.startColumn;

				int endLine = error.endLine;
				int endColumn = error.endColumn;

				String msg = error.msg;
				Vector<ErrorTable.Fix> fixes = error.fixes;

				ErrorInfo errorInfo = new ErrorInfo(fileEntry, language, startLine, startColumn, endLine, endColumn, msg, kind);
				errorInfo.fixes = fixes;
				errorInfos.add(errorInfo);

			}
			return errorInfos;
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}



	private void clearErrors( SyntaxTree syntaxTree ) {
		FileEntry fileEntry = syntaxTree.getFile();
		Language language = syntaxTree.getLanguage();
		this.errorTable.clearNonParserErrors(syntaxTree.getFile(), syntaxTree.getLanguage());
		ErrorTable.d fileEntryErrorPack = getFileEntryErrorPack(fileEntry, language);
		if ( fileEntryErrorPack != null ) fileEntryErrorPack.parseErrors.clear();
	}

	private List<ErrorInfo> getAllErrors( SyntaxTree syntaxTree ) {

		FileEntry fileEntry = syntaxTree.getFile();
		Language language = syntaxTree.getLanguage();

		int count = errorTable.SI(fileEntry, syntaxTree.getLanguage());

		List<ErrorInfo> errorInfos = new ArrayList<>();

		for ( int index = 0; index < count;index++ ) {
			ErrorTable.Error error = getError(fileEntry, language, index);

			int startLine = error.startLine;
			int startColumn = error.startColumn;

			int endLine = error.endLine;
			int endColumn = error.endColumn;

			String msg = error.msg;

			int kind = error.kind;

			Vector<ErrorTable.Fix> fixes = error.fixes;

			// 静态方法
			if ( kind == 300 ) {
				ErrorInfo errorInfo = new ErrorInfo(fileEntry, language, startLine, startColumn, endLine, endColumn, msg);
				errorInfos.add(errorInfo);
			}  else {
				// kind == 50 会由Lcom/aide/engine/Engine$DaemonTask;::XL(I)
				// 转为 112
				// 49 103
				ErrorInfo errorInfo = new ErrorInfo(fileEntry, language, startLine, startColumn, endLine, endColumn, msg, 50);
				errorInfo.fixes = fixes;
				errorInfos.add(errorInfo);
			}
		}

		return errorInfos;
	}



	public static class ErrorInfo {
		public FileEntry file;
		public Language language;
		public int startLine;
		public int startColumn;
		public int endLine;
		public int endColumn;
		public String msg;

		public int kind;

		public Vector<ErrorTable.Fix> fixes;


		public ErrorInfo( FileEntry file, Language language, int startLine, int startColumn, int endLine, int endColumn, String msg ) {
			this.file = file;
			this.language = language;
			this.startLine = startLine;
			this.startColumn = startColumn;
			this.endLine = endLine;
			this.endColumn = endColumn;
			this.msg = msg;
			this.kind = 300;
		}
		public ErrorInfo( FileEntry file, Language language, int startLine, int startColumn, int endLine, int endColumn, String msg, int kind ) {
			this.file = file;
			this.language = language;
			this.startLine = startLine;
			this.startColumn = startColumn;
			this.endLine = endLine;
			this.endColumn = endColumn;
			this.msg = msg;
			this.kind = kind;
		}

		@Override
		public String toString( ) {
			return String.format(" %s -> %s", msg, file.getPathString());
		}
	}

	public ErrorTable.Error getError( FileEntry fileEntry, Language language, int i ) {
		ErrorTable.d d = getFileEntryErrorPack(fileEntry, language);
		int size = d.parseErrors.size();

		if ( i >= size ) {
			Vector<ErrorTable.Error> analysisErrors = d.analysisErrors;
			return analysisErrors.elementAt(i - size);
		}
		Vector<ErrorTable.Error> parseErrors = d.parseErrors;
		return parseErrors.elementAt(i);
    }

	private ErrorTable.d getFileEntryErrorPack( FileEntry fileEntry, Language language ) {
		int fileEntryLanguageId = this.model.fileSpace.getFileEntryLanguageId(fileEntry, language);
		ErrorTable.d d = this.errors.get(fileEntryLanguageId);
		return d;
	}

	// 将高亮信息缓存起来
	public static class HighlighterInfo {
		public final int highlighterType;
		public final int startLine;
		public final int startColumn;
		public final int endLine;
		public final int endColumn;

		public HighlighterInfo( int highlighterType, int startLine, int startColumn, int endLine, int endColumn ) {
			this.highlighterType = highlighterType;
			this.startLine = startLine;
			this.startColumn = startColumn;
			this.endLine = endLine;
			this.endColumn = endColumn;
		}
	}


	public void v52( SyntaxTree syntaxTree ) {

		// AIDE的语义分析
		super.v5(syntaxTree);
		//  保存AIDE语义分析的结果
		List<ErrorInfo> aideSemanticAnalysis = getAllErrors(syntaxTree);
		// 清除AIDE语义分析器错误
		clearErrors(syntaxTree);

		// 当前文件error count
		FileEntry fileEntry = syntaxTree.getFile();
		Language language = syntaxTree.getLanguage();

		// 根据文件版本原则更新解析
		int fileId = fileEntry.getId();
		// get
		long oldVersion = semanticParserVersionMap.v5(fileId);
		long nowVersion = fileEntry.getVersion();

		String pathString = fileEntry.getPathString();

		if ( oldVersion == nowVersion ) {
			// 使用缓存的错误信息
			List<ErrorInfo> errorInfosCache = this.ecjSemanticAnalysisMap.get(pathString);
			if ( errorInfosCache == null ) {
				return;
			}
			// ecj生成的错误信息
			addErrorInfo(errorInfosCache, fileEntry, language);

			// AIDE的错误信息
			addErrorInfo(aideSemanticAnalysis, fileEntry, language);


			return;
		}

		// 更新
		semanticParserVersionMap.VH(fileId, nowVersion);

		// 解析
		ProjectEnvironment projectEnvironment = getProjectEnvironment(fileEntry);

		CompilationUnitDeclaration resolve3 = projectEnvironment.resolve3(syntaxTree);
		CompilationResult compilationResult = resolve3.compilationResult;

		// 缓存

		// 缓存 ecj生成的错误信息
		List<ErrorInfo> errorInfosCache = getErrorInfos(syntaxTree);
		ecjSemanticAnalysisMap.put(pathString, errorInfosCache);

		// AIDE的错误信息 和 Java项目的入口类信息
		addErrorInfo(aideSemanticAnalysis, fileEntry, language);
	}
}
