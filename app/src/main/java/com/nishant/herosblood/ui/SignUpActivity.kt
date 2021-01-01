package com.nishant.herosblood.ui

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.nishant.herosblood.R
import com.nishant.herosblood.databinding.ActivitySignUpBinding
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.viewmodels.AuthViewModel

class SignUpActivity : AppCompatActivity(), View.OnKeyListener, View.OnClickListener {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var animation: AnimationDrawable
    private val canNotBeEmpty = "Can not be empty"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        animation = binding.progressBar.drawable as AnimationDrawable

        binding.layoutBackground.setOnClickListener(this)
        binding.txtLogo.setOnClickListener(this)
        binding.txtCreateAccount.setOnClickListener(this)
        binding.txtAlready.setOnClickListener(this)
        binding.btnSignUp.setOnClickListener {
            signUpUser(
                binding.edtEmailEditText.text.toString(),
                binding.edtPasswordEditText.text.toString()
            )
        }

        viewModel.signUpStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoadingBar()
                }
                is Resource.Success -> {
                    hideLoadingBar()
                    startActivity(Intent(this, UserDashboardActivity::class.java))
                    finish()
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_LONG).show()
                }
            }
        })
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

    private fun signUpUser(email: String, password: String) {
        if (binding.edtFullNameEditText.text.isNullOrEmpty()) {
            binding.edtFullName.error = canNotBeEmpty
            return
        }
        if (email.isEmpty()) {
            binding.edtEmail.error = canNotBeEmpty
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.error = "Please provide valid email Address"
            return
        }
        if (password.isEmpty()) {
            binding.edtPassword.error = canNotBeEmpty
            return
        }
        if (password.length < 8) {
            binding.edtPassword.error = "Password length should be greater than 8"
            return
        }
        if (binding.edtConfirmPasswordEditText.text.isNullOrEmpty()) {
            binding.edtConfirmPassword.error = canNotBeEmpty
            return
        }
        if (!password.equals(binding.edtConfirmPasswordEditText.text)) {
            binding.edtConfirmPassword.error = "Password and confirm Password must be same"
            return
        }
        viewModel.signUpUser(email, password)
    }

    override fun onKey(p0: View?, i: Int, keyEvent: KeyEvent?): Boolean {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent?.action == KeyEvent.ACTION_DOWN) {
            viewModel.signUpUser(
                binding.edtEmailEditText.text.toString(),
                binding.edtPasswordEditText.text.toString()
            )
        }
        return false
    }

    override fun onClick(view: View?) {
        view?.let { it ->
            if (it.id == R.id.layout_background || it.id == R.id.txt_logo || it.id == R.id.txt_already || it.id == R.id.txt_createAccount) {
                currentFocus.let { focus ->
                    val inputMethodManager =
                        ContextCompat.getSystemService(this, InputMethodManager::class.java)
                    inputMethodManager?.hideSoftInputFromWindow(focus?.windowToken, 0)
                }
            }
        }
    }
}