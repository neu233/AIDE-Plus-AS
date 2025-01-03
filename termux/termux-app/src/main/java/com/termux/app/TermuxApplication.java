package com.termux.app;

import android.app.Application;
import android.content.Context;

import com.termux.shared.crash.TermuxCrashUtils;
import com.termux.shared.logger.Logger;
import com.termux.shared.settings.preferences.TermuxAppSharedPreferences;
import io.github.zeroaicy.util.DebugUtil;


public class TermuxApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        DebugUtil.debug();
    }


    public void onCreate() {
        super.onCreate();
        // Set crash handler for the app
        TermuxCrashUtils.setCrashHandler(this);
        // Set log level for the app
        setLogLevel();
        DebugUtil.debug();
    }

    private void setLogLevel() {
        // Load the log level from shared preferences and set it to the {@link Logger.CURRENT_LOG_LEVEL}
        TermuxAppSharedPreferences preferences = TermuxAppSharedPreferences.build(getApplicationContext());
        if (preferences == null)
            return;
        preferences.setLogLevel(null, preferences.getLogLevel());
        Logger.logDebug("Starting Application");
    }
}

