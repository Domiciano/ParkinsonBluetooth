package i2t.icesi.parkinsonbluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.UUID;

/**
 * Created by Domiciano on 25/04/2018.
 */

public class GattCallbackLeftHigher extends BluetoothGattCallback {

    public final static UUID CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public final static UUID RX = UUID.fromString("2d30c082-f39f-4ce6-923f-3484ea480596");
    public final static UUID SERVICE_RX = UUID.fromString("0000fe84-0000-1000-8000-00805f9b34fb");
    public static final UUID TX = UUID.fromString("2d30c083-f39f-4ce6-923f-3484ea480596");

    public ReadListener readListener;
    public BluetoothGatt gatt = null;
    BluetoothGattCharacteristic tx = null;
    BluetoothGattCharacteristic rx = null;

    private boolean mtuConfirmed = false;
    private boolean disconnecting = false;

    public void disconnect() {
        if(gatt != null){
            gatt.disconnect();
        }
        gatt = null;
        tx = null;
        rx = null;
    }

    public void close(){
        if(gatt != null){
            gatt.disconnect();
            gatt.close();
            gatt = null;
        }
    }

    interface ReadListener {
        void onDataReceivedLeftHigher(GattCallbackLeftHigher obj, byte[] data);
        void onConnectedLeftHigher(GattCallbackLeftHigher obj);
        void onDisconectedLeftHigher(GattCallbackLeftHigher gatt);
    }

    public void setReadListener(ReadListener readListener) {
        this.readListener = readListener;
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        //String msj = characteristic.getStringValue(0);
        byte[] msj = characteristic.getValue();
        readListener.onDataReceivedLeftHigher(this, msj);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        Log.e("parkinson", "Characteristic Write");
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        Log.e("parkinson", "STATE: "+newState);
        if (newState == BluetoothGatt.STATE_CONNECTED) {
            if(!disconnecting) gatt.discoverServices();
        }else if(newState == BluetoothGatt.STATE_DISCONNECTED){
            gatt.disconnect();
            Thread h = new Thread(){
                @Override
                public void run() {
                    try {
                        disconnecting = true;
                        Thread.sleep(5000);
                        disconnecting = false;
                        readListener.onDisconectedLeftHigher(GattCallbackLeftHigher.this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            h.start();
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
        Log.e("parkinson", "Descriptor Read");
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        Log.e("parkinson", "Descriptor Write: " + status);

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    while (!mtuConfirmed) {
                        Log.e("parkinson", "Intentando cambiar MTU...");
                        GattCallback.this.gatt.requestMtu(50);
                        Thread.sleep(1000);
                    }
                    Log.e("parkinson", "Operación exitosa");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        */
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
        mtuConfirmed = true;
        Log.e("parkinson", "MTU Changed: " + status);
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
        Log.e("parkinson", "Read Remote RSSI");
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        super.onReliableWriteCompleted(gatt, status);
        Log.e("parkinson", "Reliable Write Completed");
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        this.gatt = gatt;
        Log.e("parkinson", "Iniciando mapping...");
        for (int i = 0; i < gatt.getServices().size(); i++) {
            Log.e("parkinson", "SER->" + gatt.getServices().get(i).getUuid());
            for (int j = 0; j < gatt.getServices().get(i).getCharacteristics().size(); j++) {
                BluetoothGattCharacteristic charac = gatt.getServices().get(i).getCharacteristics().get(j);
                Log.e("parkinson", "     CHAR ->" + charac.getUuid().toString());
                for (int k = 0; k < charac.getDescriptors().size(); k++) {
                    BluetoothGattDescriptor desc = charac.getDescriptors().get(k);
                    Log.e("parkinson", "          DESC ->" + desc.getUuid());
                }
            }
        }
        Log.e("parkinson", "El mapping finalizó");
        readListener.onConnectedLeftHigher(this);


        BluetoothGattService service = gatt.getService(SERVICE_RX);
        rx = service.getCharacteristic(RX);
        BluetoothGattDescriptor desc = rx.getDescriptor(CLIENT_UUID);
        registrarDescriptor(gatt, rx, desc, true);

    }



    private void registrarDescriptor(BluetoothGatt gatt, BluetoothGattCharacteristic charac, BluetoothGattDescriptor descriptor, boolean enabled) {
        gatt.setCharacteristicNotification(charac, true);
        byte[] data = enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
        Log.e("parkinson", "" + descriptor);
        if (descriptor.setValue(data)) {
            gatt.writeDescriptor(descriptor);
        }


    }
    public void enviar(String s) {
        try {
            tx = gatt.getService(SERVICE_RX).getCharacteristic(TX);
            tx.setValue(s.getBytes());
            gatt.writeCharacteristic(tx);
        }catch (Exception e){
            Log.e(">>>",""+e.getLocalizedMessage());
        }
    }
}