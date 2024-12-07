package com.aide.codemodel.language.smali;
import com.aide.codemodel.api.EntitySpace;
import com.aide.codemodel.api.ErrorTable;
import com.aide.codemodel.api.IdentifierSpace;
import com.aide.codemodel.api.Parser;
import com.aide.codemodel.api.abstraction.Syntax;
import com.aide.codemodel.language.java.JavaSyntax;

public class SmaliParser extends Parser {
	public SmaliParser(IdentifierSpace identifierSpace, ErrorTable errorTable, EntitySpace entitySpace, JavaSyntax syntax) {
		super(identifierSpace, errorTable, entitySpace, syntax, 233, 0);
	}
	
	@Override
	public void parser() {
		
	}

}
