package com.nishant.herosblood.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.nishant.herosblood.R
import com.nishant.herosblood.databinding.ActivityUserDashboardBinding
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.viewmodels.DataViewModel

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDashboardBinding
    private lateinit var dataViewModel: DataViewModel
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_dashboard)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        mAuth.currentUser?.let { user ->
            dataViewModel.readUserData(user.uid)
        }

        dataViewModel.readUserDataStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.layoutShimmerEffect.startShimmer()
                }
                is Resource.Success -> {
                    val user = response.data
                    binding.layoutUser.visibility = View.VISIBLE
                    binding.user = response.data
                    binding.layoutShimmerEffect.stopShimmer()
                    binding.layoutShimmerEffect.visibility = View.GONE
                    user?.let {
                        if (it.registered == "false") {
                            binding.ifUserNotRegistered.visibility = View.VISIBLE
                        }else {
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