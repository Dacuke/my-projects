package com.example.observer.lifecycle

interface LifecycleOwner {
    var observers: MutableList<Observer>
    fun changeStatus(Status: String) {
        for (i in 0 until observers.size) {
            observers[i].notify(Status)
        }
    }
}