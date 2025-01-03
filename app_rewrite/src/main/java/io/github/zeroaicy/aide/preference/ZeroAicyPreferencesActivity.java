package io.github.zeroaicy.aide.preference;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.aide.ui.preferences.PreferencesActivity;
import com.aide.ui.rewrite.R;
import com.probelytics.Probelytics;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation") 
public class ZeroAicyPreferencesActivity extends PreferencesActivity implements ActionBar.TabListener {

    @SuppressWarnings({"unused"})
	private final String TAG9999_5 = "ZeroAicyPreferencesActivity";

	private final String TAG_TAB_SETTING = "tag_tab_settings";

    private final String TAG_TAB_ADVANCED_SETTING = "tag_tab_advance_settings";
	
	
    private final String TAG_FRAGMENT_ADVANCED_SETTING = "tag_fragment_advance_settings";

	private FragmentManager fm;

    private ZeroAicySettingsFragment mZeroAicySettingsFragment;

    private ListView lv = null;

	

    private boolean from_main;

	private Fragment lastVisibleFragment;

    private Menu mOptionMenu;
    public Menu getOptionMenu() {
        return mOptionMenu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lv = getListView();

        from_main = getIntent().getBooleanExtra("from_main", false);
		if (!from_main) {
			return;
		}
		ActionBar actionBar = getActionBar();
		if (actionBar == null) {
			return;
		}
		actionBar.setTitle(R.string.app_name);
		actionBar.setDisplayHomeAsUpEnabled(true);

		fm = getFragmentManager();

		if (fm.findFragmentByTag(TAG_FRAGMENT_ADVANCED_SETTING) == null) {

			mZeroAicySettingsFragment = new ZeroAicySettingsFragment();

			FragmentTransaction bt = fm.beginTransaction();
			bt.add(android.R.id.content, mZeroAicySettingsFragment, TAG_FRAGMENT_ADVANCED_SETTING);
			bt.hide(mZeroAicySettingsFragment);
			bt.commit();
			
		} else {
			mZeroAicySettingsFragment =
				(ZeroAicySettingsFragment) fm.findFragmentByTag(TAG_FRAGMENT_ADVANCED_SETTING);
		}

		ActionBar actionbar = actionBar;

		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionbar.addTab(
			actionbar
			.newTab()
			.setTag(TAG_TAB_SETTING)
			.setText(R.string.command_settings)
			.setTabListener(this));

		actionbar.addTab(
			actionbar
			.newTab()
			.setTag(TAG_TAB_ADVANCED_SETTING)
			.setText(R.string.zeroaicy_settings)
			.setTabListener(this));

		actionbar.getTabAt(0).select();
	}


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction p2) {
		
        if (mZeroAicySettingsFragment == null) return;
		
		FragmentTransaction beginTransaction = fm.beginTransaction();
		
		switch (tab.getTag().toString()) {
			
			// AIDE原设置
			case TAG_TAB_SETTING:
                lv.setVisibility(View.VISIBLE);
				beginTransaction.hide(mZeroAicySettingsFragment);
				
				if (lastVisibleFragment != null) {
					beginTransaction.show(lastVisibleFragment);
				}
				
				beginTransaction.commit();
                break;

            case TAG_TAB_ADVANCED_SETTING:
				
                lv.setVisibility(View.GONE);
				
				
				lastVisibleFragment = getVisibleFragment();
				
				if (lastVisibleFragment != null) {
					beginTransaction.hide(lastVisibleFragment);
				}
				beginTransaction.show(mZeroAicySettingsFragment).commit();
                break;
        }
    }

	public Fragment getVisibleFragment() {
		
		FragmentManager fragmentManager = fm;
		
		List<Fragment> fragments = 
		Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
		fragmentManager.getFragments() : Collections.emptyList() ;
		
		for (Fragment fragment : fragments) {
			if (fragment != null && fragment.isVisible())
				return fragment;
		}
		return null;
	}
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction p2) {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction p2) {}

    @Override
    public void onBackPressed() {
        if (from_main) {
            ActionBar actionbar = getActionBar();
            switch (actionbar.getSelectedTab().getTag().toString()) {
                case TAG_TAB_ADVANCED_SETTING:
                    actionbar.getTabAt(0).select();
                    return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getActionBar().setTitle(R.string.command_settings);
        } else {
            getActionBar().setTitle(null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }


	public static void DW(Activity activity, int i) {
		Intent intent = new Intent(activity, ZeroAicyPreferencesActivity.class);
		intent.putExtra("SHOW_PAGE", i);
		activity.startActivity(intent);
		Probelytics.BT(activity, intent);
    }
}


