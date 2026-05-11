package com.example.patrackplease.Register

import android.animation.ValueAnimator
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
import com.example.patrackplease.Login.LoginActivity
import com.example.patrackplease.R
import com.example.patrackplease.utils.GradientUtils.blend
import com.example.patrackplease.utils.SessionManager // IMPORTED
import com.google.android.material.textfield.TextInputEditText

// FIXED: Actually changed to AppCompatActivity()
class RegisterActivity : AppCompatActivity(), RegisterContract.View {

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText

    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText

    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvLoginLink: TextView

    private lateinit var presenter: RegisterContract.Presenter
    private lateinit var sessionManager: SessionManager // Declare SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registerscreen)

        // Initialize SessionManager
        sessionManager = SessionManager(this)

        // Initialize Views
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)
        tvLoginLink = findViewById(R.id.tvLoginLink)

        presenter = RegisterPresenter(this, RegisterModel())

        btnRegister.setOnClickListener {
            presenter.onRegisterClicked(
                etFirstName.text.toString().trim(),
                etLastName.text.toString().trim(),
                etUsername.text.toString().trim(),
                etEmail.text.toString().trim(),
                etPassword.text.toString().trim(),
                etConfirmPassword.text.toString().trim()
            )
        }

        tvLoginLink.setOnClickListener {
            navigateToLogin()
        }

        setupBackgroundAnimation()
    }

    // --- Contract Implementation ---

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
        btnRegister.isEnabled = false
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
        btnRegister.isEnabled = true
    }

    // ... Error UI methods stay the same ...
    override fun showFirstNameError(message: String) { etFirstName.error = message; etFirstName.requestFocus() }
    override fun showLastNameError(message: String) { etLastName.error = message; etLastName.requestFocus() }
    override fun showUsernameError(message: String) { etUsername.error = message; etUsername.requestFocus() }
    override fun showEmailError(message: String) { etEmail.error = message; etEmail.requestFocus() }
    override fun showPasswordError(message: String) { etPassword.error = message; etPassword.requestFocus() }
    override fun showConfirmPasswordError(message: String) { etConfirmPassword.error = message; etConfirmPassword.requestFocus() }

    override fun clearErrors() {
        etFirstName.error = null
        etLastName.error = null
        etUsername.error = null
        etEmail.error = null
        etPassword.error = null
        etConfirmPassword.error = null
    }

    override fun showRegisterSuccess(message: String) {
        showToast(message)
    }

    // NEW: Function for the RegisterPresenter to call to save data and auto-login
    fun onRegisterSuccessSaveData(token: String, email: String) {
        sessionManager.saveAuthToken(token)
        sessionManager.saveUserEmail(email)

        // Go to Dashboard instead of Login to provide a smoother UX
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showRegisterFailed(message: String) {
        showToast(message)
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    private fun setupBackgroundAnimation() {
        // Ensure your XML root layout has android:id="@+id/main"
        val root = findViewById<View>(R.id.main) ?: return
        val color1 = Color.parseColor("#fff05a")
        val color2 = Color.parseColor("#ff6e5a")
        val color3 = Color.parseColor("#ffd25a")

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 6000
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