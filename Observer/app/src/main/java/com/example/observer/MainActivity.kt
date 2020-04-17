package com.example.observer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.observer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), LifecycleOwner {
    val mainViewModel by lazy { ViewModelProviders.of(this).get(SimpleViewModel::class.java) }
    override var observers: MutableList<Observer> = mutableListOf()
    private var observer = MyClass()
    init {
        this.addNewObserver(observer)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val mainViewModel = ViewModelProviders.of(this).get(SimpleViewModel::class.java)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = mainViewModel
        setState("onCreate")
        mainViewModel.calledOnCreate()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putString("ONCREATE", mainViewModel._myOnCreate.value.toString())
            putString("ONSTART", mainViewModel._myOnStart.value.toString())
            putString("ONPAUSE", mainViewModel._myOnPause.value.toString())
            putString("ONSTOP", mainViewModel._myOnStop.value.toString())
            putString("ONDESTROY", mainViewModel._myOnDestroy.value.toString())
            putString("ONRESTART", mainViewModel._myOnRestart.value.toString())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mainViewModel._myOnCreate.value = savedInstanceState.getString("ONCREATE")!!.toInt()
        mainViewModel._myOnStart.value = savedInstanceState.getString("ONSTART")!!.toInt()
        mainViewModel._myOnPause.value = savedInstanceState.getString("ONPAUSE")!!.toInt()
        mainViewModel._myOnStop.value = savedInstanceState.getString("ONSTOP")!!.toInt()
        mainViewModel._myOnDestroy.value = savedInstanceState.getString("ONDESTROY")!!.toInt()
        mainViewModel._myOnRestart.value = savedInstanceState.getString("ONRESTART")!!.toInt()
    }

    override fun onStart() {
        mainViewModel.calledOnStart()
        super.onStart()
    }

    override fun onPause() {
        mainViewModel.calledOnPause()
        super.onPause()
    }

    override fun onStop() {
        mainViewModel.calledOnStop()
        super.onStop()
    }

    override fun onDestroy() {
        mainViewModel.calledOnDestroy()
        super.onDestroy()
    }

    override fun onRestart() {
        mainViewModel.calledOnRestart()
        super.onRestart()
    }
}
