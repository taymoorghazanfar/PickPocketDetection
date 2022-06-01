package com.taymoor.alarmapp.utils;

public class Constants {

    // charging service
    public static final int CHARGING_SERVICE_ID = 5000;
    public static final int CHARGING_ALARM_SERVICE_ID = 6000;
    public static final String ACTION_START_CHARGING_SERVICE = "start_charging_service";
    public static final String ACTION_STOP_CHARGING_SERVICE = "stop_charging_service";
    public static final String ACTION_START_CHARGING_ALARM_SERVICE = "start_charging_alarm_service";
    public static final String ACTION_STOP_CHARGING_ALARM_SERVICE = "stop_charging_alarm_service";
    // pocket service
    public static final int POCKET_SERVICE_ID = 7000;
    public static final int POCKET_ALARM_SERVICE_ID = 8000;
    public static final String ACTION_START_POCKET_SERVICE = "start_pocket_service";
    public static final String ACTION_STOP_POCKET_SERVICE = "stop_pocket_service";
    public static final String ACTION_START_POCKET_ALARM_SERVICE = "start_pocket_alarm_service";
    public static final String ACTION_STOP_POCKET_ALARM_SERVICE = "stop_pocket_alarm_service";
    public static boolean chargingRemovalAlreadyDetected = false;
    public static boolean pocketRemovalAlreadyDetected = false;
}
