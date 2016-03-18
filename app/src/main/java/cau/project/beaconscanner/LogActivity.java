package cau.project.beaconscanner;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jaewon Lee on 2016-02-18.
 */
public class LogActivity extends AppCompatActivity {

    TextView logView;
    Intent intent;
    Boolean admitToSave = false;
    BLEConnector bleConnector;
    Button saveButton;
    ScrollView logScroll;

    class Peripheral {

        private StringBuilder log;
        private FileWriter writer;

        public Peripheral(File filePath) {

            log = new StringBuilder();
            try {
                writer = new FileWriter(filePath, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void append(String log) {
            this.log.append(log);

            if(admitToSave == true) {
                if (writer != null) {
                    try {
                        writer.write(log);
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public String toString() {
            return log.toString();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);


        logScroll = (ScrollView) findViewById(R.id.logScroll);


        intent = getIntent();



        final File dir = new File(Environment.getExternalStorageDirectory(), "BeaconScanner/");
        Log.d("FILE", dir.getAbsolutePath());
        if(!dir.exists())
            try {
                dir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }


        final Peripheral peripheral = new Peripheral(new File(dir, "Chair.txt"));

        logView = (TextView) findViewById(R.id.log);
        saveButton = (Button) findViewById(R.id.Save);
        peripheral.append(intent.getStringExtra("Log"));

        logView.setText(peripheral.toString());


        bleConnector = new BLEConnector(this) {
            @Override
            protected void discoveryAvailableDevice(BluetoothDevice bluetoothDevice, int rssi, BeaconRecord record) {
                if(bluetoothDevice.getAddress() == null){
                    return;
                }

                if(record.getUuid() == null){
                    return;
                }

                if(bluetoothDevice.getAddress().equals(intent.getStringExtra("Address"))){
                    bleConnector.stopDiscovery();
                    bleConnector.connectDevice(bluetoothDevice);
                    Toast.makeText(getApplicationContext(), "Connected!",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void readHandler(byte[] data) {
                peripheral.append(makeLog(data));
                logView.setText(peripheral.toString());


                logScroll.post(new Runnable() {
                    @Override
                    public void run() {
                        logScroll.fullScroll(logScroll.FOCUS_DOWN);
                    }
                });

            }
        };

        bleConnector.startDiscovery();




    }

    @Override
     public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                bleConnector.startDiscovery();
                bleConnector.disconnect();
                finish();
                break;

            default:
                return false;

        }

        return false;
    }



    public void onSaveButtonClicked(View v){
        if(admitToSave == true){
            saveButton.setText("Save");
            admitToSave = false;
        }
        else{
            saveButton.setText("Stop");
            admitToSave = true;
        }
    }
    public void onBackButtonClicked(View v){
        bleConnector.stopDiscovery();
        bleConnector.disconnect();
        finish();
    }

    public String makeLog(int data){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder builder = new StringBuilder();
        builder.append(format.format(new Date()));
        builder.append(" >> ");
        builder.append(String.valueOf(data));
        builder.append("\n");

        return builder.toString();
    };
    public String makeLog(byte[] data){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder builder = new StringBuilder();
        builder.append(format.format(new Date()));
        builder.append(" >> ");
        builder.append(new String(data));
        builder.append("\n");

        return builder.toString();
    };



}