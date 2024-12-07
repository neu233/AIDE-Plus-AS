//
// Decompiled by Jadx - 907ms
//
package com.aide.codemodel.language.java;

import abcd.g6;
import android.util.SparseArray;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.SyntaxTreeStyles;
import com.aide.codemodel.api.abstraction.CodeCompiler;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.abstraction.Debugger;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.Preprocessor;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JavaCodeModelPro implements CodeModel {

    private final JSharpCommentsLanguage jsharpCommentsLanguage;
	
    private CodeCompiler javaCompiler;
    private JavaDebugger javaDebugger;


	private g6 jSharpCommentsParser;
	
	private JavaLexer javaLexer;
	private JavaParser javaParser;
	
    public final JavaLanguagePro javaLanguage;
	
    private final Model model;
	
    public JavaCodeModelPro(Model model) {
		this.model = model;
		this.javaLanguage = new JavaLanguagePro(model, this);
		this.jsharpCommentsLanguage = new JSharpCommentsLanguage(model, false);
		
		if (model == null) {
			return;
		}
		// 仅在debug版本中替换
		// this.javaCompiler = new JavaCompiler(model, this.javaLanguage);
		this.javaCompiler = new EclipseJavaCodeCompiler(model, this);
		
		this.javaDebugger = new JavaDebugger(model, this.javaLanguage, this);
		this.javaLexer = new JavaLexer(model.identifierSpace, model.errorTable, false, this.javaLanguage, this.jsharpCommentsLanguage);
		this.javaParser = new JavaParserPro(model.identifierSpace, model.errorTable, model.entitySpace, this.javaLanguage.getSyntax(), false);
		this.jSharpCommentsParser = new g6(model);

    }
	
	
	
    @Override
    public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTreeStyles> map) {
		this.javaLexer.Zo(fileEntry, reader, false, false, false, false, map.get(this.javaLanguage), map.get(this.jsharpCommentsLanguage));
    }
	
    @Override
    public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTree> map, boolean z) {
		
		SyntaxTreeStyles makeSyntaxTreeStyles = this.model.U2.makeSyntaxTreeStyles();
		SyntaxTreeStyles makeSyntaxTreeStyles2 = this.model.U2.makeSyntaxTreeStyles();
		// parser
		this.javaLexer.Zo(fileEntry, reader, z && map.containsKey(this.javaLanguage), z && map.containsKey(this.jsharpCommentsLanguage), map.containsKey(this.javaLanguage), map.containsKey(this.jsharpCommentsLanguage), makeSyntaxTreeStyles, makeSyntaxTreeStyles2);
		
		if (map.containsKey(this.javaLanguage)) {
			this.javaParser.init(makeSyntaxTreeStyles, fileEntry, z, map.get(this.javaLanguage));
		}
		
		// parser
		this.model.U2.DW(makeSyntaxTreeStyles);
		if (map.containsKey(this.jsharpCommentsLanguage)) {
			this.jSharpCommentsParser.j6(makeSyntaxTreeStyles2, fileEntry, z, map.get(this.jsharpCommentsLanguage));
		}
		
		// 回收语法树
		this.model.U2.DW(makeSyntaxTreeStyles2);
		
		initEnv();
		// ProjectEnvironment.fillFileEntry(this.projectEnvironments, model, fileEntry);
		
    }

    @Override
    public String[] getArchiveEntries(String str) {
        return null;
    }

    @Override
    public Reader getArchiveEntryReader(String str, String str2, String str3) {
        return null;
    }

    @Override
    public long getArchiveVersion(String str) {
        return 0L;
    }

    @Override
    public CodeCompiler getCodeCompiler() {
        return this.javaCompiler;
    }

    @Override
    public Debugger getDebugger() {
        return this.javaDebugger;
    }

    @Override
    public String[] getDefaultFilePatterns() {
        return new String[]{"*.java"};
    }

    @Override
    public String[] getExtendFilePatterns() {
        return new String[0];
    }
	
	@Override
    public List<Language> getLanguages() {
		ArrayList<Language> arrayList = new ArrayList<>();
		arrayList.add(this.javaLanguage);
		arrayList.add(this.jsharpCommentsLanguage);
		return arrayList;
    }

    @Override
	public String getName() {
        return "Java";
    }

    @Override
	public Preprocessor getPreprocessor() {
        return null;
    }

    @Override
	public boolean isSupportArchiveFile() {
        return false;
    }
	
	@Override
    public void closeArchive() {

    }

	
    @Override
    public void processVersion(FileEntry fileEntry, Language language) {

    }

    @Override
    public boolean u7() {
        return false;
    }

    @Override
    public void update() {
		if( this.javaCompiler instanceof JavaCompiler){
			((JavaCompiler)this.javaCompiler).createClassWriter();
		}
    }
	
	
	SparseArray<ProjectEnvironment> projectEnvironments;
	public void initEnv() {
		Model model = this.model;
		if (model == null || this.projectEnvironments != null) {
			return;
		}
		projectEnvironments = new SparseArray<>();
		ProjectEnvironment.init(model, this.projectEnvironments);
	}
	
	
	public void reset(){
		if( this.projectEnvironments == null ){
			return;
		}
		for( int index = 0, size = projectEnvironments.size(); index < size; index++){
			ProjectEnvironment projectEnvironment = projectEnvironments.valueAt(index);
			projectEnvironment.reset();
		}
		projectEnvironments.clear();
		projectEnvironments = null;
	}
	
}

