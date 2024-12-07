package io.github.zeroaicy.aide.aapt2;
import java.util.List;
import java.util.ArrayList;
import dalvik.system.DexClassLoader;
import io.github.zeroaicy.util.reflect.ReflectPie;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.util.reflect.ReflectPieException;

public class DataBindingBuilderProxy {

	static DexClassLoader DataBindingBuilderDexClassLoader;

	AaptServiceArgs aaptServiceArgs;

	static ReflectPie dataBindingBuilderReflectPie;
	public DataBindingBuilderProxy(AaptServiceArgs aaptServiceArgs) {
		this.aaptServiceArgs = aaptServiceArgs;
	}

	public boolean compilerRes(String resDir) {
		if (ZeroAicySetting.isEnableDataBinding() && check()) {

			try {
				ReflectPie call = dataBindingBuilderReflectPie.call("compilerRes", resDir);
				Boolean compilerRes = call.get();

				return compilerRes;
			} catch (Throwable e) {
				
			}
		}
		return false;
	}

	public void generateJava() {
		if (ZeroAicySetting.isEnableDataBinding() && check()) {
			dataBindingBuilderReflectPie.call("generateJava");
		}
	}
	
	
	private static boolean check() {
		if (ZeroAicySetting.isEnableDataBinding() && DataBindingBuilderDexClassLoader == null) {
			DataBindingBuilderDexClassLoader = new DexClassLoader("/storage/emulated/0/AppProjects1/.project/ZeroAicy-WearOs/文件管理器/JDOM/bin/release/dex/classes.dex.zip", null, null, DataBindingBuilderProxy.class.getClassLoader());
		}
		return dataBindingBuilderReflectPie != null;
	}

}  
