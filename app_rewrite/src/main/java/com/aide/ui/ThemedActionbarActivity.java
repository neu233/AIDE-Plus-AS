package com.aide.ui;

import android.os.Bundle;
import com.aide.ui.rewrite.R;
import io.github.zeroaicy.aide.base.BaseActivity;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;

public class ThemedActionbarActivity extends BaseActivity {


    public static void onCreate2(ThemedActionbarActivity ThemedActionbarActivity, Bundle bundle) {
		ThemedActionbarActivity.onCreate2(bundle);
	}

    protected void onCreate2(Bundle bundle) {

		enableFollowSystem(false);
		super.onCreate(bundle);
		if (ZeroAicySetting.isLightTheme()) {
			setTheme(R.style.MyAppThemeLight);
		} else {
			setTheme(R.style.MyAppThemeDark);
		}
    }
	
	@Override
    protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		if (ZeroAicySetting.isLightTheme()) {
			setTheme(R.style.MyAppThemeLight);
		} else {
			setTheme(R.style.MyAppThemeDark);
		}

    }

    
}

