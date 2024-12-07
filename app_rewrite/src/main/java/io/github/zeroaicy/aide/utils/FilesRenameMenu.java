package io.github.zeroaicy.aide.utils;

import abcd.zb;
import android.text.TextUtils;
import com.aide.common.KeyStroke;
import com.aide.common.MessageBox;
import com.aide.common.ValueRunnable;
import com.aide.ui.ServiceContainer;
import com.aide.ui.rewrite.R;
import com.aide.ui.util.FileSystem;
import java.io.File;

public class FilesRenameMenu extends zb {

		public FilesRenameMenu() {}

		public boolean DW(boolean z) {
			String path = ServiceContainer.getFileBrowserService().FH();
			
			if (path != null) {
				if( new File(path).exists()){
					return true;
				}
				if (!FileSystem.hasParent(path)) {
					return true;
				}
			}
			return false;
		}

		public int FH() {
			return R.id.filebrowserMenuRename;
		}

		public String getName() {
			return "Files - Rename";
		}

		public boolean isEnabled() {
			return DW(false);
		}

		public boolean run() {
			final String FH = ServiceContainer.getFileBrowserService().FH();
			if( TextUtils.isEmpty( FH)){
				return false;
			}
			MessageBox.XL(ServiceContainer.getMainActivity(), R.string.dialog_rename_title, R.string.dialog_rename_message, FileSystem.getName(FH), new ValueRunnable<String>(){
					@Override
					public void acceptValue(String t){
						ServiceContainer.getMainActivity().delayedShowAnalyzingProgressDialog();
						ServiceContainer.getEngineService().SI(FH, t);
					}
				});
			return true;
		}

		public KeyStroke v5() {
			return new KeyStroke(46, false, true, false);
	}
	
}
