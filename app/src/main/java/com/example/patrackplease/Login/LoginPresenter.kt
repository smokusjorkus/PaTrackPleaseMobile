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

    // FIXED: Intercepting the success response to save the token and email
    // ... inside LoginPresenter.kt ...

    override fun onSuccess(response: LoginResponse) {
        val token = response.resolvedToken()
        val email = response.resolvedEmail()

        if (token != null && email != null) {
            view?.apply {
                hideLoading()
                showLoginSuccess(response.message ?: "Login Successful")
            }

            if (view is LoginActivity) {
                (view as LoginActivity).onLoginSuccessSaveData(token, email)
            } else {
                view?.navigateToHome()
            }
        } else {
            // Only show failure if the data is ACTUALLY missing
            view?.apply {
                hideLoading()
                showLoginFailed("Error: Missing credentials from server.")
            }
            android.util.Log.e("LOGIN_DEBUG", "Token: $token, Email: $email")
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
