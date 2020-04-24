package com.example.dbexample.personname

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dbexample.database.PersonDao

class PersonNameViewModelFactory(
    private val dataSource: PersonDao,
    private val application: Application): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonNameViewModel::class.java)) {
            return PersonNameViewModel(
                dataSource,
                application
            ) as T
        }
        throw IllegalAccessException("Unknown ViewModel class")
    }
}