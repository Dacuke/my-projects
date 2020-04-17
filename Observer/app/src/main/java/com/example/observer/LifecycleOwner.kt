package com.example.observer

import android.util.Log

interface LifecycleOwner {
    var observers: MutableList<Observer>
    fun setState(newState: String) {
        for (i in 0 until observers.size) {
            observers[i].notify(newState)
        }
    }

    fun addNewObserver(newObserver: Observer) {
        Log.i("LifecycleOwner", "Added observer")
        observers.add(newObserver)
    }
}