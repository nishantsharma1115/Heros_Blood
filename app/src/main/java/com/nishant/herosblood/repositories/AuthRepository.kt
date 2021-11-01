package com.nishant.herosblood.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthRepository @Inject constructor(private val mAuth: FirebaseAuth) : AuthRepo {

    override fun sendPasswordResetLink(
        email: String,
        successCallback: (Task<Void>) -> Unit,
        failureCallback: (Exception) -> Unit
    ) {
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(successCallback)
            .addOnFailureListener(failureCallback)
    }

    override fun signUpUser(
        email: String,
        password: String,
        completeCallback: (Task<AuthResult>) -> Unit,
        failureCallback: (Exception) -> Unit
    ) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                completeCallback(task)
            }
            .addOnFailureListener { exception ->
                failureCallback(exception)
            }
    }

    override fun loginUser(
        email: String,
        password: String,
        successListener: (AuthResult) -> Unit,
        failureCallback: (Exception) -> Unit
    ) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(successListener)
            .addOnFailureListener(failureCallback)
    }
}
