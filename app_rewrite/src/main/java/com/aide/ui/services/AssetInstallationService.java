//
// Decompiled by Jadx - 863ms
//
package com.aide.ui.services;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Build;
import com.aide.common.AppLog;
import com.aide.common.StreamUtilities;
import com.aide.ui.ServiceContainer;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.ui.services.ThreadPoolService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import androidx.annotation.Keep;
import java.util.concurrent.ExecutorService;

public class AssetInstallationService {
	public static long getResourceSize(String resourceName) {
		try {
			return getResourceAssetFileDescriptor(resourceName).getLength();
		}
		catch (Throwable e) {
			return -1;
		}
	}
	private static AssetFileDescriptor getResourceAssetFileDescriptor(String resourceName) {
		if (ServiceContainer.isX86() && Build.VERSION.SDK_INT >= 20) {
			return tryGetAssetFileDescriptorFromAssetManager(resourceName, resourceName + ".jet", "x86-pie/" + resourceName, "x86/" + resourceName + ".jet");
		}
		if (ServiceContainer.isX86()) {
			return tryGetAssetFileDescriptorFromAssetManager(resourceName, resourceName + ".jet", "x86/" + resourceName, "x86/" + resourceName + ".jet");
		}
		if (Build.VERSION.SDK_INT >= 20) {
			return tryGetAssetFileDescriptorFromAssetManager(resourceName, resourceName + ".jet", "armeabi-pie/" + resourceName, "armeabi/" + resourceName + ".jet");
		}
		return tryGetAssetFileDescriptorFromAssetManager(resourceName, resourceName + ".jet", "armeabi/" + resourceName, "armeabi/" + resourceName + ".jet");
    }

    private static AssetFileDescriptor tryGetAssetFileDescriptorFromAssetManager(String... resourceNames) {
		try {
			AssetManager assets = ServiceContainer.getContext().getAssets();
			for (String resourceName : resourceNames) {
				AssetFileDescriptor assetFileDescriptor = getAssetFileDescriptorFromAssetManager(assets, resourceName);
				if (assetFileDescriptor != null) {
					return assetFileDescriptor;
				}
			}
			throw new FileNotFoundException("Asset " + resourceNames[0] + " not found.");
		}
		catch (FileNotFoundException e) {
			throw new Error(e);
		}
	}

	private static AssetFileDescriptor getAssetFileDescriptorFromAssetManager(AssetManager assetManager, String resourceName) {
		try {
			return assetManager.openFd(resourceName);
		}
		catch (Exception e) {
			return null;
		}
	}
	

    private String DW;

    private String FH;

    private String Hw;

    private String j6;

    private String v5;

    public AssetInstallationService() {}

    public static String DW(String resourceName, boolean z) {
		return FH(resourceName, z, false);
    }

    public static String FH(String resourceName, boolean z, boolean unZip) {
		extractResource(resourceName, z, unZip);
		return getOutputPath(resourceName, z);
    }

    private static InputStream getResourceInputStream(String resourceName) {
		if (ServiceContainer.isX86() && Build.VERSION.SDK_INT >= 20) {
			return tryGetInputStreamFromAssetManager(resourceName, resourceName + ".jet", "x86-pie/" + resourceName, "x86/" + resourceName + ".jet");
		}
		if (ServiceContainer.isX86()) {
			return tryGetInputStreamFromAssetManager(resourceName, resourceName + ".jet", "x86/" + resourceName, "x86/" + resourceName + ".jet");
		}
		if (Build.VERSION.SDK_INT >= 20) {
			return tryGetInputStreamFromAssetManager(resourceName, resourceName + ".jet", "armeabi-pie/" + resourceName, "armeabi/" + resourceName + ".jet");
		}
		return tryGetInputStreamFromAssetManager(resourceName, resourceName + ".jet", "armeabi/" + resourceName, "armeabi/" + resourceName + ".jet");
    }


    private static InputStream tryGetInputStreamFromAssetManager(String... resourceNames) {
        try {
			AssetManager assets = ServiceContainer.getContext().getAssets();
			for (String resourceName : resourceNames) {
				InputStream in = getInputStreamFromAssetManager(assets, resourceName);
				if (in != null) {
					return in;
				}
			}
			throw new FileNotFoundException("Asset " + resourceNames[0] + " not found.");
		}
		catch (FileNotFoundException e) {
			throw new Error(e);
		}
    }

    private static InputStream getInputStreamFromAssetManager(AssetManager assetManager, String resourceName) {
		try {
			return assetManager.open(resourceName);
		}
		catch (Exception unused) {
			return null;
		}
    }

    private static String getOutputPath(String resourceName, boolean z) {
		String aideCacheDir = FileSystem.getNoBackupFilesDirPath() + "/.aide";
		File file = new File(aideCacheDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return aideCacheDir + File.separator + resourceName;
    }

	/**
	 * 已存在最新或成功写入时返回 false
	 * true可以理解为未消耗事件
	 * 
	 */
    private static boolean needUpdateResource(String resourceName, boolean z) {
		String outputPath = getOutputPath(resourceName, z);

		SharedPreferences sharedPreferences = ServiceContainer.getContext().getSharedPreferences("AssetInstallationService", 0);
		long apkVersion = sharedPreferences.getLong("ApkVersion", 0L);
		long apkInstallationTime = getApkInstallationTime();
		long androidVersion = sharedPreferences.getInt("AndroidVersion", 0);

		// 更新AssetInstallationService数据
		if (!FileSystem.DW(apkVersion, apkInstallationTime) 
			|| androidVersion != Build.VERSION.SDK_INT) {
			SharedPreferences.Editor edit = sharedPreferences.edit();
			edit.clear();
			edit.putLong("ApkVersion", apkInstallationTime);
			edit.putInt("AndroidVersion", Build.VERSION.SDK_INT);
			edit.commit();
		}
		File outputFile = new File(outputPath);
		
		// 以解压
		if (sharedPreferences.getBoolean(resourceName, false) 
			&& outputFile.exists()) {
			return false;
		}
		// 添加已解压标志
		SharedPreferences.Editor edit2 = sharedPreferences.edit();
		edit2.putBoolean(resourceName, true);
		edit2.commit();

		try {
			if (outputFile.exists()) {
				// 文件长度相同则不更新
				// 用不了
				// This file can not be opened as a file descriptor; it is probably compressed
				/*
				if (outputFile.length() == getResourceSize(resourceName)) {
					return false;
				}
				*/
				// 更新并写入文件
				if (StreamUtilities.equals(getResourceInputStream(resourceName), new FileInputStream(outputFile))) {
					return false;
				}
			}

			return true;
		}
		catch (Exception unused) {
			return true;
		}
    }

    private static long getApkInstallationTime() {
        try {
			return new File(ServiceContainer.getContext().getPackageManager().getPackageInfo(ServiceContainer.getContext().getPackageName(), 0).applicationInfo.sourceDir).lastModified();
		}
		catch (PackageManager.NameNotFoundException unused) {
			return -1L;
		}
    }


    private static boolean extractResource(String resourceName, boolean z, boolean unZip) {
        if (needUpdateResource(resourceName, z)) {
			// 此时文件不存在
			try {
				String outputPath = getOutputPath(resourceName, z);
				InputStream resourceInputStream = getResourceInputStream(resourceName);
				if (unZip) {
					// 解压
					FileSystem.unZip(resourceInputStream, outputPath, false);
				} else {
					File outputFile = new File(outputPath);
					outputFile.setWritable(true);
					outputFile.getParentFile().mkdirs();
					// transferTo
					StreamUtilities.transferStream(resourceInputStream, new FileOutputStream(outputFile));
				}
				AppLog.d("Extracted asset " + resourceName);
				return true;
			}
			catch (IOException e) {
				AppLog.e(e);
			}
		}
		return false;
    }
	
	@Keep
    public String getWeardebugKeystore() {
        String VH2 = getOutputPath("weardebug.keystore", true);
		if (!new File(VH2).exists()) {
			GregorianCalendar gregorianCalendar = new GregorianCalendar();
			gregorianCalendar.add(1, 100);
			ServiceContainer.getSigningService().makeJksKeyStore(VH2, "xxxxxx", "weardebug", "xxxxxx", new GregorianCalendar().getTime(), gregorianCalendar.getTime(), BigInteger.ONE, "Wear Debug", "", "", "", "", "");
		}
		return VH2;
    }

    public String getAandroidJarPath() {
        return this.DW;
    }

    public String getAnnotationsJarPath() {
        return this.FH;
    }

    public String getJavaScriptAPIJsPath() {
        return this.j6;
    }

    public String getFrameworkAidlPath() {
        return this.Hw;
    }

    public String getMergerZip() {
        if (this.v5 == null) {
			this.v5 = DW("merger.zip", true);
		}
		
		File mergerZipFile = new File(this.v5);
		// Writable dex file is not allowed.
		if( mergerZipFile.canWrite()){
			mergerZipFile.setWritable(false);
		}
		
		return this.v5;
    }
	
	ExecutorService executorsService = ThreadPoolService.getDefaultThreadPoolService();
	
	// 卡[Running aidl...] 原因不在这
    public void init() {
		executorsService.submit(new Runnable(){
				@Override
				public void run() {
					try {
						// 异步解压
						AssetInstallationService.this.j6 = DW("JavaScriptAPI.js", true);
						AssetInstallationService.this.DW = DW("android.jar", true);
						AssetInstallationService.this.FH = DW("annotations.jar", true);
						AssetInstallationService.this.Hw = DW("framework.aidl", true);

						DW("proguard-android.txt", true);
						DW("proguard-android-optimize.txt", true);
						// 解压 r8
						DW("com.android.tools.r8.zip", true);
						
					}
					catch (Throwable e) {
						e.printStackTrace();
					}
				}
			});
    }
}

