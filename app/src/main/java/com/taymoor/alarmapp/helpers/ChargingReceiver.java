package com.taymoor.alarmapp.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.taymoor.alarmapp.interfaces.IChargingListener;
import com.taymoor.alarmapp.utils.Constants;

import java.util.ArrayList;

public class ChargingReceiver extends BroadcastReceiver {

    private static ChargingReceiver instance;
    private final IntentFilter intentFilter;
    private final ArrayList<IChargingListener> listeners;

    private ChargingReceiver() {

        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        listeners = new ArrayList<>();
    }

    public static ChargingReceiver getInstance() {

        if (instance == null) {

            instance = new ChargingReceiver();
        }

        return instance;
    }

    public IntentFilter getIntentFilter() {
        return intentFilter;
    }

    public void addListener(IChargingListener listener) {

        listeners.add(listener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {

            if (!Constants.chargingRemovalAlreadyDetected) {

                Constants.chargingRemovalAlreadyDetected = true;

                if (listeners != null) {

                    for (IChargingListener listener : listeners) {

                        listener.onChargingRemoved();
                    }
                }
            }
        }
    }
}