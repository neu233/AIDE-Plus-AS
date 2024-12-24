package com.aide.codemodel.api.util;

import com.aide.codemodel.api.ArrayType;
import com.aide.codemodel.api.ClassType;
import com.aide.codemodel.api.Entity;
import com.aide.codemodel.api.EntitySpace;
import com.aide.codemodel.api.ErrorTable;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.Namespace;
import com.aide.codemodel.api.ParameterizedType;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.Type;
import com.aide.codemodel.api.abstraction.Syntax;
import com.aide.codemodel.api.collections.SetOf;
import com.aide.codemodel.api.excpetions.UnknownEntityException;
import com.aide.codemodel.language.java.JavaCodeAnalyzer;
import com.aide.codemodel.language.java.JavaTypeSystem;
import com.aide.common.AppLog;
import java.util.Arrays;

public class SyntaxTreeUtils {

	
	/**
	 * 
	 */
	public static Type getVarNodeAttrType(SyntaxTree syntaxTree, int varParentNode) {
		// varParentNode[TYPE_NAME]

		if (SyntaxTreeUtils.isVarNode(syntaxTree, varParentNode) 
			&& syntaxTree.hasAttrType(varParentNode)) {
			return syntaxTree.getAttrType(varParentNode);
		}
		return null;
	}
	
	/**
	 * 计算局部变量使用 var 的变量类型[Java]
	 */
	public static Entity getVarAttrType(JavaCodeAnalyzer.a javaCodeAnalyzer$a, int varNode) throws UnknownEntityException {
		SyntaxTree syntaxTree = JavaCodeAnalyzer.a.er(javaCodeAnalyzer$a);
		// 不是var
		if (!syntaxTree.isIdentifierNode(varNode)) {
			return null;
		}

		String varNodeIdentifierString = syntaxTree.getIdentifierString(varNode);

		if (!"var".equals(varNodeIdentifierString)) {
			return null;
		}

		// varNode必须getParentNode两次才是[JavaCodeAnalyzer$a::d8]中的node
		// 获得var节点的父节点
		// varNode [IDENTIFIER]

		// [TYPE_NAME]
		int varParentNode = syntaxTree.getParentNode(varNode);
		// [VARIABLE_DECLARATION]
		int varRootNode = syntaxTree.getParentNode(varParentNode);

		// 字段变量不支持var
		if (SyntaxTreeUtils.isFields(syntaxTree, varRootNode)) {
			// 变量名称节点
			// [VARIABLE]
			int varNameNode = syntaxTree.getChildNode(varRootNode, 3);
			// [VARIABLE]子节点[IDENTIFIER]
			int errorNode = syntaxTree.getChildNode(varNameNode, 0);

			String errorMsg = "Field </C>" + syntaxTree.getIdentifierString(errorNode) + "<//C> cannot use the var keyword";

			SyntaxTreeUtils.addSemanticError(syntaxTree, errorNode, errorMsg);

			// UnknownEntityException
			throw new UnknownEntityException();
		}

		if (SyntaxTreeUtils.isVariableDeclaration(syntaxTree, varRootNode)) {
			// [JavaCodeAnalyzer$a::d8]先计算 变量的类型
			// 也即会调用此方法，无论是否解析出来
			// 都会遍历子节点，解析右侧计算表达式类型

			int parentNodeCount = syntaxTree.getChildCount(varRootNode);
			int expressionNode = -1;

			// 计算右侧表达式类型
			for (int childNodeIndex = 3; childNodeIndex < parentNodeCount; childNodeIndex += 2) {
				int childNode = syntaxTree.getChildNode(varRootNode, childNodeIndex);

				if (syntaxTree.getChildCount(childNode) > 2) {
					// JavaCodeAnalyzer$a::fY()
					expressionNode = syntaxTree.getChildNode(childNode, 3);
					JavaCodeAnalyzer.a.Zo(javaCodeAnalyzer$a, expressionNode, null);
					break;
				}
			}
			// 打印 expressionNode
			if (expressionNode != -1) {
				// 没有找到 expressionNode
				final Type expressionNodeType = syntaxTree.getAttrType(expressionNode);
				if (expressionNodeType != null && !"null".equals(expressionNodeType.getFullyQualifiedNameString())) {

					if (!expressionNodeType.isNamespace()) {
						// 解析后的 attrType带泛型
						// 但从 JavaCodeAnalyzer$a::Ej或者Od 之后被剔除泛型了
						// 此处会被覆盖所以 无用
						// 拦截覆盖了，所以必须declareAttrType
						// 由getVarAttrType拦截 varParentNode[TYPE_NAME]的类型
						syntaxTree.declareAttrType(varParentNode, expressionNodeType);
						// 不能是 16 17 20 6～10 22～25
						// 为var添加高亮 高亮为type
						syntaxTree.declareAttrReferenceKind(varNode, 30, expressionNodeType);
					}

					return expressionNodeType;
				}
			}

			// 变量名称节点
			// [VARIABLE]
			int varNameNode = syntaxTree.getChildNode(varRootNode, 3);
			// [VARIABLE]子节点[IDENTIFIER]
			int errorNode = syntaxTree.getChildNode(varNameNode, 0);

			String errorMsg = "Variable </C>" + syntaxTree.getIdentifierString(errorNode) + "<//C> might not have been initialized" + "<//C>";
			SyntaxTreeUtils.addError(syntaxTree, errorNode, errorMsg);

			// UnknownEntityException
			throw new UnknownEntityException();
		}

		// foreach语句
		if (SyntaxTreeUtils.isForeachStatement(syntaxTree, varRootNode)) {
			// type name
			int parentNodeCount = syntaxTree.getChildCount(varRootNode);

			int expressionNode = -1;

			// 查找 右侧表达式
			// 因为 foreachStatement 右侧优先被计算因此不必计算

			for (int childNodeIndex = 1; childNodeIndex < parentNodeCount; childNodeIndex ++) {
				int childNode = syntaxTree.getChildNode(varRootNode, childNodeIndex);

				// 查找 冒号
				if (SyntaxTreeUtils.isColon(syntaxTree, childNode)) {
					// 获取foreach右侧表达式
					expressionNode = syntaxTree.getChildNode(varRootNode, childNodeIndex + 1);
					// 处理匿名内部类
					if (SyntaxTreeUtils.isAnonymousClassCreation(syntaxTree, expressionNode)) {
						expressionNode = syntaxTree.getChildNode(expressionNode, 2);
					}
					break;
				}
			}

			if (expressionNode != -1) {
				// 没有找到 expressionNode
				Type expressionNodeType = syntaxTree.getAttrType(expressionNode);

				if (expressionNodeType != null && !"null".equals(expressionNodeType.getFullyQualifiedNameString())) {
					if (expressionNodeType.isArrayType()) {
						//JavaTypeSystem javaTypeSystem = JavaCodeAnalyzer$a.yS(javaCodeAnalyzer$a);
						ArrayType arrayType = (ArrayType)expressionNodeType;
						expressionNodeType = arrayType.getElementType();
					} else if (expressionNodeType.isParameterizedType()) {
						ParameterizedType parameterizedType = (ParameterizedType)expressionNodeType;
						Type[] absoluteArgumentTypes = parameterizedType.getAbsoluteArgumentTypes();
						if (absoluteArgumentTypes == null || absoluteArgumentTypes.length == 0) {
							// expressionNodeType = Object;
							// 怎么获取Object类呢？？？
							Model model = syntaxTree.getModel();
							EntitySpace entitySpace = model.entitySpace;
							Namespace zh = entitySpace.zh("java", "lang");
							return zh.accessMemberClassType(syntaxTree.getFile(), syntaxTree.getLanguage(), model.identifierSpace.get("Object"), true, 0, JavaCodeAnalyzer.a.Mr(javaCodeAnalyzer$a).lg());

						} else if (absoluteArgumentTypes.length == 1) {
							expressionNodeType = absoluteArgumentTypes[0];
						} else {
							int errorNode = expressionNode;
							ErrorTable errorTable = syntaxTree.getModel().errorTable;
							// addSemanticError
							errorTable.addSemanticError(syntaxTree.getFile(), 
										  syntaxTree.getLanguage(), 
										  syntaxTree.getStartLine(errorNode), 
										  syntaxTree.getStartColumn(errorNode), 
										  syntaxTree.getEndLine(errorNode), 
										  syntaxTree.getEndColumn(errorNode), 
										  "具有多个泛型类型 </C>" + syntaxTree.getIdentifierString(errorNode) + "<//C>", 20);
							// UnknownEntityException
							throw new UnknownEntityException();
						}
					} else if (expressionNodeType.isClassType()) {
						// 提前处理 ANONYMOUS_CLASS_CREATION了
						/*while( "(anonymous)".equals( expressionNodeType.getNameString())){
						 ClassType classType = (ClassType)expressionNodeType;
						 expressionNodeType = classType.getSuperType();
						 }*/
						SetOf<Type> allSuperTypes = ((ClassType) expressionNodeType).getAllSuperTypes();
						SetOf<Type>.Iterator default_Iterator = allSuperTypes.default_Iterator;
						default_Iterator.init();
						JavaTypeSystem javaTypeSystem = JavaCodeAnalyzer.a.yS(javaCodeAnalyzer$a);
						while (default_Iterator.hasMoreElements()) {
							Type superType = (Type) default_Iterator.nextKey();
							if (superType.isParameterizedType() && ((ParameterizedType)superType).getClassType() == javaTypeSystem.wc(syntaxTree.getFile())) {
								Type type = ((ParameterizedType)superType).getAbsoluteArgumentTypes()[0];
								if (type != null) {
									expressionNodeType = type;
									if (type.isParameterType()) {
										// 未知ParameterType
										Model model = syntaxTree.getModel();
										EntitySpace entitySpace = model.entitySpace;
										Namespace zh = entitySpace.zh("java", "lang");
										expressionNodeType = zh.accessMemberClassType(syntaxTree.getFile(), syntaxTree.getLanguage(), model.identifierSpace.get("Object"), true, 0, JavaCodeAnalyzer.a.Mr(javaCodeAnalyzer$a).lg());
									}
									break;
								}
							}
						}
					} else {
						// 变量名称节点
						// [VARIABLE]
						int varNameNode = syntaxTree.getChildNode(varRootNode, 3);
						// [VARIABLE]子节点[IDENTIFIER]
						int errorNode = syntaxTree.getChildNode(varNameNode, 0);

						String errorMsg = "Unknown entity </C>" + expressionNodeType.getFullyQualifiedNameString() + "<//C>";
						SyntaxTreeUtils.addSemanticError(syntaxTree, errorNode, errorMsg);
						// UnknownEntityException
						throw new UnknownEntityException();

					}

					if (expressionNodeType != null && !expressionNodeType.isNamespace()) {
						// 解析后的 attrType带泛型
						// 但从 JavaCodeAnalyzer$a::Ej或者Od 之后被剔除泛型了
						// 此处会被覆盖所以 无用
						// 拦截覆盖了，所以必须declareAttrType
						// 由getVarAttrType拦截 varParentNode[TYPE_NAME]的类型
						syntaxTree.declareAttrType(varParentNode, expressionNodeType);
						// ParameterizedType不能是 16 17 20 6～10 22～25
						if (expressionNodeType.isParameterizedType()) {
							syntaxTree.declareAttrReferenceKind(varNode, 30, expressionNodeType);
						} else {
							// 为var添加高亮 高亮为type
							syntaxTree.declareAttrReferenceKind(varNode, 10, expressionNodeType);

						}
					}

					//System.out.println("varRootNode[解析右侧表达式后]");
					//SyntaxTreeUtils.printNode(syntaxTree, varRootNode);

					return expressionNodeType;
				}
			}
		}
		AppLog.println_e("未知表达式: ");
		SyntaxTreeUtils.printNode(syntaxTree, varRootNode);

		throw new UnknownEntityException();
	}

	public static void addSemanticError(SyntaxTree syntaxTree, int errorNode, String errorMsg) {
		
		ErrorTable errorTable = syntaxTree.getModel().errorTable;
		// addSemanticError
		errorTable.addSemanticError(syntaxTree.getFile(), 
					  syntaxTree.getLanguage(), 
					  syntaxTree.getStartLine(errorNode), 
					  syntaxTree.getStartColumn(errorNode), 
					  syntaxTree.getEndLine(errorNode), 
					  syntaxTree.getEndColumn(errorNode), 
					  errorMsg, 20);
		
	}
	public static void addError(SyntaxTree syntaxTree, int errorNode, String errorMsg) {

		ErrorTable errorTable = syntaxTree.getModel().errorTable;
		errorTable.Hw(syntaxTree.getFile(), 
					  syntaxTree.getLanguage(), 
					  syntaxTree.getStartLine(errorNode), 
					  syntaxTree.getStartColumn(errorNode), 
					  syntaxTree.getEndLine(errorNode), 
					  syntaxTree.getEndColumn(errorNode), 
					  errorMsg, 12);

	}

	
	public static boolean isFields(SyntaxTree syntaxTree, int varRootNode) {
		return syntaxTree.getSyntaxTag(varRootNode) == 126;
	}
	public static boolean isVariableDeclaration(SyntaxTree syntaxTree, int varRootNode) {
		return syntaxTree.getSyntaxTag(varRootNode) == 151;
	}
	public static boolean isForeachStatement(SyntaxTree syntaxTree, int varRootNode) {
		return syntaxTree.getSyntaxTag(varRootNode) == 211;
	}
	
	public static boolean isColon(SyntaxTree syntaxTree, int varRootNode) {
		return syntaxTree.getSyntaxTag(varRootNode) == 26;
	}

	public static boolean isAnonymousClassCreation(SyntaxTree syntaxTree, int varRootNode) {
		return syntaxTree.getSyntaxTag(varRootNode) == 177;
	}
	
	/**
	 * 打印节点起始行列
	 */
	public static void printlnNodeAttr(SyntaxTree syntaxTree, String nodeName, int varNode) {
		System.out.print(nodeName);

		System.out.print(" StartLine " + syntaxTree.getStartLine(varNode));
		System.out.print(" StartColumn " + syntaxTree.getStartColumn(varNode));
		System.out.print(" EndLine " + syntaxTree.getEndLine(varNode));
		System.out.print(" EndColumn " + syntaxTree.getEndColumn(varNode));
		System.out.println();
	}

	public static String getIndent(int indent) {
        char[] tab = new char[indent];
        Arrays.fill(tab, '\t');
        return new String(tab);
    }
	/**
	 * 递归打印 AIDE语法树
	 */
	public static void printNode(SyntaxTree syntaxTree, int node) {
		printNode(syntaxTree, node, 0);
	}
	public static void printNode(SyntaxTree syntaxTree, int node, int indent) {
		if( node == -1 ){
			return;
		}
		
		Syntax syntax = syntaxTree.getLanguage().getSyntax();
		// 计算缩进
		String indentString = getIndent(indent);
		int syntaxTag = syntaxTree.getSyntaxTag(node);
		// 打印缩进
		System.out.print(indentString);
		System.out.print(syntax.getString(syntaxTag));
		System.out.printf(" [id: %s]", node);

		if (syntaxTree.hasAttrType(node)) {
			Type attrType = syntaxTree.getAttrType(node);
			System.out.printf(" [attr-type: %s]", attrType);	
		}
		
		if( syntax.isIdentifier(syntaxTag)){
			String nodeIdentifierString = syntaxTree.getIdentifierString(node);
			if (nodeIdentifierString != null && nodeIdentifierString.length() != 0) {
				System.out.printf(" [Identifier: %s]", nodeIdentifierString);
			}			
		}
		// syntaxTree.sh(node)[getDeclareAttrReferenceKind] declareAttrReferenceKind
		try {
			if (syntaxTree.isIdentifierNode(node)) {
				int attrReferenceKind = syntaxTree.getAttrReferenceKind(node);
				if (attrReferenceKind != 0) {
					System.out.printf(" [attrReferenceKind: %s]", attrReferenceKind);
				}
				int attrReferenceNode = syntaxTree.getAttrReferenceNode(node);
				if (attrReferenceNode != 0) {
					Entity entity = syntaxTree.getModel().entitySpace.getEntity(attrReferenceNode);
					System.out.printf(" [attrReferenceNode: %s]", entity.getFullyQualifiedNameString());
				}
				
			}			
		}
		catch (Throwable e) {

		}


		System.out.println();
		System.out.println();

		int childCount = syntaxTree.getChildCount(node);
		for (int index = 0; index < childCount; index++) {
			int childNode = syntaxTree.getChildNode(node, index);
			if (childNode != -1) {
				printNode(syntaxTree, childNode, indent + 1);
			}
		}
	}
	/**
	 * 判断type name是否是var
	 */
	public static boolean isVarNode(SyntaxTree syntaxTree, int varParentNode) {
		// varParentNode[TYPE_NAME]
		if (syntaxTree.getChildCount(varParentNode) < 1) {
			return false;
		}
		int varNode = syntaxTree.getChildNode(varParentNode, 0);
		if (!syntaxTree.isIdentifierNode(varNode)) {
			return false;
		}
		// 是var Node
		String varNodeIdentifierString = syntaxTree.getIdentifierString(varNode);
		if ("var".equals(varNodeIdentifierString)) {
			return true;
		}
		return false;
	}


	/**
	 * 非abstract方法[接口]
	 */
	public static boolean isNoInterfaceAbstractMethod(SyntaxTree syntaxTree, int nodeIndex) {

		for (int i = 0, childCount = syntaxTree.getChildCount(nodeIndex); i < childCount; i++) {
			int syntaxTag = syntaxTree.getSyntaxTag(syntaxTree.getChildNode(nodeIndex, i));
			// abstract
			if (syntaxTag == 95) {
				return false;
			}
			// default || static
			if (syntaxTag == 90
				|| syntaxTag == 86) {
				return true;
			}
		}
		return false;
	}

	public int EQ(int syntaxTag) {
		// 5511177 [ 1 4 12 13 19 21 23 ]
		int newValue;  
		switch (syntaxTag) {  
				// final	
			case 75: 
				// 896 = 0x380 = 1 << 7 | 1 << 8 | 1 << 9  
				newValue = 896; // 0x380, 1 << 7 | 1 << 8 | 1 << 9  
				break;
				// static
			case 86: 
				// 64 = 0x40 = 1 << 6  
				newValue = 64; // 0x40, 1 << 6  
				break;
				// synchronized
			case 104: 
				// 2048 = 0x800 = 1 << 11  
				newValue = 2048; // 0x800, 1 << 11  
				break;
				// @
			case 115: 
				// 536870912 = 0x20000000 = 1 << 29  
				newValue = 0x20000000; // 1 << 29  
				break;
				// native
			case 83: 
				// 524288 = 0x80000 = 1 << 19  
				newValue = 524288; // 0x80000, 1 << 19  
				break;
				// public
			case 84: 
				newValue = 1; // 1 << 0  
				break;
				// private
			case 94: 
				newValue = 4; // 1 << 2  
				break;
				// abstract
			case 95: 
				// 16384 = 0x4000 = 1 << 14  
				newValue = 16384; // 0x4000, 1 << 14  
				break;
				// strictfp
			case 97: 
				// 8192 = 0x2000 = 1 << 13  
				newValue = 8192; // 0x2000, 1 << 13  
				break;
				// volatile
			case 98: 
				// 1024 = 0x400 = 1 << 10  
				newValue = 1024; // 0x400, 1 << 10  
				break;
				// protected
			case 100: 
				newValue = 8; // 1 << 3  
				break;
				// transient
			case 101: 
				// 4096 = 0x1000 = 1 << 12  
				newValue = 4096; // 0x1000, 1 << 12  
				break;
			default: 
				newValue = 0;  
				break;
		}
		// 如果需要，可以在这里将 newValue 赋值给 syntaxTag 或者其他变量  
		syntaxTag = newValue;

        return syntaxTag;
    }
}
