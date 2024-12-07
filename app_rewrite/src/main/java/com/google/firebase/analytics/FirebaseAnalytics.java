package com.google.firebase.analytics;

import androidx.annotation.Keep;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;

@Keep
public class FirebaseAnalytics {
	
	@Keep
	private static volatile FirebaseAnalytics DW;

	//private final zzbw j6;

	//private FirebaseAnalytics(zzbw zzbw) {}
	
	@Keep
	private FirebaseAnalytics() {}
	
	@Keep
	public final void DW(boolean p) {}
	
	@Keep
	public final void FH(String string, String string1) {}

	@Keep
	public final String getFirebaseInstanceId() {
		return "";
	}
	
	@Keep
	static FirebaseAnalytics instance;
	
	@Keep
	public static FirebaseAnalytics getInstance(Context context) {
		if( instance == null){
			DW = instance = new FirebaseAnalytics();
		}
		return instance;
	}

	@Keep
	public final void j6(String string, Bundle bundle) {}

	@Keep
	public final void setCurrentScreen(Activity activity, String string, String string1) {}
	
}
