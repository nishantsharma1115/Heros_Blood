package com.nishant.herosblood.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

interface AuthRepo {
    fun sendPasswordResetLink(
        email: String,
        successCallback: (Task<Void>) -> Unit,
        failureCallback: (Exception) -> Unit
    )

    fun signUpUser(
        email: String,
        password: String,
        completeCallback: (Task<AuthResult>) -> Unit,
        failureCallback: (Exception) -> Unit
    )

    fun loginUser(
        email: String,
        password: String,
        successListener: (AuthResult) -> Unit,
        failureCallback: (Exception) -> Unit
    )
}