package io.github.zeroaicy.aide.base;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import androidx.core.content.ContextCompat;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.util.BarUtils;

public class BaseActivity extends Activity {
	@Override
    protected void onCreate(Bundle bundle) {

		enableFollowSystem(false);

		super.onCreate(bundle);

    }

	@Override
	protected void onResume() {
		super.onResume();
		enableFollowSystem(true);
	}

	@Override
    protected void onStart() {
        super.onStart();
		setStatusBar(getWindow());
    }

	@Override
	public void onConfigurationChanged(Configuration configuration) {
		super.onConfigurationChanged(configuration);
		enableFollowSystem(true);
	}

	public void enableFollowSystem(boolean recreate) {
		if (ZeroAicySetting.enableFollowSystem()) {
			if (ZeroAicySetting.isNightMode(this)) {
				if (ZeroAicySetting.isLightTheme()) {
					//修改主题为暗主题
					ZeroAicySetting.setLightTheme(false);
					if (recreate) recreate();
				}
			} else {
				if (!ZeroAicySetting.isLightTheme()) {
					//修改主题为亮主题
					ZeroAicySetting.setLightTheme(true);
					if (recreate) recreate();
				}
			}
		}
	}

	public void setStatusBar(Window window) {
		setNavBar(window);
		if (true) {
            if (Build.VERSION.SDK_INT < 23 && ZeroAicySetting.isLightTheme()) {
                window.setStatusBarColor(getThemeAttrColor(android.R.attr.colorPrimaryDark));
            } else {
                window.setStatusBarColor(getThemeAttrColor(android.R.attr.colorPrimary));
            }
		} else {
			window.setStatusBarColor(getThemeAttrColor(android.R.attr.colorPrimaryDark));
		}

		if (Build.VERSION.SDK_INT >= 23) {
            View decorView = window.getDecorView();
            if (ZeroAicySetting.isLightTheme()) {
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | 8192);
            } else {
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & -8193);
            }
        }
	}



	private void setNavBar(Window window) {
		if (Build.VERSION.SDK_INT >= 27 && ZeroAicySetting.isLightTheme()) {
			BarUtils.setNavBarColor(window, ContextCompat.getColor(window.getContext(), android.R.color.white));
			BarUtils.setNavBarLightMode(window, true);
			return;
		}

		if (!ZeroAicySetting.isLightTheme() && dp2px(BarUtils.getNavBarHeight()) < dp2px(50)) {
			BarUtils.setNavBarColor(window, Color.parseColor("#ff212121"));
			//BarUtils.setNavBarColor(window, ContextCompat.getColor(window.getContext(), R.color.app_background));
		}
	}

	public int getThemeAttrColor(int attrid) {
        TypedArray a = obtainStyledAttributes(new int[]{attrid});
        try {
            return a.getColor(0, 0);
        } finally {
            a.recycle();
        }
    }

	public int dp2px(float dpValue) {
		float scale = Resources.getSystem().getDisplayMetrics().density;
		return (int)(dpValue * scale + 0.5F);
	}
}
