package io.github.zeroaicy.aide.services;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class DexingJarTask implements Callable<DexingJarTask>{
	
	
	/**
	 * 配置类
	 */
	public static class Configuration{
		// dex版本
		public int minSdkVersion;
		// android sdk jar路径
		public String user_android_jar;
		// 库依赖
		public List<String> dependencyLibs;
		// 进度
		public AtomicInteger dexingingCount;
	}

	public static interface TaskDoneLister{
		public abstract void done();
	}

	public static final String ThreadPoolServiceName = DexingJarTask.class.getName();
	private static Map<String, String> environment;
	public static void init(Map<String, String> environment){
		DexingJarTask.environment = environment;
	}
	// 输入
	public final String inputJarFile;
	// 输出
	public final String outputDexZipFile;
	
	public final List<String> inputJarFiles;
	public final List<String> outputDexZipFiles;

	public final Configuration configuration;

	TaskDoneLister taskDoneLister;
	final boolean isBatchMode;
	
	public DexingJarTask(String inputJarFiles, String outputDexZipFile, Configuration configuration){
		this.isBatchMode = false;

		this.inputJarFiles = null;
		this.outputDexZipFiles = null;
		
		this.inputJarFile = inputJarFiles;
		this.outputDexZipFile = outputDexZipFile;
		
		this.configuration = configuration;
	}
	
	public DexingJarTask(List<String> inputJarFiles, List<String> outputDexZipFiles, Configuration configuration){
		
		this.isBatchMode = true;
		
		this.inputJarFile = null;
		this.outputDexZipFile = null;
		
		
		this.inputJarFiles = inputJarFiles;
		this.outputDexZipFiles = outputDexZipFiles;
		this.configuration = configuration;
	}
	

	public void setTaskDoneLister(TaskDoneLister taskDoneLister){
		this.taskDoneLister = taskDoneLister;
	}

	@Override
	public DexingJarTask call() throws Exception{
		try{
			if( isBatchMode){
				dexingJarLibFileBatch(this.inputJarFiles, this.outputDexZipFiles, configuration);
				// 更改进度
				configuration.dexingingCount.addAndGet(inputJarFiles.size());
				
			}else{
				dexingJarLibFile(inputJarFile, outputDexZipFile, configuration);
				
				configuration.dexingingCount.incrementAndGet();
			}
			// 就用一次
			if ( this.taskDoneLister != null ){
				this.taskDoneLister.done();
				this.taskDoneLister = null;
			}
		}
		catch (Throwable th){
			if ( th instanceof Error ) throw (Error)th; 
			if ( th instanceof Exception ) throw (Exception)th; 
			else throw new Error(th);
		}
		return this;
	}


	/**
	 * dexing库
	 */

	public static void dexingJarLibFile(String jarLibPath, String dexCachePath, Configuration configuration) throws Throwable{
		// dex版本
		int minSdkVersion;
		// android sdk jar路径
		String user_android_jar;
		// 库依赖
		List<String> dependencyLibs;

		minSdkVersion = configuration.minSdkVersion;
		user_android_jar = configuration.user_android_jar;
		dependencyLibs = configuration.dependencyLibs;

		// out
		File dexCacheFile = new File(dexCachePath);

		File dexZipTempFile = File.createTempFile(dexCacheFile.getName(), ".dex.zip", dexCacheFile.getParentFile());
		//更新时间
		dexZipTempFile.setLastModified(System.currentTimeMillis());

		List<String> argsList = new ArrayList<>();
		D8TaskWrapper.fillD8Args(argsList, minSdkVersion, false, true, user_android_jar, dependencyLibs, dexZipTempFile.getAbsolutePath());
		
		
		argsList.add("--globals-output");
		argsList.add(dexCachePath + "--globals.zip");
		//添加需要编译的jar(输入文件)
		argsList.add(jarLibPath);

		try{
			logDebug("dexing -> " + jarLibPath);
			//dexing jar
			// 大于10MB的将采用 子进程方式，防止oom
			D8TaskWrapper.runD8Task(argsList, DexingJarTask.environment, new File(jarLibPath).length() > 10 * 1024 * 1024);

			//临时文件移动到实际输出文件
			dexZipTempFile.renameTo(dexCacheFile);
			//删除
			dexZipTempFile.delete();

			//更新文件修改时间
			dexCacheFile.setLastModified(System.currentTimeMillis() + 10);

		}
		catch (Throwable e){
			throw e;
		}
		finally{
			//删除缓存
			dexZipTempFile.delete();
		}
	}


	public static void dexingJarLibFileBatch(List<String> inputJarFiles, List<String> outputDexZipFiles, Configuration configuration) throws Throwable{
		// dex版本
		int minSdkVersion;
		// android sdk jar路径
		String user_android_jar;
		// 库依赖
		List<String> dependencyLibs;

		minSdkVersion = configuration.minSdkVersion;
		user_android_jar = configuration.user_android_jar;
		dependencyLibs = configuration.dependencyLibs;

		// 通用参数
		List<String> argsList = new ArrayList<>();

		//待跟随minSDK
		argsList.add("--min-api");
		minSdkVersion = Math.max(minSdkVersion, 21);
		argsList.add(String.valueOf(minSdkVersion));

		argsList.add("--intermediate");

		argsList.add("--lib");
		argsList.add(user_android_jar);

		if ( dependencyLibs != null ){
			for ( String librarie : dependencyLibs ){
				argsList.add("--classpath");
				argsList.add(librarie);
			}
		}
		
		// 批量处理
		D8TaskWrapper.runD8BatchTask(inputJarFiles, outputDexZipFiles, argsList, DexingJarTask.environment);
		
	}

	private static final String TAG = "Worker";
	private static void logDebug(String msg){
		Log.i(TAG, msg);
	}

}
