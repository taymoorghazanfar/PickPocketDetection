package com.taymoor.alarmapp.helpers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.util.Log;

import com.taymoor.alarmapp.interfaces.IPocketListener;
import com.taymoor.alarmapp.utils.Constants;

import java.util.ArrayList;

import static android.content.Context.SENSOR_SERVICE;

public class PocketManager {

    private static PocketManager instance;
    private final SensorManager sensorManager;
    private final Sensor proximitySensor;
    private final SensorEventListener sensorEventListener;
    private final CountDownTimer timer;
    private final ArrayList<IPocketListener> listeners;
    private boolean placedInPocket;

    private PocketManager(Context context) {

        listeners = new ArrayList<>();

        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

                    if (event.values[0] == 0) {

                        Log.d("pocket", "onSensorChanged: NEAR");
                        timer.start();

                    } else {

                        Log.d("pocket", "onSensorChanged: AWAY");
                        timer.cancel();

                        if (placedInPocket) {
                            Log.d("pocket", "stop: placed in pocket: " + placedInPocket);
                            Log.d("pocket", "onSensorChanged: ready to send alarm");
                            placedInPocket = false;

                            if (!Constants.pocketRemovalAlreadyDetected) {

                                Log.d("pocket", "onSensorChanged: sending alarm");
                                Constants.pocketRemovalAlreadyDetected = true;
                                Log.d("pocket", "onSensorChanged: pocket picked");
                                for (IPocketListener listener : listeners) {

                                    listener.onPocketPicked();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        timer = new CountDownTimer(6000, 1000) {

            @Override
            public void onTick(long l) {

                Log.d("pocket", "onTick: timer running: " + l);
            }

            @Override
            public void onFinish() {

                placedInPocket = true;
                Log.d("pocket", "onFinish: placed in pocked");
            }
        };
    }

    public static PocketManager getInstance(Context context) {

        if (instance == null) {

            instance = new PocketManager(context);
        }

        return instance;
    }

    public void addListener(IPocketListener listener) {

        this.listeners.add(listener);
    }

    public void start() {

        placedInPocket = false;
        sensorManager.registerListener(sensorEventListener,
                proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {

        Log.d("pocket", "stop: placed in pocket: " + placedInPocket);
        sensorManager.unregisterListener(sensorEventListener);
    }
}
