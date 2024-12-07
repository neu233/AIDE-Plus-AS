package io.github.zeroaicy.aide.utils;

import android.content.Context;
import android.content.Intent;
import io.github.zeroaicy.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Logger implements Runnable {
	public static Logger sLogger = new Logger();

	private DefaultLogListener defaultLogListenere;


	public static synchronized void setLogger(Logger sLogger) {
		Logger.sLogger = sLogger;
	}

	public static Logger getLogger() {
		return sLogger;
	}

	//public String hostPackageName;

	public Logger() {}
	
	public class DefaultLogListener implements LogListener {
		@Override
		public void log(String logLine) {
			Log.println(logLine);
		}
	}
	
	public static class SendLog implements LogListener {
		List<String> logs = new ArrayList<>();
		String[] empty = new String[0];
		@Override
		public void log(String logLine) {
			logs.add(logLine);
			if (logs.size() > 50) {
				sendLogcatLines(logs.toArray(empty));
				logs.clear();
			}
		}
	}
	private Thread sThread;

	public void start() {
		if (sThread != null) return;
		sThread = new Thread(this);
		sThread.start();
	}
	
	
	public void addDefaultLogListener(){
		if(this.defaultLogListenere != null){
			return;
		}
		this.defaultLogListenere = new DefaultLogListener();
		addLogListener(this.defaultLogListenere);
	}
	
	public void removeDefaultLogListener(){
		if(this.defaultLogListenere == null){
			return;
		}
		removeLogListener(this.defaultLogListenere);
		this.defaultLogListenere = null;
	}
	
	public interface LogListener {
		public void log(String logLine);
	}

	private final List<LogListener> mLogListeners = new ArrayList<>();

	public void addLogListener(LogListener logListener) {
		synchronized(this.mLogListeners){
			if (logListener == null
				|| mLogListeners.contains(logListener)) {
				return;
			}
			mLogListeners.add(logListener);
		}
		
	}
	public void removeLogListener(LogListener logListener) {
		synchronized(this.mLogListeners){
			mLogListeners.remove(logListener);
		}
	}

	@Override
    public void run() {
        try {
			Runtime.getRuntime().exec("logcat -c");
            Process exec = Runtime.getRuntime().exec("logcat -v threadtime");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()), 20);

			while (true) {

				String logLine = bufferedReader.readLine();
				if (logLine == null) {
					continue;
				}
				// 向监听器写入
				for (int i = mLogListeners.size() - 1; i >= 0; i--) {
					LogListener logListener  = mLogListeners.get(i);
					try {
						logListener.log(logLine);
					}
					catch (Throwable e) {
						// 有问题的监听器要移除
						// 或者当监听器需要移除自身
						// 就可以抛出异常
						mLogListeners.remove(i);
					}
				}
            }
        }
		catch (IOException e) {
			e.printStackTrace();
        }
    }
	private static Context context;
	private static String debuggerPackageName;

	private static boolean isSendLogcat;

	public static void onContext(Context context, String debuggerPackageName) {
		if( debuggerPackageName == null ){
			return;
		}
		if( Logger.debuggerPackageName != null ){
			return;
		}
		Logger.context = context;
		Logger.debuggerPackageName = debuggerPackageName;
		Logger.isSendLogcat = true;
		getLogger().addLogListener(new SendLog());
	}

	public static void sendConnect(String str) {
		Intent intent = new Intent();
		intent.setPackage(debuggerPackageName);
		intent.setAction("com.adrt.CONNECT");
		intent.putExtra("package", str);
		context.sendBroadcast(intent);
	}

	public static void sendStop(String str) {
		Intent intent = new Intent();
		intent.setPackage(debuggerPackageName);
		intent.setAction("com.adrt.STOP");
		intent.putExtra("package", str);
		context.sendBroadcast(intent);
	}

	public static void sendBreakpointHit(String str, ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<String> arrayList3, ArrayList<String> arrayList4, ArrayList<String> arrayList5, ArrayList<String> arrayList6) {
		Intent intent = new Intent();
		intent.setPackage(debuggerPackageName);
		intent.setAction("com.adrt.BREAKPOINT_HIT");
		intent.putExtra("package", str);
		intent.putExtra("variables", arrayList);
		intent.putExtra("variableValues", arrayList2);
		intent.putExtra("variableKinds", arrayList3);
		intent.putExtra("stackMethods", arrayList4);
		intent.putExtra("stackLocations", arrayList5);
		intent.putExtra("stackLocationKinds", arrayList6);
		context.sendBroadcast(intent);
	}

	public static void sendFields(String str, String str2, ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<String> arrayList3) {
		Intent intent = new Intent();
		intent.setPackage(debuggerPackageName);
		intent.setAction("com.adrt.FIELDS");
		intent.putExtra("package", str);
		intent.putExtra("path", str2);
		intent.putExtra("fields", arrayList);
		intent.putExtra("fieldValues", arrayList2);
		intent.putExtra("fieldKinds", arrayList3);
		context.sendBroadcast(intent);
	}

	public static void sendLogcatLines(String[] logs) {
		Intent intent = new Intent();
		intent.setPackage(debuggerPackageName);
		intent.setAction("com.adrt.LOGCAT_ENTRIES");
		intent.putExtra("lines", logs);
		context.sendBroadcast(intent);
	}

}
