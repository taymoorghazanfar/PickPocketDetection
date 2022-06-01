package com.taymoor.alarmapp.services.pocket;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.taymoor.alarmapp.R;
import com.taymoor.alarmapp.activities.MainActivity;
import com.taymoor.alarmapp.utils.Constants;

public class PocketAlarmService extends Service {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    public void onCreate() {
        super.onCreate();

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);

        mediaPlayer = MediaPlayer.create(this, R.raw.alert);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(100 * .01f, 100 * .01f);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startService() {

        String channelId = "pocket_alarm_notification_channel";

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
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
                .setContentTitle("Pocket Picked")
                .setContentText("Pocket pick is detected")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {

                NotificationChannel notificationChannel = new NotificationChannel(

                        channelId,
                        "Pocket Alarm Service",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("Pocket alarm service notification channel");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        mediaPlayer.start();
        long[] pattern = {0, 100, 100};
        vibrator.vibrate(pattern, 0);

        startForeground(Constants.POCKET_ALARM_SERVICE_ID, builder.build());
    }

    private void stopService() {

        mediaPlayer.stop();
        mediaPlayer.release();

        vibrator.cancel();

        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {

            String action = intent.getAction();

            if (action != null) {

                if (action.equals(Constants.ACTION_START_POCKET_ALARM_SERVICE)) {

                    startService();

                } else if (action.equals(Constants.ACTION_STOP_POCKET_ALARM_SERVICE)) {

                    stopService();
                }
            }
        }

        return START_STICKY;
    }
}
