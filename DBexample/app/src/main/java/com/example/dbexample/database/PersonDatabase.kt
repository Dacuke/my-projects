package com.example.dbexample.database

import android.content.Context
import androidx.room.*

@Database(entities = [Person::class], version = 1, exportSchema = false)
abstract class PersonDatabase: RoomDatabase() {
//    fun PersonDatabase
    abstract val personDao: PersonDao

    companion object {
        @Volatile
        private var INSTANCE: PersonDatabase? = null
        fun getInstance(context: Context): PersonDatabase {
            synchronized(this){
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            PersonDatabase::class.java,
                            "person_name_database"
                    )
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}