package com.nishant.herosblood.ui.fragments.signup

import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nishant.herosblood.R
import com.nishant.herosblood.api.RetrofitInstance
import com.nishant.herosblood.databinding.FragmentEmailBinding
import com.nishant.herosblood.models.*
import com.nishant.herosblood.util.Constants
import kotlinx.coroutines.runBlocking

class EmailFragment :
    Fragment(R.layout.fragment_email),
    View.OnKeyListener,
    View.OnClickListener {

    private lateinit var binding: FragmentEmailBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEmailBinding.bind(view)

        binding.btnSignUp.setOnClickListener {
            val email = binding.edtEmailEditText.text.toString()
            if (checkForEmailValidation(email)) {
                sendOtp(email)
            }
        }
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

        return true
    }

    private fun sendOtp(email: String) {

        val otp = (111111 until 999999).random()

        runBlocking {
            showLoadingBar()
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
        }
        hideLoadingBar()

        findNavController().navigate(
            EmailFragmentDirections.actionSignUpFragmentToOtpVerificationFragment(email, otp)
        )
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
}