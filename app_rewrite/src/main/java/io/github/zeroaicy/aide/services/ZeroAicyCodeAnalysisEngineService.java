package io.github.zeroaicy.aide.services;
import abcd.sy;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.aide.engine.service.CodeAnalysisEngineService;
import com.aide.ui.MainActivity;
import com.aide.ui.rewrite.R;
import io.github.zeroaicy.util.Log;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationManagerCompat;
import com.aide.common.AppLog;
import androidx.core.app.NotificationChannelCompat.Builder;
import com.aide.ui.ServiceContainer;

public class ZeroAicyCodeAnalysisEngineService extends CodeAnalysisEngineService {

	private static int id = 0x26f5;

	private static final String TAG = "ZeroAicyCodeAnalysisEngineService";

	private NotificationManager notificationManager;

	private Notification notification;


	//*
	@Override
	public void onCreate() {
		super.onCreate();
		// 初始化 App
		ServiceContainer.setContext(getApplicationContext());
		
		setNotificationAndForeground();

		AppLog.d(TAG, "onCreate");
	}


	@Override
	public IBinder onBind(Intent intent) {
		AppLog.d(TAG, "onBind");
		return super.onBind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		AppLog.d(TAG, "onUnbind");

		return super.onUnbind(intent);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (this.notificationManager == null) {
			this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		}
		if (this.notification != null) {
			this.notification = null;
			this.notificationManager.cancel(id);
		}
	}

	private void setNotificationAndForeground() {
		// String CHANNEL_ID = "engine";
		String CHANNEL_ID = "other";
		
		try {
			/*
			if (this.notificationChannel == null) {
				NotificationChannelCompat.Builder builder = new NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);

				this.notificationChannel = builder.build();
				NotificationManagerCompat from = NotificationManagerCompat.from(this);
				from.createNotificationChannel(this.notificationChannel);	
			}//*/

			if (this.notification == null) {
				PendingIntent pendingIntent = MainActivity.sy(this);
				NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
				builder.setWhen(System.currentTimeMillis());
				builder.setSmallIcon(android.R.drawable.stat_notify_more);
				builder.setContentTitle("Code Analysis");
				builder.setContentText("Code analysis engine is active");
				builder.setContentIntent(pendingIntent);
				builder.setPriority(-2);

				this.notification = builder.build();

				//startForeground服务前台化，要在5秒内调用成功，否则前台化失败
				startForeground(id, notification);

			}
		}
		catch (Throwable e) {
			AppLog.e(TAG, e);
		}
	}

}
