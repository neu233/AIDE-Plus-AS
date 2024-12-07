package io.github.zeroaicy.aide.extend;

import android.content.Context;
import android.content.Intent;
import com.aide.common.KeyStroke;
import com.aide.ui.ServiceContainer;
import com.aide.ui.rewrite.R;
import com.aide.ui.services.FileBrowserService;
import java.io.File;
import com.aide.ui.command.KeyStrokeCommand;
import com.aide.ui.command.MenuCommand;
import androidx.annotation.Keep;
import android.content.ComponentName;

@Keep
public class OpenAideTermux implements KeyStrokeCommand, MenuCommand {

	@Override
	public String getName() {
		return "Open AIDE Termux";
	}

	@Override
	public KeyStroke getKeyStroke() {
		//不支持快捷方式，但是
		return new KeyStroke('\256', true, true, true);
	}

	//命令
	private static final String gradle_cmd_line_extra = "gradle_cmd_line_extra";
	//工作目录
	private static final String work_dir_extra = "work_dir_extra";

	private static final OpenAideTermux singleton = new OpenAideTermux();
	public static OpenAideTermux getSingleton() {
		return singleton;
	}

	@Override
	public int getMenuItemId() {
		return R.id.filebrowserMenuOpenAideTermux;
	}

	@Override
	public boolean isEnabled() {
		String currentFilePath;
		FileBrowserService fileBrowserService = ServiceContainer.getFileBrowserService();
		if (fileBrowserService.FH() != null) {
			//长按文件得到了文件
			currentFilePath = fileBrowserService.FH();
		} else {
			//FileBrowserService CurrentDir
			currentFilePath = fileBrowserService.j6();
		}
		
		if (currentFilePath == null) {
			return false;
		}

		File currentFile = new File(currentFilePath);
		if (currentFile.isDirectory()) {
			//是文件夹直接返回
			return true;
		}
		//不是路径看看父目录是不是文件夹
		File currentFileParentFile = currentFile.getParentFile();
		if (currentFileParentFile.isDirectory()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean run() {
		FileBrowserService fileBrowserService = ServiceContainer.getFileBrowserService();
		//长按
		String currentFilePath = fileBrowserService.FH();
		if (currentFilePath == null) {
			//FileBrowserService CurrentDir
			currentFilePath = fileBrowserService.j6();
			if (currentFilePath == null) {
				return false;
			}
		}
		
		//当前文件夹
		File currentFile = new File(currentFilePath);

		//确保打开的是文件夹
		if (!currentFile.isDirectory()) {
			//不是文件夹，查看父目录是不是文件夹
			File currentFileParentFile = currentFile.getParentFile();
			if (currentFileParentFile.isDirectory()) {
				currentFile = currentFileParentFile;
			} else {
				return true;
			}
		}
	
		Context context = ServiceContainer.getContext();
		Intent launchIntentForPackage;
		if ( "io.github.zeroaicy.aide2".equals( context.getPackageName()) ){
			launchIntentForPackage = new Intent().setComponent(new ComponentName(context, "com.termux.app.TermuxActivity"));
		}else{
			launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage("com.aide.termux");

		}
		if (launchIntentForPackage == null) {
			com.aide.common.MessageBox.BT(ServiceContainer.getMainActivity(), "运行错误", "AIDE-Termux未安装");
			return true;
		}
		
		launchIntentForPackage.putExtra(work_dir_extra, currentFile.getAbsolutePath());
		context.startActivity(launchIntentForPackage);
		return true;
	}

	@Override
	public boolean isVisible(boolean p) {
		if (isEnabled()) {
			if (!"com.aide.web".equals(ServiceContainer.appId)) {
				return true;
			}
		}
		return false;
	}
}
