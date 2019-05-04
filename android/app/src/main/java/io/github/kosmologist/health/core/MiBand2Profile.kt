package io.github.kosmologist.health.core

import java.util.*

const val AUTH_SEND_KEY: Byte = 0x01
const val AUTH_BYTE: Byte = 0x08
const val AUTH_RESPONSE: Byte = 0x10
const val AUTH_SUCCESS: Byte = 0x01
const val AUTH_REQUEST_RANDOM_AUTH_NUMBER: Byte = 0x02
const val AUTH_SEND_ENCRYPTED_AUTH_NUMBER: Byte = 0x03

val UUID_CHARACTERISTIC_AUTH = UUID.fromString("00000009-0000-3512-2118-0009af100700")
val UUID_SERVICE_AUTH = UUID.fromString("0000FEE1-0000-1000-8000-00805f9b34fb")

val UUID_SERVICE_HEART_RATE = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb")
val UUID_CHARACTERISTIC_HEART_RATE_CONTROL_POINT =
    UUID.fromString("00002A39-0000-1000-8000-00805f9b34fb") // manual or continous
val UUID_CHARACTERISTIC_HEART_RATE_MEASUREMENT = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb")

val UUID_DESCRIPTOR_GATT_CLIENT_CHARACTERISTIC_CONFIGURATION = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

val COMMAND_SET_HR_SLEEP: Byte = 0x0
val COMMAND_SET__HR_CONTINUOUS: Byte = 0x1
val COMMAND_SET_HR_MANUAL: Byte = 0x2

val startHeartMeasurementManual = byteArrayOf(0x15, COMMAND_SET_HR_MANUAL, 1)
val stopHeartMeasurementManual = byteArrayOf(0x15, COMMAND_SET_HR_MANUAL, 0)
val startHeartMeasurementContinuous = byteArrayOf(0x15, COMMAND_SET__HR_CONTINUOUS, 1)
val stopHeartMeasurementContinuous = byteArrayOf(0x15, COMMAND_SET__HR_CONTINUOUS, 0)

val heartMeasurementContinuousKeepAlive = byteArrayOf(0x16)