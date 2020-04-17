package com.example.observer.lifecycle

interface LifecycleOwner {
    var observers: MutableList<Observer>
    fun setState(newState: String) {
        for (i in 0 until observers.size) {
            observers[i].notify(newState)
        }
    }

    fun addNewObserver(newObserver: Observer) {
        observers.add(newObserver)
    }
}