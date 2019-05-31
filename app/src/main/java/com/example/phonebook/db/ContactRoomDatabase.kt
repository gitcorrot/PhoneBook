package com.example.phonebook.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.phonebook.db.dao.ContactDao
import com.example.phonebook.db.entity.Contact

@Database(entities = [Contact::class], version = 1, exportSchema = false)
public abstract class ContactRoomDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: ContactRoomDatabase? = null

        fun getDatabase(context: Context): ContactRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactRoomDatabase::class.java,
                    "Contacts_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
