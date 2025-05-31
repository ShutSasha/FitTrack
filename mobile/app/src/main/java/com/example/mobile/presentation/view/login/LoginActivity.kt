package com.example.mobile.presentation.view.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobile.presentation.view.forgotPassword.ForgotPasswordActivity
import com.example.mobile.presentation.view.main.MainActivity
import com.example.mobile.R
import com.example.mobile.presentation.view.register.RegisterActivity
import com.example.mobile.data.api.RetrofitClient
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.ActivityLoginBinding
import com.example.mobile.dto.auth.LoginDto
import com.example.mobile.dto.auth.RefreshRes
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityLoginBinding
    private var encryptedPreferencesManager: EncryptedPreferencesManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        setUpFragment()

        _binding.loginButton.setOnClickListener {
            login()
        }

        _binding.forgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setUpFragment() {
        val fullText = getString(R.string.don_t_have_an_account_register_now)
        val clickablePart = "Register now"
        val spannable = SpannableString(fullText)

        val startIndex = fullText.indexOf(clickablePart)
        val endIndex = startIndex + clickablePart.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#52795B")
                ds.isUnderlineText = false
            }
        }

        spannable.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        _binding.dontHaveAnAccount.text = spannable
        _binding.dontHaveAnAccount.movementMethod = LinkMovementMethod.getInstance()
        _binding.dontHaveAnAccount.highlightColor = Color.TRANSPARENT
    }

    private fun login() {

        val loginDto = LoginDto(
            email = _binding.email.text.toString(),
            username = _binding.username.text.toString(),
            password = _binding.password.text.toString(),
        )

        val authAPI = RetrofitClient.Companion.getInstance(this).authAPI

        authAPI.login(loginDto).enqueue(object : Callback<RefreshRes> {
            override fun onResponse(call: Call<RefreshRes>, response: Response<RefreshRes>) {
                if (response.isSuccessful) {
                    response.body()?.tokens?.let {

                        encryptedPreferencesManager?.saveTokens(it.accessToken, it.refreshToken)

                        Toasty.success(
                            this@LoginActivity,
                            "You logged in successfully!", Toast.LENGTH_SHORT, true
                        ).show()
                        Log.d("Login", it.toString())
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JSONObject(errorBody)
                        jsonObject.getString("message")
                    } catch (e: Exception) {
                        "Unknown error"
                    }

                    Toasty.error(
                        this@LoginActivity,
                        errorMessage,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    Log.e("Login", "Error: $errorMessage")
                }
            }

            override fun onFailure(call: Call<RefreshRes>, t: Throwable) {
                Toasty.error(
                    this@LoginActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT,
                    true
                ).show()
                Log.e("Login", "Failed: ${t.message}", t)
            }
        })
    }

}