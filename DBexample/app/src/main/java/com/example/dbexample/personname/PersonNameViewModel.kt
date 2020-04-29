package com.example.dbexample.personname

import android.annotation.SuppressLint
import android.app.Application
import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import com.example.dbexample.R
import com.example.dbexample.database.Person
import com.example.dbexample.database.PersonDao
import kotlinx.coroutines.*

class PersonNameViewModel(
    private val database: PersonDao,
    application: Application): AndroidViewModel(application) {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

//    private var people = MutableLiveData<Person?>()
//    @SuppressLint("ResourceType")
//    private val personName = application.resources.getString(R.id.name)
//    @SuppressLint("ResourceType")
//    private val personSurname = application.resources.getString(R.id.surname)
//    @SuppressLint("ResourceType")
//    private val personAge = application.resources.getInteger(R.id.age)
//
//    private val myPerson: Person = Person(personName, personSurname, personAge)
//    private val myPerson: Person = Person("personName", "personSurname", 10)

    private val persons = database.getAllPerson()

    val  personToString= Transformations.map(persons) { persons ->
        formatPerson(persons, application.resources)
    }
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
    private fun formatPerson(person: List<Person>, resources: Resources): Spanned {
        val sb = StringBuilder()
        sb.apply {
//            append(resources.getString(R.string.title))
            person.forEach {
                append("${it.name} ")
                append("${it.surname} ")
                append("${it.age}<br>")
                append("<br>")
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
        } else {
            return HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }
}