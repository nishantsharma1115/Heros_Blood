package com.nishant.herosblood.repositories

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.nishant.herosblood.data.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference

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

    suspend fun getAllDonors(
        completeCallback: (QuerySnapshot) -> Unit,
        failureCallback: (Exception) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            db.collection("users")
                .get()
                .addOnSuccessListener { snapshot ->
                    completeCallback(snapshot)
                }
                .addOnFailureListener { exception ->
                    failureCallback(exception)
                }
        }
    }

    fun uploadProfilePicture(
        userId: String,
        file: Uri,
        onCompleteCallback: (Task<UploadTask.TaskSnapshot>) -> Unit,
        onFailureCallback: (Exception) -> Unit
    ) {
        storageRef.child("ProfilePicture").child(userId).putFile(file)
            .addOnCompleteListener(onCompleteCallback)
            .addOnFailureListener(onFailureCallback)
    }
}