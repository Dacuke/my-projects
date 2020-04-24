package com.example.dbexample.personname

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.dbexample.R
import com.example.dbexample.database.PersonDatabase
import com.example.dbexample.databinding.FragmentPersonNameBinding

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

        return binding.root
    }

}
