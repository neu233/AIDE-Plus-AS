/**
 * @Author ZeroAicy
 * @AIDE AIDE+
*/
package com.aide.ui;

public class AIDEEditorExtend{
	
	public static AIDEEditor getCurrentEditor(AIDEEditorPager aideEditorPager){
		return AIDEEditorPager.lp(aideEditorPager);
	}
	public static AIDEEditor.t getEditorModel(AIDEEditor aideEditor){
		return AIDEEditor.jJ(aideEditor);
	}
}
