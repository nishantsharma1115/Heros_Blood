package com.nishant.herosblood.adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("setEmailWithDescription")
fun TextView.setEmailWithDescription(email: String) {
    val desc = "Please type the verification code sent to $email"
    this.text = desc
}