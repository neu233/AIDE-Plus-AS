package io.github.zeroaicy.aide.preference;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.aide.ui.rewrite.R;
import android.preference.Preference;
import android.content.Intent;
import android.app.Activity;
import io.github.zeroaicy.aide.highlight.HighlightActivity;

public class ZeroAicySettingsFragment extends PreferenceFragment {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//添加ZeroAicy扩展设置
        addPreferencesFromResource(R.xml.preferences_setting_zeroaicy);

    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	private void init() {
		setOnPreferenceClickListener(
            "zero_aicy_preference_highlight",
            new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference p1) {
					Activity activity = getActivity();
					activity.startActivity(new Intent(getActivity(), HighlightActivity.class)
										   .putExtra("title", p1.getTitle()));
                    //  getActivity().overridePendingTransition(android.R.anim.fade_in,
                    // android.R.anim.fade_out);
                    //getActivity().overridePendingTransition(0, 0);

                    return false;
                }
            });

		// 主题跟随系统
		/*setOnPreferenceClickListener(
            "zero_aicy_enable_follow_system",
            new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference p1) {
					return true;
				}
			});*/
	}
	private void setOnPreferenceClickListener(
        String key, Preference.OnPreferenceClickListener onPreferenceClickListener) {
        Preference preference = findPreference(key);
        if (preference != null) {
            preference.setOnPreferenceClickListener(onPreferenceClickListener);
        } else {
			//Toasty.error(String.format("找不到%s",key)).show();
		}
    }

}
