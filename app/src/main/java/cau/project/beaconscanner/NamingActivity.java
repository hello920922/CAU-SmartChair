package cau.project.beaconscanner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Jaewon Lee on 2016-02-18.
 */
public class NamingActivity extends AppCompatActivity {
    private HashMap<String, String> myChairs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naming);

        TextView macAddress = (TextView) findViewById(R.id.macAddress);

        final File dir = new File(Environment.getExternalStorageDirectory(), "BeaconScanner/");
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

        macAddress.setText("MAC : "+ intent.getStringExtra("MacAddress"));



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




}