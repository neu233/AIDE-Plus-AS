package com.aide.engine.service;

import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.language.aidl.AidlCodeModel;
import com.aide.codemodel.language.classfile.JavaBinaryCodeModel;
import com.aide.codemodel.language.cpp.CppCodeModel;
import com.aide.codemodel.language.css.CssCodeModel;
import com.aide.codemodel.language.dtd.DtdCodeModel;
import com.aide.codemodel.language.html.HtmlCodeModel;
import com.aide.codemodel.language.java.JavaCodeModel;
import com.aide.codemodel.language.js.JavaScriptCodeModel;
import com.aide.codemodel.language.xml.XmlCodeModel;
import com.aide.ui.util.FilePatternMatcher;
import com.aide.ui.util.FileSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface;

public class CodeModelFactory {



    public CodeModelFactory() {}

    public static CodeModel[] create(Model model, List<String> codeModelNames) {
		
        CodeModel[] codeModelArr = new CodeModel[]{
			new JavaCodeModel(model), 
			new JavaBinaryCodeModel(model), 
			new XmlCodeModel(model), 
			new HtmlCodeModel(model), 
			new CssCodeModel(model), 
			new JavaScriptCodeModel(model), 
			new DtdCodeModel(model), 
			new CppCodeModel(model), 
			new AidlCodeModel(model)};
		
		ArrayList<CodeModel> codeModels = new ArrayList<>();

		for (int i = 0; i < 9; i++) {
			CodeModel codeModel = codeModelArr[i];
			if (codeModelNames.contains(codeModel.getName())) {
				codeModels.add(codeModel);
			}
		}
		
		// 扩展 添加自定义CodeModel
		ZeroAicyExtensionInterface.createCodeModels(model, codeModelNames, codeModels);
		
		return codeModels.toArray(new CodeModel[codeModels.size()]);
    }

    private static CodeModel[] create(List<String> list) {
		return create(null, list);
	}
	
	/**
	 * 根据
	 */
    public static CodeModel findCodeModel(String str, List<String> list) {
		String name = FileSystem.getName(str);
		for (CodeModel codeModel : create(list)) {
			for (String filePatterns : Arrays.asList(codeModel.getDefaultFilePatterns())) {
				if (FilePatternMatcher.j6() != null && FilePatternMatcher.j6().DW(name, filePatterns)) {
					return codeModel;
				}
			}
		}

		return null;
    }

    public static TreeMap<String, List<String>> findCodeModels(List<String> list) {
		TreeMap<String, List<String>> treeMap = new TreeMap<>();
		for (CodeModel codeModel : create(list)) {
			String[] defaultFilePatterns = codeModel.getDefaultFilePatterns();
			List<String> defaultFilePatternList = Arrays.asList(defaultFilePatterns);
			treeMap.put(codeModel.getName(), defaultFilePatternList);
		}
		return treeMap;
	}

}
