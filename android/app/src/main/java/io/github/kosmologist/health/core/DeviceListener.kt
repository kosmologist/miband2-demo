package io.github.kosmologist.health.core

import android.bluetooth.BluetoothDevice

interface DeviceListener {

    fun onDeviceDiscovered(device:BluetoothDevice)


}