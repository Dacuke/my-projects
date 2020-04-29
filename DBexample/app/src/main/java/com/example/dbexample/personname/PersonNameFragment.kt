package com.example.dbexample.personname

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.dbexample.R
import com.example.dbexample.database.Person
import com.example.dbexample.database.PersonDatabase
import com.example.dbexample.databinding.FragmentPersonNameBinding
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_person_name.*

class PersonNameFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentPersonNameBinding = DataBindingUtil.inflate(
        inflater, R.layout.fragment_person_name, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = PersonDatabase.getInstance(application).personDao

        val viewModelFactory = PersonNameViewModelFactory(dataSource, application)

        val personNameViewModel = ViewModelProviders.of(this, viewModelFactory)
                                                    .get(PersonNameViewModel::class.java)

        binding.personNameViewModel = personNameViewModel

        binding.lifecycleOwner = this



        binding.buttonAdd.setOnClickListener {
            val personName: String?
            val personSurname: String?
            val personAge: Int?
            if ( name.text.toString() == null) {
                personName = "null"
            } else personName = name.text.toString()
            if ( surname.text.toString() == null) {
                personSurname = "null"
            } else personSurname = name.text.toString()
            if ( age.text.toString().toInt() == null) {
                personAge = 0
            } else personAge = age.text.toString().toInt()
            val myPerson = Person(personName, personSurname, personAge)
            personNameViewModel.onAdd(myPerson)
        }
        return binding.root
    }
//    android:onClick="@{() -> personNameViewModel.onAdd()}"
}
