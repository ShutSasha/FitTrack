package com.example.mobile.presentation.view.verification

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobile.R
import com.example.mobile.data.api.RetrofitClient
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.ActivityVerificationBinding
import com.example.mobile.presentation.view.newPassword.NewPasswordActivity
import es.dmoral.toasty.Toasty
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerificationActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityVerificationBinding
    private var encryptedPreferencesManager: EncryptedPreferencesManager? = null
    private lateinit var codeInputs: List<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        _binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        encryptedPreferencesManager = EncryptedPreferencesManager(this)

        codeInputs = listOf(
            _binding.code1,
            _binding.code2,
            _binding.code3,
            _binding.code4
        )

        setupCodeInputBehavior()

        setUpFragment()
    }

    private fun setupCodeInputBehavior() {
        for (i in codeInputs.indices) {
            codeInputs[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < codeInputs.size - 1) {
                        codeInputs[i + 1].requestFocus()
                    } else if (s?.isEmpty() == true && i > 0) {
                        codeInputs[i - 1].requestFocus()
                    }
                }
            })
        }
    }

    private fun setUpFragment() {
        val fullText = getString(R.string.didn_t_received_a_code_resend)
        val clickablePart = "Resend"
        val spannable = SpannableString(fullText)

        val startIndex = fullText.indexOf(clickablePart)
        val endIndex = startIndex + clickablePart.length

        if (startIndex != -1) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    sendResetPasswordCode()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.parseColor("#52795B")
                    ds.isUnderlineText = false
                }
            }

            spannable.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            _binding.didntReceiveACode.text = spannable
            _binding.didntReceiveACode.movementMethod = LinkMovementMethod.getInstance()
            _binding.didntReceiveACode.highlightColor = Color.TRANSPARENT
        }

        _binding.verifyButton.setOnClickListener {
            val code = codeInputs.joinToString("") { it.text.toString() }

            if (code.length == 4) {
                val intent = Intent(this@VerificationActivity, NewPasswordActivity::class.java)
                val email = this.intent.getStringExtra("email")
                intent.putExtra("code", code)
                intent.putExtra("email", email)
                startActivity(intent)
            } else {
                Toasty.warning(this, "Please enter the full 4-digit code", Toast.LENGTH_SHORT, true)
                    .show()
            }
        }
    }

    private fun sendResetPasswordCode() {
        val email = intent.getStringExtra("email")

        email?.let {
            val authAPI = RetrofitClient.Companion.getInstance(this).authAPI

            authAPI.sendResetPasswordCode(email).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {

                            Toasty.success(
                                this@VerificationActivity,
                                "Confirmation code sent to email", Toast.LENGTH_SHORT, true
                            ).show()
                            Log.d("ForgotPassword", it.toString())

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
                            this@VerificationActivity,
                            errorMessage,
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                        Log.e("ForgotPassword", "Error: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toasty.error(
                        this@VerificationActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    Log.e("ForgotPassword", "Failed: ${t.message}", t)
                }
            })
        } ?: run {
            Toasty.error(this, "Email not provided", Toast.LENGTH_SHORT, true).show()
            finish()
        }
    }
}
