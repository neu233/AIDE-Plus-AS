package com.aide.codemodel.language.java17;
import com.aide.codemodel.language.java.ProjectEnvironment;
import org.eclipse.jdt.internal.compiler.Compiler;


/**
 * 增量编译器
 * 实现逻辑及原理
 * 1. 重写 org.eclipse.jdt.internal.compiler.Compiler
 * 2. JavaCodeModel fillSyntaxTree时，创建 Ast
 * 3. Compiler去除注解处理器基本逻辑简单
 * 逻辑如下
 * 1. INameEnvironment environment 负责查找 类[ classfile | javafile]
 * 2. LookupEnvironment 用于填充符号信息
 * 3. LookupEnvironment可以rest
 * 4. 
 *
 */
public class IncrementalCompiler {
	Compiler compiler;
	ProjectEnvironment projectEnvironment;
}
