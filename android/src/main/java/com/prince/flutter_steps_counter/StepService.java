package com.prince.flutter_steps_counter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class StepService extends Service {
    private NotificationManager notificationManager;
    private NotificationChannel notificationChannel;

    private String CHANNEL_ID = "STEPS_CHANNEL";
    private String CHANNEL_NAME = "STEP_NOTIFICATION_CHANNEL";
    private static int NOTIFICATION_ID = 83834834;

    private String RESOURCE_ICON_NAME = "app_icon";
    private String RESOURCE_TYPE = "drawable";

    int resourceId = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        resourceId = this.getResources().getIdentifier(RESOURCE_ICON_NAME, RESOURCE_TYPE, this.getPackageName());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME;
            String description = "";
            int importance = NotificationManager.IMPORTANCE_LOW;
            notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            notificationChannel.setSound(null, null);
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private static Class getMainActivityClass(Context context) {
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        String className = launchIntent.getComponent().getClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createNotification(String title, String content) {
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(resourceId)
                .setContentTitle(content)
                .setContentText(title)
                .setStyle(new NotificationCompat.BigTextStyle().setSummaryText(content))
                .setSound(null)
                .setVibrate(new long[]{0})
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(this, getMainActivityClass(this));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(getMainActivityClass(this));
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);

        startForeground(NOTIFICATION_ID, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        createNotification(title, content);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
