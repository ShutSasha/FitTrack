package com.example.mobile.presentation.view.main.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mobile.R
import com.example.mobile.data.api.AuthAPI
import com.example.mobile.data.api.RetrofitClient
import com.example.mobile.data.dto.auth.PersonalizeDto
import com.example.mobile.data.dto.auth.PersonalizeResponse
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.FragmentEditProfileBinding
import com.example.mobile.domain.model.User
import com.example.mobile.presentation.view.util.PersonalizationDropdown
import com.example.mobile.presentation.view.util.UserProfileOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var encryptedPrefs: EncryptedPreferencesManager
    private lateinit var authApi: AuthAPI

    private lateinit var selectedGender: String
    private lateinit var selectedBodyType: String
    private lateinit var selectedActivityLevel: String
    private lateinit var selectedGoalType: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        encryptedPrefs = EncryptedPreferencesManager(requireContext())
        authApi = RetrofitClient.getInstance(requireContext()).authAPI

        setupDropdowns()
        loadUserData()

        binding.personalizeButton.setOnClickListener {
            sendPersonalizationRequest()
        }
    }

    private fun setupDropdowns() {
        PersonalizationDropdown(requireContext()).setUpDropdown(
            container = binding.genderOptions,
            header = binding.headerGender,
            arrow = binding.genderArrow,
            selectedTextView = binding.selectedGender,
            options = UserProfileOptions.genders,
            onOptionSelected = { selectedGender = it },
            optionBackgroundRes = R.drawable.shape_outlined_dropdown_item
        )

        PersonalizationDropdown(requireContext()).setUpDropdown(
            container = binding.bodyTypeOptions,
            header = binding.headerBodyType,
            arrow = binding.bodyTypeArrow,
            selectedTextView = binding.selectedBodyType,
            options = UserProfileOptions.bodyTypes,
            onOptionSelected = { selectedBodyType = it },
            optionBackgroundRes = R.drawable.shape_outlined_dropdown_item
        )

        PersonalizationDropdown(requireContext()).setUpDropdown(
            container = binding.activityLevelOptions,
            header = binding.headerActivityLevel,
            arrow = binding.activityLevelArrow,
            selectedTextView = binding.selectedActivityLevel,
            options = UserProfileOptions.activityLevels,
            onOptionSelected = { selectedActivityLevel = it },
            optionBackgroundRes = R.drawable.shape_outlined_dropdown_item
        )

        PersonalizationDropdown(requireContext()).setUpDropdown(
            container = binding.goalTypeOptions,
            header = binding.headerGoalType,
            arrow = binding.goalTypeArrow,
            selectedTextView = binding.selectedGoalType,
            options = UserProfileOptions.goalTypes,
            onOptionSelected = { selectedGoalType = it },
            optionBackgroundRes = R.drawable.shape_outlined_dropdown_item
        )
    }


    private fun loadUserData() {
        val userId = encryptedPrefs.getUserIdFromAccessToken() ?: return
        RetrofitClient.getInstance(requireContext()).userApi.getUserById(userId)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    val user = response.body() ?: return

                    binding.usernameText.text = "@${user.username}"
                    Glide.with(requireContext()).load(user.avatar).into(binding.avatarImage)

                    binding.height.setText(user.height.toString())
                    binding.weight.setText(user.weight.toString())
                    binding.birthDate.setText(user.birthDate.take(10))
                    binding.targetWeight.setText(user.targetWeight.toString())

                    selectedGender =
                        setSelected(binding.selectedGender, UserProfileOptions.genders, user.gender)
                    selectedBodyType = setSelected(
                        binding.selectedBodyType,
                        UserProfileOptions.bodyTypes,
                        user.bodyType
                    )
                    selectedActivityLevel = setSelected(
                        binding.selectedActivityLevel,
                        UserProfileOptions.activityLevels,
                        user.activityLevel
                    )
                    selectedGoalType = setSelected(
                        binding.selectedGoalType,
                        UserProfileOptions.goalTypes,
                        user.goalType
                    )
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    showToast("Failed to load profile")
                }
            })
    }

    private fun sendPersonalizationRequest() {
        val userId = encryptedPrefs.getUserIdFromAccessToken() ?: return

        val height = binding.height.text.toString().toIntOrNull()
        val weight = binding.weight.text.toString().toDoubleOrNull()
        val birthDate = binding.birthDate.text.toString().trim()
        val targetWeight = binding.targetWeight.text.toString().toDoubleOrNull()

        when {
            selectedGender.isBlank() || selectedBodyType.isBlank() ||
                    selectedActivityLevel.isBlank() || selectedGoalType.isBlank() -> {
                showToast("Please, select every field")
                return
            }

            height == null || height <= 0 -> {
                showToast("Insert correct height")
                return
            }

            weight == null || weight <= 0 -> {
                showToast("Insert correct weight")
                return
            }

            !birthDate.matches(Regex("""\d{4}-\d{2}-\d{2}""")) -> {
                showToast("Birth date format should be YYYY-MM-DD")
                return
            }

            targetWeight == null || targetWeight <= 0 -> {
                showToast("Insert correct target weight")
                return
            }
        }

        val dto = try {
            PersonalizeDto(
                userId = encryptedPrefs.getUserIdFromAccessToken() ?: "",
                gender = selectedGender,
                height = binding.height.text.toString().toInt(),
                weight = binding.weight.text.toString().toDouble(),
                bodyType = selectedBodyType,
                activityLevel = selectedActivityLevel,
                birthDate = binding.birthDate.text.toString(),
                goalType = selectedGoalType,
                targetWeight = binding.targetWeight.text.toString().toDouble(),
                currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
        } catch (e: Exception) {
            showToast("Check all fields are filled correctly")
            return
        }

        Log.d("PERSONALIZE_DTO", "Sending: $dto")
        authApi.personalize(dto).enqueue(object : Callback<PersonalizeResponse> {
            override fun onResponse(
                call: Call<PersonalizeResponse>,
                response: Response<PersonalizeResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        encryptedPrefs.saveUserId(it._id)
                        showToast("Profile updated successfully!")
                        findNavController().navigateUp()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("DTO_RESPONSE_ERROR", "Status: ${response.code()}, body: $errorBody")
                    showToast("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<PersonalizeResponse>, t: Throwable) {
                showToast("Network error: ${t.message}")
                Log.e("DTO_FAILURE", "Failure: ${t.message}", t)
            }
        })
        Log.d(
            "PERSONALIZE_DTO_VALUES", """
    userId=${dto.userId}
    gender=${dto.gender}
    height=${dto.height}
    weight=${dto.weight}
    bodyType=${dto.bodyType}
    activityLevel=${dto.activityLevel}
    birthDate=${dto.birthDate}
    goalType=${dto.goalType}
    targetWeight=${dto.targetWeight}
    currentDate=${dto.currentDate}
""".trimIndent()
        )


    }

    private fun setSelected(
        textView: TextView,
        options: List<Pair<String, String>>,
        value: String
    ): String {
        val display = options.firstOrNull { it.second == value }?.first ?: value
        textView.text = display
        return value
    }

    private fun formatDate(input: String): String {
        return input.take(10)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}
