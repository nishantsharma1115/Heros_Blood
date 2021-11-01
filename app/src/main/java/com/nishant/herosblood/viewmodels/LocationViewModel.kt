package com.nishant.herosblood.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nishant.herosblood.models.UserLocationData
import com.nishant.herosblood.repositories.DataRepository
import com.nishant.herosblood.repositories.LocationRepository
import com.nishant.herosblood.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val locationRepository: LocationRepository
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

    private var _getAllDonorLocationStatus =
        MutableLiveData<Resource<ArrayList<UserLocationData>>>()
    val getAllDonorLocationStatus: LiveData<Resource<ArrayList<UserLocationData>>> =
        _getAllDonorLocationStatus

    fun getAllDonorLocation(userId: String) = viewModelScope.launch {
        val locationList: ArrayList<UserLocationData> = ArrayList()
        _getAllDonorLocationStatus.postValue(Resource.Loading())
        locationRepository.getAllDonorLocation(userId, { donorLocations ->
            for (donorLocation in donorLocations) {
                val location = donorLocation.toObject(UserLocationData::class.java)
                locationList.add(location)
            }
            _getAllDonorLocationStatus.postValue(Resource.Success(locationList))
        }, { exception ->
            _getAllDonorLocationStatus.postValue(Resource.Error(exception.message.toString()))
        })
    }
}
