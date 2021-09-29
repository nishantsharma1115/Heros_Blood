package com.nishant.herosblood.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nishant.herosblood.R
import com.nishant.herosblood.databinding.ActivityRequestForBloodBinding
import com.nishant.herosblood.models.BloodRequestData
import com.nishant.herosblood.util.InvalidInputChecker
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.util.ValidationInput
import com.nishant.herosblood.util.location.LocationModel
import com.nishant.herosblood.viewmodels.DataViewModel

class RequestForBloodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestForBloodBinding
    private lateinit var dataViewModel: DataViewModel
    private lateinit var bloodType: Array<String>
    private var bloodRequestData: BloodRequestData = BloodRequestData()
    private lateinit var userLocationData: LocationModel
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestForBloodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        userLocationData = intent.getSerializableExtra("UserLocationData") as LocationModel
        userId = intent.getStringExtra("userId") as String

        bloodType = resources.getStringArray(R.array.blood_group) as Array<String>
        autoCompleteListAdapter(bloodType, binding.actvBloodGroup)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.txtWhyThis.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Contact Details")
                .setMessage("Using this details Donor will easily contact you. Kindly fill all the data Accurately")
                .setPositiveButton("OK") { _, _ ->

                }
                .show()
        }

        binding.txtWhyThis2.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Address Details")
                .setMessage("Using this details Donor will easily contact you. Kindly fill all the data Accurately")
                .setPositiveButton("OK") { _, _ ->

                }
                .show()
        }

        binding.bloodUnit.addTextChangedListener {
            binding.edtBloodUnit.error = null
        }
        binding.firstName.addTextChangedListener {
            binding.edtFirstName.error = null
        }
        binding.lastName.addTextChangedListener {
            binding.edtLastName.error = null
        }
        binding.mobileNumber.addTextChangedListener {
            binding.edtMobileNumber.error = null
        }
        binding.address.addTextChangedListener {
            binding.edtAddress.error = null
        }

        binding.btnSaveBloodRequest.setOnClickListener {
            bloodRequestData = setBloodRequestData()
            if (isBloodGroupSelected()) {
                if (validateInput(bloodRequestData)) {
                    Log.d("Calling Fun", "Yes")
                    dataViewModel.saveBloodRequest(bloodRequestData)
                }
            } else {
                Toast.makeText(this, "Please select Blood Group", Toast.LENGTH_SHORT).show()
            }
        }
        dataViewModel.getSaveBloodRequestStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoadingBar()
                }
                is Resource.Success -> {
                    hideLoadingBar()
                    Toast.makeText(this, "Blood Request Done", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    Log.d("ErrorMessage", response.message.toString())
                    Toast.makeText(this, response.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showLoadingBar() {
        binding.layout.alpha = 0.3F
        binding.progressBar.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideLoadingBar() {
        binding.progressBar.visibility = View.GONE
        binding.layout.alpha = 1F
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private fun setBloodRequestData(): BloodRequestData {
        return BloodRequestData(
            binding.etBloodGroup.editText?.text.toString(),
            binding.bloodUnit.text.toString(),
            binding.checkBoxIsCritical.isChecked.toString(),
            binding.firstName.text.toString(),
            binding.lastName.text.toString(),
            binding.firstName.text.toString() + binding.lastName.text.toString(),
            binding.mobileNumber.text.toString(),
            binding.address.text.toString(),
            userId,
            userLocationData
        )
    }

    private fun validateInput(bloodRequestData: BloodRequestData): Boolean {

        var isValid = false

        InvalidInputChecker.checkForRequestBloodInputs(bloodRequestData) { error ->
            when (error) {
                is ValidationInput.EmptyBloodUnit -> binding.edtBloodUnit.error = "Required"
                is ValidationInput.EmptyFirstName -> binding.edtFirstName.error = "Required"
                is ValidationInput.EmptyLastName -> binding.edtLastName.error = "Required"
                is ValidationInput.EmptyPhoneNumber -> binding.edtMobileNumber.error = "Required"
                is ValidationInput.EmptyAddress -> binding.edtAddress.error = "Required"
                else -> isValid = true
            }
        }

        return isValid
    }

    private fun autoCompleteListAdapter(
        list: Array<String>,
        autoCompleteTextView: AutoCompleteTextView
    ) {
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, list)
        autoCompleteTextView.setAdapter(arrayAdapter)
    }

    private fun isBloodGroupSelected(): Boolean {
        return !(binding.etBloodGroup.editText?.text?.isEmpty() == true || binding.etBloodGroup.editText?.text?.toString() == "Blood group")
    }
}