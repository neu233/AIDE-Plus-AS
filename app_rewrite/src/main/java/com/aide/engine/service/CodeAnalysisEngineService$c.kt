package com.aide.engine.service

import android.content.res.AssetManager
import android.os.Process
import com.aide.codemodel.api.abstraction.DebuggerResourceProvider
import com.aide.common.AppLog
import com.aide.engine.c
import io.github.zeroaicy.aide.ClassReader
import io.github.zeroaicy.util.Log
import java.io.InputStream


/**
 * @author: 罪慾
 * @github: https://github.com/neu233/
 * @mail: 3115093767@qq.com
 * @createTime: 2024/12/9
 */


class `CodeAnalysisEngineService$c`(private val codeAnalysisEngineService: CodeAnalysisEngineService)  : c {
    override fun MP(th: Throwable?) {
        AppLog.e(th)

        Log.e("CodeAnalysisEngineService", "CodeAnalysis", th)

        if (CodeAnalysisEngineService.getEngineListener(codeAnalysisEngineService) != null) {
            try {
                CodeAnalysisEngineService.getEngineListener(codeAnalysisEngineService).rJ()
            } catch (unused: Exception) {
                AppLog.e(th)
            }
        }
    }

    override fun oa() {
        AppLog.e("Engine process killed after OOM")
        if (CodeAnalysisEngineService.getEngineListener(codeAnalysisEngineService) != null) {
            try {
                CodeAnalysisEngineService.getEngineListener(codeAnalysisEngineService).oa()
            } catch (e: java.lang.Exception) {
                AppLog.e(e)
            }
        }
        Process.killProcess(Process.myPid())
        Process.killProcess(Process.myPid())
        System.exit(-1)
    }

    override fun qP() {
        AppLog.d("Engine process killed after shutdown")
        Process.killProcess(Process.myPid())
    }


}

class `CodeAnalysisEngineService$a`(private val codeAnalysisEngineService: CodeAnalysisEngineService) : DebuggerResourceProvider() {
    /**
     * 拦截调试器，动态修改调试器宿主包名
     */
    override fun getResourceInputStream(fileName: String): InputStream {
        try {
            val assets: AssetManager = codeAnalysisEngineService.assets
            var open = assets.open(fileName)

            if ("adrt/ADRT.class" == fileName) {
                open = ClassReader.modifyADRT(open)
            }
            return open
        } catch (e: java.lang.Exception) {
            AppLog.e("DebuggerResourceProvider", e)
            throw Error(e)
        }
    }

    /**
     * 调试器注入包名
     */
    override fun getHostPackageName(): String {
        return codeAnalysisEngineService.packageName
    }
}