package com.assistant.wavy

import android.bluetooth.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.core.graphics.blue
import com.assistant.wavy.core.AUTH_BYTE
import com.assistant.wavy.core.AUTH_SEND_KEY
import com.assistant.wavy.core.DeviceListener
import com.assistant.wavy.core.DeviceManager
import com.assistant.wavy.utils.console
import com.assistant.wavy.utils.isBLESupported
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), DeviceListener {

    val btnStart by lazy { findViewById<Button>(R.id.btnStartDiscovery) }
    val btnStop by lazy { findViewById<TextView>(R.id.btnStopDiscovery) }

    val devices = ArrayList<BluetoothDevice>()
    lateinit var adapter: ArrayAdapter<String>

    fun log(msg: String) {
        runOnUiThread {
            tvDebugView.text = tvDebugView.text.toString() + msg + "\n"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDebugView.movementMethod = ScrollingMovementMethod()
        log("BLE ${if (!isBLESupported(this)) "Not " else ""}Supported")

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList<String>())
        listDevices.adapter = adapter
        listDevices.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            log("Connecting Device:  ${devices.get(position).name} ${devices.get(position).address}")
            selectedDevice = devices.get(position)
            connectToDevice()
        }


        val deviceManager = DeviceManager(this, this)
        btnStart.setOnClickListener {
            deviceManager.startDiscovery()
            pbScanning.visibility = View.VISIBLE
        }
        btnStop.setOnClickListener {
            deviceManager.stopDiscovery()
            pbScanning.visibility = View.GONE
        }
    }

    override fun onDeviceDiscovered(device: BluetoothDevice) {
        val result = devices.filter { it.address == device.address }
        if (result.isEmpty() && device.name != null) {
            devices.add(device)
            adapter.add(device.name)
            adapter.notifyDataSetChanged()
        }
    }

    var bluetoothGatt: BluetoothGatt? = null
    lateinit var selectedDevice: BluetoothDevice

    private fun connectToDevice() {
        bluetoothGatt = selectedDevice.connectGatt(this, false, bluetoothGattCallback)
    }

    fun disconnect() {
        bluetoothGatt?.let {
            it.disconnect()
            it.close()
            bluetoothGatt = null
        }
    }

    val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            console.log("onCharacteristicRead")
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            console.log("onCharacteristicWrite")
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            console.log("onServicesDiscovered ")
            val services = gatt?.services
            if (services != null) log(listServices(services))
            val authCharacteristic =
                services?.last()?.getCharacteristic(UUID.fromString("00000009-0000-3512-2118-0009af100700"))
            if (authCharacteristic != null) {
                log("Auth Characteristic Found, Requesting Permission from Device")
                requestPermissionFromDevice(authCharacteristic)
            } else log("Auth characteristics not found")
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                log("Connected to Device")
                log("Attempting to discover services ${gatt?.discoverServices()}")
                gatt?.discoverServices()

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                log("Disconnected from Device")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
            log("onCharacteristicChanged ${characteristic?.uuid}")
        }
    }

    fun listServices(services: List<BluetoothGattService>): String {
        val builder = StringBuilder()
        services.forEach {
            builder.append("--- Service: ${it.uuid} ---\n")
            it.characteristics.forEach {
                "Characteristic: ${it.uuid}\n"
            }
            builder.append("----------------------------\n")
        }
        return builder.toString()
    }

    fun getSecretKey(): ByteArray{
        val authKeyBytes = byteArrayOf(0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45)
        return authKeyBytes
    }

    fun requestPermissionFromDevice(authCharacteristic: BluetoothGattCharacteristic) {

        val key:ByteArray = org.apache.commons.lang3.ArrayUtils.addAll(
            byteArrayOf(AUTH_SEND_KEY.toByte(), AUTH_BYTE.toByte()),
            *getSecretKey()
        )
        authCharacteristic.value = key

        if ((authCharacteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE) > 0 ||
            (authCharacteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
            //bluetoothGatt?.setCharacteristicNotification(authCharacteristic, true)
            val result = bluetoothGatt?.writeCharacteristic(authCharacteristic)
            log("Auth Characteristics wrote: $result")
        } else {
            log("Unable to write auth characteristic")
        }
    }


}
