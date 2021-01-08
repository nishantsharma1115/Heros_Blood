package com.nishant.herosblood.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nishant.herosblood.data.UserData
import com.nishant.herosblood.repositories.DataRepository
import com.nishant.herosblood.util.InvalidInput
import com.nishant.herosblood.util.Resource

class DataViewModel(
    private val dataRepository: DataRepository = DataRepository()
) : ViewModel() {

    val saveUserDataStatus: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    fun saveUserData(
        user: UserData,
        from: String,
        errorInput: (InvalidInput) -> Unit
    ) {
        if (user.bloodGroup!!.isEmpty() && from == "Registration") {
            errorInput(InvalidInput.EmptyBloodGroup)
            return
        }
        if (user.address!!.isEmpty() && from == "Registration") {
            errorInput(InvalidInput.EmptyAddress)
            return
        }
        if (user.state!!.isEmpty() && from == "Registration") {
            errorInput(InvalidInput.EmptyState)
            return
        }
        if (user.city!!.isEmpty() && from == "Registration") {
            errorInput(InvalidInput.EmptyCity)
            return
        }
        if (user.pincode!!.isEmpty() && from == "Registration") {
            errorInput(InvalidInput.EmptyPincode)
            return
        }
        if (user.phoneNumber!!.isEmpty() && from == "Registration") {
            errorInput(InvalidInput.EmptyPhoneNumber)
            return
        }
        saveUserDataStatus.postValue(Resource.Loading())
        dataRepository.saveUserData(user, { task ->
            if (task.isSuccessful) {
                Log.d("Inside ViewModel", "Task is successful")
                saveUserDataStatus.postValue(Resource.Success(true))
            } else {
                saveUserDataStatus.postValue(Resource.Success(false))
            }
        }, {
            saveUserDataStatus.postValue(Resource.Error(it.message.toString()))
        })
    }

    val readUserDataStatus: MutableLiveData<Resource<UserData>> = MutableLiveData()
    fun readUserData(
        userId: String
    ) {
        readUserDataStatus.postValue(Resource.Loading())
        dataRepository.readUserData(userId, { document ->
            if (document.exists()) {
                val user = document.toObject(UserData::class.java)
                user?.let {
                    readUserDataStatus.postValue(Resource.Success(user))
                }
            } else {
                readUserDataStatus.postValue(Resource.Success(UserData()))
            }
        }, {
            readUserDataStatus.postValue(Resource.Error(it.message.toString()))
        })
    }
}