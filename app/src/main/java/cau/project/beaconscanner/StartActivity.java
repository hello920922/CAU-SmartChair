package cau.project.beaconscanner;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Jaewon Lee on 2016-02-18.
 */
public class StartActivity extends AppCompatActivity {
    private HashMap<String, String> myChairs = new HashMap<>();
    private int REQUEST_ENROLL = 1;

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_ENROLL){
            myChairs = (HashMap<String, String>)data.getExtras().getSerializable("Map");
            Log.d("Map","Recieve map succesfully");
            Log.d("Map","size of Map = "+ myChairs.size());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        FloatingActionButton search = (FloatingActionButton)findViewById(R.id.search);
        final FloatingActionButton enroll = (FloatingActionButton)findViewById(R.id.enroll);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(mainIntent);

            }
        });

        enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enrollIntent = new Intent(StartActivity.this, EnrollActivity.class);
                enrollIntent.putExtra("Map",myChairs);
                startActivityForResult(enrollIntent, REQUEST_ENROLL);

            }
        });


        final File dir = new File(Environment.getExternalStorageDirectory(), "BeaconScanner/");
        Log.d("FILE", dir.getAbsolutePath());
        if(!dir.exists())
            try {
                dir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }

        File file = new File(dir, "MyChairs.txt");
        if(!file.exists()) {

            try {

                FileWriter fw = new FileWriter(file);
                fw.write("");
                fw.flush();
                Log.d("File", "File is created.");
                fw.close();
            } catch (Exception e) {
                System.out.println("Error:" + e.getMessage());
            }
        }

        try{
            FileReader fr = null;
            BufferedReader br = null;
            String read = null;
            String mac = null;
            String name = null;
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            while((read = br.readLine())!=null){
                Log.d("File","Messege : " + read);
                mac = read.split("/")[0];
                name = read.split("/")[1];
                Log.d("File","mac: " + mac);
                Log.d("File","name: " + name);
                myChairs.put(mac, name);
                Log.d("map","Inserted successfully");

            }
            if(fr!=null)fr.close();
            if(br!=null)br.close();
        }catch(Exception e){
            System.out.println("Error:" + e.getMessage());
        }

        Log.i("map","size of map : " + myChairs.size());

    }




}