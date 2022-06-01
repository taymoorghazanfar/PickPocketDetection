package com.taymoor.alarmapp.services.pocket;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.taymoor.alarmapp.R;
import com.taymoor.alarmapp.helpers.PocketManager;
import com.taymoor.alarmapp.interfaces.IPocketListener;
import com.taymoor.alarmapp.utils.Constants;

public class PocketService extends Service implements IPocketListener {

    private PocketManager pocketManager;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startPocketPickDetection() {

        pocketManager = PocketManager.getInstance(this);
        pocketManager.addListener(this);
        pocketManager.start();
    }

    private void startService() {

        String channelId = "pocket_notification_channel";

        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(

                getApplicationContext(),
                channelId
        );

        builder.setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Pocket Service")
                .setContentText("Pocket pick detection is running")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(false)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {

                NotificationChannel notificationChannel = new NotificationChannel(

                        channelId,
                        "Pocket Service",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("Pocket service notification channel");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        startPocketPickDetection();
        startForeground(Constants.POCKET_SERVICE_ID, builder.build());
    }

    private void stopService() {

        if (pocketManager != null) {

            pocketManager.stop();
        }
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {

            String action = intent.getAction();

            if (action != null) {

                if (action.equals(Constants.ACTION_START_POCKET_SERVICE)) {

                    startService();

                } else if (action.equals(Constants.ACTION_STOP_POCKET_SERVICE)) {

                    stopService();
                }
            }
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPocketPicked() {

        if (PocketServiceUtils.isServiceRunning(this)) {
            Log.d("pocket", "onPocketPicked: starting alarm");
            PocketAlarmServiceUtils.startService(this);
        }
    }
}