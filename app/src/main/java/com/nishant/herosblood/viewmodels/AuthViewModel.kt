package com.nishant.herosblood.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nishant.herosblood.repositories.AuthRepository
import com.nishant.herosblood.util.Resource

class AuthViewModel(
    private var authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

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
            loginStatus.postValue(Resource.Error(it.message.toString(), null))
        })
    }
}