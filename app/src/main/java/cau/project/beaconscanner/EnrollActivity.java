package cau.project.beaconscanner;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;


public class EnrollActivity extends AppCompatActivity implements View.OnClickListener{
    private BLEConnector bleConnector;
    private LinearLayout peripheralList;
    private HashMap<String, Peripheral> peripheralMap;
    private Button btnScan;
    private HashMap<String, String> myChairs = new HashMap<>();
    private int REQUEST_NAME = 1;

    class Peripheral {
        private Button button;
        public Peripheral(Context context){
            button = new Button(context);
        }
        public Button getButton(){
            return button;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);


        //Search main directory and create if not exist.
        final File dir = new File(Environment.getExternalStorageDirectory(), "BeaconScanner/");
        Log.d("FILE", dir.getAbsolutePath());
        if(!dir.exists())
            try {
                dir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }

        //Get the intent that parent activity gives.
        Intent intent = getIntent();

        //Get the hash map that parent activity transmit.
        myChairs = (HashMap <String,String>)intent.getExtras().getSerializable("Map");
        Log.d("Map","Recieve map Successfully");
        Log.d("Map", "size of map : " + myChairs.size());

        //Find view that is in the layout.
        btnScan = ((Button)findViewById(R.id.btn_scan));
        peripheralList = (LinearLayout)findViewById(R.id.list_peripheral);

        //Apply listenr to button.
        btnScan.setOnClickListener(this);


        //Complete abstract method of bleConnector.

        peripheralMap = new HashMap<>();
        bleConnector = new BLEConnector(this, new ReadInterface() {
            @Override
            public void read(byte[] data) {

            }
        }) {
            @Override
            protected void discoveryAvailableDevice(final BluetoothDevice bluetoothDevice, final int rssi, final BeaconRecord record) {

                final String buttonLabel = "Name : " + myChairs.get(bluetoothDevice.getAddress()) + "\n" + bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress() + "\n"
                        + record.getUuid() + "\n" + "Major : " + record.getMajor() + "\n" + "Minor : " + record.getMinor() + "\n" + "RSSI : " + rssi
                        + "\n" + "Registered : " + myChairs.containsKey(bluetoothDevice.getAddress());


                if(!peripheralMap.containsKey(bluetoothDevice.getAddress())) {
                    if(record.getUuid() == null){
                        return;
                    }
                    final Peripheral peripheral = new Peripheral(this.getContext());
                    peripheral.getButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent namingIntent = new Intent(EnrollActivity.this, NamingActivity.class);
                            namingIntent.putExtra("MacAddress", bluetoothDevice.getAddress());
                            namingIntent.putExtra("Map",myChairs);
                            startActivityForResult(namingIntent, REQUEST_NAME);


                        }
                    });
                    peripheral.getButton().setText(buttonLabel);
                    peripheralList.addView(peripheral.getButton());
                    peripheralMap.put(bluetoothDevice.getAddress(), peripheral);
                }
                else{

                    Peripheral peripheral = peripheralMap.get(bluetoothDevice.getAddress());
                    peripheral.getButton().setText(buttonLabel);

                }
            }
        };
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_NAME){
            myChairs = (HashMap<String,String>)data.getSerializableExtra("Map");
            Log.d("Map","Recieve map succesfully");
            Log.d("Map","size of Map = "+ myChairs.size());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_scan) {
            if(bleConnector.getState() == BLEConnector.STATE_WAITING){
                if(bleConnector.startDiscovery()){
                    btnScan.setText("STOP");
                }
            }
            else if(bleConnector.getState() == BLEConnector.STATE_CONNECTED || bleConnector.getState() == BLEConnector.STATE_CONNECTING){
                btnScan.setText("SCAN");
                bleConnector.disconnect();
            }
            else if(bleConnector.getState() == BLEConnector.STATE_SCANNING){
                btnScan.setText("SCAN");
                bleConnector.stopDiscovery();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                setResult(RESULT_OK, new Intent().putExtra("Map", myChairs));
                bleConnector.stopDiscovery();
                finish();
                break;

            default:
                return false;

        }

        return false;
    }


}