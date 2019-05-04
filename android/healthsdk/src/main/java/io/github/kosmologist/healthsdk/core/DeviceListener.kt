package io.github.kosmologist.healthsdk.core

import android.bluetooth.BluetoothDevice

interface DeviceListener {

    fun onDeviceDiscovered(device:BluetoothDevice)


}