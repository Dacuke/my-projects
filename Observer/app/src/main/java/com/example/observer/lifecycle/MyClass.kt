package com.example.observer.lifecycle

import android.util.Log

class MyClass : Observer {
    override fun notify(name: String) {
        Log.i("MyClassObserver", "Called $name")
    }
}