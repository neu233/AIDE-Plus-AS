package com.termux.shared.terminal.io;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import com.termux.shared.terminal.io.extrakeys.ExtraKeyButton;
import com.termux.shared.terminal.io.extrakeys.ExtraKeysView;
import com.termux.shared.terminal.io.extrakeys.SpecialButton;
import com.termux.view.TerminalView;
import static com.termux.shared.terminal.io.extrakeys.ExtraKeysConstants.PRIMARY_KEY_CODES_FOR_STRINGS;
import java.util.function.IntConsumer;

public class TerminalExtraKeys implements ExtraKeysView.IExtraKeysView {

    private final TerminalView mTerminalView;

    public TerminalExtraKeys(@NonNull TerminalView terminalView) {
        mTerminalView = terminalView;
    }

    @Override
    public void onExtraKeyButtonClick(View view, ExtraKeyButton buttonInfo, Button button) {
        if (buttonInfo.isMacro()) {
            String[] keys = buttonInfo.getKey().split(" ");
            boolean ctrlDown = false;
            boolean altDown = false;
            boolean shiftDown = false;
            boolean fnDown = false;
            for (String key : keys) {
                if (SpecialButton.CTRL.getKey().equals(key)) {
                    ctrlDown = true;
                } else if (SpecialButton.ALT.getKey().equals(key)) {
                    altDown = true;
                } else if (SpecialButton.SHIFT.getKey().equals(key)) {
                    shiftDown = true;
                } else if (SpecialButton.FN.getKey().equals(key)) {
                    fnDown = true;
                } else {
                    onTerminalExtraKeyButtonClick(view, key, ctrlDown, altDown, shiftDown, fnDown);
                    ctrlDown = false;
                    altDown = false;
                    shiftDown = false;
                    fnDown = false;
                }
            }
        } else {
            onTerminalExtraKeyButtonClick(view, buttonInfo.getKey(), false, false, false, false);
        }
    }

    protected void onTerminalExtraKeyButtonClick(View view, String key, final boolean ctrlDown, final boolean altDown, boolean shiftDown, boolean fnDown) {
        if (PRIMARY_KEY_CODES_FOR_STRINGS.containsKey(key)) {
            Integer keyCode = PRIMARY_KEY_CODES_FOR_STRINGS.get(key);
            if (keyCode == null)
                return;
            int metaState = 0;
            if (ctrlDown)
                metaState |= KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON;
            if (altDown)
                metaState |= KeyEvent.META_ALT_ON | KeyEvent.META_ALT_LEFT_ON;
            if (shiftDown)
                metaState |= KeyEvent.META_SHIFT_ON | KeyEvent.META_SHIFT_LEFT_ON;
            if (fnDown)
                metaState |= KeyEvent.META_FUNCTION_ON;
            KeyEvent keyEvent = new KeyEvent(0, 0, KeyEvent.ACTION_UP, keyCode, 0, metaState);
            mTerminalView.onKeyDown(keyCode, keyEvent);
        } else {
            // not a control char
            key.codePoints().forEach(new IntConsumer() {

                @Override
                public void accept(int codePoint) {
                    mTerminalView.inputCodePoint(codePoint, ctrlDown, altDown);
                }
            });
        }
    }

    @Override
    public boolean performExtraKeyButtonHapticFeedback(View view, ExtraKeyButton buttonInfo, Button button) {
        return false;
    }
}

