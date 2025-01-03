package io.github.zeroaicy.aide.aapt2;

import android.app.Application;
import com.aide.common.AppLog;
import com.aide.ui.services.AssetInstallationService;
import dalvik.system.DexClassLoader;
import io.github.zeroaicy.util.reflect.ReflectPie;
import java.io.File;

public class GenerateViewBindingTask{
	
	
	private static ClassLoader viewbindingClassLoader;
	
	public static void run(String mainProjectResPath, String mainProjectGenDir, String mainProjectPackageName, boolean isAndroidx) throws Exception{
		if( viewbindingClassLoader == null ){
			String viewbindingZipPath = getViewbindingZipPath();
			viewbindingClassLoader = new DexClassLoader(viewbindingZipPath, null, null, Application.class.getClassLoader());
		}
		if( viewbindingClassLoader == null  ){
			throw new NullPointerException("viewbindingClassLoader为null");
		}
		
		ReflectPie.onClass("ZY.ViewBinding.Utils", viewbindingClassLoader).call("BindingTask", new Object[]{mainProjectResPath, mainProjectGenDir, mainProjectPackageName, isAndroidx});
//		if( false ){
//			AppLog.d("主项目res目录", mainProjectResPath);
//			AppLog.d("主项目gen目录", mainProjectGenDir);
//			AppLog.d("主项目包名", mainProjectPackageName);
//			AppLog.d("isAndroidx", isAndroidx);
//		}
    }

	private static String getViewbindingZipPath(){
		String viewbindingZipPath = AssetInstallationService.DW("viewbinding.zip", false);
		
		File viewbindingZipFile = new File(viewbindingZipPath);
		if(!viewbindingZipFile.canExecute()){
			viewbindingZipFile.setReadable(true, false);
			viewbindingZipFile.setExecutable(true, false);
		}
		return viewbindingZipPath;
	}
}
