package com.aide.ui;

//
// Decompiled by Jadx - 508ms
//

import androidx.multidex.MultiDexApplication;
import com.aide.ui.marketing.b;
import com.aide.ui.rewrite.R;
import io.github.zeroaicy.util.ContextUtil;

public class AIDEApplication extends MultiDexApplication{
	public void onCreate() {
        super.onCreate();
		// 开启错误打印 
		// Probelytics.VH(this, 0, "0", "0", true, false, "", "", "");
		// 比较烦probelytics还是不用这个了
		//iy.VH(this, 1615898270903L, "androidRelease", "fe5d2222", true, false, "https://probes.probelytics.com/project/iF0snISTR5Ko3hbTUY88bg/audience/dev/current.probes2", "https://probes.probelytics.com/project/iF0snISTR5Ko3hbTUY88bg/audience/public/current.probes2", "https://ingest.probelytics.com/api/report/v1alpha1/data/project/iF0snISTR5Ko3hbTUY88bg2");
		
		// 共存版关键
		ServiceContainer.setAppId("com.aide.ui");
		
		if (ContextUtil.isMainProcess()) {
			if (ServiceContainer.appId.equals("com.aide.ui")) {
				b.j6(this, 0xa9bd, R.drawable.ic_launcher, "New version of AIDE installed", "Check out what's new!", MainActivity.Mz(this));
			} else if (ServiceContainer.appId.equals("com.aide.web")) {
				b.j6(this, 0xa9bd, R.drawable.ic_launcher_web, "New version of AIDE Web installed", "Check out what's new!", MainActivity.Mz(this));
			}
		}
    }
}

