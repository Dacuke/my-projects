package com.example.observer.lifecycle

import android.util.Log

interface Observer {
    fun notify(name: String) {
        Log.i("observer", "Called $name")
    }
}