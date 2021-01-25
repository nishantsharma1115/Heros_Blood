package com.nishant.herosblood.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class AuthRepository {
    private val mAuth = FirebaseAuth.getInstance()

    fun signUpUser(
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

    fun loginUser(
        email: String,
        password: String,
        completeCallback: (Task<AuthResult>) -> Unit,
        failureCallback: (Exception) -> Unit
    ) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                completeCallback(task)
            }
            .addOnFailureListener { exception ->
                failureCallback(exception)
            }
    }
}