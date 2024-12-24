package io.github.zeroaicy.aide.highlight;

import android.content.Context;
import android.graphics.Typeface;
import com.aide.ui.rewrite.R;

public enum ColorKind {
	/**
	 * 编辑器颜色
	 */
	EDITOR_BACKGROUND("EditorBackground", "编辑器背景颜色", R.color.editor_background_light, R.color.editor_background),

	EDITOR_SELECTION("EditorSelection", "已选文字背景颜色", R.color.editor_selection_material_light, R.color.editor_selection_material),

	/**
	 * 代码高亮颜色
	 */
	// 常规文字颜色
    PLAIN("Plain", "文字颜色", R.color.editor_syntax_plain_light, R.color.editor_syntax_plain, Typeface.NORMAL),

	// BOLD -> BOLD_ITALIC
	// 关键字颜色
	KEYWORD("Keyword", "关键字颜色", R.color.editor_syntax_keyword_light, R.color.editor_syntax_keyword, Typeface.BOLD),

    //标识符颜色
	IDENTIFIER("Identifier", "标识符颜色", R.color.editor_syntax_identifier_light, R.color.editor_syntax_identifier, Typeface.NORMAL),

	// ITALIC -> NORMAL
	// 包名颜色
    NAMESPACE_IDENTIFIER("Namespace/Package Identifier", "包名颜色", R.color.editor_syntax_package_light, R.color.editor_syntax_package, Typeface.NORMAL),

	// 类型标识符颜色
    TYPE_IDENTIFIER("Type Identifier", "类型标识符颜色", R.color.editor_syntax_type_light, R.color.editor_syntax_type, Typeface.NORMAL),

	// "操作符颜色"
	OPERATOR("Operator", "操作符颜色", R.color.editor_syntax_operator_light, R.color.editor_syntax_operator, Typeface.NORMAL),

	// "括号、标点颜色"
	SEPARATOR("Separator/Punctuator", "括号、标点颜色", R.color.editor_syntax_separator_light, R.color.editor_syntax_separator, Typeface.NORMAL),

	// "字符串、数字、布尔值颜色"
	LITERAL("Literal", "字符串、数字、布尔值颜色", R.color.editor_syntax_literal_light, R.color.editor_syntax_literal, Typeface.NORMAL),

	// NORMAL -> ITALIC
	// "代码注释颜色"
	// -> #FF9B9B9B
    COMMENT("Comment", "代码注释颜色", R.color.editor_syntax_comment_light, R.color.editor_syntax_comment, Typeface.ITALIC),

	// 扩展
	UNUSED("Unused", "未使用变量颜色", R.color.editor_syntax_unused_light, R.color.editor_syntax_unused, Typeface.NORMAL),

	// 
	ARGUMENT_IDENTIFIER("Argument Identifier", "参数标识符颜色", R.color.editor_syntax_argument_identifier_light, R.color.editor_syntax_argument_identifier, Typeface.ITALIC),

	;

	// 数据持久化key
	public final String key;
	public final String colorName;
	// R.color.xxxxx

	// 亮主题颜色id
	// private final boolean isColorValue;
	private final int lightColorId;
	private final int darkColorId;

	// 默认字体风格 -1说明不支持字体风格
	private final int defaultTypefaceStyle;
	// 自定义字体风格
	private int customTypefaceStyle = -1;


	private ColorKind(String colorKindKey, String colorName, int lightColorId, int darkColorId){
			this(colorKindKey, colorName, lightColorId, darkColorId, -1);
	}
	
//	private ColorKind(String colorKindKey, String colorName, boolean isColorValue, int lightColorId, int darkColorId){
//			this(colorKindKey, colorName, isColorValue, lightColorId, darkColorId, -1);
//	}

	private ColorKind(String colorKindKey, String colorName, int lightColorId, int darkColorId, int typefaceStyle){
			this(colorKindKey, colorName, false, lightColorId, darkColorId, typefaceStyle);
	}

	private ColorKind(String colorKindKey, String colorName, boolean isColorValue, int lightColorId, int darkColorId, int typefaceStyle){
		// 作为唯一标志
		this.key = colorKindKey;

		this.colorName = colorName;

		// this.isColorValue = isColorValue;
		this.lightColorId = lightColorId;
		this.darkColorId = darkColorId;
		
		this.defaultTypefaceStyle = typefaceStyle;
	}


	public String getColorName(){
		return this.colorName;
	}

	public int getColorId(){
		return this.lightColorId;
	}

	// 颜色值
	private boolean hasCustomLightColor;
	private boolean hasCustomDarkColor;

	private int customLightColor; // 可动态设置
	private int customDarkColor; // 可动态设置


	public void setCustomColor(int color, boolean isLight){
		if( isLight ){
			this.customLightColor = color;
			// 具有color值
			this.hasCustomLightColor = true;
		}else{
			this.customDarkColor = color;
			// 具有color值
			this.hasCustomDarkColor = true;
		}		
	}

	/**
	 * 恢复默认，即去除自定义值
	 */
	public void restoreDefault(boolean isLight){
		restoreDefault(isLight, false);
	}
	
	public void restoreDefault(boolean isLight, boolean isTypefaceStyle){
		if( isTypefaceStyle ){
			this.customTypefaceStyle = -1;
		}
		if( isLight){
			this.hasCustomLightColor = false;
		}else{
			this.hasCustomDarkColor = false;
		}
	}

	/**
	 * 获取颜色
	 */
	public int getColor(Context context, boolean isLight) {
		
		if( isLight){
			if( this.hasCustomLightColor){
				return this.customLightColor;
			}
			
			int defaultLightColor = context.getColor(this.lightColorId);
			// colorId的值是定死的，与主题无关，所以直接储存起来
			setCustomColor(defaultLightColor, isLight);

			return defaultLightColor;
		}


		if( this.hasCustomDarkColor){
			return this.customDarkColor;
		}
		
		int defaultDarkColor = context.getColor(this.darkColorId);
		// colorId的值是定死的，与主题无关，所以直接储存起来
		setCustomColor(defaultDarkColor, isLight);

		return defaultDarkColor;
	}

	/**
	 * 优先返回自定义风格，请确保支持字体风格
	 */
	public int getTypefaceStyle() {
		if( this.customTypefaceStyle != -1){
			return this.customTypefaceStyle;
		}
		return this.defaultTypefaceStyle;
    }
	public void setTypefaceStyle(int customTypefaceStyle) {
		this.customTypefaceStyle = customTypefaceStyle;
	}

	/**
	 * 查询是否支持自定义字体风格
	 */
	public boolean hasTypefaceStyle(){
		return this.defaultTypefaceStyle != -1;
	}
}
