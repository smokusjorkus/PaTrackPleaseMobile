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
        // 1. Extract from the top level (matching your new LoginResponse class)
        val token = response.token
        val email = response.email

        if (token != null && email != null) {
            view?.apply {
                hideLoading()
                // 2. Only show success if we actually have the data to save
                showRegisterSuccess(response.message ?: "Registration Successful")
            }

            if (view is RegisterActivity) {
                (view as RegisterActivity).onRegisterSuccessSaveData(token, email)
            } else {
                view?.navigateToLogin()
            }
        } else {
            // 3. Log the error so you know exactly what the server sent back
            android.util.Log.e("REGISTER_DEBUG", "Missing Data - Token: $token, Email: $email")

            view?.apply {
                hideLoading()
                showRegisterFailed("Registration appeared to succeed, but server data is missing.")
                // Optional: Still navigate to login if you want them to try logging in manually
                // navigateToLogin()
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