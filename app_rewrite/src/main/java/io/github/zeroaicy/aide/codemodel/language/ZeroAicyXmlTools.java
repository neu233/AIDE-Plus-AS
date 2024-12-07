package io.github.zeroaicy.aide.codemodel.language;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.language.xml.XmlLanguage;
import com.aide.codemodel.language.xml.XmlTools;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.collections.SetOf;
import com.aide.codemodel.api.Type;

public class ZeroAicyXmlTools extends XmlTools {
	public ZeroAicyXmlTools(Model model, XmlLanguage xmlLanguage, boolean p) {
		super(model, xmlLanguage, p);
	}
	
	// ADD_IMPORT 忽略
	@Override
	public String vJ(SyntaxTree syntaxTree, int p, int p1, SetOf<? extends Type> setOf) {
		return "";
	}
	
}
