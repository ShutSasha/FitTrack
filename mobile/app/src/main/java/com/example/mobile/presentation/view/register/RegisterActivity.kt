package com.example.mobile.presentation.view.register

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
import com.example.mobile.R
import com.example.mobile.data.api.RetrofitClient
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.ActivityRegisterBinding
import com.example.mobile.dto.auth.RefreshRes
import com.example.mobile.dto.auth.RegisterDto
import com.example.mobile.presentation.view.util.ErrorUtils
import com.example.mobile.presentation.view.login.LoginActivity
import com.example.mobile.presentation.view.personalization.PersonalizationActivity
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityRegisterBinding
    private var encryptedPreferencesManager: EncryptedPreferencesManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        setUpFragment()

        _binding.registerButton.setOnClickListener {
            register()
        }
    }

    private fun setUpFragment() {
        val fullText = getString(R.string.already_have_an_account_login_now)
        val clickablePart = "Login now"
        val spannable = SpannableString(fullText)

        val startIndex = fullText.indexOf(clickablePart)
        val endIndex = startIndex + clickablePart.length

        if (startIndex != -1) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.parseColor("#52795B")
                    ds.isUnderlineText = false
                }
            }

            spannable.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            _binding.alreadyHaveAnAaccount.text = spannable
            _binding.alreadyHaveAnAaccount.movementMethod = LinkMovementMethod.getInstance()
            _binding.alreadyHaveAnAaccount.highlightColor = Color.TRANSPARENT
        }
    }

    private fun register(){

        val registerDto = RegisterDto(
            email = _binding.email.text.toString(),
            username = _binding.username.text.toString(),
            password = _binding.password.text.toString(),
            confirmPassword = _binding.confirmPassword.text.toString(),
        )

        val authAPI = RetrofitClient.Companion.getInstance(this).authAPI

        authAPI.register(registerDto).enqueue(object : Callback<RefreshRes> {
            override fun onResponse(call: Call<RefreshRes>, response: Response<RefreshRes>) {
                if (response.isSuccessful) {
                    response.body()?.tokens?.let {

                        encryptedPreferencesManager?.saveTokens(it.accessToken, it.refreshToken)

                        Toasty.success(
                            this@RegisterActivity,
                            "You registered successfully!", Toast.LENGTH_SHORT, true
                        ).show()
                        Log.d("Register", it.toString())

                        val intent =
                            Intent(this@RegisterActivity, PersonalizationActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    val errorMessage = ErrorUtils.parseErrorMessage(response.errorBody()?.string())

                    Toasty.error(
                        this@RegisterActivity,
                        errorMessage,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    Log.e("Register", "Error: $errorMessage")
                }
            }

            override fun onFailure(call: Call<RefreshRes>, t: Throwable) {
                Toasty.error(
                    this@RegisterActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT,
                    true
                ).show()
                Log.e("Register", "Failed: ${t.message}", t)
            }
        })
    }
}