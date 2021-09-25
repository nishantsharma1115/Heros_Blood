package com.nishant.herosblood.adapters

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.nishant.herosblood.R

@BindingAdapter("setEmailWithDescription")
fun TextView.setEmailWithDescription(email: String) {
    val desc = "Please type the verification code sent to $email"
    val spannable = SpannableString(desc)
    spannable.setSpan(
        ForegroundColorSpan(resources.getColor(R.color.redColor)),
        42,
        desc.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    this.text = spannable
}

@BindingAdapter("setWeight")
fun TextView.setWeight(weight: String) {
    this.text = weight + " KG"
}