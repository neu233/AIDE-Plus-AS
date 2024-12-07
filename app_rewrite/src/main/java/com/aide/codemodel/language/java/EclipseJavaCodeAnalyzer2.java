package com.aide.codemodel.language.java;

import android.util.SparseArray;
import com.aide.codemodel.api.ErrorTable;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.FileSpace;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.collections.HashtableOfInt;
import com.aide.codemodel.api.collections.MapOfIntLong;
import io.github.zeroaicy.util.reflect.ReflectPie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.aide.codemodel.api.ErrorTable.d;

public class EclipseJavaCodeAnalyzer2 extends JavaCodeAnalyzer {
	JavaCodeModelPro javaCodeModel;
	Model model;
	JavaLanguage javaLanguage;

	ErrorTable errorTable;
	HashtableOfInt<ErrorTable.d> errors;

	FileSpace fileSpace;

	final MapOfIntLong map;
	Map<String, List<ErrorInfo>> errorInfosMap = new HashMap<>();

	public EclipseJavaCodeAnalyzer2(JavaCodeModelPro codeModel, Model model, JavaLanguage javaLanguage) {
		super(model, javaLanguage);
		this.javaCodeModel = codeModel;
		this.model = model;
		this.javaLanguage = javaLanguage;
		this.map = new MapOfIntLong();

		if (model == null) {
			return;
		}
		this.errorTable = model.errorTable;
		ReflectPie errorTableReflectPie = ReflectPie.on(errorTable);
		this.errors = errorTableReflectPie.get("errors");

		this.fileSpace = model.fileSpace;
	}

	// resolve
	@Override
	public void v5(SyntaxTree syntaxTree) {

		super.v5(syntaxTree);

		List<ErrorInfo> allErrorInfos = getAllErrors(syntaxTree);
		
		// 清除AIDE语义分析器错误
		clearErrors(syntaxTree);

		// 优先添加 确保Java入口类添加
		// 从allErrorInfos清除并添加 Java项目入口类
		/*for (int i = allErrorInfos.size() -1 ; i >= 0; i--) {
			ErrorInfo errorInfo = allErrorInfos.get(i);
			if(errorInfo.kind == 300){
				errorTable.lg(errorInfo.file, errorInfo.language, errorInfo.startLine, errorInfo.startColumn, errorInfo.endLine, errorInfo.endColumn, errorInfo.msg, errorInfo.kind);
				allErrorInfos.remove(errorInfo);
			}
		}*/
		

		// 当前文件error count
		FileEntry fileEntry = syntaxTree.getFile();
		Language language = syntaxTree.getLanguage();

		// 根据文件版本原则更新解析
		int fileId = fileEntry.getId();
		// get
		long oldVersion = map.v5(fileId);
		long nowVersion = fileEntry.getVersion();
		String pathString = fileEntry.getPathString();

		if (oldVersion == nowVersion) {
			// 使用缓存的错误信息
			List<ErrorInfo> errorInfosCache = this.errorInfosMap.get(pathString);
			if (errorInfosCache == null) {
				return;
			}
			// ecj生成的错误信息
			addErrorInfo(errorInfosCache, fileEntry, language);
			
			// AIDE的错误信息
			addErrorInfo(allErrorInfos, fileEntry, language);

			return;
		}

		// 更新
		map.VH(fileId, nowVersion);

		// 解析
		ProjectEnvironment projectEnvironment = getProjectEnvironment(fileEntry);
		projectEnvironment.resolve3(syntaxTree);
		// 缓存 ecj生成的错误信息
		List<ErrorInfo> errorInfosCache = getErrorInfos(syntaxTree);
		errorInfosMap.put(pathString, errorInfosCache);
		
		// AIDE的错误信息 和 Java项目的入口类信息
		addErrorInfo(allErrorInfos, fileEntry, language);

		
	}

	private void addErrorInfo(List<ErrorInfo> errorInfosCache, FileEntry fileEntry, Language language) {
		for (ErrorInfo errorInfo : errorInfosCache) {
			// Hw会 put compileErrors里导致 编译器不调用
			errorTable.lg(errorInfo.file, errorInfo.language, errorInfo.startLine, errorInfo.startColumn, errorInfo.endLine, errorInfo.endColumn, errorInfo.msg, errorInfo.kind);

			ErrorTable.d fileEntryErrorPack = getFileEntryErrorPack(fileEntry, language);
			if (fileEntryErrorPack == null) {
				continue;
			}
			Vector<ErrorTable.Error> analysisErrors = fileEntryErrorPack.analysisErrors;
			if (analysisErrors == null) {
				continue;
			}
			analysisErrors.get(analysisErrors.size() - 1).fixes = errorInfo.fixes;
		}
	}


	private ProjectEnvironment getProjectEnvironment(FileEntry file) {
		int assemblyId = fileSpace.getAssembly(file);
		SparseArray<ProjectEnvironment> projectEnvironments = javaCodeModel.projectEnvironments;
		if (projectEnvironments == null) {
			return null;
		}
		ProjectEnvironment projectEnvironment = projectEnvironments.get(assemblyId);
		if (projectEnvironment == null) {
			return null;
		}
		return projectEnvironment;
	}

	private List<ErrorInfo> getErrorInfos(SyntaxTree syntaxTree) {
		try {
			FileEntry fileEntry = syntaxTree.getFile();
			Language language = syntaxTree.getLanguage();

			int count = errorTable.SI(fileEntry, language);
			List<ErrorInfo> errorInfos = new ArrayList<>();


			for (int index = 0; index < count;index++) {
				ErrorTable.Error error = getError(fileEntry, language, index);

				int kind = error.kind;

				// 静态方法
				if (kind == 300) {
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


	
	private void clearErrors(SyntaxTree syntaxTree) {
		FileEntry fileEntry = syntaxTree.getFile();
		Language language = syntaxTree.getLanguage();
		this.errorTable.clearNonParserErrors(syntaxTree.getFile(), syntaxTree.getLanguage());
		ErrorTable.d fileEntryErrorPack = getFileEntryErrorPack(fileEntry, language);
		if( fileEntryErrorPack != null ) fileEntryErrorPack.parseErrors.clear();
	}
	
	private List<ErrorInfo> getAllErrors(SyntaxTree syntaxTree) {
		
		FileEntry fileEntry = syntaxTree.getFile();
		Language language = syntaxTree.getLanguage();

		int count = errorTable.SI(fileEntry, syntaxTree.getLanguage());
		List<ErrorInfo> errorInfos = new ArrayList<>();
		
		for (int index = 0; index < count;index++) {
			ErrorTable.Error error = getError(fileEntry, language, index);

			int startLine = error.startLine;
			int startColumn = error.startColumn;

			int endLine = error.endLine;
			int endColumn = error.endColumn;

			String msg = error.msg;

			int kind = error.kind;

			Vector<ErrorTable.Fix> fixes = error.fixes;

			// 静态方法
			if (kind == 300) {
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


		public ErrorInfo(FileEntry file, Language language, int startLine, int startColumn, int endLine, int endColumn, String msg) {
			this.file = file;
			this.language = language;
			this.startLine = startLine;
			this.startColumn = startColumn;
			this.endLine = endLine;
			this.endColumn = endColumn;
			this.msg = msg;
			this.kind = 300;
		}
		public ErrorInfo(FileEntry file, Language language, int startLine, int startColumn, int endLine, int endColumn, String msg, int kind) {
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
		public String toString() {
			return String.format(" %s -> %s", msg, file.getPathString());
		}
	}

	public ErrorTable.Error getError(FileEntry fileEntry, Language language, int i) {
		ErrorTable.d d = getFileEntryErrorPack(fileEntry, language);
		int size = d.parseErrors.size();

		if (i >= size) {
			Vector<ErrorTable.Error> analysisErrors = d.analysisErrors;
			return analysisErrors.elementAt(i - size);
		}
		Vector<ErrorTable.Error> parseErrors = d.parseErrors;
		return parseErrors.elementAt(i);
    }

	private ErrorTable.d getFileEntryErrorPack(FileEntry fileEntry, Language language) {
		int fileEntryLanguageId = this.model.fileSpace.getFileEntryLanguageId(fileEntry, language);
		ErrorTable.d d = this.errors.get(fileEntryLanguageId);
		return d;
	}
}
