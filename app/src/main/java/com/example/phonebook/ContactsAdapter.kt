package com.example.phonebook

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.phonebook.db.entity.Contact
import kotlinx.android.synthetic.main.contact_item.view.*

class ContactsAdapter(private var contacts: List<Contact>) :
    RecyclerView.Adapter<ContactsAdapter.ContactHolder>() {

    class ContactHolder(v: View) : RecyclerView.ViewHolder(v) {
        val view: View = v
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val inflatedView = parent.inflate(R.layout.contact_item, false)
        return ContactHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        holder.view.tv_contact_item_name.text = contacts[position].name
        holder.view.tv_contact_item_phone_number.text = contacts[position].phoneNumber
        holder.view.tv_contact_item_email.text = contacts[position].email

        Glide
            .with(holder.view)
            .load(contacts[position].photoPath)
            .into(holder.view.iv_contact_item_photo)
    }

    override fun getItemCount(): Int = contacts.size

    fun setContacts(newContacts: List<Contact>) {
        val diff = notifyChanges(newContacts, contacts)
        contacts = newContacts
        diff.dispatchUpdatesTo(this)
    }

    private fun notifyChanges(newContacts: List<Contact>, oldContacts: List<Contact>): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldContacts[oldItemPosition].name == newContacts[newItemPosition].name

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldContacts[oldItemPosition] == newContacts[newItemPosition]

            override fun getOldListSize() = oldContacts.size

            override fun getNewListSize() = newContacts.size
        })
    }
}
