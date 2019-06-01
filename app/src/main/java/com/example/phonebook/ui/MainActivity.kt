package com.example.phonebook.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.phonebook.ContactsAdapter
import com.example.phonebook.R
import com.example.phonebook.Utils
import com.example.phonebook.viewmodel.ContactViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    companion object {
        const val PICK_PHOTO_REQUEST = 123
        const val READ_EXTERNAL_STORAGE_REQUEST = 1234
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var dim: View
    private lateinit var contactLabel: TextView
    private lateinit var addButton: Button
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var mViewModel: ContactViewModel
    private lateinit var textInputLayouts: ArrayList<TextInputLayout>

    private var readExternalStorageGranted: Boolean = false

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
        textInputLayouts = getTextInputLayouts(bottom_sheet_layout)


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // Show why user should grant permission
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_REQUEST
                )
            }
        }

        dim.setOnClickListener {
            if (it.visibility == View.VISIBLE) bottomSheetBehavior.state = STATE_COLLAPSED
        }

        contactLabel.setOnClickListener {
            when (bottomSheetBehavior.state) {
                STATE_EXPANDED -> bottomSheetBehavior.state = STATE_COLLAPSED
                STATE_COLLAPSED -> bottomSheetBehavior.state = STATE_EXPANDED
            }
        }

        iv_contact_photo.setOnClickListener { pickPhotoFromGallery() }

        et_contact_name.doAfterTextChanged { mViewModel.setName(it.toString()) }
        et_contact_phone_number.doAfterTextChanged { mViewModel.setPhone(it.toString()) }
        et_contact_email.doAfterTextChanged { mViewModel.setEmail(it.toString()) }

        mViewModel.getPhotoPath().observe(this, Observer {
            Glide
                .with(this)
                .load(it)
                .into(iv_contact_photo)
        })

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

        mViewModel.getContacts().observe(this, Observer { contacts ->
            adapter.setContacts(contacts)
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                readExternalStorageGranted =
                    grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select a picture"), PICK_PHOTO_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PHOTO_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = data?.data
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                val path = saveToInternalStorage(bitmap)
                mViewModel.setPhotoPath(path)
            }
        }
    }

    // This function converts bitmap, save it to
    // internal storage and returns its absolutePath
    private fun saveToInternalStorage(bitmapImage: Bitmap): String {
        val bitmap = Utils.getResizedBitmap(bitmapImage, 500)
        val imageName = Calendar.getInstance().time.toString()
        val file = File(this.filesDir, imageName) // it should create new file

        this.openFileOutput(imageName, Context.MODE_PRIVATE).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, it)
        }
        return file.absolutePath
    }

    private fun addContact() {
        val error = checkForErrors(textInputLayouts)
        if (!error) {
            mViewModel.insertContact()
            et_contact_name.setText("")
            et_contact_name.clearFocus()

            et_contact_phone_number.setText("")
            et_contact_phone_number.clearFocus()

            et_contact_email.setText("")
            et_contact_email.clearFocus()

            iv_contact_photo.setImageURI(null)

            bottomSheetBehavior.state = STATE_COLLAPSED
        }
    }

    private fun getTextInputLayouts(root: ViewGroup): ArrayList<TextInputLayout> {
        val textInputLayouts = ArrayList<TextInputLayout>()
        root.children.forEach {
            if (it is TextInputLayout) textInputLayouts.add(it)
        }
        return textInputLayouts
    }

    private fun checkForErrors(list: ArrayList<TextInputLayout>): Boolean {
        var error = false
        list.forEach {
            if (it.editText!!.text.toString().isBlank()) {
                error = true
                it.error = "Field must not be empty."
            } else it.error = null
        }
        return error
    }
}
