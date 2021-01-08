package com.nishant.herosblood.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.nishant.herosblood.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_dashboard)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)

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

        dataViewModel.readUserDataStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.layoutShimmerEffect.startShimmer()
                }
                is Resource.Success -> {
                    user = response.data as UserData
                    isDataReceived = true
                    binding.layoutUser.visibility = View.VISIBLE
                    binding.user = response.data
                    binding.layoutShimmerEffect.stopShimmer()
                    binding.layoutShimmerEffect.visibility = View.GONE
                    user.let {
                        if (it.registered == "false") {
                            binding.ifUserNotRegistered.visibility = View.VISIBLE
                        } else {
                            binding.ifUserRegistered.visibility = View.VISIBLE
                        }
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
    }
}