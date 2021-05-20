package com.nishant.herosblood.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.nishant.herosblood.R
import com.nishant.herosblood.databinding.ActivityUserProfileBinding
import com.nishant.herosblood.models.UserData
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.viewmodels.DataViewModel
import kotlinx.android.synthetic.main.activity_user_profile.view.*
import java.io.Serializable

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private var user: UserData = UserData()
    private lateinit var dataViewModel: DataViewModel
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        dataViewModel.readUserDataStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.layoutBackground.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.layoutBackground.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    user = response.data as UserData
                    binding.user = user
                    binding.availabilityToggle.isChecked = user.available.toBoolean()

                    binding.availabilityToggle.setOnCheckedChangeListener { button, b ->
                        if (!b) {
                            button.availabilityToggle.thumbTintList =
                                ContextCompat.getColorStateList(this, R.color.greyColor)
                            user.available = "false"
                            dataViewModel.updateUserAvailability(user.userId!!, user.available)
                        } else {
                            button.availabilityToggle.thumbTintList =
                                ContextCompat.getColorStateList(this, R.color.redColor)
                            user.available = "true"
                            dataViewModel.updateUserAvailability(user.userId!!, user.available)
                        }
                    }

                    if (user.profilePictureUrl != null) {
                        binding.imgProfilePicture.load(user.profilePictureUrl)
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(
                        this,
                        response.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })

        //binding.availabilityToggle.isChecked = user.isAvailable.toBoolean()

        val widthDp = resources.displayMetrics.run { widthPixels / density }
        binding.guideline.setGuidelineBegin(widthDp.toInt() + widthDp.toInt() / 2)

        dataViewModel.updateUserAvailabilityStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.layoutBackground.alpha = 0.4f
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                }
                is Resource.Success -> {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    binding.progressBar.visibility = View.GONE
                    binding.layoutBackground.alpha = 1f

                    if (response.data != true) {
                        Toast.makeText(this, "Network Error", Toast.LENGTH_LONG).show()
                        binding.availabilityToggle.isChecked = !user.available.toBoolean()
                    }
                }
                is Resource.Error -> {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    binding.progressBar.visibility = View.GONE
                    binding.layoutBackground.alpha = 1f
                    Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
                }
            }
        })

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

    override fun onResume() {
        val firebaseUser = mAuth.currentUser
        if (firebaseUser != null) {
            dataViewModel.readUserData(firebaseUser.uid)
        }
        super.onResume()
    }
}