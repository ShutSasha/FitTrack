package com.example.mobile.presentation.view.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.mobile.R
import com.example.mobile.data.api.RetrofitClient
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.FragmentProfileBinding
import com.example.mobile.domain.model.User
import com.example.mobile.presentation.view.util.UserProfileOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var retrofitClient: RetrofitClient
    private lateinit var encryptedPrefs: EncryptedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retrofitClient = RetrofitClient.getInstance(requireContext())
        encryptedPrefs = EncryptedPreferencesManager(requireContext())


        binding.personalizeButton.setOnClickListener {
            val action = Navigation.findNavController(requireView())
            action.navigate(R.id.navigation_edit_profile)
        }

        loadUserProfile()
    }


    private fun loadUserProfile() {
        val userId = encryptedPrefs.getUserIdFromAccessToken() ?: return
        retrofitClient.userApi.getUserById(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body() ?: return
                    with(binding) {
                        usernameText.text = "@${user.username}"
                        heightText.text = "${user.height}cm"
                        weightText.text = "${user.weight}kg"
                        goalTypeText.text = formatGoalType(user.goalType)
                        targetWeightText.text = "${user.targetWeight}kg"
                        Glide.with(requireContext()).load(user.avatar).into(avatarImage)
                    }
                } else {
                    showToast(getString(R.string.profile_load_error))
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                showToast(getString(R.string.profile_load_error))
            }
        })
    }

    private fun formatGoalType(rawValue: String): String {
        return UserProfileOptions.goalTypes.firstOrNull { it.second == rawValue }?.first ?: rawValue
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

