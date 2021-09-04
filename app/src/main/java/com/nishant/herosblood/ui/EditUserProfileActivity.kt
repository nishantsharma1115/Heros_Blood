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
import coil.load
import com.google.firebase.storage.FirebaseStorage
import com.nishant.herosblood.R
import com.nishant.herosblood.databinding.ActivityEditUserProfileBinding
import com.nishant.herosblood.models.UserData
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.viewmodels.DataViewModel
import com.theartofdev.edmodo.cropper.CropImage

class EditUserProfileActivity : AppCompatActivity() {

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(4, 4)
                .getIntent(this@EditUserProfileActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>
    private var user: UserData = UserData()
    private lateinit var dataViewModel: DataViewModel
    private lateinit var binding: ActivityEditUserProfileBinding
    private var isProfilePictureUpdated = false
    private var photoUri: Uri? = null
    private lateinit var selectedBloodGroup: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_user_profile)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        user = intent.getSerializableExtra("UserData") as UserData
        binding.currentUser = user

        if (user.profilePictureUrl != null) {
            binding.imgProfilePicture.load(user.profilePictureUrl)
        } else {
            binding.imgProfilePicture.load(R.drawable.profile_none)
        }

        val bloodType = resources.getStringArray(R.array.blood_group)
        val bloodGroupAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodType)
        binding.bloodGroupList.adapter = bloodGroupAdapter

        binding.bloodGroupList.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    selectedBloodGroup = bloodType[position]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    val error = binding.bloodGroupList.selectedView as TextView
                    error.error = "Required"
                    error.text = resources.getString(R.string.selectBloodGroup)
                }
            }

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {
            it?.let { uri ->
                binding.imgProfilePicture.setImageURI(uri)
                isProfilePictureUpdated = true
                photoUri = uri
            }
        }

        binding.changeProfilePicture.setOnClickListener {
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
            saveUserData()
        }

        binding.imgSave.setOnClickListener {
            saveUserData()
        }

        binding.btnCancel.setOnClickListener {
            onBackPressed()
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        dataViewModel.getProfilePictureStatus.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Loading -> {
                        showLoadingBar()
                    }
                    is Resource.Success -> {
                        hideLoadingBar()
                        FirebaseStorage.getInstance().reference.child("ProfilePicture")
                            .child(user.userId!!).downloadUrl.addOnSuccessListener { uri ->
                                updateUserData(uri)
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
            }
        )
        dataViewModel.saveUserDataStatus.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Loading -> {
                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                        showLoadingBar()
                    }
                    is Resource.Success -> {
                        if (response.data == true) {
                            hideLoadingBar()
                            onBackPressed()
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
            }
        )
    }

    private fun saveUserData() {
        if (isProfilePictureUpdated) {
            dataViewModel.uploadProfilePicture(user.userId!!, photoUri!!)
        } else if (isDataChange()) {
            user.apply {
                this.name = binding.name.text.toString()
                this.fullAddress = binding.address.text.toString()
                this.email = binding.email.text.toString()
                this.phoneNumber = binding.mobile.text.toString()
                this.bloodGroup = selectedBloodGroup
            }
            dataViewModel.saveUserData(user)
        }
    }

    private fun updateUserData(uri: Uri) {
        user.apply {
            this.name = binding.name.text.toString()
            this.fullAddress = binding.address.text.toString()
            this.email = binding.email.text.toString()
            this.phoneNumber = binding.mobile.text.toString()
            this.profilePictureUrl = uri.toString()
            this.bloodGroup = selectedBloodGroup
        }
        dataViewModel.saveUserData(user)
    }

    private fun isDataChange(): Boolean {
        if (
            binding.name.text.toString() == user.name &&
            binding.address.text.toString() == user.fullAddress &&
            binding.email.text.toString() == user.email &&
            binding.mobile.text.toString() == user.phoneNumber &&
            selectedBloodGroup == user.bloodGroup
        ) {
            return false
        }
        return true
    }

    private fun showLoadingBar() {
        binding.layoutBackground.alpha = 0.1F
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        binding.layoutBackground.alpha = 1F
        binding.progressBar.visibility = View.GONE
    }
}
