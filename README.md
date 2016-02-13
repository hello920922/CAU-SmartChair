# CAU-SmartChair API Guide

## BLEConnector Class
> ### The class about bluetooth api. You can use followed methods
    
> #### The abstract methods
    @Override
    protected void discoveryAvailableDevice(final BluetoothDevice bluetoothDevice, final int rssi, final BeaconRecord record)
      - Define what you want when an available bluetooth device is discovered.
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
