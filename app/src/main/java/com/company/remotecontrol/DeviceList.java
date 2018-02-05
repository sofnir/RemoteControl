package com.company.remotecontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.Set;
import java.util.ArrayList;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class DeviceList extends AppCompatActivity {
    //Define widgets
    Button pairBtn;
    ListView devicesList;

    //Declaring BT module
    private BluetoothAdapter btModule = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);

        //Create widgets
        pairBtn = (Button)findViewById(R.id.button);
        devicesList = (ListView)findViewById(R.id.listView);

        //Check if device have BT modeule
        btModule = BluetoothAdapter.getDefaultAdapter();
        if(btModule == null) {
            Toast.makeText(getApplicationContext(), "Device don't have bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }
        else if(!btModule.isEnabled()) {
            //Ask for turning on bluetooth
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        //Pairing devices
        pairBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPairedDevices();
            }
        });
    }

    private void ShowPairedDevices() {
        pairedDevices = btModule.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0) {
            for(BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Name and MAC of device
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Dont' find any paired devices", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        devicesList.setAdapter(adapter);
        devicesList.setOnItemClickListener(myListClickListener); //Call method after pick device
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3) {
            //Show MAC of the BT device
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            //Open new activity
            Intent i = new Intent(DeviceList.this, LedControl.class);
            i.putExtra(EXTRA_ADDRESS, address); //Send MAC adress to the new activity
            startActivity(i);
        }
    };
}