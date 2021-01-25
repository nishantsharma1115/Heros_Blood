package com.nishant.herosblood.util

sealed class ValidationInput {

    object EmptyName : ValidationInput()
    object EmptyEmail : ValidationInput()
    object EmptyPhoneNumber : ValidationInput()
    object EmptyBloodGroup : ValidationInput()
    object EmptyAddress : ValidationInput()
    object EmptyState : ValidationInput()
    object EmptyCity : ValidationInput()
    object EmptyPincode : ValidationInput()
    object EmptyPassword : ValidationInput()
    object EmptyConfirmPassword : ValidationInput()

    object InvalidEmailPattern : ValidationInput()
    object ShortPasswordLength : ValidationInput()
    object ConfirmPasswordNotEqual : ValidationInput()

    object ValidInput : ValidationInput()
}