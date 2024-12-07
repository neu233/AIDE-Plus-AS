package com.aide.ui.build;
import abcd.o8;
import com.aide.common.MessageBox;
import com.aide.common.ValueRunnable;
import com.aide.ui.AppPreferences;
import com.aide.ui.ServiceContainer;
import com.aide.ui.build.android.n;
import com.aide.ui.build.java.RunJavaActivity;
import com.aide.ui.build.java.RunTrainerJavaActivity;
import com.aide.ui.build.packagingservice.IExternalPackagingServiceListener;
import com.aide.ui.project.JavaGradleProjectSupport;
import com.aide.ui.services.ErrorService;
import java.io.File;
import java.util.List;
import java.util.Map;
import com.aide.ui.services.ProjectService;
import com.aide.ui.project.AndroidProjectSupport;
import io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface;
import com.aide.ui.project.internal.GradleTools;
import com.aide.ui.util.BuildGradle;
import com.aide.ui.build.android.SigningService;
import com.aide.ui.MainActivity;

public class JavaGradleProjectBuildService implements IBuildService, abcd.o8 {

	public static JavaProjectBuildService javaProjectBuildService = new JavaProjectBuildService();

	@Override
	public a VH() {
		return o8.a.JAVA;
	}

	@Override
	public int j6() {
		return javaProjectBuildService.j6();
	}

	@Override
	public void DW() {
		javaProjectBuildService.DW();
	}

	@Override
	public void FH(boolean p) {
		javaProjectBuildService.FH(p);
	}

	@Override
	public void Hw(String string) {
		javaProjectBuildService.Hw(string);
	}

	@Override
	public void Zo() {
		javaProjectBuildService.Zo();
	}

	@Override
	public void gn() {
		javaProjectBuildService.gn();
	}

	@Override
	public void startCompile() {
		javaProjectBuildService.startCompile();
	}

	public void buildProject(boolean buildRefresh, String buildType) {
		javaProjectBuildService.buildProject(buildRefresh, buildType);
	}

	public static class JavaProjectBuildService implements IBuildService, o8 {

		private boolean isCompiling;

		private String title;

		private int progress;

		private boolean isDebugAide;

		private boolean buildRefresh;

		private com.aide.ui.build.android.n packagingService;

		private int v5;

		public JavaProjectBuildService() {}

		private void Mr() {
			notifyProgress(null, 0, false);
			if (this.buildRefresh) {
				return;
			}
			//  main(String[])方法所在类
			List<String> SI = ServiceContainer.getErrorService().SI();
			if (ServiceContainer.isTrainerMode() && SI.size() != 1) {
				ServiceContainer.getTrainerService().Eq();
				return;
			}

			if (SI.size() == 0) {
				MessageBox.BT(ServiceContainer.getMainActivity(), "Run", "There's no main method to run in this project!");
			} else if (SI.size() == 1) {
				U2(SI.get(0));
			} else {
				MessageBox.VH(ServiceContainer.getMainActivity(), "Run", SI, new ValueRunnable<String>(){

						@Override
						public void acceptValue(String t) {
							U2(t);
						}
					});
			}
		}

		public void U2(String str) {
			String Sf = JavaGradleProjectSupport.Sf(ServiceContainer.getProjectService().getCurrentAppHome(), this.isDebugAide);
			MainActivity mainActivity = ServiceContainer.getMainActivity();
			if (ServiceContainer.isTrainerMode()) {
				mainActivity.Ws(false);
				RunTrainerJavaActivity.lg(mainActivity, AppPreferences.isLightTheme(), Sf, str, this.isDebugAide, 15);
			} else {
				RunJavaActivity.a8(mainActivity, 
								   AppPreferences.isLightTheme(),
								   Sf, str, 
								   this.isDebugAide);
			}
		}

		public void notifyProgress(String title, int progress, boolean isCompiling) {
			this.title = title;
			this.progress = progress;
			this.v5 = 0;
			this.isCompiling = isCompiling;
			ServiceContainer.getBuildService().J0(ServiceContainer.getProjectService().getCurrentAppHome(), title, progress, this.v5);

		}

		public void aM() {
			if (this.title != null 
				|| ServiceContainer.DW().VH() 
				|| ServiceContainer.DW().v5()) {
				return;
			}
			Mr();
		}

		public void er() {
			int sh = ServiceContainer.getErrorService().sh();
			int cn = ServiceContainer.getErrorService().cn();
			int i = 100;
			if (sh != 0) {
				i = (cn * 100) / sh;
			}
			this.v5 = i;
			ServiceContainer.getBuildService().J0(ServiceContainer.getProjectService().getCurrentAppHome(), this.title, this.progress, this.v5);

		}
		private void lg() {
			notifyProgress("Compiling...", 10, true);
			String vJ = JavaGradleProjectSupport.getOutputPath(ServiceContainer.getProjectService().getCurrentAppHome(), this.isDebugAide);
			if (!new File(vJ).exists() && !new File(vJ).mkdirs()) {
				tp("Could not create destination dir " + vJ);
				return;
			}
			if (this.buildRefresh) {
				ServiceContainer.getEngineService().P8();
			} else {
				ServiceContainer.getEngineService().tp();
			}

		}

		public String keyAlias;

		public String keyPassword;

		public String storeFilePath;

		public String storePassword;


		private void rN() {

			String currentAppHome = ServiceContainer.getProjectService().getCurrentAppHome();
			Map<String, List<String>> vy = ServiceContainer.getProjectService().vy(currentAppHome);

			String mainClassCacheDir = JavaGradleProjectSupport.getOutputPath(currentAppHome, this.isDebugAide);

			String[] classFileRootDirs = JavaGradleProjectSupport.getClassFileRootDirs(vy, isDebugAide);

			String[] sourceDirs = JavaGradleProjectSupport.aj(vy);

			String[] dependencyLibs = JavaGradleProjectSupport.cb(currentAppHome);

			String outDirPath = JavaGradleProjectSupport.getProjectOutputPath(currentAppHome, this.isDebugAide);

			String jardexPath = outDirPath + "/jardex";

			String aAptResourcePath = null;

			String[] nativeLibDirs = null;

			String outFilePath = JavaGradleProjectSupport.Sf(currentAppHome, this.isDebugAide);

			// debug || release
			ProjectService projectService = ServiceContainer.getProjectService();
			String buildType = projectService.er();
			String userKeystore = AppPreferences.getUserKeystore();


			BuildGradle.SigningConfig signingConfig = ZeroAicyExtensionInterface.getBuildGradle().getConfiguration(GradleTools.getBuildGradlePath(currentAppHome)).getSigningConfig(ProjectService.KD(buildType));
			SigningService.SigningRunnable j6 = new SigningService.SigningRunnable(){
				@Override
				public void j6(String storePath, String storePassword, String aliasName, String aliasPassword) {
					JavaProjectBuildService.this.storeFilePath = storePath;
					JavaProjectBuildService.this.storePassword = storePassword;
					JavaProjectBuildService.this.keyAlias = aliasName;
					JavaProjectBuildService.this.keyPassword = aliasPassword;

				}
			};

			SigningService signingService = (SigningService)(Object)ServiceContainer.getSigningService();
			signingService.Zo(userKeystore, signingConfig, j6);

			this.packagingService.VH(
				mainClassCacheDir, classFileRootDirs, sourceDirs, 
				dependencyLibs, outDirPath, jardexPath,
				aAptResourcePath, nativeLibDirs, outFilePath, 
				this.storeFilePath, this.storePassword, this.keyAlias, this.keyPassword, 
				this.buildRefresh, AppPreferences.isOptimzeDex(), false);

			this.packagingService.we();

		}

		public void tp(String str) {
			this.title = null;
			ServiceContainer.getBuildService().j6(str);

		}

		private void u7() {
			tp("Your project contains errors. Please fix them before running the app.");
		}

		@Override
		public void DW() {
			ServiceContainer.getErrorService().u7(new ErrorService.ErrorListener(){

					@Override
					public void DW() {
						if (isCompiling) {
							er();
						}
					}

					@Override
					public void Hw(String string) {
						if (isCompiling) {
							er();
						}
					}
				});

			n packagingService = new n();
			this.packagingService = packagingService;
			packagingService.tp(new IExternalPackagingServiceListener.Sub(){
					@Override
					public void E6(final String string) {
						ServiceContainer.aj(new Runnable(){
								@Override
								public void run() {
									tp(string);
								}
							});
					}

					@Override
					public void J0() {
						ServiceContainer.aj(new Runnable(){
								@Override
								public void run() {
									tp("Packaging was interrupted!");
								}
							});
					}

					@Override
					public void jD(final String string, final int p) {
						ServiceContainer.aj(new Runnable(){
								@Override
								public void run() {
									notifyProgress(string, p, false);
								}
							});
					}

					@Override
					public void vJ(boolean p) {
						ServiceContainer.aj(new Runnable(){
								@Override
								public void run() {
									notifyProgress(null, 0, false);
									aM();
								}
							});
					}
				});

		}

		@Override
		public void FH(boolean z) {

			ErrorService errorService = ServiceContainer.getErrorService();
			
			if (errorService.a8(".java")) {
                u7();
            } else {
                rN();
            }
			
			/*
			for (String error :  errorService.SI()) {
				if (error.startsWith("ecj: ")) {
					u7();
					return;
				}
			}

			rN();
			*/
			/*
			 if ( errorService.a8(".java") ) {
			 u7();
			 } else {
			 rN();
			 }
			 /*/

		}

		public void Hw(String str) {
			tp("Compilation failed: " + str);

		}

		@Override
		public o8.a VH() {
			return o8.a.JAVA;

		}

		public void buildProject(boolean buildRefresh, String variant) {

			ServiceContainer.getOpenFileService().KD(false, false);

			ServiceContainer.getMainActivity().getAIDEEditorPager().Eq();

			this.buildRefresh = buildRefresh;

			this.isDebugAide = "debug-aide".equals(variant);
			
			ServiceContainer.getErrorService().j3();

			ServiceContainer.getBuildService().J8(this, ServiceContainer.DW().Hw());

			notifyProgress("Building...", 0, false);

			lg();

			ServiceContainer.DW().gn(this);
		}

		@Override
		public void Zo() {}

		@Override
		public void gn() {}

		public void j3() {
			ServiceContainer.getMainActivity().Gj();
			aM();
		}

		@Override
		public int j6() {
			return 18;
		}

		@Override
		public void startCompile() {
			if (this.isCompiling) {
				lg();
			}

		}
	}

}
