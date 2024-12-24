package io.github.zeroaicy.aide.highlight;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.aide.ui.ThemedActionbarActivity;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings({"deprecation"})
public class HighlightActivity extends ThemedActionbarActivity implements AdapterView.OnItemClickListener ,ActionBar.TabListener {

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
		// TODO: Implement this method
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		toggleList("p1".equals(tab.getTag()));
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
		// TODO: Implement this method
	}



	// private boolean from_main;

	private ListView mListView;

	private ArrayList<ColorKind> list;

	private HighlightAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// from_main  =  getIntent().getBooleanExtra("from_main", false);
		mListView  =  new ListView(this);

		mListView.setDivider(null);
        mListView.setFastScrollEnabled(true);
		setContentView(mListView);

		mListView.setOnItemClickListener(this);

		toggleList(ZeroAicySetting.isLightTheme());
		initActionBar();
	}

	private void toggleList(boolean isLight) {
        list  =  new ArrayList<ColorKind>();
		list.addAll(Arrays.asList(ColorKind.values()));

        if (adapter  ==  null) {
            adapter  =  new HighlightAdapter(this, list);
            adapter.setIsLight(isLight);
            mListView.setAdapter(adapter);
        } else {
            adapter.setIsLight(isLight);
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }

	private void initActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(getIntent().getCharSequenceExtra("title"));
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.addTab(actionBar.newTab()
						 .setText("亮主题高亮配置")
						 .setTag("p1")
						 .setTabListener(this));


        actionBar.addTab(actionBar.newTab()
                         .setText("暗主题高亮配置")
                         .setTag("p2")
                         .setTabListener(this));


		if (!ZeroAicySetting.isLightTheme()) {
			actionBar.getTabAt(1).select();
		} else {
			actionBar.getTabAt(0).select();
		}
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long itemId) {
        final ColorKind colorKind = adapter.getItem(position);

		// 编辑后回调 返回结果 颜色值 以及可能的 字体值
		final ColorKindEditDialog colorKindEditDialog = new ColorKindEditDialog(this, colorKind.getColor(this, adapter.isLight()));

		colorKindEditDialog.setHexValueEnabled(true);
		colorKindEditDialog.setAlphaSliderVisible(true);

		if (colorKind.hasTypefaceStyle()) {
			colorKindEditDialog.setTypefaceStyleEditEnabled(true);
			colorKindEditDialog.setTypefaceStyleValue(colorKind.getTypefaceStyle());

		}

		colorKindEditDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 保存更改
					saveColorKind(colorKindEditDialog, colorKind);
				}
			});
		colorKindEditDialog.show();
    }

	private void saveColorKind(ColorKindEditDialog colorKindEditDialog, ColorKind colorKind) {
		colorKind.setCustomColor(colorKindEditDialog.getColor(), this.adapter.isLight());
		colorKind.setTypefaceStyle(colorKindEditDialog.getTypefaceStyleValue());

		this.adapter.notifyDataSetInvalidated();
		// 保存ColorKind
		CodeTheme.save(HighlightActivity.this, colorKind);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//SubMenu sm = menu.addSubMenu("还原": "Restore");
		Menu sm = menu;
		sm.add("还原亮主题").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){

				@Override
				public boolean onMenuItemClick(MenuItem menuItem) {
					CodeTheme.restore(true);
					showToast("亮主题已还原，字体风格未还原");

					onTabSelected(getActionBar().getSelectedTab(), null);
					HighlightActivity.this.adapter.notifyDataSetInvalidated();
					
					return false;
				}
			});

		sm.add("还原暗主题").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
				@Override
				public boolean onMenuItemClick(MenuItem menuItem) {
					CodeTheme.restore(false);

					onTabSelected(getActionBar().getSelectedTab(), null);

					showToast("已还原暗主题，字体风格未还原");

					HighlightActivity.this.adapter.notifyDataSetInvalidated();

					return false;
				}
			});
		return super.onCreateOptionsMenu(menu);
	}
	public void showToast(String msg) {
		Toast.makeText(HighlightActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
	
	
	/* @Override
	 public boolean onCreateOptionsMenu2(Menu menu) {
	 //SubMenu sm = menu.addSubMenu("还原": "Restore");
	 Menu sm = menu;
	 sm.add("还原").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){

	 @Override
	 public boolean onMenuItemClick(MenuItem p1) {
	 CodeTheme.restore();
	 onTabSelected(getActionBar().getSelectedTab(), null);
	 //isDataChangeed  =  true;
	 //Toasty.success("已还原至默认").show();

	 return false;
	 }
	 });



	 sm.add("还原亮/暗").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){

	 @Override
	 public boolean onMenuItemClick(MenuItem p1) {
	 SharedPreferences sp = HighlightUtils.getHSp();
	 SharedPreferences.Editor edit = sp.edit();
	 edit.clear();
	 for (Color color:HighlightUtils.getHighlightColor().values()) {
	 edit.putString(HighlightUtils.getSpKey(color.name, true), color.lightColor);
	 edit.putString(HighlightUtils.getSpKey(color.name, false), color.darkColor);
	 }

	 edit.commit();

	 onTabSelected(getActionBar().getSelectedTab(), null);

	 isDataChangeed  =  true;
	 //Toasty.success("已还原至默认": "Reset to default").show();

	 return false;
	 }
	 });



	 sm.add("复制配置": "Copy configuration").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){

	 @Override
	 public boolean onMenuItemClick(MenuItem p1) {
	 StringBuffer sb  =  new StringBuffer();
	 sb.append(AppTheme.getThemeCode());
	 for (Item item : list) {
	 sb.append(";");
	 sb.append(compresscolor(item.getColor(true)));
	 sb.append(",");
	 sb.append(compresscolor(item.getColor(false)));
	 }
	 Utils.copyToClipboard(EncodeUtils.base64Encode2String(Utils.gzip(sb.toString().getBytes())));
	 return false;
	 }

	 private String compresscolor(String color) {
	 if (color.length()  ==  9 && color.toLowerCase().startsWith("#ff")) {
	 return color.substring(3);
	 }
	 return color
	 ;
	 }
	 });


	 sm.add("导入配置": "Import configuration").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){

	 @Override
	 public boolean onMenuItemClick(MenuItem p1) {
	 LinearLayout view  =  new LinearLayout(HighlightActivity.this);
	 view.setOrientation(view.VERTICAL);
	 view.setPadding(
	 SizeUtils.dp2px(24), SizeUtils.dp2px(10), SizeUtils.dp2px(24), SizeUtils.dp2px(16));
	 final EditText input  =  new EditText(HighlightActivity.this);
	 view.addView(input, -1, -2);


	 final AlertDialog dialog  = 
	 new AlertDialog.Builder(HighlightActivity.this)
	 .setTitle(p1.getTitle())
	 .setView(view)
	 .setPositiveButton("导入": "Import", null)
	 .setNegativeButton(android.R.string.cancel, null)
	 .create();
	 dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	 dialog.show();

	 input.addTextChangedListener(new TextWatcher(){

	 @Override
	 public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	 }

	 @Override
	 public void onTextChanged(CharSequence s, int start, int before, int count) {
	 dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(input.length()! =  0);
	 }

	 @Override
	 public void afterTextChanged(Editable s) {

	 }
	 });

	 dialog.getButton(AlertDialog.BUTTON_POSITIVE).set
	 (new View.OnClickListener() {

	 @Override
	 public void onClick(View view) {
	 dialog.dismiss();
	 try {
	 String data  =  ConvertUtils.bytes2String(Utils.ungzip(EncodeUtils.base64Decode(input.getText().toString())));

	 String[] split  =  data.split(";");
	 String themeCode  =  split[0];

	 SharedPreferences sp = HighlightUtils.getHSp();
	 SharedPreferences.Editor edit = sp.edit();

	 int offset  =  1;
	 for (int i  =  offset; i < split.length; i++) {
	 try {
	 String[] colors  =  split[i].split(",");

	 edit.putString(list.get(i - offset).getSpKey(true), decompresscolor(colors[0]));
	 edit.putString(list.get(i - offset).getSpKey(false), decompresscolor(colors[1]));
	 }
	 catch (Throwable e) {}
	 }

	 edit.commit();
	 onTabSelected(getActionBar().getSelectedTab(), null);
	 isDataChangeed  =  true;
	 }
	 catch (Throwable e) {
	 Utils.showExDialog(HighlightActivity.this, e);
	 }

	 }

	 private String decompresscolor(String color) {
	 if (!color.startsWith("#")) {
	 return "#" + color;
	 }
	 return color
	 ;
	 }


	 });

	 input.setText("");


	 return false;
	 }
	 });

	 return super.onCreateOptionsMenu(menu);
	 }//*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
				//if (!from_main) overridePendingTransition(0, 0);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getActionBar().getTabCount() >=  2) {
            if (getActionBar().getSelectedTab().getTag().equals("p2")) {
                getActionBar().getTabAt(0).select();
                return;
            }
        }

        super.onBackPressed();
        // overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        // if (!from_main)overridePendingTransition(0, 0);

    }
    private boolean isDataChangeed=false;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isDataChangeed) {
            //AIDEUtils.getAIDEEditorPager().Zo();
            //AIDEUtils.getMainActivity().setEditorBackground();
        }
    }


}
