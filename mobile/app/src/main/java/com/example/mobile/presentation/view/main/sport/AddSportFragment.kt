package com.example.mobile.presentation.view.main.sport

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobile.R
import com.example.mobile.data.api.RetrofitClient
import com.example.mobile.data.dto.activity.AddActivityRequest
import com.example.mobile.data.dto.activity.EditActivityRequest
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.FragmentAddSportBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class AddSportFragment : Fragment() {

    private lateinit var binding: FragmentAddSportBinding
    private lateinit var encryptedPrefs: EncryptedPreferencesManager

    private lateinit var activityId: String
    private lateinit var activityName: String
    private var caloriesPerMin: Double = 0.0

    private var isEditMode = false
    private var existingDate: String? = null
    private var existingMinutes: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddSportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        encryptedPrefs = EncryptedPreferencesManager(requireContext())

        arguments?.let { args ->
            activityId = args.getString("activityId") ?: return
            activityName = args.getString("activityName") ?: return
            caloriesPerMin = args.getDouble("caloriesPerMin")
        }

        isEditMode = arguments?.getBoolean("isEditMode") ?: false
        existingDate = arguments?.getString("existingDate")
        existingMinutes = arguments?.getInt("totalMinutes") ?: 0

        if (isEditMode) {
            binding.inputMinutes.setText(existingMinutes.toString())
        }

        binding.activityName.text = activityName
        binding.caloriesText.text = "$caloriesPerMin Kcal / min"

        binding.buttonSave.setOnClickListener {
            val minutes = binding.inputMinutes.text.toString().toIntOrNull()
            val userId = encryptedPrefs.getUserId() ?: return@setOnClickListener
            val date = LocalDate.now().toString()

            if (minutes == null || minutes <= 0) {
                Toast.makeText(requireContext(), "Enter valid minutes", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val api = RetrofitClient.getInstance(requireContext()).activityApi

            val callback = object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Activity saved", Toast.LENGTH_SHORT)
                            .show()
                        findNavController().navigate(R.id.navigation_home)
                    } else {
                        Toast.makeText(requireContext(), "Server error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
                }
            }

            if (isEditMode && existingDate != null) {
                val date = existingDate!!.substring(0, 10)
                val request = EditActivityRequest(
                    userId = userId,
                    date = date,
                    activityId = activityId,
                    totalMinutes = minutes
                )
                Log.d("EditActivityRequest", request.toString())
                api.editActivityInDailyLog(request).enqueue(callback)
            }
            else {
                val request = AddActivityRequest(
                    userId = userId,
                    date = date,
                    activityId = activityId,
                    totalMinutes = minutes
                )
                api.addActivityToDailyLog(request).enqueue(callback)
            }
        }
    }
}
