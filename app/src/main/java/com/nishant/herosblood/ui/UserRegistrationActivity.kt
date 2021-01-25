package com.nishant.herosblood.ui

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.nishant.herosblood.R
import com.nishant.herosblood.data.UserData
import com.nishant.herosblood.databinding.ActivityUserRegistrationBinding
import com.nishant.herosblood.util.InvalidInputChecker
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.util.ValidationInput
import com.nishant.herosblood.viewmodels.DataViewModel

class UserRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserRegistrationBinding
    private var user: UserData = UserData()
    private lateinit var dataViewModel: DataViewModel
    private val invalidInputChecker: InvalidInputChecker = InvalidInputChecker()
    private lateinit var animation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_registration)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        animation = binding.progressBar.drawable as AnimationDrawable

        user = intent.getSerializableExtra("UserData") as UserData

        val bloodType = resources.getStringArray(R.array.blood_group)
        val bloodGroupAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, bloodType)
        binding.bloodGroupList.setAdapter(bloodGroupAdapter)

        binding.btnSave.setOnClickListener {
            var isErrorPresent = false
            setUserData(user)
            invalidInputChecker.checkForRegistrationValidInputs(user) { error ->
                isErrorPresent = true
                when (error) {
                    is ValidationInput.EmptyBloodGroup -> binding.bloodType.error =
                        "Please provide Blood Group"
                    is ValidationInput.EmptyAddress -> binding.edtAddress.error = "Required"
                    is ValidationInput.EmptyState -> binding.edtState.error = "Required"
                    is ValidationInput.EmptyCity -> binding.edtCity.error = "Required"
                    is ValidationInput.EmptyPincode -> binding.edtPinCode.error = "Required"
                    is ValidationInput.EmptyPhoneNumber -> binding.edtPhoneNo.error = "Required"
                }
            }
            if (!isErrorPresent) {
                dataViewModel.saveUserData(user)
            }
        }

        dataViewModel.saveUserDataStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                    showLoadingBar()
                }
                is Resource.Success -> {
                    if (response.data == true) {
                        hideLoadingBar()
                        startActivity(Intent(this, UserDashboardActivity::class.java))
                        finish()
                    } else {
                        hideLoadingBar()
                        Toast.makeText(
                            this,
                            "Check Internet Connection",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    Toast.makeText(
                        this,
                        "Check Internet Connection",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun setUserData(user: UserData) {
        user.bloodGroup = binding.bloodGroupList.text.toString()
        user.state = binding.edtStateEditText.text.toString()
        user.city = binding.edtCityEditText.text.toString()
        user.pincode = binding.edtPinCodeEditText.text.toString()
        user.address = binding.edtAddressEditText.text.toString()
        user.fullAddress = user.address + user.city + user.state + user.pincode
        user.phoneNumber = binding.edtPhoneNoEditText.text.toString()
        user.registered = "true"
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