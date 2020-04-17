package com.example.observer.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SimpleViewModel : ViewModel() {
    val _myOnCreate = MutableLiveData<Int>()
    val _myOnStart = MutableLiveData<Int>()
    val _myOnPause = MutableLiveData<Int>()
    val _myOnStop = MutableLiveData<Int>()
    val _myOnDestroy = MutableLiveData<Int>()
    val _myOnRestart = MutableLiveData<Int>()

    val myOnCreate: LiveData<Int> = _myOnCreate
    val myOnStart: LiveData<Int> = _myOnStart
    val myOnPause: LiveData<Int> = _myOnPause
    val myOnStop: LiveData<Int> = _myOnStop
    val myOnDestroy: LiveData<Int> = _myOnDestroy
    val myOnRestart: LiveData<Int> = _myOnRestart

    init {
        _myOnCreate.value = 0
        _myOnStart.value = 0
        _myOnPause.value = 0
        _myOnStop.value = 0
        _myOnDestroy.value = 0
        _myOnRestart.value = 0
    }

    fun calledOnCreate() {
        _myOnCreate.value = (_myOnCreate.value ?: 0) + 1
    }

    fun calledOnStart() {
        _myOnStart.value = (_myOnStart.value ?: 0) + 1
    }

    fun calledOnPause() {
        _myOnPause.value = (_myOnPause.value ?: 0) + 1
    }

    fun calledOnStop() {
        _myOnStop.value = (_myOnStop.value ?: 0) + 1
    }

    fun calledOnDestroy() {
        _myOnDestroy.value = (_myOnDestroy.value ?: 0) + 1
    }

    fun calledOnRestart() {
        _myOnRestart.value = (_myOnRestart.value ?: 0) + 1
    }

}