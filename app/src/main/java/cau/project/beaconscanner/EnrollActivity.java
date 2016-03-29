package cau.project.beaconscanner;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    private HashMap<String, String> myChairs;

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

        final File dir = new File(Environment.getExternalStorageDirectory(), "BeaconScanner/");
        Log.d("FILE", dir.getAbsolutePath());
        if(!dir.exists())
            try {
                dir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        Intent intent = getIntent();
        myChairs = (HashMap <String,String>)intent.getExtras().getSerializable("Map");
        Log.d("Map","Recieve map Successfully");
        Log.d("Map", "size of map : " + myChairs.size());

        btnScan = ((Button)findViewById(R.id.btn_scan));
        btnScan.setOnClickListener(this);
        peripheralList = (LinearLayout)findViewById(R.id.list_peripheral);
        peripheralMap = new HashMap<>();

        bleConnector = new BLEConnector(this, new ReadInterface() {
            @Override
            public void read(byte[] data) {

            }
        }) {
            @Override
            protected void discoveryAvailableDevice(final BluetoothDevice bluetoothDevice, final int rssi, final BeaconRecord record) {
                final String buttonLabel = bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress() + "\n"
                                        + record.getUuid() + "\n" + "Major : " + record.getMajor() + "\n" +"Minor : " + record.getMinor() + "\n" + "RSSI : " + rssi;
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
                            startActivity(namingIntent);


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

    public void onBackButtonClicked(View v){
        bleConnector.stopDiscovery();
        bleConnector.disconnect();

        finish();
    }


}