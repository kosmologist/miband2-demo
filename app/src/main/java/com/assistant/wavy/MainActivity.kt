package com.assistant.wavy

import android.bluetooth.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.core.os.postDelayed
import com.assistant.wavy.core.*
import com.assistant.wavy.utils.console
import com.assistant.wavy.utils.isBLESupported
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.IllegalStateException
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList
import kotlin.experimental.and

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

    var charateristicHRControlPoint: BluetoothGattCharacteristic? = null
    var characteristicHRMeasurement: BluetoothGattCharacteristic? = null

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


    var currentState = HR_STATE.idle

    enum class HR_STATE {
        idle,
        stopManual,
        stopContinous,
        startContinous,
        startedContinousMeasument
    }


    val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            log("onCharacteristicRead ${characteristic?.uuid}")
            if (characteristic?.uuid == UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT) {
                log("Characteristic read for HR measurment")
                val value = characteristic?.value ?: throw IllegalStateException("Should never happen..")
                handleHeartRate(value)
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            log("onCharacteristicWrite ${characteristic?.uuid}")
            if (characteristic?.uuid == UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT && status == BluetoothGatt.GATT_SUCCESS) {
                if (currentState == HR_STATE.stopManual) {
                    characteristic?.value = stopHeartMeasurementContinuous
                    val result = write(characteristic!!)
                    currentState = HR_STATE.stopContinous
                    if (result) log("Stopped Manual Measurment, Moving to step 2") else log("ERR: Failed to stop Manual measurment")
                } else if (currentState == HR_STATE.stopContinous) {
                    characteristic?.value = startHeartMeasurementContinuous
                    val result = write(characteristic!!)
                    currentState = HR_STATE.startContinous
                    if (result) log("Stopped Continous Measurment, Moving to step 3") else log("ERR: Failed to stop Continous measurment")
                } else if (currentState == HR_STATE.startContinous) {
                    log("Started Continous measurment succesfully")
                    currentState = HR_STATE.startedContinousMeasument
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            console.log("onServicesDiscovered ")
            val services = gatt?.services
            if (services != null) log(listServices(services))
            val authCharacteristic =
                services?.last()?.getCharacteristic(UUID.fromString("00000009-0000-3512-2118-0009af100700"))

            val heartRateService = services?.find { it.uuid == UUID_SERVICE_HEART_RATE }
                ?: throw RuntimeException("HR Service Not found")  //todo: fix this
            characteristicHRMeasurement = heartRateService.getCharacteristic(UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT)
            charateristicHRControlPoint =
                heartRateService.getCharacteristic(UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT)
            if (characteristicHRMeasurement != null && charateristicHRControlPoint != null) {
                log("HR Service and Charateristics found...")
            } else throw RuntimeException("ERR: HR Service and Charateristics not found")

            if (authCharacteristic != null) {
                log("Auth Characteristic Found, Requesting Permission from Device")
                requestPermissionFromDevice(authCharacteristic)
            } else log("ERR: Auth characteristics not found")
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
            log("onCharacteristicChanged ${characteristic?.uuid}")
            if (characteristic?.uuid == UUID_CHARACTERISTIC_AUTH) {
                val value = characteristic?.value ?: throw IllegalStateException("SHOULD NEVER HAPPEN")
                if (value[0] == AUTH_RESPONSE && value[1] == AUTH_SEND_KEY && value[2] == AUTH_SUCCESS) {
                    log("Step2. Sending secrect key to band now...")
                    sendSecretKeyToBand(characteristic)
                } else if (value[0] == AUTH_RESPONSE && value[1] == AUTH_REQUEST_RANDOM_AUTH_NUMBER && value[2] == AUTH_SUCCESS) {
                    log("Step 3. Sending the encrypted random key to band")
                    sendEncryptedRandomKeyToBand(characteristic)
                } else if (value[0] == AUTH_RESPONSE && value[1] == AUTH_SEND_ENCRYPTED_AUTH_NUMBER && value[2] == AUTH_SUCCESS) {
                    log("Authenticated, now moving to phase 2...")
                    enableRealtimeHeartRateMeasurement(true)
                }
            } else if (characteristic?.uuid == UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT) {
                log("Heart Rate Measurement charateristic changed")
                val value = characteristic?.value ?: throw IllegalStateException("Should never happen..")
                handleHeartRate(value)
            } else {
                log("Unknown characteristic changed: ${characteristic?.uuid}")
                super.onCharacteristicChanged(gatt, characteristic)
            }
        }
    }

    private fun handleHeartRate(value: ByteArray) {
        if (value.size == 2 && value[0].toInt() == 0) {
            val hrValue = value[1] and 0xff.toByte()
            log("Heart Rate: $hrValue")
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

    private fun getSecretKey(): ByteArray {
        val authKeyBytes =
            byteArrayOf(0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45)
        return authKeyBytes
    }

    // Step.1
    fun requestPermissionFromDevice(authCharacteristic: BluetoothGattCharacteristic) {
        val key: ByteArray = org.apache.commons.lang3.ArrayUtils.addAll(
            byteArrayOf(AUTH_SEND_KEY, AUTH_BYTE),
            *getSecretKey()
        )
        authCharacteristic.value = key
        val result = write(authCharacteristic)
        if (result) {
            log("Auth Characteristics wrote: $result")
        } else log("Unable to write auth characteristic")
    }

    // authflag==authbyte
    private fun requestAuthNumber(): ByteArray {
        return byteArrayOf(AUTH_REQUEST_RANDOM_AUTH_NUMBER, AUTH_BYTE)
    }

    // Step.2
    fun sendSecretKeyToBand(characteristic: BluetoothGattCharacteristic) {
        characteristic.value = requestAuthNumber()
        val result = write(characteristic)
        if (result) log("Secrect Key sent to band") else log("Failed to send secrect key to band")
    }

    // Step.3
    fun sendEncryptedRandomKeyToBand(characteristic: BluetoothGattCharacteristic) {
        val value = characteristic.value
        val eValue = handleAESAuth(value, getSecretKey())
        val responseValue =
            org.apache.commons.lang3.ArrayUtils.addAll(byteArrayOf(AUTH_SEND_ENCRYPTED_AUTH_NUMBER, AUTH_BYTE), *eValue)
        characteristic.value = responseValue
        val result = write(characteristic)
        if (result) log("Sent Encrypted Random Key to Band") else log("Failed to send encrypted random key to band")
    }

    private fun write(characteristic: BluetoothGattCharacteristic, enableNotification: Boolean = true): Boolean {
        if ((characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE) > 0 ||
            (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0
        ) {
            bluetoothGatt?.setCharacteristicNotification(characteristic, enableNotification)
            return bluetoothGatt?.writeCharacteristic(characteristic) ?: false
        } else {
            log("Unable to write characteristic")
            return false
        }
    }

    fun delay(duration: Long, callback: () -> Unit) {
        Handler().postDelayed(callback, duration)
    }

    fun enableRealtimeHeartRateMeasurement(enable: Boolean) {
        charateristicHRControlPoint?.let {
            enableNotificationForHRMeasurement(enable)
            if (enable) {
                it.value = stopHeartMeasurementManual
                val result1 = write(it)
                if (result1) log("Stopped HR Measurement Manual") else log("ERR: Failed to stop HR Measurement Manual")
                currentState = HR_STATE.stopManual
            } else {
                it.value = stopHeartMeasurementContinuous
            }
        } ?: log("ERR: Failed to enable Realtime HR Measurment")
    }

    private fun enableNotificationForHRMeasurement(enable: Boolean) {
        val gatt = bluetoothGatt ?: throw IllegalStateException("SHOULD NOT HAPPEN")
        val characteristic = characteristicHRMeasurement ?: throw IllegalStateException("SHOULD NOT HAPPEN")
        val result = enableNotification(gatt, characteristic, enable)
        if (result) log("Enabled HR Measurment notif") else log("ERR: Failed to enable HR Measurement notif")
    }

    private fun enableNotification(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        flag: Boolean
    ): Boolean {
        var result = gatt.setCharacteristicNotification(characteristic, flag)
        /*if (result) {
            val notifyDescriptor =
                characteristic.getDescriptor(UUID_DESCRIPTOR_GATT_CLIENT_CHARACTERISTIC_CONFIGURATION)
            if (notifyDescriptor != null) {
                val properties = characteristic.properties
                when {
                    (properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0 -> {
                        notifyDescriptor.value =
                            if (flag) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                        result = gatt.writeDescriptor(notifyDescriptor)
                    }
                    (properties and BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0 -> {
                        notifyDescriptor.value =
                            if (flag) BluetoothGattDescriptor.ENABLE_INDICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                        result = gatt.writeDescriptor(notifyDescriptor)
                    }
                }
            } else {
                log("WARN: descriptor client_charac_config for characteristic ${characteristic.uuid} is null")
            }
        } else {
            log("ERR: Unable to enable notification for ${characteristic.uuid}")
        }*/
        return result
    }


    @Throws(
        InvalidKeyException::class,
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    private fun handleAESAuth(value: ByteArray, secretKey: ByteArray): ByteArray {
        val mValue = Arrays.copyOfRange(value, 3, 19)
        val ecipher = Cipher.getInstance("AES/ECB/NoPadding")
        val newKey = SecretKeySpec(secretKey, "AES")
        ecipher.init(Cipher.ENCRYPT_MODE, newKey)
        return ecipher.doFinal(mValue)
    }


}
