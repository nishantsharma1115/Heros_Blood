package com.nishant.herosblood.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nishant.herosblood.data.UserLocationData
import com.nishant.herosblood.repositories.DataRepository
import kotlinx.coroutines.launch

class LocationViewModel(
    private val dataRepository: DataRepository = DataRepository()
) : ViewModel() {

    fun saveUserLocation(
        locationData: UserLocationData
    ) = viewModelScope.launch {
        dataRepository.saveUserLocation(locationData)
    }
}