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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Mingyu Park on 2016-01-11.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private BLEConnector bleConnector;
    private LinearLayout peripheralList;
    private HashMap<String, Peripheral> peripheralMap;
    private Button btnScan;

    class Peripheral {
        private BluetoothDevice device;
        private Button button;
        private StringBuilder log;
        private FileWriter writer;

        public Peripheral(BluetoothDevice device, Context context, File filePath){
            this.device = device;
            button = new Button(context);
            log = new StringBuilder();
            try {
                writer = new FileWriter(filePath, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public Button getButton(){
            return button;
        }
        public void append(String log){
            this.log.append(log);
            if(writer != null){
                try {
                    writer.write(log);
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public String toString(){
            return log.toString();
        }
        public BluetoothDevice getDevice(){
            return device;
        }
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        final File dir = new File(Environment.getExternalStorageDirectory(), "BeaconScanner/");
        Log.d("FILE", dir.getAbsolutePath());
        if(!dir.exists())
            try {
                dir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }

        btnScan = ((Button)findViewById(R.id.btn_scan));
        btnScan.setOnClickListener(this);
        peripheralList = (LinearLayout)findViewById(R.id.list_peripheral);
        peripheralMap = new HashMap<>();

        bleConnector = new BLEConnector(this) {
            @Override
            protected void discoveryAvailableDevice(final BluetoothDevice bluetoothDevice, final int rssi, final BeaconRecord record) {
                final String buttonLabel = bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress() + "\n"
                                        + record.getUuid() + "\n" + "RSSI : " + rssi;
                if(!peripheralMap.containsKey(bluetoothDevice.getAddress())) {
                    if(record.getUuid() == null){
                        return;
                    }
                    final Peripheral peripheral = new Peripheral(bluetoothDevice, this.getContext(), new File(dir, record.getUuid()));
                    peripheral.getButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(bleConnector.getContext(), "Try to connect with " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, ChairActivity.class);
                            intent.putExtra("Address", bluetoothDevice.getAddress());

                            startActivity(intent);
                        }
                    });
                    peripheral.getButton().setText(buttonLabel);
                    peripheral.append(makeLog(record.getMinor()));
                    peripheralList.addView(peripheral.getButton());
                    peripheralMap.put(bluetoothDevice.getAddress(), peripheral);
                }
                else{

                    Peripheral peripheral = peripheralMap.get(bluetoothDevice.getAddress());
                    peripheral.getButton().setText(buttonLabel);
                    if(bleConnector.getState() != BLEConnector.STATE_CONNECTED)
                        peripheral.append(makeLog(record.getMinor()));
                }
            }

            @Override
            public void readHandler(byte[] data) {
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

    public String makeLog(byte[] data){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder builder = new StringBuilder();
        builder.append(format.format(new Date()));
        builder.append(" >> ");
        builder.append(new String(data));
        builder.append("\n");

        return builder.toString();
    };
    public String makeLog(int data){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder builder = new StringBuilder();
        builder.append(format.format(new Date()));
        builder.append(" >> ");
        builder.append(String.valueOf(data));
        builder.append("\n");

        return builder.toString();
    };
}