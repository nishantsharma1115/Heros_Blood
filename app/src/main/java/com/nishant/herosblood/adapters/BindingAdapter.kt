package com.nishant.herosblood.adapters

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import com.nishant.herosblood.R
import com.nishant.herosblood.models.BloodRequestData
import de.hdodenhof.circleimageview.CircleImageView

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

@BindingAdapter("nishant:setImageCircle")
fun CircleImageView.setImageCircle(url: String?) {
    if (url.isNullOrEmpty()) {
        this.load(R.drawable.profile_none)
    } else {
        this.load(url) {
            placeholder(R.drawable.profile_none)
        }
    }
}

@BindingAdapter("nishant:setBloodUnit")
fun TextView.setBloodUnit(request: BloodRequestData) {
    this.text = "${request.unitOfBloodRequired} Unit (${request.bloodGroup} Blood Group)"
}