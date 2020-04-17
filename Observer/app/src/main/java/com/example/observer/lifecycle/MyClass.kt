package com.example.observer.lifecycle

import android.util.Log

class MyClass : Observer {
    override fun notify(value: String) {
        Log.i("MyClassObserver", "Called $value")
    }
}