package com.aide.codemodel.language.smali;

import com.aide.codemodel.HighlighterSyntax;
import com.aide.codemodel.api.abstraction.CodeAnalyzer;
import com.aide.codemodel.api.abstraction.CodeRenderer;
import com.aide.codemodel.api.abstraction.FormatOption;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.SignatureAnalyzer;
import com.aide.codemodel.api.abstraction.Syntax;
import com.aide.codemodel.api.abstraction.Tools;
import com.aide.codemodel.api.abstraction.TypeSystem;
import java.util.Set;

public class SmaliLanguage implements Language {

	
	// na -> Model
    // DW() -> shrink
	//public class SmaliLanguage implements na{

	// private final SmaliCodeModel myCodeModel;
	HighlighterSyntax highlighterSyntax = new HighlighterSyntax();
	
	public SmaliLanguage(SmaliCodeModel codeModel) {
		// myCodeModel = codeModel;
	}

	@Override
	public void shrink() {

	}

	// ra -> TypeSystem
	// FH() -> getTypeSystem
	@Override
	public TypeSystem getTypeSystem() {
		// TODO: Implement this method
		return null;
	}
	// Hw() -> getCodeAnalyzer
	// ga -> CodeAnalyzer
	@Override
	public CodeAnalyzer getCodeAnalyzer() {
		return null;
	}
	
	// oa -> SignatureAnalyzer
	// VH()-> getSignatureAnalyzer
	@Override
	public SignatureAnalyzer getSignatureAnalyzer() {
		return null;
	}
	
	// Zo() -> getFormatOptionSet
	// ha -> FormatOption
	@Override
	public Set<? extends FormatOption> getFormatOptionSet() {
		return null;
	}
	
	// gn() -> getCodeRenderer
	// ia -> CodeRenderer
	@Override
	public CodeRenderer getCodeRenderer() {
		return null;
	}
	
	// j6() -> getName
	
	@Override
	public String getName() {
		return "Smali";
	}
	
	// j6() -> isParenChar
	
	@Override
	public boolean isParenChar(char c) {
		return false;
	}

	// qa -> Tools
	@Override
	public Tools getTools() {
		return null;
	}
	
	// pa -> Syntax
	// v5() -> getSyntax
	@Override
	public Syntax getSyntax() {
		return highlighterSyntax;
	}
}
