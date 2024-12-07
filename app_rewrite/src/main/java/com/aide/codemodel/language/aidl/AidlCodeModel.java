//
// Decompiled by Jadx - 793ms
//
package com.aide.codemodel.language.aidl;

import abcd.th;
import com.aide.codemodel.Highlighter;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.SyntaxTreeStyles;
import com.aide.codemodel.api.abstraction.CodeCompiler;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.abstraction.Debugger;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.Preprocessor;
import com.aide.codemodel.language.aidl.AidlCodeModel;
import com.google.android.gms.internal.ads.iy;
import com.probelytics.annotation.ExceptionEnabled;
import com.probelytics.annotation.MethodMark;
import com.probelytics.annotation.ParametersEnabled;
import com.probelytics.annotation.TypeMark;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@TypeMark(clazz = 6949712858441021368L, container = 6949712858441021368L, user = true)
public class AidlCodeModel implements CodeModel {

    @ExceptionEnabled
    private static boolean DW;

    @ParametersEnabled
    private static boolean j6;

	private Highlighter myHighlighter;

	private AidlLanguage myLanguage;

	
	Model model;
	AidlLexer aidlLexer;
	
    @MethodMark(method = 36659414205610416L)
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

    @MethodMark(method = -3641933856937742513L)
    public void closeArchive() {
        
    }

    @MethodMark(method = -2860436783176542765L)
    public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTreeStyles> map) {
        myHighlighter.highlight(fileEntry, reader, map.get(myLanguage));
    }

    @MethodMark(method = 2470293890794684288L)
    public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTree> map, boolean z) {
        if (map.containsKey(myLanguage)) {
			SyntaxTree syntaxTree = map.get(myLanguage);
			if (syntaxTree != null){
				// declareContent [声明内容]
				syntaxTree.declareContent(syntaxTree.declareNode(0, true, new int[0], 0, 0, 1, 1));					
			}
		}
    }

    @MethodMark(method = 5849451323807206887L)
    public String[] getArchiveEntries(String str) {
        return null;
    }

    @MethodMark(method = 3954060409225664760L)
    public Reader getArchiveEntryReader(String str, String str2, String str3) {
        return null;
    }

    @MethodMark(method = 2040231874625047915L)
    public long getArchiveVersion(String str) {
		return 0L;
	}

    @MethodMark(method = -4462979187785903708L)
    public CodeCompiler getCodeCompiler() {
        return null;
    }

    @MethodMark(method = -1765898278405099200L)
    public Debugger getDebugger() {
        return null;
    }

    @MethodMark(method = 22993740422317037L)
    public String[] getDefaultFilePatterns() {
        return new String[]{"*.aidl"};
    }

    @MethodMark(method = -67344288115762064L)
    public String[] getExtendFilePatterns() {
        return new String[0];
    }

    @MethodMark(method = -3657681744464385708L)
    public List<Language> getLanguages() {
        List<Language> list=new ArrayList<>();
		list.add(myLanguage);
		return list;
    }

    @MethodMark(method = -2217810603626894101L)
    public String getName() {
		return "AIDL";
	}

    @MethodMark(method = -942823976672949672L)
    public Preprocessor getPreprocessor() {
        return null;
    }

    @MethodMark(method = -1665509274194170872L)
    public boolean isSupportArchiveFile() {
        return false;
    }

    @MethodMark(method = -2352837954259293108L)
    public void processVersion(FileEntry fileEntry, Language language) {
        
    }

    @MethodMark(method = 1567655813524964112L)
    public boolean u7() {
        return true;
    }

    @MethodMark(method = -2887196389108170100L)
    public void update() {}
}

