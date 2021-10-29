package com.nishant.herosblood.repositories

import com.google.firebase.firestore.QuerySnapshot

interface LocationRepo {
    suspend fun getAllDonorLocation(
        userId: String,
        onCompleteCallback: (QuerySnapshot) -> Unit,
        onFailureCallback: (Exception) -> Unit
    )
}