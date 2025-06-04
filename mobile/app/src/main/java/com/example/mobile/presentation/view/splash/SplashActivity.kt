package com.example.mobile.presentation.view.splash

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobile.R
import com.example.mobile.databinding.ActivitySplashBinding
import com.example.mobile.presentation.view.login.LoginActivity
import com.example.mobile.presentation.view.register.RegisterActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var _binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        _binding.main.post {
            runEntranceAnimation()
        }

        _binding.loginButton.setOnClickListener {
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        _binding.registerButton.setOnClickListener {
            val intent = Intent(this@SplashActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun runEntranceAnimation() {
        val shiftDp = 50f
        val shiftPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, shiftDp, resources.displayMetrics
        )

        _binding.imageView.apply {
            alpha = 0f
            translationY = shiftPx
        }
        _binding.entranceTitle.apply {
            alpha = 0f
            translationY = shiftPx
        }
        _binding.loginButton.apply {
            alpha = 0f
            translationY = shiftPx
        }
        _binding.registerButton.apply {
            alpha = 0f
            translationY = shiftPx
        }

        _binding.imageView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .withEndAction {
                _binding.entranceTitle.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .withEndAction {
                        _binding.loginButton.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(400)
                            .start()
                        _binding.registerButton.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(400)
                            .start()
                    }
                    .start()
            }
            .start()
    }
}
