package io.github.zeroaicy.aide.aapt2;
import android.content.Context;
import android.content.SharedPreferences;
import com.aide.ui.build.android.AaptService;
import com.aide.ui.build.android.AaptService$ErrorResult;
import com.aide.ui.build.android.AaptService$Task;
import com.aide.ui.services.AssetInstallationService;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.utils.Utils;
import io.github.zeroaicy.aide.utils.ZeroAicyBuildGradle;
import io.github.zeroaicy.util.ContextUtil;
import io.github.zeroaicy.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AaptServiceArgs{

	AaptService AaptService;

	AaptService$Task task;

	public final String buildBin;


	private final File intermediates;
	private final File resDirOut;
	private final File compileDirFile;
	private final File mergedDirFile;

	//每个res路径的flatZip
	public final Set<String> flatZipFileSet = new LinkedHashSet<String>();
	//每个res路径的flat目录
	public final Set<String> flatDirSet = new HashSet<String>();

	public final PrintStream aaptLog;

	public final int defaultMinSdk;
	public final int defaultTargetSdk;


	private DataBindingBuilderProxy dataBindingBuilder;


	final boolean isEnableViewBinding;
	public boolean isEnableViewBinding(){
		return this.isEnableViewBinding;
	}
	final boolean useAndroidx;
	public boolean isUseAndroidx(){
		return this.useAndroidx;
	}

	public DataBindingBuilderProxy getDataBindingBuilder(){
		if ( this.dataBindingBuilder == null ){
			this.dataBindingBuilder = new DataBindingBuilderProxy(this);
		}
		return this.dataBindingBuilder;
	}

	public static SharedPreferences getProjectService(){
        return ContextUtil.getContext().getSharedPreferences("ProjectService", Context.MODE_PRIVATE);
    }
    public static String getCurrentAppHome(){
        return getProjectService().getString("CurrentAppHome", null);
    }

	//自定义数据
	public boolean isGradleProject;

	//主项目的res
	public String mainProjectResPath;

	public final boolean shrinkResources;

	public AaptServiceArgs(AaptService$Task task){
		this.task = task;

		String currentAppHome = getCurrentAppHome();

		String buildGradlePath = currentAppHome + "/build.gradle";

		ZeroAicyBuildGradle buildGradle = ZeroAicyBuildGradle.getSingleton();
		try{
			if ( FileSystem.exists(buildGradlePath) ){
				isGradleProject = true;
				buildGradle = buildGradle.getConfiguration((buildGradlePath));
			}
		}
		catch (Throwable e){}

		if ( !buildGradle.isSingleton() ){
			// 混淆代码
			this.shrinkResources = buildGradle.isShrinkResources();

			// 渠道包 暂时不用渠道包的 minsdk
			this.defaultMinSdk = Utils.parseInt(buildGradle.getMinSdkVersion(null), 14);
			this.defaultTargetSdk = Utils.parseInt(buildGradle.getTargetSdkVersion(null), 28);
			this.isEnableViewBinding = buildGradle.isViewBindingEnabled();
			this.useAndroidx = buildGradle.isUseAndroidx();
			
		}else{
			this.shrinkResources = false;

			this.defaultMinSdk = 14;
			this.defaultTargetSdk = 28;
			this.isEnableViewBinding = false;
			this.useAndroidx = true;
		}


		// ((Boolean)argsRef.get("aM")).booleanValue();
		this.isBuildRefresh = task.isBuildRefresh;

		// argsRef.get("Hw");
		this.androidJar = task.androidSdkFilePath;

		//resource.ap_
		// argsRef.get("gn");
		this.resourcesApPath = task.resourcesApPath;

		// 构建缓存路径
		this.buildBin = new File(this.resourcesApPath).getParent();
		// 日志输出
		this.aaptLog = new PrintStream(Log.AsyncOutputStreamHold.createOutStream(new File(buildBin, "intermediates/aapt_log.log")));

		//gen查找packageName
		// argsRef.get("EQ");
		this.genPackageNameMap = task.genPackageNameMap;

		//gen对应的 res(包含res依赖，[0]为gen所在res)
		// argsRef.get("tp");
		this.genResDirsMap = task.genResDirsMap;

		// fullCustomVar();


		//主项目gen目录
		// argsRef.get("Zo");
		this.mainProjectGenDir = task.mainProjectGenDir;

		//主项目res目录
		List<String> mainResDirs = this.genResDirsMap.get(this.mainProjectGenDir);
		for ( String resDir : mainResDirs ){
			//有时mainResDirs.get(0)是generated
			//本着不修改AIDE给的所有数据，在此过滤
			if ( resDir.endsWith("/res") ){
				this.mainProjectResPath = resDir;
				break;
			}
		}

		//res -> bin的res(正好可以用于DataBinding存放脱糖的xml)
		// argsRef.get("u7");
		this.allResourceMap = task.allResDirMap;

		// argsRef.get("VH");
		List<String> assetDirPaths = task.assetDirPaths;
		if ( assetDirPaths != null ){
			this.assetDirPaths.addAll(assetDirPaths);
		}


		this.intermediates = new File(this.buildBin, "intermediates");
		//aapt2缓存目录
		this.resDirOut = new File(this.intermediates, "res");

		this.compileDirFile = new File(this.resDirOut, "flat");
		this.compileDirFile.mkdirs();

		this.mergedDirFile = new File(this.resDirOut, "merged");
		this.mergedDirFile.mkdirs();


		// 子项目的gen目录
		// argsRef.get("we");
		this.subProjectGens = task.subProjectGens;

		// argsRef.get("J0");
		this.variantManifestPaths = task.variantManifestPaths;

		// argsRef.get("J8");
		this.mergedAndroidManifestMap = task.mergedAndroidManifestMap;

		// argsRef.get("QX");
		this.injectedAndroidManifestMap = task.injectedAndroidManifestMap;

		// argsRef.get("Ws");
		this.androidManifestMap = task.androidManifestMap;

		this.mainPackageName = this.genPackageNameMap.get(this.mainProjectGenDir);

	}

	/**
	 * 填充自定义变量
	 */
	private void fullCustomVar(){
		this.resDirGenDir = new HashMap<>();
		this.packageDependencieMap = new HashMap<>();

		//res查找gen
		for ( Map.Entry<String, List<String>> entry : this.genResDirsMap.entrySet() ){

			String genDir = entry.getKey();

			boolean isGradleProject = genDir.endsWith("/build/gen");
			String projectDir;
			int endIndex;
			if ( isGradleProject ){
				endIndex = genDir.length() - "build/gen".length();
			}else{
				endIndex = genDir.length() - "gen".length();
			}
			projectDir = genDir.substring(0, endIndex);

			//初始化自定义
			for ( String resDir :  entry.getValue() ){
				//排除 generated
				if ( resDir.endsWith("/generated") ){
					continue;
				}
				if ( resDir.startsWith(projectDir) ){
					this.resDirGenDir.put(resDir, genDir);
				}
			}
		}

		for ( Map.Entry<String, List<String>> entry : genResDirsMap.entrySet() ){
			String genDir = entry.getKey();
			String packageName  = genPackageNameMap.get(genDir);
			//packageName依赖的子包名
			List<String> childPackageNames = new ArrayList<>();

			List<String> childResDirs = entry.getValue();

			//0是当前项目res
			for ( int i = 1; i < childResDirs.size(); i++ ){
				String childResDir = childResDirs.get(i);
				if ( childResDir.endsWith("/generated") ){
					continue;
				}

				String childGenDir = this.resDirGenDir.get(childResDir);
				String childPackageName = genPackageNameMap.get(childGenDir);
				childPackageNames.add(childPackageName);
			}
			this.packageDependencieMap.put(packageName, childPackageNames);
		}
	}

	//根据已有推出，res查找gen目录
	private Map<String, String> resDirGenDir;
	//转化为包名依赖
	//根据已有推出，包名查找子包名
	public Map<String, List<String>> packageDependencieMap;

	public final boolean isBuildRefresh;

	public final String androidJar;

	public final String resourcesApPath;

	//主项目gen目录 Zo
	public final String mainProjectGenDir;

	public final List<String> assetDirPaths = new ArrayList<>();

	//genDir -> packageName，但只有子项目，子项目的子项目没有
	public final Map<String, String> genPackageNameMap;

	//所有资源
	public final Map<String, String> allResourceMap;

	public final Map<String, List<String>> genResDirsMap;

	//所有的子项目gen路径
	public final List<String> subProjectGens;
	//所有的variantManifestPaths
	public final List<String> variantManifestPaths;

	public final Map<String, String> mergedAndroidManifestMap;

	public final Map<String, String> injectedAndroidManifestMap;

	public final Map<String, String> androidManifestMap;

	/**
	 * 根据已有数据计算
	 */
	public final String mainPackageName;
	/**
	 * 反射调用元方法
	 */
	public void buildRefresh(){
		// this.argsRef.call("v5");
		this.task.buildRefresh();
	}
	public void generateBuildConfigJava(){
		// this.argsRef.call("Zo");
		this.task.generateBuildConfigJava();

	}

	public String getAapt2Error(abcd.wf j62){
		// return this.argsRef.call("VH", new Object[] {j62.j6(), j62.DW()}).get();
		return this.task.getAaptError(j62.j6(), j62.DW());
	}

	//合并AndroidManifestxml
	public AaptService$ErrorResult mergedAndroidManifestxml(){
		// return this.argsRef.call("EQ").get();
		
		return this.task.mergedAndroidManifestxml();
	}


	public String getIntermediates(){
		if ( !this.intermediates.exists() ){
			this.intermediates.mkdirs();
		}
		return this.intermediates.getAbsolutePath();
	}


	public String getAaptRulesPath(){
		return new File(this.intermediates, "aapt_rules.txt").getAbsolutePath();
	}
	//aapt2输出目录
	public File getResOutFile(){
		if ( !this.resDirOut.exists() ){
			this.resDirOut.mkdirs();
		}
		return this.resDirOut;
	}

	//资源编译输出目录
	public File getFlatDirFile(){
		if ( !compileDirFile.exists() ){
			compileDirFile.mkdirs();
		}
		// intermediates/res/flat
		return compileDirFile;
	}
	public String getCompileDirPath(){

		return getFlatDirFile().getAbsolutePath();
	}
	//资源合并输出目录
	public File getMergedDirFile(){
		if ( !this.mergedDirFile.exists() ){
			this.mergedDirFile.mkdirs();
		}
		return this.mergedDirFile;
	}
	public String getMergedDirPath(){
		return getMergedDirFile().getAbsolutePath();
	}

	// 取得Aapt2路径
	public static String getAapt2Path2(){
		//会从assets自动解压
		String aapt2Path = AssetInstallationService.DW("aapt2", true);
		File aapt2File = new File(aapt2Path);
		if ( !aapt2File.canExecute() ){
			aapt2File.setReadable(true, false);
			aapt2File.setExecutable(true, false);
		}
		return aapt2Path;
    }

	public static String getAapt2Path(){
		return ContextUtil.getContext().getApplicationInfo().nativeLibraryDir + "/libaapt2.so";
	}

	//字段含义
	//Map DW key: 项目路径 value: 依赖的项目(复数)

	//Map EQ key: gen路径 value: 包名

	//String Hw android.jar路径

	//Map J8 key: gen路径 value: merged AndroidManifest.xml路径

	//AaptService Mr

	//Map QX key: gen路径 value: injected AndroidManifest.xml路径

	//List VH assets路径

	//Ws key: injected AndroidManifest.xml路径 value: AndroidManifest.xml路径

	//XL true ?

	//Zo 主项目 gen路径

	//aM false ? 是否 构建缓存清除

	//gn resources.ap_

	//j3 false ?

	//j6 主项目路径 build.gradle父目录

	//Map tp key: gen路径 value: res路径

	//u7 key: res | bin/generated路径  value: build/bin/res路径

	//

	//we 子项目gen路径




	public static List<String> listLine(File file){
		List<String> list = new ArrayList<String>();

		InputStream in = null;
		BufferedReader br = null;
		try{
			in = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ( (line = br.readLine()) != null ){
				list.add(line);
			}
			br.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
		finally{
			try{
				if ( in != null ) in.close();
			}
			catch (IOException e){}

			try{
				if ( br != null ) br.close();
			}
			catch (IOException e){}
		}
		return list;
	}

	public static void writeLines(File outFile, List<String> lines){
		File parentFile = outFile.getParentFile();
		if ( !parentFile.exists() ){
			parentFile.mkdirs();
		}
		try{
			PrintWriter printWriter = new PrintWriter(outFile);
			int size = lines.size();
			for ( int i = 0; i < size; i++ ){
				printWriter.println(lines.get(i));
			}
			printWriter.flush();
			printWriter.close();
		}
		catch (Throwable e){
			e.printStackTrace();
		}
	}

}
