package com.taymoor.alarmapp.services.charging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.taymoor.alarmapp.R;
import com.taymoor.alarmapp.helpers.ChargingReceiver;
import com.taymoor.alarmapp.interfaces.IChargingListener;
import com.taymoor.alarmapp.utils.Constants;

public class ChargingService extends Service implements IChargingListener {

    private ChargingReceiver chargingReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(chargingReceiver);
    }

    private void startChargingRemovalDetection() {

        chargingReceiver = ChargingReceiver.getInstance();

        chargingReceiver.addListener(this);

        registerReceiver(chargingReceiver, chargingReceiver.getIntentFilter());
    }

    private void startService() {

        String channelId = "charging_notification_channel";

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
                .setContentTitle("Charging Service")
                .setContentText("Charging removal detection is running")
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
                        "Charging Service",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("Charging service notification channel");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        startChargingRemovalDetection();
        startForeground(Constants.CHARGING_SERVICE_ID, builder.build());
    }

    private void stopService() {

        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {

            String action = intent.getAction();

            if (action != null) {

                if (action.equals(Constants.ACTION_START_CHARGING_SERVICE)) {

                    startService();

                } else if (action.equals(Constants.ACTION_STOP_CHARGING_SERVICE)) {

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
    public void onChargingRemoved() {

        if (ChargingServiceUtils.isServiceRunning(this)) {

            ChargingAlarmServiceUtils.startService(this);
        }
    }
}