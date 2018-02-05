package com.company.remotecontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import java.io.IOException;
import java.util.UUID;

public class LedControl extends AppCompatActivity{
    //Define widgets
    Button topBtn, stopBtn, downBtn, disconnectBtn, alarmBtn;
    String address = null;

    //Define BT
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.led_control);

        //Get MAC adress
        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);

        //Create widgets
        topBtn = (Button)findViewById(R.id.topBtn);
        downBtn = (Button)findViewById(R.id.downBtn);
        alarmBtn = (Button)findViewById(R.id.alarmBtn);
        stopBtn = (Button)findViewById(R.id.stopBtn);
        disconnectBtn = (Button)findViewById(R.id.disconnectBtn);

        new ConnectBT().execute();

        //Events
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTop();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goDown();
            }
        });

        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect(); //Rozłączenie
            }
        });

        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAlarm(); //włączanie LED
            }
        });
    }

    private void goTop() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("TOP".toString().getBytes());
            }
            catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void stop() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("STOP".toString().getBytes());
            }
            catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void goDown() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("DOWN".toString().getBytes());
            }
            catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void Disconnect() {
        if (btSocket!=null) {
            try {
                btSocket.close();
            }
            catch (IOException e) {
                msg("Error");
            }
        }

        finish(); //Back to the DeviceList activity
    }

    private void callAlarm() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("ALARM".toString().getBytes());
            }
            catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>
    {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(LedControl.this, "Connecting...", "Please wait...");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            }
            catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connecting error, try again...");
                finish();
            }
            else {
                msg("Connected");
                isBtConnected = true;
            }

            progress.dismiss();
        }
    }
}