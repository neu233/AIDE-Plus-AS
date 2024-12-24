/**
 * @Author ZeroAicy
 * @AIDE AIDE+
 */
package com.aide.codemodel.language.java;

import com.aide.codemodel.api.HighlighterType;
import com.aide.engine.SyntaxStyleType;
import java.util.List;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.TextBlock;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.util.Util;

public class SimpleHighlighterASTVisitor extends ASTVisitor {

	public static class TextRange {
		int startLine;
		int startColumn;
		int endLine;
		int endColumn;

		public TextRange( int startLine, int startColumn, int endLine, int endColumn ) {
			this.startLine = startLine;
			this.startColumn = startColumn;
			this.endLine = endLine;
			this.endColumn = endColumn;
		}
	}
	public TextRange getTextRange( ASTNode node ) {
		int sourceStart = node.sourceStart();
		int sourceEnd = node.sourceEnd();
		return getTextRange(sourceStart, sourceEnd);
	}

	public TextRange getTextRange( int sourceStart, int sourceEnd ) {
		int[] lineEnds = this.lineEnds;
		int startLine = sourceStart >= 0
			? Util.getLineNumber(sourceStart, lineEnds, 0, lineEnds.length - 1)
			: 0;
		int startColumn = sourceStart >= 0
			? Util.searchColumnNumber(lineEnds, startLine, sourceStart)
			: 0;

		int endLine = sourceEnd >= 0
			? Util.getLineNumber(sourceEnd, lineEnds, 0, lineEnds.length - 1)
			: 0;
			
		int endColumn = sourceEnd >= 0
			? Util.searchColumnNumber(lineEnds, endLine, sourceEnd) + 1
			: 0;

		TextRange textRange = new TextRange(startLine, startColumn, endLine, endColumn);
		return textRange;
	}


	protected int[] lineEnds;
	protected CompilationResult unitResult;

	protected List<EclipseJavaCodeAnalyzer2.HighlighterInfo> highlighterInfos;

	public void init( CompilationUnitDeclaration unit, List<EclipseJavaCodeAnalyzer2.HighlighterInfo> infos ) {
		this.highlighterInfos = infos;
		this.unitResult = unit.compilationResult;
		this.lineEnds = unitResult.getLineSeparatorPositions();
	}

	public void addHighlighterInfo( SyntaxStyleType syntaxStyleType, TextRange textRange ) {
		this.addHighlighterInfo(syntaxStyleType.ordinal(), textRange);
	}

	public void addHighlighterInfo( int highlighterType, TextRange textRange ) {
		this.highlighterInfos.add(new EclipseJavaCodeAnalyzer2.HighlighterInfo(highlighterType, textRange.startLine, textRange.startColumn, textRange.endLine, textRange.endColumn));
	}

	@Override
	public boolean visit( StringLiteral stringLiteral, BlockScope scope ) {

		if ( stringLiteral instanceof TextBlock ) {
			addHighlighterInfo(HighlighterType.TextBlock, getTextRange(stringLiteral));
		}
		return super.visit(stringLiteral, scope);
	}

	@Override
	public boolean visit( MethodDeclaration methodDeclaration, ClassScope scope ) {
		// methodDeclaration
		Argument[] arguments = methodDeclaration.arguments;
		if ( arguments != null ) {
			int argumentLength = arguments.length;
			for ( int i = 0; i < argumentLength; i++ ) {
				addHighlighterInfo(HighlighterType.ArgumentIdentifier, getTextRange(arguments[i]));
			}

		}
		// 语句
		Statement[] statements = methodDeclaration.statements;
		if ( statements != null ) {
			int statementsLength = statements.length;
			for ( int i = 0; i < statementsLength; i++ )
				statements[i].traverse(this, methodDeclaration.scope);
		}
		this.endVisit(methodDeclaration, scope);	
		// 
		return false;
		// return super.visit(methodDeclaration, scope);
	}



}
