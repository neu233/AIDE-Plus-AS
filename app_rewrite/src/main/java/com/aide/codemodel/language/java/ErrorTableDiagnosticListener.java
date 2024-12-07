package com.aide.codemodel.language.java;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic;
import com.aide.codemodel.api.ErrorTable;
import com.aide.codemodel.api.FileSpace;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.abstraction.Language;
import java.util.Locale;

public class ErrorTableDiagnosticListener implements DiagnosticListener<JavaFileObject>{
	ErrorTable errorTable;


	private FileEntry fileEntry;
	EclipseJavaCodeCompiler eclipseJavaCodeCompiler;

	private Language language;

	public ErrorTableDiagnosticListener(EclipseJavaCodeCompiler eclipseJavaCodeCompiler){
		this.eclipseJavaCodeCompiler = eclipseJavaCodeCompiler;
		language = this.eclipseJavaCodeCompiler.language;

		this.errorTable = eclipseJavaCodeCompiler.errorTable;
		this.fileEntry = eclipseJavaCodeCompiler.fileEntry;

	}

	@Override
	public void report(Diagnostic<? extends JavaFileObject> diagnostic){
		switch ( diagnostic.getKind() ){
			case ERROR:{

					String name = diagnostic.getSource().getName();
					FileEntry entry = fileEntry.getEntry(name);
					int lineNumber = (int) diagnostic.getLineNumber();
					int columnNumber = (int)diagnostic.getColumnNumber();

					// int startPosition = (int) diagnostic.getStartPosition();
					// diagnostic.getCode();
					String message = diagnostic.getMessage(Locale.getDefault());
					System.out.println(name + " -> " + message);
					
					this.errorTable.addSemanticError(
						entry, 
						language, 
						lineNumber, 
						columnNumber,
						lineNumber, columnNumber, "ecj: -> addSemanticError -> " + message, 20);
					
					this.errorTable.Hw(
						entry, 
						language, 
						lineNumber, 
						columnNumber,
						lineNumber, columnNumber, "ecj: -> Hw -> " + message, 20);
					this.errorTable.lg(
						entry, 
						language, 
						lineNumber, 
						columnNumber,
						lineNumber, columnNumber, "ecj: -> lg -> " + message, 20);
					
				}
				break;
			case WARNING:{
					String name = diagnostic.getSource().getName();
					FileEntry entry = fileEntry.getEntry(name);
					int lineNumber = (int) diagnostic.getLineNumber();
					int columnNumber = (int)diagnostic.getColumnNumber();
					String message = diagnostic.getMessage(Locale.getDefault());
					
					System.out.println(name + " -> " + message);
					
					// int startPosition = (int) diagnostic.getStartPosition();
					// diagnostic.getCode();


					this.errorTable.addSemanticWarning(
						entry, 
						language, 
						lineNumber, 
						columnNumber,
						lineNumber, columnNumber, "ecj: " + message, 26);


				}
				break;
		}
	}

}
