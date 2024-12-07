package com.aide.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import androidx.core.content.ContextCompat;
import com.aide.ui.rewrite.R;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.util.BarUtils;
import android.graphics.Color;
import io.github.zeroaicy.aide.base.*;

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

