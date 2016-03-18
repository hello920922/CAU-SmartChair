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


    private TextView dataField;
    private Intent intent;
    private StringBuilder log = new StringBuilder();
    private int REQUEST_CHAIR = 1001;

    private BLEConnector bleConnector = new BLEConnector(this) {
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

        @Override
        public void readHandler(byte[] data) {


            dataField.setText("Data  : " + data);
            log.append(makeLog(data));




        }
    };





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


        dataField = (TextView)findViewById(R.id.data);


        final File dir = new File(Environment.getExternalStorageDirectory(), "BeaconScanner/");
        Log.d("FILE", dir.getAbsolutePath());
        if(!dir.exists())
            try {
                dir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }




        bleConnector.startDiscovery();

        


    }

    public void onLogButtonClicked(View v){
        Intent newIntent = new Intent(ChairActivity.this, LogActivity.class);

        newIntent.putExtra("Log", log.toString());
        newIntent.putExtra("Address", intent.getStringExtra("Address"));
        bleConnector.disconnect();
        bleConnector.stopDiscovery();
        startActivityForResult(newIntent, REQUEST_CHAIR);




    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        log.delete(0,log.length());
        log.append(data.getAction());
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

        float pOvalWidth = 50f;
        float pOvalHeight = 20f;

        Oval PRB = new Oval(new Center(530f,1000f), pOvalWidth, pOvalHeight);
        Oval PRF = new Oval(new Center(500f,1075f), pOvalWidth, pOvalHeight);
        Oval PLB = new Oval(new Center(935f,1000f), pOvalWidth, pOvalHeight);
        Oval PLF = new Oval(new Center(985f,1075f), pOvalWidth, pOvalHeight);





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
                    canvas.drawOval(PLF.getCenter().getX() - PLF.getWidth(), PLF.getCenter().getY() - PLF.getHeight(), PLF.getCenter().getX() + PLF.getWidth(), PLF.getCenter().getY()+PLF.getHeight(), paint); //PLF



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

