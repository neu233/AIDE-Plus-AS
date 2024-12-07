package io.github.zeroaicy.aide.aapt2;

import com.aide.ui.build.android.AaptService$ErrorResult;
import com.aide.ui.build.android.AaptService$Task;
import io.github.zeroaicy.util.Log;

public class Aapt2Task {

	private static final String TAG = Aapt2Task.class.getSimpleName();;
	//AaptService$c m;
	public static AaptService$ErrorResult proxyAapt(AaptService$Task task) {
		try {
			return proxyAaptZeroAicy(task);
		} catch (Throwable e) {
			Log.e(TAG, "proxyAapt出错 --> ", e);
			return new AaptService$ErrorResult(e.getLocalizedMessage());
		}
	}
	
	private static AaptService$ErrorResult proxyAaptZeroAicy(AaptService$Task aapt$c) throws Exception {
		return Aapt2TaskFromZeroAicy.proxyAapt(aapt$c);
	}

}
