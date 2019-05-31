package com.example.phonebook

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dim: View = activity_main_dim
        val contactLabel: TextView = tv_contact_label

        bottomSheetBehavior = from(contact_bottom_sheet)

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
    }
}
