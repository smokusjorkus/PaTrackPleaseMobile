package com.example.patrackplease.LandingPage
import com.example.patrackplease.utils.GradientUtils.blend
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.patrackplease.Login.LoginActivity
import com.example.patrackplease.R
import com.example.patrackplease.Register.RegisterActivity

class LandingPageActivity : Activity() {

    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landingpage)

        // ---------------------------
        // VIEW BINDING
        // ---------------------------
        val root = findViewById<View>(R.id.main)
        val content = findViewById<View>(R.id.content)
        val title = findViewById<View>(R.id.tvTitle)
        val subtitle = findViewById<View>(R.id.tvSubtitle)
        val footer = findViewById<View>(R.id.tvFooter)

        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        // ---------------------------
        // NAVIGATION
        // ---------------------------
        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // ---------------------------
        // ENTRY ANIMATIONS
        // ---------------------------
        content.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.fade_in)
        )

        title.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.slide_up)
        )

        subtitle.postDelayed({
            subtitle.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.slide_up)
            )
        }, 120)

        btnLogin.postDelayed({
            btnLogin.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.slide_up)
            )
        }, 220)

        btnRegister.postDelayed({
            btnRegister.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.slide_up)
            )
        }, 320)

        footer.postDelayed({
            footer.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.fade_in)
            )
        }, 420)

        // ---------------------------
        // ANIMATED GRADIENT BACKGROUND
        // ---------------------------
        startGradientAnimation(root)
    }

    // ---------------------------
    // GRADIENT ANIMATION
    // ---------------------------
    private fun startGradientAnimation(root: View) {

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

    // ---------------------------
    // COLOR BLENDING FUNCTION
    // ---------------------------

}