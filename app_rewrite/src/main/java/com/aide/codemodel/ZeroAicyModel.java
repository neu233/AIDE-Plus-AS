package com.aide.codemodel;

import abcd.q2;
import abcd.x2;
import abcd.y2;
import com.aide.codemodel.api.BomReaderFactory;
import com.aide.codemodel.api.callback.APISearcherCallback;
import com.aide.codemodel.api.callback.CodeCompleterCallback;
import com.aide.codemodel.api.callback.CodeMetricsCallback;
import com.aide.codemodel.api.callback.DebugMetadataCallback;
import com.aide.codemodel.api.callback.HighlighterCallback;
import com.aide.codemodel.api.callback.OpenFileCallback;
import com.aide.codemodel.api.callback.RefactoringCallback;
import com.aide.codemodel.api.callback.StopCallback;
import com.aide.codemodel.api.callback.StructureCallback;
import com.aide.codemodel.api.callback.SymbolSearcherCallback;
import com.aide.codemodel.api.callback.TemplateEvaluatorCallback;
import com.aide.codemodel.api.callback.UsageSearcherCallback;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.language.java.JavaCodeModelPro;
import com.aide.engine.EngineSolution;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.SyntaxTreeStyles;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.common.AppLog;

public class ZeroAicyModel extends AIDEModel {

	public static class HighlighterCallback2 implements HighlighterCallback {
		HighlighterCallback highlighterCallback;

		public HighlighterCallback2( HighlighterCallback highlighterCallback ) {
			this.highlighterCallback = highlighterCallback;
		}

		@Override
		public void addSyntaxTreeStyles( Language language, SyntaxTreeStyles syntaxTreeStyles ) {
			this.highlighterCallback.addSyntaxTreeStyles(language, syntaxTreeStyles);
			AppLog.e(new Throwable());

		}

		@Override
		public void delegateFound( Language language, int startLine, int startColumn, int endLine, int endColumn ) {
			this.highlighterCallback.delegateFound(language, startLine, startColumn, endLine, endColumn);
			AppLog.e(new Throwable());

		}

		@Override
		public void fileFinished( FileEntry fileEntry ) {
			this.highlighterCallback.fileFinished(fileEntry);

			AppLog.e(new Throwable());
		}

		@Override
		public void identifierFound( Language language, int startLine, int startColumn, int endLine, int endColumn ) {
			AppLog.e(new Throwable());

		}

		@Override
		public void j6( ) {
			this.highlighterCallback.j6();
			AppLog.e(new Throwable());

		}

		@Override
		public void keywordFound( Language language, int startLine, int startColumn, int endLine, int endColumn ) {
			this.highlighterCallback.keywordFound(language, startLine, startColumn, endLine, endColumn);
			AppLog.e(new Throwable());

		}

		@Override
		public void namespaceFound( Language language, int startLine, int startColumn, int endLine, int endColumn ) {
			this.highlighterCallback.namespaceFound(language, startLine, startColumn, endLine, endColumn);
			AppLog.e(new Throwable());

		}

		@Override
		public void releaseSyntaxTree( ) {
			this.highlighterCallback.releaseSyntaxTree();
			AppLog.e(new Throwable());

		}

		@Override
		public void typeFound( Language language, int startLine, int startColumn, int endLine, int endColumn ) {
			this.highlighterCallback.typeFound(language, startLine, startColumn, endLine, endColumn);
			AppLog.e(new Throwable());

		}

		@Override
		public void unifedLineFound( FileEntry fileEntry, int p ) {
			this.highlighterCallback.unifedLineFound(fileEntry, p);
			AppLog.e(new Throwable());

		}

		@Override
		public void found( int type, int startLine, int startColumn, int endLine, int endColumn ) {
			this.highlighterCallback.found(type, startLine, startColumn, endLine, endColumn);
			AppLog.e(new Throwable());
			
		}

	}

	public ZeroAicyModel(
		OpenFileCallback openFileCallback, 
		StopCallback stopCallback, 
		StructureCallback structureCallback, 
		HighlighterCallback highlighterCallback, 
		SymbolSearcherCallback symbolSearcherCallback, 
		CodeCompleterCallback codeCompleterCallback, 
		RefactoringCallback refactoringCallback, 
		UsageSearcherCallback usageSearcherCallback, 
		CodeMetricsCallback codeMetricsCallback, 
		APISearcherCallback aPISearcherCallback, 
		DebugMetadataCallback debugMetadataCallback, 
		TemplateEvaluatorCallback templateEvaluatorCallback, y2 y2Var, q2 q2Var, x2 x2Var, BomReaderFactory bomReaderFactory ) {
		//super(structureCallback, highlighterCallback,);
		// ReflectPie.on(this).set("errorTable", new ErrorTablePro(this));

		super(openFileCallback, stopCallback, structureCallback, highlighterCallback, symbolSearcherCallback, codeCompleterCallback, refactoringCallback, usageSearcherCallback, codeMetricsCallback, aPISearcherCallback, debugMetadataCallback, templateEvaluatorCallback, y2Var, q2Var, x2Var, bomReaderFactory);

	}


	// configure
	@Override
	public void J0( ) {
		for ( CodeModel codeModel : getCodeModels() ) {
			if ( codeModel instanceof JavaCodeModelPro ) {
				JavaCodeModelPro javaCodeModelPro = (JavaCodeModelPro)codeModel;
				javaCodeModelPro.reset();
			}
		}
		super.J0();
	}

	@Override
	public void setEngineSolution( EngineSolution engineSolution ) {
		super.setEngineSolution(engineSolution);

	}
}
