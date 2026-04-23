package com.example.patrackplease.Register

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.patrackplease.Extensions.showToast
import com.example.patrackplease.R

class RegisterActivity : AppCompatActivity(), RegisterContract.View {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var presenter: RegisterContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registerscreen)

        etName = findViewById(R.id.RegEtName)
        etEmail = findViewById(R.id.RegEtEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.RegisterEtName)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)

        presenter = RegisterPresenter(this, RegisterModel())

        btnRegister.setOnClickListener {
            presenter.onRegisterClicked(
                etName.text.toString().trim(),
                etEmail.text.toString().trim(),
                etPassword.text.toString().trim(),
                etConfirmPassword.text.toString().trim()
            )
        }
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
        btnRegister.isEnabled = false
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
        btnRegister.isEnabled = true
    }

    override fun showNameError(message: String) {
        etName.error = message
        etName.requestFocus()
    }

    override fun showEmailError(message: String) {
        etEmail.error = message
        etEmail.requestFocus()
    }

    override fun showPasswordError(message: String) {
        etPassword.error = message
        etPassword.requestFocus()
    }

    override fun showConfirmPasswordError(message: String) {
        etConfirmPassword.error = message
        etConfirmPassword.requestFocus()
    }

    override fun clearErrors() {
        etName.error = null
        etEmail.error = null
        etPassword.error = null
        etConfirmPassword.error = null
    }

    override fun showRegisterSuccess(message: String) {
        showToast(message)
    }

    override fun showRegisterFailed(message: String) {
        showToast(message)
    }

    override fun navigateToLogin() {
        finish()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}