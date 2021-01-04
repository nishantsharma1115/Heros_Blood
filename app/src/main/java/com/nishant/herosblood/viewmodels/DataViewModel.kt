package com.nishant.herosblood.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nishant.herosblood.data.UserData
import com.nishant.herosblood.repositories.DataRepository
import com.nishant.herosblood.util.Resource

class DataViewModel(
    val dataRepository: DataRepository = DataRepository()
) : ViewModel() {

    val saveUserDataStatus: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    fun saveUserData(
        user: UserData
    ) {
        saveUserDataStatus.postValue(Resource.Loading())
        dataRepository.saveUserData(user, { task ->
            if (task.isSuccessful) {
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