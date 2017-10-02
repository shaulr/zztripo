package com.tikalk.zztripo.zztripo.BLE

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.content.Intent



/**
 * Created by shaulr on 02/10/2017.
 */
class BLEManager {
    val TAG = "BLEManager"

    lateinit var context :Context
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var bluetoothManager: BluetoothManager
    val REQUEST_ENABLE_BT = 123

    fun init(context: Context) : Boolean {
        this.context = context
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(TAG, "BLE not supported")
            return false
        }
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {

        }
        return true
    }

    fun isBluetoothEnabled(): Boolean {
        return (bluetoothAdapter == null || !bluetoothAdapter.isEnabled)
    }


    fun startEnableBluetoothActivity(activity: Activity) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
    }


}