package com.nishant.herosblood.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.nishant.herosblood.R
import com.nishant.herosblood.data.UserData
import com.nishant.herosblood.databinding.ActivityUserRegistrationBinding
import com.nishant.herosblood.util.InvalidInput
import com.nishant.herosblood.viewmodels.DataViewModel

class UserRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserRegistrationBinding
    private var user: UserData = UserData()
    private lateinit var dataViewModel: DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_registration)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        user = intent.getSerializableExtra("UserData") as UserData

        val bloodType = resources.getStringArray(R.array.blood_group)
        val bloodGroupAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, bloodType)
        binding.bloodGroupList.setAdapter(bloodGroupAdapter)

        binding.btnSave.setOnClickListener {
            setUserData(user)
            dataViewModel.saveUserData(user, "Registration") { error ->
                when (error) {
                    is InvalidInput.EmptyBloodGroup -> binding.bloodType.error = "Please provide Blood Group"
                    is InvalidInput.EmptyAddress -> binding.edtAddress.error = "Required"
                    is InvalidInput.EmptyState -> binding.edtState.error = "Required"
                    is InvalidInput.EmptyCity -> binding.edtCity.error = "Required"
                    is InvalidInput.EmptyPincode -> binding.edtPinCode.error = "Required"
                    is InvalidInput.EmptyPhoneNumber -> binding.edtPhoneNo.error = "Required"
                }
            }
        }
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
}