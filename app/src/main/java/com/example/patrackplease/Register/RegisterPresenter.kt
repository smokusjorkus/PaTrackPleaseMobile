package com.example.patrackplease.Register

class RegisterPresenter(
    private var view: RegisterContract.View?,
    private val model: RegisterContract.Model
) : RegisterContract.Presenter, RegisterContract.Model.OnRegisterFinishedListener {

    override fun onRegisterClicked(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        view?.clearErrors()
        view?.showLoading()
        model.register(name, email, password, confirmPassword, this)
    }

    override fun onNameError(message: String) {
        view?.hideLoading()
        view?.showNameError(message)
    }

    override fun onEmailError(message: String) {
        view?.hideLoading()
        view?.showEmailError(message)
    }

    override fun onPasswordError(message: String) {
        view?.hideLoading()
        view?.showPasswordError(message)
    }

    override fun onConfirmPasswordError(message: String) {
        view?.hideLoading()
        view?.showConfirmPasswordError(message)
    }

    override fun onSuccess(message: String) {
        view?.hideLoading()
        view?.showRegisterSuccess(message)
        view?.navigateToLogin()
    }

    override fun onFailure(message: String) {
        view?.hideLoading()
        view?.showRegisterFailed(message)
    }

    override fun onDestroy() {
        view = null
    }
}