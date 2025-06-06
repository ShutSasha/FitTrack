package com.example.mobile.presentation.view.personalization

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
import com.example.mobile.data.dto.auth.PersonalizeDto
import com.example.mobile.data.dto.auth.PersonalizeResponse
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.ActivityPersonalizationBinding
import com.example.mobile.presentation.view.splash.SplashActivity
import com.example.mobile.presentation.view.util.DateUtils.getTodayDateString
import com.example.mobile.presentation.view.util.PersonalizationDropdown
import com.example.mobile.presentation.view.util.UserProfileOptions
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalizationActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityPersonalizationBinding
    private var encryptedPreferencesManager: EncryptedPreferencesManager? = null
    private lateinit var personalizationDropdown: PersonalizationDropdown
    private lateinit var selectedGender: String
    private lateinit var selectedBodyType: String
    private lateinit var selectedActivityLevel: String
    private lateinit var selectedGoalType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        _binding = ActivityPersonalizationBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        encryptedPreferencesManager = EncryptedPreferencesManager(this)
        personalizationDropdown = PersonalizationDropdown(this)

        personalizationDropdown.setUpDropdown(
            container = _binding.genderOptions,
            header = _binding.headerGender,
            arrow = _binding.genderArrow,
            selectedTextView = _binding.selectedGender,
            options = UserProfileOptions.genders,
            onOptionSelected = { selectedKey -> selectedGender = selectedKey },
            optionBackgroundRes = R.drawable.shape_outlined_button
        )

        personalizationDropdown.setUpDropdown(
            container = _binding.bodyTypeOptions,
            header = _binding.headerBodyType,
            arrow = _binding.bodyTypeArrow,
            selectedTextView = _binding.selectedBodyType,
            options = UserProfileOptions.bodyTypes,
            onOptionSelected = { selectedKey -> selectedBodyType = selectedKey },
            optionBackgroundRes = R.drawable.shape_outlined_button
        )

        personalizationDropdown.setUpDropdown(
            container = _binding.activityLevelOptions,
            header = _binding.headerActivityLevel,
            arrow = _binding.activityLevelArrow,
            selectedTextView = _binding.selectedActivityLevel,
            options = UserProfileOptions.activityLevels,
            onOptionSelected = { selectedKey -> selectedActivityLevel = selectedKey },
            optionBackgroundRes = R.drawable.shape_outlined_button
        )

        personalizationDropdown.setUpDropdown(
            container = _binding.goalTypeOptions,
            header = _binding.headerGoalType,
            arrow = _binding.goalTypeArrow,
            selectedTextView = _binding.selectedGoalType,
            options = UserProfileOptions.goalTypes,
            onOptionSelected = { selectedKey -> selectedGoalType = selectedKey },
            optionBackgroundRes = R.drawable.shape_outlined_button
        )


        _binding.personalizeButton.setOnClickListener {
            personalize()
        }

    }

    private fun personalize() {

        val personalizeDto = PersonalizeDto(
            userId = encryptedPreferencesManager?.getUserIdFromAccessToken() ?: "",
            gender = selectedGender,
            height = _binding.height.text.toString().toInt(),
            weight = _binding.weight.text.toString().toDouble(),
            bodyType = selectedBodyType,
            activityLevel = selectedActivityLevel,
            birthDate = _binding.birthDate.text.toString(),
            goalType = selectedGoalType,
            targetWeight = _binding.targetWeight.text.toString().toDouble(),
            currentDate = getTodayDateString()
        )
        Log.d("Personalization", personalizeDto.toString())
        val authAPI = RetrofitClient.Companion.getInstance(this).authAPI

        authAPI.personalize(personalizeDto).enqueue(object : Callback<PersonalizeResponse> {
            override fun onResponse(
                call: Call<PersonalizeResponse>,
                response: Response<PersonalizeResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Toasty.success(
                            this@PersonalizationActivity,
                            "You set up profile successfully!", Toast.LENGTH_SHORT, true
                        ).show()
                        Log.d("Personalization", it.toString())
                        encryptedPreferencesManager?.saveUserId(response.body()?._id)

                        val intent =
                            Intent(this@PersonalizationActivity, SplashActivity::class.java)
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
                        this@PersonalizationActivity,
                        errorMessage,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    Log.e("Personalization", "Error: $errorMessage")
                }
            }

            override fun onFailure(call: Call<PersonalizeResponse>, t: Throwable) {
                Toasty.error(
                    this@PersonalizationActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT,
                    true
                ).show()
                Log.e("Personalization", "Failed: ${t.message}", t)
            }
        })
    }
}
