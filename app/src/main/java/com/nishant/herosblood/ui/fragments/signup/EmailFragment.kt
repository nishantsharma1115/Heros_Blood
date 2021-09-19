package com.nishant.herosblood.ui.fragments.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nishant.herosblood.R
import com.nishant.herosblood.api.RetrofitInstance
import com.nishant.herosblood.databinding.FragmentEmailBinding
import com.nishant.herosblood.models.smsbody.*
import com.nishant.herosblood.ui.LoginActivity
import com.nishant.herosblood.util.Constants
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.viewmodels.AuthViewModel

class EmailFragment :
    Fragment(R.layout.fragment_email),
    View.OnKeyListener,
    View.OnClickListener {

    private lateinit var binding: FragmentEmailBinding
    private lateinit var authViewModel: AuthViewModel
    private var email = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEmailBinding.bind(view)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        binding.btnSignUp.setOnClickListener {
            binding.edtEmail.error = null
            email = binding.edtEmailEditText.text.toString().trim()
            if (checkForEmailValidation(email)) {
                checkForExistingUser(email)
            }
        }

        binding.edtEmailEditText.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                binding.edtEmail.error = null
            }
        }

        authViewModel.loginStatus.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoadingBar()
                }
                is Resource.Success -> {
                    hideLoadingBar()
                    binding.edtEmail.error = "User already exist"
                    binding.edtEmail.requestFocus()
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    response.message?.let {
                        if ("There is no user record corresponding to this identifier" in it) {
                            sendOtp(email)
                        } else if ("The password is invalid" in it) {
                            binding.edtEmail.error = "User already exist"
                        }
                    }
                }
            }
        })

        binding.txtSignIn.setOnClickListener {
            Intent(activity, LoginActivity::class.java).apply {
                startActivity(this)
                activity?.finish()
            }
        }
    }

    private fun checkForExistingUser(email: String) {
        authViewModel.loginUser(email, PASSWORD)
    }

    private fun checkForEmailValidation(email: String): Boolean {

        if (email.isEmpty()) {
            binding.edtEmail.error = "Can not be Empty"
            binding.edtEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.error = "Invalid Email Format"
            binding.edtEmail.requestFocus()
            return false
        }

        if (email == "heroesblood02@gmail.com") {
            binding.edtEmail.error = "User already exist"
            binding.edtEmail.requestFocus()
            return false
        }

        return true
    }

    private fun sendOtp(email: String) {

        val otp = (111111 until 999999).random()

        showLoadingBar()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            RetrofitInstance.api.sendVerificationMail(
                SmsApiBody(
                    listOf(Content("text/plain", "OTP is $otp")),
                    From(Constants.COMPANY_MAIL, Constants.COMPANY_NAME),
                    listOf(
                        Personalization(
                            "Otp Verification",
                            listOf(To(email, ""))
                        )
                    ),
                    ReplyTo(Constants.COMPANY_MAIL, Constants.COMPANY_NAME)
                )
            )
            hideLoadingBar()
            authViewModel.clearLoginStatus()
            findNavController().navigate(
                EmailFragmentDirections.actionSignUpFragmentToOtpVerificationFragment(email, otp)
            )
        }
    }

    private fun showLoadingBar() {
        binding.layoutBackground.alpha = 0.1F
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        binding.layoutBackground.alpha = 1F
        binding.progressBar.visibility = View.GONE
    }

    override fun onKey(p0: View?, i: Int, keyEvent: KeyEvent?): Boolean {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent?.action == KeyEvent.ACTION_DOWN) {
            val email = binding.edtEmailEditText.text.toString()
            if (checkForEmailValidation(email)) {
                sendOtp(email)
            }
        }
        return false
    }

    override fun onClick(view: View?) {
        view?.let { it ->
            if (it.id == R.id.layout_background || it.id == R.id.txt_logo || it.id == R.id.txt_already || it.id == R.id.txt_createAccount) {
                activity?.currentFocus.let { focus ->
                    val inputMethodManager =
                        ContextCompat.getSystemService(
                            requireContext(),
                            InputMethodManager::class.java
                        )
                    inputMethodManager?.hideSoftInputFromWindow(focus?.windowToken, 0)
                }
            }
        }
    }

    companion object {
        private const val PASSWORD = "000000000"
    }
}