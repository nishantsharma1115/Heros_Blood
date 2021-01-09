package com.nishant.herosblood.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nishant.herosblood.repositories.AuthRepository
import com.nishant.herosblood.util.Resource

class AuthViewModel(
    private var authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    val signUpStatus: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    fun signUpUser(
        email: String,
        password: String
    ) {
        signUpStatus.postValue(Resource.Loading())
        authRepository.signUpUser(email, password, { task ->
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
        password: String
    ) {
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