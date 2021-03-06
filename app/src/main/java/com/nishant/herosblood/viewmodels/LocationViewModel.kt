package com.nishant.herosblood.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nishant.herosblood.models.UserLocationData
import com.nishant.herosblood.repositories.DataRepository
import com.nishant.herosblood.util.Resource
import kotlinx.coroutines.launch

class LocationViewModel(
    private val dataRepository: DataRepository = DataRepository()
) : ViewModel() {

    fun saveUserLocation(
        locationData: UserLocationData
    ) = viewModelScope.launch {
        dataRepository.saveUserLocation(locationData)
    }

    private val _getUserLocationStatus: MutableLiveData<Resource<UserLocationData?>> =
        MutableLiveData()
    val getUserLocationStatus: LiveData<Resource<UserLocationData?>> = _getUserLocationStatus
    fun getUserLocation(
        userId: String
    ) = viewModelScope.launch {
        _getUserLocationStatus.postValue(Resource.Loading())
        dataRepository.getUserLocation(userId, { snapshot ->
            try {
                val userLocation = snapshot.documents[0].toObject(UserLocationData::class.java)
                _getUserLocationStatus.postValue(Resource.Success(userLocation))
            } catch (exception: IndexOutOfBoundsException) {
                _getUserLocationStatus.postValue(Resource.Success(null))
            }
        }, { exception ->
            _getUserLocationStatus.postValue(Resource.Error(exception.message.toString()))
        })
    }
}