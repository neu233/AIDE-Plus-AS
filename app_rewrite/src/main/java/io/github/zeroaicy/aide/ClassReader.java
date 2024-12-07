package io.github.zeroaicy.aide;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import dalvik.system.DexClassLoader;
import io.github.zeroaicy.readclass.classInfo.ClassInfoTest3;
import io.github.zeroaicy.readclass.classInfo.DefaultMethodAllowedList;
import io.github.zeroaicy.util.ContextUtil;
import io.github.zeroaicy.util.Log;
import io.github.zeroaicy.util.reflect.ReflectPie;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import androidx.annotation.Keep;
import com.aide.common.AppLog;

@Keep
public class ClassReader {
	private static final String TAG3 = "ZeroAicyClassReader";

	private static final String TAG = "ZeroAicyClassReader";

	public static final boolean isDirect = isDirect();
	public static final boolean isDynamic = isDynamic();

	private static ClassLoader dynamicDexClassLoader;

	private static boolean isLoadDexError = false;

	private static final String DefaultMethodAllowedListClass = "io.github.zeroaicy.readclass.classInfo.DefaultMethodAllowedList";
	private static Method hasDefaultMethod;


	private static final String TestReadClass = "io.github.zeroaicy.readclass.classInfo.ClassInfoTest3";
	private static Method TestReadClassMethod;
	private static Method ModifyADRTMethod;

	private static boolean useReaderClassFromZeroAicy = true;
	private static boolean disableDefaultMethod = false;
	private static boolean disableMethodCode = true;
	

	private static final String useReaderClassKey = "useReaderClass";
	private static final String disableDefaultMethodKey = "disableDefaultMethod";
	private static final String disableMethodCodeKey = "disableMethodCode";

	//防止gc
	private static SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChanged;

	static{
		/**
		 * 动态添加
		 */
		if (isDynamic) {
			AppLog.d(TAG, "动态调用");
			loadDynamicDex();
		} else if (isDirect) {
			AppLog.d(TAG, "合并模式, Direct调用");
		} else {
			useReaderClassFromZeroAicy = false;
			AppLog.w(TAG, "错误模式，解析库禁用");

		}

		try {
			Context context = ContextUtil.getContext();
			AppLog.d(TAG, "context is " +  context.getPackageName());

			SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			onSharedPreferenceChanged = new SharedPreferences.OnSharedPreferenceChangeListener(){
				@Override
				public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
					switch (key) {
						case useReaderClassKey:
							//默认使用新ReaderClass
							ClassReader.useReaderClassFromZeroAicy = sp.getBoolean(useReaderClassKey, true);
							break;
						case disableDefaultMethodKey:
							//默认不禁用默认方法
							ClassReader.disableDefaultMethod = sp.getBoolean(disableDefaultMethodKey, false);
							break;
						case disableMethodCodeKey:
							//默认禁用方法代码
							// disableMethodCode
							ClassReader.disableMethodCode = sp.getBoolean(disableMethodCodeKey, true);
							
							break;
					}

					AppLog.d(TAG, "useReaderClassFromZeroAicy改变为: " +  ClassReader.useReaderClassFromZeroAicy);
					AppLog.d(TAG, "disableDefaultMethod改变为: " +  ClassReader.disableDefaultMethod);
				}
			};
			defaultSharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChanged);
			//默认使用新ReaderClass
			useReaderClassFromZeroAicy = defaultSharedPreferences.getBoolean(useReaderClassKey, true);
			//默认不禁用默认方法
			disableDefaultMethod = defaultSharedPreferences.getBoolean(disableDefaultMethodKey, false);
			//默认禁用方法代码
			// disableMethodCode
			ClassReader.disableMethodCode = defaultSharedPreferences.getBoolean(disableMethodCodeKey, true);
			
			//失败了
			if( false && !ClassReader.disableMethodCode && !ContextUtil.isMainProcess()){
				// 只需要主进程可以开启
				ClassReader.disableMethodCode = true;
			}
			if (isDynamic && dynamicDexClassLoader != null) {
				ReflectPie onClass = ReflectPie.onClass(DefaultMethodAllowedListClass, dynamicDexClassLoader);
				onClass.call("setDisableDefaultMethod", disableDefaultMethod);
				onClass.call("setDisableMethodCode", disableMethodCode);
				
			} else if (isDirect) {
				ReflectPie onClass = ReflectPie.onClass(DefaultMethodAllowedListClass);
				onClass.call("setDisableDefaultMethod", disableDefaultMethod);
				onClass.call("setDisableMethodCode", disableMethodCode);
			}

		}
		catch (Throwable e) {
			AppLog.e(TAG, "开关错误", e);
		}
	}

	private static boolean isDirect() {
		try {
            Class.forName(TestReadClass).getMethod("TestReadClass", String.class, String.class);
            Class.forName(DefaultMethodAllowedListClass).getMethod("hasDefaultMethod", String.class);
        }
		catch (Throwable e) {
			Log.e(TAG, "isDirect false", e);
			return false;
        }
        return true;
	}
	private static boolean isDynamic() {
		if (isDirect || isLoadDexError) return false;
		return true;
	}
	/**
	 * 固定api
	 */
	public static Reader Dc_ReadClassFile(String zipFilePath, String className) {
		if (!useReaderClassFromZeroAicy) {
			return null;
		}
		try {
			if (isDynamic) {
				return dynamic_ReadClassFile(zipFilePath, className);
			} else if (isDirect) {
				return ClassInfoTest3.TestReadClass(zipFilePath, className);
			}
		}
		catch (StringIndexOutOfBoundsException e) {
			AppLog.d(zipFilePath, className);
			e.printStackTrace();
		}
		catch (Throwable e) {
			notReadClassFileLib();
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			return new StringReader(stringWriter.getBuffer().toString());
		}
		return null;
	}

	private static boolean isPrintlned = true;
	private static void notReadClassFileLib() {
		if (isPrintlned) {
			return;
		}
		isPrintlned = true;
		AppLog.e(TAG, "没有发现ReadClassFile库，请向AIDE+中添加class解析库");
	}

	private static Reader dynamic_ReadClassFile(String zipFilePath, String className) {
		try {
			return (Reader)TestReadClassMethod.invoke(null, zipFilePath, className);
		}
		catch (StringIndexOutOfBoundsException e) {
			AppLog.d(zipFilePath, className);
			e.printStackTrace();
		}
		catch (Throwable e) {}
		notReadClassFileLib();
		return null;
	}

	/**
	 invoke-virtual {p0}, Labcd/n1;->gn()Ljava/lang/String;
	 move-result-object v1
	 invoke-static {v1}, Lio/github/zeroaicy/aide/ClassReader;->hasDefaultMethod(Ljava/lang/String;)Z
	 move-result v1

	 if-eqz v1, :cond_21
	 and-int/lit16 v0, v0, -0x4001
	 #修改
	 :cond_21
	 return v0
	 */
	/**
	 * 固定api
	 */
	public static boolean hasDefaultMethod(String s) {
		boolean hasDefaultMethod2 = hasDefaultMethod2(s);
		//debug Log.println(s + " : " + hasDefaultMethod2);
		return hasDefaultMethod2;
	}

	/**
	 * 具体实现
	 */
	private static boolean hasDefaultMethod2(String s) {
		try {
			if (isDynamic) {
				return (Boolean)hasDefaultMethod.invoke(null, s);
			} else if (isDirect) {
				return DefaultMethodAllowedList.hasDefaultMethod(s);
			}
		}
		catch (Throwable e) {
			AppLog.e(TAG, "hasDefaultMethod2", e);
		}
		return true;
	}

	/*
	 * 修改ADRT，以适应共存版
	 */
	public static InputStream modifyADRT(InputStream open) {
		try {
			if (isDynamic) {
				return (InputStream)ModifyADRTMethod.invoke(null, open);
			} else if (isDirect) {
				return ClassInfoTest3.modifyADRT(open);
			}
		}
		catch (Throwable e) {

		}
		notReadClassFileLib();
		return open;
	}
	private static void loadDynamicDex() {
		if (!isDynamic && dynamicDexClassLoader != null) return;
		try {
			String readerclassDexFilePath = "/storage/emulated/0/AppProjects1/.ZeroAicy/AIDE工具/ReaderClassFile/bin/release/dex/classes.dex.zip";

			File file = new File(readerclassDexFilePath);
			if (!file.exists()) {
				isLoadDexError = true;
				return;
			}
			dynamicDexClassLoader = new DexClassLoader(readerclassDexFilePath, null, null, ClassReader.class.getClassLoader());
			Class<?> loadClass = dynamicDexClassLoader.loadClass(DefaultMethodAllowedListClass);

			hasDefaultMethod = loadClass.getMethod("hasDefaultMethod", String.class);


			Class<?> loadClass_TestReadClass = dynamicDexClassLoader.loadClass(TestReadClass);
			TestReadClassMethod = loadClass_TestReadClass.getMethod("TestReadClass", String.class, String.class);
			ModifyADRTMethod = loadClass_TestReadClass.getMethod("modifyADRT", InputStream.class);

			//TestMethod = loadClass_TestReadClass.getMethod("TestMethod", Class.forName("abcd.q1"), Reader.class);

		}
		catch (Throwable e) {
			notReadClassFileLib();
			isLoadDexError = true;
		}
	}
}
