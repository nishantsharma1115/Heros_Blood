package com.nishant.herosblood.viewmodels

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nishant.herosblood.data.UserData
import com.nishant.herosblood.repositories.AuthRepository
import com.nishant.herosblood.util.InvalidInput
import com.nishant.herosblood.util.Resource

class AuthViewModel(
    private var authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    val signUpStatus: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    fun signUpUser(
        userData: UserData,
        password: String,
        confirmPassword: String,
        errorInput: (InvalidInput) -> Unit
    ) {
        if (userData.name!!.isEmpty()) {
            errorInput(InvalidInput.EmptyName)
            return
        }
        if (userData.email!!.isEmpty()) {
            errorInput(InvalidInput.EmptyEmail)
            return
        }
        if (password.isEmpty()) {
            errorInput(InvalidInput.EmptyPassword)
            return
        }
        if (confirmPassword.isEmpty()) {
            errorInput(InvalidInput.EmptyConfirmPassword)
            return
        }
        if (password != confirmPassword) {
            errorInput(InvalidInput.ConfirmPasswordNotEqual)
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userData.email!!).matches()) {
            errorInput(InvalidInput.InvalidEmailPattern)
            return
        }
        if (password.length < 8) {
            errorInput(InvalidInput.ShortPasswordLength)
            return
        }
        signUpStatus.postValue(Resource.Loading())
        authRepository.signUpUser(userData.email!!, password, { task ->
            if (task.isSuccessful) {
                signUpStatus.postValue(Resource.Success(true))
            } else {
                signUpStatus.postValue(Resource.Success(false))
            }
        }, {
            signUpStatus.postValue(Resource.Error(it.message.toString()))
        })
    }

    val loginStatus: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    fun loginUser(
        email: String,
        password: String,
        errorInput: (InvalidInput) -> Unit
    ) {
        if (email.isEmpty()) {
            errorInput(InvalidInput.EmptyEmail)
            return
        }
        if (password.isEmpty()) {
            errorInput(InvalidInput.EmptyPassword)
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorInput(InvalidInput.InvalidEmailPattern)
            return
        }
        if (password.length < 8) {
            errorInput(InvalidInput.ShortPasswordLength)
            return
        }
        loginStatus.postValue(Resource.Loading())
        authRepository.loginUser(email, password, { task ->
            if (task.isSuccessful) {
                loginStatus.postValue(Resource.Success(true))
            } else {
                loginStatus.postValue(Resource.Success(false))
            }
        }, {
            loginStatus.postValue(Resource.Error(it.message.toString()))
        })
    }
}