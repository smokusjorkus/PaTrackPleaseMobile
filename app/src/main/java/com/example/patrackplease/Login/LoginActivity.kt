package com.example.patrackplease.Login
import com.example.patrackplease.utils.GradientUtils.blend
import android.animation.ValueAnimator
import com.example.patrackplease.Extensions.showToast
import com.example.patrackplease.Extensions.showLongToast
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
import android.widget.Toast
import com.example.patrackplease.R
import com.example.patrackplease.Register.RegisterActivity

class LoginActivity : Activity(), LoginContract.View {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvRegister: TextView

    private lateinit var presenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginscreen)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)
        tvRegister = findViewById<TextView>(R.id.tvRegister)

        presenter = LoginPresenter(this, LoginModel())

        btnLogin.setOnClickListener{
            presenter.onLoginClicked(
                etEmail.text.toString().trim(),
                etPassword.text.toString().trim()
                )
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val root = findViewById<View>(R.id.main)

        val color1 = Color.parseColor("#fff05a")
        val color2 = Color.parseColor("#ff6e5a")
        val color3 = Color.parseColor("#ffd25a")

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 5000
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