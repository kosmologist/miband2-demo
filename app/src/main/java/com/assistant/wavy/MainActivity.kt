package com.assistant.wavy

import android.bluetooth.BluetoothDevice
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
            tvStatus.text = "Selected Device:  ${devices.get(position).name} ${devices.get(position).address}"
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
}
