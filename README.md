# CAU-SmartChair API Guide

## BLEConnector Class
### The class about bluetooth api. Users can use followed methods
    
> #### The abstract functions
    @Override
    protected void discoveryAvailableDevice(final BluetoothDevice bluetoothDevice, final int rssi, final BeaconRecord record)
      - Define what you want when an available bluetooth device is discovered.
    
>
    @Override
    public void readHandler(byte[] data)
      - Define what you want when the connected device read message.
      
> #### The usable functions
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
