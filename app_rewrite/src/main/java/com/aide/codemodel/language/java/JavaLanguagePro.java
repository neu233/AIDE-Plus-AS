package com.aide.codemodel.language.java;

import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.abstraction.CodeRenderer;
import com.aide.codemodel.api.abstraction.Syntax;

public class JavaLanguagePro extends JavaLanguage {

	private JavaSyntax javaSyntax;

	private JavaSignatureAnalyzer javaSignatureAnalyzer;

	@Override
	public CodeRenderer getCodeRenderer() {
		return we();
	}

	JavaCodeAnalyzer eclipseJavaCodeAnalyzer;

	JavaCodeModelPro javaCodeModelPro;
	public JavaLanguagePro(Model model, JavaCodeModelPro javaCodeModelPro) {
		super(model);
		this.javaCodeModelPro = javaCodeModelPro;
		if (model == null) {
			return;
		}
		this.eclipseJavaCodeAnalyzer = 
			//*
			new EclipseJavaCodeAnalyzer2(javaCodeModelPro, model, this);
		/*/
		 new JavaCodeAnalyzer(model, this);
		 //*/

		this.javaSyntax = new JavaSyntax(model.identifierSpace);
		this.javaSignatureAnalyzer = new JavaSignatureAnalyzer(model, this);
	}

	@Override
	public JavaSignatureAnalyzer getSignatureAnalyzer() {
		return this.javaSignatureAnalyzer;
	}

	@Override
	public JavaCodeAnalyzer getCodeAnalyzer() {
		return this.eclipseJavaCodeAnalyzer;
	}

	@Override
	public JavaSyntax getSyntax() {
		return this.javaSyntax;
	}


}
