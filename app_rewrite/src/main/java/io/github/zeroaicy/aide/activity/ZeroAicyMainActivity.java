package io.github.zeroaicy.aide.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import com.aide.common.AndroidHelper;
import com.aide.common.AppLog;
import com.aide.ui.MainActivity;
import com.aide.ui.ServiceContainer;
import com.aide.ui.rewrite.R;
import com.aide.ui.services.OpenFileService;
import com.aide.ui.util.FileSpan;
import com.aide.ui.util.FileSystem;
import com.aide.ui.views.SplitView;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.probelytics.Probelytics;
import io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface;
import io.github.zeroaicy.aide.preference.ZeroAicyPreferencesActivity;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.aide.ui.services.ThreadPoolService;
import io.github.zeroaicy.aide.ui.views.ZeroAicySplitView;
import io.github.zeroaicy.util.reflect.ReflectPie;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import android.view.Display;

public class ZeroAicyMainActivity extends MainActivity {


	// 用于通知AIDE CodeModel
	@SuppressWarnings("unused") 
	private static final String TAG_1595554556 = "ZeroAicyMainActivity";

	private static final String TAG = "ZeroAicyMainActivity";

	static ZeroAicyExtensionInterface zeroAicyExtensionInterface;

	boolean isRecreate = false;
	@Override
	public void onCreate(Bundle bundle) {
		if (isRecreate) {
			return;
		}

		super.onCreate(bundle);
		// 隐藏Home键
		getActionBar().setDisplayShowHomeEnabled(false);

		if (enableActionDrawerLayout()) {
			setUpDrawerLayout();
		}

		if (!ZeroAicySetting.isWatch()) {
			// 检查并申请管理外部储存权限
			showRequestManageExternalStorage();
		}
	}

	private boolean isExit = false;
	@Override
	public void BT() {
		// 若没有需要保存的文件
		// 则 finish()
		OpenFileService openFileService = ServiceContainer.getOpenFileService();
		if (!openFileService.U2()) {
			this.isExit = true;
		}
		super.BT();

	}

	@Override
	public void recreate() {
		this.isExit = false;
		this.isRecreate = true;
		super.recreate();
	}

	@Override
	public void finish() {
		super.finish();
		exit();
	}

	@Override
	protected void onDestroy() {

//		//  Trying to restart engine service
//		// ServiceContainer.shutdown()
//		EngineService engineService = ServiceContainer.getEngineService();
//		// 
//		engineService.lp();
//		
		super.onDestroy();
		exit();
	}

	private void exit() {
		if (this.isExit) {
			this.isExit = false;
			// 强制退出，防止ServiceContainer::shutdown()与异步导致的错误
			System.exit(0);
			android.os.Process.killProcess(android.os.Process.myPid());
		}

	}


	/**
	 * 是否启用DrawerLayout
	 */
	private boolean enableActionDrawerLayout() {
		return !ServiceContainer.isTrainerMode() 
			&& ZeroAicySetting.enableActionDrawerLayout();
	}

	public void q7Async() {
		super.q7();
	}

	@Override
	public void q7() {
		// -> Jl() -> com.aide.ui.m::FH
		// -> ImageView.setVisibility
		runOnUiThread(new Runnable(){
				@Override
				public void run() {
					q7Async();
				}
			});
	}


	public void DWAsync() {
		super.DW();
	}
	@Override
	public void DW() {
		runOnUiThread(new Runnable(){
				@Override
				public void run() {
					DWAsync();
				}
			});
	}


	public void eUAsync() {
		super.eU();
	}
	@Override
	public void eU() {
		runOnUiThread(new Runnable(){
				@Override
				public void run() {
					eUAsync();
				}
			});
	}

	// 必须在主线程
	@Override
	public void Hw(final String string) {
		if (ThreadPoolService.isUiThread()) {
			super.Hw(string);
			return;
		}
		runOnUiThread(new Runnable(){
				@Override
				public void run() {
					HwAsync(string);
				}
			});
	}
	public void HwAsync(String string) {
		super.Hw(string);
	}

	// mainMasterButton 点击回调
	//*
	@Override
	public void Nh() {
		if (enableActionDrawerLayout() && mDrawerLayout != null) {
			if (mDrawerLayout.isOpen()) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
			} else {
				mDrawerLayout.openDrawer(Gravity.LEFT);
			}
		} else {
			super.Nh();			
		}
	}
	//*/


	@Override
	public void setContentView(int layoutResID) {
		// 仅替换 R.layout.main
		if (layoutResID != R.layout.main) {
			super.setContentView(layoutResID);
		} else if (enableActionDrawerLayout()) {
			super.setContentView(R.layout.main_drawer);
		} else {
			super.setContentView(R.layout.main);
		}
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout != null && mDrawerLayout.isOpen()) {
			mDrawerLayout.close();
			return;
		}
		super.onBackPressed();
	}

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	ZeroAicySplitView zeroAicySplitView;
	@SuppressLint("ClickableViewAccessibility")
    public void setUpDrawerLayout() {

		this.mDrawerLayout = findViewById(R.id.mainDrawerLayout);

		this.getActionBar().setDisplayHomeAsUpEnabled(true);

        this.mDrawerToggle =
			new ActionBarDrawerToggle(this,
									  mDrawerLayout,
									  // R.drawable.ic_drawer,
									  R.string.app_name,
									  R.string.app_name);
		this.mDrawerLayout.addDrawerListener(mDrawerToggle);
        this.mDrawerToggle.syncState();

		this.mDrawerLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                mDrawerLayout.close();
            return true;
        });

		this.mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener(){
				@Override
				public void onDrawerClosed(View view) {
					mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

				}
				@Override
				public void onDrawerOpened(View view) {
					mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
				}
			});

		SplitView splitView = getSplitView();
		if (splitView instanceof ZeroAicySplitView) {
			zeroAicySplitView = (ZeroAicySplitView) splitView;
			// closeSplit
			zeroAicySplitView.closeSplit(false);

			// SplitView事件拦截器
			zeroAicySplitView.setOnSplitInterceptListener(new ZeroAicySplitView.OnSplitInterceptListener(){
					@Override
					public boolean closeSplit(boolean animator, Runnable animatorListenerAdapterRunable) {
						return true;
					}
					@Override
					public boolean openSplit(boolean isHorizontal, boolean animator) {
						mDrawerLayout.openDrawer(Gravity.LEFT);
						return true;
					}
				});
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (menuItem.getItemId() == android.R.id.home 
			&& this.mDrawerToggle != null 
			&& this.mDrawerToggle.onOptionsItemSelected(menuItem)) {
			Nh();
            return true;
        } else if (handleOptionsItemSelected(menuItem)) {
            return true;
        } else {
            return super.onOptionsItemSelected(menuItem);
        }

	}




	/**
	 * 显示授权请求弹窗
	 */
	public void showRequestManageExternalStorage() {
		if (XXPermissions.isGranted(this, android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
			return;
		}
		String app_name = getString(R.string.app_name);
		String message = new StringBuilder("为了访问您设备上的文件，您需要手动为")
			.append(app_name)
			.append("授予「所有文件访问」权限，点击确认后进入设置界面，选择「")
			.append(app_name)
			.append("」并开启授权。")
			.toString();
		new AlertDialog.Builder(ZeroAicyMainActivity.this)
			.setTitle("授权请求")
			.setMessage(message)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					requestManageExternalStorage();
				}
			})
			.show();
	}

	/**
	 * 申请 所有文件访问权限
	 */
	@SuppressLint("WrongConstant")
    public void requestManageExternalStorage() {
		XXPermissions.with(this).
			permission(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
			.request(new OnPermissionCallback(){
				public void onDenied(List<String> permissions, boolean doNotAskAgain) {
					showRequestManageExternalStorage();
				}
				@Override
				public void onGranted(List<String> list, boolean p) {}
			});
	}

	@Override
	public void setHasEmbeddedTabs() {
		//ServiceContainer.Mz() && AndroidHelper.u7(this) <= 610.0f
		AndroidHelper.setActionBarHasEmbeddedTabs(this, ZeroAicySetting.enableActionBarSpinner() 
												  || (ServiceContainer.isTrainerMode() 
												  && AndroidHelper.getScreenWidthInDp(this) <= 610.0f));
		//绑定监听器
		AndroidHelper.setTabSpinnerOnClickListener(this);
	}


	//当前屏幕的高度
	public static float Zo(Context context) {
		try {

			@SuppressWarnings("deprecation") 
				Display defaultDisplay = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ? 
				context.getDisplay() 
				: ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();


			// Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
			int height = defaultDisplay.getHeight();
			return height / context.getResources().getDisplayMetrics().density;
        }
		catch (Throwable t) {
			throw new RuntimeException(t);
        }
    }


	//可见屏幕的高度
	public static float Ws(Context context) {
        try {
            Activity activity = (Activity) context;
            float f = activity.getResources().getDisplayMetrics().density;
            Rect rect = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            return (rect.bottom - rect.top) / f;
        }
		catch (Throwable e) {
			throw new RuntimeException(e);
        }
    }

	@Override
	public void FH(boolean z) {
		ServiceContainer.DW().u7(!z);
		q7();
		if (z) {
            boolean isLandscape = isLandscape();
			if (isLandscape && ((AndroidHelper.getScreenHeightInDp(this) > 800.0f || getSplitView().isHorizontal() && AndroidHelper.getScreenHeightInDp(this) >= 540.0f))) {
				return;
			}
			Ws(false);
		}
	}
	//是否横屏
	private boolean isLandscape() {
		boolean isLandscape = false;
		Configuration configuration = getResources().getConfiguration();
		if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 设备处于横屏模式下
			isLandscape = true;
		}
		return isLandscape;
	}

	@Override
	public void IS(int showPageIndex) {
		//拦截并替换设置PreferencesActivity
		ZeroAicyPreferencesActivity.DW(this, showPageIndex);
    }


	@Override
	public void openFile(String str) {
		String suffixName = FileSystem.getSuffixName(str).toLowerCase();
		String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffixName);

		if (mimeTypeFromExtension != null 
			&& !mimeTypeFromExtension.startsWith("text")
			&& !mimeTypeFromExtension.equals("application/javascript")

			&& !suffixName.equals("java") 
			&& !suffixName.equals("class") 
			&& !suffixName.equals("xml") 
			&& !suffixName.equals("svg") 

			&& !suffixName.equals("js") 
			&& !suffixName.equals("css") 
			) {


			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");

			Uri fromFile = null;
			if (Build.VERSION.SDK_INT >= 24) {
				fromFile = FileProvider.getUriForFile(this, FileSystem.j3(), new File(str));
				intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			} else {
				fromFile = Uri.fromFile(new File(str));
			}
			intent.setDataAndType(fromFile, mimeTypeFromExtension);
			try {
				gn(this, intent);
				startActivity(intent);
				Probelytics.BT(this, intent);
			}
			catch (ActivityNotFoundException unused) {
				Context VH = ServiceContainer.getContext();
				Toast.makeText(VH, "No handler found for type " + mimeTypeFromExtension, Toast.LENGTH_SHORT).show();
			}
			return;
		}

		if (FileSystem.isEmptyFile(str)) {
			return;
		}

		AppLog.d(TAG, "openFile this %s", this);
		AppLog.d(TAG, "ServiceContainer isShutdowned %s", String.valueOf(ServiceContainer.isShutdowned()));

		aq(new FileSpan(str, 1, 1, 1, 1));
		ServiceContainer.getProjectService().openFile(str);


    }

	private static void gn(Object obj, Intent intent) {
        ((MainActivity) obj).startActivity(intent);
        Probelytics.BT(obj, intent);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}


	//命令
	private static final String gradle_cmd_line_extra = "gradle_cmd_line_extra";
	//工作目录
	private static final String work_dir_extra = "work_dir_extra";

	private boolean handleOptionsItemSelected(MenuItem menuItem) {
		int itemId = menuItem.getItemId();
		if (itemId == R.id.mainMenuSettings) {
			startActivity(new Intent(this, ZeroAicyPreferencesActivity.class).putExtra("from_main", true));
			return true;
		}
		if (itemId == R.id.mainMenuRunGradle) {

			return handleRunGradle(menuItem);
		}
		return false;
	}

	private boolean handleRunGradle(MenuItem menuItem) {
		showGradleBuildDialog(menuItem);
		return true;
	}

	private void showGradleBuildDialog(final MenuItem runMenuItem) {
		String currentAppHome = ZeroAicySetting.getCurrentAppHome();
		final Map<CharSequence, String> itemNameMap = new LinkedHashMap<>();
		//只有是gradle项目才添加
		boolean hasGradlew = hasGradlew(currentAppHome);
		if (hasGradlew) {
			itemNameMap.putAll(ZeroAicySetting.getCommands());
		}
		boolean isCN = getResources().getConfiguration().locale.equals(Locale.CHINA);
		itemNameMap.put(isCN ? "运行终端" : "Terminal", "terminal");

		PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.mainMenuRunGradle));
		Menu menu = popupMenu.getMenu();

		for (final CharSequence itemName : itemNameMap.keySet()) {
			menu.add(itemName)
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
					@Override
					public boolean onMenuItemClick(MenuItem _item) {
						String cmdline = itemNameMap.get(itemName);

						Intent launchIntentForPackage;
						if ("io.github.zeroaicy.aide2".equals(getPackageName())) {
							launchIntentForPackage = new Intent().setComponent(new ComponentName(ZeroAicyMainActivity.this, "com.termux.app.TermuxActivity"));
						} else {
							launchIntentForPackage = getPackageManager().getLaunchIntentForPackage("com.aide.termux");

						}

						if (launchIntentForPackage == null) {
							com.aide.common.MessageBox.BT(ServiceContainer.getMainActivity(), "运行错误", "AIDE-Termux未安装或找不到主Activity");
							return true;
						}


						String currentAppHome = ZeroAicySetting.getCurrentAppHome();
						if (currentAppHome == null) {
							com.aide.common.MessageBox.BT(ServiceContainer.getMainActivity(), "没有打开Gradle项目", "请保证项目目录下GradleWrapper(Gradle包装器)");
							return true;
						}
						File gradleProjectRootDir = new File(currentAppHome).getParentFile();
						launchIntentForPackage.putExtra(work_dir_extra, gradleProjectRootDir.getAbsolutePath());
						if (cmdline.contains("gradle")) {
							if (!hasGradlew(currentAppHome)) {
								com.aide.common.MessageBox.BT(ServiceContainer.getMainActivity(), "不是Gradle项目", "请保证项目目录下GradleWrapper(Gradle包装器)");
								return true;
							}
							launchIntentForPackage.putExtra(gradle_cmd_line_extra, cmdline);
						}
						startActivity(launchIntentForPackage);

						return true;
					}
				});
		}
		popupMenu.show();
	}

	private boolean hasGradlew(String currentAppHome) {
		if (TextUtils.isEmpty(currentAppHome)) {
			return false;
		}
		File gradleProjectRootDir = new File(currentAppHome).getParentFile();

		File gradlewFile = new File(gradleProjectRootDir, "gradlew");
		File gradleWrapperJarFile = new File(gradleProjectRootDir, "gradle/wrapper/gradle-wrapper.jar");
		File gradleWrapperProperties = new File(gradleProjectRootDir, "gradle/wrapper/gradle-wrapper.properties");

		return gradlewFile.isFile()
			&& gradleWrapperJarFile.isFile()
			&& gradleWrapperProperties.isFile();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		RepairBUG1(menu);
		if (!ServiceContainer.isTrainerMode()) {
			RepairBUG2(menu);
		}
		boolean onPrepareOptionsMenu = super.onPrepareOptionsMenu(menu);
		for (int index= 0, size = menu.size(); index < size; index++) {
			MenuItem menuItem = menu.getItem(index);
			if (!menuItem.isEnabled() && menuItem.isVisible()) {
				SpannableString s = new SpannableString(menuItem.getTitle());
				s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, s.length(), 0);
				menuItem.setTitle(s);
			}
		}
		/*
		 MenuItem mainMenuSaveItem = menu.findItem(R.id.mainMenuSave);

		 if( mainMenuSaveItem != null){
		 MenuItemCommand saveMenuItemCommand = AppCommands.u7(mainMenuSaveItem.getItemId());
		 // setEnabled不变灰色，那就隐藏😕
		 boolean isEnabled = saveMenuItemCommand.isEnabled();
		 if( !isEnabled ){
		 // 设置背景
		 SpannableString s = new SpannableString(mainMenuSaveItem.getTitle());
		 s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, s.length(), 0);
		 mainMenuSaveItem.setTitle(s);

		 // 隐藏
		 //mainMenuSaveItem.setVisible(isEnabled);
		 }
		 }*/
		return onPrepareOptionsMenu;
	}


	//查找Toolbar
	public static android.widget.Toolbar findToolbarByMenu(Menu mMenu) {
		try {
            if (mMenu == null) {
				//实例是 MenuItemImpl
                return null;
            }
			CopyOnWriteArrayList<WeakReference> mPresenters = ReflectPie.on(mMenu).get("mPresenters");
            for (WeakReference ref : mPresenters) {
                final Object presenter = ref.get();
                if (presenter == null) {
                    mPresenters.remove(ref);
					continue;
                }
				if (presenter.getClass().getName().contains("Toolbar$")) {
					Object unknownToolbar = ReflectPie.on(presenter).get("this$0");
					if (unknownToolbar instanceof android.widget.Toolbar) {
						return (android.widget.Toolbar)unknownToolbar;
					}
                }
            }
        }
		catch ( Throwable e) {}
		return null;
	}
	//Menu clear修复
	public static void RepairBUG1(Menu mMenu) {
        try {
            if (mMenu == null) {
                return;
            }
			android.widget.Toolbar mToolbar = findToolbarByMenu(mMenu);
			RepairCollapseActionView(mToolbar);//修复
        }
		catch ( Throwable e) {}

    }
	//mCollapseButtonView 修复
	public static void RepairBUG2(Menu mMenu) {
        try {
			final android.widget.Toolbar mToolbar = findToolbarByMenu(mMenu);
			if (mToolbar == null) {
				return;
			}
			View mCollapseButtonView = ReflectPie.on(mToolbar).get("mCollapseButtonView");
			if (mCollapseButtonView == null) {

			}
			if (mCollapseButtonView != null) {
				mCollapseButtonView.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View view) {
							try {
								//修复
								RepairCollapseActionView(mToolbar);
								mToolbar.collapseActionView();
							}
							catch ( Throwable e) {}
						}
					});
			}
        }
		catch ( Throwable e) {}
	}

	//修复 collapseActionView方法
	public static void RepairCollapseActionView(android.widget.Toolbar mToolbar) {
        try {
            if (mToolbar == null) {
                return;
            }
			List<View> mHiddenViews = ReflectPie.on(mToolbar).get("mHiddenViews");
			if (mHiddenViews == null) {
				return;
			}
			for (View view : mHiddenViews) {
				ViewGroup parent = (ViewGroup) view.getParent();
				if (parent == null) {
					continue;
				}
				AppLog.d("RepairBUG", "移除Parent->" + view);
				parent.removeView(view);

			}
		}
		catch ( Throwable e) {}
	}
}
