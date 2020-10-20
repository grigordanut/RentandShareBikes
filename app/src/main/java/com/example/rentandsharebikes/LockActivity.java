package com.example.rentandsharebikes;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class LockActivity extends AppCompatActivity {

    private static final String TAG = "Bike Lock";
    //private String mac = "B4A828044186";  //Replace the MAC with the corresponding lock
    private String mac = "C4A8280D5494";  //Replace the MAC with the corresponding lock

    public class DataClass {
        public BluetoothDevice device = null;
        public String name;
        public Integer rssi = 0;
        public int count = 0;
        public String address;
    }

    private DataClass m_myData = new DataClass();
    BluetoothAdapter mBluetoothAdapter;
    BluetoothGattCharacteristic writeCharacteristic;
    BluetoothGattCharacteristic readCharacteristic;
    BluetoothGatt mBluetoothGatt;


    byte[] key = {32, 87, 47, 82, 54, 75, 63, 71, 48, 80, 65, 88, 17, 99, 45, 43};

    public static final ParcelUuid findServerUUID = ParcelUuid.fromString("0000fee7-0000-1000-8000-00805f9b34fb");
    public static final UUID OAD_SERVICE_UUID = UUID.fromString("f000ffc0-0451-4000-b000-000000000000");

    public static final UUID bltServerUUID = UUID.fromString("0000fee7-0000-1000-8000-00805f9b34fb");
    public static final UUID readDataUUID = UUID.fromString("000036f6-0000-1000-8000-00805f9b34fb");

    public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final UUID writeDataUUID = UUID.fromString("000036f5-0000-1000-8000-00805f9b34fb");

    byte[] token = new byte[4];

    byte[] gettoken = {0x06, 0x01, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    volatile String m_nowMac = "";

    private TextView info;
    private TextView info_state;
    private TextView str_inhex;
    private TextView str_outhex;
    private Button buttonConnection;
    private Button buttonDisConnection;
    private Button buttonDown;

    private Button btnLockBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        Log.d("TAG", "inside lock");

        initView();
        initEvent();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Your device does not support bluetooth 4.0", Toast.LENGTH_SHORT).show();
            finish();
        }

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Failed to acquire bluetooth", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 188);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        } else {
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {//The three parameters are the request code with the same custom, the permission array, the authorization result, and the permission array
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            //PERMISSION_GRANTED代表授权，PERMISSION_DENIED代表拒绝授权
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        }
    }

    public void SendData(byte[] data) {

        byte[] miwen = Encrypt(data, key);
        if (miwen != null) {
            writeCharacteristic.setValue(miwen);
            mBluetoothGatt.writeCharacteristic(writeCharacteristic);

            String hexString = bytesToHexString(data);
            Message m_hex = m_myHandler.obtainMessage(9, 1, 1, hexString);
            m_myHandler.sendMessage(m_hex);

        }
    }

    // encryption
    public byte[] Encrypt(byte[] sSrc, byte[] sKey) {

        try {
            SecretKeySpec skeySpec = new SecretKeySpec(sKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");//"Algorithm/mode/complement mode"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc);

            return encrypted;//BASE64 is used for transcoding, and it can be used to encrypt two times.
        } catch (Exception ex) {
            return null;
        }
    }

    // decryption
    public byte[] Decrypt(byte[] sSrc, byte[] sKey) {

        try {
            SecretKeySpec skeySpec = new SecretKeySpec(sKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] dncrypted = cipher.doFinal(sSrc);
            return dncrypted;

        } catch (Exception ex) {
            return null;
        }
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private void dispNotFindDeviceToast() {
        Toast toast = Toast.makeText(LockActivity.this, "No equipment",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    View.OnClickListener onCheckedConnectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (m_myData.device == null) {
                dispNotFindDeviceToast();
                return;
            }

            mBluetoothAdapter.stopLeScan(mLeScanCallback);

            if (mBluetoothGatt == null) {
                mBluetoothGatt = m_myData.device.connectGatt(LockActivity.this, false, mGattCallback);
            }
        }
    };

    View.OnClickListener onCheckedDisConnectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (m_myData.device == null) {
                dispNotFindDeviceToast();
                return;
            }

            if (mBluetoothGatt != null) {
                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
                mBluetoothGatt = null;
                m_myHandler.sendEmptyMessage(6);
            }
        }
    };

    View.OnClickListener onCheckedRunDownListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (m_myData.device == null) {
                dispNotFindDeviceToast();
                return;
            }

            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            if (mBluetoothGatt != null && writeCharacteristic != null) {
                if (mBluetoothGatt.getDevice().getAddress().equals(m_myData.address)) {
                    byte[] downLock = {0x05, 0x01, 0x06, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, token[0], token[1], token[2], token[3], 0x00, 0x00, 0x00};
                    SendData(downLock);
                }
            }
        }
    };

    private void initEvent() {

        buttonConnection.setOnClickListener(onCheckedConnectListener);
        buttonDisConnection.setOnClickListener(onCheckedDisConnectListener);
        buttonDown.setOnClickListener(onCheckedRunDownListener);

        btnLockBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        info = (TextView) findViewById(R.id.text_info);
        info_state = (TextView) findViewById(R.id.text_state);
        str_inhex = (TextView) findViewById(R.id.text_inhex);
        str_outhex = (TextView) findViewById(R.id.text_outhex);

        buttonConnection = (Button) findViewById(R.id.button_connection);
        buttonDisConnection = (Button) findViewById(R.id.button_disconnection);
        buttonDown = (Button) findViewById(R.id.button_down);
        btnLockBack = (Button) findViewById(R.id.btnLockBack);
    }

    Handler m_myHandler = new Handler(new Handler.Callback() {


        @Override
        public boolean handleMessage(Message mes) {
            switch (mes.what) {
                case 1: {
                    //Unlock successfully 开锁成功
                    m_myData.count++;
                    info.setText("Equipment name:" + m_myData.name + "\r\n" + "Signal strength:" + m_myData.rssi + "\r\n" + "Operation frequency:" + m_myData.count + "\r\n" + "Bluetooth address:" + m_myData.address);


                    Toast toast = Toast.makeText(LockActivity.this, "successful",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
                }
                case 3: {
                    info.setText("Equipment name:" + m_myData.name + "\r\n" + "Signal strength:" + m_myData.rssi + "\r\n" + "Operation frequency:" + m_myData.count + "\r\n" + "Bluetooth address:" + m_myData.address);
                    break;
                }
                case 4: {
                    info.setText((String) mes.obj);
                    break;
                }

                case 5: {
                    info_state.setText("The connection");
                    break;
                }
                case 6: {
                    info_state.setText("Disconnect the");
                    break;
                }
                case 8: {
                    str_outhex.setText((String) mes.obj);
                    break;
                }

                case 9: {
                    str_inhex.setText((String) mes.obj);
                    Log.d("case9", mes.obj+"");
                    break;
                }
                default:
                    break;
            }
            return false;
        }
    });

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {


                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

                    if (device.getAddress().replace(":", "").equals(mac)) {

                        String nowAddress = device.getAddress();

                        m_myData.device = device;
                        m_myData.name = device.getName();
                        m_myData.address = nowAddress;
                        m_myData.rssi = rssi;
                        m_myData.count = 0;
                        m_myHandler.sendEmptyMessage(3);

                        mBluetoothAdapter.stopLeScan(mLeScanCallback);

                    }
                }
            };

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            Log.w("TAG", "onConnectionStateChange");

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
                m_myHandler.sendEmptyMessage(5);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
                mBluetoothGatt = null;
                m_myHandler.sendEmptyMessage(6);
                m_myData.count = 0;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            Log.w("TAG", "onServicesDiscovered");

            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(bltServerUUID);
                readCharacteristic = service.getCharacteristic(readDataUUID);
                writeCharacteristic = service.getCharacteristic(writeDataUUID);

                gatt.setCharacteristicNotification(readCharacteristic, true);

                BluetoothGattDescriptor descriptor = readCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);

            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);

            SendData(gettoken);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            byte[] values = characteristic.getValue();
            byte[] x = new byte[16];
            System.arraycopy(values, 0, x, 0, 16);
            byte[] mingwen = Decrypt(x, key);

            String hexString = bytesToHexString(mingwen);
            Message m_hex = m_myHandler.obtainMessage(8, 1, 1, hexString);
            m_myHandler.sendMessage(m_hex);

            if (mingwen != null && mingwen.length == 16) {
                if (mingwen[0] == 0x06 && mingwen[1] == 0x02) {
                    token[0] = mingwen[3];
                    token[1] = mingwen[4];
                    token[2] = mingwen[5];
                    token[3] = mingwen[6];
                } else if (mingwen[0] == 0x05 && mingwen[1] == 0x02) {
                    if (mingwen[3] == 0x00) {   //Unlock success
                        Message msg = m_myHandler.obtainMessage(1, 1, 1,
                                gatt.getDevice().getAddress());
                        m_myHandler.sendMessage(msg);
                    } else {//The lock failure
                        Message msg = m_myHandler.obtainMessage(2, 1, 1,
                                "failure");
                        m_myHandler.sendMessage(msg);
                    }
                } else if (mingwen[0] == 0x05 && mingwen[1] == 0x08) {
                    if (mingwen[3] == 0x00) { //You success
                        Message msg = m_myHandler.obtainMessage(1, 1, 1,
                                gatt.getDevice().getAddress());
                        m_myHandler.sendMessage(msg);
                    } else {  //You failed
                        Message msg = m_myHandler.obtainMessage(2, 1, 1,
                                "failure");
                        m_myHandler.sendMessage(msg);
                    }
                }
            }
        }
    };
}