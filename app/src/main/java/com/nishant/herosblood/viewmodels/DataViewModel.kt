package com.nishant.herosblood.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nishant.herosblood.data.UserData
import com.nishant.herosblood.repositories.DataRepository
import com.nishant.herosblood.util.Resource
import kotlinx.coroutines.launch

class DataViewModel(
    private val dataRepository: DataRepository = DataRepository()
) : ViewModel() {

    val saveUserDataStatus: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    fun saveUserData(user: UserData) {
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

    val getAllDonorsStatus: MutableLiveData<Resource<HashMap<String, ArrayList<UserData>>>> =
        MutableLiveData()

    suspend fun getAllDonors(currentUserId: String) {
        getAllDonorsStatus.postValue(Resource.Loading())
        val oPositiveDonors: ArrayList<UserData> = ArrayList()
        val aPositiveDonors: ArrayList<UserData> = ArrayList()
        val oNegativeDonors: ArrayList<UserData> = ArrayList()
        val abPositiveDonors: ArrayList<UserData> = ArrayList()
        val abNegativeDonors: ArrayList<UserData> = ArrayList()
        val users = hashMapOf(
            "O+" to oPositiveDonors,
            "A+" to aPositiveDonors,
            "O-" to oNegativeDonors,
            "AB+" to abPositiveDonors,
            "AB-" to abNegativeDonors
        )

        viewModelScope.launch {

            dataRepository.getAllDonors({
                for (document in it) {
                    val user = document.toObject(UserData::class.java)
                    if (user.userId != currentUserId) {
                        if (user.bloodGroup.equals("O+")) {
                            oPositiveDonors.add(user)
                        }
                        if (user.bloodGroup.equals("A+")) {
                            aPositiveDonors.add(user)
                        }
                        if (user.bloodGroup.equals("O-")) {
                            oNegativeDonors.add(user)
                        }
                        if (user.bloodGroup.equals("AB+")) {
                            abPositiveDonors.add(user)
                        }
                        if (user.bloodGroup.equals("AB-")) {
                            abNegativeDonors.add(user)
                        }
                    }
                }
                getAllDonorsStatus.postValue(Resource.Success(users))
            }, {
                getAllDonorsStatus.postValue(Resource.Error(it.message.toString()))
            })
        }
    }

    val getProfilePictureStatus: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    fun uploadProfilePicture(
        userId: String,
        file: Uri
    ) = viewModelScope.launch {
        getProfilePictureStatus.postValue(Resource.Loading())
        dataRepository.uploadProfilePicture(userId, file, { task ->
            if (task.isSuccessful) {
                getProfilePictureStatus.postValue(Resource.Success(true))
            } else {
                getProfilePictureStatus.postValue(Resource.Success(false))
            }
        }, { exception ->
            getProfilePictureStatus.postValue(Resource.Error(exception.message.toString()))
        })
    }
}