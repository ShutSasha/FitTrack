package com.example.mobile.presentation.view.newPassword

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobile.R
import com.example.mobile.data.api.RetrofitClient
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.ActivityNewPasswordBinding
import com.example.mobile.dto.auth.ResetPasswordDto
import com.example.mobile.presentation.view.login.LoginActivity
import es.dmoral.toasty.Toasty
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewPasswordActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityNewPasswordBinding
    private var encryptedPreferencesManager: EncryptedPreferencesManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        _binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        encryptedPreferencesManager = EncryptedPreferencesManager(this)

        _binding.resetPasswordButton.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword(){
        val email = intent.getStringExtra("email")
        val code = intent.getStringExtra("code")

        if (email != null && code != null) {

            val resetPasswordDto = ResetPasswordDto(
                email = email,
                code = code.toInt(),
                newPassword = _binding.newPassword.text.toString(),
                newPasswordConfirm = _binding.confirmPassword.text.toString()
            )
            val authAPI = RetrofitClient.Companion.getInstance(this).authAPI

            authAPI.confirmResetPassword(resetPasswordDto).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        response.body()?.let {

                            Toasty.success(
                                this@NewPasswordActivity,
                                "Password changed successfully", Toast.LENGTH_SHORT, true
                            ).show()
                            Log.d("ForgotPassword", it.toString())
                            val intent = Intent(this@NewPasswordActivity, LoginActivity::class.java)
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
                            this@NewPasswordActivity,
                            errorMessage,
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                        Log.e("ForgotPassword", "Error: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toasty.error(
                        this@NewPasswordActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    Log.e("ForgotPassword", "Failed: ${t.message}", t)
                }
            })
        } else {
            Toasty.error(this, "Email or code not provided", Toast.LENGTH_SHORT, true).show()
            finish()
        }
    }
}