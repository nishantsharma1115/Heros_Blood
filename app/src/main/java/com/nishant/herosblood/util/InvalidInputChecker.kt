package com.nishant.herosblood.util

import android.util.Patterns
import com.nishant.herosblood.data.UserData

class InvalidInputChecker {

    fun checkForRegistrationValidInputs(
        user: UserData,
        errorInput: (ValidationInput) -> Unit
    ) {
        if (user.bloodGroup!!.isEmpty()) {
            errorInput(ValidationInput.EmptyBloodGroup)
            return
        }
        if (user.address!!.isEmpty()) {
            errorInput(ValidationInput.EmptyAddress)
            return
        }
        if (user.state!!.isEmpty()) {
            errorInput(ValidationInput.EmptyState)
            return
        }
        if (user.city!!.isEmpty()) {
            errorInput(ValidationInput.EmptyCity)
            return
        }
        if (user.pincode!!.isEmpty()) {
            errorInput(ValidationInput.EmptyPincode)
            return
        }
        if (user.phoneNumber!!.isEmpty()) {
            errorInput(ValidationInput.EmptyPhoneNumber)
            return
        }
    }

    fun checkForLoginValidInputs(
        email: String,
        password: String,
        errorInput: (ValidationInput) -> Unit
    ) {
        if (email.isEmpty()) {
            errorInput(ValidationInput.EmptyEmail)
            return
        }
        if (password.isEmpty()) {
            errorInput(ValidationInput.EmptyPassword)
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorInput(ValidationInput.InvalidEmailPattern)
            return
        }
        if (password.length < 8) {
            errorInput(ValidationInput.ShortPasswordLength)
            return
        }
        errorInput(ValidationInput.ValidInput)
    }

    fun checkForSignUpValidInputs(
        userData: UserData,
        password: String,
        confirmPassword: String,
        errorInput: (ValidationInput) -> Unit
    ) {
        if (userData.name!!.isEmpty()) {
            errorInput(ValidationInput.EmptyName)
            return
        }
        if (userData.email!!.isEmpty()) {
            errorInput(ValidationInput.EmptyEmail)
            return
        }
        if (password.isEmpty()) {
            errorInput(ValidationInput.EmptyPassword)
            return
        }
        if (confirmPassword.isEmpty()) {
            errorInput(ValidationInput.EmptyConfirmPassword)
            return
        }
        if (password != confirmPassword) {
            errorInput(ValidationInput.ConfirmPasswordNotEqual)
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userData.email!!).matches()) {
            errorInput(ValidationInput.InvalidEmailPattern)
            return
        }
        if (password.length < 8) {
            errorInput(ValidationInput.ShortPasswordLength)
            return
        }
        errorInput(ValidationInput.ValidInput)
    }
}