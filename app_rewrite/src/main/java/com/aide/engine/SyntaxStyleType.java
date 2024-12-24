package com.aide.engine;

import android.content.Context;
import android.graphics.Typeface;
import android.util.SparseArray;
import com.aide.codemodel.api.HighlighterType;
import com.aide.ui.rewrite.R;
import io.github.zeroaicy.aide.highlight.CodeTheme;
import io.github.zeroaicy.aide.highlight.ColorKind;

public enum SyntaxStyleType {

	// 常规文字颜色
    PLAIN("Plain", R.color.editor_syntax_plain_light, R.color.editor_syntax_plain, Typeface.NORMAL),

	// BOLD -> BOLD_ITALIC
	// 关键字颜色
	KEYWORD("Keyword", R.color.editor_syntax_keyword_light, R.color.editor_syntax_keyword, Typeface.BOLD),

    //标识符颜色
	IDENTIFIER("Identifier", R.color.editor_syntax_identifier_light, R.color.editor_syntax_identifier, Typeface.NORMAL),

	// ITALIC -> NORMAL
	// 包名颜色
    NAMESPACE_IDENTIFIER("Namespace/Package Identifier", R.color.editor_syntax_package_light, R.color.editor_syntax_package, Typeface.NORMAL),

	// 类型标识符颜色
    TYPE_IDENTIFIER("Type Identifier", R.color.editor_syntax_type_light, R.color.editor_syntax_type, Typeface.NORMAL),

    // ITALIC -> NORMAL
    DELEGATE_IDENTIFIER("Delegate Identifier", R.color.editor_syntax_type_light, R.color.editor_syntax_type, Typeface.NORMAL),

	// "操作符颜色"
	OPERATOR("Operator", R.color.editor_syntax_operator_light, R.color.editor_syntax_operator, Typeface.NORMAL),

	// "括号、标点颜色"
	SEPARATOR("Separator/Punctuator", R.color.editor_syntax_separator_light, R.color.editor_syntax_separator, Typeface.NORMAL),

	// "字符串、数字、布尔值颜色"
	LITERAL("Literal", R.color.editor_syntax_literal_light, R.color.editor_syntax_literal, Typeface.NORMAL),

	PREPROCESSOR("Preprocessor", R.color.editor_syntax_plain_light, R.color.editor_syntax_plain, Typeface.NORMAL),

	// NORMAL -> ITALIC
	// "代码注释颜色"
    COMMENT("Comment", R.color.editor_syntax_comment_light, R.color.editor_syntax_comment, Typeface.ITALIC),

	DOC_COMMENT("Documentation Comment", R.color.editor_syntax_comment_light, R.color.editor_syntax_comment, Typeface.ITALIC),

	SCRIPT_BACKGROUND("Script Background", R.color.editor_syntax_plain, R.color.editor_syntax_plain, Typeface.NORMAL),
	
	//扩展的
	//PARAMETER("Parameter", R.color.material_grey_100, R.color.material_grey_100, Typeface.ITALIC)
	// 未使用
	UNUSED("Unused", R.color.editor_syntax_unused_light, R.color.editor_syntax_unused, Typeface.NORMAL),
	// 参数标识符
	ARGUMENT_IDENTIFIER("Argument Identifier", R.color.editor_syntax_argument_identifier_light, R.color.editor_syntax_argument_identifier, Typeface.ITALIC),
	
	// 必须是最后一个
	// Lcom/aide/engine/StyleSpan;->gn(BIIII)V
	SCRIPT("Script", R.color.editor_syntax_plain_light, R.color.editor_syntax_plain, Typeface.NORMAL),


	;

    // private final String enumName;

	private final int darkResId;
    private final int lightResId;
    private final int typefaceStyle;

	private final ColorKind colorKind;
	
	private final boolean isColorValue;
    private SyntaxStyleType(String enumName, int lightResId, int darkResId, int typefaceStyle) {
		// this.enumName = enumName;

		this.isColorValue = false;
		this.lightResId = lightResId;
		this.darkResId = darkResId;
		// 粗体 斜体 
		this.typefaceStyle = typefaceStyle;

		// 绑定ColorKind;
		this.colorKind = CodeTheme.getColorKind(enumName);
    }
	
	private SyntaxStyleType(String enumName, boolean isColorValue, int lightColorValue, int darkColorValue, int typefaceStyle) {
		this.isColorValue = true;;
		this.lightResId = lightColorValue;
		this.darkResId = darkColorValue;
		// 粗体 斜体 
		this.typefaceStyle = typefaceStyle;
		// 绑定ColorKind;
		this.colorKind = CodeTheme.getColorKind(enumName);
	}
	
	
    public int getColor(Context context, boolean isLight) {
		
		if( this.colorKind != null){
			return this.colorKind.getColor(context, isLight);
		}
		// 颜色值模式
		if( this.isColorValue){
			return isLight ? lightResId : darkResId;
		}
		
		// 颜色Id模式
		return context.getColor(isLight ? lightResId : darkResId);
	}

    public int getTypefaceStyle() {
		if( this.colorKind != null){
			return this.colorKind.getTypefaceStyle();
		}
		return this.typefaceStyle;
    }
	
	private static SparseArray<SyntaxStyleType> syntaxStyleTypes;
	
	public static SyntaxStyleType getStyleTypeOfHighlighterType(int highlighterType){
		switch(highlighterType){
			case HighlighterType.UnUsed:
				return SyntaxStyleType.UNUSED;
			case HighlighterType.ArgumentIdentifier:
				return SyntaxStyleType.ARGUMENT_IDENTIFIER;
				
		}
		
		
		if( syntaxStyleTypes == null){
			syntaxStyleTypes = new SparseArray<>();
			for( SyntaxStyleType syntaxStyleType : SyntaxStyleType.values()){
				syntaxStyleTypes.put(syntaxStyleType.ordinal(), syntaxStyleType);
			}
		}
		
		SyntaxStyleType syntaxStyleType = syntaxStyleTypes.get(highlighterType);
		if( syntaxStyleType != null){
			return syntaxStyleType;
		}
		// 防止为null
		return SyntaxStyleType.PLAIN;
	}
}

