package io.github.zeroaicy.aide.services;

import android.text.TextUtils;
import com.aide.common.AppLog;
import com.aide.ui.services.AssetInstallationService;
import dalvik.system.DexClassLoader;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.util.IOUtils;
import io.github.zeroaicy.util.reflect.ReflectPie;
import io.github.zeroaicy.util.reflect.ReflectPieException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class D8TaskWrapper {

	public static final String R8Task = "io.github.zeroaicy.r8.R8Task";
	public static final String D8Task = "io.github.zeroaicy.r8.D8Task";

	public static final String D8BatchTask = "io.github.zeroaicy.r8.D8BatchTask";

	public static final String TAG = "D8TaskWrapper";
	
	public static void runD8Task(List<String> argList) throws Throwable {

		// 使用 app_process运行 d8 || r8

		// 临时先使用 这个
		// com.android.tools.r8.D8.main(argList.toArray(new String[argList.size()]));
		run(D8Task, argList);

	}

	public static void runD8Task(List<String> argList, Map<String, String> environment) throws Throwable {
		run(D8Task, argList, environment);
	}
	
	public static void runD8Task(List<String> argList, Map<String, String> environment, boolean processMode) throws Throwable {
		run(D8Task, argList, environment, processMode);
	}
	
	/**
	 * 编译多个jar且输出多个路径
	 * argList 为通用配置 run minsdk android_sdk路径等
	 * 不可有 --output及输入文件
	 */
	public static void runD8BatchTask(List<String> inputFiles, List<String> outputFiles, List<String> argList, Map<String, String> environment) throws Throwable {
		// 输出
		argList.add(String.join("|", outputFiles));
		// 输入
		argList.add(String.join("|", inputFiles));

		run(D8BatchTask, argList, environment);

	}

	public static void runR8Task(List<String> argList) throws Throwable {

		// 使用 app_process运行 d8 || r8
		run(R8Task, argList, Collections.<String, String>emptyMap());

	}
	public static void runR8Task(List<String> argList, Map<String, String> environment) throws Throwable {

		// 使用 app_process运行 d8 || r8
		run(R8Task, argList, environment);

	}


	private static void run(String className, List<String> argList) throws Throwable {
		run(className, argList, Collections.<String, String>emptyMap());
	}

	private static DexClassLoader r8DexClassLoader;
	
	private static void run(String className, List<String> argList, Map<String, String> environment) throws Throwable {
		run(className, argList, environment, false);
	}
	private static void run(String className, List<String> argList, Map<String, String> environment, boolean processMode) throws Throwable {
		AppLog.println_d("d8 classname: %s ", className);
		AppLog.println_d("d8 argList: %s ", argList);
		
		String r8Path = AssetInstallationService.DW("com.android.tools.r8.zip", true);
		// 去除写入权限
		File r8ZipFile = new File(r8Path);
		if (r8ZipFile.canWrite()) {
			r8ZipFile.setWritable(false);
		}
		if (!processMode && !R8Task.equals(className)) {
			// 只有r8采用线进程方式
			// D8Task D8BatchTask 采用动态加载dex的方式运行
			// 这样可能有dex2oat优化
			if (r8DexClassLoader == null) {
				r8DexClassLoader = new DexClassLoader(r8Path, null, null, D8TaskWrapper.class.getClassLoader());
			}
			List<String> cmdList = new ArrayList<String>();
			// 方便改变线程数
			// 都启用多线程dexing ❛˓◞˂̵✧
			cmdList.add("--thread-count");
			cmdList.add("16");
			// 参数
			cmdList.addAll(argList);
			String[] args = cmdList.toArray(new String[cmdList.size()]);
			try {
				ReflectPie.onClass(className, r8DexClassLoader).call("main", new Object[]{args});
			}
			catch (ReflectPieException e) {
				Throwable cause = e.getCause();
				if (cause != null) {
					Throwable cause2 = cause.getCause();
					if (cause2 != null) {
						cause = cause2;						
					}
				}
				
				throw cause;
			}catch (Throwable e) {
				
				throw e;
			}
			AppLog.d(TAG, "D8Task | D8BatchTask 退出正常");
			return;
		}

		// /system/bin/app_process -Djava.class.path="r8Path" /system/bin --nice-name=R8Task io.github.zeroaicy.r8.R8Task "$@"
		ArrayList<String> cmdList = new ArrayList<String>();
		cmdList.add("app_process");
		cmdList.add("-Djava.class.path=" + r8Path);
		cmdList.add("/system/bin");
		cmdList.add("--nice-name=D8Task");


		// 需要运行的类
		cmdList.add(className);

		// 方便改变线程数
		// 都启用多线程dexing ❛˓◞˂̵✧
		cmdList.add("--thread-count");
		cmdList.add("16");

		// 参数
		cmdList.addAll(argList);


		//*
		String[] args = cmdList.toArray(new String[cmdList.size()]);
		//System.out.println(cmdList);
		run(className, args, environment, false);
	}

	private static void run(String className, String[] args, Map<String, String> environment, boolean isExceptionHandling) throws Throwable {

		ProcessBuilder processBuilder = new ProcessBuilder(args);
		if (environment != null) {
			processBuilder.environment().putAll(environment);
		}


		// 运行进程
		Process process = processBuilder.start();

		// 读取错误流
		ProcessStreamReader errorStreamReader = new ProcessStreamReader(process.getErrorStream(), ZeroAicySetting.isEnableDetailedLog());
		Thread errorStreamReaderThread = new Thread(errorStreamReader);
		errorStreamReaderThread.start();

		// 读取输出流
		ProcessStreamReader inputStreamReader = new ProcessStreamReader(process.getInputStream());
		Thread inputStreamReaderThread = new Thread(inputStreamReader);
		inputStreamReaderThread.start();		

		// 等待 r8进程运行完
		// 再此之前必须读取输出流和错误流
		// 否则缓存池用完会阻塞
		process.waitFor();
		int exitValue = process.exitValue();
		// 正常退出
		if (exitValue == 0) {
			return;
		}
		// 异常处理 可能会再次运行
		//已经是在处理异常了 及时退出否则死递归了
		// int[] exceptionCodes = new int[]{134, 13};
		if (!isExceptionHandling 
			&& (exitValue == 134 || exitValue == 139)) {
			// 扩容库储存
			// 禁用扩容
			ZeroAicySetting.disableEnableEnsureCapacity();
			environment.remove("EnsureCapacity");
			// 再次运行 以处理异常的方式
			run(className, args, environment, true);
		}

		String error = errorStreamReader.getError();

		//String output = inputStreamReader.getError();

		String format = String.format(
			"\nTask: %s -> exited with code %s\nError:\n%s\n", 
			className, process.exitValue(), error);


		if (exitValue == 137) {
			throw new OutOfMemoryError("r8 task exited code 137可能是OOM\n" + format);
		}
		throw new Error(format);

	}


	public static class ProcessStreamReader implements Runnable {

		public static String TAG = "ProcessStreamReader";
		//BufferedInputStream bufferedInputStream;
		// ByteArrayOutputStream byteArrayOutputStream;

		BufferedReader bufferedReader;
		StringBuilder stringBuilder = new StringBuilder();

		boolean isErrorStream;
		public ProcessStreamReader(InputStream inputStream) {
			this(inputStream, false);
		}

		public ProcessStreamReader(InputStream inputStream, boolean isErrorStream) {
			//this.bufferedInputStream = new BufferedInputStream(inputStream);
			//this.byteArrayOutputStream = new ByteArrayOutputStream();
			this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			this.isErrorStream = isErrorStream;
		}

		String error;
		public String getError() {
			return this.error;
		}
		@Override
		public void run() {
			try {
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					// 边运行边打印
					if (isErrorStream) AppLog.println_d(line);

					stringBuilder.append(line);
					stringBuilder.append(System.lineSeparator());
				}
			}
			catch (Throwable e) {
				AppLog.e(TAG, e);
			}
			finally {
				// 关闭流
				//IOUtils.close(this.bufferedInputStream);
				IOUtils.close(this.bufferedReader);
				this.error = stringBuilder.toString();
			}
		}
	}

	public static void fillD8Args(List<String> argsList, int minSdk, boolean file_per_class_file, boolean intermediate, String user_androidjar, List<String> dependencyLibs, String outPath) {
		// 都启用多线程dexing ❛˓◞˂̵✧
		argsList.add("--min-api");

		minSdk = Math.max(minSdk, 21);
		//待跟随minSDK
		argsList.add(String.valueOf(minSdk));

		if (file_per_class_file) {
			argsList.add("--file-per-class-file");
		}
		if (intermediate) {
			argsList.add("--intermediate");
		}
		if (!TextUtils.isEmpty(user_androidjar)) {
			argsList.add("--lib");
			argsList.add(user_androidjar);
		}

		if (dependencyLibs != null) {
			for (String librarie : dependencyLibs) {
				argsList.add("--classpath");
				argsList.add(librarie);
			}
		}
		argsList.add("--output");
		argsList.add(outPath);
	}
}
