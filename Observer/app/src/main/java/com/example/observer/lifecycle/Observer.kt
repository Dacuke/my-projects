package com.example.observer.lifecycle

import android.util.Log

interface Observer {
    fun notify(value: String) {
        Log.i("observer", "Called $value")
    }
}