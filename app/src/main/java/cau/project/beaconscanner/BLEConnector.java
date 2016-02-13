package cau.project.beaconscanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Mingyu Park on 2016-01-11.
 */
public abstract class BLEConnector extends Handler{
    // Define constant to process handler message
    public static final int BLUETOOTH_READ_MESSAGE = 3;
    public static final int BLUETOOTH_WRITE_MESSAGE = 4;
    public static final int BLUETOOTH_DISCOVERY_MESSAGE = 0;
    public static final int BLUETOOTH_CONNECT_MESSAGE = 1;
    public static final int BLUETOOTH_NOT_SUPPORT_MESSAGE = 2;

    public static final int WRITE_TYPE_DEFAULT = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
    public static final int WRITE_TYPE_NO_RESPONSE = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;
    public static final int WRITE_TYPE_SIGNED = BluetoothGattCharacteristic.WRITE_TYPE_SIGNED;

    public static final int STATE_WAITING = 0;
    public static final int STATE_SCANNING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_DISCONNECTING = 4;

    // Define constant to process intent
    public static final int REQUEST_ENABLE_BT = 0;

    // Define constant to log message
    private final String LOGTAG = "BLEConnector";

    // Define variables to processing
    private Context context;

    // Define variables to BLE
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanCallback mScanCallback;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private int state;
    private int failCount;

    // variables to process callback function
    private BluetoothGattCallback mBluetoothGattCallback;
    private BluetoothGatt mDefaultBluetoothGatt;
    private BluetoothGattCharacteristic mDefaultBluetoothGattCharacteristic;

    // variable to send or receive
    private byte[] data;

    // Initialize variables to processing
    public BLEConnector(Context context) {
        this.context = context;

        this.state = STATE_WAITING;
        this.failCount = 0;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            mScanCallback = makeScanCallback();
            mLeScanCallback = null;
        }
        else {
            mBluetoothLeScanner = null;
            mLeScanCallback = makeLeScanCallback();
            mScanCallback = null;
        }
        mBluetoothGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    state = STATE_CONNECTED;
                    Log.d(LOGTAG, "Connected");
                    failCount = 0;
                    gatt.discoverServices();
                }
                else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                    Log.d(LOGTAG, "Disconnected");
                    if(state == STATE_CONNECTING && failCount < 4) {
                        Log.d(LOGTAG, "Retry to connect");
                        failCount ++;
                        connectDevice(gatt.getDevice());
                    }else {
                        failCount = 0;
                        state = STATE_WAITING;
                    }
                }
                else if (newState == BluetoothProfile.STATE_CONNECTING){
                    state = STATE_CONNECTING;
                    Log.d(LOGTAG, "Connecting...");
                }
                else if (newState == BluetoothProfile.STATE_DISCONNECTING){
                    state = STATE_DISCONNECTING;
                    Log.d(LOGTAG, "Disconnecting...");
                }
                else if (newState == BluetoothGatt.GATT_FAILURE){
                    state = STATE_WAITING;
                    Log.d(LOGTAG, "Failure");
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                List<BluetoothGattService> gattServices = gatt.getServices();
                for (BluetoothGattService gattService : gattServices) {
                    List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                        if (isWritableCharacteristic(gattCharacteristic) && isReadableCharacteristic(gattCharacteristic) && isNotificationCharacteristic(gattCharacteristic)) {
                            gatt.setCharacteristicNotification(gattCharacteristic, true);
                            mDefaultBluetoothGatt = gatt;
                            mDefaultBluetoothGattCharacteristic = gattCharacteristic;
                            Log.d(LOGTAG, "Available service uuid : " + gattService.getUuid());
                            Log.d(LOGTAG, "Available characteristic uuid : " + gattCharacteristic.getUuid());
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS)
                    Log.d(LOGTAG, "Success to read");
                else if(status == BluetoothGatt.GATT_FAILURE)
                    Log.d(LOGTAG, "Fail to read");
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS)
                    Log.d(LOGTAG, "Success to write");
                else if(status == BluetoothGatt.GATT_FAILURE)
                    Log.d(LOGTAG, "Fail to write");
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                byte[] readData = characteristic.getValue();
                if (readData == null || readData.length <= 0) {
                    return;
                }
                data = readData;
                Log.d(LOGTAG, "Read message : " + new String(data));
                sendEmptyMessage(BLUETOOTH_READ_MESSAGE);
            }
        };
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case BLUETOOTH_READ_MESSAGE :
                readHandler(data);
                break;
            case BLUETOOTH_WRITE_MESSAGE :
                writeHandler((byte[])msg.obj, msg.arg1);
                break;
            case BLUETOOTH_DISCOVERY_MESSAGE :
                Object[] objs = (Object[])msg.obj;
                discoveryAvailableDevice((BluetoothDevice)objs[0], msg.arg1, new BeaconRecord((byte[])objs[1]));
                break;
            case BLUETOOTH_CONNECT_MESSAGE :
                connectHandler((BluetoothDevice) msg.obj);
                break;
            case BLUETOOTH_NOT_SUPPORT_MESSAGE:
                notSupportBluetooth();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback makeScanCallback() {
        return new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.d(LOGTAG, "Found device : " + result.getDevice().getName() + "\t(" + result.getDevice().getAddress() + ")");

                    Object[] objs = new Object[2];
                    objs[0] = result.getDevice();
                    objs[1] = result.getScanRecord().getBytes();

                    Message msg = Message.obtain();
                    msg.obj = objs;
                    msg.what = BLUETOOTH_DISCOVERY_MESSAGE;
                    msg.arg1 = result.getRssi();

                    sendMessage(msg);
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                switch (errorCode) {
                    case ScanCallback.SCAN_FAILED_ALREADY_STARTED:
                        break;
                    case ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                        break;
                    case ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED:
                        break;
                    case ScanCallback.SCAN_FAILED_INTERNAL_ERROR:
                        break;
                }
            }
        };
    }

    private BluetoothAdapter.LeScanCallback makeLeScanCallback(){
        return new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                Log.d(LOGTAG, "Found device : " + device.getName() + "\t(" + device.getAddress() + ")");

                Object[] objs = new Object[2];
                objs[0] = device;
                objs[1] = scanRecord;

                Message msg = Message.obtain();
                msg.obj = objs;
                msg.what = BLUETOOTH_DISCOVERY_MESSAGE;
                msg.arg1 = rssi;

                sendMessage(msg);
            }
        };
    }

    class BeaconRecord{
        private String uuid;
        private int minor;
        private int major;

        public BeaconRecord(byte[] scanRecord) {
            int startByte = 2;
            boolean patternFound = false;
            while (startByte <= 5) {
                if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                        ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                    patternFound = true;
                    break;
                }
                startByte++;
            }

            if (patternFound) {
                //Convert to hex String
                byte[] uuidBytes = new byte[16];
                System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                String hexString = bytesToHex(uuidBytes);

                //Here is your UUID
                uuid = hexString.substring(0, 8) + "-" +
                        hexString.substring(8, 12) + "-" +
                        hexString.substring(12, 16) + "-" +
                        hexString.substring(16, 20) + "-" +
                        hexString.substring(20, 32);

                //Here is your Major value
                major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);

                //Here is your Minor value
                minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);
            }
        }
        public String getUuid(){return uuid;}
        public int getMinor(){return minor;}
        public int getMajor(){return major;}
    }

    // API users should implement this method
    protected abstract void discoveryAvailableDevice(final BluetoothDevice bluetoothDevice, int rssi, BeaconRecord record);
    public abstract void readHandler(byte[] data);

    // After check whether the device support bluetooth and bluetooth state, start discovery
    public void startDiscovery() {
        this.state = STATE_SCANNING;
        if (mBluetoothAdapter == null) {
            Log.e(LOGTAG, "This device does not support bluetooth");
            this.sendEmptyMessage(BLUETOOTH_NOT_SUPPORT_MESSAGE);
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent btEnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if(context instanceof AppCompatActivity)
                ((AppCompatActivity)context).startActivityForResult(btEnIntent, REQUEST_ENABLE_BT);
            else
                context.startActivity(btEnIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){
            // Check API Level
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                Log.d(LOGTAG, "This SDK version is 5.0 or higher");
                mBluetoothLeScanner.startScan(mScanCallback);
            }
            else{
                Log.d(LOGTAG, "This SDK version is less than 5.0");
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        }
    }
    public void stopDiscovery(){
        this.state = STATE_WAITING;
        if(mBluetoothAdapter != null)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                Log.d(LOGTAG, "This SDK version is 5.0 or higher");
                mBluetoothLeScanner.stopScan(mScanCallback);
            }
            else{
                Log.d(LOGTAG, "This SDK version is less than 5.0");
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
    }

    // Cancel discovery or disconnect
    public void disconnect() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.stopLeScan(new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

                }
            });
        }
        if (mDefaultBluetoothGattCharacteristic != null)
            mDefaultBluetoothGattCharacteristic = null;
        if (mDefaultBluetoothGatt != null)
            mDefaultBluetoothGatt.disconnect();
        this.state = STATE_WAITING;
    }

    // If device does not support bluetooth, this method is executed
    protected void notSupportBluetooth(){
        Toast.makeText(context, "This device does not support bluetooth", Toast.LENGTH_SHORT).show();
    }

    // Users can reimplement this method
    protected void discoveryStartAction() {
        Log.d(LOGTAG, "Discovery start");
    }

    private void foundAction(BluetoothDevice bluetoothDevice) {
        String deviceName = bluetoothDevice.getName();
        String deviceAddr = bluetoothDevice.getAddress();
        Log.d(LOGTAG, "Found device : " + deviceName + "\t(" + deviceAddr + ")");

        Message msg = Message.obtain();
        msg.what = BLUETOOTH_DISCOVERY_MESSAGE;
        msg.obj = bluetoothDevice;
        this.sendMessage(msg);
    }

    // Users can reimplement this method
    protected void discoveryFinishAction() {
        Log.d(LOGTAG, "Discovery finish");
        stopDiscovery();
    }

    public void connectDevice(String deviceAddress) {
        connectDevice(mBluetoothAdapter.getRemoteDevice(deviceAddress));
    }

    public void connectDevice(BluetoothDevice bluetoothDevice) {
        Message msg = Message.obtain();
        msg.what = BLUETOOTH_CONNECT_MESSAGE;
        msg.obj = bluetoothDevice;
        sendMessage(msg);
    }

    // Connect with BLE device using handler
    private void connectHandler(BluetoothDevice bluetoothDevice){
        stopDiscovery();
        if(mDefaultBluetoothGatt != null)
            mDefaultBluetoothGatt.disconnect();
        mDefaultBluetoothGatt = null;
        mDefaultBluetoothGattCharacteristic = null;
        bluetoothDevice.connectGatt(context, false, mBluetoothGattCallback);
    }

    // Write to BLE device using handler
    private void writeHandler(byte[] message, int writeType){
        mDefaultBluetoothGattCharacteristic.setValue(message);
        mDefaultBluetoothGattCharacteristic.setWriteType(writeType);
        mDefaultBluetoothGatt.writeCharacteristic(mDefaultBluetoothGattCharacteristic);
        Log.d(LOGTAG, "Write message : " + new String(message));
    }

    // Default write method
    public void writeMessage(byte[] message){
        Message msg = Message.obtain();
        msg.what = BLUETOOTH_WRITE_MESSAGE;
        msg.obj = message;
        msg.arg1 = WRITE_TYPE_DEFAULT;

        sendMessage(msg);
    }

    // Write method with write type
    public void writeMessage(byte[] message, int writeType){
        Message msg = Message.obtain();
        msg.what = BLUETOOTH_WRITE_MESSAGE;
        msg.obj = message;
        msg.arg1 = writeType;

        sendMessage(msg);
    }

    // If Activity is changed, users can change handle activity using this method
    public void changeContext(Context context){
        this.context = context;
    }

    // Return this object's target context
    public Context getContext(){return context;}

    // Return bluetooth adapter's state
    public int getState(){return state;}

    // Parsing byte array to Hex String
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    // Check the characteristic's writable
    private boolean isWritableCharacteristic(BluetoothGattCharacteristic chr) {
        if(chr == null) return false;

        final int charaProp = chr.getProperties();
        if (((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) |
                (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) > 0) {
            return true;
        } else {
            return false;
        }
    }

    // Check the characteristic's readable
    private boolean isReadableCharacteristic(BluetoothGattCharacteristic chr) {
        if(chr == null) return false;

        final int charaProp = chr.getProperties();
        if((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            return true;
        } else {
            return false;
        }
    }

    // Check the characteristic's notification
    private boolean isNotificationCharacteristic(BluetoothGattCharacteristic chr) {
        if(chr == null) return false;

        final int charaProp = chr.getProperties();
        if((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            return true;
        } else {
            return false;
        }
    }
}
