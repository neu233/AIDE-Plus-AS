package com.aide.codemodel.language.kotlin;

import com.aide.codemodel.Highlighter;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.SyntaxTreeStyles;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.abstraction.Debugger;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.Preprocessor;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KotlinCodeModel implements CodeModel {
    public final Model model;
    private final KotlinLexer kotlinLexer = new KotlinLexer();
    private final KotlinLanguage myLanguage;
    private final Highlighter myHighlighter;

	private KotlinCodeCompiler KotlinCodeCompiler;
    public KotlinCodeModel(Model model) {
        this.model = model;
        this.myLanguage = new KotlinLanguage(this);
        this.myHighlighter = new Highlighter(kotlinLexer);

		if (model == null) {
			return;
		}
		KotlinCodeCompiler = new KotlinCodeCompiler(model, myLanguage);
		
    }
    @Override
    public long getArchiveVersion(String s) {
        return 0;
    }

    @Override
    public void update() {

    }

    @Override
    public boolean u7() {
        return false;
    }

    @Override
    public String[] getExtendFilePatterns() {
        return new String[0];
    }

    @Override
    public String[] getDefaultFilePatterns() {
        return new String[]{"*.kt","*.kts"};
    }

    @Override
    public void closeArchive() {

    }

    @Override
    public Debugger getDebugger() {
        return null;
    }

    @Override
    public boolean isSupportArchiveFile() {
        return false;
    }

    @Override
    public Reader getArchiveEntryReader(String s, String s1, String s2) {
        return null;
    }

    @Override
    public String getName() {
        return "Kotlin";
    }

    @Override
    public void processVersion(FileEntry da, Language na) {

    }

    @Override
    public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTreeStyles> map) {
        myHighlighter.highlight(fileEntry, reader, map.get(myLanguage));
    }

    @Override
    public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTree> map, boolean b) {
        if (map.containsKey(myLanguage)) {
            SyntaxTree syntaxTree = map.get(myLanguage);
            if (syntaxTree != null)
				syntaxTree.declareContent(syntaxTree.declareNode(0, true, new int[0], 0, 0, 1, 1));
			//sa.DW(sa.j6(0, true, new int[0], 0, 0, 1, 1));
        }
    }

    @Override
    public String[] getArchiveEntries(String s) {
        return new String[0];
    }

    @Override
    public Preprocessor getPreprocessor() {
        return null;
    }

	@Override
    public com.aide.codemodel.api.abstraction.CodeCompiler getCodeCompiler() {
        return this.KotlinCodeCompiler;
    }

    @Override
    public List<Language> getLanguages() {
        List<Language> list=new ArrayList<>();
        list.add(myLanguage);
        return list;
    }
}
