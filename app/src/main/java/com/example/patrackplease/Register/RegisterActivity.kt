package com.example.patrackplease.Register
import com.example.patrackplease.utils.GradientUtils.blend
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
import androidx.appcompat.app.AppCompatActivity
import com.example.patrackplease.Extensions.showToast
import com.example.patrackplease.Login.LoginActivity
import com.example.patrackplease.R

class RegisterActivity : Activity(), RegisterContract.View {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText

    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvLoginLink: TextView

    private lateinit var presenter: RegisterContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registerscreen)

        // Views
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)
        tvLoginLink = findViewById(R.id.tvLoginLink)

        presenter = RegisterPresenter(this, RegisterModel())

        // Register click
        btnRegister.setOnClickListener {
            presenter.onRegisterClicked(
                etName.text.toString().trim(),
                etEmail.text.toString().trim(),
                etPassword.text.toString().trim(),
                etConfirmPassword.text.toString().trim()
            )
        }

        // Navigate back to login
        tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val root = findViewById<View>(R.id.main)

        val color1 = Color.parseColor("#fff05a")
        val color2 = Color.parseColor("#ff6e5a")
        val color3 = Color.parseColor("#ffd25a")

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 6000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float

            val blended1 = blend(color1, color2, value)
            val blended2 = blend(color2, color3, value)
            val blended3 = blend(color3, color1, value)

            val gradient = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(blended1, blended2, blended3)
            )

            root.background = gradient
        }

        animator.start()
    }



    // ---------------- UI STATES ----------------

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
        showToast("Account created successfully")
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun showRegisterFailed(message: String) {
        showToast("Registration failed")
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}