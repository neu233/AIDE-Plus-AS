package com.aide.codemodel.language.java17;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.CodeAnalyzer;
import com.aide.codemodel.api.abstraction.SignatureAnalyzer;
import com.aide.codemodel.api.abstraction.Syntax;
import com.aide.codemodel.api.abstraction.Tools;
import com.aide.codemodel.api.abstraction.TypeSystem;
import com.aide.codemodel.api.abstraction.CodeRenderer;
import java.util.Set;
import com.aide.codemodel.api.abstraction.FormatOption;
import com.aide.codemodel.api.Model;

public class Java17Language implements Language {
	
	public Java17Language(Model model){
		
	}
	
	@Override
	public CodeAnalyzer getCodeAnalyzer() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public CodeRenderer getCodeRenderer() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public Set<? extends FormatOption> getFormatOptionSet() {
		return null;
	}

	@Override
	public String getName() {
		return "Java";
	}

	@Override
	public SignatureAnalyzer getSignatureAnalyzer() {
		return null;
	}

	@Override
	public Syntax getSyntax() {
		return null;
	}

	@Override
	public Tools getTools() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public TypeSystem getTypeSystem() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public boolean isParenChar(char c) {
		return c == '(' || c == '[' || c == '{' || c == '}' || c == ':' || c == ';';
	}
	
	
	/******************************no support******************************************/
	@Override
	public void shrink() {
		
	}
	
}
