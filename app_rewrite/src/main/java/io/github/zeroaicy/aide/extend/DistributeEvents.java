package io.github.zeroaicy.aide.extend;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import com.aide.ui.ServiceContainer;
import com.aide.ui.firebase.FireBaseLogEvent;
import com.aide.ui.project.internal.GradleTools;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.extend.InstalApkFromShizuku;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.aide.shizuku.ShizukuUtil;
import io.github.zeroaicy.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DistributeEvents {
	


	
	
	//此函数返回值仅代表是否拦截AIDE默认安装流程
	public static boolean instalApp(final String appPath) {
		//使用自定义安装器安装 没有Shizuku权限时
		if (ZeroAicySetting.isCustomInstaller() 
			|| !ShizukuUtil.checkPermission()
			|| !ZeroAicySetting.isShizukuInstaller() ) {
			return customInstaler(appPath);
		}
		// 需要用 子线程安装
		if (ZeroAicySetting.isShizukuInstaller()) {
			new Thread(new InstalApkFromShizuku(appPath)).start();
			//拦截默认安装流程
			return true;
		}

		return false;
	}
	//安装App
	public static boolean customInstaler(String appPath) {
		try {
			
            if (!ServiceContainer.isTrainerMode() 
				|| ServiceContainer.nw().Ws()) {
                Intent intent = new Intent(Intent.ACTION_DEFAULT);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				//分屏
                //intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
				Uri apkUri;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    apkUri = FileProvider.getUriForFile(ServiceContainer.getContext(), FileSystem.j3(), new File(appPath));
                    intent.addFlags(1);
                }
				else {
                    apkUri = Uri.fromFile(new File(appPath));
                }
				intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

                Context context = ServiceContainer.getContext();
				//默认系统安装器
				String apkInstall = ZeroAicySetting.getApkInstallPackageName();

				List<ResolveInfo> queryIntentActivities = ServiceContainer.getContext().getPackageManager().queryIntentActivities(intent, 0);
				if (queryIntentActivities != null && queryIntentActivities.size() > 0) {
					for (ResolveInfo resolveInfo : queryIntentActivities) {
						ActivityInfo activityInfo = resolveInfo.activityInfo;
						if (apkInstall.equals(activityInfo.applicationInfo.packageName)) {
							intent.setComponent(new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name));
							break;
						}
                    }
                }
				context.startActivity(intent);
				
				Log.d("HandleEventInstalApp", intent.toString());
				//iy.BT(context, intent);
				//FireBaseLogEvent.tp("Run app without root");
				return true;
            }
        } catch (Throwable th) {
            throw new Error(th);
        }
		return false;
	}
	public static boolean cmakeBuild(){
		return false;
	}
	
	/*public static Map<String, List<SyntaxError>> build(String projectPath){
		return CmakeBuild.build(projectPath);
	}
	public static boolean isCmakeProject(String projectPath){
		
		return CmakeBuild.isCmakeProject(projectPath);
	}*/
	
	public static boolean isGradleProject(String str) {
        return GradleTools.isGradleProject(str);
    }
	public static void NDKEenhancement(List<String> list, String project) {
        if (isGradleProject(project)) {
			//安卓gradle工程
			list.add("NDK_PROJECT_PATH=.");
			list.add("APP_BUILD_SCRIPT=src/main/jni/Android.mk");
			list.add("NDK_APP_OUT=src/main/obj");
			list.add("NDK_LIBS_OUT=src/main/jniLibs");

			File ApplicationFile = new File(project, "src/main/jni/Application.mk");
			if (ApplicationFile.exists()) {
				list.add("NDK_APPLICATION_MK=" + "src/main/jni/Application.mk");
			}
		}
	}
	/*
	 没替换AIDE实现
	 */
	public static int GarbledRepai(InputStream mInput, byte[] data) throws IOException {
        int read = mInput.read();

        if (read < 0) {
            return -1;
        }
		//已经读入的字节数
        int count = 0;
		//utf-8 第一个字节规律
		//0000表示一个字节 即ASCII码 
		//1100 0000表示两个字节
		//1110 0000 表示有3个字节 
		//1111 0000 表示四个字节
		final int dataSize = data.length;
		final int maxSize = dataSize - 4;
		while (read >= 0 && count < dataSize) {
			//判断第一个字节是否是ASCII
			if ((read & 0x80) == 0) {
				//utf-8单字节读取 ASCII
				data[count] = (byte) read;
				++count;
			}
			else {
				//不是ASCII
				data[count] = (byte) read;
				++count;
				// 根据utf-8的第一个字节第7位
				// 判断需要读取几个字节
				while ((read & 0x40) != 0) {
					data[count] = (byte) mInput.read();
					read <<= 1;
					++count;
				}
			}
			//utf-8 最多4字节 所以预留四个字节
			if (maxSize >= count) {
				//预留能够装一个中文的字节数
				read = mInput.read();
				if (read == -1) {
					//没有数据退出
					break;
				}
			}
			else {
				//数组剩余空间不够装多字节的utf-8
				break;
			}
		}
		return count;
    }
}
