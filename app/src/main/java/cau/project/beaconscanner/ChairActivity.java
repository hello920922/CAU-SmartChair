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
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
 * Created by Jaewon Lee on 2016-02-18.
 */
public class ChairActivity extends AppCompatActivity {
    private BLEConnector bleConnector;
    private TextView uuid;
    private TextView major;
    private TextView minor;
    private Intent intent;


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
		setContentView(R.layout.activity_chair);
        intent = getIntent();



        final ChairView m = new ChairView(this);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        frameLayout.addView(m);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.chair, options);
        Bitmap b1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        ImageView im = (ImageView)findViewById(R.id.imageView);
        im.setImageBitmap(b1);


        uuid = (TextView)findViewById(R.id.uuid);
        major = (TextView)findViewById(R.id.major);
        minor = (TextView)findViewById(R.id.minor);



        bleConnector = new BLEConnector(this) {
            @Override
            protected void discoveryAvailableDevice(final BluetoothDevice bluetoothDevice, final int rssi, final BeaconRecord record) {

                if(bluetoothDevice.getAddress().equals(intent.getStringExtra("Address"))){
                    bleConnector.connectDevice(bluetoothDevice);
                    uuid.setText(record.getUuid().toString());
                    major.setText(String.valueOf(record.getMajor()));
                    minor.setText(String.valueOf(record.getMinor()));


                }
                else{
                    uuid.setText("x");
                    major.setText("x");
                    minor.setText("x");


                }

            }

            @Override
            public void readHandler(byte[] data) {
            }
        };


        bleConnector.startDiscovery();

        


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
        private String code = "-1";
        Paint paint = new Paint();

        float pOvalWidth = 100f;
        float pOvalHeight = 50f;
        float tOvalWidth = 200f;
        float tOvalHeight = 60f;
        Oval PRB = new Oval(new Center(540f,1060f), pOvalWidth, pOvalHeight);
        Oval PRF = new Oval(new Center(500f,1270f), pOvalWidth, pOvalHeight);
        Oval PLB = new Oval(new Center(945f,1050f), pOvalWidth, pOvalHeight);
        Oval PLF = new Oval(new Center(995f,1270f), pOvalWidth, pOvalHeight);
        Oval LT = new Oval(new Center(1040f,1420f), tOvalWidth, tOvalHeight);
        Oval RT = new Oval(new Center(480f, 1420f), tOvalWidth, tOvalHeight);




        public ChairView(Context context){
            super(context);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(6F);


        }

        public void setCode(String str){
            code = str;

        }
        @Override
        protected void onDraw(Canvas canvas){


            switch(code){
                case "-1":
                    paint.setColor(Color.RED);


                    canvas.drawOval(PRB.getCenter().getX() - PRB.getWidth(), PRB.getCenter().getY() - PRB.getHeight(), PRB.getCenter().getX() + PRB.getWidth(), PRB.getCenter().getY() + PRB.getHeight(), paint); //PRB
                    canvas.drawOval(PRF.getCenter().getX()-PRF.getWidth(), PRF.getCenter().getY()-PRF.getHeight(), PRF.getCenter().getX()+PRF.getWidth(), PRF.getCenter().getY()+PRF.getHeight(), paint);    //PRF
                    canvas.drawOval(PLB.getCenter().getX()-PLB.getWidth(), PLB.getCenter().getY()-PLB.getHeight(), PLB.getCenter().getX()+PLB.getWidth(), PLB.getCenter().getY()+PLB.getHeight(), paint);   //PLB
                    canvas.drawOval(PLF.getCenter().getX()-PLF.getWidth(), PLF.getCenter().getY()-PLF.getHeight(), PLF.getCenter().getX()+PLF.getWidth(), PLF.getCenter().getY()+PLF.getHeight(), paint); //PLF

                    paint.setColor(Color.BLUE);

                    //canvas.rotate(15,RT.getCenter().getX(), RT.getCenter().getY());
                    canvas.drawOval(RT.getCenter().getX() - RT.getWidth(), RT.getCenter().getY() - RT.getHeight(), RT.getCenter().getX() + RT.getWidth(), RT.getCenter().getY() + RT.getHeight(), paint);
                    //canvas.restore();
                    //canvas.rotate(-15,LT.getCenter().getX(), LT.getCenter().getY());
                    canvas.drawOval(LT.getCenter().getX() - LT.getWidth(), LT.getCenter().getY() - LT.getHeight(), LT.getCenter().getX() + LT.getWidth(), LT.getCenter().getY() + LT.getHeight(), paint);
                    //canvas.restore();
                    break;


                default:
                    paint.setColor(Color.RED);

                    canvas.drawOval(PRB.getCenter().getX() - PRB.getWidth(), PRB.getCenter().getY() - PRB.getHeight(), PRB.getCenter().getX() + PRB.getWidth(), PRB.getCenter().getY() + PRB.getHeight(), paint); //PRB
                    canvas.drawOval(PRF.getCenter().getX()-PRF.getWidth(), PRF.getCenter().getY()-PRF.getHeight(), PRF.getCenter().getX()+PRF.getWidth(), PRF.getCenter().getY()+PRF.getHeight(), paint);    //PRF
                    canvas.drawOval(PLB.getCenter().getX()-PLB.getWidth(), PLB.getCenter().getY()-PLB.getHeight(), PLB.getCenter().getX()+PLB.getWidth(), PLB.getCenter().getY()+PLB.getHeight(), paint);   //PLB
                    canvas.drawOval(PLF.getCenter().getX()-PLF.getWidth(), PLF.getCenter().getY()-PLF.getHeight(), PLF.getCenter().getX()+PLF.getWidth(), PLF.getCenter().getY()+PLF.getHeight(), paint); //PLF

            }


        }
    }

}

