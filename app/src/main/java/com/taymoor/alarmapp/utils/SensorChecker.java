package com.taymoor.alarmapp.utils;

import android.content.Context;
import android.content.pm.PackageManager;

public class SensorChecker {

    public static boolean isProximitySensorAvailable(Context context) {

        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY);
    }
}
