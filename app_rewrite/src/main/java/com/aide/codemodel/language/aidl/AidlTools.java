package com.aide.codemodel.language.aidl;
import abcd.tp;
import com.aide.codemodel.DefaultTools;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.callback.CodeCompleterCallback;

public class AidlTools extends DefaultTools {
	public AidlTools(Model model) {
        super(model);
    }

    @Override
    public void er(SyntaxTree syntaxTree, FileEntry fileEntry, Language language, int line, int column) {
        String pathString = fileEntry.getPathString();
		CodeCompleterCallback codeCompleterCallback = model.codeCompleterCallback;
		if (pathString.endsWith(".aidl") ) {

			codeCompleterCallback.listStarted();

            codeCompleterCallback.listElementKeywordFound("true");
			codeCompleterCallback.listElementKeywordFound("false");
			codeCompleterCallback.listElementKeywordFound("null");
			codeCompleterCallback.listElementKeywordFound("abstract");
			codeCompleterCallback.listElementKeywordFound("boolean");
			codeCompleterCallback.listElementKeywordFound("break");
			codeCompleterCallback.listElementKeywordFound("byte");
			codeCompleterCallback.listElementKeywordFound("case");
			codeCompleterCallback.listElementKeywordFound("catch");
			codeCompleterCallback.listElementKeywordFound("char");
			codeCompleterCallback.listElementKeywordFound("class");
			codeCompleterCallback.listElementKeywordFound("const");
			codeCompleterCallback.listElementKeywordFound("continue");
			codeCompleterCallback.listElementKeywordFound("default");
			codeCompleterCallback.listElementKeywordFound("do");
			codeCompleterCallback.listElementKeywordFound("double");
			codeCompleterCallback.listElementKeywordFound("else");
			codeCompleterCallback.listElementKeywordFound("extends");
			codeCompleterCallback.listElementKeywordFound("final");
			codeCompleterCallback.listElementKeywordFound("finally");
			codeCompleterCallback.listElementKeywordFound("float");
			codeCompleterCallback.listElementKeywordFound("for");
			codeCompleterCallback.listElementKeywordFound("goto");
			codeCompleterCallback.listElementKeywordFound("if");
			codeCompleterCallback.listElementKeywordFound("implements");
			codeCompleterCallback.listElementKeywordFound("import");
			codeCompleterCallback.listElementKeywordFound("instanceof");
			codeCompleterCallback.listElementKeywordFound("int");
			codeCompleterCallback.listElementKeywordFound("interface");
			codeCompleterCallback.listElementKeywordFound("long");
			codeCompleterCallback.listElementKeywordFound("native");
			codeCompleterCallback.listElementKeywordFound("new");
			codeCompleterCallback.listElementKeywordFound("package");
			codeCompleterCallback.listElementKeywordFound("private");
			codeCompleterCallback.listElementKeywordFound("public");
			codeCompleterCallback.listElementKeywordFound("short");
			codeCompleterCallback.listElementKeywordFound("super");
			codeCompleterCallback.listElementKeywordFound("switch");
			codeCompleterCallback.listElementKeywordFound("synchronized");
			codeCompleterCallback.listElementKeywordFound("this");
			codeCompleterCallback.listElementKeywordFound("throw");
			codeCompleterCallback.listElementKeywordFound("protected");
			codeCompleterCallback.listElementKeywordFound("transient");
			codeCompleterCallback.listElementKeywordFound("return");
			codeCompleterCallback.listElementKeywordFound("void");
			codeCompleterCallback.listElementKeywordFound("static");
			codeCompleterCallback.listElementKeywordFound("strictfp");
			codeCompleterCallback.listElementKeywordFound("while");
			codeCompleterCallback.listElementKeywordFound("try");
			codeCompleterCallback.listElementKeywordFound("volatile");
			codeCompleterCallback.listElementKeywordFound("throws");
			
			codeCompleterCallback.listElementKeywordFound("parcelable");
			codeCompleterCallback.listElementKeywordFound("import");
			codeCompleterCallback.listElementKeywordFound("package");
			codeCompleterCallback.listElementKeywordFound("in");
			codeCompleterCallback.listElementKeywordFound("out");
			codeCompleterCallback.listElementKeywordFound("inout");
			codeCompleterCallback.listElementKeywordFound("cpp_header");
			codeCompleterCallback.listElementKeywordFound("ndk_header");
			codeCompleterCallback.listElementKeywordFound("rust_type");
			codeCompleterCallback.listElementKeywordFound("const");
			codeCompleterCallback.listElementKeywordFound("true");
			codeCompleterCallback.listElementKeywordFound("false");
			codeCompleterCallback.listElementKeywordFound("interface");
			codeCompleterCallback.listElementKeywordFound("oneway");
			codeCompleterCallback.listElementKeywordFound("enum");
			codeCompleterCallback.listElementKeywordFound("union");
			// j6 -> J8
            codeCompleterCallback.J8(fileEntry, language, line, column, tp(fileEntry, line, column), false, false);
			return;
        }
		codeCompleterCallback.a8(fileEntry, line, column);
    }

	protected int tp(FileEntry fileEntry, int line, int column) {

		String Mr = fileEntry.Mr(line, column);
		int length = Mr.length() - 1;
		while (length >= 0) {
			if (!Character.isLetter(Mr.charAt(length)) && Mr.charAt(length) != '.') {
				break;
			}
			length--;
		}
		return length + 2;
    }
}
