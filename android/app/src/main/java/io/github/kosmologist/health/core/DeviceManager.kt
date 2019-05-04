package io.github.kosmologist.health.core

import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import io.github.kosmologist.health.utils.console

class DeviceManager(private val context: Context, val listener: DeviceListener) {

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter

    private val scanCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            console.log("Scan failed $errorCode")
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            console.log("Scan Result: " + result?.device?.name)
            if (result?.device != null){
                listener.onDeviceDiscovered(result.device)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            console.log("Scan batched results: ${results?.size}")
        }
    }

    fun startDiscovery() {
        bluetoothAdapter
            .bluetoothLeScanner
            .startScan(scanCallback)
    }

    fun stopDiscovery() {
        bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
    }


}