package com.nishant.herosblood.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nishant.herosblood.models.BloodRequestData
import com.nishant.herosblood.models.UserData
import com.nishant.herosblood.repositories.DataRepository
import com.nishant.herosblood.util.DifferentDonorLists
import com.nishant.herosblood.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _updateUserAvailabilityStatus: MutableLiveData<Resource<Boolean>> =
        MutableLiveData()
    val updateUserAvailabilityStatus: LiveData<Resource<Boolean>> = _updateUserAvailabilityStatus
    fun updateUserAvailability(
        userId: String,
        newValue: String
    ) {
        _updateUserAvailabilityStatus.postValue(Resource.Loading())
        dataRepository.updateUserAvailability(
            userId, newValue,
            { task ->
                if (task.isSuccessful) {
                    _updateUserAvailabilityStatus.postValue(Resource.Success(true))
                } else {
                    _updateUserAvailabilityStatus.postValue(Resource.Success(false))
                }
            },
            {
                _updateUserAvailabilityStatus.postValue(Resource.Error(it.message.toString()))
            }
        )
    }

    private val _saveUserDataStatus: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val saveUserDataStatus: LiveData<Resource<Boolean>> = _saveUserDataStatus
    fun saveUserData(user: UserData) {
        _saveUserDataStatus.postValue(Resource.Loading())
        dataRepository.saveUserData(
            user,
            { task ->
                if (task.isSuccessful) {
                    _saveUserDataStatus.postValue(Resource.Success(true))
                } else {
                    _saveUserDataStatus.postValue(Resource.Success(false))
                }
            },
            {
                _saveUserDataStatus.postValue(Resource.Error(it.message.toString()))
            }
        )
    }

    private var _getSaveBloodRequestStatus = MutableLiveData<Resource<Boolean>>()
    val getSaveBloodRequestStatus: LiveData<Resource<Boolean>> = _getSaveBloodRequestStatus
    fun saveBloodRequest(
        bloodRequestData: BloodRequestData
    ) {
        _getSaveBloodRequestStatus.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.saveBloodRequest(bloodRequestData, completeCallback = { task ->
                if (task.isSuccessful) {
                    _getSaveBloodRequestStatus.postValue(Resource.Success(true))
                } else {
                    _getSaveBloodRequestStatus.postValue(Resource.Success(true))
                }
            }, failureCallback = {
                _getSaveBloodRequestStatus.postValue(Resource.Error(it.message.toString()))
            })
        }
    }

    private var _getFetchPreviousBloodRequestStatus =
        MutableLiveData<Resource<ArrayList<BloodRequestData>>>()
    val getFetchPreviousBloodRequestStatus: LiveData<Resource<ArrayList<BloodRequestData>>> =
        _getFetchPreviousBloodRequestStatus

    fun fetchPreviousBloodRequest(userId: String) {
        _getFetchPreviousBloodRequestStatus.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.fetchPreviousBloodRequest(userId, successCallback = {
                val bloodRequests = ArrayList<BloodRequestData>()
                for (document in it) {
                    val bloodRequest = document.toObject(BloodRequestData::class.java)
                    bloodRequests.add(bloodRequest)
                }
                _getFetchPreviousBloodRequestStatus.postValue(Resource.Success(bloodRequests))
            }, failureCallback = {
                _getFetchPreviousBloodRequestStatus.postValue(Resource.Error(it.message.toString()))
            })
        }
    }

    private var _getFetchNearbyRequestsStatus =
        MutableLiveData<Resource<ArrayList<BloodRequestData>>>()
    val getFetchNearbyRequestsStatus: LiveData<Resource<ArrayList<BloodRequestData>>> =
        _getFetchNearbyRequestsStatus

    fun fetchNearbyRequest(userId: String) {
        _getFetchNearbyRequestsStatus.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.fetchNearbyRequests(userId, successCallback = {
                val nearbyRequests = ArrayList<BloodRequestData>()
                for (document in it) {
                    val bloodRequest = document.toObject(BloodRequestData::class.java)
                    nearbyRequests.add(bloodRequest)
                }
                _getFetchNearbyRequestsStatus.postValue(Resource.Success(nearbyRequests))
            }, failureCallback = {
                _getFetchNearbyRequestsStatus.postValue(Resource.Error(it.message.toString()))
            })
        }
    }

    private var _getFetchNearbyRequestsForDashboardStatus =
        MutableLiveData<Resource<ArrayList<BloodRequestData>>>()
    val getFetchNearbyRequestsForDashboardStatus: LiveData<Resource<ArrayList<BloodRequestData>>> =
        _getFetchNearbyRequestsForDashboardStatus

    fun fetchNearbyRequestForDashboard(userId: String) {
        _getFetchNearbyRequestsForDashboardStatus.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.fetchNearbyRequestsForDashboard(userId, successCallback = {
                val nearbyRequests = ArrayList<BloodRequestData>()
                for (document in it) {
                    val bloodRequest = document.toObject(BloodRequestData::class.java)
                    nearbyRequests.add(bloodRequest)
                }
                _getFetchNearbyRequestsForDashboardStatus.postValue(Resource.Success(nearbyRequests))
            }, failureCallback = {
                _getFetchNearbyRequestsForDashboardStatus.postValue(Resource.Error(it.message.toString()))
            })
        }
    }

    private val _readUserDataStatus: MutableLiveData<Resource<UserData>> = MutableLiveData()
    val readUserDataStatus: LiveData<Resource<UserData>> = _readUserDataStatus
    fun readUserData(
        userId: String
    ) {
        _readUserDataStatus.postValue(Resource.Loading())
        dataRepository.readUserData(
            userId,
            { document ->
                if (document.exists()) {
                    val user = document.toObject(UserData::class.java)
                    user?.let {
                        _readUserDataStatus.postValue(Resource.Success(user))
                    }
                } else {
                    _readUserDataStatus.postValue(Resource.Success(UserData()))
                }
            },
            {
                _readUserDataStatus.postValue(Resource.Error(it.message.toString()))
            }
        )
    }

    private val _getAllDonorsStatus: MutableLiveData<Resource<DifferentDonorLists>> =
        MutableLiveData()
    val getAllDonorsStatus: LiveData<Resource<DifferentDonorLists>> = _getAllDonorsStatus
    fun getAllDonors(currentUserId: String) = viewModelScope.launch {
        _getAllDonorsStatus.postValue(Resource.Loading())
        val donorsList = DifferentDonorLists()

        dataRepository.getAllDonors(
            currentUserId,
            {
                for (document in it) {
                    val user = document.toObject(UserData::class.java)
                    if (user.bloodGroup.equals("A+")) {
                        donorsList.aPositiveDonors.add(user)
                    }
                    if (user.bloodGroup.equals("A-")) {
                        donorsList.aNegativeDonors.add(user)
                    }
                    if (user.bloodGroup.equals("B+")) {
                        donorsList.bPositiveDonors.add(user)
                    }
                    if (user.bloodGroup.equals("B-")) {
                        donorsList.bNegativeDonors.add(user)
                    }
                    if (user.bloodGroup.equals("O+")) {
                        donorsList.oPositiveDonors.add(user)
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
                _getAllDonorsStatus.postValue(Resource.Success(donorsList))
            },
            {
                _getAllDonorsStatus.postValue(Resource.Error(it.message.toString()))
            }
        )
    }

    private val _getDonorListStatus: MutableLiveData<Resource<ArrayList<UserData>>> =
        MutableLiveData()
    val getDonorListStatus: LiveData<Resource<ArrayList<UserData>>> = _getDonorListStatus
    fun getDonorList(
        userId: String,
        bloodType: String
    ) = viewModelScope.launch {
        val donorList: ArrayList<UserData> = ArrayList()
        _getDonorListStatus.postValue(Resource.Loading())
        dataRepository.getDonorList(
            userId, bloodType,
            { snapshot ->
                if (!snapshot.isEmpty) {
                    for (donors in snapshot) {
                        val donor: UserData = donors.toObject(UserData::class.java)
                        donorList.add(donor)
                    }
                    _getDonorListStatus.postValue(Resource.Success(donorList))
                } else {
                    _getDonorListStatus.postValue(Resource.Success(donorList))
                }
            },
            {
                _getDonorListStatus.postValue(Resource.Error("Some Error Occurred"))
            }
        )
    }

    private var _getSearchDonorStatus = MutableLiveData<Resource<ArrayList<UserData>>>()
    val getSearchDonorStatus: LiveData<Resource<ArrayList<UserData>>> = _getSearchDonorStatus
    fun getSearchDonorList(
        userId: String,
        bloodType: String,
        city: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        _getSearchDonorStatus.postValue(Resource.Loading())
        dataRepository.getSearchDonorList(userId, bloodType, city, { querySnapshot ->
            val donorList: ArrayList<UserData> = ArrayList()
            if (querySnapshot.isEmpty) {
                _getSearchDonorStatus.postValue(Resource.Success(donorList))
            } else {
                for (donors in querySnapshot) {
                    val donor: UserData = donors.toObject(UserData::class.java)
                    donorList.add(donor)
                }
                _getSearchDonorStatus.postValue(Resource.Success(donorList))
            }
        }, {
            _getSearchDonorStatus.postValue(Resource.Error("Something went wrong"))
        })
    }

    private val _getProfilePictureStatus: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val getProfilePictureStatus: LiveData<Resource<Boolean>> = _getProfilePictureStatus
    fun uploadProfilePicture(
        userId: String,
        file: Uri
    ) = viewModelScope.launch {
        _getProfilePictureStatus.postValue(Resource.Loading())
        dataRepository.uploadProfilePicture(
            userId, file,
            { task ->
                if (task.isSuccessful) {
                    _getProfilePictureStatus.postValue(Resource.Success(true))
                } else {
                    _getProfilePictureStatus.postValue(Resource.Success(false))
                }
            },
            { exception ->
                _getProfilePictureStatus.postValue(Resource.Error(exception.message.toString()))
            }
        )
    }
}
