package com.nishant.herosblood.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference

    suspend fun getAllDonorLocation(
        userId: String,
        onCompleteCallback: (QuerySnapshot) -> Unit,
        onFailureCallback: (Exception) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            db.collection("userLocationData")
                .whereNotEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(onCompleteCallback)
                .addOnFailureListener(onFailureCallback)
        }
    }
}