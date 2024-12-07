package com.aide.codemodel.language.java;

import com.aide.codemodel.api.IdentifierSpace;

public class JavaSyntaxPro extends JavaSyntax {
	public JavaSyntaxPro(IdentifierSpace identifierSpace) {
		super(identifierSpace);
	}

	@Override
	public String getString(int syntaxTag) {
		if( syntaxTag == 247){
			return "LAMBDA_EXPRESSION";
		}
		return super.getString(syntaxTag);
	}
	
	
}
