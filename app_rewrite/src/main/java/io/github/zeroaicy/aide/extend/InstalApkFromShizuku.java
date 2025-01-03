package io.github.zeroaicy.aide.extend;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.aide.ui.ServiceContainer;
import com.aide.ui.project.AndroidProjectSupport;
import io.github.zeroaicy.aide.shizuku.ShizukuUtil;



public class InstalApkFromShizuku implements Runnable {

	private String appPath;

	public InstalApkFromShizuku(String appPath) {
		this.appPath = appPath;
	}

	// 子线程中
	@Override
	public void run() {
		final String instalApkError = ShizukuUtil.instalApk(appPath);
		// 安装完毕通知主进程显示安装结果
		// 或启动app
		ServiceContainer.aj(new Runnable(){
				//在主线程中
				@Override
				public void run() {
					Context context = ServiceContainer.getContext();
					if (TextUtils.isEmpty(instalApkError)) {
						//成功安装，启动应用
						String gW = ServiceContainer.getProjectService().getCurrentAppHome();
						String packageName = AndroidProjectSupport.getProjectPackageName(gW, ServiceContainer.getProjectService().getFlavor());
						PackageManager packageManager = context.getPackageManager();
						Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);

						if (launchIntentForPackage != null) {
							context.startActivity(launchIntentForPackage);
						} else {
							com.aide.common.MessageBox.BT(ServiceContainer.getMainActivity(), "运行错误", "应用程序已成功安装，但找不到主活动");					
						}
					} else {
						//安装失败
						com.aide.common.MessageBox.BT(ServiceContainer.getMainActivity(), "安装失败", instalApkError);
					}
				}
			});
	}


}

