package com.nishant.herosblood.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nishant.herosblood.data.UserData
import com.nishant.herosblood.repositories.DataRepository
import com.nishant.herosblood.util.Resource

class DataViewModel(
    private val dataRepository: DataRepository = DataRepository()
) : ViewModel() {

    val saveUserDataStatus: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    fun saveUserData(user: UserData) {
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

    val getAllDonorsStatus: MutableLiveData<Resource<HashMap<String, ArrayList<UserData>>>> =
        MutableLiveData()

    fun getAllDonors() {
        getAllDonorsStatus.postValue(Resource.Loading())
        val OPositiveDonors: ArrayList<UserData> = ArrayList()
        val APositiveDonors: ArrayList<UserData> = ArrayList()
        val ONegativeDonors: ArrayList<UserData> = ArrayList()
        val ABPositiveDonors: ArrayList<UserData> = ArrayList()
        val ABNegativeDonors: ArrayList<UserData> = ArrayList()
        val users = hashMapOf(
            "O+" to OPositiveDonors,
            "A+" to APositiveDonors,
            "O-" to ONegativeDonors,
            "AB+" to ABPositiveDonors,
            "AB-" to ABNegativeDonors
        )
        dataRepository.getAllDonors({
            for (document in it) {
                val user = document.toObject(UserData::class.java)
                if (user.bloodGroup.equals("O+")) {
                    OPositiveDonors.add(user)
                }
                if (user.bloodGroup.equals("A+")) {
                    APositiveDonors.add(user)
                }
                if (user.bloodGroup.equals("O-")) {
                    ONegativeDonors.add(user)
                }
                if (user.bloodGroup.equals("AB+")) {
                    ABPositiveDonors.add(user)
                }
                if (user.bloodGroup.equals("AB-")) {
                    ABNegativeDonors.add(user)
                }
            }
            getAllDonorsStatus.postValue(Resource.Success(users))
        }, {
            getAllDonorsStatus.postValue(Resource.Error(it.message.toString()))
        })
    }
}