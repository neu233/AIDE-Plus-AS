package com.aide.codemodel.language.java;

import android.util.Log;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.collections.FunctionOfIntLong;
import com.aide.codemodel.api.collections.HashtableOfInt;
import com.aide.codemodel.api.collections.SetOfFileEntry;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;

import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;
import static javax.tools.StandardLocation.PLATFORM_CLASS_PATH;

/**
 * javax tools
 */
public class ECJJavaCodeCompiler implements com.aide.codemodel.api.abstraction.CodeCompiler {

	private Language language;
	private final Model model;

	public ECJJavaCodeCompiler(Model model, Language language) {
		this.model = model;
		this.language = language;
	}

	public void createClassWriter() {
		// TODO: Implement this method
	}

	@Override
	public void compile(List<SyntaxTree> list, boolean p) {
		for (SyntaxTree syntaxTree : list) {
			if (syntaxTree.getLanguage() == this.language) {
				try {
					compileAssembly(syntaxTree.getFile());
				}
				catch (Exception e) {
					return;
				}
			}
		}

	}

	@Override
	public void init(CodeModel codeModel) {
		//myModel = codeModel;
	}

    private static final String LOG_TAG = "ECJJavaCodeCompiler";
    private final FunctionOfIntLong caches = new FunctionOfIntLong();
    private final HashtableOfInt<List<File>> myPlatformClassPaths = new HashtableOfInt<>();
    private final HashtableOfInt<List<File>> myClassPaths = new HashtableOfInt<>();



    private void getClassPaths(List<File> platformClassPaths, List<File> classPaths, List<File> sourcePaths, FileEntry fileEntry) {
        boolean addedClassPaths = true;

		// contains -> Hw
        int assembly = fileEntry.getAssembly();
		if (caches.contains(assembly)) {
			// get -> v5
            platformClassPaths.addAll(myPlatformClassPaths.get(assembly));
            classPaths.addAll(myClassPaths.get(assembly));
            addedClassPaths = false;
        }

        if (addedClassPaths) {
			SetOfFileEntry files = model.fileSpace.getSolutionFiles();
            files.default_Iterator.init();
            while (files.default_Iterator.hasMoreElements()) {
				FileEntry file = files.default_Iterator.nextKey();
                if (file.isArchiveEntry()) {
                    file = file.getParentArchive();
                }

				// getFullName -> getFullNameString
				// getAssembly -> gn
				String fileName = file.getFullNameString();

                if (assembly == file.getAssembly() 
					|| model.fileSpace.isReferableFrom(model.fileSpace.getFileEntry(assembly), file)) {

                    if (fileName.equals("android.jar") ||
						fileName.equals("rt.jar") ||
						fileName.equals("core-lambda-stubs.jar")) {
						File rawFile = new File(file.getPathString());
                        if (!platformClassPaths.contains(rawFile)) {
                            platformClassPaths.add(rawFile);
                            Log.d(LOG_TAG, "Added Platform Jar " + rawFile.getPath());
                        }
                    } else {
                        if (fileName.endsWith(".jar")) {
							File rawFile = new File(file.getPathString());
                            if (!classPaths.contains(rawFile)) {
                                classPaths.add(rawFile);
                                Log.d(LOG_TAG, "Added Jar " + rawFile.getPath());
                            }
                        }
                    }
                }
            }

        }

		// getCheckedSolutionFiles -> KD
		SetOfFileEntry files = model.fileSpace.KD();
        files.default_Iterator.init();
        while (files.default_Iterator.hasMoreElements()) {
			FileEntry file = files.default_Iterator.nextKey();
            // getAssembly -> gn
			if (assembly == file.getAssembly() || model.fileSpace.isReferableFrom(model.fileSpace.getFileEntry(assembly), file)) {
                if (file.getFullNameString().endsWith(".java")) {
					File rawFile = new File(file.getPathString());
					if (!sourcePaths.contains(rawFile)) {
						sourcePaths.add(rawFile);
                        Log.d(LOG_TAG, "Added Source " + rawFile.getPath());
                    }
                }
            }
        }

        if (addedClassPaths) {
            caches.put(assembly, 0);

			// put -> VH
            myPlatformClassPaths.put(assembly, platformClassPaths);
            myClassPaths.put(assembly, classPaths);
        }
    }

    public void compile() throws Exception {
//        
//		var assembles = myModel.fileSpace.getAssembles();
//        assembles.default_Iterator.init();
//        while (assembles.default_Iterator.hasMoreElements()) {
//            compileAssembly(assembles.default_Iterator.nextKey());
//        }

    }

    private void compileAssembly(FileEntry fileEntry) throws Exception {
		String sourceLevel = model.fileSpace.getTargetVersion(fileEntry);
		String targetLevel = model.fileSpace.getTargetVersion(fileEntry);

		// getDestinationPath -> getReleaseOutputPath
		String destinationPath = model.fileSpace.getReleaseOutputPath(fileEntry);
        internalCompile(fileEntry, sourceLevel, targetLevel, destinationPath);
    }

    private void internalCompile(FileEntry fileEntry, String sourceLevel, String targetLevel, String destinationPath) throws Exception {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
			PrintWriter outputPrinter = new PrintWriter(output);
			EclipseCompiler compiler = new EclipseCompiler();
            DiagnosticListener<JavaFileObject> diagnosticListener = new DiagnosticListener<JavaFileObject>(){

				@Override
				public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
					int line = (int) diagnostic.getLineNumber();
					int column = (int) diagnostic.getColumnNumber();
					int endColumn = (int) (column + (diagnostic.getEndPosition() - diagnostic.getStartPosition()));
					String msg = diagnostic.getMessage(Locale.getDefault());
					Log.d(LOG_TAG, "compile diagnostic: (" + line + "," + column + "," + line + "," + endColumn + ") " + msg);
				}
			};

			ArrayList<String> options = new ArrayList<String>();
            options.add("-source");
            options.add(sourceLevel);
            options.add("-target");
            options.add(targetLevel);
            options.add("-encoding");
            options.add(model.fileSpace.getEncoding());

			StandardJavaFileManager fileSystem = compiler.getStandardFileManager(diagnosticListener, Locale.getDefault(), Charset.defaultCharset());
			ArrayList<File> platformClassPaths = new ArrayList<File>();
			ArrayList<File> classPaths = new ArrayList<File>();
			ArrayList<File> sourcePaths = new ArrayList<File>();
            getClassPaths(platformClassPaths, classPaths, sourcePaths, fileEntry);
            fileSystem.setLocation(PLATFORM_CLASS_PATH, platformClassPaths);
            fileSystem.setLocation(CLASS_PATH, classPaths);

			File[] files = null;
            List<File> asList = Arrays.<File>asList(files);
			fileSystem.setLocation(CLASS_OUTPUT, asList);

			JavaCompiler.CompilationTask task = compiler.getTask(
				outputPrinter,
				fileSystem,
				diagnosticListener,
				options,
				null,
				fileSystem.getJavaFileObjectsFromFiles(sourcePaths));

            if (!task.call()) {
                Log.d(LOG_TAG, "compile: error " + output);
                throw new Exception(output.toString());
            }
        } finally {
			if( output != null ){
				output.close();
			}
        }
    }
}//*/
