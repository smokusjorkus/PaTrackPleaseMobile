package com.example.patrackplease.Login

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity // MUST BE IMPORTED
import com.example.patrackplease.Dashboard.DashboardActivity
import com.example.patrackplease.Extensions.showToast
import com.example.patrackplease.R
import com.example.patrackplease.Register.RegisterActivity
import com.example.patrackplease.utils.GradientUtils.blend
import com.example.patrackplease.utils.SessionManager // Import your SessionManager
import com.google.android.material.textfield.TextInputEditText // MUST BE IMPORTED

// FIXED: Actually changed to AppCompatActivity()
class LoginActivity : Activity(), LoginContract.View {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvRegister: TextView

    private lateinit var presenter: LoginContract.Presenter
    private lateinit var sessionManager: SessionManager // Declare SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginscreen)

        // Initialize SessionManager
        sessionManager = SessionManager(this)

        // 1. Initialize Views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)
        tvRegister = findViewById(R.id.tvRegister)

        // 2. Initialize Presenter
        presenter = LoginPresenter(this, LoginModel())

        // 3. Button Clicks
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            presenter.onLoginClicked(email, password)
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // 4. Background UI
        setupBackgroundAnimation()
    }

    // --- Contract Implementation ---

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
        etEmail.requestFocus()
    }

    override fun showPasswordError(message: String) {
        etPassword.error = message
        etPassword.requestFocus()
    }

    override fun clearErrors() {
        etEmail.error = null
        etPassword.error = null
    }

    override fun showLoginSuccess(message: String) {
        showToast(message)
    }

    override fun showLoginFailed(message: String) {
        showToast(message)
    }

    // UPDATED: Now requires the token and email so we can save them!
    fun onLoginSuccessSaveData(token: String, email: String) {
        sessionManager.saveAuthToken(token)
        sessionManager.saveUserEmail(email)
        navigateToHome()
    }

    override fun navigateToHome() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    private fun setupBackgroundAnimation() {
        val root = findViewById<View>(R.id.main) ?: return
        val color1 = Color.parseColor("#fff05a")
        val color2 = Color.parseColor("#ff6e5a")
        val color3 = Color.parseColor("#ffd25a")

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 5000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                val b1 = blend(color1, color2, value)
                val b2 = blend(color2, color3, value)
                val b3 = blend(color3, color1, value)

                root.background = GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    intArrayOf(b1, b2, b3)
                )
            }
            start()
        }
    }
}