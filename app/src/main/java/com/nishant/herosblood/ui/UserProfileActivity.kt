package com.nishant.herosblood.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.nishant.herosblood.R
import com.nishant.herosblood.data.UserData
import com.nishant.herosblood.databinding.ActivityUserProfileBinding
import java.io.Serializable

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private var user: UserData = UserData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile)

        user = intent.getSerializableExtra("UserData") as UserData
        binding.user = user

        val widthDp = resources.displayMetrics.run { widthPixels / density }
        binding.guideline.setGuidelineBegin(widthDp.toInt() + widthDp.toInt() / 2)

        if (user.profilePictureUrl != null) {
            binding.imgProfilePicture.load(user.profilePictureUrl)
        }

        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.txtEdit.setOnClickListener {
            Intent(this, EditUserProfileActivity::class.java).also {
                it.putExtra("UserData", user as Serializable)
                startActivity(it)
            }
        }
    }
}