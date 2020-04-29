package com.example.dbexample.personname

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.dbexample.database.Person
import com.example.dbexample.database.PersonDao
import kotlinx.coroutines.*

class PersonNameViewModel(
    private val database: PersonDao,
    application: Application): AndroidViewModel(application) {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val persons = database.getAllPerson()

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.deleteAllPerson()
        }
    }

    private suspend fun update(person: Person) {
        withContext(Dispatchers.IO) {
            database.update(person)
        }
    }

    private suspend fun insert(person: Person) {
        withContext(Dispatchers.IO) {
            database.insert(person)
        }
    }

    fun onAdd(myPerson: Person) {
        uiScope.launch {
            insert(myPerson)
        }
    }

    fun onClear() {
        uiScope.launch {
            clear()
        }
    }
}