package com.aide.codemodel.language.kotlin;

import com.aide.codemodel.HighlighterSyntax;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.abstraction.CodeAnalyzer;
import com.aide.codemodel.api.abstraction.CodeRenderer;
import com.aide.codemodel.api.abstraction.FormatOption;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.SignatureAnalyzer;
import com.aide.codemodel.api.abstraction.Syntax;
import com.aide.codemodel.api.abstraction.Tools;
import com.aide.codemodel.api.abstraction.TypeSystem;
import java.util.Set;

public class KotlinLanguage implements Language {
    // private final KotlinCodeModel codeModel;
    private KotlinTools kotlinTools;
    public KotlinLanguage(KotlinCodeModel codeModel) {
        //this.codeModel = codeModel;
        Model model = codeModel.model;
		if (model != null)
            this.kotlinTools = new KotlinTools(model);
    }
	
    @Override
    public void shrink() {

    }

    @Override
    public TypeSystem getTypeSystem() {
        return null;
    }

    @Override
    public CodeAnalyzer getCodeAnalyzer() {
        return null;
    }

    @Override
    public SignatureAnalyzer getSignatureAnalyzer() {
        return null;
    }

    @Override
    public Set<? extends FormatOption> getFormatOptionSet() {
        return null;
    }

    @Override
    public CodeRenderer getCodeRenderer() {
        return null;
    }

    @Override
    public String getName() {
        return "Kotlin";
    }

    @Override
    public boolean isParenChar(char c) {
        return false;
    }

    @Override
    public Tools getTools() {
        return kotlinTools;
    }

    @Override
    public Syntax getSyntax() {
        return new HighlighterSyntax();
    }
}
