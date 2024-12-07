package com.aide.ui.build.android;
import com.aide.ui.build.BuildServiceCollect;

public class AndroidProjectBuildServiceKt {
	
	public static void setDisablePackaging(boolean isPackaging){
		AndroidProjectBuildService.ei(BuildServiceCollect.androidProjectBuildService, isPackaging);
	}
}
