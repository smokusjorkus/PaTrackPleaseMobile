package com.example.patrackplease.Register

import com.example.patrackplease.Login.LoginResponse

interface RegisterContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showFirstNameError(message: String)
        fun showLastNameError(message: String)
        fun showEmailError(message: String)
        fun showPasswordError(message: String)
        fun showConfirmPasswordError(message: String)
        fun clearErrors()
        fun showRegisterSuccess(message: String)
        fun showRegisterFailed(message: String)
        fun navigateToLogin()
    }

    interface Presenter {
        fun onRegisterClicked(firstName: String, lastName: String, email: String, password: String, confirmPass: String)
        fun onDestroy()
    }

    interface Model {
        interface OnRegisterFinishedListener {
            fun onFirstNameError(message: String)
            fun onLastNameError(message: String)
            fun onEmailError(message: String)
            fun onPasswordError(message: String)
            fun onConfirmPasswordError(message: String)
            fun onSuccess(response: LoginResponse)
            fun onFailure(message: String)
        }
        fun register(firstName: String, lastName: String, email: String, password: String, confirmPass: String, callback: OnRegisterFinishedListener)
    }
}