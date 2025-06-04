package com.example.mobile.presentation.view.main.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobile.R
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.FragmentAdminBinding

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private var encryptedPreferencesManager: EncryptedPreferencesManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        val role = encryptedPreferencesManager?.getRoleFromAccessToken()

        if (role == "MODERATOR") {
            binding.changeRoleButton.visibility = View.GONE
        }

        binding.foodRequestsButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_foodRequest)
        }

        binding.changeRoleButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_changeRole)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
