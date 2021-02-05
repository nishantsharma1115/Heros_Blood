package com.nishant.herosblood.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nishant.herosblood.data.UserData
import com.nishant.herosblood.repositories.DataRepository
import com.nishant.herosblood.util.DifferentDonorLists
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

    val getAllDonorsStatus: MutableLiveData<Resource<DifferentDonorLists>> = MutableLiveData()
    fun getAllDonors(currentUserId: String) = viewModelScope.launch {
        getAllDonorsStatus.postValue(Resource.Loading())
        val donorsList = DifferentDonorLists()

        dataRepository.getAllDonors(currentUserId, {
            for (document in it) {
                val user = document.toObject(UserData::class.java)
                if (user.bloodGroup.equals("O+")) {
                    donorsList.oPositiveDonors.add(user)
                }
                if (user.bloodGroup.equals("A+")) {
                    donorsList.aPositiveDonors.add(user)
                }
                if (user.bloodGroup.equals("O-")) {
                    donorsList.oNegativeDonors.add(user)
                }
                if (user.bloodGroup.equals("AB+")) {
                    donorsList.abPositiveDonors.add(user)
                }
                if (user.bloodGroup.equals("AB-")) {
                    donorsList.abNegativeDonors.add(user)
                }
            }
            getAllDonorsStatus.postValue(Resource.Success(donorsList))
        }, {
            getAllDonorsStatus.postValue(Resource.Error(it.message.toString()))
        })
    }

    val getDonorListStatus: MutableLiveData<Resource<ArrayList<UserData>>> = MutableLiveData()
    fun getDonorList(
        userId: String,
        bloodType: String
    ) = viewModelScope.launch {
        val donorList: ArrayList<UserData> = ArrayList()
        getDonorListStatus.postValue(Resource.Loading())
        dataRepository.getDonorList(userId, bloodType, { snapshot ->
            if (!snapshot.isEmpty) {
                for (donors in snapshot) {
                    val donor: UserData = donors.toObject(UserData::class.java)
                    donorList.add(donor)
                }
                getDonorListStatus.postValue(Resource.Success(donorList))
            } else {
                getDonorListStatus.postValue(Resource.Success(donorList))
            }
        }, {
            Log.d("Error: ", it.message.toString())
            getDonorListStatus.postValue(Resource.Error("Some Error Occurred"))
        })
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