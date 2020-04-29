package com.example.observer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.observer.databinding.ActivityMainBinding
import com.example.observer.lifecycle.*
import com.example.observer.viewModel.SimpleViewModel

class MainActivity : AppCompatActivity(), LifecycleOwner {
    private val mainViewModel by lazy { ViewModelProviders.of(this).get(SimpleViewModel::class.java) }
    override var observers: MutableList<Observer> = mutableListOf()
    private var observer = MyClass()
    init {
        observers.add(observer)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = mainViewModel
        changeStatus("onCreate")
        mainViewModel.calledOnCreate()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putInt("ONCREATE", mainViewModel._myOnCreate.value!!)
            putInt("ONSTART", mainViewModel._myOnStart.value!!)
            putInt("ONPAUSE", mainViewModel._myOnPause.value!!)
            putInt("ONSTOP", mainViewModel._myOnStop.value!!)
            putInt("ONDESTROY", mainViewModel._myOnDestroy.value!!)
            putInt("ONRESTART", mainViewModel._myOnRestart.value!!)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mainViewModel._myOnCreate.value = savedInstanceState.getInt("ONCREATE")
        mainViewModel._myOnStart.value = savedInstanceState.getInt("ONSTART")
        mainViewModel._myOnPause.value = savedInstanceState.getInt("ONPAUSE")
        mainViewModel._myOnStop.value = savedInstanceState.getInt("ONSTOP")
        mainViewModel._myOnDestroy.value = savedInstanceState.getInt("ONDESTROY")
        mainViewModel._myOnRestart.value = savedInstanceState.getInt("ONRESTART")
    }

    override fun onStart() {
        mainViewModel.calledOnStart()
        changeStatus("onStart")
        super.onStart()
    }

    override fun onPause() {
        mainViewModel.calledOnPause()
        changeStatus("onPause")
        super.onPause()
    }

    override fun onStop() {
        mainViewModel.calledOnStop()
        changeStatus("onStop")
        super.onStop()
    }

    override fun onDestroy() {
        mainViewModel.calledOnDestroy()
        changeStatus("onDestroy")
        super.onDestroy()
    }

    override fun onRestart() {
        mainViewModel.calledOnRestart()
        changeStatus("onRestart")
        super.onRestart()
    }
}
