//
// Decompiled by Jadx - 853ms
//
package com.aide.common;

import android.text.TextUtils;
import androidx.annotation.Keep;
import com.aide.common.MessageBox;
import com.aide.common.ValueRunnable;
import com.aide.ui.ServiceContainer;
import com.aide.ui.project.AndroidProjectSupport;
import com.aide.ui.rewrite.R;
import com.aide.ui.util.FileSystem;
import java.io.File;
import com.aide.ui.services.ProjectService;
import com.aide.ui.MainActivity;
import io.github.zeroaicy.util.Log;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import java.io.IOException;

/**
 * 安卓项目 添加xxx文件
 * 异步导致的启用不正确
 */
@Keep
public class AddAndroidFiles {

    public AddAndroidFiles() {

    }

	@Keep
    public static void DW(final String dirPath, final ValueRunnable<String> valueRunnable) {
		if (Zo(dirPath)) {
			MainActivity mainActivity = ServiceContainer.getMainActivity();
			
			MessageBox.XL(mainActivity, R.string.command_files_add_new_class, R.string.dialog_create_message, "", new ValueRunnable<String>(){
					@Override
					public void acceptValue(String className) {
						if (className.endsWith(".java")) {
							className = className.substring(0, className.length() - 5);
						}
						try {

							String javaFilePath = dirPath + File.separator + className + ".java";
							if( FileSystem.exists(javaFilePath)){
								throw new IOException(javaFilePath + " already exists"); 
							}
							// 如果类名中包含路径
							int classNameStart = className.lastIndexOf('/');
							if (classNameStart > 0) {
								className = className.substring(classNameStart + 1);
							}
							// 确保文件父目录存在
							String javaFileParentPath = FileSystem.getParent(javaFilePath);

							// mkdir
							if (!FileSystem.exists(javaFileParentPath)){
								FileSystem.mkdirs(javaFileParentPath);
							}
							
							
							ProjectService projectService = ServiceContainer.getProjectService();
							// 内容
							String sourceContent = String.format("/**\n * @Author %s\n * @AIDE AIDE+\n*/\n", ZeroAicySetting.getDefaultSpString("git_user_name", ""));

							// 包名
							String packageName = AndroidProjectSupport.Ev(projectService.getLibraryMapping(), projectService.getFlavor(), javaFileParentPath);
							if (!TextUtils.isEmpty(packageName)) {
								sourceContent += "package " + packageName + ";\n\n";
							}

							FileSystem.writeStringToFile(javaFilePath, sourceContent + "public class " + className + "{\n\n}");
							valueRunnable.acceptValue(javaFilePath);
						}
						catch (Throwable e) {
							MessageBox.P8(ServiceContainer.getMainActivity(), "Create Java Class",  new Throwable(Log.getStackTraceString(e)));
						}
					}
				});
		} else if (v5(dirPath)) {
			MessageBox.XL(ServiceContainer.getMainActivity(), R.string.command_files_add_new_xml, R.string.dialog_create_message, "", new ValueRunnable<String>(){
					@Override
					public void acceptValue(String name) {
						if (name.endsWith(".xml")) {
							name = name.substring(0, name.length() - 4);
						}
						String xmlPath = dirPath + File.separator + name + ".xml";
						String content;
						String parent = FileSystem.getParent(xmlPath);
						String parentName = FileSystem.getName(parent);

						if (parentName.startsWith("layout")) {
							content = "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n    android:layout_width=\"fill_parent\"\n    android:layout_height=\"fill_parent\"\n    android:orientation=\"vertical\">\n    \n</LinearLayout>\n";
						} else {
							if (parentName.startsWith("menu")) {
								content = "<menu xmlns:android=\"http://schemas.android.com/apk/res/android\">\n    \n    <item\n        android:id=\"@+id/item\"\n        android:title=\"Item\"/>\n    \n</menu>\n";
							} else {
								content = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
							}
						}
						FileSystem.writeStringToFile(xmlPath, content);
						valueRunnable.acceptValue(xmlPath);
					}
				});
		}
    }

	/**
	 * getDrawableId
	 */
	@Keep
    public static int getDrawableId(String str) {
		return R.drawable.file_new;
    }


	// old method 
    public static int getAddTypeName(String dirPath) {
		if (Zo(dirPath)) {
			return R.string.command_files_add_new_class;
		}
		if (v5(dirPath)) {
			return R.string.command_files_add_new_xml;
		}
		return 0;
    }

	/**
	 * 是否显示 command_files_add按钮
	 */
	@Keep
    public static boolean isVisible(String dirPath) {
		return Zo(dirPath) || v5(dirPath);
	}

	private static boolean Zo(String dirPath) {
		return isJavaSourceDir(dirPath); // || AndroidProjectSupport.Ev(ServiceContainer.getProjectService().getLibraryMapping(), ServiceContainer.getProjectService().getFlavor(), dirPath) != null;
    }
	/**
	 * 是否是xml路径[layout，menu]等
	 */
	private static boolean v5(String dirPath) {
		return isXmlSourceDir(dirPath) || ((FileSystem.parentFileNameContain(dirPath, "res") != null && FileSystem.isPrefix(ServiceContainer.getProjectService().getCurrentAppHome(), dirPath)));
    }


	public static boolean isJavaSourceDir(String dirPath) {
		if (TextUtils.isEmpty(dirPath)) {
			return false;
		}
		if (dirPath.contains("/java")
			|| dirPath.contains("/aidl")) {
			return true;
		}
		return false;
	}
	private static boolean isXmlSourceDir(String dirPath) {
		return !TextUtils.isEmpty(dirPath) && dirPath.lastIndexOf("res/") > 0;
	}



	/**
	 * 是否是源码路径
	 */
    private static boolean ZoOld(String dirPath) {
		return AndroidProjectSupport.Ev(ServiceContainer.getProjectService().getLibraryMapping(), ServiceContainer.getProjectService().getFlavor(), dirPath) != null;
    }

	/**
	 * 返回 command_files_add具体名称
	 */
	public static int getAddTypeName2(String dirPath) {
		// Java源码目录
		// class
		if (dirPath.contains("/java")) {
			return R.string.command_files_add_new_class;				
		}
		// xml
		if (v5(dirPath)) {
			// 是layout目录
			return R.string.command_files_add_new_xml;
		}
		return 0;
    }
}
