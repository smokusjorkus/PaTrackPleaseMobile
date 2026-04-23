package com.example.patrackplease.Login

class LoginPresenter(
    private var view: LoginContract.View?,
    private val model: LoginContract.Model
) : LoginContract.Presenter, LoginContract.Model.OnLoginFinishedListener {

    override fun onLoginClicked(email: String, password: String) {
        view?.clearErrors()
        view?.showLoading()
        model.login(email, password, this)
    }

    override fun onEmailError(message: String) {
        view?.hideLoading()
        view?.showEmailError(message)
    }

    override fun onPasswordError(message: String) {
        view?.hideLoading()
        view?.showPasswordError(message)
    }

    override fun onSuccess(message: String) {
        view?.hideLoading()
        view?.showLoginSuccess(message)
        view?.navigateToHome()
    }

    override fun onFailure(message: String) {
        view?.hideLoading()
        view?.showLoginFailed(message)
    }

    override fun onDestroy() {
        view = null
    }
}