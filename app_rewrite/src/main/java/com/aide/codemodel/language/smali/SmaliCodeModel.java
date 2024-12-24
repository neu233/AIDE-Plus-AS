package com.aide.codemodel.language.smali;

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

public class SmaliCodeModel implements CodeModel {
	
	
	// la -> CodeModel
    // La -> Model
	// Da -> FileEntry
	// Wa -> SyntaxTreeStyles
    // na -> Language

    public final Model model;
    private final SmaliLexer myLexer=new SmaliLexer();
    private final Highlighter myHighlighter;
    private final SmaliLanguage language;
    public SmaliCodeModel(Model model) {
        this.model = model;
        language = new SmaliLanguage(this);
        myHighlighter = new Highlighter(myLexer);
    }

	// DW -> getArchiveVersion
    @Override
    public long getArchiveVersion(String s) {
        return 0;
    }
	
	// DW -> update
    @Override
    public void update() {

    }
	
	// EQ() -> u7
	@Override
	public boolean u7() {
		return false;
	}

	// FH() -> getExtendFilePatterns
    @Override
    public String[] getExtendFilePatterns() {
        return new String[0];
    }

	// Hw -> getDefaultFilePatterns
    @Override
    public String[] getDefaultFilePatterns() {
        return new String[]{"*.smali"};
    }
	
	// VH() -> closeArchive
    @Override
    public void closeArchive() {

    }

	// ka -> Debugger
	// Zo() -> getDebugger
    @Override
    public Debugger getDebugger() {
        return null;
    }
	
	// gn -> isSupportArchiveFile
    @Override
    public boolean isSupportArchiveFile() {
        return false;
    }

	// j6 -> getArchiveEntryReader
    @Override
    public Reader getArchiveEntryReader(String s, String s1, String s2) {
        return null;
    }

	// j6 -> getName
    @Override
    public String getName() {
        return "Smali";
    }

	// j6 -> processVersion
    @Override
    public void processVersion(FileEntry fileEntry, Language language) {

    }

	@Override
	public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTreeStyles> map) {
        myHighlighter.highlight(fileEntry, reader, map.get(language));
    }

	// Sa -> SyntaxTree
    @Override
    public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTree> map, boolean b) {
        if (map.containsKey(language)) {
            SyntaxTree syntaxTree = map.get(language);
            if (syntaxTree != null)
				syntaxTree.declareContent(syntaxTree.declareNode(0, true, new int[0], 0, 0, 1, 1));
			//syntaxTree.DW(syntaxTree.j6(0, true, new int[0], 0, 0, 1, 1));
        }
    }

	// j6 -> getArchiveEntries
    @Override
    public String[] getArchiveEntries(String s) {
        return new String[0];
    }

	// ma -> Preprocessor
	// tp() -> getPreprocessor
    @Override
    public Preprocessor getPreprocessor() {
        return null;
    }


	// ja -> CodeCompiler
	// u7() ->getCodeCompiler
    @Override
    public com.aide.codemodel.api.abstraction.CodeCompiler getCodeCompiler() {
        return null;
    }

	// v5 -> getLanguages
    @Override
    public List<Language> getLanguages() {
        List<Language> list=new ArrayList<>();
        list.add(language);
        return list;
    }
}
