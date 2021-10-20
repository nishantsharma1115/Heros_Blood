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
import androidx.lifecycle.ViewModelProvider
import com.nishant.herosblood.databinding.ActivityLoginBinding
import com.nishant.herosblood.util.InvalidInputChecker
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.util.ValidationInput
import com.nishant.herosblood.viewmodels.AuthViewModel

class LoginActivity : AppCompatActivity(), View.OnKeyListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        binding.layoutBackground.setOnClickListener {
            hideKeyboard()
        }
        binding.txtLogo.setOnClickListener {
            hideKeyboard()
        }
        binding.txtSignIn.setOnClickListener {
            hideKeyboard()
        }
        binding.txtNewUser.setOnClickListener {
            hideKeyboard()
        }
        binding.txtForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
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

        binding.edtEmailEditText.addTextChangedListener {
            binding.edtEmail.error = null
        }
        binding.edtPasswordEditText.addTextChangedListener {
            binding.edtPassword.error = null
        }

        viewModel.loginStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoadingBar()
                }
                is Resource.Success -> {
                    hideLoadingBar()
                    val intent = Intent(this, UserDashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    response.message?.let {
                        showError(it)
                    }
                }
            }
        })
    }

    private fun showError(msg: String) {
        if ("There is no user record corresponding to this identifier" in msg) {
            binding.edtEmail.error = "User not found!!"
        } else if ("The password is invalid" in msg) {
            binding.edtPassword.error = "Incorrect Password"
        }
    }

    private fun loginUser(email: String, password: String) {
        InvalidInputChecker.checkForLoginValidInputs(email, password) { error ->
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

    private fun hideKeyboard() {
        currentFocus.let { focus ->
            val inputMethodManager =
                ContextCompat.getSystemService(this, InputMethodManager::class.java)
            inputMethodManager?.hideSoftInputFromWindow(focus?.windowToken, 0)
        }
    }

    private fun showLoadingBar() {
        binding.edtEmail.error = null
        binding.edtPassword.error = null
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        binding.layoutBackground.alpha = 0.1F
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        binding.layoutBackground.alpha = 1F
        binding.progressBar.visibility = View.GONE
    }
}