package com.example.patrackplease.Register

import com.example.patrackplease.Login.LoginResponse

class RegisterPresenter(
    private var view: RegisterContract.View?,
    private val model: RegisterContract.Model
) : RegisterContract.Presenter, RegisterContract.Model.OnRegisterFinishedListener {

    override fun onRegisterClicked(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        view?.apply {
            clearErrors()
            showLoading()
        }
        model.register(firstName, lastName, username, email, password, confirmPassword, this)
    }

    // --- Callbacks from the Model ---

    override fun onFirstNameError(message: String) {
        view?.apply {
            hideLoading()
            showFirstNameError(message)
        }
    }

    override fun onLastNameError(message: String) {
        view?.apply {
            hideLoading()
            showLastNameError(message)
        }
    }

    // ADDED: Missing username error callback
    override fun onUsernameError(message: String) {
        view?.apply {
            hideLoading()
            showUsernameError(message)
        }
    }

    override fun onEmailError(message: String) {
        view?.apply {
            hideLoading()
            showEmailError(message)
        }
    }

    override fun onPasswordError(message: String) {
        view?.apply {
            hideLoading()
            showPasswordError(message)
        }
    }

    override fun onConfirmPasswordError(message: String) {
        view?.apply {
            hideLoading()
            showConfirmPasswordError(message)
        }
    }

    override fun onSuccess(response: LoginResponse) {
        val token = response.resolvedToken()
        val email = response.resolvedEmail()

        if (token != null && email != null) {
            view?.apply {
                hideLoading()
                showRegisterSuccess(response.message ?: "Registration Successful")
            }

            if (view is RegisterActivity) {
                (view as RegisterActivity).onRegisterSuccessSaveData(token, email)
            } else {
                view?.navigateToLogin()
            }
        } else {
            android.util.Log.e("REGISTER_DEBUG", "Missing Data - Token: $token, Email: $email")

            view?.apply {
                hideLoading()
                showRegisterFailed("Registration successful. Please log in with your new account.")
                navigateToLogin()
            }
        }
    }


    override fun onFailure(message: String) {
        view?.apply {
            hideLoading()
            showRegisterFailed(message)
        }
    }

    override fun onDestroy() {
        view = null
    }
}
