package com.example.phonebook

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.phonebook.db.dao.ContactDao
import com.example.phonebook.db.entity.Contact

class ContactRepository(private val contactDao: ContactDao) {

    val allContacts: LiveData<List<Contact>> = contactDao.getAll()

    @WorkerThread
    suspend fun insert(contact: Contact) {
        contactDao.insert(contact)
    }

    @WorkerThread
    suspend fun delete(contact: Contact) {
        contactDao.delete(contact)
    }

    // TODO: Worker thread?
    suspend fun deleteAll() {
        contactDao.deleteAll()
    }
}
