package com.nishant.herosblood.ui

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.nishant.herosblood.R
import com.nishant.herosblood.adapters.BloodTypeListAdapters
import com.nishant.herosblood.data.UserData
import com.nishant.herosblood.databinding.ActivityUserDashboardBinding
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.viewmodels.DataViewModel
import java.io.Serializable

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDashboardBinding
    private lateinit var dataViewModel: DataViewModel
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: UserData = UserData()
    private var isDataReceived: Boolean = false
    private lateinit var animation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_dashboard)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        animation = binding.progressBar.drawable as AnimationDrawable
        val bloodTypeList = resources.getStringArray(R.array.blood_group).toList()

        dataViewModel.getAllDonors()
        dataViewModel.getAllDonorsStatus.observe(this, {
            when (it) {
                is Resource.Loading -> {
                    showLoadingBar()
                }
                is Resource.Success -> {
                    hideLoadingBar()
                    binding.rvDonorListUserDashboard.adapter = BloodTypeListAdapters(this, bloodTypeList, it.data!!)
                    binding.rvDonorListUserDashboard.layoutManager = LinearLayoutManager(this)
                    binding.rvDonorListUserDashboard.setHasFixedSize(true)
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_LONG).show()
                }
            }
        })

        binding.txtDonorClickHere.setOnClickListener {
            if (isDataReceived) {
                val intent = Intent(this, UserRegistrationActivity::class.java)
                intent.putExtra("UserData", user as Serializable)
                startActivity(intent)
            }
        }
        mAuth.currentUser?.let { firebaseUser ->
            dataViewModel.readUserData(firebaseUser.uid)
        }
        binding.imgProfilePicture.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("UserData", user as Serializable)
            startActivity(intent)
        }
        dataViewModel.readUserDataStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.layoutShimmerEffect.startShimmer()
                }
                is Resource.Success -> {
                    user = response.data as UserData
                    isDataReceived = true
                    binding.layoutUser.visibility = View.VISIBLE
                    binding.user = user
                    binding.layoutShimmerEffect.stopShimmer()
                    binding.layoutShimmerEffect.visibility = View.GONE
                    user.let {
                        if (it.registered == "false") {
                            binding.registered.visibility = View.GONE
                        } else if (it.registered == "true") {
                            binding.notRegistered.visibility = View.GONE
                        }
                    }
                    binding.imgProfilePicture.bringToFront()
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
    }

    private fun showLoadingBar() {
        binding.layoutBackground.alpha = 0.1F
        binding.progressBar.visibility = View.VISIBLE
        animation.start()
    }

    private fun hideLoadingBar() {
        binding.layoutBackground.alpha = 1F
        binding.progressBar.visibility = View.GONE
        animation.stop()
    }
}