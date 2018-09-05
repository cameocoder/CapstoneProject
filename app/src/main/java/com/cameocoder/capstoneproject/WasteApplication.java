package com.cameocoder.capstoneproject;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import com.squareup.leakcanary.LeakCanary;

import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.O;
import static com.cameocoder.capstoneproject.NotificationService.NOTIFICATION_CHANNEL_ID;

public class WasteApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (SDK_INT >= O) {
            CharSequence name = getString(R.string.notification_channel_name);
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, IMPORTANCE_HIGH);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
