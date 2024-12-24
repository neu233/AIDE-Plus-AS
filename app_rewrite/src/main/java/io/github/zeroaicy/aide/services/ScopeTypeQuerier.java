package io.github.zeroaicy.aide.services;
import com.aide.common.AppLog;
import com.aide.ui.project.internal.GradleTools;
import com.aide.ui.services.MavenService;
import com.aide.ui.util.BuildGradle;
import com.aide.ui.util.BuildGradleExt;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.utils.ZeroAicyBuildGradle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;
import java.io.FileNotFoundException;
import com.aide.ui.services.AssetInstallationService;


/**
 * 目的对 dependencyLibs进行分组
 * 默认支持 compileOnly runtimeOnly libgdxNatives
 * 考虑支持 desugar_libs
 */
/* desugar_libs:{
 validLibs.add("/storage/emulated/0/.MyAicy/.aide/maven/com/android/tools/desugar_jdk_libs/2.0.4/desugar_jdk_libs-2.0.4.jar");
 validLibs.add("/storage/emulated/0/.MyAicy/.aide/maven/com/android/tools/desugar_jdk_libs_configuration/2.0.4/desugar_jdk_libs_configuration-2.0.4.jar");
 } */
public class ScopeTypeQuerier{

	private static final BuildGradleExt buildGradleExt = new BuildGradleExt();
	private static final ZeroAicyBuildGradle singleton = ZeroAicyBuildGradle.getSingleton();

	public enum ScopeType{
		compileOnly,
		runtimeOnly,
		libgdxNatives,
		dexing;
	}
	public static class ScopeTypeMap{
		private final Map<String, ScopeType> fileScopeTypeMap = new HashMap<>();
		private final Map<String, Integer> runtimeOnlySerialMap = new HashMap<>();
		private int runtimeOnlySerial = 0;
		private final Set<String> dirScopeTypeSet = new HashSet<>();
		public void putDir(String key, ScopeType value){
			this.dirScopeTypeSet.add(key);
			this.put(key, value);
		}
		public void put(String key, ScopeType value){
			if ( value == null ) return;
			this.fileScopeTypeMap.put(key, value);
			this.runtimeOnlySerialMap.put(key, runtimeOnlySerial++);

		}
		public ScopeType get(String key){
			if ( key == null ) return null;
			ScopeType value = this.fileScopeTypeMap.get(key);
			if ( value != null ){
				return value;
			}
			for ( String dir : this.dirScopeTypeSet ){
				if ( key.startsWith(dir) ){
					// 使用 目录的序号
					this.runtimeOnlySerialMap.put(key, this.runtimeOnlySerialMap.get(dir));
					// 返回这个目录的 ScopeType
					return this.fileScopeTypeMap.get(dir);
				}
			}
			return null;
		}

		public void sortRuntimeOnly(List<String> runtimeOnlyLibs){
			Collections.sort(runtimeOnlyLibs, new Comparator<String>(){
					@Override
					public int compare(String o1, String o2){
						String o1Name = FileSystem.getName(o1).toLowerCase();
						String o2Name = FileSystem.getName(o2).toLowerCase();
						if ( o1Name.endsWith("_resource.jar") && o2Name.endsWith("_resource.jar") ){
							return compare2(o1Name, o2Name);
						}
						if ( !o1Name.endsWith("_resource.jar") && o2Name.endsWith("_resource.jar") ){
							return -1;
						}
						if ( o1Name.endsWith("_resource.jar") && !o2Name.endsWith("_resource.jar") ){
							return 1;
						}
						Integer o1Serial = ScopeTypeMap.this.runtimeOnlySerialMap.get(o1);
						Integer o2Serial = ScopeTypeMap.this.runtimeOnlySerialMap.get(o2);
						if ( o1Serial == null ){
							//最后打包
							o1Serial = Integer.MAX_VALUE;
						}
						if ( o2Serial == null ){
							//最后打包
							o2Serial = Integer.MAX_VALUE;
						}

						return o1Serial.intValue() - o2Serial.intValue();
					}
					/**
					 * 兼容旧RuntimeOnly排序方案
					 */
					public int compare2(String o1Name, String o2Name){
						int defaultVersion = 0;
						int o1Version = defaultVersion;
						int o2Version = defaultVersion;


						int suffixLength = "_resource.jar".length();

						int versionTempStart = o1Name.lastIndexOf("_", o1Name.length() - suffixLength - 1);

						if ( versionTempStart > 0 ){
							String versionTemp = o1Name.substring(versionTempStart + 1, o1Name.lastIndexOf("_"));
							try{
								o1Version = Integer.parseInt(versionTemp);
							}
							catch (NumberFormatException e){}
						}

						versionTempStart = o2Name.lastIndexOf("_", o2Name.length() - suffixLength - 1);
						if ( versionTempStart > 0 ){
							String versionTemp = o2Name.substring(versionTempStart + 1, o2Name.lastIndexOf("_"));
							try{
								o2Version = Integer.parseInt(versionTemp);
							}
							catch (NumberFormatException e){}
						}
						return o2Version - o1Version;
					}
				});
		}
	}

	// 需要dexing的 
	private final List<String> dexingLibs = new ArrayList<>();
	private final Set<String> dexingLibsSet = new HashSet<>();

	// 仅compile，当做库参与dexing，不打包资源
	private final List<String> compileOnlyLibs = new ArrayList<>();
	private final Set<String> compileOnlyLibsSet = new HashSet<>();

	// 仅打包，不参与compile和dexing
	private final List<String> runtimeOnlyLibs = new ArrayList<>();
	private final Set<String> runtimeOnlyLibsSet = new HashSet<>();

	// libgdxNatives 依赖 仅打包，但需要对内部的so的名称进行额外处理
	private final List<String> libgdxNativesLibs = new ArrayList<>();
	private final Set<String> libgdxNativesLibsSet = new HashSet<>();

	private ScopeTypeMap scopeTypeMap = new ScopeTypeMap();

	public ScopeTypeQuerier(String[] dependencyLibs, ZeroAicyBuildGradle zeroAicyBuildGradle) throws Throwable{
		
		addCompileOnlyLib(AssetInstallationService.DW("core-lambda-stubs.jar", true));
		if ( dependencyLibs == null ){
			return;
		}
		// gradle项目才支持
		if ( !zeroAicyBuildGradle.isSingleton() ){
			String curProjectPath = FileSystem.getParent(zeroAicyBuildGradle.configurationPath);
			resolvingProjectDependency(curProjectPath, new HashSet<String>());
		}


		// 标记依赖并分组
		HashSet<String> dependencyLibsHashSet = new HashSet<String>(Arrays.asList(dependencyLibs));
		boolean isSingleton = zeroAicyBuildGradle.isSingleton();

		for ( String libFilePath : dependencyLibsHashSet ){
			File libFile = new File(libFilePath);

			String fileName = libFile.getName().toLowerCase();
			// 不是依赖库
			if ( !fileName.endsWith(".jar") ){
				continue;
			}
			if ( !libFile.exists() ){
				// 非Gradle项目 忽略
				if ( isSingleton ){
					// 
					continue;
				}
				//不是依赖库跳过
				throw new FileNotFoundException(libFilePath + "不存在\n依赖没有下载全，返回桌面，重新进入");				
			}

			try{
				//嗅探一下，d8打不开zip，不报路径😭
				new ZipFile(libFile).close();
			}
			catch (IOException e){
				// 坏的jar
				libFile.delete();
				throw new Error(libFilePath + "zip文件不完整或错误", e);
			}

			String libFileNameLowerCase = fileName.toLowerCase();
			/**
			 * 兼容旧方案
			 */
			if ( this.isCompileOnly(libFileNameLowerCase) ){
				addCompileOnlyLib(libFilePath);
				continue;
			}
			if ( this.isRuntimeOnly(fileName) ){
				addRuntimeOnlyLib(libFilePath);
				continue;
			}
			/**
			 * 非gradle项目 兼容旧方案
			 */
			if ( isSingleton ){
				addDexingLib(libFilePath);
				continue;
			}

			// 查询库类型
			ScopeType type = getScopeType(libFilePath);
			switch ( type ){
				case compileOnly:
					addCompileOnlyLib(libFilePath);
					break;
				case runtimeOnly:
					addRuntimeOnlyLib(libFilePath);
					break;
				case libgdxNatives:
					addLibgdxNativesLib(libFilePath);
					break;
				case dexing:
				default:
					addDexingLib(libFilePath);
					break;

			}
		}
		// 排序 因为其根目录classes%d.dex需要优先级
		this.scopeTypeMap.sortRuntimeOnly(this.runtimeOnlyLibs);

		AppLog.d("compileOnlyLibs: ", this.compileOnlyLibs);

		AppLog.d("runtimeOnlyLibs: ", this.runtimeOnlyLibs);

		AppLog.d("libgdxNativesLibs: ", this.libgdxNativesLibs);

		AppLog.d("dexingLibs: ", this.dexingLibs);

	}
	/**
	 * 是否仅编译，接受小写
	 */
	private boolean isCompileOnly(String libFileNameLowerCase){
		return libFileNameLowerCase.endsWith("_compileonly.jar");
	} 
	/**
	 * 是否仅打包，接受小写
	 */
	private boolean isRuntimeOnly(String libFileNameLowerCase){
		return libFileNameLowerCase.endsWith("_resource.jar");
	}
	/**
	 * 查询jar文件的依赖类型
	 */
	private ScopeType getScopeType(String libFilePath){
		ScopeType scopeType = scopeTypeMap.get(libFilePath);
		if ( scopeType == null ){
			scopeType =  ScopeType.dexing;
		}
		return scopeType;
	}

	/**
	 * 递归解析子项目
	 * 
	 */
	public void resolvingProjectDependency(String curProjectPath, HashSet<String> childProjectPathSet){
		if ( childProjectPathSet.contains(curProjectPath) ){
			// 已解析
			return;
		}
		childProjectPathSet.add(curProjectPath);
		if ( !GradleTools.isGradleProject(curProjectPath) ){
			return;
		}

		// 当前项目build.gradle路径
		String curProjectBuildGradle = GradleTools.getBuildGradlePath(curProjectPath);

		ZeroAicyBuildGradle childZeroAicyBuildGradle = singleton.getConfiguration(curProjectBuildGradle);
		if ( childZeroAicyBuildGradle == null ){
			return;
		}


		// 遍历特殊声明的依赖，例如: compileOnly runtimeOnly libgdxNatives
		for ( ZeroAicyBuildGradle.DependencyExt dependencyExt : childZeroAicyBuildGradle.getDependencyExts() ){
			// FilesDependency FileTreeDependency MavenDependency
			int type = dependencyExt.type;

			ScopeType scopeType = getScopeType(type);

			// 计算出路径
			BuildGradle.Dependency dependency = dependencyExt.dependency;
			if ( dependency instanceof BuildGradle.FilesDependency ){
				// xxx files("filePath")
				// 
				String filesPath = ((BuildGradle.FilesDependency)dependency).getFilesPath(curProjectPath);
				this.scopeTypeMap.put(filesPath, scopeType);
				if ( scopeType == ScopeType.runtimeOnly ){
					addRuntimeOnlyLib(filesPath);
				}
				continue;
			}

			if ( dependency instanceof BuildGradle.FileTreeDependency ){
				// xxx fileTree(dir: "filePath")
				BuildGradle.FileTreeDependency fileTreeDependency=  (BuildGradle.FileTreeDependency)dependency;
				String dirPath = fileTreeDependency.getDirPath(curProjectPath);
				this.scopeTypeMap.putDir(dirPath, scopeType);

				continue;
			}

			if ( dependency instanceof BuildGradle.MavenDependency ){
				// xxx "G:A:V" 仅标记显示依赖
				BuildGradle.MavenDependency mavenDependency = (BuildGradle.MavenDependency)dependency;
				String metadataPath = MavenService.getMetadataPath(null, mavenDependency);

				if ( metadataPath == null ){
					continue;
				}
				String dirPath = FileSystem.getParent(metadataPath);
				if ( dirPath == null ){
					continue;
				}
				this.scopeTypeMap.putDir(dirPath, scopeType);
			}

		}

		// 解析子项目
		for ( ZeroAicyBuildGradle.ProjectDependency projectDependency : childZeroAicyBuildGradle.getProjectDependencys() ){

			try{

				// 子项目路径
				String settingsGradlePath = GradleTools.getSettingsGradlePath(curProjectPath);
				BuildGradleExt settingsBuildGradleExt;
				if ( settingsGradlePath == null ){
					settingsBuildGradleExt = buildGradleExt.getConfiguration(settingsGradlePath);
				}else{
					settingsBuildGradleExt = buildGradleExt;
				}
				String projectDependencyPath = projectDependency.getProjectDependencyPath(curProjectPath, settingsBuildGradleExt);

				if ( FileSystem.isDirectory(projectDependencyPath) ){
					// 递归解析子项目
					resolvingProjectDependency(projectDependencyPath, childProjectPathSet);
				}
			}
			catch (Throwable e){
				e.printStackTrace();
			}
		}
	}

	private void addCompileOnlyLib(String libFilePath){
		if ( this.compileOnlyLibsSet.contains(libFilePath) ){
			return;
		}
		this.compileOnlyLibsSet.add(libFilePath);

		this.compileOnlyLibs.add(libFilePath);

	}
	private void addRuntimeOnlyLib(String libFilePath){
		if ( this.runtimeOnlyLibsSet.contains(libFilePath) ){
			return;
		}
		this.runtimeOnlyLibsSet.add(libFilePath);
		// 添加runtimeOnly，那么在BuildGradle解析中就可以不添加他了
		this.runtimeOnlyLibs.add(libFilePath);
	}

	public void addDexingLib(String libFilePath){
		if ( this.dexingLibsSet.contains(libFilePath) ){
			return;
		}
		this.dexingLibsSet.add(libFilePath);
		this.dexingLibs.add(libFilePath);
	}



	private void addLibgdxNativesLib(String libFilePath){
		if ( this.libgdxNativesLibsSet.contains(libFilePath) ){
			return;
		}
		this.libgdxNativesLibsSet.add(libFilePath);
		this.libgdxNativesLibs.add(libFilePath);

	}

	public List<String> getDexingLibs(){
		return this.dexingLibs;
	}

	public List<String> getCompileOnlyLibs(){
		return this.compileOnlyLibs;
	}

	public List<String> getRuntimeOnlyLibs(){
		return this.runtimeOnlyLibs;
	}

	public List<String> getLibgdxNativesLibs(){
		return this.libgdxNativesLibs;
	}

	private static ScopeType getScopeType(int type){
		switch ( type ){
			case ZeroAicyBuildGradle.DependencyExt.CompileOnly:
				return ScopeType.compileOnly;
			case ZeroAicyBuildGradle.DependencyExt.RuntimeOnly:
				return ScopeType.runtimeOnly;
			case ZeroAicyBuildGradle.DependencyExt.LibgdxNatives:
				return ScopeType.libgdxNatives;
			default :
				return ScopeType.dexing;
		}
	}
}
