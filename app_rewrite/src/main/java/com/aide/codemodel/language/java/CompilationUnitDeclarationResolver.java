package com.aide.codemodel.language.java;


import com.aide.common.AppLog;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.PlainPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.UnresolvedReferenceBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.HashtableOfType;

public class CompilationUnitDeclarationResolver extends Compiler {


	static{
		System.loadLibrary("EnsureCapacity");
	}


	public static class ResetCompilationUnitDeclaration extends ASTVisitor {

		@Override
		public boolean visit(CompilationUnitDeclaration compilationUnitDeclaration, CompilationUnitScope scope) {
			compilationUnitDeclaration.scope = null;
			compilationUnitDeclaration.localTypes.clear();

			return super.visit(compilationUnitDeclaration, scope);
		}
		/*
		 public boolean visit(AllocationExpression allocationExpression, BlockScope scope) {

		 }

		 public boolean visit(AnnotationMethodDeclaration annotationTypeDeclaration, ClassScope classScope) {}

		 public boolean visit(Argument argument, BlockScope scope) {}

		 public boolean visit(Argument argument, ClassScope scope) {}

		 public boolean visit(ArrayAllocationExpression arrayAllocationExpression, BlockScope scope) {}

		 public boolean visit(ArrayInitializer arrayInitializer, BlockScope scope) {}

		 public boolean visit(ArrayInitializer arrayInitializer, ClassScope scope) {}

		 public boolean visit(ArrayQualifiedTypeReference arrayQualifiedTypeReference, BlockScope scope) {}

		 public boolean visit(ArrayQualifiedTypeReference arrayQualifiedTypeReference, ClassScope scope) {}

		 public boolean visit(ArrayReference arrayReference, BlockScope scope) {}

		 public boolean visit(ArrayTypeReference arrayTypeReference, BlockScope scope) {}

		 public boolean visit(ArrayTypeReference arrayTypeReference, ClassScope scope) {}

		 public boolean visit(AssertStatement assertStatement, BlockScope scope) {}

		 public boolean visit(Assignment assignment, BlockScope scope) {}

		 public boolean visit(BinaryExpression binaryExpression, BlockScope scope) {}

		 public boolean visit(Block block, BlockScope scope) {}

		 public boolean visit(BreakStatement breakStatement, BlockScope scope) {}

		 public boolean visit(CaseStatement caseStatement, BlockScope scope) {}

		 public boolean visit(CastExpression castExpression, BlockScope scope) {}

		 public boolean visit(CharLiteral charLiteral, BlockScope scope) {}

		 public boolean visit(ClassLiteralAccess classLiteral, BlockScope scope) {}

		 public boolean visit(Clinit clinit, ClassScope scope) {}

		 public boolean visit(CompactConstructorDeclaration ccd, ClassScope scope) {}

		 public boolean visit(CompoundAssignment compoundAssignment, BlockScope scope) {}

		 public boolean visit(ConditionalExpression conditionalExpression, BlockScope scope) {}

		 public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {}

		 public boolean visit(ContinueStatement continueStatement, BlockScope scope) {}

		 public boolean visit(DoStatement doStatement, BlockScope scope) {}

		 public boolean visit(DoubleLiteral doubleLiteral, BlockScope scope) {}

		 public boolean visit(EmptyStatement emptyStatement, BlockScope scope) {}

		 public boolean visit(EqualExpression equalExpression, BlockScope scope) {}

		 public boolean visit(ExplicitConstructorCall explicitConstructor, BlockScope scope) {}

		 public boolean visit(ExtendedStringLiteral extendedStringLiteral, BlockScope scope) {}

		 public boolean visit(FakeDefaultLiteral fakeDefaultLiteral, BlockScope scope) {}

		 public boolean visit(FalseLiteral falseLiteral, BlockScope scope) {}

		 public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {}

		 public boolean visit(FieldReference fieldReference, BlockScope scope) {}

		 public boolean visit(FieldReference fieldReference, ClassScope scope) {}

		 public boolean visit(FloatLiteral floatLiteral, BlockScope scope) {}

		 public boolean visit(ForStatement forStatement, BlockScope scope) {}

		 public boolean visit(ForeachStatement forStatement, BlockScope scope) {}

		 public boolean visit(GuardedPattern guardedPattern, BlockScope scope) {}

		 public boolean visit(IfStatement ifStatement, BlockScope scope) {}

		 public boolean visit(ImportReference importRef, CompilationUnitScope scope) {}

		 public boolean visit(Initializer initializer, MethodScope scope) {}

		 public boolean visit(InstanceOfExpression instanceOfExpression, BlockScope scope) {}

		 public boolean visit(IntLiteral intLiteral, BlockScope scope) {}

		 public boolean visit(IntersectionCastTypeReference intersectionCastTypeReference, BlockScope scope) {}

		 public boolean visit(IntersectionCastTypeReference intersectionCastTypeReference, ClassScope scope) {}

		 public boolean visit(Javadoc javadoc, BlockScope scope) {}

		 public boolean visit(Javadoc javadoc, ClassScope scope) {}

		 public boolean visit(JavadocAllocationExpression expression, BlockScope scope) {}

		 public boolean visit(JavadocAllocationExpression expression, ClassScope scope) {}

		 public boolean visit(JavadocArgumentExpression expression, BlockScope scope) {}

		 public boolean visit(JavadocArgumentExpression expression, ClassScope scope) {}

		 public boolean visit(JavadocArrayQualifiedTypeReference typeRef, BlockScope scope) {}

		 public boolean visit(JavadocArrayQualifiedTypeReference typeRef, ClassScope scope) {}

		 public boolean visit(JavadocArraySingleTypeReference typeRef, BlockScope scope) {}

		 public boolean visit(JavadocArraySingleTypeReference typeRef, ClassScope scope) {}

		 public boolean visit(JavadocFieldReference fieldRef, BlockScope scope) {}

		 public boolean visit(JavadocFieldReference fieldRef, ClassScope scope) {}

		 public boolean visit(JavadocImplicitTypeReference implicitTypeReference, BlockScope scope) {}

		 public boolean visit(JavadocImplicitTypeReference implicitTypeReference, ClassScope scope) {}

		 public boolean visit(JavadocMessageSend messageSend, BlockScope scope) {}

		 public boolean visit(JavadocMessageSend messageSend, ClassScope scope) {}

		 public boolean visit(JavadocModuleReference moduleRef, BlockScope scope) {}

		 public boolean visit(JavadocModuleReference moduleRef, ClassScope scope) {}

		 public boolean visit(JavadocQualifiedTypeReference typeRef, BlockScope scope) {}

		 public boolean visit(JavadocQualifiedTypeReference typeRef, ClassScope scope) {}

		 public boolean visit(JavadocReturnStatement statement, BlockScope scope) {}

		 public boolean visit(JavadocReturnStatement statement, ClassScope scope) {}

		 public boolean visit(JavadocSingleNameReference argument, BlockScope scope) {}

		 public boolean visit(JavadocSingleNameReference argument, ClassScope scope) {}

		 public boolean visit(JavadocSingleTypeReference typeRef, BlockScope scope) {}

		 public boolean visit(JavadocSingleTypeReference typeRef, ClassScope scope) {}

		 public boolean visit(LabeledStatement labeledStatement, BlockScope scope) {}

		 public boolean visit(LambdaExpression lambdaExpression, BlockScope blockScope) {}

		 public boolean visit(LocalDeclaration localDeclaration, BlockScope scope) {}

		 public boolean visit(LongLiteral longLiteral, BlockScope scope) {}

		 public boolean visit(MarkerAnnotation annotation, BlockScope scope) {}

		 public boolean visit(MarkerAnnotation annotation, ClassScope scope) {}

		 public boolean visit(MemberValuePair pair, BlockScope scope) {}

		 public boolean visit(MemberValuePair pair, ClassScope scope) {}

		 public boolean visit(MessageSend messageSend, BlockScope scope) {}

		 public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {}

		 public boolean visit(ModuleDeclaration module, CompilationUnitScope scope) {}

		 public boolean visit(NormalAnnotation annotation, BlockScope scope) {}

		 public boolean visit(NormalAnnotation annotation, ClassScope scope) {}

		 public boolean visit(NullLiteral nullLiteral, BlockScope scope) {}

		 public boolean visit(OR_OR_Expression or_or_Expression, BlockScope scope) {}

		 public boolean visit(ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, BlockScope scope) {}

		 public boolean visit(ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, ClassScope scope) {}

		 public boolean visit(ParameterizedSingleTypeReference parameterizedSingleTypeReference, BlockScope scope) {}

		 public boolean visit(ParameterizedSingleTypeReference parameterizedSingleTypeReference, ClassScope scope) {}

		 public boolean visit(Pattern patternExpression, BlockScope scope) {}

		 public boolean visit(PostfixExpression postfixExpression, BlockScope scope) {}

		 public boolean visit(PrefixExpression prefixExpression, BlockScope scope) {}

		 public boolean visit(QualifiedAllocationExpression qualifiedAllocationExpression, BlockScope scope) {}

		 public boolean visit(QualifiedNameReference qualifiedNameReference, BlockScope scope) {}

		 public boolean visit(QualifiedNameReference qualifiedNameReference, ClassScope scope) {}

		 public boolean visit(QualifiedSuperReference qualifiedSuperReference, BlockScope scope) {}

		 public boolean visit(QualifiedSuperReference qualifiedSuperReference, ClassScope scope) {}

		 public boolean visit(QualifiedThisReference qualifiedThisReference, BlockScope scope) {}

		 public boolean visit(QualifiedThisReference qualifiedThisReference, ClassScope scope) {}

		 public boolean visit(QualifiedTypeReference qualifiedTypeReference, BlockScope scope) {}

		 public boolean visit(QualifiedTypeReference qualifiedTypeReference, ClassScope scope) {}

		 public boolean visit(RecordComponent recordComponent, BlockScope scope) {}

		 public boolean visit(RecordPattern recordPattern, BlockScope scope) {}

		 public boolean visit(ReferenceExpression referenceExpression, BlockScope blockScope) {}

		 public boolean visit(ReturnStatement returnStatement, BlockScope scope) {}

		 public boolean visit(SingleMemberAnnotation annotation, BlockScope scope) {}

		 public boolean visit(SingleMemberAnnotation annotation, ClassScope scope) {}

		 public boolean visit(SingleNameReference singleNameReference, BlockScope scope) {}

		 public boolean visit(SingleNameReference singleNameReference, ClassScope scope) {}

		 public boolean visit(SingleTypeReference singleTypeReference, BlockScope scope) {}

		 public boolean visit(SingleTypeReference singleTypeReference, ClassScope scope) {}

		 public boolean visit(StringLiteral stringLiteral, BlockScope scope) {}

		 public boolean visit(StringLiteralConcatenation literal, BlockScope scope) {}

		 public boolean visit(StringTemplate expr, BlockScope scope1) {}

		 public boolean visit(SuperReference superReference, BlockScope scope) {}

		 public boolean visit(SwitchExpression switchExpression, BlockScope blockScope) {}

		 public boolean visit(SwitchStatement switchStatement, BlockScope scope) {}

		 public boolean visit(SynchronizedStatement synchronizedStatement, BlockScope scope) {}

		 public boolean visit(TemplateExpression expr, BlockScope scope1) {}

		 public boolean visit(ThisReference thisReference, BlockScope scope) {}

		 public boolean visit(ThisReference thisReference, ClassScope scope) {}

		 public boolean visit(ThrowStatement throwStatement, BlockScope scope) {}

		 public boolean visit(TrueLiteral trueLiteral, BlockScope scope) {}

		 public boolean visit(TryStatement tryStatement, BlockScope scope) {}

		 public boolean visit(TypeDeclaration localTypeDeclaration, BlockScope scope) {}

		 public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {}

		 public boolean visit(TypeDeclaration typeDeclaration, CompilationUnitScope scope) {}

		 public boolean visit(TypeParameter typeParameter, BlockScope scope) {}

		 public boolean visit(TypeParameter typeParameter, ClassScope scope) {}

		 public boolean visit(TypePattern anyPattern, BlockScope scope) {}

		 public boolean visit(UnaryExpression unaryExpression, BlockScope scope) {}

		 public boolean visit(UnionTypeReference unionTypeReference, BlockScope scope) {}

		 public boolean visit(UnionTypeReference unionTypeReference, ClassScope scope) {}

		 public boolean visit(WhileStatement whileStatement, BlockScope scope) {}

		 public boolean visit(Wildcard wildcard, BlockScope scope) {}

		 public boolean visit(Wildcard wildcard, ClassScope scope) {}

		 public boolean visit(YieldStatement yieldStatement, BlockScope scope) {}
		 */
	}

	public static IErrorHandlingPolicy getHandlingPolicy() {

		// passes the initial set of files to the batch oracle (to avoid finding more than once the same units when case insensitive match)
		return new IErrorHandlingPolicy() {
			@Override
			public boolean proceedOnErrors() {
				return !true; // stop if there are some errors
			}
			@Override
			public boolean stopOnFirstError() {
				return false;
			}
			@Override
			public boolean ignoreAllErrors() {
				return false;
			}
		};
	}
	@Override
	public void initializeParser() {
		//*
		this.problemReporter = new ProblemReporter(getHandlingPolicy(), this.options, new DefaultProblemFactory()){
			@Override
			public void duplicateTypes(CompilationUnitDeclaration compUnitDecl, TypeDeclaration typeDecl) {
				ICompilationUnit compilationUnit = compUnitDecl.compilationResult.compilationUnit;
				char[] fileName = compilationUnit.getFileName();

				AppLog.println_d("filepath %s\n", String.valueOf(fileName));
				// AppLog.println_d("typeDecl %s\n", String.valueOf(typeDecl));

				AppLog.println_e(Thread.currentThread().getStackTrace());
				AppLog.println_e("-------------------------------\n");
				super.duplicateTypes(compUnitDecl, typeDecl);
			}
		};
		//*/
		super.initializeParser();
	}

	ProjectEnvironment projecttEnvironment;
	public CompilationUnitDeclarationResolver(
		ProjectEnvironment projecttEnvironment,
		INameEnvironment environment,
		IErrorHandlingPolicy policy,
		CompilerOptions compilerOptions,
		ICompilerRequestor requestor,
		IProblemFactory problemFactory) {
		super(environment, policy, compilerOptions, requestor, problemFactory);

		this.projecttEnvironment = projecttEnvironment;

	}


	/***********************************************************************************************************************************************/



	// private ResetCompilationUnitDeclaration resetASTVisitor = new ResetCompilationUnitDeclaration();

	private Set<String> sourcePaths = new HashSet<>();
	public void setSourceFiles(Set<String> sourcePaths) {
		if (sourcePaths == null) return;

		this.sourcePaths.clear();
		this.sourcePaths.addAll(sourcePaths);
		AppLog.println_d("setSourceFiles size: %s", sourcePaths.size());
	}

	// 自动 buildTypeBindings
	private static CompilationUnitDeclaration dietParse2(String filePath, ICompilationUnit sourceUnit, CompilationUnitDeclarationResolver resolver) throws AbortCompilation {
		CompilationResult unitResult = new CompilationResult(sourceUnit, 0, 1, resolver.options.maxProblemsPerUnit);
		CompilationUnitDeclaration parsedUnit;

		try {
			LookupEnvironment lookupEnvironment = resolver.lookupEnvironment;
			// dietParse
			parsedUnit = resolver.parser.dietParse(sourceUnit, unitResult);
			resolver.unitDeclCacheMap2.put(filePath, parsedUnit);

			// initial type binding creation
			lookupEnvironment.buildTypeBindings(parsedUnit, null);

			ImportReference currentPackage = parsedUnit.currentPackage;
			if (currentPackage != null) {
				unitResult.recordPackageName(currentPackage.tokens);
			}
		}
		catch (AbortCompilation a) {
			// best effort to find a way for reporting this problem:
			if (a.compilationResult == null)
				a.compilationResult = unitResult;
			throw a;
		}
		return parsedUnit;
	}

	private void resolve2(CompilationUnitDeclaration unit) {
		boolean verifyMethods = true;
		boolean analyzeCode = true;
		boolean generateCode = !true;

		this.lookupEnvironment.unitBeingCompleted = unit;

		// 解析
		this.parser.getMethodBodies(unit);
		if (unit.scope != null) {
			// fault in fields & methods
			unit.scope.faultInTypes();
			if (unit.scope != null && verifyMethods) {
				// http://dev.eclipse.org/bugs/show_bug.cgi?id=23117
				// verify inherited methods
				unit.scope.verifyMethods(this.lookupEnvironment.methodVerifier());
			}
			// type checking
			unit.resolve();

			// flow analysis
			if (analyzeCode) unit.analyseCode();

			// code generation
			if (generateCode) unit.generateCode();

			// finalize problems (suppressWarnings)
			unit.finalizeProblems();
		}

		this.lookupEnvironment.unitBeingCompleted = null;
	}

	/**
	 * 测试为什么不能重复填充符号表
	 * 即，以文件为单位的 增量更新
	 */
	Map<String, CompilationUnitDeclaration> unitDeclCacheMap2 = new HashMap<>();
	Set<String> resolvedFilePathSet = new HashSet<>();

	/*public CompilationUnitDeclaration updateFile(String filePath) throws Exception {
		return updateFile(filePath, this.projecttEnvironment.getCompilationUnit(filePath), true);
	}*/

	/*public CompilationUnitDeclaration updateFile(String filePath, boolean resolve) throws Exception {
	 InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath));
	 return updateFile(filePath, reader, false);
	 }*/

	public boolean print = true;
	public CompilationUnitDeclaration updateFile(String filePath, ICompilationUnit compilationUnit, boolean resolve) throws Exception {
		this.sourcePaths.add(filePath);

		Map<String, CompilationUnitDeclaration> unitDeclCacheMap2 = this.unitDeclCacheMap2;

		// 移除旧Ast
		CompilationUnitDeclaration parsedUnitOld = unitDeclCacheMap2.remove(filePath);

		// 怎么可以减少此代码块的调用
		if (parsedUnitOld != null 
			|| unitDeclCacheMap2.containsKey(filePath)) {
			// 重置
			LookupEnvironment lookupEnvironment = this.lookupEnvironment;
			lookupEnvironment.reset();
			// 此时没有resolve的unit了
			this.resolvedFilePathSet.clear();

			// 依赖重新 buildTypeBindings
			for (Map.Entry<String, CompilationUnitDeclaration> other : unitDeclCacheMap2.entrySet()) {
				String key = other.getKey();
				CompilationUnitDeclaration value = other.getValue();

				dietParse2(key, value);
				// dietParse3(key, value);

			}
		}

		// dietParse2 and buildTypeBindings
		CompilationUnitDeclaration dietParse2 = dietParse2(filePath, compilationUnit, this);

		// binding resolution
		this.lookupEnvironment.completeTypeBindings();

		if (resolve) {

			resolve2(dietParse2);
			resolvedFilePathSet.add(filePath);

		} else {
			resolvedFilePathSet.remove(filePath);
		}

		return dietParse2;
		// 填充符号表的核心操作
		// this.lookupEnvironment.buildTypeBindings(null,  null);
		// this.lookupEnvironment.completeTypeBindings();
	}



	private CompilationUnitDeclaration dietParse2(String filePath, CompilationUnitDeclaration other) {
		CompilationResult unitResult = other.compilationResult;
		ICompilationUnit sourceUnit = unitResult.compilationUnit;
		// dietParse
		CompilationUnitDeclaration parsedUnit;
		try {
			parsedUnit = this.parser.dietParse(sourceUnit, unitResult);
			this.unitDeclCacheMap2.put(filePath, parsedUnit);
			LookupEnvironment lookupEnvironment = this.lookupEnvironment;
			// initial type binding creation
			lookupEnvironment.buildTypeBindings(parsedUnit, null);

			ImportReference currentPackage = parsedUnit.currentPackage;
			if (currentPackage != null) {
				unitResult.recordPackageName(currentPackage.tokens);
			}
		}
		catch (AbortCompilation a) {
			// best effort to find a way for reporting this problem:
			if (a.compilationResult == null)
				a.compilationResult = unitResult;
			throw a;
		}
		return parsedUnit;
	}


	public void reset(boolean resetOnlyLookupEnvironment) {
		if (resetOnlyLookupEnvironment) {
			this.lookupEnvironment.reset();
			this.unitDeclCacheMap2.clear();
			return;
		}
		super.reset();
	}

	/****************************************************************************************************************************************************************************/

	 static void removeScope(TypeDeclaration enclosingType) {
//		if (enclosingType.scope != null) System.out.println("scope");
//		if (enclosingType.binding != null) System.out.println("binding");
//		if (enclosingType.initializerScope != null) System.out.println("initializerScope");
//		if (enclosingType.staticInitializerScope != null) System.out.println("staticInitializerScope");
//
		if (enclosingType == null) {
			return;
		}
		// enclosingType.superclass = null;

		/*
		 enclosingType.binding = null;
		 enclosingType.scope = null;
		 enclosingType.initializerScope = null;
		 enclosingType.staticInitializerScope = null;
		 */

		TypeDeclaration[] memberTypes = enclosingType.memberTypes;
		if (memberTypes != null) {
			for (TypeDeclaration memberType : memberTypes) {
				removeScope(memberType);
			}
		}

		TypeReference[] superInterfaces = enclosingType.superInterfaces;
		if (superInterfaces != null) {
			for (TypeReference superInterface : superInterfaces) {
				superInterface.resolvedType = null;
			}
		}

		Annotation[] annotations = enclosingType.annotations;
		if (annotations != null) {
			for (Annotation annotation : annotations) {
				annotation.recipient = null;
			}
		}
		// 方法 
		AbstractMethodDeclaration[] methods = enclosingType.methods;
		if (methods != null) {
			for (AbstractMethodDeclaration method : methods) {
				method.scope = null;
				method.binding = null;
			}
		}
	}

	 static void removeOldTypes(CompilationUnitDeclaration parsedUnitOld, CompilationUnitDeclarationResolver resolver, boolean removeNull) {
		char[][] currentPackageName = parsedUnitOld.currentPackage == null ? CharOperation.NO_CHAR_CHAR : parsedUnitOld.currentPackage.tokens;


		// 默认包
		PlainPackageBinding fPackage;

		// 计算 enclosingType PackageBinding
		if (currentPackageName == CharOperation.NO_CHAR_CHAR) {
			fPackage = resolver.lookupEnvironment.defaultPackage;
		} else {
			fPackage = resolver.lookupEnvironment.createPlainPackage(currentPackageName);
		}

		TypeDeclaration[] types = parsedUnitOld.types;
		int typeLength = (types == null) ? 0 : types.length;

		for (int i = 0; i < typeLength; i++) {
			TypeDeclaration enclosingType = types[i];
			char[] name = enclosingType.name;
			HashtableOfType knownTypes = fPackage.knownTypes;

			// 从封闭类PackageBinding中置空自己
			if (knownTypes != null) {
				ReferenceBinding typeReferenceBinding = knownTypes.get(name);
				if (typeReferenceBinding != null) {
					knownTypes.put(name, null);
				}					
			}

			// 封闭类绑定
			SourceTypeBinding enclosingTypeBinding = enclosingType.binding;
			if (enclosingTypeBinding == null) {
				continue;
			}
			PackageBinding enclosingTypePackageBinding = enclosingTypeBinding.fPackage;
			HashtableOfType enclosingTypePackageBindingKnownTypes = enclosingTypePackageBinding.knownTypes;
			if (enclosingTypePackageBinding == null 
				|| enclosingTypePackageBindingKnownTypes == null 
				|| enclosingType.memberTypes == null) {
				continue;
			}

			char[][] enclosingTypeCompoundName = enclosingTypeBinding.compoundName;
			char[] enclosingTypeName = enclosingTypeCompoundName[enclosingTypeCompoundName.length - 1];
			// 处理 enclosingType中的成员类
			for (TypeDeclaration memberType : enclosingType.memberTypes) {
				// 处理具有 enclosingType的type
				char[] memberTypeName = CharOperation.concat(enclosingTypeName, memberType.name, '$');

				// className[className.length - 1] =	memberTypeName;
				// put
				enclosingTypePackageBindingKnownTypes.put(memberTypeName, null);
			}
			// 类可能必须移除
			removeNullTypeBinding(enclosingTypePackageBinding);

		}

		// 移除null
		if (removeNull) {
			removeNullTypeBinding(fPackage);
		}

	}

	public static ReferenceBinding getType0(PlainPackageBinding fPackage, char[] name) {
		if (fPackage.knownTypes == null)
			return null;
		return fPackage.knownTypes.get(name);
	}

	/**
	 * Test if this package (or any of its incarnations in case of a SplitPackageBinding) has recorded
	 * an actual, resolved type of the given name (based on answers from getType0()).
	 * Useful for clash detection.
	 */
	public static boolean hasType0Any(PlainPackageBinding fPackage, char[] name) {
		ReferenceBinding type0 = getType0(fPackage, name);
		return type0 != null && type0.isValidBinding() 
			&& !(type0 instanceof UnresolvedReferenceBinding);
	}
	private static void removeNullTypeBinding(PackageBinding fPackage) {
		HashtableOfType knownTypes = fPackage.knownTypes;
		if (knownTypes == null) {
			return;
		}
		HashtableOfType newHashtable = new HashtableOfType(knownTypes.size());

		for (int i = knownTypes.keyTable.length; --i >= 0;) {
			char[] currentKey = knownTypes.keyTable[i];
			if (currentKey != null) {
				ReferenceBinding value = knownTypes.valueTable[i];
				if (value == null) {
					continue;
				}
				int problemId = value.problemId();
				if (problemId == LookupEnvironment.NotFound) {
					continue;
				}
				newHashtable.put(currentKey, value);
			}
		}
		fPackage.knownTypes = newHashtable;
	}

}
