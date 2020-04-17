package com.example.observer

import android.util.Log
import android.widget.Toast

interface Observer {
    fun notify(value: String) {
        Toast.makeToast(this, "Called $value", Toast.LENGTH_SHORT).show()
        Log.i("observer", "Called $value")
    }
}