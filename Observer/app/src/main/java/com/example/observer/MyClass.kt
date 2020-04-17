package com.example.observer

import android.util.Log
import com.example.observer.Observer

class MyClass : Observer {
    override fun notify(value: String) {
        Log.i("MyClassObserver", "Called $value")
    }
}