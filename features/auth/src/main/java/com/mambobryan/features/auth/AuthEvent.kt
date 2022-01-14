package com.mambobryan.features.auth

sealed class AuthEvent {

    object NavigateToSignIn : AuthEvent()
    object NavigateToSignUp : AuthEvent()
    object NavigateToSetup : AuthEvent()
    object NavigateToHome : AuthEvent()

}