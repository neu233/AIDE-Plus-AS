//
// Decompiled by Jadx - 755ms
//
package com.aide.common;

import android.content.Context;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

public class KeyStrokeDetector2 extends KeyStrokeDetector {


    private boolean rightAlt;

    private boolean EQ;

    private boolean leftShift;

    private boolean rightShift;

    private Context context;

    private boolean VH;

    private boolean rightCtrl;

    private boolean rightShiftPhysical;

    private boolean leftAlt;

    private int tp;

    private boolean leftShiftPhysical;

    private boolean leftCtrl;


    private KeyCharacterMap keyCharacterMap;


    public KeyStrokeDetector2(Context context) {
		super(context);

		this.context = context;
		boolean z = context.getResources().getConfiguration().keyboard == 1;
		this.EQ = z;
		
		we("new KeyStrokeDetector2() - isSoftKeyboard: " + this.EQ);

    }

    static int DW(KeyStrokeDetector2 KeyStrokeDetector2) {
        return KeyStrokeDetector2.tp;
    }

    static int FH(KeyStrokeDetector2 KeyStrokeDetector2, int i) {
        KeyStrokeDetector2.tp = i;
        return i;
    }

    static boolean Hw(KeyStrokeDetector2 KeyStrokeDetector2) {
        return KeyStrokeDetector2.EQ;
    }

    private void J0(String str, int i, KeyEvent keyEvent) {
		StringBuilder sb = new StringBuilder();
		sb.append(str);
		sb.append(" ");
		sb.append(i);
		sb.append("  ");
		sb.append(keyEvent.getFlags());
		sb.append(keyEvent.isAltPressed() ? " alt" : "");
		sb.append(keyEvent.isShiftPressed() ? " shift" : "");
		sb.append(" ");
		sb.append(isLeftCtrlPressed(keyEvent.getMetaState()) ? " ctrl" : "");
		we(sb.toString());
    }

    private void J8(KeyStroke keyStroke) {
		we("onKeyStroke " + keyStroke.toString());
	}

    private KeyStroke Mr(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_UNKNOWN 
			|| keyCode == KeyEvent.KEYCODE_HOME 
			|| keyCode == KeyEvent.KEYCODE_BACK 
			|| keyCode == KeyEvent.KEYCODE_CTRL_LEFT 
			|| keyCode == KeyEvent.KEYCODE_CTRL_RIGHT) {
			return null;
		}
		switch (keyCode) {
			case KeyEvent.KEYCODE_ALT_LEFT:
			case KeyEvent.KEYCODE_ALT_RIGHT:
			case KeyEvent.KEYCODE_SHIFT_LEFT:
			case KeyEvent.KEYCODE_SHIFT_RIGHT:
				return null;

			default:
				boolean isShiftPressed = this.leftShift | this.rightShift | keyEvent.isShiftPressed();

				boolean isCtrlPressed = this.leftCtrl | this.rightCtrl | isLeftCtrlPressed(keyEvent.getMetaState());

				boolean isAltPressed = this.leftAlt | this.rightAlt | keyEvent.isAltPressed();

				int unicodeChar = keyEvent.getUnicodeChar();

				char unicodeChar2 = (unicodeChar == 0 || Character.isISOControl(unicodeChar))
					? (char) 65535 : (char) unicodeChar;
				return new KeyStroke(keyCode, unicodeChar2, isShiftPressed, isCtrlPressed, isAltPressed);
		}
    }

    private void QX(int keyCode, boolean isSoftKeyBoard) {
        we("onMetaKeysUp " + keyCode);

        this.leftAlt &= keyCode != KeyEvent.KEYCODE_ALT_LEFT; /*57*/
        this.rightAlt &= keyCode != KeyEvent.KEYCODE_ALT_RIGHT; /*58*/

        this.leftShift &= keyCode != KeyEvent.KEYCODE_SHIFT_LEFT; /*59*/
        this.rightShift &= keyCode != KeyEvent.KEYCODE_SHIFT_RIGHT; /*60*/

		// this.leftShiftPhysical &= !(keyCode == KeyEvent.KEYCODE_SHIFT_LEFT && !isSoftKeyBoard);
		// 再次按下物理leftShift键，取消leftShift状态
        this.leftShiftPhysical &= (keyCode != KeyEvent.KEYCODE_SHIFT_LEFT || isSoftKeyBoard);

        this.rightShiftPhysical &= (keyCode != KeyEvent.KEYCODE_SHIFT_RIGHT || isSoftKeyBoard);

        this.VH &= keyCode != KeyEvent.KEYCODE_UNKNOWN;

        this.leftCtrl &= keyCode != KeyEvent.KEYCODE_CTRL_LEFT;
        this.rightCtrl &= keyCode != KeyEvent.KEYCODE_CTRL_RIGHT;
    }

    static KeyStroke VH(KeyStrokeDetector2 KeyStrokeDetector2, char c) {
        return KeyStrokeDetector2.j3(c);
    }


    private void onMetaKeysDown(int keyCode, boolean isSoftKeyBoard) {
		we("onMetaKeysDown " + keyCode);

		this.leftAlt |= keyCode == KeyEvent.KEYCODE_ALT_LEFT;
		this.rightAlt |= keyCode == KeyEvent.KEYCODE_ALT_RIGHT;

		this.leftShift |= keyCode == KeyEvent.KEYCODE_SHIFT_LEFT;
		this.rightShift |= keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT;

		// 物理键盘 leftShift
		this.leftShiftPhysical |= (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT && !isSoftKeyBoard);
		this.rightShiftPhysical |= keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT && !isSoftKeyBoard;

		this.VH |= keyCode == KeyEvent.KEYCODE_UNKNOWN;

		this.leftCtrl |= keyCode == KeyEvent.KEYCODE_CTRL_LEFT;

		this.rightCtrl |= keyCode == KeyEvent.KEYCODE_CTRL_RIGHT;

    }

	/**
	 * 通过 KeyEvent::getMetaState判断 LEFT CTRL 
	 */
    private boolean isLeftCtrlPressed(int i) {
		// 此掩码用于检查是否按下了CTRL元键之一
		// 此掩码用于检查是否按下了左CTRL元键。
		return (i & (KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON)) != 0;
    }

    static boolean Zo(KeyStrokeDetector2 KeyStrokeDetector2) {
        return KeyStrokeDetector2.rightShiftPhysical;
    }

    static KeyCharacterMap gn(KeyStrokeDetector2 KeyStrokeDetector2) {
        return KeyStrokeDetector2.keyCharacterMap;
    }

    private KeyStroke j3(char unicodeChar) {
		return new KeyStroke(-1, unicodeChar, this.leftShiftPhysical | this.rightShiftPhysical | Character.isUpperCase(unicodeChar), false, false);
    }

    static void j6(KeyStrokeDetector2 KeyStrokeDetector2, String str) {
        KeyStrokeDetector2.we(str);
    }

	// getContext
    static Context tp(KeyStrokeDetector2 KeyStrokeDetector2) {
        return KeyStrokeDetector2.context;
    }

	// setKeyCharacterMap
    static KeyCharacterMap u7(KeyStrokeDetector2 KeyStrokeDetector2, KeyCharacterMap keyCharacterMap) {
        KeyStrokeDetector2.keyCharacterMap = keyCharacterMap;
        return keyCharacterMap;
    }

    static boolean v5(KeyStrokeDetector2 KeyStrokeDetector2) {
        return KeyStrokeDetector2.leftShiftPhysical;
    }

	/**
	 * log
	 */
    private void we(String log) {

    }

    public InputConnection EQ(View view, KeyStrokeHandler keyStrokeHandler) {
		return null; //new KeyStrokeDetector$a(this, view, true, keyStrokeHandler, view);
    }

    public void U2() {
		this.tp = 0;
    }

    public void a8(int keyCode, KeyEvent keyEvent) {
		onMetaKeysDown(keyCode, (keyEvent.getFlags() & KeyEvent.FLAG_SOFT_KEYBOARD) != 0);
    }

	/**
	 * isCtrl
	 */
    public boolean aM() {
		return this.leftCtrl || this.rightCtrl;
    }

	// onKeyDown
    public boolean er(int keyCode, KeyEvent keyEvent, KeyStrokeHandler keyStrokeHandler) {
		J0("onKeyDown", keyCode, keyEvent);
		int keyCode2 = keyCode == KeyEvent.KEYCODE_SEARCH ? KeyEvent.KEYCODE_ALT_LEFT : keyCode;

		onMetaKeysDown(keyCode2, (keyEvent.getFlags() & KeyEvent.FLAG_SOFT_KEYBOARD) != 0);

		KeyStroke keyStroke = Mr(keyCode2, keyEvent);

		if (keyStroke == null 
			|| !keyStrokeHandler.j6(keyStroke)) {
			//
			return keyCode == KeyEvent.KEYCODE_SEARCH;
		}
		// 打印 log
		J8(keyStroke);

		return true;
	}

    public void lg(int keyCode, KeyEvent keyEvent) {
        QX(keyCode, (keyEvent.getFlags() & KeyEvent.FLAG_SOFT_KEYBOARD) != 0);

    }

	// onConfigChange
    public void rN(Context context) {
		boolean z = context.getResources().getConfiguration().keyboard == 1;
		this.EQ = z;
		we("KeyStrokeDetector2.onConfigChange() - isSoftKeyboard: " + this.EQ);
		this.keyCharacterMap = null;
    }

	// onKeyUp
    public boolean yS(int keyCode, KeyEvent keyEvent, KeyStrokeHandler keyStrokeHandler) {
		J0("onKeyUp", keyCode, keyEvent);
		int keyCode2 = keyCode == KeyEvent.KEYCODE_SEARCH ? KeyEvent.KEYCODE_ALT_LEFT : keyCode;
		QX(keyCode2, (keyEvent.getFlags() & KeyEvent.FLAG_SOFT_KEYBOARD) != 0);

		return keyCode == KeyEvent.KEYCODE_SEARCH;

    }

	public interface KeyStrokeHandler extends KeyStrokeDetector.KeyStrokeHandler {
		public boolean j6(KeyStroke keyStroke);
	}

}

