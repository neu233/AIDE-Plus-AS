//
// Decompiled by Jadx - 882ms
//
package com.aide.engine.service;

import android.os.Process;
import com.aide.common.AppLog;
import com.aide.engine.c;
import io.github.zeroaicy.util.Log;

// 🤔必须使用全名???
public class CodeAnalysisEngineService$c implements com.aide.engine.c{

    final CodeAnalysisEngineService j6;

    public CodeAnalysisEngineService$c(CodeAnalysisEngineService codeAnalysisEngineService){
        this.j6 = codeAnalysisEngineService;
    }

    public void MP(Throwable th){

		AppLog.e(th);

		Log.e("CodeAnalysisEngineService", "CodeAnalysis", th);

		if ( CodeAnalysisEngineService.getEngineListener(this.j6) != null ){
			try{
				CodeAnalysisEngineService.getEngineListener(this.j6).rJ();
			}
			catch (Exception unused){
				AppLog.e(th);
			}
		}
    }

    public void oa(){
		AppLog.e("Engine process killed after OOM");
		if ( CodeAnalysisEngineService.getEngineListener(this.j6) != null ){
			try{
				CodeAnalysisEngineService.getEngineListener(this.j6).oa();
			}
			catch (Exception e){
				AppLog.e(e);
			}
		}
		Process.killProcess(Process.myPid());
		Process.killProcess(Process.myPid());
		System.exit(-1);
    }

    public void qP(){
		AppLog.d("Engine process killed after shutdown");
		Process.killProcess(Process.myPid());
    }
}

