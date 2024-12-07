//
// Decompiled by Jadx - 661ms
//
package com.aide.ui;

import abcd.th;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.aide.ui.PromoNotificationAlarmReceiver;
import com.aide.ui.firebase.FireBaseLogEvent;
import com.google.android.gms.internal.ads.iy;
import com.probelytics.annotation.ExceptionEnabled;
import com.probelytics.annotation.MethodMark;
import com.probelytics.annotation.ParametersEnabled;
import com.probelytics.annotation.TypeMark;
import com.probelytics.Probelytics;

@TypeMark(clazz = 1533015674575090552L, container = 1533015674575090552L, user = true)
public class PromoNotificationAlarmReceiver extends BroadcastReceiver {

    @ExceptionEnabled
    private static boolean DW;

    @ParametersEnabled
    private static boolean j6;

    @MethodMark(method = -1512152669004809752L)
    public PromoNotificationAlarmReceiver() {
        
    }

    @MethodMark(method = -3645032137445328080L)
    public static void DW(Context context) {
        try {
            context = context.getApplicationContext();
            int flags = 0x08000000;
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
				flags |= PendingIntent.FLAG_IMMUTABLE;
			}
			
			((AlarmManager) context.getSystemService("alarm")).cancel(PendingIntent.getBroadcast(context, 0, new Intent(context, (Class<?>) PromoNotificationAlarmReceiver.class), flags));
        } catch (Throwable th) {
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }
	//T argeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE or FLAG_MUTABLE be specified when creating a PendingIntent.
	// Strongly consider using FLAG_IMMUTABLE, only use FLAG_MUTABLE if some functionality depends on the PendingIntent being mutable, e.g.
	// if it needs to be used with inline replies or bubbles.
	//
    @MethodMark(method = -938132574093077385L)
    public static void FH(Context context, long j, int i, String str, String str2, String str3) {
        Context context2;
        try {
            context2 = context.getApplicationContext();
            try {
                Intent intent = new Intent(context2, (Class<?>) PromoNotificationAlarmReceiver.class);
                intent.putExtra("EXTRA_MSG", str);
                intent.putExtra("EXTRA_TEXT", str3);
                intent.putExtra("EXTRA_TITLE", str2);
                intent.putExtra("EXTRA_ICON", i);
                int flags = 0x08000000;
				if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
					flags |= PendingIntent.FLAG_IMMUTABLE;
				}
				
				((AlarmManager) context2.getSystemService("alarm")).set(1, j, PendingIntent.getBroadcast(context2, 0, intent, flags));
            } catch (Throwable th) {
                if(th instanceof Error) throw (Error)th; else throw new Error(th);
            }
        } catch (Throwable th2) {
            context2 = context;
        }
    }

    @MethodMark(method = 250640105624016960L)
    private void Hw(Context context, int i, String str, String str2, String str3) {
        try {
            FireBaseLogEvent.QX(str3);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            long currentTimeMillis = System.currentTimeMillis();
            PendingIntent aj = MainActivity.aj(context);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "other");
            builder.setWhen(currentTimeMillis);
            builder.setTicker(str);
            builder.setSmallIcon(i);
            builder.setContentTitle(str2);
            builder.setContentText(str3);
            builder.setContentIntent(aj);
            builder.setAutoCancel(true);
            builder.setDefaults(1);
            j6(notificationManager, 0x27078e78, builder.build());
        } catch (Throwable th) {
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    private static void j6(NotificationManager notificationManager, int i, Notification notification) {
        notificationManager.notify(i, notification);
        Probelytics.gW(notificationManager, i, notification);
    }

    @Override
    @MethodMark(method = -1159758616834058325L)
    public void onReceive(Context context, Intent intent) {
        try {
            Hw(context, intent.getIntExtra("EXTRA_ICON", 0), intent.getStringExtra("EXTRA_MSG"), intent.getStringExtra("EXTRA_TITLE"), intent.getStringExtra("EXTRA_TEXT"));
        } catch (Throwable th) {
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }
}

