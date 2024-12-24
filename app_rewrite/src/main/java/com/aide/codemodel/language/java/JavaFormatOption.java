/**
 * @Author ZeroAicy
 * @AIDE AIDE+
 */

package com.aide.codemodel.language.java;

import androidx.annotation.Keep;
import com.aide.codemodel.api.abstraction.FormatOption;
import java.util.HashSet;
import java.util.Set;
import java.util.Locale;

public enum JavaFormatOption implements FormatOption {
	// Java indentation options
	// Java new line options
	// Java spacing options
    BLOCK_INDENT("Java indentation options", "Indent block contents", "缩进块内容", "public void f()\n{\n    int i;\n}", "public void f()\n{\nint i;\n}"),
    ARRAY_INDENT("Java indentation options", "Indent array initializer", "缩进数组初始化", "int[] arr = new int[]\n{\n    1\n};", "int[] arr = new int[]\n{\n1\n};"),
    BRACE_INDENT("Java indentation options", "Indent open and close brace", "缩进大括号", "public void f()\n    {\n        int i;\n    }", "public void f()\n{\n    int i;\n}"),
    CASE_INDENT("Java indentation options", "Indent case contents", "缩进case内容", "switch (x)\n{\n    case 10:\n        return 5;\n}", "switch (x)\n{\n    case 10:\n    return 5;\n}"),
    CASELABEL_INDENT("Java indentation options", "Indent case labels", "缩进case标签", "switch (x)\n{\n    case 10:\n        return 5;\n}", "switch (x)\n{\ncase 10:\n    return 5;\n}"),
    LABELLEFT_INDENT("Java indentation options", "Indent goto labels in leftmost column", "缩进列最左侧goto labels", "    goto L;\nL:\n    return;", "    goto L;\n    L:\n    return;"),
    NEXTLINE_INDENT("Java indentation options", "Indent lines in multi line statements", "缩进多行语句", "int i = 10 +\n    20;", "int i = 10 +\n20;"),
    ARGUMENT_INDENT("Java indentation options", "Align arguments", "对齐实参", "System.out.println(\"\",\n                  20);", "System.out.println(\"\",\n    20);"),
    PARAMETER_INDENT("Java indentation options", "Align parameters", "对齐形参", "void foo(int i,\n         int j)\n{\n}", "void foo(int i,\n    int j)\n{\n}"),
    ADJUST_NEWLINES("Java new line options", "Adjust lines on autoformat", "格式化时自动调整行", "", ""),
    TYPE_NEWLINE("Java new line options", "Place open brace on new line for types", "在类型声明时，左大括号换行", "public class C\n{\n    //...\n}", "public class C {\n    //...\n}"),
    METHOD_NEWLINE("Java new line options", "Place open brace on new line for methods", "在方法声明时，左大括号换行", "public void f()\n{\n    //...\n}", "public void f() {\n    //...\n}"),
    BLOCK_NEWLINE("Java new line options", "Place open brace on new line for control blocks", "在控制块声明时，左大括号换行", "public void f()\n{\n    if (a < b)\n    {\n    }\n}", "public void f()\n{\n    if (a < b) {\n    }\n}"),
    ELSE_NEWLINE("Java new line options", "Place \"else\" on new line", "将\"else\"放在新行", "if (i == 10) {\n    return 10;\n}\nelse {\n    return 0;\n}", "if (i == 10) {\n    return 10;\n} else {\n    return 0;\n}"),
    CATCH_NEWLINE("Java new line options", "Place \"catch\" on new line", "将\"catch\"放在新行", "\n}\ncatch {\n}", "\n} catch {\n}"),
    FINALLY_NEWLINE("Java new line options", "Place \"finally\" on new line", "将\"finally\"放在新行", "\n}\nfinally {\n}", "\n} finally {\n}"),
    STATEMENT_WRAP("Java wrapping options", "Place statement in a new line if it doesn't fit", "如果语句过长，则将其放在新行", "int i;\ni = 10;", "int i; i = 10;"),
    ADJUST_SPACES("Java spacing options", "Adjust spaces on autoformat", "格式化时自动调整空格", "", ""),
    BRACE_SPACE("Java spacing options", "Insert space before open brace", "在左大括号前插入空格", "public class C {\n    //...\n}", "public class C{\n    //...\n}"),
    METHODNAME_SPACE("Java spacing options", "Insert space before parameters", "在参数前插入空格", "public void f ()\n{\n}", "public void f()\n{\n}"),
    PARAMETER_SPACE("Java spacing options", "Insert space within parameter parentheses", "在参数括号内插入空格", "public void f( int i )\n{\n}", "public void f(int i)\n{\n}"),
    PARAMETERCOMMA_SPACE("Java spacing options", "Insert space after each parameter", "在每个形参后插入空格", "public void f(int i, int j)\n{\n}", "public void f(int i,int j)\n{\n}"),
    KEYWORD_SPACE("Java spacing options", "Insert space after keywords", "在关键字后插入空格", "if (a == b) return 0;", "if(a == b) return 0;"),
    CONDITION_SPACE("Java spacing options", "Insert space within statement parentheses", "在语句括号内插入空格", "if ( a == b ) return 0;", "if (a == b) return 0;"),
    PAREN_SPACE("Java spacing options", "Insert space within parentheses", "在括号内插入空格", "int i = ( 10 + 20 ) * 5;", "int i = (10 + 20) * 5;"),
    METHODCALL_SPACE("Java spacing options", "Insert space before arguments", "在实参前插入空格", "System.out.println (10);", "System.out.println(10);"),
    ARGUMENT_SPACE("Java spacing options", "Insert space within argument parentheses", "在实参括号内插入空格", "System.out.println( 10 );", "System.out.println(10);"),
    ARGUMENTCOMMA_SPACE("Java spacing options", "Insert space after each argument", "在每个实参后插入空格", "System.out.println(\"\", 20);", "System.out.println(\"\",20);"),
    ARRAYMETHODCALL_SPACE("Java spacing options", "Insert space before array brackets", "在数组括号前插入空格", "int i = ai [10];", "int i = ai[10];"),
    ARRAYARGUMENT_SPACE("Java spacing options", "Insert space within array brackets", "在数组括号内插入空格", "int i = ai[ 10 ];", "int i = ai[10];"),
    BINARYOPERATOR_SPACE("Java spacing options", "Insert space around binary operator ", "在二进制运算符周围插入空格", "int i;\ni = 10 + 20;", "int i;\ni = 10+20;"),
    ASSIGNMENTOPERATOR_SPACE("Java spacing options", "Insert space around assignment operator ", "在赋值运算符周围插入空格", "int i;\ni = 10;", "int i;\ni=10;");


	// 风格类型 type
	// 缩进参数 换行参数 空格参数
    private final String description;
    private final String description2;

    private final String oldStyleText;

    private final String newStyleText;

    private final String name;
    private final String name2;

	private JavaFormatOption(String formatOptionType, String styleName, String oldStyleText, String nowStyleText) {
			this( formatOptionType, styleName, styleName, oldStyleText, nowStyleText);
	}
    private JavaFormatOption(String formatOptionType, String styleName, String styleName2, String newStyleText, String oldStyleText) {
		this.description = formatOptionType;

		String formatOptionType2;
		switch(formatOptionType){
			case "Java indentation options":
				formatOptionType2 = "Java缩进选项";
				break;
			case "Java new line options":
				formatOptionType2 = "Java换行选项";
				break;
			case "Java wrapping options":
				formatOptionType2 = "Java包装选项";
				break;
			case "Java spacing options":
				formatOptionType2 = "Java空格选项";
				break;
			default :
				formatOptionType2 = formatOptionType;
				break;
		}
		this.description2 = formatOptionType2;


		// 
		this.name = styleName;
		this.name2 = styleName2;

		this.newStyleText = newStyleText;
		this.oldStyleText = oldStyleText;
    }

    @Keep
	public static Set<JavaFormatOption> getJavaFormatOptionSet() {

		HashSet<JavaFormatOption> hashSet = new HashSet<>();
		hashSet.add(ADJUST_NEWLINES);
		hashSet.add(ADJUST_SPACES);
		hashSet.add(TYPE_NEWLINE);
		hashSet.add(METHOD_NEWLINE);
		hashSet.add(BLOCK_NEWLINE);
		hashSet.add(ELSE_NEWLINE);
		hashSet.add(CATCH_NEWLINE);
		hashSet.add(FINALLY_NEWLINE);
		hashSet.add(STATEMENT_WRAP);
		hashSet.add(PARAMETERCOMMA_SPACE);
		hashSet.add(KEYWORD_SPACE);
		hashSet.add(BRACE_SPACE);
		hashSet.add(ARGUMENTCOMMA_SPACE);
		hashSet.add(BINARYOPERATOR_SPACE);
		hashSet.add(ASSIGNMENTOPERATOR_SPACE);
		hashSet.add(BLOCK_INDENT);
		hashSet.add(ARRAY_INDENT);
		hashSet.add(CASE_INDENT);
		hashSet.add(CASELABEL_INDENT);
		hashSet.add(ARGUMENT_INDENT);
		hashSet.add(PARAMETER_INDENT);
		hashSet.add(NEXTLINE_INDENT);
		return hashSet;
    }

    public static JavaFormatOption valueOfCustom(String name) {
        return (JavaFormatOption) Enum.valueOf(JavaFormatOption.class, name);
    }

    @Keep
	public static JavaFormatOption[] valuesCustom() {
        return (JavaFormatOption[]) values().clone();
    }

    @Keep
	public String getDescription() {
		if( isCN(Locale.getDefault())){
			return this.description2;
		}
		return this.description;
    }

	public static boolean isCN(Locale locale) {
        if (locale == null)return false;
        String language =locale.getLanguage();
        if (language == null)return false;
        return "zh".equals(language.toLowerCase(Locale.ENGLISH));
    }

	@Keep
	public String getExampleCode(boolean enable) {
        return enable ? this.newStyleText : this.oldStyleText;
    }

    @Keep
	public String getKey() {
        return "java_" + name();
    }

    @Keep
	public String getName() {
		if( isCN(Locale.getDefault())){
			return this.name2;
		}
        return this.name;
    }
}

