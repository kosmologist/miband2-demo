package com.assistant.wavy

import android.bluetooth.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import com.assistant.wavy.core.DeviceListener
import com.assistant.wavy.core.DeviceManager
import com.assistant.wavy.utils.console
import com.assistant.wavy.utils.isBLESupported
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), DeviceListener {

    val tvStatus by lazy { findViewById<TextView>(R.id.tvStatus) }
    val btnStart by lazy { findViewById<Button>(R.id.btnStartDiscovery) }
    val btnStop by lazy { findViewById<TextView>(R.id.btnStopDiscovery) }

    val devices = ArrayList<BluetoothDevice>()
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isBLESupported(this)) {
            tvStatus.text = "BLE Supported"
        } else {
            tvStatus.text = "BLE Not Supported"
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList<String>())
        listDevices.adapter = adapter
        listDevices.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            tvStatus.text = "Connecting Device:  ${devices.get(position).name} ${devices.get(position).address}"
            connectToDevice(devices.get(position))
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
        if (result.isEmpty() && device.name!=null){
            devices.add(device)
            adapter.add(device.name)
            adapter.notifyDataSetChanged()
        }
    }

    var bluetoothGatt:BluetoothGatt? = null
    fun connectToDevice(device: BluetoothDevice){
        bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
    }

    fun disconnect(device: BluetoothDevice){
        bluetoothGatt?.let {
            it.disconnect()
            it.close()
            bluetoothGatt = null
        }
    }

    val bluetoothGattCallback = object : BluetoothGattCallback(){
        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, status)
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            console.log("Connection State changed " + newState)
            if (newState == BluetoothProfile.STATE_CONNECTED){
                console.log("Connected from Server")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                console.log("Disconnected from Server")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
        }
    }




}
