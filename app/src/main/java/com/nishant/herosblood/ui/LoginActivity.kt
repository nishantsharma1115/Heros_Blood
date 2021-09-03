package com.nishant.herosblood.ui

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.nishant.herosblood.R
import com.nishant.herosblood.databinding.ActivityLoginBinding
import com.nishant.herosblood.util.InvalidInputChecker
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.util.ValidationInput
import com.nishant.herosblood.viewmodels.AuthViewModel

class LoginActivity : AppCompatActivity(), View.OnKeyListener, View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    private val invalidInputChecker: InvalidInputChecker = InvalidInputChecker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        binding.layoutBackground.setOnClickListener(this)
        binding.txtLogo.setOnClickListener(this)
        binding.txtSignIn.setOnClickListener(this)
        binding.txtNewUser.setOnClickListener(this)
        binding.txtForgotPassword.setOnClickListener(this)

        binding.edtEmailEditText.addTextChangedListener {
            binding.edtEmail.error = null
        }
        binding.edtPasswordEditText.addTextChangedListener {
            binding.edtPassword.error = null
        }

        binding.txtCreateAccount.setOnClickListener {
            Intent(this, SignUpActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }
        binding.btnLogin.setOnClickListener {
            loginUser(
                binding.edtEmailEditText.text.toString(),
                binding.edtPasswordEditText.text.toString()
            )
        }

        viewModel.loginStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    if (binding.edtEmail.isErrorEnabled) {
                        binding.edtEmail.isErrorEnabled = false
                    }
                    if (binding.edtPassword.isErrorEnabled) {
                        binding.edtPassword.isErrorEnabled = false
                    }
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                    showLoadingBar()
                }
                is Resource.Success -> {
                    hideLoadingBar()
                    startActivity(Intent(this, UserDashboardActivity::class.java))
                    finish()
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    response.message?.let {
                        if ("There is no user record corresponding to this identifier" in it) {
                            binding.edtEmail.error = "User not found!!"
                        } else if ("The password is invalid" in it) {
                            binding.edtPassword.error = "Incorrect Password"
                        }
                    }
                }
            }
        })
    }

    private fun loginUser(email: String, password: String) {
        invalidInputChecker.checkForLoginValidInputs(email, password) { error ->
            when (error) {
                is ValidationInput.EmptyEmail -> binding.edtEmail.error = "Can not be empty"
                is ValidationInput.EmptyPassword -> binding.edtPassword.error = "Can not be empty"
                is ValidationInput.InvalidEmailPattern -> binding.edtEmail.error =
                    "Please provide valid email Address"
                is ValidationInput.ShortPasswordLength -> binding.edtPassword.error =
                    "Password length should be greater than 8"
                is ValidationInput.ValidInput -> {
                    viewModel.loginUser(email, password)
                }
                else -> Toast.makeText(this, "Something wrong happen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onKey(p0: View?, i: Int, keyEvent: KeyEvent?): Boolean {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent?.action == KeyEvent.ACTION_DOWN) {
            loginUser(
                binding.edtEmailEditText.text.toString(),
                binding.edtPasswordEditText.text.toString()
            )
        }
        return false
    }

    override fun onClick(view: View?) {
        view?.let { it ->
            if (it.id == R.id.layout_background || it.id == R.id.txt_logo || it.id == R.id.txt_signIn || it.id == R.id.txt_newUser) {
                currentFocus.let { focus ->
                    val inputMethodManager =
                        ContextCompat.getSystemService(this, InputMethodManager::class.java)
                    inputMethodManager?.hideSoftInputFromWindow(focus?.windowToken, 0)
                }
            }
            if (it.id == R.id.txtForgotPassword) {
                startActivity(Intent(this, ForgotPasswordActivity::class.java))
            }

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
}