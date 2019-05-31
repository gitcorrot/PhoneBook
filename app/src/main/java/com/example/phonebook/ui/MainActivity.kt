package com.example.phonebook.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebook.ContactsAdapter
import com.example.phonebook.R
import com.example.phonebook.db.entity.Contact
import com.example.phonebook.viewmodel.ContactViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var dim: View
    private lateinit var contactLabel: TextView
    private lateinit var addButton: Button
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var mViewModel: ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dim = activity_main_dim
        contactLabel = tv_contact_label
        addButton = btn_add_contact
        recyclerView = rv_contacts
        bottomSheetBehavior = from(contact_bottom_sheet)
        linearLayoutManager = LinearLayoutManager(this)
        mViewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)

        /*
val textInputLayouts = Utils.findViewsWithType(
       rootView, TextInputLayout::class.java)
*/

        dim.setOnClickListener {
            if (it.visibility == View.VISIBLE) bottomSheetBehavior.state = STATE_COLLAPSED
        }

        contactLabel.setOnClickListener {
            when (bottomSheetBehavior.state) {
                STATE_EXPANDED -> bottomSheetBehavior.state = STATE_COLLAPSED
                STATE_COLLAPSED -> bottomSheetBehavior.state = STATE_EXPANDED
            }
        }

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                dim.alpha = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    STATE_COLLAPSED, STATE_HIDDEN -> dim.visibility = View.GONE
                    else -> dim.visibility = View.VISIBLE
                }
            }
        })

        addButton.setOnClickListener { addContact() }

        val adapter = ContactsAdapter(ArrayList())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        mViewModel.allContacts.observe(this, Observer { contacts ->
            adapter.setContacts(contacts)
        })
    }

    private fun addContact() {
        val name = tv_contact_name.text.toString()
        val phoneNumber = tv_contact_phone_number.text.toString()
        val email = tv_contact_email.text.toString()
        // TODO: val photo =
        val contact = Contact(name, phoneNumber)
        contact.email = email

        mViewModel.insertContact(contact)
    }
}
