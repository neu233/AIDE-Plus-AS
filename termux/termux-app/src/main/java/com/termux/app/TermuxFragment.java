package com.termux.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.WindowManager;

import com.termux.app.settings.properties.TermuxAppSharedProperties;
import com.termux.shared.shell.TermuxSession;
import com.termux.shared.terminal.TermuxTerminalSessionClientBase;
import com.termux.shared.terminal.TermuxTerminalViewClientBase;
import com.termux.shared.termux.TermuxConstants.TERMUX_APP.TERMUX_ACTIVITY;
import com.termux.shared.termux.TermuxUtils;
import com.termux.terminal.TerminalSession;
import com.termux.view.TerminalView;

import java.util.List;


public class TermuxFragment implements ServiceConnection {
    final Activity activity;
    /**
     * The connection to the {@link TermuxService}. Requested in {@link #onCreate(Bundle)} with a call to
     * {@link #bindService(Intent, ServiceConnection, int)}, and obtained and stored in
     * {@link #onServiceConnected(ComponentName, IBinder)}.
     */
    TermuxService mTermuxService;

    /**
     * The {@link TerminalView} shown in  {@link TermuxActivity} that displays the terminal.
     */
    TerminalView mTerminalView;

    /**
     * The {@link TerminalViewClient} interface implementation to allow for communication between
     * {@link TerminalView} and {@link TermuxActivity}.
     */
    TermuxTerminalViewClientBase mTermuxTerminalViewClient;

    /**
     * The {@link TerminalSessionClient} interface implementation to allow for communication between
     * {@link TerminalSession} and {@link TermuxActivity}.
     */
    TermuxTerminalSessionClientBase mTermuxTerminalSessionClient;
    /**
     * Termux app shared properties manager, loaded from termux.properties
     */
    private TermuxAppSharedProperties mProperties;

    private boolean mIsVisible = true;

    public TermuxFragment(Activity activity) {
        this.activity = activity;
        // Load termux shared properties
        mProperties = new TermuxAppSharedProperties(activity);

        mTerminalView = new TerminalView(activity, null);
//		mTermuxTerminalSessionClient = new TermuxTerminalSessionClientBase();
        mTermuxTerminalViewClient = new TermuxTerminalViewClientBase();
        mTerminalView.setTerminalViewClient(mTermuxTerminalViewClient);

        mTerminalView.postDelayed(new Runnable() {
            @Override
            public void run() {
                method(TermuxFragment.this.activity);
            }
        }, 5000);

    }

    private void method(Activity activity) throws RuntimeException {
        // Start the {@link TermuxService} and make it run regardless of who is bound to it
        Intent serviceIntent = new Intent(activity, TermuxService.class);
        activity.startService(serviceIntent);
        System.out.println(serviceIntent);
        System.out.println(activity);
        onServiceConnected(null, null);
        // Attempt to bind to the service, this will call the {@link #onServiceConnected(ComponentName, IBinder)}
        // callback if it succeeds.
		/*if (!activity.bindService(serviceIntent, this, 0)) {
			throw new RuntimeException("bindService() failed");
		}*/

        // Send the {@link TermuxConstants#BROADCAST_TERMUX_OPENED} broadcast to notify apps that Termux
        // app has been opened.
        TermuxUtils.sendTermuxOpenedBroadcast(activity);
    }

    public TerminalView getTerminalView() {
        return mTerminalView;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        if (service != null) {
            mTermuxService = ((TermuxService.LocalBinder) service).service;
        } else {
            mTermuxService = TermuxService.termuxService;
        }

        if (mTermuxService.isTermuxSessionsEmpty()) {
            if (mIsVisible) {
                TermuxInstaller.setupBootstrapIfNeeded(activity, new Runnable() {
                    // Activity might have been destroyed.
                    // Activity finished - ignore.
                    @Override
                    public void run() {
                        // Activity might have been destroyed.
                        if (mTermuxService == null)
                            return;
                        try {
                            Bundle bundle = activity.getIntent().getExtras();
                            boolean launchFailsafe = false;
                            if (bundle != null) {
                                launchFailsafe = bundle.getBoolean(TERMUX_ACTIVITY.EXTRA_FAILSAFE_SESSION, false);
                            }
                            addNewSession(launchFailsafe, null);
                        } catch (WindowManager.BadTokenException e) {
                        }
                    }

                });
            }
        } else {
            //绑定TermuxSession
            setCurrentSession(getCurrentSession());
        }
    }

    private TerminalSession getCurrentSession() {
        List<TermuxSession> termuxSessions = this.mTermuxService.getTermuxSessions();
        if (termuxSessions.isEmpty()) {
            return null;
        }
        return termuxSessions.get(0).getTerminalSession();
    }

    private void setCurrentSession(TerminalSession newTerminalSession) {
        mTerminalView.attachSession(newTerminalSession);
    }

    private void addNewSession(boolean isFailSafe, String sessionName) {
        TerminalSession currentSession = getCurrentSession();
        String workingDirectory;
        if (currentSession == null) {
            workingDirectory = mProperties.getDefaultWorkingDirectory();
        } else {
            workingDirectory = currentSession.getCwd();
        }
        TermuxSession newTermuxSession = mTermuxService.createTermuxSession(null, null, null, workingDirectory, isFailSafe, sessionName);
        if (newTermuxSession == null)
            return;
        TerminalSession newTerminalSession = newTermuxSession.getTerminalSession();
        setCurrentSession(newTerminalSession);

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

}
