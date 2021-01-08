package com.nishant.herosblood.util

sealed class InvalidInput {
    object EmptyName : InvalidInput()
    object EmptyEmail : InvalidInput()
    object EmptyPhoneNumber : InvalidInput()
    object EmptyBloodGroup : InvalidInput()
    object EmptyAddress : InvalidInput()
    object EmptyState : InvalidInput()
    object EmptyCity : InvalidInput()
    object EmptyPincode : InvalidInput()
    object EmptyPassword : InvalidInput()
    object EmptyConfirmPassword : InvalidInput()
    object InvalidEmailPattern : InvalidInput()
    object ShortPasswordLength : InvalidInput()
    object ConfirmPasswordNotEqual : InvalidInput()
}