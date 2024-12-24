/**
 * @Author ZeroAicy
 * @AIDE AIDE+
 */
package com.aide.codemodel.language.java;

import org.eclipse.jdt.internal.compiler.ast.*;

import com.aide.engine.SyntaxStyleType;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;

public class HighlighterASTVisitor extends SimpleHighlighterASTVisitor {

	public boolean visit( AllocationExpression allocationExpression, BlockScope scope ) {
		return true;
	}

	public boolean visit( AnnotationMethodDeclaration annotationTypeDeclaration, ClassScope classScope ) {
		return true;
	}

	public boolean visit( Argument argument, BlockScope scope ) {
		return true;
	}

	public boolean visit( Argument argument, ClassScope scope ) {
		return true;
	}

	public boolean visit( ArrayAllocationExpression arrayAllocationExpression, BlockScope scope ) {
		return true;
	}

	public boolean visit( ArrayInitializer arrayInitializer, BlockScope scope ) {
		return true;
	}

	public boolean visit( ArrayInitializer arrayInitializer, ClassScope scope ) {
		return true;
	}

	public boolean visit( ArrayQualifiedTypeReference arrayQualifiedTypeReference, BlockScope scope ) {
		return true;
	}

	public boolean visit( ArrayQualifiedTypeReference arrayQualifiedTypeReference, ClassScope scope ) {
		return true;
	}

	public boolean visit( ArrayReference arrayReference, BlockScope scope ) {

		return true;
	}

	public boolean visit( ArrayTypeReference arrayTypeReference, BlockScope scope ) {
		TextRange textRange = getTextRange(arrayTypeReference);
		addHighlighterInfo(SyntaxStyleType.TYPE_IDENTIFIER, textRange);
		return true;
	}

	public boolean visit( ArrayTypeReference arrayTypeReference, ClassScope scope ) {
		TextRange textRange = getTextRange(arrayTypeReference);
		addHighlighterInfo(SyntaxStyleType.TYPE_IDENTIFIER, textRange);
		return true;
	}

	public boolean visit( AssertStatement assertStatement, BlockScope scope ) {
		return true;
	}

	public boolean visit( Assignment assignment, BlockScope scope ) {
		return true;
	}

	public boolean visit( BinaryExpression binaryExpression, BlockScope scope ) {
		return true;
	}

	public boolean visit( Block block, BlockScope scope ) {
		return true;
	}

	public boolean visit( BreakStatement breakStatement, BlockScope scope ) {
		return true;
	}

	public boolean visit( CaseStatement caseStatement, BlockScope scope ) {
		return true;
	}

	public boolean visit( CastExpression castExpression, BlockScope scope ) {
		return true;
	}

	public boolean visit( CharLiteral charLiteral, BlockScope scope ) {
		// TextRange textRange = getTextRange(charLiteral);
		// addHighlighterInfo(SyntaxStyleType.TYPE_IDENTIFIER, textRange);
		return true;
	}

	public boolean visit( ClassLiteralAccess classLiteral, BlockScope scope ) {
		return true;
	}

	public boolean visit( Clinit clinit, ClassScope scope ) {
		return true;
	}

	public boolean visit( CompactConstructorDeclaration ccd, ClassScope scope ) {
		return true;
	}

	public boolean visit( CompoundAssignment compoundAssignment, BlockScope scope ) {
		return true;
	}

	public boolean visit( ConditionalExpression conditionalExpression, BlockScope scope ) {
		return true;
	}

	public boolean visit( ConstructorDeclaration constructorDeclaration, ClassScope scope ) {
		return true;
	}

	public boolean visit( ContinueStatement continueStatement, BlockScope scope ) {
		return true;
	}

	public boolean visit( DoStatement doStatement, BlockScope scope ) {
		return true;
	}

	public boolean visit( DoubleLiteral doubleLiteral, BlockScope scope ) {
		return true;
	}

	public boolean visit( EmptyStatement emptyStatement, BlockScope scope ) {
		return true;
	}

	public boolean visit( EqualExpression equalExpression, BlockScope scope ) {
		return true;
	}

	public boolean visit( ExplicitConstructorCall explicitConstructor, BlockScope scope ) {
		return true;
	}

	public boolean visit( ExtendedStringLiteral extendedStringLiteral, BlockScope scope ) {
		return true;
	}

	public boolean visit( FakeDefaultLiteral fakeDefaultLiteral, BlockScope scope ) {
		return true;
	}

	public boolean visit( FalseLiteral falseLiteral, BlockScope scope ) {
		return true;
	}

	public boolean visit( FieldDeclaration fieldDeclaration, MethodScope scope ) {
		return true;
	}

	public boolean visit( FieldReference fieldReference, BlockScope scope ) {
		return true;
	}

	public boolean visit( FieldReference fieldReference, ClassScope scope ) {
		return true;
	}

	public boolean visit( FloatLiteral floatLiteral, BlockScope scope ) {
		return true;
	}

	public boolean visit( ForStatement forStatement, BlockScope scope ) {
		return true;
	}

	public boolean visit( ForeachStatement forStatement, BlockScope scope ) {
		return true;
	}

	public boolean visit( GuardedPattern guardedPattern, BlockScope scope ) {
		return true;
	}

	public boolean visit( IfStatement ifStatement, BlockScope scope ) {
		return true;
	}

	public boolean visit( ImportReference importRef, CompilationUnitScope scope ) {
		TextRange textRange = getTextRange(importRef);
		addHighlighterInfo(SyntaxStyleType.NAMESPACE_IDENTIFIER, textRange);
		
		return true;
	}

	public boolean visit( Initializer initializer, MethodScope scope ) {
		return true;
	}

	public boolean visit( InstanceOfExpression instanceOfExpression, BlockScope scope ) {
		return true;
	}

	public boolean visit( IntLiteral intLiteral, BlockScope scope ) {
		return true;
	}

	public boolean visit( IntersectionCastTypeReference intersectionCastTypeReference, BlockScope scope ) {
		return true;
	}

	public boolean visit( IntersectionCastTypeReference intersectionCastTypeReference, ClassScope scope ) {
		return true;
	}

	public boolean visit( Javadoc javadoc, BlockScope scope ) {
		return true;
	}

	public boolean visit( Javadoc javadoc, ClassScope scope ) {
		return true;
	}

	public boolean visit( JavadocAllocationExpression expression, BlockScope scope ) {
		return true;
	}

	public boolean visit( JavadocAllocationExpression expression, ClassScope scope ) {
		return true;
	}

	public boolean visit( JavadocArgumentExpression expression, BlockScope scope ) {
		return true;
	}

	public boolean visit( JavadocArgumentExpression expression, ClassScope scope ) {
		return true;
	}

	public boolean visit( JavadocArrayQualifiedTypeReference typeRef, BlockScope scope ) {
		return true;
	}

	public boolean visit( JavadocArrayQualifiedTypeReference typeRef, ClassScope scope ) {
		return true;
	}

	public boolean visit( JavadocArraySingleTypeReference typeRef, BlockScope scope ) {
		return true;
	}

	public boolean visit( JavadocArraySingleTypeReference typeRef, ClassScope scope ) {
		return true;
	}

	public boolean visit( JavadocFieldReference fieldRef, BlockScope scope ) {
		return true;
	}

	public boolean visit( JavadocFieldReference fieldRef, ClassScope scope ) {
		return true;
	}

	public boolean visit( JavadocImplicitTypeReference implicitTypeReference, BlockScope scope ) {
		return true;
	}

	public boolean visit( JavadocImplicitTypeReference implicitTypeReference, ClassScope scope ) {
		return true;
	}

	public boolean visit( JavadocMessageSend messageSend, BlockScope scope ) {
		return true;
	}

	public boolean visit( JavadocMessageSend messageSend, ClassScope scope ) {
		return true;
	}

	public boolean visit( JavadocModuleReference moduleRef, BlockScope scope ) {
		return true;
	}

	public boolean visit( JavadocModuleReference moduleRef, ClassScope scope ) {
		return true;
	}

	public boolean visit( JavadocQualifiedTypeReference typeRef, BlockScope scope ) {
		return true;
	}

	public boolean visit( JavadocQualifiedTypeReference typeRef, ClassScope scope ) {
		return true;
	}

	public boolean visit( JavadocReturnStatement statement, BlockScope scope ) {
		return true;
	}

	public boolean visit( JavadocReturnStatement statement, ClassScope scope ) {
		return true;
	}

	public boolean visit( JavadocSingleNameReference argument, BlockScope scope ) {
		return true;
	}

	public boolean visit( JavadocSingleNameReference argument, ClassScope scope ) {
		return true;
	}

	public boolean visit( JavadocSingleTypeReference typeRef, BlockScope scope ) {
		return true;
	}

	public boolean visit( JavadocSingleTypeReference typeRef, ClassScope scope ) {
		return true;
	}

	public boolean visit( LabeledStatement labeledStatement, BlockScope scope ) {
		return true;
	}

	public boolean visit( LambdaExpression lambdaExpression, BlockScope blockscope ) {
		return true;
	}

	public boolean visit( LocalDeclaration localDeclaration, BlockScope scope ) {
		TextRange textRange = getTextRange(localDeclaration);
		addHighlighterInfo(SyntaxStyleType.TYPE_IDENTIFIER, textRange);
		return true;
	}

	public boolean visit( LongLiteral longLiteral, BlockScope scope ) {
		return true;
	}

	public boolean visit( MarkerAnnotation annotation, BlockScope scope ) {
		return true;
	}

	public boolean visit( MarkerAnnotation annotation, ClassScope scope ) {
		return true;
	}

	public boolean visit( MemberValuePair pair, BlockScope scope ) {
		return true;
	}

	public boolean visit( MemberValuePair pair, ClassScope scope ) {
		return true;
	}

	public boolean visit( MessageSend messageSend, BlockScope scope ) {
		return true;
	}

	public boolean visit( MethodDeclaration methodDeclaration, ClassScope scope ) {
		return true;
	}

	public boolean visit( ModuleDeclaration module, CompilationUnitScope scope ) {
		return true;
	}

	public boolean visit( NormalAnnotation annotation, BlockScope scope ) {
		return true;
	}

	public boolean visit( NormalAnnotation annotation, ClassScope scope ) {
		return true;
	}

	public boolean visit( NullLiteral nullLiteral, BlockScope scope ) {
		return true;
	}

	public boolean visit( OR_OR_Expression or_or_Expression, BlockScope scope ) {
		return true;
	}

	public boolean visit( ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, BlockScope scope ) {
		return true;
	}

	public boolean visit( ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, ClassScope scope ) {
		return true;
	}

	public boolean visit( ParameterizedSingleTypeReference parameterizedSingleTypeReference, BlockScope scope ) {
		return true;
	}

	public boolean visit( ParameterizedSingleTypeReference parameterizedSingleTypeReference, ClassScope scope ) {
		return true;
	}

	public boolean visit( Pattern patternExpression, BlockScope scope ) {
		return true;
	}

	public boolean visit( PostfixExpression postfixExpression, BlockScope scope ) {
		return true;
	}

	public boolean visit( PrefixExpression prefixExpression, BlockScope scope ) {
		return true;
	}

	public boolean visit( QualifiedAllocationExpression qualifiedAllocationExpression, BlockScope scope ) {
		return true;
	}

	public boolean visit( QualifiedNameReference qualifiedNameReference, BlockScope scope ) {
		TextRange textRange = getTextRange(qualifiedNameReference);
		addHighlighterInfo(SyntaxStyleType.TYPE_IDENTIFIER, textRange);
		return true;
	}

	public boolean visit( QualifiedNameReference qualifiedNameReference, ClassScope scope ) {
		TextRange textRange = getTextRange(qualifiedNameReference);
		addHighlighterInfo(SyntaxStyleType.TYPE_IDENTIFIER, textRange);
		
		return true;
	}

	public boolean visit( QualifiedSuperReference qualifiedSuperReference, BlockScope scope ) {
		return true;
	}

	public boolean visit( QualifiedSuperReference qualifiedSuperReference, ClassScope scope ) {
		return true;
	}

	public boolean visit( QualifiedThisReference qualifiedThisReference, BlockScope scope ) {
		return true;
	}

	public boolean visit( QualifiedThisReference qualifiedThisReference, ClassScope scope ) {
		return true;
	}

	public boolean visit( QualifiedTypeReference qualifiedTypeReference, BlockScope scope ) {
		TextRange textRange = getTextRange(qualifiedTypeReference);
		addHighlighterInfo(SyntaxStyleType.TYPE_IDENTIFIER, textRange);
		return true;
	}

	public boolean visit( QualifiedTypeReference qualifiedTypeReference, ClassScope scope ) {
		TextRange textRange = getTextRange(qualifiedTypeReference);
		addHighlighterInfo(SyntaxStyleType.TYPE_IDENTIFIER, textRange);
		return true;
	}

	public boolean visit( RecordComponent recordComponent, BlockScope scope ) {
		return true;
	}

	public boolean visit( RecordPattern recordPattern, BlockScope scope ) {
		return true;
	}

	public boolean visit( ReferenceExpression referenceExpression, BlockScope blockscope ) {
		return true;
	}

	public boolean visit( ReturnStatement returnStatement, BlockScope scope ) {
		return true;
	}

	public boolean visit( SingleMemberAnnotation annotation, BlockScope scope ) {
		return true;
	}

	public boolean visit( SingleMemberAnnotation annotation, ClassScope scope ) {
		return true;
	}

	public boolean visit( SingleNameReference singleNameReference, BlockScope scope ) {
		return true;
	}

	public boolean visit( SingleNameReference singleNameReference, ClassScope scope ) {
		return true;
	}

	public boolean visit( SingleTypeReference singleTypeReference, BlockScope scope ) {
		TextRange textRange = getTextRange(singleTypeReference);
		addHighlighterInfo(SyntaxStyleType.TYPE_IDENTIFIER, textRange);
		return true;
	}

	public boolean visit( SingleTypeReference singleTypeReference, ClassScope scope ) {
		TextRange textRange = getTextRange(singleTypeReference);
		addHighlighterInfo(SyntaxStyleType.TYPE_IDENTIFIER, textRange);
		return true;
	}

	public boolean visit( StringLiteral stringLiteral, BlockScope scope ) {
		return true;
	}

	public boolean visit( StringLiteralConcatenation literal, BlockScope scope ) {
		return true;
	}

	public boolean visit( StringTemplate expr, BlockScope scope1 ) {
		return true;
	}

	public boolean visit( SuperReference superReference, BlockScope scope ) {
		return true;
	}

	public boolean visit( SwitchExpression switchExpression, BlockScope blockscope ) {
		return true;
	}

	public boolean visit( SwitchStatement switchStatement, BlockScope scope ) {
		return true;
	}

	public boolean visit( SynchronizedStatement synchronizedStatement, BlockScope scope ) {
		return true;
	}

	public boolean visit( TemplateExpression expr, BlockScope scope1 ) {
		return true;
	}

	public boolean visit( ThisReference thisReference, BlockScope scope ) {
		return true;
	}

	public boolean visit( ThisReference thisReference, ClassScope scope ) {
		return true;
	}

	public boolean visit( ThrowStatement throwStatement, BlockScope scope ) {
		return true;
	}

	public boolean visit( TrueLiteral trueLiteral, BlockScope scope ) {
		return true;
	}

	public boolean visit( TryStatement tryStatement, BlockScope scope ) {
		return true;
	}

	public boolean visit( TypeDeclaration localTypeDeclaration, BlockScope scope ) {
		TextRange textRange = getTextRange(localTypeDeclaration);
		addHighlighterInfo(SyntaxStyleType.TYPE_IDENTIFIER, textRange);
		return true;
	}

	public boolean visit( TypeDeclaration memberTypeDeclaration, ClassScope scope ) {
		TextRange textRange = getTextRange(memberTypeDeclaration);
		addHighlighterInfo(SyntaxStyleType.TYPE_IDENTIFIER, textRange);
		return true;
	}

	public boolean visit( TypeDeclaration typeDeclaration, CompilationUnitScope scope ) {
		TextRange textRange = getTextRange(typeDeclaration);
		addHighlighterInfo(SyntaxStyleType.TYPE_IDENTIFIER, textRange);
		
		return true;
	}

	public boolean visit( TypeParameter typeParameter, BlockScope scope ) {
		return true;
	}

	public boolean visit( TypeParameter typeParameter, ClassScope scope ) {
		return true;
	}

	public boolean visit( TypePattern anyPattern, BlockScope scope ) {
		return true;
	}

	public boolean visit( UnaryExpression unaryExpression, BlockScope scope ) {
		return true;
	}

	public boolean visit( UnionTypeReference unionTypeReference, BlockScope scope ) {
		return true;
	}

	public boolean visit( UnionTypeReference unionTypeReference, ClassScope scope ) {
		return true;
	}

	public boolean visit( WhileStatement whileStatement, BlockScope scope ) {
		return true;
	}

	public boolean visit( Wildcard wildcard, BlockScope scope ) {
		return true;
	}

	public boolean visit( Wildcard wildcard, ClassScope scope ) {
		return true;
	}

	public boolean visit( YieldStatement yieldStatement, BlockScope scope ) {
		return true;
	}



	@Override
	public boolean visit( CompilationUnitDeclaration compilationUnitDeclaration, CompilationUnitScope scope ) {

		return super.visit(compilationUnitDeclaration, scope);
	}

}
