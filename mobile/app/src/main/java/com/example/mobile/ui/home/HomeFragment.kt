package com.example.mobile.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mobile.api.RetrofitClient
import com.example.mobile.databinding.FragmentHomeBinding
import com.example.mobile.dto.auth.LoginDto
import com.example.mobile.dto.auth.RefreshRes
import com.example.mobile.store.EncryptedPreferencesManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var encryptedPreferencesManager: EncryptedPreferencesManager? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        encryptedPreferencesManager = EncryptedPreferencesManager(requireContext())
        val appAPI = RetrofitClient.getInstance(requireContext()).appAPI
        val authAPI = RetrofitClient.getInstance(requireContext()).authAPI


        val textView: TextView = binding.textHome

//        textView.setOnClickListener {
//            appAPI.hello().enqueue(object : Callback<ResponseBody> {
//                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                    if (response.isSuccessful) {
//                        val registrationResponse = response.body()
//                        if (registrationResponse != null) {
//                            val responseText = registrationResponse.string()
//                            Log.d("Response", responseText)
//                        }
//                    } else {
//                        Log.e("Response", "Response Failed: ${response.message()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                    Log.e("Response", "Failed: ${t.message}")
//                }
//            })
//        }

        textView.setOnClickListener {

            val loginDto = LoginDto("cdidk1", "qwerty123123")

            authAPI.login(loginDto).enqueue(object : Callback<RefreshRes> {
                override fun onResponse(call: Call<RefreshRes>, response: Response<RefreshRes>) {
                    response.body()?.tokens?.let {
                        encryptedPreferencesManager?.saveTokens(it.accessToken, it.refreshToken)
                        Log.d("Login", it.toString())
                    }
                }

                override fun onFailure(call: Call<RefreshRes>, t: Throwable) {
                    Log.e("Login", "Failed: ${t.message}")
                }
            })
        }

        val textView1: TextView = binding.textHome1

        textView1.setOnClickListener {
            Log.d("HelloWith", encryptedPreferencesManager?.getAccessToken().toString())
            appAPI.helloWithAuth().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    response.body()?.let { body ->
                        val text = body.string()
                        Log.d("HelloWith", text)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("HelloWith", "Failed: ${t.message}")
                }
            })
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}