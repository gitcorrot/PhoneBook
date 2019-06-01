package com.example.phonebook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.phonebook.ContactRepository
import com.example.phonebook.db.ContactRoomDatabase
import com.example.phonebook.db.entity.Contact
import com.example.phonebook.notifyObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ContactRepository

    private val allContacts: LiveData<List<Contact>>

    private val name: MutableLiveData<String?>
    private val phone: MutableLiveData<String?>
    private val email: MutableLiveData<String?>
    private val photoPath: MutableLiveData<String?>

    init {
        val contactDao = ContactRoomDatabase.getDatabase(application).contactDao()
        repository = ContactRepository(contactDao)
        allContacts = repository.allContacts
        name = MutableLiveData()
        phone = MutableLiveData()
        email = MutableLiveData()
        photoPath = MutableLiveData()
    }

    fun getContacts() = allContacts

    fun setName(name: String) {
        this.name.value = name
        this.name.notifyObserver()
    }

    fun setPhone(phone: String) {
        this.phone.value = phone
        this.phone.notifyObserver()
    }

    fun setEmail(email: String) {
        this.email.value = email
        this.email.notifyObserver()
    }

    fun setPhotoPath(photoPath: String) {
        this.photoPath.value = photoPath
        this.photoPath.notifyObserver()
    }

    fun getName(): LiveData<String?> = name
    fun getPhone(): LiveData<String?> = phone
    fun getEmail(): LiveData<String?> = email
    fun getPhotoPath(): LiveData<String?> = photoPath

    // Data needed to save contact is saved on change in viewModel
    fun insertContact() = viewModelScope.launch(Dispatchers.IO) {
        val n = name.value!! // !! because null conditions have been checked before
        val pn = phone.value!!
        val contact = Contact(n, pn)
        email.value?.let { contact.email = it }
        photoPath.value?.let { contact.photoPath = it }
        repository.insert(contact)
    }
}
