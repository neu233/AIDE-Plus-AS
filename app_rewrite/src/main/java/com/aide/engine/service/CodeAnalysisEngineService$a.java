//
// Decompiled by Jadx - 850ms
//
package com.aide.engine.service;

import android.content.res.AssetManager;
import com.aide.common.AppLog;
import io.github.zeroaicy.aide.ClassReader;
import java.io.InputStream;

public class CodeAnalysisEngineService$a extends com.aide.codemodel.api.abstraction.DebuggerResourceProvider{
    final CodeAnalysisEngineService codeAnalysisEngineService;
	
	public CodeAnalysisEngineService$a(CodeAnalysisEngineService codeAnalysisEngineService){
        this.codeAnalysisEngineService = codeAnalysisEngineService;
    }
	
	/**
	 * 拦截调试器，动态修改调试器宿主包名
	 */
	@Override
    public InputStream getResourceInputStream(String fileName){
        try{
			AssetManager assets = this.codeAnalysisEngineService.getAssets();
			InputStream open = assets.open(fileName);

			if ( "adrt/ADRT.class".equals(fileName) ){
				open = ClassReader.modifyADRT(open);
			}
			return open;
		}
		catch (Exception e){
			AppLog.e("DebuggerResourceProvider", e);
			throw new Error(e);
		}
    }
	
	/**
	 * 调试器注入包名
	 */
	@Override
    public String getHostPackageName(){
		return this.codeAnalysisEngineService.getPackageName();
    }
}

