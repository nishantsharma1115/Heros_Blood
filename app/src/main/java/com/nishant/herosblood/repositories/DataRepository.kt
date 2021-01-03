package com.nishant.herosblood.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nishant.herosblood.data.UserData

class DataRepository {
    private val db = Firebase.firestore

    fun saveUserData(
        user: UserData,
        completeListener: (Task<Void>) -> Unit,
        failureListener: (Exception) -> Unit
    ) {
        db.collection("users").document()
            .set(user)
            .addOnCompleteListener {
                completeListener(it)
            }
            .addOnFailureListener {
                failureListener(it)
            }
    }
}