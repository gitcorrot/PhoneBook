package com.example.phonebook.db.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Contacts")
data class Contact(@PrimaryKey @NonNull val name: String, @NonNull val phoneNumber: String) {
    var email: String? = null
    var photoPath: String? = null
}
