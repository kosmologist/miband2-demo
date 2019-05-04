package com.assistant.wavy.utils

import android.util.Log

class console {
    companion object {
        var TAG = "Wavy-Assistant"
        fun log(msg:String){
            Log.i(TAG, msg)
        }
    }
}