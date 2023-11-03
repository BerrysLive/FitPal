package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private TextView heartRateText;
    private boolean running;

    // The UUID must match the UUID that the Bangle.js uses for its serial communication service
    private static final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        heartRateText = findViewById(R.id.heartRateText);

        // Get the default Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Device doesn't support Bluetooth");
            return;
        }

        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth isn't enabled, prompt the user to turn it on.
            // You'll need an onActivityResult handler to handle the user's choice
        }

        // Attempt to connect to the Bangle.js
        connectToBangleJs();
    }

    private void connectToBangleJs() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("Bangle.js")) { // Match the name with your Bangle.js device
                    try {
                        bluetoothSocket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
                        bluetoothSocket.connect();
                        inputStream = bluetoothSocket.getInputStream();
                        startListeningForHeartRate();
                        break;
                    } catch (IOException e) {
                        Log.e(TAG, "Error while connecting", e);
                    }
                }
            }
        }
    }

    private void startListeningForHeartRate() {
        running = true;
        Thread thread = new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            try {
                while (running && (line = reader.readLine()) != null) {
                    // Parse the JSON
                    JSONObject jsonObject = new JSONObject(line);
                    int heartRate = jsonObject.getInt("heartRate");

                    // Update UI with the new heart rate data
                    runOnUiThread(() -> heartRateText.setText(String.valueOf(heartRate)));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while reading data", e);
            }
        });
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error while closing stream", e);
            }
        }
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error while closing socket", e);
            }
        }
    }
}
