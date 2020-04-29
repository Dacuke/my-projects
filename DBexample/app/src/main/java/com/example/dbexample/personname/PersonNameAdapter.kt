package com.example.dbexample.personname


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dbexample.R
import com.example.dbexample.database.Person

class PersonNameAdapter: RecyclerView.Adapter<PersonNameAdapter.ViewHolder>() {
    var data = listOf<Person>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val personName: TextView = itemView.findViewById(R.id.person_name_text)
        val personSurname: TextView = itemView.findViewById(R.id.person_surname_text)
        val personAge: TextView = itemView.findViewById(R.id.person_age_text)

        fun bind(item: Person) {
            personName.text = item.name
            personSurname.text = item.surname
            personAge.text = item.age.toString()
        }
    }
}