package com.example.patrackplease.Register

interface RegisterContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showNameError(message: String)
        fun showEmailError(message: String)
        fun showPasswordError(message: String)
        fun showConfirmPasswordError(message: String)
        fun clearErrors()
        fun showRegisterSuccess(message: String)
        fun showRegisterFailed(message: String)
        fun navigateToLogin()
    }

    interface Presenter {
        fun onRegisterClicked(
            name: String,
            email: String,
            password: String,
            confirmPassword: String
        )
        fun onDestroy()
    }

    interface Model {
        fun register(
            name: String,
            email: String,
            password: String,
            confirmPassword: String,
            callback: OnRegisterFinishedListener
        )

        interface OnRegisterFinishedListener {
            fun onNameError(message: String)
            fun onEmailError(message: String)
            fun onPasswordError(message: String)
            fun onConfirmPasswordError(message: String)
            fun onSuccess(message: String)
            fun onFailure(message: String)
        }
    }
}