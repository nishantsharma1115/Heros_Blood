package com.nishant.herosblood.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
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
        user.userId?.let {
            db.collection("users").document(it)
                .set(user)
                .addOnCompleteListener { task ->
                    completeListener(task)
                }
                .addOnFailureListener { exception ->
                    failureListener(exception)
                }
        }
    }

    fun readUserData(
        userId: String,
        completeCallback: (DocumentSnapshot) -> Unit,
        failureCallback: (Exception) -> Unit
    ) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                completeCallback(document)
            }
            .addOnFailureListener { exception ->
                failureCallback(exception)
            }
    }

    fun getAllDonors(
        completeCallback: (QuerySnapshot) -> Unit,
        failureCallback: (Exception) -> Unit
    ) {
        db.collection("users")
            .get()
            .addOnSuccessListener {
                completeCallback(it)
            }
            .addOnFailureListener {
                failureCallback(it)
            }
    }
}