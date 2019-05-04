package io.github.kosmologist.healthsdk.utils

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler

/**
 * Created by mqasim on 1/21/2016.
 */

fun isBLESupported(c: Context): Boolean {
    return if (c.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
        true
    } else
        false
}

fun enableBluetoothWithPermission(context: Context, bluetoothAdapter: BluetoothAdapter?) {
    if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled) {
        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        val activity = context as Activity
        activity.startActivityForResult(enableIntent, 1)
    }
}

fun disableBluetooth(bluetoothAdapter: BluetoothAdapter?): Boolean {
    return if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
        bluetoothAdapter.disable()
    } else
        false
}

fun enableBluetoothWithoutPermission(bluetoothAdapter: BluetoothAdapter): Boolean {
    return if (bluetoothAdapter.isEnabled) {
        true
    } else if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled) {
        bluetoothAdapter.enable()
    } else
        false
}

fun scanForBLEDevices(
    auto_stop_max_period: Int,
    bluetoothAdapter: BluetoothAdapter,
    leScanCallback: BluetoothAdapter.LeScanCallback,
    scan_completionCallBack: LeScanCompleteListener
): Boolean {

    val handler = Handler()
    handler.postDelayed({
        bluetoothAdapter.stopLeScan(leScanCallback)
        scan_completionCallBack.onLeScanCompleted()
    }, auto_stop_max_period.toLong())
    return bluetoothAdapter.startLeScan(leScanCallback)
}

fun stopScanningForBleDevices(
    bluetoothAdapter: BluetoothAdapter?,
    leScanCallback: BluetoothAdapter.LeScanCallback
) {
    if (bluetoothAdapter != null) {
        try {
            bluetoothAdapter.stopLeScan(leScanCallback)
        } catch (ex: Exception) {

        }

    }
}
