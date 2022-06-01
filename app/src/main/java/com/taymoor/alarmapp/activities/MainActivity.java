package com.taymoor.alarmapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.taymoor.alarmapp.R;
import com.taymoor.alarmapp.helpers.ChargingReceiver;
import com.taymoor.alarmapp.helpers.PocketManager;
import com.taymoor.alarmapp.interfaces.IChargingListener;
import com.taymoor.alarmapp.interfaces.IPocketListener;
import com.taymoor.alarmapp.services.charging.ChargingAlarmServiceUtils;
import com.taymoor.alarmapp.services.charging.ChargingServiceUtils;
import com.taymoor.alarmapp.services.pocket.PocketAlarmServiceUtils;
import com.taymoor.alarmapp.services.pocket.PocketServiceUtils;
import com.taymoor.alarmapp.utils.Constants;
import com.taymoor.alarmapp.utils.SensorChecker;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, IChargingListener, IPocketListener {

    // variables
    public static final int STARTED = 1;
    public static final int STOPPED = 2;
    public static final int DETECTED = 3;
    private boolean chargingServiceStarted = false;
    private boolean pocketServiceStarted = false;

    // helpers
    private ChargingReceiver chargingReceiver;
    private PocketManager pocketManager;

    // views
    private TextView textViewChargingServiceStatus;
    private AppCompatButton buttonStartChargingService;
    private AppCompatButton buttonStopChargingService;
    private AppCompatButton buttonResetChargingService;

    private TextView textViewPocketServiceStatus;
    private AppCompatButton buttonStartPocketService;
    private AppCompatButton buttonStopPocketService;
    private AppCompatButton buttonResetPocketService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        chargingReceiver = ChargingReceiver.getInstance();
        chargingReceiver.addListener(this);
        registerReceiver(chargingReceiver, chargingReceiver.getIntentFilter());

        pocketManager = PocketManager.getInstance(this);
        pocketManager.addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(chargingReceiver);
    }

    private void initViews() {

        // charging
        textViewChargingServiceStatus = findViewById(R.id.text_view_charging);
        buttonStartChargingService = findViewById(R.id.button_start_charging_service);
        buttonStopChargingService = findViewById(R.id.button_stop_charging_service);
        buttonResetChargingService = findViewById(R.id.button_reset_charging_service);
        buttonStartChargingService.setOnClickListener(this);
        buttonStopChargingService.setOnClickListener(this);
        buttonResetChargingService.setOnClickListener(this);

        // pocket
        textViewPocketServiceStatus = findViewById(R.id.text_view_pocket);
        buttonStartPocketService = findViewById(R.id.button_start_pocket_service);
        buttonStopPocketService = findViewById(R.id.button_stop_pocket_service);
        buttonResetPocketService = findViewById(R.id.button_reset_pocket_service);
        buttonStartPocketService.setOnClickListener(this);
        buttonStopPocketService.setOnClickListener(this);
        buttonResetPocketService.setOnClickListener(this);
    }

    private void setupViews() {

        // charging
        if (ChargingServiceUtils.isServiceRunning(this)) {

            if (ChargingAlarmServiceUtils.isServiceRunning(this)) {

                toggleChargingControls(DETECTED);

            } else {

                toggleChargingControls(STARTED);
            }

        } else {

            toggleChargingControls(STOPPED);
        }

        // pocket
        if (PocketServiceUtils.isServiceRunning(this)) {

            if (PocketAlarmServiceUtils.isServiceRunning(this)) {

                togglePocketControls(DETECTED);

            } else {

                togglePocketControls(STARTED);
            }

        } else {

            togglePocketControls(STOPPED);
        }
    }

    @Override
    public void onClick(View view) {

        // charging
        if (view.getId() == R.id.button_start_charging_service) {

            ChargingServiceUtils.startService(this);
            toggleChargingControls(STARTED);

        } else if (view.getId() == R.id.button_stop_charging_service) {

            ChargingServiceUtils.stopService(this);
            toggleChargingControls(STOPPED);

        } else if (view.getId() == R.id.button_reset_charging_service) {

            Constants.chargingRemovalAlreadyDetected = false;
            ChargingAlarmServiceUtils.stopService(this);
            toggleChargingControls(STARTED);
        }

        //pocket
        else if (view.getId() == R.id.button_start_pocket_service) {

            if (SensorChecker.isProximitySensorAvailable(this)) {

                PocketServiceUtils.startService(this);
                togglePocketControls(STARTED);
                return;
            }

            Toast.makeText(this, "Proximity is required for this feature",
                    Toast.LENGTH_SHORT).show();

        } else if (view.getId() == R.id.button_stop_pocket_service) {

            PocketServiceUtils.stopService(this);
            togglePocketControls(STOPPED);

        } else if (view.getId() == R.id.button_reset_pocket_service) {

            Constants.pocketRemovalAlreadyDetected = false;
            PocketAlarmServiceUtils.stopService(this);
            togglePocketControls(STARTED);
        }
    }

    private void toggleChargingControls(int state) {

        switch (state) {

            case STARTED:
                chargingServiceStarted = true;
                textViewChargingServiceStatus.setText("Service is running...");

                buttonStartChargingService.setEnabled(false);
                buttonStartChargingService.setBackgroundColor(Color.GRAY);

                buttonStopChargingService.setEnabled(true);
                buttonStopChargingService.setBackgroundColor(Color.parseColor("#2196F3"));

                buttonResetChargingService.setEnabled(false);
                buttonResetChargingService.setBackgroundColor(Color.GRAY);
                break;

            case STOPPED:
                chargingServiceStarted = false;
                textViewChargingServiceStatus.setText("Service is stopped");

                buttonStartChargingService.setEnabled(true);
                buttonStartChargingService.setBackgroundColor(Color.parseColor("#2196F3"));

                buttonStopChargingService.setEnabled(false);
                buttonStopChargingService.setBackgroundColor(Color.GRAY);

                buttonResetChargingService.setEnabled(false);
                buttonResetChargingService.setBackgroundColor(Color.GRAY);
                break;

            case DETECTED:
                textViewChargingServiceStatus.setText("Charger removal detected");
                buttonStartChargingService.setEnabled(false);
                buttonStartChargingService.setBackgroundColor(Color.GRAY);

                buttonStopChargingService.setEnabled(false);
                buttonStopChargingService.setBackgroundColor(Color.GRAY);

                buttonResetChargingService.setEnabled(true);
                buttonResetChargingService.setBackgroundColor(Color.parseColor("#2196F3"));
        }
    }

    private void togglePocketControls(int state) {

        switch (state) {

            case STARTED:
                pocketServiceStarted = true;
                textViewPocketServiceStatus.setText("Service is running...");

                buttonStartPocketService.setEnabled(false);
                buttonStartPocketService.setBackgroundColor(Color.GRAY);

                buttonStopPocketService.setEnabled(true);
                buttonStopPocketService.setBackgroundColor(Color.parseColor("#009688"));

                buttonResetPocketService.setEnabled(false);
                buttonResetPocketService.setBackgroundColor(Color.GRAY);
                break;

            case STOPPED:
                pocketServiceStarted = false;
                textViewPocketServiceStatus.setText("Service is stopped");

                buttonStartPocketService.setEnabled(true);
                buttonStartPocketService.setBackgroundColor(Color.parseColor("#009688"));

                buttonStopPocketService.setEnabled(false);
                buttonStopPocketService.setBackgroundColor(Color.GRAY);

                buttonResetPocketService.setEnabled(false);
                buttonResetPocketService.setBackgroundColor(Color.GRAY);
                break;

            case DETECTED:
                textViewPocketServiceStatus.setText("Pocket pick detected");
                buttonStartPocketService.setEnabled(false);
                buttonStartPocketService.setBackgroundColor(Color.GRAY);

                buttonStopPocketService.setEnabled(false);
                buttonStopPocketService.setBackgroundColor(Color.GRAY);

                buttonResetPocketService.setEnabled(true);
                buttonResetPocketService.setBackgroundColor(Color.parseColor("#009688"));
        }
    }

    @Override
    public void onChargingRemoved() {

        if (chargingServiceStarted) {

            toggleChargingControls(DETECTED);
        }
    }

    @Override
    public void onPocketPicked() {

        if (pocketServiceStarted) {

            togglePocketControls(DETECTED);
        }
    }
}
