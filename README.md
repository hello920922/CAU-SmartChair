# CAU-SmartChair API Guide

## BLEConnector Class
> ### The class about bluetooth api. You can use followed methods
    
> #### The abstract methods
    @Override
    protected void discoveryAvailableDevice(final BluetoothDevice bluetoothDevice, final int rssi, final BeaconRecord record)
      - Define what you want when an available is discovered.
        (Available device means a bluetooth device including readable, writable, notification characteristic.)
>
    @Override
    public void readHandler(byte[] data)
      - Define what you want when the connected device read message.
      
> #### The usable methods
    public void startDiscovery()
      - Start discovering bluetooth devices
>    
    public void stopDiscovery()
      - Stop discovering bluetooth devices
>
    public void connectDevice(BluetoothDevice bluetoothDevice)
    public void connectDevice(String deviceAddress)
      - Connect with the target device
>
    public void disconnect()
      - Disconnect with the connected device
>
    public void writeMessage(byte[] message)
    public void writeMessage(byte[] message, int writeType)
      - Send data to the connected device
      
> #### The usable variables
    public static final int WRITE_TYPE_DEFAULT = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
    public static final int WRITE_TYPE_NO_RESPONSE = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;
    public static final int WRITE_TYPE_SIGNED = BluetoothGattCharacteristic.WRITE_TYPE_SIGNED;
      - Write type variables for method 'writeMessage(byte[] message, int writeType)'
>
    public static final int STATE_WAITING = 0;
    public static final int STATE_SCANNING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_DISCONNECTING = 4;
      - State variables for knowing current state of android bluetooth

## BeaconRecord Class
> ### The class about beacon information.
> #### When the device is discovered, you can take an object of this class.
> ##### (It is recommended with the abstarct mothod 'discoveryAvailableDevice' in the BLEConnector class.)

> #### The usable methods
    public String getUuid()
      - Get the discovered Beacon's uuid
>      
    public int getMinor()
      - Get the discovered Beacon's minor value
>
    public int getMajor()
      - Get the discovered Beacon's major value
