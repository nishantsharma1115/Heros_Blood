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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nishant.herosblood.R
import com.nishant.herosblood.data.UserData
import com.nishant.herosblood.databinding.ActivitySignUpBinding
import com.nishant.herosblood.util.ValidationInput
import com.nishant.herosblood.util.InvalidInputChecker
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.viewmodels.AuthViewModel
import com.nishant.herosblood.viewmodels.DataViewModel

class SignUpActivity : AppCompatActivity(), View.OnKeyListener, View.OnClickListener {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dataViewModel: DataViewModel
    private lateinit var animation: AnimationDrawable
    private var user: UserData = UserData()
    private val somethingWentWrong = "Something went wrong. Check Internet Connection"
    private val invalidInputChecker: InvalidInputChecker = InvalidInputChecker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        animation = binding.progressBar.drawable as AnimationDrawable

        binding.layoutBackground.setOnClickListener(this)
        binding.txtLogo.setOnClickListener(this)
        binding.txtCreateAccount.setOnClickListener(this)
        binding.txtAlready.setOnClickListener(this)
        binding.btnSignUp.setOnClickListener {
            signUpUser()
        }

        authViewModel.signUpStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
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
                            this,
                            somethingWentWrong,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    Toast.makeText(
                        this,
                        response.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        dataViewModel.saveUserDataStatus.observe(this, { response ->
            when (response) {
                is Resource.Success -> {
                    if (response.data == true) {
                        hideLoadingBar()
                        startActivity(Intent(this, UserDashboardActivity::class.java))
                        finish()
                    } else {
                        hideLoadingBar()
                        Toast.makeText(
                            this,
                            somethingWentWrong,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    Toast.makeText(
                        this,
                        somethingWentWrong,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun signUpUser() {
        user.name = binding.edtFullNameEditText.text.toString()
        user.email = binding.edtEmailEditText.text.toString()
        user.registered = "false"

        invalidInputChecker.checkForSignUpValidInputs(
            user,
            binding.edtPasswordEditText.text.toString(),
            binding.edtConfirmPasswordEditText.text.toString()
        ) { error ->
            when (error) {
                is ValidationInput.EmptyName -> binding.edtFullName.error = "Required"
                is ValidationInput.EmptyEmail -> binding.edtEmail.error = "Required"
                is ValidationInput.EmptyPassword -> binding.edtPassword.error = "Required"
                is ValidationInput.EmptyConfirmPassword -> binding.edtConfirmPassword.error =
                    "Required"
                is ValidationInput.InvalidEmailPattern -> binding.edtEmail.error = "Invalid Email"
                is ValidationInput.ShortPasswordLength -> binding.edtPassword.error =
                    "Password should be greater than 8"
                is ValidationInput.ConfirmPasswordNotEqual -> binding.edtConfirmPassword.error =
                    "Confirm Password not equal to Password"
                is ValidationInput.ValidInput -> {
                    authViewModel.signUpUser(
                        binding.edtEmailEditText.text.toString(),
                        binding.edtPasswordEditText.text.toString()
                    )
                }
            }
        }
    }

    override fun onKey(p0: View?, i: Int, keyEvent: KeyEvent?): Boolean {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent?.action == KeyEvent.ACTION_DOWN) {
            signUpUser()
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