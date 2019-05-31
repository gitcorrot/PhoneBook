package com.example.phonebook.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.phonebook.db.entity.Contact

@Dao
interface ContactDao {

    @Query("SELECT * FROM Contacts")
    fun getAll(): LiveData<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Query("DELETE FROM Contacts")
    fun deleteAll()
}