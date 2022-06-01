package com.taymoor.alarmapp.services.charging;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.taymoor.alarmapp.utils.Constants;

public class ChargingServiceUtils {

    public static boolean isServiceRunning(Context context) {

        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager != null) {

            for (ActivityManager.RunningServiceInfo serviceInfo
                    : activityManager.getRunningServices(Integer.MAX_VALUE)) {

                if (ChargingService.class.getName().equals(serviceInfo.service.getClassName())) {

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

            Intent intent = new Intent(context.getApplicationContext(), ChargingService.class);
            intent.setAction(Constants.ACTION_START_CHARGING_SERVICE);
            context.startService(intent);
            Log.d("charging", "startChargingService: service started");
        }
    }

    public static void stopService(Context context) {

        if (isServiceRunning(context)) {

            Intent intent = new Intent(context.getApplicationContext(), ChargingService.class);
            intent.setAction(Constants.ACTION_STOP_CHARGING_SERVICE);
            context.startService(intent);
            Log.d("charging", "startChargingService: service stopped");
        }
    }
}
