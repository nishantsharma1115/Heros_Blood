package com.nishant.herosblood.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nishant.herosblood.repositories.AuthRepository
import com.nishant.herosblood.util.Resource

class AuthViewModel(
    private var authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _signUpStatus: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val signUpStatus: LiveData<Resource<Boolean>> = _signUpStatus
    fun signUpUser(
        email: String,
        password: String
    ) {
        _signUpStatus.postValue(Resource.Loading())
        authRepository.signUpUser(email, password, { task ->
            if (task.isSuccessful) {
                _signUpStatus.postValue(Resource.Success(true))
            } else {
                _signUpStatus.postValue(Resource.Success(false))
            }
        }, {
            _signUpStatus.postValue(Resource.Error(it.message.toString()))
        })
    }

    private val _loginStatus: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val loginStatus: LiveData<Resource<Boolean>> = _loginStatus
    fun loginUser(
        email: String,
        password: String
    ) {
        _loginStatus.postValue(Resource.Loading())
        authRepository.loginUser(email, password, { task ->
            if (task.isSuccessful) {
                _loginStatus.postValue(Resource.Success(true))
            } else {
                _loginStatus.postValue(Resource.Success(false))
            }
        }, {
            _loginStatus.postValue(Resource.Error(it.message.toString()))
        })
    }
}