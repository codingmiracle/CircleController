package com.harrysoft.androidbluetoothserial.demoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.harrysoft.androidbluetoothserial.demoapp.JoystickView.JoystickDataModel;
import com.harrysoft.androidbluetoothserial.demoapp.JoystickView.JoystickView;

import java.util.logging.Logger;

public class CommunicateActivity extends AppCompatActivity {

    private TextView connectionText, messagesView;
    private EditText messageBox;
    private Button sendButton, connectButton;
    private JoystickView joystick;
    private TextView battery;

    private CommunicateViewModel viewModel;

    //Get Battery state
    private BroadcastReceiver nBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            battery.setText(String.valueOf(level)+"%");

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup our activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicate);
        // Enable the back button in the action bar if possible
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        battery = (TextView) this.findViewById(R.id.communicate_messages);
        this.registerReceiver(this.nBatInfoReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        // Setup our ViewModel
        viewModel = ViewModelProviders.of(this).get(CommunicateViewModel.class);

        // This method return false if there is an error, so if it does, we should close.
        if (!viewModel.setupViewModel(getIntent().getStringExtra("device_name"), getIntent().getStringExtra("device_mac"))) {
            finish();
            return;
        }

        // Setup our Views
        connectionText = findViewById(R.id.communicate_connection_text);
        messagesView = findViewById(R.id.communicate_messages);
        messageBox = findViewById(R.id.communicate_message);
        sendButton = findViewById(R.id.communicate_send);
        connectButton = findViewById(R.id.communicate_connect);

        // Start observing the data sent to us by the ViewModel
        viewModel.getConnectionStatus().observe(this, this::onConnectionStatus);
        viewModel.getDeviceName().observe(this, name -> setTitle(getString(R.string.device_name_format, name)));
        viewModel.getMessages().observe(this, message -> {
            if (TextUtils.isEmpty(message)) {
                message = getString(R.string.no_messages);
            }
            messagesView.setText(message);
        });
        viewModel.getMessage().observe(this, message -> {
            // Only update the message if the ViewModel is trying to reset it
            if (TextUtils.isEmpty(message)) {
                messageBox.setText(message);
            }
        });

        // Setup the send button click action
        sendButton.setOnClickListener(v -> viewModel.sendMessage(messageBox.getText().toString()));

        joystick = (JoystickView) findViewById(R.id.joystick);

        joystick.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {
            @Override
            public void onValueChanged(int angle, int power, int direction) {
                JoystickDataModel joystickDataModel = new JoystickDataModel(angle, power);
                viewModel.sendMessage(joystickDataModel.toString());
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
    }

    // Called when the ViewModel updates us of our connectivity status
    private void onConnectionStatus(CommunicateViewModel.ConnectionStatus connectionStatus) {
        switch (connectionStatus) {
            case CONNECTED:
                connectionText.setText(R.string.status_connected);
                messageBox.setEnabled(true);
                sendButton.setEnabled(true);
                connectButton.setEnabled(true);
                connectButton.setText(R.string.disconnect);
                connectButton.setOnClickListener(v -> viewModel.disconnect());
                break;

            case CONNECTING:
                connectionText.setText(R.string.status_connecting);
                messageBox.setEnabled(false);
                sendButton.setEnabled(false);
                connectButton.setEnabled(false);
                connectButton.setText(R.string.connect);
                break;

            case DISCONNECTED:
                connectionText.setText(R.string.status_disconnected);
                messageBox.setEnabled(false);
                sendButton.setEnabled(false);
                connectButton.setEnabled(true);
                connectButton.setText(R.string.connect);
                connectButton.setOnClickListener(v -> viewModel.connect());
                break;
        }
    }

    // Called when a button in the action bar is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                // If the back button was pressed, handle it the normal way
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Called when the user presses the back button
    @Override
    public void onBackPressed() {
        // Close the activity
        finish();
    }
}
