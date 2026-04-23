package com.example.patrackplease.Login

import com.example.patrackplease.Extensions.showToast
import com.example.patrackplease.Extensions.showLongToast
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.patrackplease.R

class LoginActivity : Activity(), LoginContract.View {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var presenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginscreen)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)

        presenter = LoginPresenter(this, LoginModel())

        btnLogin.setOnClickListener{
            presenter.onLoginClicked(
                etEmail.text.toString().trim(),
                etPassword.text.toString().trim()
                )
        }
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
        btnLogin.isEnabled = false
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
        btnLogin.isEnabled = true
    }

    override fun showEmailError(message: String) {
        etEmail.error = message
    }

    override fun showPasswordError(message: String) {
        etPassword.error = message
    }

    override fun clearErrors() {
        etEmail.error = null
        etPassword.error = null
    }

    override fun showLoginSuccess(message: String) {
        showToast("Login Successful")
    }

    override fun showLoginFailed(message: String) {
        showToast("Login Failed: Invalid email or password.")
    }

    override fun navigateToHome() {
        // startActivity(Intent(this, HomeActivity::class.java))
        // finish()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}