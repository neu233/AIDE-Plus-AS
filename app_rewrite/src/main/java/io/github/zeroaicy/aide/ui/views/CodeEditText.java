package io.github.zeroaicy.aide.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import com.aide.common.AndroidHelper;
import com.aide.ui.views.editor.Color;
import com.probelytics.annotation.MethodMark;

public class CodeEditText extends com.aide.ui.views.CodeEditText {
	public CodeEditText(Context context) {
		this(context, null);
	}

	@MethodMark(method=-16008434376576729L)
	public CodeEditText(Context context, AttributeSet attributeSet) {
		this(context, attributeSet, 0);
	}

	@MethodMark(method=-658231255645821984L)
	public CodeEditText(Context context, AttributeSet attributeSet, int defStyleAttr) {
		super(context, attributeSet, defStyleAttr);
		init();
	}

	// 代替 sh()，并替换EditorView
	private void init() {
		removeAllViews();
		addView(new EditorView(getContext()));
	}
	
	
	public boolean isLightTheme(){
		return super.isLightTheme();
	}
	com.aide.ui.views.CodeEditText.EditorView 测试;
	
	public class EditorView extends com.aide.ui.views.CodeEditText.EditorView {
		CodeEditText codeEditText = CodeEditText.this;
		public EditorView(Context context) {
			super(context);

		}
		
		@Override
		public void initColors() {
			super.initColors();

			if (this.codeEditText == null) {
                return;
            }
            if (AndroidHelper.isMaterialTheme(getContext())) {
                this.selectionColor = new Color(getResources().getColor(this.codeEditText.isLightTheme() ? 0x7f050027 : 0x7f050026));
            } else {
                this.selectionColor = new Color(getResources().getColor(this.codeEditText.isLightTheme() ? 0x7f050025 : 0x7f050024));
            }
            this.graphicsColor = new Color(getResources().getColor(this.codeEditText.isLightTheme() ? 0x7f050017 : 0x7f050016));



			if (this.showCaretLine) {
                this.Za = new Color(getResources().getColor(this.codeEditText.isLightTheme() ? 0x7f05001b : 0x7f05001a));
            }else{
				this.Za = null;				
			}


			if (this.showCaretLine) {
				this.stepbarColor = new Color(getResources().getColor(this.codeEditText.isLightTheme() ? 0x7f05002b : 0x7f05002a));
            }else{
				this.stepbarColor = null;
			}

            this.Pa = new Color(getResources().getColor(this.codeEditText.isLightTheme() ? 0x7f050019 : 0x7f050018));
			
            this.separatorColor = new Color(getResources().getColor(this.codeEditText.isLightTheme() ? 0x7f050029 : 0x7f050028));
            this.hyperlinkColor = new Color(getResources().getColor(this.codeEditText.isLightTheme() ? 0x7f050021 : 0x7f050020));
			
            this.Bx = new Color(getResources().getColor(this.codeEditText.isLightTheme() ? 0x7f05001f : 0x7f05001e));
            this.Jm = new Color(getResources().getColor(this.codeEditText.isLightTheme() ? 0x7f05001d : 0x7f05001c));
            this.An = new Color(getResources().getColor(this.codeEditText.isLightTheme() ? 0x7f050023 : 0x7f050022));
		}

		protected boolean showCaretLine = true;
		
		@Override
		public void setShowCaretLine(boolean showCaretLine) {
			super.setShowCaretLine(this.showCaretLine = showCaretLine);
		}

	}
}
