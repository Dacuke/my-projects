package com.example.dbexample.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "person_table")
data class Person (
    @ColumnInfo(name = "person_name")
    val name: String = "null",
    @ColumnInfo(name = "person_surname")
    val surname: String = "null",
    @ColumnInfo(name = "person_age")
    val age: Int = 0,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
    )