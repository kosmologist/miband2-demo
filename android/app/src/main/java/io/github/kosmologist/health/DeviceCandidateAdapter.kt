package io.github.kosmologist.health

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class DeviceCandidateAdapter(context: Context, var devices: ArrayList<BluetoothDevice>) :
    ArrayAdapter<BluetoothDevice>(context, -1, devices) {

    fun updateDevices(devs: ArrayList<BluetoothDevice>) {
        this.devices.clear()
        this.devices.addAll(devs)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflator = LayoutInflater.from(context)
        val rowView = inflator.inflate(R.layout.item_device_candidate, parent, false)

        val device = devices[position]
        with(rowView)
        {
            findViewById<TextView>(R.id.device_candidate_name).text = device.name
            findViewById<TextView>(R.id.device_candidate_detail).text = device.address
        }
        return rowView
    }

}