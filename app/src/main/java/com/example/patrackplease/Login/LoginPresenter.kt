package com.example.patrackplease.Login

import com.example.patrackplease.Login.LoginResponse

class LoginPresenter(
    private var view: LoginContract.View?,
    private val model: LoginContract.Model
) : LoginContract.Presenter, LoginContract.Model.OnLoginFinishedListener {

    override fun onLoginClicked(email: String, password: String) {
        view?.apply {
            clearErrors()
            showLoading()
        }
        model.login(email, password, this)
    }

    // --- Callbacks from the Model ---

    override fun onEmailError(message: String) {
        view?.hideLoading()
        view?.showEmailError(message)
    }

    override fun onPasswordError(message: String) {
        view?.hideLoading()
        view?.showPasswordError(message)
    }

    // This must match EXACTLY what is in LoginContract.Model.OnLoginFinishedListener
    // This now matches the LoginResponse object defined in your API package
    override fun onSuccess(response: LoginResponse) {
        view?.apply {
            hideLoading()
            // We use response.message because the object contains the string from the server
            showLoginSuccess(response.message)
            navigateToHome()
        }
    }

    override fun onFailure(message: String) {
        view?.apply {
            hideLoading()
            showLoginFailed(message)
        }
    }

    override fun onDestroy() {
        view = null // Clears the reference to the Activity to prevent memory leaks
    }
}