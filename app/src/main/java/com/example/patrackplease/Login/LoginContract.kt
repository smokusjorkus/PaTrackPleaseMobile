package com.example.patrackplease.Login

interface LoginContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showEmailError(message: String)
        fun showPasswordError(message: String)
        fun clearErrors()
        fun showLoginSuccess(message: String)
        fun showLoginFailed(message: String)
        fun navigateToHome()
        // REMOVED: The weird "abstract fun LoginPresenter" line that was here
    }

    interface Presenter {
        fun onLoginClicked(email: String, password: String)
        fun onDestroy()
    }

    interface Model {
        fun login(email: String, password: String, callback: OnLoginFinishedListener)

        interface OnLoginFinishedListener {
            fun onEmailError(message: String)
            fun onPasswordError(message: String)
            fun onSuccess(response: LoginResponse) // Keep ONLY this one
            fun onFailure(message: String)
        }
    }
}