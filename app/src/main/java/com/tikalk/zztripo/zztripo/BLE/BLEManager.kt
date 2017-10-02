package com.tikalk.zztripo.zztripo.BLE

import android.app.Activity
import android.bluetooth.*
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.content.Intent
import java.util.*
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.*
import android.os.ParcelUuid
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothGattServerCallback
import com.tikalk.zztripo.zztripo.logics.MessageProvider
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanFilter
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


/**
 * Created by shaulr on 02/10/2017.
 */
class BLEManager {

    lateinit var context: Context
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var bluetoothManager: BluetoothManager
    private var bluetoothGatt: BluetoothGatt? = null
    private var connectionState = STATE_DISCONNECTED
    lateinit var bluetoothLeAdvertiser: BluetoothLeAdvertiser
    val REQUEST_ENABLE_BT = 123
    val devices = ArrayList<BluetoothDevice>()
    private var bluetoothDeviceAddress: String? = null
    private val registeredDevices = HashSet<BluetoothDevice>()
     var bluetoothCharacteristic: BluetoothGattCharacteristic? = null
    lateinit var bleCallback : IBleCallback

    companion object {


        private val TAG = BLEManager::class.java.simpleName

        private val STATE_DISCONNECTED = 0
        private val STATE_CONNECTING = 1
        private val STATE_CONNECTED = 2

        val ACTION_GATT_CONNECTED = "com.tikalk.zztripo.ACTION_GATT_CONNECTED"
        val ACTION_GATT_DISCONNECTED = "com.tikalk.zztripo.ACTION_GATT_DISCONNECTED"
        val ACTION_GATT_SERVICES_DISCOVERED = "com.tikalk.zztripo.ACTION_GATT_SERVICES_DISCOVERED"
        val ACTION_DATA_AVAILABLE = "com.tikalk.zztripo.ACTION_DATA_AVAILABLE"
        val EXTRA_DATA = "com.tikalk.zztripo.EXTRA_DATA"
        val TRIP_SERVICE = "00002a37-0000-1000-8000-00805f9b34fb"
        val TRIP_MESSAGE = "00002a2b-0000-1000-8000-00805f9b34fb"
        val CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"
        val UUID_TRIP_MEESSAGE = UUID.fromString(BLEManager.TRIP_MESSAGE)
        val UUID_TRIP_SERVICE = UUID.fromString(BLEManager.TRIP_SERVICE)
        val UUID_TRIP_CHARACTERISTIC_CONFIG = UUID.fromString(BLEManager.CLIENT_CHARACTERISTIC_CONFIG)

    }

    fun init(context: Context, bleCallback : IBleCallback): Boolean {
        this.context = context
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(TAG, "BLE not supported")
            return false
        }
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        if ( !bluetoothAdapter.isEnabled) {

        }
        this.bleCallback = bleCallback
        return true
    }

    fun isBluetoothEnabled(): Boolean {
        return ( !bluetoothAdapter.isEnabled)
    }


    fun startEnableBluetoothActivity(activity: Activity) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
    }


    fun startScan(timeout: Long, callback: IScanCallback) {
        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int,
                                      result: ScanResult) {
                if (!devices.contains(result.device))
                    devices.add(result.device)
                result.device.connectGatt(context, false, gattCallback)
                callback.onDeviceDiscovered(result.device)
                bluetoothAdapter.bluetoothLeScanner.stopScan(this)
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                if (results != null && results.size > 0) {
                    results.mapTo(devices) { it.device }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.d(TAG, "scan failed")
                callback.onError()
            }
        }
        val filters = ArrayList<ScanFilter>()
                val filter = ScanFilter.Builder().setServiceUuid(
                        ParcelUuid(UUID_TRIP_MEESSAGE
                        )).build()
                filters.add(filter)


        val settings = ScanSettings.Builder().build()



        bluetoothAdapter.bluetoothLeScanner.startScan(filters, settings, scanCallback)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
                callback.onDone()
            }
        }, timeout)

    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val intentAction: String
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED
                connectionState = STATE_CONNECTED
                broadcastUpdate(intentAction)
                Log.i(TAG, "Connected to GATT server.")
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" + gatt.discoverServices())

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED
                connectionState = STATE_DISCONNECTED
                Log.i(TAG, "Disconnected from GATT server.")
                broadcastUpdate(intentAction)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                gatt.connect()
                for(service in gatt.services) {
                    if(UUID_TRIP_SERVICE == service.uuid) {
                        bluetoothGatt = gatt
                        val characteristic = service.getCharacteristic(UUID_TRIP_MEESSAGE)
                        if (characteristic != null) {
                            bluetoothCharacteristic = characteristic
                            bleCallback.onConnectSuccesful()
                            gatt.setCharacteristicNotification(characteristic, true)
                            gatt.readCharacteristic(characteristic)

                        }
                    }
                }
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status)
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt,
                                          characteristic: BluetoothGattCharacteristic,
                                          status: Int) {
            Log.d(TAG, "onCharacteristicRead")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "onCharacteristicRead " )
                bleCallback.onMessage(characteristic.getStringValue(0))
                writeData(MessageProvider.instance.getPingJson())
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt,
                                             characteristic: BluetoothGattCharacteristic) {
            Log.d(TAG, "onCharacteristicChanged")

            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
        }
    }

    fun connect(address: String?): Boolean {
        if ( address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.")
            return false
        }

        // Previously connected device.  Try to reconnect.
        if (bluetoothDeviceAddress != null && address == bluetoothDeviceAddress
                && bluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.")
            if (bluetoothGatt!!.connect()) {
                connectionState = STATE_CONNECTING
                return true
            } else {
                return false
            }
        }

        val device = bluetoothAdapter.getRemoteDevice(address)
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.")
            return false
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
        Log.d(TAG, "Trying to create a new connection.")
        bluetoothDeviceAddress = address
        connectionState = STATE_CONNECTING
        return true
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * `BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)`
     * callback.
     */
    fun disconnect() {
        if ( bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        bluetoothGatt!!.disconnect()
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    fun close() {
        if (bluetoothGatt == null) {
            return
        }
        bluetoothGatt!!.close()
        bluetoothGatt = null
    }

    /**
     * Request a read on a given `BluetoothGattCharacteristic`. The read result is reported
     * asynchronously through the `BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)`
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        if ( bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        bluetoothGatt!!.readCharacteristic(characteristic)
    }

    fun setCharacteristicNotification(characteristic: BluetoothGattCharacteristic,
                                      enabled: Boolean) {
        if ( bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        bluetoothGatt!!.setCharacteristicNotification(characteristic, enabled)

        // This is specific to Heart Rate Measurement.
        if (UUID_TRIP_MEESSAGE == characteristic.uuid) {
            val descriptor = characteristic.getDescriptor(
                    UUID.fromString(BLEManager.CLIENT_CHARACTERISTIC_CONFIG))
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            bluetoothGatt!!.writeDescriptor(descriptor)
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after `BluetoothGatt#discoverServices()` completes successfully.
     *
     * @return A `List` of supported services.
     */
    val supportedGattServices: List<BluetoothGattService>?
        get() = if (bluetoothGatt == null) null else bluetoothGatt!!.services


    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic? = null) {

    }

    private val mBluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)

            when (state) {
                BluetoothAdapter.STATE_ON -> {
                    startAdvertising()
                    startServer()
                }
                BluetoothAdapter.STATE_OFF -> {
                    stopServer()
                    stopAdvertising()
                }
            }// Do nothing

        }
    }


    fun startAdvertising() {
        val bluetoothAdapter = bluetoothManager.adapter
        bluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser


        val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build()

        val data = AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(ParcelUuid(UUID_TRIP_MEESSAGE))
                .build()

        bluetoothLeAdvertiser
                .startAdvertising(settings, data, mAdvertiseCallback)
    }

    /**
     * Stop Bluetooth advertisements.
     */
    private fun stopAdvertising() {
        if (bluetoothLeAdvertiser != null)
            bluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback)
    }

     lateinit var bluetoothGattServer: BluetoothGattServer

    /**
     * Initialize the GATT server instance with the services/characteristics
     * from the Time Profile.
     */
    fun startServer() {
        bluetoothGattServer = bluetoothManager.openGattServer(context, gattServerCallback)


        bluetoothGattServer.addService(createTripService())

    }

    private fun createTripService(): BluetoothGattService {
        val service = BluetoothGattService(UUID_TRIP_SERVICE,
                BluetoothGattService.SERVICE_TYPE_PRIMARY)

        // Current Time characteristic
        val currentTime = BluetoothGattCharacteristic(UUID_TRIP_MEESSAGE,
                //Read-only characteristic, supports notifications
                BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ)
        val configDescriptor = BluetoothGattDescriptor(UUID_TRIP_CHARACTERISTIC_CONFIG,
                //Read/write descriptor
                BluetoothGattDescriptor.PERMISSION_READ or BluetoothGattDescriptor.PERMISSION_WRITE)
        currentTime.addDescriptor(configDescriptor)

        // Local Time Information characteristic
        val localTime = BluetoothGattCharacteristic(UUID_TRIP_MEESSAGE,
                //Read-only characteristic
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ)

        service.addCharacteristic(currentTime)
        service.addCharacteristic(localTime)

        return service
    }
    /**
     * Shut down the GATT server.
     */
    private fun stopServer() {

        bluetoothGattServer.close()
    }

    /**
     * Callback to receive information about the advertisement process.
     */
    private val mAdvertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Log.i(TAG, "LE Advertise Started.")
        }

        override fun onStartFailure(errorCode: Int) {
            Log.w(TAG, "LE Advertise Failed: " + errorCode)
        }
    }

    /**
     * Send a time service notification to any devices that are subscribed
     * to the characteristic.
     */
    private fun notifyRegisteredDevices(timestamp: Long, adjustReason: Byte) {
        if (registeredDevices.isEmpty()) {
            Log.i(TAG, "No subscribers registered")
            return
        }
//        val exactTime = TimeProfile.getExactTime(timestamp, adjustReason)
//
//        Log.i(TAG, "Sending update to " + mRegisteredDevices.size() + " subscribers")
//        for (device in mRegisteredDevices) {
//            val timeCharacteristic = mBluetoothGattServer
//                    .getService(TimeProfile.TIME_SERVICE)
//                    .getCharacteristic(TimeProfile.CURRENT_TIME)
//            timeCharacteristic.setValue(exactTime)
//            mBluetoothGattServer.notifyCharacteristicChanged(device, timeCharacteristic, false)
//        }
    }

    private val gattServerCallback = object : BluetoothGattServerCallback() {

        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothDevice CONNECTED: " + device)
                bleCallback.onDeviceConnected(device)

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "BluetoothDevice DISCONNECTED: " + device)
                //Remove device from any active subscriptions
                registeredDevices.remove(device)
            }
        }

        override fun onCharacteristicReadRequest(device: BluetoothDevice, requestId: Int, offset: Int,
                                                 characteristic: BluetoothGattCharacteristic) {
            val now = System.currentTimeMillis()
            Log.i(TAG, "Read characteristic")
            when {
                UUID_TRIP_MEESSAGE.equals(characteristic.uuid) -> {
                    Log.i(TAG, "Read TRIP_MEESSAGE")
                    bluetoothGattServer.sendResponse(device,
                            requestId,
                            BluetoothGatt.GATT_SUCCESS,
                            0,
                            MessageProvider.instance.getPongJson().toByteArray())
                }
//                UUID_TRIP_CHARACTERISTIC_CONFIG.equals(characteristic.uuid) -> {
//                    Log.i(TAG, "Read LocalTimeInfo")
//                    bluetoothGattServer.sendResponse(device,
//                            requestId,
//                            BluetoothGatt.GATT_SUCCESS,
//                            0,
//                            TimeProfile.getLocalTimeInfo(now))
//                }
                else -> {
                    // Invalid characteristic
                    Log.w(TAG, "Invalid Characteristic Read: " + characteristic.uuid)
                    bluetoothGattServer.sendResponse(device,
                            requestId,
                            BluetoothGatt.GATT_FAILURE,
                            0,
                            null)
                }
            }
        }

        override fun onDescriptorReadRequest(device: BluetoothDevice, requestId: Int, offset: Int,
                                             descriptor: BluetoothGattDescriptor) {
            Log.d(TAG, "onDescriptorReadRequest")

            if (UUID_TRIP_CHARACTERISTIC_CONFIG.equals(descriptor.uuid)) {
                Log.d(TAG, "Config descriptor read")
                val returnValue: ByteArray = if (registeredDevices.contains(device)) {
                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                } else {
                    BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                }
                bluetoothGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0,
                        returnValue)
            } else {
                Log.w(TAG, "Unknown descriptor read request")
                bluetoothGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0, null)
            }
        }

        override fun onDescriptorWriteRequest(device: BluetoothDevice, requestId: Int,
                                              descriptor: BluetoothGattDescriptor,
                                              preparedWrite: Boolean, responseNeeded: Boolean,
                                              offset: Int, value: ByteArray) {
            Log.d(TAG, "onDescriptorWriteRequest " + descriptor.uuid)
            if (UUID_TRIP_CHARACTERISTIC_CONFIG.equals(descriptor.uuid)) {
                if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
                    Log.d(TAG, "Subscribe device to notifications: " + device)
                    registeredDevices.add(device)
                } else if (Arrays.equals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, value)) {
                    Log.d(TAG, "Unsubscribe device from notifications: " + device)
                    registeredDevices.remove(device)
                }

                if (responseNeeded) {
                    bluetoothGattServer.sendResponse(device,
                            requestId,
                            BluetoothGatt.GATT_SUCCESS,
                            0, null)
                }
            } else {
                Log.w(TAG, "Unknown descriptor write request")
                if (responseNeeded) {
                    bluetoothGattServer.sendResponse(device,
                            requestId,
                            BluetoothGatt.GATT_FAILURE,
                            0, null)
                }
            }
        }
    }


    fun writeData(data: String) {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }

        Log.i(TAG, "characteristic " + bluetoothCharacteristic.toString())
        try {
            Log.i(TAG, "data " + data)

            bluetoothCharacteristic?.setValue(data)

            // TODO
            bluetoothGatt!!.writeCharacteristic(bluetoothCharacteristic)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

    }
}

interface IBleCallback {
    fun onConnectSuccesful()
    fun onMessage( data: String)
    fun onDisconnect()
    fun onDeviceConnected(device: BluetoothDevice)
}



interface IScanCallback {
    fun onDone()
    fun onDeviceDiscovered( device: BluetoothDevice)
    fun onError()
}
