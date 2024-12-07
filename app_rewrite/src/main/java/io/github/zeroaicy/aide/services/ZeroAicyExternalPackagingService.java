package io.github.zeroaicy.aide.services;


import android.text.TextUtils;
import com.aide.common.AppLog;
import com.aide.ui.ServiceContainer;
import com.aide.ui.build.packagingservice.ExternalPackagingService;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.aide.ui.services.ThreadPoolService;
import io.github.zeroaicy.aide.utils.AndroidManifestParser;
import io.github.zeroaicy.aide.utils.Utils;
import io.github.zeroaicy.aide.utils.ZeroAicyBuildGradle;
import io.github.zeroaicy.util.FileUtil;
import io.github.zeroaicy.util.IOUtils;
import io.github.zeroaicy.util.Log;
import io.github.zeroaicy.util.MD5Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ExecutorService;
import android.content.Intent;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import com.aide.ui.util.FileSystem;

public class ZeroAicyExternalPackagingService extends ExternalPackagingService {
	@Override
	public void onCreate() {

		AppLog.d("ZeroAicyExternalPackagingService", "初始化");
		try {
			// 初始化 App
			ServiceContainer.setContext(getApplicationContext());

			ExternalPackagingService.ExternalPackagingServiceWorker externalPackagingServiceWorker = getExternalPackagingServiceWorker();
			if (externalPackagingServiceWorker != null) {
				//释放旧的
				this.WB.we();
				//换成自己的
				this.WB = externalPackagingServiceWorker;			
			}	
		}
		catch (Throwable e) {
			AppLog.e("ZeroAicyPackagingWorker", "替换打包实现失败", e);
		}
		super.onCreate();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		AppLog.d(TAG, intent, "onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		AppLog.d(TAG, "onDestroy");
		super.onDestroy();
	}


	public PackagingWorkerWrapper getExternalPackagingServiceWorker() {
		return new ZeroAicyPackagingWorker(this);
	}

	public static String getFileName(String path) {
		int fileNameStartIndex = path.lastIndexOf('/');
		if (fileNameStartIndex >= 0) {
			return path.substring(fileNameStartIndex);
		}
		return path;
	}
	private static final String TAG = "Worker";

	public class ZeroAicyPackagingWorker extends PackagingWorkerWrapper {

		public ZeroAicyPackagingWorker(ExternalPackagingService service) {
			super(service);
		}

		// QX minsdk是否大于等于21
		@Override
		public TaskWrapper
		getTaskWrapper(String mainClassCacheDir, String[] classFileRootDirs, String[] sourceDirs, 
					   String[] dependencyLibs, String outDirPath, String jardexPath, 
					   String aAptResourcePath, String[] nativeLibDirs, String outFilePath, 
					   String signaturePath, String signaturePassword, String signatureAlias, 
					   String signatureAliasPassword, boolean buildRefresh, boolean optimze_dex, boolean QX) {


			return new ZeroAicyR8Task(mainClassCacheDir, classFileRootDirs, sourceDirs, 
									  dependencyLibs, outDirPath, jardexPath, aAptResourcePath, 
									  nativeLibDirs, outFilePath, signaturePath, signaturePassword, 
									  signatureAlias, signatureAliasPassword, buildRefresh, optimze_dex, QX);
		}

		public class ZeroAicyR8Task extends TaskWrapper {
			private HashMap<String, String> environment = new HashMap<String, String>();

			public ZeroAicyR8Task(String mainClassCacheDir, String[] classFileRootDirs, String[] sourceDirs, String[] dependencyLibs, String outDirPath, String Zo, String aAptResourcePath, String[] nativeLibDirs, String outFilePath, String signaturePath, String signaturePassword, String signatureAlias, String signatureAliasPassword, boolean buildRefresh, boolean Ws, boolean QX) {
				super(mainClassCacheDir, classFileRootDirs, sourceDirs, dependencyLibs, outDirPath, Zo, aAptResourcePath, nativeLibDirs, outFilePath, signaturePath, signaturePassword, signatureAlias, signatureAliasPassword, buildRefresh, Ws, QX);

				// 从文件夹添加原生库文件，
				this.nativeLibZipEntryTransformer = new ZipEntryTransformer.NativeLibFileTransformer(getAndroidFxtractNativeLibs());
				this.libgdxNativesTransformer = new ZipEntryTransformer.LibgdxNativesTransformer(getAndroidFxtractNativeLibs());
				if (ZeroAicySetting.isEnableEnsureCapacity()) {
					this.environment.put("EnsureCapacity", getLibEnsureCapacityPathPath());				
				}
				// 初始化
				DexingJarTask.init(this.environment);
			}

			@Override
			public void packaging() throws Throwable {
				long packagingStart = Utils.nowTime();

				long now = packagingStart;
				this.initBuildEnvironment();
				AppLog.d(TAG, "initBuildEnvironment: %s ms", Utils.nowTime() - now);

				if (isNotDebugFormAIDE) {
					for (String classFileRootDir : getAllClassFileRootDirs()) {
						// 删除 adrt注入的class文件
						deleteADRTClassFile(classFileRootDir);
					}
				}

				// 混淆
				if (isMinifyAndroidProject()) {
					now = Utils.nowTime();
					packagingAndroidMinify();
					AppLog.d(TAG, "packaging Minify Android : %s ms" , Utils.nowTime() - now);
					return;
				}

				if (isMinifyJavaGradleProject()) {
					now = Utils.nowTime();
					packagingJavaGradleMinify();
					AppLog.d(TAG, "packaging Minify Java Gradle: %s ms" , Utils.nowTime() - now);
					return;
				}

				now = Utils.nowTime();
				List<String> classesDexZipList = getClassesDexZipList();
				AppLog.d(TAG, "Dexing: %s ms", (Utils.nowTime() - now));

				now = Utils.nowTime();
				//Java工程
				if (getOutFilePath().endsWith(".zip")) {
					packagingJavaProject(classesDexZipList);
				} else {
					//打包安卓项目
					packagingAndroidProject(classesDexZipList);
				}
				AppLog.d(TAG, "output file build: %s ms", (Utils.nowTime() - now));

				AppLog.d(TAG, "packaging: %s ms", (Utils.nowTime() - now));
			}

			/**
			 * 返回 合并class文件缓存后的输出路径？

			 */
			private String getMainClassesDexZipFilePath() {
				return getMergerCacheDirPath() + "/classes.dex.zip";
			}

			private String getDependencyMergerFilePath() {
				return getMergerCacheDirPath() + "/dependency_merger.dex.zip";
			}

			//混淆
			private File getMixUpDexZipFile(boolean delete) {
				File file = new File(getMergerCacheDirPath() , "/classes_mix_up.dex.zip");
				if (file.exists() && delete) {
					file.delete();
				}
				return file;
			}

			/**
			 * d8 class jar 并返回classesDexZip[d8输出zip]
			 */
			@Override
			public List<String> getClassesDexZipList() throws Throwable {
				List<String> classesDexZipList = new ArrayList<>();
				//先dexing 主classes.dex，即从源码编译的class
				checkInterrupted();

				showProgress("Run D8 Dexing", 60);

				String dexingMergingJarDexFiles = null;
				if (!getDexingLibs().isEmpty()) {
					dexingMergingJarDexFiles = dexingMergingJarDexFiles();
				}

				//dexing merging class文件
				String dexingMergingClassFiles = dexingMergingClassFiles();
				//保证主dex优先添加
				classesDexZipList.add(0, dexingMergingClassFiles);

				//添加依赖库dexing缓存
				if (dexingMergingJarDexFiles != null) {
					classesDexZipList.add(dexingMergingJarDexFiles);
				}
				return classesDexZipList;
			}
			/**
			 * 检查线程中断信号
			 */
			private void checkInterrupted() throws InterruptedException {
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
			}
			/**
			 * dexing And merging Jar Class Files
			 */
			private String dexingMergingJarDexFiles() throws InterruptedException, Throwable {

				showProgress("Dexing - Libraries", 62);

				List<String> dependencyLibDexs = new ArrayList<>();

				//缓存目录都不存在，全量dexing
				boolean existsCacheDir = new File(getDefaultJarDexDirPath()).exists();
				// dexing jarw不管构建刷新
				boolean isBuildRefresh = /*isBuildRefresh() ||*/ !existsCacheDir;
				//dexing 没有Jardex缓存的依赖
				List<String> dexingLibs = getDexingLibs();
				List<String> needDexingLibs = new ArrayList<>();

				for (String inputJarFilePath: dexingLibs) {
					checkInterrupted();

					File inputJarFile = new File(inputJarFilePath);
					String outputDexZipFile = getJarDexCachePath(inputJarFilePath);
					File dexCacheFile = new File(outputDexZipFile);

					//根据时间戳判断是否需要dexing
					if (isBuildRefresh
						|| !dexCacheFile.exists() 
						|| inputJarFile.lastModified() > dexCacheFile.lastModified()) {
						// 需要重写dexing，添加进dexing列表
						needDexingLibs.add(inputJarFilePath);
					} else {
						dependencyLibDexs.add(outputDexZipFile);
					}
				}

				if (!needDexingLibs.isEmpty()) {

					final AtomicInteger dexingingCount = new AtomicInteger(1);
					DexingJarTask.Configuration configuration = new DexingJarTask.Configuration();
					configuration.minSdkVersion = getMinSdk();
					configuration.user_android_jar = getUserAndroidJar();
					configuration.dependencyLibs =  getDependencyLibs();

					configuration.dexingingCount = dexingingCount;

					List<DexingJarTask> tasks = new ArrayList<>();

					final int needDexingLibsSize = needDexingLibs.size();
					DexingJarTask.TaskDoneLister taskDoneLister = new DexingJarTask.TaskDoneLister(){
						@Override
						public synchronized void done() {
							showProgress(String.format("Dexing - Libraries (%d/%d)", dexingingCount.get(), needDexingLibsSize), 64);
						}
					};
					// 先显示一下
					taskDoneLister.done();

					// 填充任务列表
					fillDexingJarTasks(needDexingLibs, configuration, taskDoneLister, tasks);

					long now = Utils.nowTime();

					// DexingJarTask专用线程
					ExecutorService threadPoolService = ThreadPoolService.getThreadPoolService(DexingJarTask.ThreadPoolServiceName, 3);

					List<Future<DexingJarTask>> futures = threadPoolService.invokeAll(tasks);

					for (Future<DexingJarTask> future : futures) {
						// // 这会阻塞直到任务完成或抛出异常
						try {
							DexingJarTask dexingJarTask = future.get();

							// 添加dex.zip路径
							if (dexingJarTask.isBatchMode) {
								dependencyLibDexs.addAll(dexingJarTask.outputDexZipFiles);
							} else {
								dependencyLibDexs.add(dexingJarTask.outputDexZipFile);
							}
						}
						catch (Throwable e) {
							if (e instanceof ExecutionException) {
								e = ((ExecutionException)e).getCause();
							}
							throw e;
						}
					}

					AppLog.d(TAG, "Dexing - Libraries 共用时: " + (Utils.nowTime() - now) + "ms");
				}
				String dependencyMergerFilePath = getDependencyMergerFilePath();

				if (!isMergingJarDexFiles(dependencyLibDexs)) {
					AppLog.d(TAG, "缓存文件没有更新，不需要合并");
					return dependencyMergerFilePath;
				}

				// 合并依赖
				MergingJarDexFiles(dependencyLibDexs, dependencyMergerFilePath);

				return dependencyMergerFilePath;
			}
			/**
			 * 对需要dexing的Jar进行分组
			 */
			private void fillDexingJarTasks(List<String> needDexingLibs, DexingJarTask.Configuration configuration, DexingJarTask.TaskDoneLister taskDoneLister, List<DexingJarTask> tasks) {

				final int needDexingLibsSize = needDexingLibs.size();

				long filterThreshold = 8 * 1024 * 1024;
				long filterThreshold2 = 8 * 1024 * 1024;

				for (int index = 0; index < needDexingLibsSize; index++) {

					String inputJarFile = needDexingLibs.get(index);
					String outputDexZipFile = getJarDexCachePath(inputJarFile);
					long bigInputJarFileSize = new File(inputJarFile).length();

					// 单个Jar文件大于6MB，则不用批量处理模式
					// 防止OOM
					if (bigInputJarFileSize > filterThreshold) {
						DexingJarTask dexingJarTask = new DexingJarTask(inputJarFile, outputDexZipFile, configuration);
						dexingJarTask.setTaskDoneLister(taskDoneLister);
						tasks.add(dexingJarTask);
						continue;
					}

					// 处理小文件
					// 小文件集合
					List<String> smallInputJarFiles = new ArrayList<String>();
					List<String> smallOutputDexZipFiles = new ArrayList<String>();
					// 添加当前输入小文件
					smallInputJarFiles.add(inputJarFile);
					smallOutputDexZipFiles.add(outputDexZipFile);
					// 添加完了就应该更新索引
					index++;

					long smallInputJarFilesSize = bigInputJarFileSize;
					// 在阈值内
					while (smallInputJarFilesSize < filterThreshold2 
						   && index < needDexingLibsSize) {

						String inputJarFile2 = needDexingLibs.get(index);
						String outputDexZipFile2 = getJarDexCachePath(inputJarFile2);

						long fileSize2 = new File(inputJarFile2).length();

						// 当前文件自己就是大文件，仍然是大文件模式
						if (fileSize2 > filterThreshold) {
							DexingJarTask dexingJarTask = new DexingJarTask(inputJarFile2, outputDexZipFile2, configuration);
							dexingJarTask.setTaskDoneLister(taskDoneLister);
							tasks.add(dexingJarTask);
						} else {
							// 小文件
							// 更新小文件整体大小
							smallInputJarFilesSize += fileSize2;
							// 添加当前输入小文件
							smallInputJarFiles.add(inputJarFile2);
							smallOutputDexZipFiles.add(outputDexZipFile2);
						}
						// 下一个文件
						index++;
					}
					// 此时说明小文件集合大于阈值
					// 启用批量模式
					DexingJarTask dexingJarTask = new DexingJarTask(smallInputJarFiles, smallOutputDexZipFiles, configuration);
					dexingJarTask.setTaskDoneLister(taskDoneLister);
					tasks.add(dexingJarTask);
					AppLog.d(TAG, "DexingJarTask内文件 %d", smallInputJarFiles.size());
				}
			}

			public void MergingJarDexFiles(List<String> dependencyLibDexs, String dependencyMergerFile) throws Throwable {
				showProgress("Merging - Libraries", 65);
				List<String> argsList  = new ArrayList<String>();
				String user_androidjar = null;
				// 合并*.jar.dex
				D8TaskWrapper.fillD8Args(argsList, getMinSdk(), false, false, user_androidjar, null, dependencyMergerFile);
				
				// 处理 record
				for( String outputDexZipFile : dependencyLibDexs){
					// 根据合并时的 输入 jar.dex.zip 文件 计算globals文件
					String outputDexZipPath = outputDexZipFile + "--globals.zip";
					
					File globalsZipFile = new File(outputDexZipPath);
					if( !globalsZipFile.exists() 
						|| !globalsZipFile.isFile()
						// 空zip 22 bytes
						|| globalsZipFile.length() <= 22){
						continue;
					}
					
					argsList.add("--globals");
					argsList.add(outputDexZipPath);
				}
				
				//输入dexs
				argsList.addAll(dependencyLibDexs);
				
				try {
					// 将采用 子进程方式，防止oom
					D8TaskWrapper.runD8Task(argsList, environment);
				}
				catch (Throwable e) {

					// 有错误时，删除缓存
					new File(dependencyMergerFile).delete();

					throw e;
				}
				AppLog.d(TAG, "合并依赖库，已输出: " + dependencyMergerFile);
			}

			private boolean isMergingJarDexFiles(List<String> inputLibDexs) throws FileNotFoundException, IOException {
				File dependencyMergerFile = new File(getDependencyMergerFilePath());
				long lastModified = dependencyMergerFile.lastModified();


				boolean isMergingJarDexFiles = false;

				//对比时间戳
				for (String dependencyLibDex : inputLibDexs) {
					if (new File(dependencyLibDex).lastModified() > lastModified) {
						isMergingJarDexFiles = true;
						break;
					}
				}

				//查看输入文件集合是否一致
				Set<String> inputJarFilesSet = new HashSet<String>(inputLibDexs);
				File inputInfoFile = new File(getDependencyMergerFilePath() + "_inputInfo.txt");
				if (!inputInfoFile.exists()) {
					isMergingJarDexFiles = true;
				} 

				if (!isMergingJarDexFiles) {
					Set<String> lastInputJarFilesSet = new HashSet<>();
					BufferedReader br = null;
					try {
						br = new BufferedReader(new InputStreamReader(new FileInputStream(inputInfoFile)));
						String line;
						while ((line = br.readLine()) != null) {
							lastInputJarFilesSet.add(line);
						}
					}
					finally {
						IOUtils.close(br);
					}
					//比较
					isMergingJarDexFiles = inputJarFilesSet.size() != lastInputJarFilesSet.size()
						|| !inputJarFilesSet.containsAll(lastInputJarFilesSet);
				}

				if (isMergingJarDexFiles) {
					//写入inputJarFilesSet
					FileOutputStream output =null;
					try { 
						output = new FileOutputStream(inputInfoFile);
						for (String input : inputJarFilesSet) {
							output.write(input.getBytes());
							output.write('\n');
						}
						output.close();
					}
					finally {
						IOUtils.close(output);
					}
				}
				return isMergingJarDexFiles;
			}



			public void deleteADRTClassFile(String classFileCacheDir) {
				if (classFileCacheDir.endsWith("debug") || ZeroAicySetting.enableADRT()) {
					return;
				}

				String defaultClassDexCacheDirPath = getDefaultClassDexCacheDirPath();

				{
					String[] deleteClassFileNames = new String[]{"adrt/ADRTSender", "adrt/ADRTLogCatReader"};
					for (String deleteClassFileName : deleteClassFileNames) {
						// adrt/ADRTSender
						//String deleteClassFileName = "adrt/ADRTSender.class";
						File deleteClassFile = new File(classFileCacheDir, deleteClassFileName + ".class");
						if (deleteClassFile.exists()) {
							deleteClassFile.delete();
						}
						File adrtDexFile = new File(defaultClassDexCacheDirPath, deleteClassFileName + ".dex");
						if (adrtDexFile.exists()) {
							adrtDexFile.delete();
						}
					}	
				}

			}
			/**
			 * dexing And merging Class Files
			 */
			private String dexingMergingClassFiles() throws InterruptedException, Throwable {
				checkInterrupted();

				List<String> incrementalClassFiles = new ArrayList<>();
				// 
				Set<String> classFileMap = new HashSet<>();


				//填充class文件

				// 主项目class缓存路径
				String mainProjectClassCacheDirPath = getMainClassCacheDir();
				
				// AppLog.println_d("mainProjectClassCacheDirPath 2 " + mainProjectClassCacheDirPath);
				
				// 优先填充填主项目的class缓存
				fillClassFileCache(mainProjectClassCacheDirPath, incrementalClassFiles, classFileMap);

				//遍历添加所有项目的class缓存目录
				for (String classFileRootDir : getAllClassFileRootDirs()) {
					checkInterrupted();

					if (classFileRootDir == null 
						|| classFileRootDir.equals(mainProjectClassCacheDirPath)) {
							continue;
					}
					// 填充子项目class缓存
					fillClassFileCache(classFileRootDir, incrementalClassFiles, classFileMap);
				}
				
				String mainClassesDexZipFilePath = getMainClassesDexZipFilePath();
				//增量为0，不dexing
				if (incrementalClassFiles.isEmpty()) {
					return mainClassesDexZipFilePath;
				}

				//Log.d(TAG, "待dexing类文件数量: " + incrementalClassFiles.size());

				showProgress("Dexing - Classes", 67);
				//dexing classFile[增量]
				dexingClassFilesFromD8(getDefaultClassDexCacheDirPath(), incrementalClassFiles);

				showProgress("Merging - Classes", 69);

				// 查询需要合并的 dex
				List<String> classeDexFiles = FileUtil.Files2Strings(FileUtil.findFile(new File(getDefaultClassDexCacheDirPath()), ".dex"));
				// 合并dex
				mergingClassDexs(mainClassesDexZipFilePath, classeDexFiles);				

				return mainClassesDexZipFilePath;
			}

			// dexing所有class
			private void dexingClassFilesFromD8(String outPath, List<String> dexingClassFiles) throws Throwable {
				List<String> argsList = new ArrayList<>();
				//检查输出目录
				File outDir = new File(outPath);
				if (!outDir.exists()) {
					outDir.mkdirs();
				}
				String user_android_jar = getUserAndroidJar();
				int minSdk = getMinSdk();
				if (minSdk < 21) {
					//minSdk＜21，无法编译生成的class
					minSdk = 21;
				}
				// AIDE生成的dex不会与库依赖合并
				// 合并dex时 并不会脱糖
				// 因此必须在 dexing时添加库信息
				// 所以必须提前脱糖

				// dexing AIDE编译的 *.class
				D8TaskWrapper.fillD8Args(argsList, minSdk, true, true, user_android_jar, getDexingLibs(), outPath);
				for (String compileOnlyLib : compileOnlyLibs) {
					argsList.add("--lib");
					argsList.add(compileOnlyLib);
				}
				
				argsList.add("--globals-output");
				argsList.add(getDefaultClassDexGlobalsPath());
				
				//添加需要编译的jar
				argsList.addAll(dexingClassFiles);

				// 将采用 子进程方式，防止oom
				D8TaskWrapper.runD8Task(argsList, this.environment);
			}

			//合并AIDE生成的class.dex
			private void mergingClassDexs(String outDexZipPath, Collection<String> classeDexFiles) throws Throwable {
				File outDexZipFile = new File(outDexZipPath);
				//删除缓存文件
				outDexZipFile.delete();

				List<String> argsList  = new ArrayList<String>();
				String user_androidjar = null; 
				// 合并 AIDE编译的 *.class.dex
				D8TaskWrapper.fillD8Args(argsList, getMinSdk(), false, false, user_androidjar, null, outDexZipPath);
				
				String defaultClassDexGlobalsPath = getDefaultClassDexGlobalsPath();
				if (new File(defaultClassDexGlobalsPath).isFile()) {
					argsList.add("--globals");
					argsList.add(defaultClassDexGlobalsPath);
				}
				
				//输入dexs
				argsList.addAll(classeDexFiles);
				// 将采用 子进程方式，防止oom
				D8TaskWrapper.runD8Task(argsList, this.environment);
				//Log.d(TAG, "合并classes.dex，已输出: " + outDexZipPath);
			}

			/**
			 * dexing jar
			 */
			private List<String> dexingLibs = new ArrayList<>();
			public List<String> getDexingLibs() {
				return this.dexingLibs;
			}

			private List<String> dependencyLibs;
			public List<String> getDependencyLibs() {
				if (this.dependencyLibs == null) {
					List<String> dexingLibs = getDexingLibs();
					int initialCapacity = dexingLibs.size() + this.compileOnlyLibs.size();
					this.dependencyLibs = new ArrayList<>(initialCapacity);
					this.dependencyLibs.addAll(dexingLibs);
					this.dependencyLibs.addAll(this.compileOnlyLibs);
				}
				return this.dependencyLibs;
			}

			/**
			 * compileOnly
			 */
			private List<String> compileOnlyLibs  = new ArrayList<>();

			/**
			 * aar混淆规则文件
			 */
			private List<Path> proguardPaths  = new ArrayList<>();
			private List<String> proguardFiles  = new ArrayList<>();


			/**
			 * 初始化环境
			 */
			private void initBuildEnvironment() throws Throwable {

				if (isBuildRefresh()) {
					deleteCacheDir();
				}

				ScopeTypeQuerier scopeTypeQuerier = getScopeTypeQuerier();
				this.compileOnlyLibs = scopeTypeQuerier.getCompileOnlyLibs();
				//this.runtimeOnlyLibs = scopeTypeQuerier.getRuntimeOnlyLibs();
				this.dexingLibs = scopeTypeQuerier.getDexingLibs();


			}
			private void deleteCacheDir() {
				File defaultJarDexDir = new File(getDefaultJarDexDirPath());
				File defaultClassDexCacheDir = new File(getDefaultClassDexCacheDirPath());
				File mergerCacheDir = new File(getMergerCacheDirPath());

				FileUtil.deleteFolder(defaultClassDexCacheDir);
				FileUtil.deleteFolder(mergerCacheDir);
				// FileUtil.deleteFolder(new File(getDefaultIntermediatesDirPath()));

				defaultJarDexDir.mkdirs();
				defaultClassDexCacheDir.mkdirs();
				mergerCacheDir.mkdirs();
			}

			// dex.zip转换器，即根目录下有classes%d.dex的zip文件的转换器
			final ZipEntryTransformer.DexZipTransformer dexZipEntryTransformer = new ZipEntryTransformer.DexZipTransformer();
			// 从jar依赖添加资源的过滤器，
			final ZipEntryTransformer.ZipResourceTransformer zipResourceZipEntryTransformer = new ZipEntryTransformer.ZipResourceTransformer();
			// 从文件夹添加原生库文件，
			final ZipEntryTransformer.NativeLibFileTransformer nativeLibZipEntryTransformer;
			final ZipEntryTransformer.LibgdxNativesTransformer libgdxNativesTransformer;

			public void minify2() throws Exception, Throwable {
				// proguardPaths
				// 
				// 初始化混淆环境

				// 查找主项目生成的 "aapt_rules.txt"
				File aaptRulesFile = new File(getDefaultIntermediatesDirPath(), "aapt_rules.txt");
				if (aaptRulesFile.isFile()) {
					this.proguardFiles.add(aaptRulesFile.getAbsolutePath());
					this.proguardPaths.add(aaptRulesFile.toPath());			
				}

				/**
				 * 从主项目build.gradle添加混淆规则文件
				 */
				for (String proguardPulesFilePath  : getZeroAicyBuildGradle().getProguardFiles()) {
					if (TextUtils.isEmpty(proguardPulesFilePath)) {continue;}
					File proguardPulesFile  = new File(proguardPulesFilePath);
					if (proguardPulesFile.isFile()) {
						this.proguardFiles.add(proguardPulesFile.getAbsolutePath());
						this.proguardPaths.add(proguardPulesFile.toPath());
					}
				}
				// 遍历并添加混淆规则文件
				for (String dependencyLib : this.dexingLibs) {
					File jarFile = new File(dependencyLib);
					String fileName = jarFile.getName().toLowerCase();

					if (fileName.equals("classes.jar")) {
						File proguardFile = new File(jarFile.getParentFile(), "proguard.txt");
						if (proguardFile.isFile()) {
							this.proguardFiles.add(proguardFile.getAbsolutePath());
							this.proguardPaths.add(proguardFile.toPath());
						}					
					}
				}

				// 编译依赖库
				showProgress("配置混淆中", 60);

				AppLog.d(TAG, "合并待混淆类库");


				// get混淆输出文件路径[dex.zip]
				File mixUpDexZipFile = getMixUpDexZipFile(true);


				//BaseDiagnosticsHandler baseDiagnosticsHandler = new BaseDiagnosticsHandler();
				//32线程
				int minSdk = getMinSdk();
				//主要是为了兼容AIDE的输出类
				minSdk = Math.max(minSdk, 21);

				List<String> argsList = new ArrayList<>();

				argsList.add("--lib");
				argsList.add(getUserAndroidJar());

				argsList.add("--min-api");

				argsList.add(String.valueOf(minSdk));

				// 输出路径
				argsList.add("--output ");
				argsList.add(mixUpDexZipFile.getAbsolutePath());
				for (String compileOnlyLib : this.compileOnlyLibs) {
					argsList.add("--lib");
					argsList.add(compileOnlyLib);

				}
				// 添加 AIDE编译的类
				List<String> mainClassFilePaths = getMainClassFilePaths();
				// 添加java -> class的类文件
				argsList.addAll(mainClassFilePaths);

				// 添加库
				argsList.addAll(this.getDexingLibs());

				// 添加混淆规则
				for (String proguardPath : this.proguardFiles) {
					argsList.add("--pg-conf");
					argsList.add(proguardPath);
				}

				//* 输出映射表
				if (true) {
					argsList.add("--pg-map-output");
					File proguardMapFile = new File(getIntermediatesChildDirPath("r8"), "proguardMap.txt");
					argsList.add(proguardMapFile.getAbsolutePath());
				}
				//*/

				showProgress("混淆class中", 65);

				//运行r8
				D8TaskWrapper.runR8Task(argsList, this.environment);

			}


			/**
			 * 遍历AIDE Class缓存路径添加class文件
			 */
			private List<String> getMainClassFilePaths() throws InterruptedException {
				List<String> allClassFiles = new ArrayList<>();
				//类名
				Set<String> classFileSet = new HashSet<>();
				//填充class文件
				String mainProjectClassCacheDirPath = getMainClassCacheDir();
				fillAllClassFileCache(mainProjectClassCacheDirPath, allClassFiles, classFileSet);
				//遍历添加所有项目的class缓存目录
				for (String classFileRootDir : getAllClassFileRootDirs()) {
					checkInterrupted();
					if (classFileRootDir != null 
						|| !classFileRootDir.equals(mainProjectClassCacheDirPath)) {
						fillAllClassFileCache(classFileRootDir, allClassFiles, classFileSet);
					}
				}
				return allClassFiles;
			}

			// 也对JavaGradle支持混淆
			public void packagingJavaGradleMinify() throws Throwable {
				// 编译依赖库
				showProgress("配置混淆中", 60);

				/**
				 * 从主项目build.gradle添加混淆规则文件
				 */
				for (String proguardPulesFilePath  : getZeroAicyBuildGradle().getProguardFiles()) {
					if (TextUtils.isEmpty(proguardPulesFilePath)) {continue;}
					File proguardPulesFile  = new File(proguardPulesFilePath);
					if (proguardPulesFile.isFile()) {
						this.proguardFiles.add(proguardPulesFile.getAbsolutePath());
						this.proguardPaths.add(proguardPulesFile.toPath());
					}
				}

				AppLog.d(TAG, "合并待混淆类库");


				// get混淆输出文件路径[dex.zip]
				File mixUpDexZipFile = getMixUpDexZipFile(true);

				int minSdk = getMinSdk();
				//主要是为了兼容AIDE的输出类
				minSdk = Math.max(minSdk, 21);

				List<String> argsList = new ArrayList<>();

				argsList.add("--lib");
				argsList.add(getUserAndroidJar());

				argsList.add("--min-api");

				argsList.add(String.valueOf(minSdk));

				// 输出路径
				argsList.add("--output ");
				argsList.add(mixUpDexZipFile.getAbsolutePath());
				for (String compileOnlyLib : this.compileOnlyLibs) {
					argsList.add("--lib");
					argsList.add(compileOnlyLib);

				}
				// 添加 AIDE编译的类
				List<String> mainClassFilePaths = getMainClassFilePaths();
				// 添加java -> class的类文件
				argsList.addAll(mainClassFilePaths);

				// 添加库
				argsList.addAll(this.getDexingLibs());

				// 添加混淆规则
				for (String proguardPath : this.proguardFiles) {
					argsList.add("--pg-conf");
					argsList.add(proguardPath);
				}

				//* 输出映射表
				if (true) {
					argsList.add("--pg-map-output");
					File proguardMapFile = new File(getIntermediatesChildDirPath("r8"), "proguardMap.txt");
					argsList.add(proguardMapFile.getAbsolutePath());
				}
				//*/

				showProgress("混淆class中", 65);

				//运行r8
				D8TaskWrapper.runR8Task(argsList, this.environment);

				packagingJavaProject(Collections.singletonList(mixUpDexZipFile.getAbsolutePath()));
			}

			// 安卓支持混淆才有意义，也只能是安卓
			private void packagingAndroidMinify() throws Throwable {

				//混淆
				minify2();

				showProgress("构建APK", 80);			
				//未zip优化，未签名
				File unZipAlignedSignedApkFile = getUnZipAlignSignerApkFile(true);
				PackagingStream packagingZipOutput = new PackagingStream(new FileOutputStream(unZipAlignedSignedApkFile));

				AppLog.d(TAG, "从aapt2生成文件添加资源");
				//resources_ap_file
				String aAptResourceFilePath = getAAptResourceFilePath();
				//打包resources.ap_ 文件
				ZipEntryTransformerService.packagingZipFile(aAptResourceFilePath, zipResourceZipEntryTransformer, packagingZipOutput, true);

				// 从文件夹添加原生库文件，
				ZipEntryTransformer.NativeLibFileTransformer nativeLibZipEntryTransformer = new ZipEntryTransformer.NativeLibFileTransformer(getAndroidFxtractNativeLibs());

				//从原生库目录添加so
				for (String nativeLibDirPath : this.getNativeLibDirs()) {
					File nativeLibDirFile = new File(nativeLibDirPath);
					if (!nativeLibDirFile.exists()) {
						continue;
					}
					AppLog.d(TAG, "从原生库添加" + nativeLibDirPath);
					ZipEntryTransformerService.packagingDirFile(nativeLibDirPath, nativeLibDirFile, nativeLibZipEntryTransformer, packagingZipOutput);

				}
				//打包混淆后的dex
				ZipEntryTransformerService.packagingZipFile(getMixUpDexZipFile(false).getAbsolutePath(), dexZipEntryTransformer, packagingZipOutput, false);

				//打包源码目录下的资源
				packagingSourceDirsResource(packagingZipOutput);			
				//打包jar资源，优先第一个
				packagingJarResources(packagingZipOutput);
				// 打包 libgdxNatives依赖资源
				packagingLibgdxNativesResources(packagingZipOutput);
				//打包完成
				packagingZipOutput.close();

				//优化并签名apk
				zipalignSignerApk();
			}
			/**
			 * 打包Java项目即zip
			 */
			@Override
			public void packagingJavaProject(List<String> dexZipPathList) throws Throwable {
				showProgress("packaging - JavaProject", 73);

				File outFile = new File(getOutFilePath());
				File outDirFile = outFile.getParentFile();
				if (!outDirFile.exists()) {
					outDirFile.mkdirs();
				}

				File outTempFile = File.createTempFile(outFile.getName(), ".unSigner", outDirFile);
				
				try {
					//构建输出文件
					PackagingStream packagingZipOutput = new PackagingStream(new FileOutputStream(outTempFile));
					//打包dex
					packagingDexs(dexZipPathList, packagingZipOutput);
					packagingSourceDirsResource(packagingZipOutput);
					//打包依赖库资源
					packagingJarResources(packagingZipOutput);
					// 打包 libgdxNatives依赖资源
					packagingLibgdxNativesResources(packagingZipOutput);

					packagingZipOutput.close();

					ApkSignerService.signerApk(
						getMinSdk(),
						getSignaturePath(),
						getSignatureAlias(),
						getSignatureAliasPassword(),
						getSignaturePassword(),
						outTempFile, 
						outFile);
				}
				finally {
					outTempFile.delete();
				}
			}

			private void packagingDexs(List<String> dexZipPathList, PackagingStream packagingZipOutput) throws IOException {
				AppLog.d(TAG, "添加classes.dex");
				for (String dexZipPath : dexZipPathList) {
					ZipEntryTransformerService.packagingZipFile(dexZipPath, dexZipEntryTransformer, packagingZipOutput, false);
				}
			}
			/**
			 * 打包安卓即apk
			 */
			@Override
			public void packagingAndroidProject(List<String> dexZipPathList) throws Throwable {

				showProgress("构建APK", 80);
				//未zip优化，未签名
				File unZipAlignedUnSignedApkFile = getUnZipAlignSignerApkFile(true);

				PackagingStream packagingZipOutput = new PackagingStream(new FileOutputStream(unZipAlignedUnSignedApkFile));
				//resources_ap_file
				String aAptResourceFilePath = getAAptResourceFilePath();
				AppLog.d(TAG, "Adding aapt generated resources from " + aAptResourceFilePath);
				//打包resources.ap_ 文件
				ZipEntryTransformerService.packagingZipFile(aAptResourceFilePath, zipResourceZipEntryTransformer, packagingZipOutput, true);

				//从原生库目录添加so
				AppLog.d(TAG, "添加原生库");
				for (String nativeLibDirPath : this.getNativeLibDirs()) {
					File nativeLibDirFile = new File(nativeLibDirPath);
					if (nativeLibDirFile.exists()) {
						ZipEntryTransformerService.packagingDirFile(nativeLibDirPath, nativeLibDirFile, nativeLibZipEntryTransformer, packagingZipOutput);
					}
				}
				//打包dex
				packagingDexs(dexZipPathList, packagingZipOutput);
				//打包源码目录下的资源
				packagingSourceDirsResource(packagingZipOutput);			
				//打包jar资源，优先第一个
				packagingJarResources(packagingZipOutput);
				// 打包 libgdxNatives依赖资源
				packagingLibgdxNativesResources(packagingZipOutput);

				//打包完成
				packagingZipOutput.close();

				//优化并签名apk
				zipalignSignerApk();

			}

			private void zipalignSignerApk() throws Throwable {

				//优化apk
				showProgress("ZeroAicy Zipalign APK ", 85);
				// zipalign命令路径 优化前，未签名 优化后，未签名
				File unSignedApkFile = ApkSignerService.zipalignApk(getZipalignLibPath(), getUnZipAlignSignerApkFile(false), getUnSignedApkFile(true));

				//签名
				AppLog.d(TAG, "开始Signing APK: ");
				long now = Utils.nowTime();
				//-zipaligned-unsigned 
				showProgress("ZeroAicy Signing APK ", 90);
				ApkSignerService.signerApk(
					getMinSdk(),
					getSignaturePath(),
					getSignatureAlias(),
					getSignatureAliasPassword(),
					getSignaturePassword(),
					unSignedApkFile, 
					new File(getOutFilePath()));

				AppLog.d(TAG, "Signing APK共用时: %sms", Utils.nowTime() - now);
			}

			private void packagingLibgdxNativesResources(PackagingStream packagingZipOutput) throws IOException, Throwable {
				AppLog.d(TAG, "从LibgdxNatives添加资源");
				for (String libgdxNativesLibPath : getScopeTypeQuerier().getLibgdxNativesLibs()) {
					this.libgdxNativesTransformer.setCurLibgdxNativesLibsPath(libgdxNativesLibPath);
					ZipEntryTransformerService.packagingZipFile(libgdxNativesLibPath, libgdxNativesTransformer, packagingZipOutput, false);
				}

			}

			private void packagingSourceDirsResource(PackagingStream packagingZipOutput) throws IOException {
				AppLog.d(TAG, "从源码目录添加资源");
				//从源码目录添加
				String[] sourceDirs = getSourceDirs();
				if (sourceDirs == null) {
					return;
				}
				for (String sourceDir : sourceDirs) {
					File sourceDirFile = new File(sourceDir);
					if (!sourceDirFile.exists()) {
						continue;
					}
					ZipEntryTransformerService.packagingDirFile(sourceDir, new File(sourceDir), zipResourceZipEntryTransformer, packagingZipOutput);
				}
			}

			private void packagingJarResources(PackagingStream packagingZipOutput) throws IOException, Throwable {

				AppLog.d(TAG, "从JAR文件添加资源");

				// dexing jar资源
				for (String dependencyLibPath : this.dexingLibs) {
					ZipEntryTransformerService.packagingZipFile(dependencyLibPath, zipResourceZipEntryTransformer, packagingZipOutput, false);
				}
				// runtimeOnly Jar资源
				for (String runtimeOnlyJarPath : getScopeTypeQuerier().getRuntimeOnlyLibs()) {
					ZipEntryTransformerService.packagingZipFile(runtimeOnlyJarPath, dexZipEntryTransformer, packagingZipOutput, false);
				}

			}

			/**
			 * 返回未优化,未签名时的apk输出文件，并清除已有缓存
			 */
			private File getUnZipAlignSignerApkFile(boolean delete) {
				String getUnZipAlignSignerApkPath = getOutFilePath() + "-unzipaligned-unsigned";
				File unZipAlignUnSignerApkFile = new File(getUnZipAlignSignerApkPath);
				if (delete && unZipAlignUnSignerApkFile.exists()) {
					unZipAlignUnSignerApkFile.delete();
					//直接返回，文件存在所以父目录也存在
					return unZipAlignUnSignerApkFile;
				}

				File parentFile = unZipAlignUnSignerApkFile.getParentFile();
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
				return unZipAlignUnSignerApkFile;
			}
			/**
			 * 返回未未签名apk的文件，并清除已有缓存
			 */
			private File getUnSignedApkFile(boolean delete) {
				File unSignedApkFile = new File(getOutFilePath() + "-unsigned");
				File parentFile = unSignedApkFile.getParentFile();
				//删除输出
				if (delete && unSignedApkFile.exists()) {
					unSignedApkFile.delete();
					return unSignedApkFile;
				}

				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
				return unSignedApkFile;
			}
		}
	}
	public abstract class PackagingWorkerWrapper extends ExternalPackagingService.ExternalPackagingServiceWorker {

		public 
		ExternalPackagingService externalPackagingService;
		private String noBackupFilesDirPath;
		public PackagingWorkerWrapper(ExternalPackagingService externalPackagingService) {
			//父类没有使用封闭类[ExternalPackagingService]
			this.externalPackagingService = externalPackagingService;
		}



		private String libEnsureCapacityPath;
		public String getLibEnsureCapacityPathPath() {
			if (this.libEnsureCapacityPath == null) {
				this.libEnsureCapacityPath = externalPackagingService.getApplicationInfo().nativeLibraryDir + "/libEnsureCapacity.so";
			}
			return this.libEnsureCapacityPath;
		}

		public String getUserAndroidJar() {
			String defaultAndroidJar = getNoBackupFilesDirPath() + "/.aide/android.jar";
			
			String userAndroidJar = ZeroAicySetting.getDefaultSpString("user_androidjar", noBackupFilesDirPath);
			
			
			if (TextUtils.isEmpty(userAndroidJar)) {
				return defaultAndroidJar;
			}
			// 确保androidJar文件存在
			File file = new File(userAndroidJar);
			if (!file.exists() || !file.isFile()) {
				return defaultAndroidJar;
			}
			return userAndroidJar;
		}

		/**
		 * no_backup路径
		 */
		public String getNoBackupFilesDirPath() {
			if (noBackupFilesDirPath == null) {
				noBackupFilesDirPath = externalPackagingService.getNoBackupFilesDir().getAbsolutePath();
			}
			return this.noBackupFilesDirPath;
		}

		@Override
		public final void Zo(String str, String[] strArr, String[] strArr2, String[] strArr3, String str2, String str3, String str4, String[] strArr4, String str5, String str6, String str7, String str8, String str9, boolean z, boolean z2, boolean z3) {
			if (this.Hw == null) {
				this.Hw = new ArrayList<>();
			}
			//添加打包任务
			this.Hw.add(getTaskWrapper(str, strArr, strArr2, strArr3, str2, str3, str4, strArr4, str5, str6, str7, str8, str9, z, z2, z3));
		}

		public abstract TaskWrapper getTaskWrapper(String str, String[] strArr, String[] strArr2, String[] strArr3, String str2, String str3, String str4, String[] strArr4, String str5, String str6, String str7, String str8, String str9, boolean z, boolean z2, boolean z3);

		public abstract class TaskWrapper extends Task {
			//class转dex默认输出目录
			private final String defaultClassDexCacheDirPath;

			private final String defaultIntermediatesDirPath;

			/**
			 * android:extractNativeLibs="false"必须无压缩
			 */
			private boolean androidFxtractNativeLibs = true;
			// 低于21时d8无法dexing AIDE产生的class文件
			private int minSdk = 21;

			/*************************参数*********************************/
			// 主项目.class file缓存目录
			private final String mainClassCacheDir;
			//所有class文件，顶级父目录
			private String[] allClassFileRootDirs;

			//构建时缓存文件的父目录
			private String outDirPath;
			// 若为gradle项目才有
			private ZeroAicyBuildGradle zeroAicyBuildGradle;

			//构建文件地址[类型分为apk和zip]
			private String outFilePath;
			// .ap_文件(aapt2 link产物)
			private String aaptResourcePath;


			//项目的jardex默认目录
			private String defaultJarDexDirPath;

			//所有依赖库
			private String[] dependencyLibs;
			// 使用替代
			private ScopeTypeQuerier scopeTypeQuerier;

			//源码目录
			private String[] sourceDirs;
			//源码目录
			private String[] nativeLibDirs;
			//构建刷新
			private final boolean buildRefresh;

			private final String zipalignLibPath;

			private final String signaturePath;

			private final String signaturePassword;

			private final String signatureAlias;

			private final String signatureAliasPassword;

			private String mergerCachePath;

			public int getMinSdk() {
				return this.minSdk;
			}

			public String getZipalignLibPath() {
				return this.zipalignLibPath;
			}

			protected final boolean isNotDebugFormAIDE;

			public TaskWrapper(String mainClassCacheDir, String[] classFileRootDirs, String[] sourceDirs, String[] dependencyLibs, String outDirPath, String Zo, String aaptResourcePath, String[] nativeLibDirs, String outFilePath, String signaturePath, String signaturePassword, String signatureAlias, String signatureAliasPassword, boolean buildRefresh, boolean Ws, boolean QX) {
				super(mainClassCacheDir, classFileRootDirs, sourceDirs, dependencyLibs, outDirPath, Zo, aaptResourcePath, nativeLibDirs, outFilePath, signaturePath, signaturePassword, signatureAlias, signatureAliasPassword, buildRefresh, Ws, QX);

				this.mainClassCacheDir = mainClassCacheDir;

				File mainProjectClassCacheDirFile = new File(this.mainClassCacheDir);
				String classCacheDirFileName = mainProjectClassCacheDirFile.getName();

				File parentFile = mainProjectClassCacheDirFile.getParentFile();
				this.defaultIntermediatesDirPath = new File(parentFile, "intermediates").getAbsolutePath();

				this.defaultClassDexCacheDirPath = this.getIntermediatesChildDirPath(classCacheDirFileName);
				this.mergerCachePath = getIntermediatesChildDirPath("dex");

				this.allClassFileRootDirs = classFileRootDirs;
				this.sourceDirs = sourceDirs;
				this.dependencyLibs = dependencyLibs;

				//  build/bin | bin/[debug | release]/dex
				this.outDirPath = outDirPath;
				this.zeroAicyBuildGradle = getZeroAicyBuildGradle();

				//Zo
				this.defaultJarDexDirPath = this.getIntermediatesChildDirPath("jardex");

				this.aaptResourcePath = aaptResourcePath;
				this.nativeLibDirs = nativeLibDirs;
				this.outFilePath = outFilePath;

				this.signaturePath = signaturePath;
				this.signaturePassword = signaturePassword;
				this.signatureAlias = signatureAlias;
				this.signatureAliasPassword = signatureAliasPassword;

				this.buildRefresh = buildRefresh;

				this.minSdk = this.getProjectMinSdk();
				this.zipalignLibPath = externalPackagingService.getApplicationInfo().nativeLibraryDir + "/libzipalign.so";


				// GradleProject输出是classesdebug 也没debug-aide
				this.isNotDebugFormAIDE = !classCacheDirFileName.endsWith("debug");
			}

			private int getProjectMinSdk() {
				final int defaultProjectMinSdk = 21;
				final int projectMinSdk;
				if (isAndroidProject()) {
					AndroidManifestParser androidManifestParser = getAndroidManifestParser();
					if (androidManifestParser == null) {
						projectMinSdk = defaultProjectMinSdk;
					} else {
						this.androidFxtractNativeLibs = androidManifestParser.getExtractNativeLibs();

						String minSdkVersion = androidManifestParser.getMinSdkVersion();
						projectMinSdk = Utils.parseInt(minSdkVersion, defaultProjectMinSdk);
					}
				} else {
					//Java项目为当前设备
					projectMinSdk = ZeroAicySetting.getJavaProjectMinSdkLevel();
				}
				return projectMinSdk;
			}

			/**
			 * 在outDirPath赋值后
			 */
			public ZeroAicyBuildGradle getZeroAicyBuildGradle() {
				if (zeroAicyBuildGradle != null) {
					return zeroAicyBuildGradle;
				}
				ZeroAicyBuildGradle singleton = ZeroAicyBuildGradle.getSingleton();

				String buildOutDirPath = this.getBuildOutDirPath();

				// 不是gradle项目
				String suffix = "/build/bin";

				if (!buildOutDirPath.endsWith(suffix)) {
					this.zeroAicyBuildGradle = singleton;
					return singleton;
				}

				File buildGradleFile = new File(buildOutDirPath.substring(0, buildOutDirPath.length() - suffix.length()), "build.gradle");
				if (!buildGradleFile.exists()) {
					this.zeroAicyBuildGradle = singleton;
					return singleton;				
				}
				return this.zeroAicyBuildGradle = singleton.getConfiguration(buildGradleFile.getAbsolutePath());
			}

			private AndroidManifestParser getAndroidManifestParser() {
				String buildOutDirPath = getBuildOutDirPath();
				AndroidManifestParser androidManifestParser = null;  
				for (String androidManifestAbsolutePath : new String[]{"injected/AndroidManifest.xml","merged/AndroidManifest.xml","../AndroidManifest.xml"}) {
					File androidManifestXmlFile = new File(buildOutDirPath, androidManifestAbsolutePath);
					if (androidManifestXmlFile.exists()) {
						androidManifestParser = AndroidManifestParser.get(androidManifestXmlFile);
						break;
					}
				}
				return androidManifestParser;
			}

			public boolean isMinifyEnabled() {
				return this.isNotDebugFormAIDE
					&& isGradleProject() 
					&& getZeroAicyBuildGradle().isMinifyEnabled();
			}

			public boolean isGradleProject() {
				return !getZeroAicyBuildGradle().isSingleton();
			}

			public boolean isAndroidProject() {
				return getOutFilePath().endsWith(".apk");
			}

			public boolean isJavaGradleProject() {
				return getOutFilePath().endsWith("/build/bin/classes.dex.zip");
			}

			// intermediates目录地址
			public String getDefaultIntermediatesDirPath() {
				return defaultIntermediatesDirPath;
			}

			public String getMergerCacheDirPath() {
				return this.mergerCachePath;
			}

			//返回 intermediates子文件夹
			public String getIntermediatesChildDirPath(String childDirName) {
				File childFile = new File(getDefaultIntermediatesDirPath(), childDirName);
				if (!childFile.exists() && !childFile.mkdirs()) {
					AppLog.e("Could not create dir: " + childFile);
				}
				return childFile.getAbsolutePath();
			}
			
			@Override
			public final void Mr() {
				try {
					packaging();
				}
				catch (Throwable e) {
					// 将错误信息保存到日志中
					AppLog.e("packaging()", e);

					if (e instanceof Error) {
						throw (Error)e;
					}
					if (e instanceof RuntimeException) {
						throw (RuntimeException)e;
					}

					throw new Error(e);
				}
			}
			/*************************抽象层，屏蔽所有父类方法*********************************/
			/**
			 * 打包
			 */
			public abstract void packaging() throws Throwable;
			public abstract void packagingJavaProject(List<String> dexZipPathList) throws Throwable;
			public abstract void packagingAndroidProject(List<String> dexZipPathList) throws Throwable;
			/**
			 * d8 class jar 并返回classesDexZip[d8输出zip]
			 */
			public abstract List<String> getClassesDexZipList() throws Throwable;


			/******************************************/
			/**
			 * 返回android:extractNativeLibs="false"的值
			 */
			public boolean getAndroidFxtractNativeLibs() {
				return this.androidFxtractNativeLibs;
			}

			/**********共用层，共用一些相同的代码逻辑***********************************/

			/**
			 * 是否启用混淆安卓项目
			 */
			public boolean isMinifyAndroidProject() {
				// 不是debug-aide变体 无法简单区分 debug 与 release
				// 可以从用 sp的ProjectService 但是 sp不能跨进程
				// 还是算了
				return isAndroidProject() 
					&& isMinifyEnabled();
			}


			/**
			 * 是否启用混淆JavaGradle项目
			 */
			public boolean isMinifyJavaGradleProject() {
				// 不是debug-aide变体 无法简单区分 debug 与 release
				// 可以从用 sp的ProjectService 但是 sp不能跨进程
				// 还是算了

				return this.isJavaGradleProject() 
					&& this.isMinifyEnabled();
			}

			/**
			 * 根据isBuildRefresh是否增量
			 */
			public void fillClassFileCache(String classCacheRootDirPath, List<String> incrementalClassFiles, Set<String> classFileSet) {
				// 规范文件路径信息
				// bug compile project(':/xxxxx/xxx -> 导致classCacheRootDirPath 为 //xxxxx/xxx
				classCacheRootDirPath = new File(classCacheRootDirPath).getAbsolutePath();
				
				fillClassFileCacheMap(classCacheRootDirPath, new File(classCacheRootDirPath), incrementalClassFiles, classFileSet, this.isBuildRefresh());
			}
			/**
			 * 默认不增量
			 */
			public void fillAllClassFileCache(String classCacheRootDirPath, List<String> incrementalClassFiles, Set<String> classFileSet) {
				fillClassFileCacheMap(classCacheRootDirPath, new File(classCacheRootDirPath), incrementalClassFiles, classFileSet, true);
			}

			/**
			 * 递归填充classCacheRootDirPath目录下的所有class文件
			 */
			public void fillClassFileCacheMap(String classCacheRootDirPath, File dirFile, List<String> incrementalClassFiles, Set<String> classFileSet, boolean isBuildRefresh) {
				File[] listFiles = dirFile.listFiles();
				if (listFiles == null) {
					return;
				}

				for (File classFile : listFiles) {
					if (classFile.isDirectory()) {
						fillClassFileCacheMap(classCacheRootDirPath, classFile, incrementalClassFiles, classFileSet, isBuildRefresh);
						continue;
					}

					if (classFile.isFile()) {
						String classFilePath = classFile.getPath();
						if (!getFileName(classFilePath).toLowerCase().endsWith(".class")) {
							continue;
						}

						String classFileSubPath = classFilePath.substring(classCacheRootDirPath.length());
						if (classFileSet.contains(classFileSubPath)) {
							//AppLog.d("忽略重复 .class 文件 " + classFilePath);
							continue;
						}
						classFileSet.add(classFileSubPath);

						if (isBuildRefresh) {
							//构建刷新，直接添加
							incrementalClassFiles.add(classFilePath);
							continue;
						}

						//判断是否更新
						String classDexFileCache = getClassDexFileCache(classFileSubPath);

						File dexFile = new File(classDexFileCache);
						if (classFile.lastModified() > dexFile.lastModified()) {
							//需要重新dexing的class
							incrementalClassFiles.add(classFilePath);
						}
					}
				}
			}
			/**
			 * 签名路径
			 */
			public String getSignaturePath() {
				return this.signaturePath;
			}
			//签名密码
			public String getSignaturePassword() {
				return signaturePassword;
			}
			//别名
			public String getSignatureAlias() {
				return signatureAlias;
			}
			//别名密码
			public String getSignatureAliasPassword() {
				return signatureAliasPassword;
			}

			/**
			 * 构建刷新
			 */
			public boolean isBuildRefresh() {
				return this.buildRefresh;
			}

			public String[] getAllClassFileRootDirs() {
				return this.allClassFileRootDirs;
			}
			/**
			 * 返回主项目class文件缓存路径，即主项目class文件的输出目录
			 */
			public String getMainClassCacheDir() {
				return this.mainClassCacheDir;
			}
			/**
			 * 返回默认dex文件缓存路径，[从class文件dexing的]
			 */
			public String getDefaultClassDexCacheDirPath() {
				return defaultClassDexCacheDirPath;
			}
			public String getDefaultClassDexGlobalsPath() {
				return getDefaultClassDexCacheDirPath() + "/globals.zip";
			}
			

			/**
			 * 返回class dexing后的dex缓存路径
			 */
			public String getClassDexFileCache(String classFileSubPath) {
				int endIndex = classFileSubPath.length() - ".class".length();
				return getDefaultClassDexCacheDirPath() 
					+ classFileSubPath.substring(0, endIndex) + ".dex";
			}
			/**
			 * 返回jar的dex.zip缓存路径
			 */
			public String getJarDexCachePath(String jarFilePath) {
				String jarDexCacheDirPath = getJarDexCacheDirPath(jarFilePath);
				// 

				File jarFile = new File(jarFilePath);
				String depFileName = jarFile.getName();
				if (jarFilePath.endsWith(".exploded.aar/classes.jar")) {
					String name = jarFile.getParentFile().getName();
					depFileName = name.substring(0, name.length() - ".exploded.aar".length()) + ".jar";
				} else {

				}
				String jarDexZipName = depFileName + ".dex.zip";

				//不会为null
				if (jarDexCacheDirPath == null) {
					jarDexCacheDirPath = getDefaultJarDexDirPath();
				}
				return jarDexCacheDirPath + "/" +  jarDexZipName;
			}

			/**
			 * 返回 getIntermediatesChildDirPath("jardex")目录下路径的md5码
			 * 如果是maven仓库则去掉仓库路径在进行计算
			 */
			public String getJarDexCacheDirPath(String dependencyLibPath) {
				String userM2repositories = ZeroAicySetting.getDefaultSpString("user_m2repositories", null);
				String defaultM2repositoriesDirPath = getNoBackupFilesDirPath() + "/.aide/maven";

				String key;
				/**
				 * 是否是在自定义仓库缓存文件夹中
				 */
				if (!TextUtils.isEmpty(userM2repositories) 
					&& dependencyLibPath.startsWith(userM2repositories)) {
					//指定了minsdk，会污染缓存，所以只能放项目目录
					key = dependencyLibPath.substring(userM2repositories.length());
				} else if (dependencyLibPath.startsWith(defaultM2repositoriesDirPath)) {
					key = dependencyLibPath.substring(defaultM2repositoriesDirPath.length());
				} else {
					key = dependencyLibPath;	
				}

				return getIntermediatesChildDirPath("jardex/" + MD5Util.stringMD5(key));
			}
			/**
			 * 全部依赖，不只是jar
			 */
			public String[] getAllDependencyLibs() {
				return this.dependencyLibs;
			}
			public ScopeTypeQuerier getScopeTypeQuerier() throws Throwable {
				if (this.scopeTypeQuerier == null) {
					this.scopeTypeQuerier = new ScopeTypeQuerier(this.dependencyLibs, getZeroAicyBuildGradle());
				}
				return this.scopeTypeQuerier;
			}

			public String getDefaultJarDexDirPath() {
				return this.defaultJarDexDirPath;
			}

			public String[] getSourceDirs() {
				return this.sourceDirs;
			}
			public String[] getNativeLibDirs() {
				return this.nativeLibDirs;
			}
			/**
			 * 输出
			 */
			public String getBuildOutDirPath() {
				return this.outDirPath;
			}

			/**
			 * 返回输出文件路径
			 * 可以在此处实现将apk生成项目缓存路径中
			 */
			public String getOutFilePath() {
				return this.outFilePath;
			}

			//resource.ap_文件
			public String getAAptResourceFilePath() {
				if (getZeroAicyBuildGradle().isShrinkResources()) {
					String resourcesAp_Dir = new File(this.aaptResourcePath).getParent();
					return new File(resourcesAp_Dir, "resources_optimize.ap_").getAbsolutePath();
				}
				return this.aaptResourcePath;
			}

			/**
			 * 显示构建总进度，只有一级进度
			 */
			public void showProgress(String progressTitle, int progress) {
				ExternalPackagingService.ExternalPackagingServiceWorker.v5(PackagingWorkerWrapper.this, progressTitle + "...", progress);
				//super.a8(progressTitle, progress);
			}
		}
	}

}
