package com.example.dbexample.personname

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dbexample.R
import com.example.dbexample.database.Person
import com.example.dbexample.database.PersonDatabase
import com.example.dbexample.databinding.FragmentPersonNameBinding
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

        val adapter = PersonNameAdapter()

        binding.nameList.adapter = adapter

        personNameViewModel.persons.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })

        binding.lifecycleOwner = this

        binding.buttonAdd.setOnClickListener {
            val personName: String
            val personSurname: String
            val personAge: Int
            if (( name.text.toString() == "") || ( surname.text.toString() == "") || ( age.text.toString() == "")) {
                Toast.makeText(it.context,"Enter correct parameter",  Toast.LENGTH_SHORT).show()
            } else {
                personName = name.text.toString()
                personSurname = surname.text.toString()
                personAge = age.text.toString().toInt()
                val myPerson = Person(personName, personSurname, personAge)
                personNameViewModel.onAdd(myPerson)
            }
        }
        return binding.root
    }
//    android:onClick="@{() -> personNameViewModel.onAdd()}"
}
