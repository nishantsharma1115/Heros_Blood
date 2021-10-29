package com.nishant.herosblood.repositories

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask
import com.nishant.herosblood.models.BloodRequestData
import com.nishant.herosblood.models.UserData
import com.nishant.herosblood.models.UserLocationData

interface DataRepo {
    fun saveUserData(
        user: UserData,
        completeCallback: (Task<Void>) -> Unit,
        failureCallback: (Exception) -> Unit
    )

    fun saveBloodRequest(
        bloodRequestData: BloodRequestData,
        completeCallback: (Task<Void>) -> Unit,
        failureCallback: (Exception) -> Unit
    )

    fun fetchPreviousBloodRequest(
        userId: String,
        successCallback: (QuerySnapshot) -> Unit,
        failureCallback: (Exception) -> Unit
    )

    fun fetchNearbyRequests(
        userId: String,
        successCallback: (QuerySnapshot) -> Unit,
        failureCallback: (Exception) -> Unit
    )

    fun fetchNearbyRequestsForDashboard(
        userId: String,
        successCallback: (QuerySnapshot) -> Unit,
        failureCallback: (Exception) -> Unit
    )

    fun updateUserAvailability(
        userId: String,
        newValue: String,
        completeCallback: (Task<Void>) -> Unit,
        failureCallback: (Exception) -> Unit
    )

    fun readUserData(
        userId: String,
        completeCallback: (DocumentSnapshot) -> Unit,
        failureCallback: (Exception) -> Unit
    )

    suspend fun getAllDonors(
        userId: String,
        completeCallback: (QuerySnapshot) -> Unit,
        failureCallback: (Exception) -> Unit
    )

    suspend fun getDonorList(
        userId: String,
        bloodType: String,
        completeCallback: (QuerySnapshot) -> Unit,
        failureCallback: (Exception) -> Unit
    )

    suspend fun getSearchDonorList(
        userId: String,
        bloodType: String,
        city: String,
        completeCallback: (QuerySnapshot) -> Unit,
        failureCallback: (Exception) -> Unit
    )

    fun uploadProfilePicture(
        userId: String,
        file: Uri,
        onCompleteCallback: (Task<UploadTask.TaskSnapshot>) -> Unit,
        onFailureCallback: (Exception) -> Unit
    )

    suspend fun saveUserLocation(
        locationData: UserLocationData
    )

    suspend fun getUserLocation(
        userId: String,
        onCompleteCallback: (QuerySnapshot) -> Unit,
        onFailureCallback: (Exception) -> Unit
    )
}