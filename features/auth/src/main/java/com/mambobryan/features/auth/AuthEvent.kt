package com.mambobryan.features.auth

sealed class AuthEvent {

    data class ShowErrorMessage(val message : String) : AuthEvent()
    data class ShowSuccessMessage(val message : String) : AuthEvent()

    object ShowLoadingDialog : AuthEvent()
    object HideLoadingDialog : AuthEvent()

    object NavigateToSignIn : AuthEvent()
    object NavigateToSignUp : AuthEvent()
    object NavigateToSetup : AuthEvent()
    object NavigateToHome : AuthEvent()

}