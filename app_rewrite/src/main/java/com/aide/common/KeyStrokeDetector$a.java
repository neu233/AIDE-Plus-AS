

//
// Decompiled by Jadx - 982ms
//
package com.aide.common;

import android.os.Build;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import com.aide.common.KeyStrokeDetector;
import com.aide.ui.ServiceContainer;
import com.aide.ui.MainActivity;
import com.aide.ui.util.FileSpan;
import com.aide.ui.views.editor.OEditor;
import java.io.StringReader;
import com.aide.ui.views.editor.Model;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;



public class KeyStrokeDetector$a extends BaseInputConnection {
    public static final String TAG = "KeyStrokeDetector$a";
	/*
	 KeyStrokeEditText[EdittextView] 
	 || CodeEditText$EditorView[OEditor[OConsole[View]]]
	 || CompletionListView[CustomKeysListView[ListView]]
	 */
	//只有自己引用，可以改名(DW)
    final View editorTextView;

	// 自定义
	final OEditor oEditor;

    //只有自己引用，可以改名(FH)
    final KeyStrokeDetector keyStrokeDetector;

	//只有自己引用，可以改名(j6)
    final KeyStrokeDetector.KeyStrokeHandler KeyStrokeHandler;
	
	private final boolean isWatch = ZeroAicySetting.isWatch();
	
	public KeyStrokeDetector$a(KeyStrokeDetector keyStrokeDetector, View view, boolean fullEditor, KeyStrokeDetector.KeyStrokeHandler keyStrokeHandler, View editorTextView) {
        super(view, fullEditor);
        this.keyStrokeDetector = keyStrokeDetector;
        this.KeyStrokeHandler = keyStrokeHandler;
		//view2与view就是一个
        this.editorTextView = editorTextView;

		if (this.editorTextView instanceof OEditor) {
			oEditor = (OEditor)this.editorTextView;
		} else {
			oEditor = null;
		}
    }

	// 全选时，讯飞输入法在 getExtractedText 以及 extractedText.text
	// 不为null时调用此方法
	@Override
	public boolean setSelection(int start, int end) {
		if (start == 0 && end == this.extractedTextEnd) {
			// setSelection(0, getExtractedText().text.length)
			// 就是selectAll
			return performContextMenuAction(android.R.id.selectAll);
		}
		return super.setSelection(start, end);
	}
	/*修改*/
	@Override
	public ExtractedText getExtractedText(ExtractedTextRequest request, int i) {
		ExtractedText outText = new ExtractedText();
		if (extractTextInternal(request, outText)) {
			return outText;
		}
		return null;
	}
	/**
	 * 用于判断是否全选
	 */
	private int extractedTextEnd;
	private boolean extractTextInternal(ExtractedTextRequest request, ExtractedText outText) {
        if (outText == null) {
            return false;
        }
		outText.text = "1234";
		outText.startOffset = 0;
		// 如果返回当前所在行，适合Watch
		// 也能增强依赖getExtractedText输入法的兼容性
		
		// 感觉变卡了 仅在isWatch启用试试
		if (this.oEditor !=  null) {

			if (isWatch) {
				Model model = this.oEditor.getModel();
				// 光标所在行
				int caretLine = this.oEditor.getCaretLine();
				int nextLine = 0;
				// 修复 下一行只有一个\n的情况复制不了的问题
				if (caretLine != model.getLineCount()) {
					nextLine = 1;
				}
				this.extractedTextEnd = model.getColumnCount(caretLine) + nextLine;
				
				char[] readLineText = new char[extractedTextEnd];
				model.readLineText(caretLine, readLineText);
				if (nextLine == 1) {
					readLineText[readLineText.length - 1] = '\n';
				}
				outText.text = new String(readLineText);
			}else{
				this.extractedTextEnd = outText.text.length();
			}
			
			if (this.oEditor.getSelectionVisibility()) {
				outText.flags = ExtractedText.FLAG_SELECTING;
				outText.selectionStart = 0; 
				outText.selectionEnd = outText.text.length();
			}
		} else {
			//this.extractedTextEnd = 0;
			this.extractedTextEnd = outText.text.length();
			
		}

        return true;
    }

	@Override
	public boolean performContextMenuAction(int id) {
        switch (id) {
			case android.R.id.selectAll:
                MainActivity rN = ServiceContainer.getMainActivity();
                if (rN == null) return true;
                rN.delayedShowAnalyzingProgressDialog();
                FileSpan currentFileSpan = ServiceContainer.getMainActivity().getAIDEEditorPager().getCurrentFileSpan();
                ServiceContainer.getEngineService().QX(currentFileSpan.j6, currentFileSpan.DW, currentFileSpan.FH, currentFileSpan.Hw, currentFileSpan.v5);
                return true;

			case android.R.id.cut:
                if (this.oEditor != null) {
					this.oEditor.b();
                }
                return true;

			case android.R.id.copy:
                if (this.oEditor != null) {
					this.oEditor.copySelectedText();
					// 取消选择模式
                    this.oEditor.setSelectionVisibility(false);
				}
                return true;

				// 无效
			case android.R.id.paste:
                if (this.oEditor != null) {
                    this.oEditor.tj();
                }
				return true;
		}
		return super.performContextMenuAction(id);
	}

	/**
	 * commitText为"\n"
	 */
    private void DW(CharSequence commitText, boolean isSoftKeyboard, View view) {
		// getKeyCharacterMap
		KeyCharacterMap keyCharacterMap = KeyStrokeDetector.getKeyCharacterMap(this.keyStrokeDetector);
		if (keyCharacterMap == null) {
			// setKeyCharacterMap
			keyCharacterMap = KeyStrokeDetector.setKeyCharacterMap(this.keyStrokeDetector, KeyCharacterMap.load(0));
		}
		for (int i = 0; i < commitText.length(); i++) {
			char charAt = commitText.charAt(i);
			// 物理键盘
			if (!isSoftKeyboard) {
				// 物理键 Shift
				boolean isLeftShiftPhysical = KeyStrokeDetector.getLeftShiftPhysical(this.keyStrokeDetector);
				boolean isRightShiftPhysical = KeyStrokeDetector.getRightShiftPhysical(this.keyStrokeDetector);
				if (isLeftShiftPhysical
					|| isRightShiftPhysical) {
					//大写
					charAt = Character.toUpperCase(charAt);
				} else {
					// 小写
					charAt = Character.toLowerCase(charAt);
				}
			}
			KeyEvent[] events = keyCharacterMap.getEvents(new char[]{charAt});
			if (events != null) {
				for (KeyEvent keyEvent : events) {
					sendKeyEvent(keyEvent);
				}
			}
		}
    }

    private void j6(CharSequence text, boolean z, KeyStrokeDetector.KeyStrokeHandler bVar) {
        for (int index = 0; index < text.length(); index++) {
			char c = text.charAt(index);
			if (!z) {
				//是否是快捷键，否则小写
				if (KeyStrokeDetector.getLeftShiftPhysical(this.keyStrokeDetector) 
					|| KeyStrokeDetector.getRightShiftPhysical(this.keyStrokeDetector)) {
					c = Character.toUpperCase(c);
				} else {
					c = Character.toLowerCase(c);
				}
			}

			if (bVar != null) {
				bVar.j6(KeyStrokeDetector.VH(this.keyStrokeDetector, c));
			}
		}
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
		String commitText;
		if (text instanceof String) {
			commitText = (String)text;
		} else {
			commitText = text.toString();
		}

		LogD("commitText: ['" + commitText + "']");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			for (int index = 0; index < KeyStrokeDetector.DW(this.keyStrokeDetector); index++) {
				this.KeyStrokeHandler.j6(new KeyStroke(KeyEvent.KEYCODE_DEL, false, false, false));
			}
		} else if (KeyStrokeDetector.DW(this.keyStrokeDetector) > 0
				   && commitText.length() == 1 
				   && commitText.charAt(0) == ' ') {
			KeyStrokeDetector.FH(this.keyStrokeDetector, 0);
			return true;
		}

		KeyStrokeDetector.FH(this.keyStrokeDetector, 0);

		if ("\n".equals(commitText)) {
			//换行 Hw[isSoftKeyboard]
			boolean isSoftKeyboard = KeyStrokeDetector.isSoftKeyboard(this.keyStrokeDetector);
			DW(commitText, isSoftKeyboard, this.editorTextView);
		} else if (this.oEditor != null 
				   && commitText.indexOf('\n') >= 0) {
			// 多行模式

			// OEditor::tj()
			//  kf() == getSelectionVisibility
			// 删除已经选中的字符串
			if (this.oEditor.getSelectionVisibility()) {
				this.oEditor.getEditorModel().b1();
				this.oEditor.k4();
				this.oEditor.setSelectionVisibility(false);
			}

			// OEditor::pn()
			int newLineNumber = 0;
			for (int offset = 0; offset < commitText.length(); offset++) {
				if (commitText.charAt(offset) == '\n') {
					newLineNumber++;
				}
			}
			int caretLine = oEditor.getCaretLine();
			int endLineNumber = newLineNumber + caretLine;
			//粘贴
			oEditor.getEditorModel().ys(oEditor.getCaretColumn(), oEditor.getCaretLine(), oEditor.getInsertTabsAsSpaces(), oEditor.getTabSize(), new StringReader(commitText), this);

			//更新行信息
			oEditor.eN(caretLine, endLineNumber);

			return true;
		} else {
			// 输入内容非多行模式
			// 或不是 OEditor
			j6(commitText, KeyStrokeDetector.isSoftKeyboard(this.keyStrokeDetector), this.KeyStrokeHandler);
		}
		return true;
    }

	// del功能
    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
		LogD("deleteSurroundingText " + beforeLength + " " + afterLength);
		KeyStrokeDetector.FH(this.keyStrokeDetector, 0);
		for (int index = 0; index < beforeLength; index++) {
			this.KeyStrokeHandler.j6(new KeyStroke(KeyEvent.KEYCODE_DEL, false, false, false));
		}
		for (int index = 0; index < afterLength; index++) {
			this.KeyStrokeHandler.j6(new KeyStroke(KeyEvent.KEYCODE_FORWARD_DEL, false, false, false));
		}

		return super.deleteSurroundingText(beforeLength, afterLength);
    }
	/**
	 * 建议
	 */
    @Override
    public CharSequence getTextBeforeCursor(int length, int flags) {
		if (AndroidHelper.isInTelevisionMode(KeyStrokeDetector.getContext(this.keyStrokeDetector))) {
			return super.getTextBeforeCursor(length, flags);
		}
		return "";
    }

    @Override
    public boolean sendKeyEvent(KeyEvent keyEvent) {
		LogD("sendKeyEvent " + keyEvent.getKeyCode());
		KeyStrokeDetector.FH(this.keyStrokeDetector, 0);
		return super.sendKeyEvent(wrapUpKeyEvent(keyEvent));
    }
    private KeyEvent wrapUpKeyEvent(KeyEvent keyEvent) {
		return new KeyEvent(keyEvent.getDownTime(), keyEvent.getEventTime(), 
							keyEvent.getAction(), keyEvent.getKeyCode(), 
							keyEvent.getRepeatCount(), keyEvent.getMetaState(),
							keyEvent.getDeviceId(), keyEvent.getScanCode(), 
							keyEvent.getFlags() | KeyEvent.FLAG_KEEP_TOUCH_MODE | KeyEvent.FLAG_SOFT_KEYBOARD);
    }

    @Override
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
		// 实际KeyStrokeDetector->we[空方法]
		LogD("setComposingText '" + ((Object) text) + "'");

		for (int i2 = 0; i2 < KeyStrokeDetector.DW(this.keyStrokeDetector); i2++) {
			this.KeyStrokeHandler.j6(new KeyStroke('C', false, false, false));
		}
		KeyStrokeDetector.FH(this.keyStrokeDetector, text.length());

		j6(text, KeyStrokeDetector.isSoftKeyboard(this.keyStrokeDetector), this.KeyStrokeHandler);

		return true;
	}

	// 实际KeyStrokeDetector->we[空方法]
	public void LogD(String charSequence) {
		//KeyStrokeDetector.j6(keyStrokeDetector, charSequence);
		//Log.d(TAG, charSequence);
	}




}

