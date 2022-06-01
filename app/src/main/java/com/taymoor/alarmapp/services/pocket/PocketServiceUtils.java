package com.taymoor.alarmapp.services.pocket;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.taymoor.alarmapp.utils.Constants;

public class PocketServiceUtils {

    public static boolean isServiceRunning(Context context) {

        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager != null) {

            for (ActivityManager.RunningServiceInfo serviceInfo
                    : activityManager.getRunningServices(Integer.MAX_VALUE)) {

                if (PocketService.class.getName().equals(serviceInfo.service.getClassName())) {

                    if (serviceInfo.foreground) {

                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    public static void startService(Context context) {

        if (!isServiceRunning(context)) {

            Intent intent = new Intent(context.getApplicationContext(), PocketService.class);
            intent.setAction(Constants.ACTION_START_POCKET_SERVICE);
            context.startService(intent);
        }
    }

    public static void stopService(Context context) {

        if (isServiceRunning(context)) {

            Intent intent = new Intent(context.getApplicationContext(), PocketService.class);
            intent.setAction(Constants.ACTION_STOP_POCKET_SERVICE);
            context.startService(intent);
        }
    }
}
