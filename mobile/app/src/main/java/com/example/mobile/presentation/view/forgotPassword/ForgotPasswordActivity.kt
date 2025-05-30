package com.example.mobile.presentation.view.forgotPassword

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
import com.example.mobile.presentation.view.register.RegisterActivity
import com.example.mobile.presentation.view.verification.VerificationActivity
import com.example.mobile.data.api.RetrofitClient
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.ActivityForgotPasswordBinding
import es.dmoral.toasty.Toasty
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityForgotPasswordBinding
    private var encryptedPreferencesManager: EncryptedPreferencesManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        _binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        setUpFragment()

        _binding.sendCodeButton.setOnClickListener {
            sendResetPasswordCode()
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
                val intent = Intent(this@ForgotPasswordActivity, RegisterActivity::class.java)
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

    private fun sendResetPasswordCode() {
        val email = _binding.email.text.toString()

        val authAPI = RetrofitClient.Companion.getInstance(this).authAPI

        authAPI.sendResetPasswordCode(email).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let {

                        Toasty.success(
                            this@ForgotPasswordActivity,
                            "Confirmation code sent to email", Toast.LENGTH_SHORT, true
                        ).show()
                        Log.d("ForgotPassword", it.toString())
                        val intent =
                            Intent(this@ForgotPasswordActivity, VerificationActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
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
                        this@ForgotPasswordActivity,
                        errorMessage,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    Log.e("ForgotPassword", "Error: $errorMessage")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toasty.error(
                    this@ForgotPasswordActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT,
                    true
                ).show()
                Log.e("ForgotPassword", "Failed: ${t.message}", t)
            }
        })
    }
}