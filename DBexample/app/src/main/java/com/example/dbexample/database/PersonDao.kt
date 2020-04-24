package com.example.dbexample.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PersonDao {
    @Insert
    fun insert(person: Person)

    @Update
    fun update(person: Person)

    @Delete
    fun delete(person: Person)

    @Query("DELETE FROM person_table")
    fun deleteAllPerson()

    @Query("SELECT * FROM person_table ORDER BY id DESC")
    fun getAllPerson(): LiveData<List<Person>>
}