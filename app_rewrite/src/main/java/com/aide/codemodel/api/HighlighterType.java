/**
 * @Author ZeroAicy
 * @AIDE AIDE+
*/
package com.aide.codemodel.api;
import com.aide.engine.SyntaxStyleType;

public class HighlighterType{
	
	// 采用负值 防止与 SyntaxStyleType ordinal 冲突
	// 未使用
	public static final int UnUsed = -1;
	// 方法参数标识符
	public static final int ArgumentIdentifier = -2;
	
	
	public static final int TextBlock = SyntaxStyleType.LITERAL.ordinal();
}
