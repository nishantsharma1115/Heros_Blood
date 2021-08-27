package com.nishant.herosblood.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nishant.herosblood.repositories.AuthRepository
import com.nishant.herosblood.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(
    private var authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _sendPasswordResetEmailStatus = MutableLiveData<Resource<Boolean>>()
    var sendPasswordResetEmailStatus: LiveData<Resource<Boolean>> = _sendPasswordResetEmailStatus
    fun sendPasswordResetLink(
        email: String
    ) {
        _sendPasswordResetEmailStatus.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.sendPasswordResetLink(email, { task ->
                if (task.isSuccessful) {
                    _sendPasswordResetEmailStatus.postValue(Resource.Success(true))
                }
            }, { exception ->
                _sendPasswordResetEmailStatus.postValue(Resource.Error(exception.message.toString()))
            })
        }
    }

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
        authRepository.loginUser(email, password, {
            _loginStatus.postValue(Resource.Success(true))
        }, {
            _loginStatus.postValue(Resource.Error(it.message.toString()))
        })
    }

    fun clearLoginStatus() {
        _loginStatus.postValue(null)
    }
}