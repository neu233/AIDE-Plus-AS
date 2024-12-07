//
// Decompiled by Jadx - 763ms
//
package com.aide.ui.build.android;

import com.aide.ui.build.android.AaptService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

class AaptService$FutureTask extends FutureTask<AaptService$ErrorResult> {


    final AaptService aaptService;
    public AaptService$FutureTask(AaptService aaptService, AaptService$Callable callable) {
        super(callable);
		this.aaptService = aaptService;
    }
	
	/**
	 * 必须运行在ProjectService所在线程
	 * 否则可能导致并发问题
	 */
    @Override
    protected void done() {
		if (isCancelled()) {
			return;
		}
		try {
			AaptService$ErrorResult errorResult = get();
			// 判断aapt是否有错误
			if (errorResult.syntaxErrorsMap == null) {
				// 切换到主线程
				// e::vJ
				// 没有 syntaxErrorsMap 回调是有错误
				AaptService.j6(this.aaptService, errorResult.hasError);
			} else {
				// 有syntaxErrorsMap 回调传入syntaxErrorsMap
				AaptService.DW(this.aaptService, errorResult.syntaxErrorsMap);
			}
		}
		catch (InterruptedException unused) {
			// 中断 e::J0 aapt was interrupted
			AaptService.FH(this.aaptService);
		}
		catch (ExecutionException e) {
			// e::g3() 
			AaptService.Hw(this.aaptService, e.getCause());
		}
    }
}

