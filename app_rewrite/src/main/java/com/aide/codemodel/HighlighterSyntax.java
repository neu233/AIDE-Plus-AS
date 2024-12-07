package com.aide.codemodel;

import com.aide.codemodel.api.abstraction.Syntax;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.collections.ListOfInt;

public class HighlighterSyntax implements Syntax {

	@Override
	public int EQ(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public int FH(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public boolean I(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean KD(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public int Mr(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public int OW(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public boolean QX(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean Sf(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean U2(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean VH(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public int Ws(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public int XG(SyntaxTree syntaxTree) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public int XL(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public boolean Zo(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public int br(SyntaxTree syntaxTree, int i, int i1) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public boolean ca(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public String cb(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public boolean ef(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public int er(SyntaxTree syntaxTree, int i, int i1) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public boolean g3(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public int getImportEndLine(SyntaxTree syntaxTree) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public ListOfInt getImportNodePairs(SyntaxTree syntaxTree) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public ListOfInt getNamespaceNodePairs(SyntaxTree syntaxTree) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public String getString(int i) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public int getTokenLength(int i) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public int gn(SyntaxTree syntaxTree) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public boolean hasAttrDAIndex(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean hasAttrTarget(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean hasAttrType(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean hasAttrValue(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean hasAttrVariableSlot(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean isArguments(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean isBlock(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean isBooleanLiteral(int i) {
        return i == Styles.LiteralStyle || isLiteral(i);
	}

	@Override
	public boolean isChangedExpressionNode(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean isClassBody(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean isClassDeclaration(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean isComment(int i) {
		return i == Styles.PreprocessorStyle;
	}

	@Override
	public boolean isDocComment(int i) {
        return i == Styles.CommentStyle;
	}

	@Override
	public boolean isExpression(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean isFieldDeclaration(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean isIdentifier(int i) {
		return false;
	}

	@Override
	public boolean isLiteral(int i) {
		return i == Styles.StringStyle 
			|| i == Styles.NumberStyle
			|| i == Styles.LiteralStyle;
	}

	@Override
	public boolean isMemberDeclaration(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean isMethodDeclaration(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean isOperator(int i) {
        return i == Styles.OperatorStyle;
	}

	@Override
	public boolean isParameters(int i) {
		return false;
	}

	@Override
	public boolean isSeparator(int i) {
        return i == Styles.SeparatorStyle;
	}

	@Override
	public boolean isToken(int i) {
        return i == Styles.KeywordStyle;
	}

	@Override
	public boolean isTypeIdentifier(int i) {
        return i == Styles.TypeStyle;
	}

	@Override
	public int lg(SyntaxTree syntaxTree) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public int ro(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public String sh(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public boolean tp(int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public int vy(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public int we(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public boolean x9(SyntaxTree syntaxTree, int i) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean yS(int i) {
		// TODO: Implement this method
		return false;
	}

}
