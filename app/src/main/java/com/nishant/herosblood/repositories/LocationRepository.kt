package com.nishant.herosblood.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val db: FirebaseFirestore
) : LocationRepo {

    override suspend fun getAllDonorLocation(
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
