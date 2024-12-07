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

public class ZeroAicyModel extends AIDEModel {

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
