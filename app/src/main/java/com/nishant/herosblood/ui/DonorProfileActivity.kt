package com.nishant.herosblood.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import coil.load
import com.nishant.herosblood.R
import com.nishant.herosblood.data.UserData
import com.nishant.herosblood.databinding.ActivityDonorProfileBinding

class DonorProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonorProfileBinding
    private var user: UserData = UserData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_donor_profile)

        user = intent.getSerializableExtra("UserData") as UserData
        binding.user = user

        binding.donorProfilePicture.load(user.profilePictureUrl) {
            this.placeholder(R.drawable.profile_none)
        }
    }
}