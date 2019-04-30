package com.assistant.wavy.core

import android.bluetooth.BluetoothDevice

interface DeviceListener {

    fun onDeviceDiscovered(device:BluetoothDevice)


}