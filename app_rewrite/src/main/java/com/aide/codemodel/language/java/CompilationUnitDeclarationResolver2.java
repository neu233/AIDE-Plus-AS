package com.aide.codemodel.language.java;

import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;

public class CompilationUnitDeclarationResolver2 extends org.eclipse.jdt.internal.compiler.Compiler {

	public CompilationUnitDeclaration resolve9999( String pathString ) {
		// TODO: Implement this method
		return null;
	}

	static{
		if ( ZeroAicySetting.isEnableEnsureCapacity() ) 
			System.loadLibrary("EnsureCapacity");
	}

	ProjectEnvironment projecttEnvironment;
	public CompilationUnitDeclarationResolver2(
		ProjectEnvironment projecttEnvironment,
		INameEnvironment environment,
		IErrorHandlingPolicy policy,
		CompilerOptions compilerOptions,
		ICompilerRequestor requestor,
		IProblemFactory problemFactory ) {
		super(environment, policy, compilerOptions, requestor, problemFactory);

		this.projecttEnvironment = projecttEnvironment;

	}


	public CompilationUnitDeclaration resolve3( ICompilationUnit compilationunit ) {
		CompilationUnitDeclaration unit = dietParse2(compilationunit);
		// binding resolution
		this.lookupEnvironment.completeTypeBindings();

		// resolve
		resolve2(unit);


		return unit;
	}

	// 自动 buildTypeBindings
	private CompilationUnitDeclaration dietParse2( ICompilationUnit sourceUnit ) throws AbortCompilation {
		CompilationResult unitResult = new CompilationResult(sourceUnit, 0, 1, this.options.maxProblemsPerUnit);
		CompilationUnitDeclaration parsedUnit;
		try {
			LookupEnvironment lookupEnvironment = this.lookupEnvironment;
			// dietParse
			parsedUnit = this.parser.dietParse(sourceUnit, unitResult);

			// initial type binding creation
			lookupEnvironment.buildTypeBindings(parsedUnit, null);

			ImportReference currentPackage = parsedUnit.currentPackage;
			if ( currentPackage != null ) {
				unitResult.recordPackageName(currentPackage.tokens);
			}
		}
		catch (AbortCompilation a) {
			// best effort to find a way for reporting this problem:
			if ( a.compilationResult == null )
				a.compilationResult = unitResult;
			throw a;
		}
		return parsedUnit;
	}

	private void resolve2( CompilationUnitDeclaration unit ) {
		boolean verifyMethods = true;
		boolean analyzeCode = true;
		boolean generateCode = !true;

		this.lookupEnvironment.unitBeingCompleted = unit;

		// 解析
		this.parser.getMethodBodies(unit);
		if ( unit.scope != null ) {
			// fault in fields & methods
			unit.scope.faultInTypes();
			if ( unit.scope != null && verifyMethods ) {
				// http://dev.eclipse.org/bugs/show_bug.cgi?id=23117
				// verify inherited methods
				unit.scope.verifyMethods(this.lookupEnvironment.methodVerifier());
			}
			// type checking
			unit.resolve();

			// flow analysis
			if ( analyzeCode ) unit.analyseCode();

			// code generation
			if ( generateCode ) unit.generateCode();

			// finalize problems (suppressWarnings)
			unit.finalizeProblems();
		}

		this.lookupEnvironment.unitBeingCompleted = null;
	}

	@Override
	public CompilationUnitDeclaration resolve( CompilationUnitDeclaration unit, ICompilationUnit sourceUnit, boolean verifyMethods, boolean analyzeCode, boolean generateCode ) {
		// TODO: Implement this method
		return super.resolve(unit, sourceUnit, verifyMethods, analyzeCode, generateCode);
	}

}
