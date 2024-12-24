//
// Decompiled by Jadx - 793ms
//
package com.aide.codemodel.language.aidl;

import com.aide.codemodel.*;
import com.aide.codemodel.api.*;
import com.aide.codemodel.api.abstraction.*;
import java.io.*;
import java.util.*;

public class AidlCodeModel implements CodeModel {

	private Highlighter myHighlighter;

	private AidlLanguage myLanguage;

	
	Model model;
	AidlLexer aidlLexer;
	
    public AidlCodeModel(Model model) {
		
        try {
			this.model = model;
			myLanguage = new AidlLanguage(model);
			aidlLexer = new AidlLexer();
			myHighlighter = new Highlighter(aidlLexer);
        } catch (Throwable th) {
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    public void closeArchive() {
        
    }

    public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTreeStyles> map) {
        myHighlighter.highlight(fileEntry, reader, map.get(myLanguage));
    }

    public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTree> map, boolean z) {
        if (map.containsKey(myLanguage)) {
			SyntaxTree syntaxTree = map.get(myLanguage);
			if (syntaxTree != null){
				// declareContent [声明内容]
				syntaxTree.declareContent(syntaxTree.declareNode(0, true, new int[0], 0, 0, 1, 1));					
			}
		}
    }

    public String[] getArchiveEntries(String str) {
        return null;
    }

    public Reader getArchiveEntryReader(String str, String str2, String str3) {
        return null;
    }

    public long getArchiveVersion(String str) {
		return 0L;
	}

    public CodeCompiler getCodeCompiler() {
        return null;
    }

    public Debugger getDebugger() {
        return null;
    }

    public String[] getDefaultFilePatterns() {
        return new String[]{"*.aidl"};
    }

    public String[] getExtendFilePatterns() {
        return new String[0];
    }

    public List<Language> getLanguages() {
        List<Language> list=new ArrayList<>();
		list.add(myLanguage);
		return list;
    }

    public String getName() {
		return "AIDL";
	}

    public Preprocessor getPreprocessor() {
        return null;
    }

    public boolean isSupportArchiveFile() {
        return false;
    }

    public void processVersion(FileEntry fileEntry, Language language) {
        
    }

    public boolean u7() {
        return true;
    }

    public void update() {}
}

