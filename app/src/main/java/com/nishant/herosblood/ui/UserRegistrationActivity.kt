package com.nishant.herosblood.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.storage.FirebaseStorage
import com.nishant.herosblood.R
import com.nishant.herosblood.databinding.ActivityUserRegistrationBinding
import com.nishant.herosblood.models.UserData
import com.nishant.herosblood.util.InvalidInputChecker
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.util.ValidationInput
import com.nishant.herosblood.viewmodels.DataViewModel
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_user_profile.view.*

class UserRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserRegistrationBinding
    private lateinit var dataViewModel: DataViewModel
    private val invalidInputChecker: InvalidInputChecker = InvalidInputChecker()
    private lateinit var bloodGroup: String
    private lateinit var bloodType: Array<String>
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>
    private var isProfileChanged = false
    private lateinit var photoUri: Uri
    private var downloadUrl: Uri? = null

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(4, 4)
                .getIntent(this@UserRegistrationActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_registration)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        val user = intent.getSerializableExtra("UserData") as UserData
        setBloodSpinner()
        binding.bloodGroupList.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    bloodGroup = bloodType[position]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    val error = binding.bloodGroupList.selectedView as TextView
                    error.error = "Required"
                    error.text = resources.getString(R.string.selectBloodGroup)
                }
            }

        binding.availabilityToggle.setOnCheckedChangeListener { button, b ->
            if (!b) {
                button.availabilityToggle.thumbTintList =
                    ContextCompat.getColorStateList(this, R.color.greyColor)
                user.available = "false"
            } else {
                button.availabilityToggle.thumbTintList =
                    ContextCompat.getColorStateList(this, R.color.redColor)
                user.available = "true"
            }
        }

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {
            it?.let { uri ->
                binding.imgProfilePicture.setImageURI(uri)
                isProfileChanged = true
                photoUri = uri
            }
        }

        binding.imgUploadImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                cropActivityResultLauncher.launch(null)
            }
        }

        binding.btnSave.setOnClickListener {
            setUserData(user)
            if (validateInput(user)) {
                if (isProfileChanged) {
                    dataViewModel.uploadProfilePicture(user.userId!!, photoUri)
                } else {
                    updateUserData(user, Uri.parse("null"))
                }
            }
        }

        dataViewModel.getProfilePictureStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoadingBar()
                }
                is Resource.Success -> {
                    hideLoadingBar()
                    FirebaseStorage.getInstance().reference.child("ProfilePicture")
                        .child(user.userId!!).downloadUrl.addOnSuccessListener { uriFromFirebase ->
                            downloadUrl = uriFromFirebase
                            updateUserData(user, uriFromFirebase)
                        }
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    Toast.makeText(
                        this,
                        resources.getString(R.string.checkInternetConnection),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })

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
        user.bloodGroup = bloodGroup
        user.state = binding.edtStateEditText.text.toString()
        user.city = binding.edtCityEditText.text.toString()
        user.pincode = binding.edtPinCodeEditText.text.toString()
        user.address = binding.edtAddressEditText.text.toString()
        user.fullAddress = user.address + " " + user.city + " " + user.state + " " + user.pincode
        user.phoneNumber = binding.edtPhoneNoEditText.text.toString()
        user.registered = "true"
    }

    private fun validateInput(user: UserData): Boolean {

        var isValid = false

        if (user.bloodGroup == "Select Blood Group") {
            val errorShown = binding.bloodGroupList.selectedView as TextView
            errorShown.error = "Required"
            errorShown.text = resources.getString(R.string.selectBloodGroup)
            return false
        }

        invalidInputChecker.checkForRegistrationValidInputs(user) { error ->
            when (error) {
                is ValidationInput.EmptyAddress -> binding.edtAddress.error = "Required"
                is ValidationInput.EmptyState -> binding.edtState.error = "Required"
                is ValidationInput.EmptyCity -> binding.edtCity.error = "Required"
                is ValidationInput.EmptyPincode -> binding.edtPinCode.error = "Required"
                is ValidationInput.EmptyPhoneNumber -> binding.edtPhoneNo.error = "Required"
                else -> isValid = true
            }
        }

        return isValid
    }

    private fun updateUserData(user: UserData, url: Uri) {
        if (url.toString() != "null") {
            user.profilePictureUrl = url.toString()
        } else {
            user.profilePictureUrl = ""
        }
        dataViewModel.saveUserData(user)
    }

    private fun showLoadingBar() {
        binding.layoutBackground.alpha = 0.1F
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        binding.layoutBackground.alpha = 1F
        binding.progressBar.visibility = View.GONE
    }

    private fun setBloodSpinner() {
        bloodType = resources.getStringArray(R.array.blood_group) as Array<String>
        val bloodGroupAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodType)
        binding.bloodGroupList.adapter = bloodGroupAdapter
    }
}