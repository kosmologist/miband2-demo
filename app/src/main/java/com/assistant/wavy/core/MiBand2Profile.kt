package com.assistant.wavy.core

import java.util.*

const val AUTH_SEND_KEY:Byte = 0x01
const val AUTH_BYTE:Byte = 0x08
val UUID_CHARACTERISTIC_AUTH = UUID.fromString("00000009-0000-3512-2118-0009af100700")
val UUID_SERVICE_AUTH = UUID.fromString("0000FEE1-0000-1000-8000-00805f9b34fb")
val AUTH_RESPONSE: Byte = 0x10
val AUTH_SUCCESS: Byte = 0x01
val AUTH_REQUEST_RANDOM_AUTH_NUMBER: Byte = 0x02
val AUTH_SEND_ENCRYPTED_AUTH_NUMBER: Byte = 0x03



