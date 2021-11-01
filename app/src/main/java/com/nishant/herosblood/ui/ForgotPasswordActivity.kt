package com.nishant.herosblood.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nishant.herosblood.R
import com.nishant.herosblood.databinding.ActivityForgotPasswordBinding
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)

        binding.edtEmailEditText.addTextChangedListener {
            binding.edtEmail.error = null
        }

        authViewModel.sendPasswordResetEmailStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoadingBar()
                }
                is Resource.Success -> {
                    hideLoadingBar()
                    binding.edtEmail.error = null
                    binding.llUserDoesNotExist.visibility = View.GONE
                    showDialog()
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    if (response.message.toString().contains("There is no user record")) {
                        binding.llUserDoesNotExist.visibility = View.VISIBLE
                        binding.edtEmail.error = ""
                        binding.edtEmail.requestFocus()
                    } else {
                        binding.llUserDoesNotExist.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Check your Internet connection",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })

        binding.btnResetPassword.setOnClickListener {

            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

            val email = binding.edtEmailEditText.text.toString()
            if (checkEmailValidity(email)) {
                authViewModel.sendPasswordResetLink(email)
            }
        }

        binding.txtCreateAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Email Link Send")
            .setMessage("An Email with password reset link is send to you. Kindly check your inbox. Also check SPAM folder")
            .setNegativeButton("OK") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setPositiveButton("View Mail") { _, _ ->
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_APP_EMAIL)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            .show()
    }

    private fun checkEmailValidity(email: String): Boolean {
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

    private fun showLoadingBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        binding.progressBar.visibility = View.VISIBLE
        binding.layoutBackground.alpha = 0.3F
    }

    private fun hideLoadingBar() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.progressBar.visibility = View.GONE
        binding.layoutBackground.alpha = 1F
    }
}
