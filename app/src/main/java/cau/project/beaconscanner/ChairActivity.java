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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Jaewon Lee on 2016-02-18.
 */
public class ChairActivity extends AppCompatActivity {

    BitmapFactory.Options options;
    Bitmap bitmap;
    Bitmap b1;
    ImageView im;
    private HashMap<String, String> myChairs;
    private TextView dataField;
    private TextView PRBField;
    private TextView PLBField;
    private TextView PRFField;
    private TextView PLFField;
    private TextView RTiltField;
    private TextView LTiltField;
    private Intent intent;
    private int value = -1;
    private int LValue = -1 ,RValue = -1;
    private int PRB = -1 , PLB = -1 , PRF = -1 , PLF = -1 , RTilt = -1 , LTilt = -1;
    private String sData;
    private ChairView m;
    private LinearLayout chairActivity;
    private RelativeLayout logActivity;
    private Peripheral peripheral;
    private Boolean admitToSave = false;
    private TextView logView;
    private Button saveButton;
    private ScrollView logScroll;
    private int control;
    private int LOG_ACTIVITY = 1;
    private int CHAIR_ACTIVITY = 2;



    private BLEConnector bleConnector = new BLEConnector(this, new ReadInterface() {
        @Override
        public void read(byte[] data) {
            dataField.setText("Data  : " + new String(data));
            peripheral.append(makeLog(data));
            logView.setText(peripheral.toString());
            logScroll.post(new Runnable() {
                @Override
                public void run() {
                    logScroll.fullScroll(logScroll.FOCUS_DOWN);
                }
            });
            sData = new String(data);
            if(!sData.contains("?")){
                value = Integer.parseInt(sData);
                LValue = value >> 8;
                RValue = value & 0x00FF;
                PLF = (LValue >> 4) & 0x0003;
                PLB = (LValue >> 2) & 0x0003;
                LTilt = (LValue) & 0x0003;
                PRF = (RValue >> 4) & 0x0003;
                PRB = (RValue >> 2) & 0x0003;
                RTilt = (RValue) & 0x0003;

                Log.d("Cencor", "PLF : " + PLF + ", PLB : " + PLB + ", LTilt : " + LTilt + ", PRF : " + PRF + ", PRB : " + PRB + ", RTilt : " + RTilt);
                m.setCode(PLF, PLB, PRF, PRB);

                PLFField.setText("PRB : " + PRB);
                PLBField.setText("PLB : " + PLB);
                LTiltField.setText("LTilt : " + LTilt);
                PRFField.setText("PRF : " + PRF);
                PRBField.setText("PRB : " + PRB);
                RTiltField.setText("RTilt: " + RTilt);


                switch(RTilt){
                    case 0:
                        switch(LTilt){
                            case 0:
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.Chair00, options);
                                b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                im = (ImageView)findViewById(R.id.imageView);
                                im.setImageBitmap(b1);
                                break;
                            case 1:
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.Chair01, options);
                                b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                im = (ImageView)findViewById(R.id.imageView);
                                im.setImageBitmap(b1);
                                break;
                            case 2:
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.Chair02, options);
                                b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                im = (ImageView)findViewById(R.id.imageView);
                                im.setImageBitmap(b1);
                                break;
                            case 3:
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.Chair03, options);
                                b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                im = (ImageView)findViewById(R.id.imageView);
                                im.setImageBitmap(b1);
                                break;

                        }
                        break;


                    case 1:
                        switch(RTilt){
                            case 0:
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.Chair10, options);
                                b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                im = (ImageView)findViewById(R.id.imageView);
                                im.setImageBitmap(b1);
                                break;
                            case 1:
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.Chair11, options);
                                b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                im = (ImageView)findViewById(R.id.imageView);
                                im.setImageBitmap(b1);
                                break;
                            case 2:
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.Chair12, options);
                                b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                im = (ImageView)findViewById(R.id.imageView);
                                im.setImageBitmap(b1);
                                break;
                            case 3:
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.Chair13, options);
                                b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                im = (ImageView)findViewById(R.id.imageView);
                                im.setImageBitmap(b1);
                                break;

                        }
                        break;



                    case 2:
                        switch(RTilt){
                            case 0:
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.Chair20, options);
                                b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                im = (ImageView)findViewById(R.id.imageView);
                                im.setImageBitmap(b1);
                                break;
                            case 1:
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.Chair21, options);
                                b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                im = (ImageView)findViewById(R.id.imageView);
                                im.setImageBitmap(b1);
                                break;
                            case 2:
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.Chair22, options);
                                b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                im = (ImageView)findViewById(R.id.imageView);
                                im.setImageBitmap(b1);
                                break;
                            case 3:
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.Chair23, options);
                                b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                im = (ImageView)findViewById(R.id.imageView);
                                im.setImageBitmap(b1);
                                break;

                        }
                        break;


                    case 3:
                        switch(RTilt){
                            case 0:
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.Chair30, options);
                                b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                im = (ImageView)findViewById(R.id.imageView);
                                im.setImageBitmap(b1);
                                break;
                            case 1:
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.Chair31, options);
                                b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                im = (ImageView)findViewById(R.id.imageView);
                                im.setImageBitmap(b1);
                                break;
                            case 2:
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.Chair32, options);
                                b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                im = (ImageView)findViewById(R.id.imageView);
                                im.setImageBitmap(b1);
                                break;
                            case 3:
                                options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.Chair33, options);
                                b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                im = (ImageView)findViewById(R.id.imageView);
                                im.setImageBitmap(b1);
                                break;

                        }
                        break;

                }
                m.invalidate();
            }
            else{
                Log.d("Cencor",sData);
            }


        }
    }) {
        @Override
        protected void discoveryAvailableDevice(final BluetoothDevice bluetoothDevice, final int rssi, final BLEConnector.BeaconRecord record) {


            if(bluetoothDevice.getAddress() == null){
                return;
            }

            if(record.getUuid() == null){
                return;
            }

            if(bluetoothDevice.getAddress().equals(intent.getStringExtra("Address"))){
                bleConnector.connectDevice(bluetoothDevice);
                Toast.makeText(getApplicationContext(), "Connected!",Toast.LENGTH_LONG).show();

            }


        }

    };


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
		setContentView(R.layout.activity_chair);
        control = CHAIR_ACTIVITY;
        intent = getIntent();
        final File dir = new File(Environment.getExternalStorageDirectory(), "BeaconScanner/");
        Log.d("FILE", dir.getAbsolutePath());
        if(!dir.exists())
            try {
                dir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }

        myChairs = (HashMap<String, String>)intent.getSerializableExtra("Map");
        Log.d("Map","Recieve map Successfully");
        Log.d("Map", "size of map : " + myChairs.size());

        peripheral = new Peripheral(new File(dir, myChairs.get(intent.getStringExtra("Address"))+".txt"));

        m = new ChairView(this);
        logScroll = (ScrollView) findViewById(R.id.logScroll);
        logView = (TextView) findViewById(R.id.log);
        saveButton = (Button) findViewById(R.id.Save);

        chairActivity = (LinearLayout) findViewById(R.id.ChairActivity);
        logActivity = (RelativeLayout) findViewById(R.id.LogActivity);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        frameLayout.addView(m);

        options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.chair, options);
        b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        im = (ImageView)findViewById(R.id.imageView);
        im.setImageBitmap(b1);




        dataField = (TextView)findViewById(R.id.data);
        PRBField = (TextView)findViewById(R.id.PRB);
        PRFField = (TextView)findViewById(R.id.PRF);
        RTiltField = (TextView)findViewById(R.id.RTilt);
        PLBField = (TextView)findViewById(R.id.PLB);
        PLFField = (TextView)findViewById(R.id.PLF);
        LTiltField = (TextView)findViewById(R.id.LTilt);









        bleConnector.startDiscovery();

        


    }

    public void onLogButtonClicked(View v){
        chairActivity.setVisibility(LinearLayout.INVISIBLE);
        logActivity.setVisibility(RelativeLayout.VISIBLE);
        control = LOG_ACTIVITY;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                if(control == CHAIR_ACTIVITY){
                    finish();
                }
                else if(control == LOG_ACTIVITY){
                    logActivity.setVisibility(RelativeLayout.INVISIBLE);
                    chairActivity.setVisibility(LinearLayout.VISIBLE);
                    control=CHAIR_ACTIVITY;
                }
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
        logActivity.setVisibility(RelativeLayout.INVISIBLE);
        chairActivity.setVisibility(LinearLayout.VISIBLE);
        control = CHAIR_ACTIVITY;
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


    class Center{
        private float x;
        private float y;

        public Center(){
            x=0;
            y=0;
        }

        public Center(float x, float y){
            this.x = x;
            this.y = y;
        }

        public void setX(float x){
            this.x = x;
        }

        public void setY(float y){
            this.y = y;
        }

        public float getX(){
            return x;
        }

        public float getY(){
            return y;
        }
    }

    class Oval{
        private Center center = new Center();
        private float height;
        private float width;

        public Oval(Center center, float width, float height){
            this.center = center;
            this.height = height;
            this.width = width;

        }

        public Oval(){
            center = new Center(0,0);
            height = 0;
            width = 0;
        }

        public Center getCenter(){
            return center;
        }

        public float getHeight(){
            return height;
        }

        public float getWidth(){
            return width;
        }
    }


    class ChairView extends View{
        Paint paint = new Paint();

        float pOvalWidth = 50f;
        float pOvalHeight = 20f;

        Oval PRB = new Oval(new Center(530f,1000f), pOvalWidth, pOvalHeight);
        Oval PRF = new Oval(new Center(500f,1075f), pOvalWidth, pOvalHeight);
        Oval PLB = new Oval(new Center(935f,1000f), pOvalWidth, pOvalHeight);
        Oval PLF = new Oval(new Center(985f,1075f), pOvalWidth, pOvalHeight);
        int code_PRB;
        int code_PRF;
        int code_PLB;
        int code_PLF;


        public ChairView(Context context){
            super(context);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(6F);


        }

        public void setCode(int PLF, int PLB, int PRF, int PRB){
            code_PLF = PLF;
            code_PLB = PLB;
            code_PRF = PRF;
            code_PRB = PRB;

        }
        @Override
        protected void onDraw(Canvas canvas){



            switch(code_PLF){
                case 0:
                    paint.setColor(Color.GREEN);
                    canvas.drawOval(PLF.getCenter().getX() - PLF.getWidth(), PLF.getCenter().getY() - PLF.getHeight(), PLF.getCenter().getX() + PLF.getWidth(), PLF.getCenter().getY() + PLF.getHeight(), paint); //PLF

                    break;
                case 1:
                    paint.setColor(Color.YELLOW);
                    canvas.drawOval(PLF.getCenter().getX() - PLF.getWidth(), PLF.getCenter().getY() - PLF.getHeight(), PLF.getCenter().getX() + PLF.getWidth(), PLF.getCenter().getY() + PLF.getHeight(), paint); //PLF

                    break;
                case 2:
                    paint.setColor(Color.argb(255,255,127,0));
                    canvas.drawOval(PLF.getCenter().getX() - PLF.getWidth(), PLF.getCenter().getY() - PLF.getHeight(), PLF.getCenter().getX() + PLF.getWidth(), PLF.getCenter().getY() + PLF.getHeight(), paint); //PLF

                    break;
                case 3:
                    paint.setColor(Color.RED);
                    canvas.drawOval(PLF.getCenter().getX() - PLF.getWidth(), PLF.getCenter().getY() - PLF.getHeight(), PLF.getCenter().getX() + PLF.getWidth(), PLF.getCenter().getY()+PLF.getHeight(), paint); //PLF

                    break;
            }

            switch(code_PLB){
                case 0:
                    paint.setColor(Color.GREEN);
                    canvas.drawOval(PLB.getCenter().getX() - PLB.getWidth(), PLB.getCenter().getY() - PLB.getHeight(), PLB.getCenter().getX() + PLB.getWidth(), PLB.getCenter().getY() + PLB.getHeight(), paint); //PLB

                    break;
                case 1:
                    paint.setColor(Color.YELLOW);
                    canvas.drawOval(PLB.getCenter().getX() - PLB.getWidth(), PLB.getCenter().getY() - PLB.getHeight(), PLB.getCenter().getX() + PLB.getWidth(), PLB.getCenter().getY() + PLB.getHeight(), paint); //PLB

                    break;
                case 2:
                    paint.setColor(Color.argb(255,255,127,0));
                    canvas.drawOval(PLB.getCenter().getX() - PLB.getWidth(), PLB.getCenter().getY() - PLB.getHeight(), PLB.getCenter().getX() + PLB.getWidth(), PLB.getCenter().getY() + PLB.getHeight(), paint); //PLB

                    break;
                case 3:
                    paint.setColor(Color.RED);
                    canvas.drawOval(PLB.getCenter().getX() - PLB.getWidth(), PLB.getCenter().getY() - PLB.getHeight(), PLB.getCenter().getX() + PLB.getWidth(), PLB.getCenter().getY()+PLB.getHeight(), paint); //PLB

                    break;
            }

            switch(code_PRF){
                case 0:
                    paint.setColor(Color.GREEN);
                    canvas.drawOval(PRF.getCenter().getX() - PRF.getWidth(), PRF.getCenter().getY() - PRF.getHeight(), PRF.getCenter().getX() + PRF.getWidth(), PRF.getCenter().getY() + PRF.getHeight(), paint); //PRF

                    break;
                case 1:
                    paint.setColor(Color.YELLOW);
                    canvas.drawOval(PRF.getCenter().getX() - PRF.getWidth(), PRF.getCenter().getY() - PRF.getHeight(), PRF.getCenter().getX() + PRF.getWidth(), PRF.getCenter().getY() + PRF.getHeight(), paint); //PRF

                    break;
                case 2:
                    paint.setColor(Color.argb(255,255,127,0));
                    canvas.drawOval(PRF.getCenter().getX() - PRF.getWidth(), PRF.getCenter().getY() - PRF.getHeight(), PRF.getCenter().getX() + PRF.getWidth(), PRF.getCenter().getY() + PRF.getHeight(), paint); //PRF

                    break;
                case 3:
                    paint.setColor(Color.RED);
                    canvas.drawOval(PRF.getCenter().getX() - PRF.getWidth(), PRF.getCenter().getY() - PRF.getHeight(), PRF.getCenter().getX() + PRF.getWidth(), PRF.getCenter().getY() + PRF.getHeight(), paint); //PRF

                    break;
            }

            switch(code_PRB){
                case 0:
                    paint.setColor(Color.GREEN);
                    canvas.drawOval(PRB.getCenter().getX() - PRB.getWidth(), PRB.getCenter().getY() - PRB.getHeight(), PRB.getCenter().getX() + PRB.getWidth(), PRB.getCenter().getY() + PRB.getHeight(), paint); //PRB

                    break;
                case 1:
                    paint.setColor(Color.YELLOW);
                    canvas.drawOval(PRB.getCenter().getX() - PRB.getWidth(), PRB.getCenter().getY() - PRB.getHeight(), PRB.getCenter().getX() + PRB.getWidth(), PRB.getCenter().getY() + PRB.getHeight(), paint); //PRB

                    break;
                case 2:
                    paint.setColor(Color.argb(255,255,127,0));
                    canvas.drawOval(PRB.getCenter().getX() - PRB.getWidth(), PRB.getCenter().getY() - PRB.getHeight(), PRB.getCenter().getX() + PRB.getWidth(), PRB.getCenter().getY() + PRB.getHeight(), paint); //PRB

                    break;
                case 3:
                    paint.setColor(Color.RED);
                    canvas.drawOval(PRB.getCenter().getX() - PRB.getWidth(), PRB.getCenter().getY() - PRB.getHeight(), PRB.getCenter().getX() + PRB.getWidth(), PRB.getCenter().getY()+PRB.getHeight(), paint); //PRB

                    break;
            }










        }
    }

}

