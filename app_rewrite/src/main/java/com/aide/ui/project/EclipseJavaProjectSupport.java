package com.aide.ui.project;


/*
public class EclipseJavaProjectSupport implements ProjectSupport {

	@Override
	public boolean Hw() {
		// TODO: Implement this method
		return false;
	}

	@Override
	public void J0(String string) {
		// TODO: Implement this method
	}

	@Override
	public boolean J8() {
		// TODO: Implement this method
		return false;
	}

	@Override
	public void Mr() {
		// TODO: Implement this method
	}

	@Override
	public void P8(String string, String string1) {
		// TODO: Implement this method
	}

	@Override
	public void SI(String string, ValueRunnable<String> valueRunnable) {
		// TODO: Implement this method
	}
	@Override
	public TemplateService.TemplateGroup[] VH() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public List<com.aide.ui.trainer.Course.File> XL() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public boolean Zo(String string) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean a8(String string) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean aM(String string) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public void buildProject(boolean p) {
		// TODO: Implement this method
	}

	@Override
	public void cn(List<String> list, boolean p) {
		// TODO: Implement this method
	}

	@Override
	public void ei(String string) {
		// TODO: Implement this method
	}

	@Override
	public boolean gW() {
		// TODO: Implement this method
		return false;
	}

	@Override
	public int getOpenProjectNameStringId(String string) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public List<String> getProductFlavors(String string) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public String getProjectAttributeHtmlString() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public void gn() {
		// TODO: Implement this method
	}

	@Override
	public boolean isPremium() {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean j3(String string) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public void j6() {
		// TODO: Implement this method
	}

	@Override
	public boolean lg() {
		// TODO: Implement this method
		return false;
	}

	@Override
	public EngineSolution makeEngineSolution() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public void nw(String string) {
		// TODO: Implement this method
	}

	@Override
	public int rN(String string) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public List<String> ro(String string) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public String sh(String string) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public String tp(String string) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public boolean u7(String string) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public String v5(String string) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public boolean vy(String string) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public int we(String string) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public String yS() {
		// TODO: Implement this method
		return null;
	}
	
	// isProjectHome -> isSupport
	@Override
	public boolean isSupport(@NonNull String rootPath) {
		return isRawFile(rootPath + "/.classpath") &&
			isRawFile(rootPath + "/.project");
	}

	private boolean isRawFile(String rootPath) {
		// TODO: Implement this method
		return false;
	}
	
	// load -> U2
	@Override
	public void U2(String projectHome, @NonNull Map<String, List<String>> libraries, @NonNull List<String> projects) {
		projects.add(projectHome);
		libraries.put(projectHome, getLibraries(projectHome));
	}

	@Override
	public boolean canRemoveLibrary(@NonNull String library) {
		return getLibraries(getProjectService().getProjectHome()).contains(library);
	}

	@Override
	public void removeLibrary(@NonNull String library) {
		String projectHome = getProjectService().getProjectHome();
		try {
			ClassPath configuration = new ClassPath().getConfiguration(projectHome + "/.classpath");
			List<ClassPath.Entry> classPathEntries = configuration
				.Zo;
			for (ClassPath.Entry entry : classPathEntries) {
				if (entry.isLibKind()) {
					String path = FileSystem.tryMakeRelative(projectHome, library);
					if (entry.getPath().equals(path)) {
						classPathEntries.remove(entry);
						break;
					}
				}
			}
			ClassPathFile.write(projectHome + "/.classpath", classPathEntries);
			App.getOpenFileService().syncFile(projectHome + "/.classpath");
		} catch (Exception e) {
			AppLog.e(e);
		}
	}
	
	// canAddLibrary -> FH
	@Override
	public boolean FH(@NonNull String library) {
		if (library.toLowerCase().endsWith(".jar")) {
			return !getLibraries(getProjectService().getProjectHome()).contains(library);
		}
		return false;
	}

	@Override
	public void addLibrary(@NonNull String library) {
		String projectHome = getProjectService().getProjectHome();
		try {
			var classPathEntries = new ClassPathFile()
				.syncGet(projectHome + "/.classpath")
				.getClassPathEntries();
			classPathEntries.add(new ClassPathEntry("lib", tryMakeRelative(projectHome, library), false));
			ClassPathFile.write(projectHome + "/.classpath", classPathEntries);
			//App.getOpenFileService().syncFile(projectHome+"/.classpath");
		} catch (Exception e) {
			AppLog.e(e);
		}
	}

	@NonNull
	@Override
	public List<String> getAddLibraryTypeList(@NonNull String rootPath) {
		return List.of("External Library");
	}

	@Override
	public void showAddLibraryDialog(@NonNull String rootPath, @NonNull String type) {
		MessageBox.dismiss();
		if (type.equals("External Library")) {
			File file = new File(rootPath + "/lib");
			if (!file.exists()) {
				Toast.makeText(getUI(), "The lib' directory does not exist.", Toast.LENGTH_SHORT).show();
				return;
			}
			var values = new ArrayList<String>();
			var libraries = getLibraries(rootPath);
			var files = file.listFiles();
			if (files != null) {
				for (File child : files) {
					if (child.isFile() && child.getName().toLowerCase().endsWith(".jar")) {
						if (!libraries.contains(child.getPath())) {
							values.add(child.getPath());
						}
					}
				}
			}
			if (values.isEmpty()) {
				Toast.makeText(getUI(), "All jars are already used as libraries.", Toast.LENGTH_SHORT).show();
				return;
			}
			var selected = new ArrayList<Boolean>();
			for (String value : values) {
				selected.add(true);
			}
			queryMultipleChoiceFromList(getUI(),
										"Add External Library",
										values,
										selected,
			value -> {
				for (Integer integer : value) {
					if (selected.get(integer)) {
						addLibrary(values.get(integer));
					}
				}
			});
		}
	}

	public static List<String> getLibraries(String path) {
		List<String> list = new ArrayList<>();
		try {
			ClassPath configureFile = new ClassPath().getConfiguration(path + "/.classpath");
			List<ClassPath.Entry> classPathEntries = configureFile.Zo;
			for (ClassPath.Entry entry : classPathEntries) {
				if (entry.isLibKind()) {
					list.add(entry.resolveFilePath(path));
				}
			}
		} catch (Exception e) {
			AppLog.e(e);
		}

		return list;
	}

	@NonNull
	@Override
	public String getProperties() {
		StringBuilder msg = new StringBuilder(("<b>Java Project:</b><br/><br/>" + getProjectService().getProjectHome() + "<br/><br/>") + "<i>Library JARs:</i><br/><br/>");
		List<String> libraries = getLibraries(getProjectService().getProjectHome());
		if (libraries.isEmpty()) {
			return msg + "&lt;none&gt;<br/><br/>";
		}
		for (String lib : libraries) {
			if (!isRawExists(lib)) {
				msg.append("(NOT FOUND) ");
			}
			msg.append(lib).append("<br/><br/>");
		}
		return msg.toString();
	}

	@Nullable
	@Override
	public Template[] getTemplates() {
		return new Template[]{
			new Template("Eclipse",
						 "Java Console",
						 "Eclipse/Java",
						 "MyConsole",
						 "Java",
						 true,
						 R.drawable.ic_launcher_java,
						 "app.zip",
						 new String[]{
							 ".classpath",
							 ".project",
							 "Main.java"}
						 )
		};
	}

	@NonNull
	@Override
	public Solution getSolution() {
		var projects = new ArrayList<SolutionProject>();
		var entries = new ClassPathFile()
			.syncGet(getProjectService().getProjectHome() + "/.classpath")
			.getClassPathEntries();
		projects.add(createAppProject(getProjectService().getProjectHome(), entries));
		projects.add(createJavaSdkProject());
		projects.add(createJavaLambdaProject());

		for (var entry : entries) {
			if (entry.isLib()) {
				projects.add(createLibraryProject(
								 getProjectService().getProjectHome(),
								 entry,
								 entries));
			}
		}

		return new Solution("UTF-8", Languages.getFilePatterns(), projects);
	}

	private SolutionProject createLibraryProject(String rootPath, ClassPathEntry entry, List<ClassPathEntry> entries) {
		String path = resolvePath(rootPath, entry.getPath());
		var files = new ArrayList<SolutionFile>();
		files.add(new SolutionFile(
					  path,
					  "Java Binary",
					  "",
					  false));
		var list = new ArrayList<String>();
		for (var pathEntry : entries) {
			if (pathEntry.isLib()) {
				list.add(pathEntry.getFileName());
			}
		}
		list.add("rt.jar");
		return new SolutionProject(entry.getFileName(),
								   path, path,
								   list, files,
								   false,
								   "17",
								   "17",
								   "",
								   "", true);
	}

	private SolutionProject createAppProject(String rootPath, List<ClassPathEntry> entries) {
		String debugPath = getBuildPath(rootPath, "debug") + "/classes";
		String releasePath = getBuildPath(rootPath, "release") + "/classes";
		boolean isDebug = getProjectService().getFlavor().equals("debug");

		var files = new ArrayList<SolutionFile>();
		var list = new ArrayList<String>();
		for (var entry : entries) {
			if (entry.isSrc()) {
				files.add(new SolutionFile(
							  resolvePath(rootPath, entry.getPath()),
							  "Java",
							  "",
							  false));
			}

			if (entry.isLib())
				list.add(entry.getFileName());

			if (entry.isOutput()) {
				String path = resolvePath(rootPath, entry.getPath());
				debugPath = path + "/debug/classes";
				releasePath = path + "/release/classes";
			}
		}
		list.add(rootPath);
		list.add("rt.jar");
		list.add("core-lambda-stubs.jar");
		return new SolutionProject(
			rootPath,
			rootPath,
			rootPath,
			list,
			files,
			true,
			"17",
			"17",
			debugPath,
			releasePath,
			isDebug);
	}


	public static String getBuildPath(String rootPath, String variant) {
		var entries = new ClassPathFile()
			.syncGet(getProjectService().getProjectHome() + "/.classpath")
			.getClassPathEntries();
		for (var entry : entries) {
			if (entry.isOutput()) {
				String path = resolvePath(rootPath, entry.getPath());
				return path + "/" + variant;
			}
		}
		return rootPath + "/" + variant;
	}

	private SolutionProject createJavaSdkProject() {
		String javaSdkHome = AppSettings.getJavaSdkFile().getAbsolutePath();
		var files = new ArrayList<SolutionFile>();
		files.add(new SolutionFile(
					  javaSdkHome,
					  "Java Binary",
					  "",
					  false));
		List<String> subList = new ArrayList<>();
		subList.add("rt.jar");
		return new SolutionProject(
			"rt.jar",
			javaSdkHome,
			"rt.jar",
			subList,
			files,
			false,
			"17",
			"17",
			"",
			"",
			false);
	}

	private SolutionProject createJavaLambdaProject() {
		String javaSdkHome = Apply.get().foundFilePath("core-lambda-stubs.jar");
		var files = new ArrayList<SolutionFile>();
		files.add(new SolutionFile(
					  javaSdkHome,
					  "Java Binary",
					  "",
					  false));
		List<String> subList = new ArrayList<>();
		subList.add("core-lambda-stubs.jar");
		return new SolutionProject(
			"core-lambda-stubs.jar",
			javaSdkHome,
			"core-lambda-stubs.jar",
			subList,
			files,
			false,
			"17",
			"17",
			"",
			"",
			false);
	}

	@Override
	public void build(boolean full, boolean run) {
		App.getJavaBuildService().build(full, run);
	}

	@Override
	public boolean isBuilding() {
		return BuildServiceCollect.javaProjectBuildService.isBuilding();
	}
	
	public ProjectService getProjectService(){
		ServiceContainer.getMavenService();
	}
}
//*/
