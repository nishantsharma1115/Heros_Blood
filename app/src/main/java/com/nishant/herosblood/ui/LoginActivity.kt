package com.nishant.herosblood.ui

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private lateinit var animation: AnimationDrawable
    private val invalidInputChecker: InvalidInputChecker = InvalidInputChecker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        animation = binding.progressBar.drawable as AnimationDrawable

        binding.layoutBackground.setOnClickListener(this)
        binding.txtLogo.setOnClickListener(this)
        binding.txtSignIn.setOnClickListener(this)
        binding.txtNewUser.setOnClickListener(this)

        binding.txtCreateAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
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
                    Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun loginUser(email: String, password: String) {
        invalidInputChecker.checkForLoginValidInputs(email, password) { error ->
            when (error) {
                is ValidationInput.EmptyEmail -> binding.edtEmail.error = "Can not be empty"
                is ValidationInput.EmptyPassword -> binding.edtPassword.error = "Can not be empty"
                is ValidationInput.InvalidEmailPattern -> binding.edtEmail.error = "Please provide valid email Address"
                is ValidationInput.ShortPasswordLength -> binding.edtPassword.error = "Password length should be greater than 8"
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

        }
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
}