package com.aide.codemodel.language.aidl;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.language.classfile.JavaBinaryLanguage;
import com.aide.codemodel.api.abstraction.SignatureAnalyzer;
import com.aide.codemodel.api.abstraction.CodeAnalyzer;
import com.aide.codemodel.api.abstraction.TypeSystem;
import com.aide.codemodel.api.abstraction.CodeRenderer;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.Tools;
import com.aide.codemodel.api.abstraction.Syntax;
import java.util.Set;
import com.aide.codemodel.api.abstraction.FormatOption;
import com.aide.codemodel.language.java.JavaTypeSystem;
import com.aide.codemodel.language.java.JavaCodeAnalyzer;
import com.aide.codemodel.language.java.JavaSignatureAnalyzer;
import com.aide.codemodel.language.java.JavaFormatOption;
import com.aide.codemodel.HighlighterSyntax;

public class AidlLanguage implements Language {


	@Override
	public Set<? extends FormatOption> getFormatOptionSet() {
		return JavaFormatOption.getJavaFormatOptionSet();
	}

	@Override
	public String getName() {
		return "AIDL";
	}

	@Override
	public Syntax getSyntax() {
		return new HighlighterSyntax();
	}

	@Override
	public Tools getTools() {
		return this.aidlTools;
	}

	@Override
	public boolean isParenChar(char p) {
		return false;
	}

	@Override
	public void shrink() {}


	@Override
	public JavaCodeAnalyzer getCodeAnalyzer() {
		return null;
	}

	@Override
	public CodeRenderer getCodeRenderer() {
		return null;
	}

	@Override
	public JavaSignatureAnalyzer getSignatureAnalyzer() {
		return null;
	}

	@Override
	public JavaTypeSystem getTypeSystem() {
		return null;
	}

	private final Model model;
	private AidlTools aidlTools;

	public AidlLanguage(Model model) {
		this.model = model;
		if (model != null)
            this.aidlTools = new AidlTools(model);
	}

}
