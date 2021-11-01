package com.nishant.herosblood.ui.fragments.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nishant.herosblood.R
import com.nishant.herosblood.databinding.FragmentNameAndPasswordBinding
import com.nishant.herosblood.models.UserData
import com.nishant.herosblood.ui.UserDashboardActivity
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.viewmodels.AuthViewModel
import com.nishant.herosblood.viewmodels.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NameAndPasswordFragment : Fragment(R.layout.fragment_name_and_password) {

    private lateinit var binding: FragmentNameAndPasswordBinding
    private val args: NameAndPasswordFragmentArgs by navArgs()
    private val authViewModel by viewModels<AuthViewModel>()
    private val dataViewModel by viewModels<DataViewModel>()
    private var user: UserData = UserData()
    private val somethingWentWrong = "Something went wrong. Check Internet Connection"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        binding = FragmentNameAndPasswordBinding.bind(view)
        val email = args.email

        binding.btnSignUp.setOnClickListener {

            val name = binding.edtFullNameEditText.text.toString()
            val password = binding.edtPasswordEditText.text.toString()
            val confirmPassword = binding.edtConfirmPasswordEditText.text.toString()

            if (validateInputs(name, password, confirmPassword)) {
                user.name = name
                user.email = email
                user.registered = "false"
                signUpUser(user, password)
            }
        }

        authViewModel.signUpStatus.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoadingBar()
                }
                is Resource.Success -> {
                    if (response.data == true) {
                        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                        firebaseUser?.let {
                            user.userId = it.uid
                        }
                        dataViewModel.saveUserData(user)
                    } else {
                        hideLoadingBar()
                        Toast.makeText(
                            context,
                            somethingWentWrong,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    Toast.makeText(
                        context,
                        response.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        dataViewModel.saveUserDataStatus.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    if (response.data == true) {
                        hideLoadingBar()
                        activity?.startActivity(Intent(context, UserDashboardActivity::class.java))
                        activity?.finish()
                    } else {
                        hideLoadingBar()
                        Toast.makeText(
                            context,
                            somethingWentWrong,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    Toast.makeText(
                        context,
                        somethingWentWrong,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun signUpUser(user: UserData, password: String) {
        authViewModel.signUpUser(
            user.email!!,
            password
        )
    }

    private fun validateInputs(
        name: String,
        password: String,
        confirmPassword: String
    ): Boolean {

        if (name.isEmpty()) {
            binding.edtFullName.error = "Cannot be empty"
            return false
        }
        if (password.isEmpty()) {
            binding.edtPassword.error = "Cannot be empty"
            return false
        }
        if (confirmPassword.isEmpty()) {
            binding.edtConfirmPassword.error = "Cannot be empty"
            return false
        }
        if (password.length < 8) {
            binding.edtPassword.error = "Password should be greater than 8"
            return false
        }
        if (password != confirmPassword) {
            binding.edtConfirmPassword.error = "Confirm Password should be equal to Password"
            return false
        }

        return true
    }

    private fun showLoadingBar() {
        binding.layoutBackgroundFinal.alpha = 0.1F
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        binding.layoutBackgroundFinal.alpha = 1F
        binding.progressBar.visibility = View.GONE
    }
}
