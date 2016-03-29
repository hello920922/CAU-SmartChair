package cau.project.beaconscanner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Jaewon Lee on 2016-02-18.
 */
public class NamingActivity extends AppCompatActivity {
    private HashMap<String, String> myChairs;
    private File dir;
    private String mac;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naming);

        TextView macAddress = (TextView) findViewById(R.id.macAddress);

        dir = new File(Environment.getExternalStorageDirectory(), "BeaconScanner/");
        Log.d("FILE", dir.getAbsolutePath());
        if(!dir.exists())
            try {
                dir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        Intent intent = getIntent();

        myChairs = (HashMap<String, String>) intent.getSerializableExtra("Map");
        Log.d("Map","Recieve map Successfully");
        Log.d("Map", "size of map : " + myChairs.size());

        mac = intent.getStringExtra("MacAddress");
        macAddress.setText("MAC : "+ mac);



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                setResult(RESULT_OK, new Intent().putExtra("Map", myChairs));
                finish();
                break;

            default:
                return false;

        }

        return false;
    }


    public void onEnrollButtonClicked(View v){

        File file = new File(dir, "MyChairs.txt");
        EditText editText = (EditText) findViewById(R.id.name);
        name = editText.getText().toString();

        try {
            FileWriter writer = new FileWriter(file,true);
            writer.write(mac + "/" + name + "\n");
            Log.d("File","Inserted Successfully");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        myChairs.put(mac, name);
        Log.d("Map", "Insert Successfully : " + mac + name);



        setResult(RESULT_OK, new Intent().putExtra("Map", myChairs));
        finish();
    }

    public void onCancelButtonClicked(View v){
        setResult(RESULT_OK, new Intent().putExtra("Map", myChairs));
        finish();
    }



}