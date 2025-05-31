package com.example.mobile.presentation.view.main.admin

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.mobile.R
import com.example.mobile.data.api.RetrofitClient
import com.example.mobile.databinding.FragmentChangeRoleBinding
import com.example.mobile.dto.role.ChangeRoleDto
import com.example.mobile.dto.user.UserSearchResponse
import com.example.mobile.domain.model.Role
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.presentation.view.util.ErrorUtils
import com.example.mobile.presentation.view.custom.ChangeRoleDropdown
import es.dmoral.toasty.Toasty
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeRoleFragment : Fragment() {

    private var _binding: FragmentChangeRoleBinding? = null
    private val binding get() = _binding!!
    private var encryptedPreferencesManager: EncryptedPreferencesManager? = null
    private var userSearchResponse: UserSearchResponse? = null
    private var roles: List<Role> = listOf()
    private var currentPage = 1
    private val pageSize = 10
    private var isLoading = false
    private var isLastPage = false
    private var searchRunnable: Runnable? = null
    private val searchHandler = Handler()
    private var currentQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangeRoleBinding.inflate(inflater, container, false)
        encryptedPreferencesManager = EncryptedPreferencesManager(requireContext())

        fetchRoles()
        setupScrollListener()

        binding.searchField.sortAndFilter.visibility = View.GONE

        binding.searchField.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
            }

            override fun afterTextChanged(s: Editable?) {
                searchRunnable = Runnable {
                    val query = s.toString().trim()

                    currentQuery = query
                    currentPage = 1
                    isLastPage = false
                    userSearchResponse = null

                    fetchUsers(
                        query = if (query.isEmpty()) null else query,
                        page = currentPage,
                        limit = pageSize
                    )
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }
        })

        return binding.root
    }

    private fun setupScrollListener() {
        val scrollView = binding?.main ?: return
        val scrollListener = ViewTreeObserver.OnScrollChangedListener {
            val b = binding ?: return@OnScrollChangedListener
            val sv = b.main
            val view = sv.getChildAt(0) ?: return@OnScrollChangedListener

            val diff = view.bottom - (sv.height + sv.scrollY)
            if (diff <= 0 && !isLoading && !isLastPage) {
                currentPage++
                fetchUsers(currentQuery, currentPage, pageSize)
            }
        }

        scrollView.viewTreeObserver.addOnScrollChangedListener(scrollListener)

        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                if (scrollView.viewTreeObserver.isAlive) {
                    scrollView.viewTreeObserver.removeOnScrollChangedListener(scrollListener)
                }
            }
        })
    }

    private fun fetchUsers(query: String? = null, page: Int = 1, limit: Int = 10) {
        if (isLoading || isLastPage) return
        isLoading = true

        val userApi = RetrofitClient.getInstance(requireContext()).userApi

        userApi.searchUsers(query, page, limit).enqueue(object : Callback<UserSearchResponse> {
            override fun onResponse(call: Call<UserSearchResponse>, response: Response<UserSearchResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    val newData = response.body()
                    newData?.let { result ->
                        if (page == 1) {
                            userSearchResponse = result
                        } else {
                            val updatedItems = userSearchResponse?.items.orEmpty() + result.items
                            userSearchResponse = result.copy(items = updatedItems)
                        }

                        if (result.items.size < limit) {
                            isLastPage = true
                        } else {
                            currentPage++
                        }

                        renderUsers()
                    }
                } else {
                    showError(ErrorUtils.parseErrorMessage(response.errorBody()?.string()))
                }
            }

            override fun onFailure(call: Call<UserSearchResponse>, t: Throwable) {
                isLoading = false
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun fetchRoles() {
        val accessToken = encryptedPreferencesManager?.getAccessToken()
        val roleApi = RetrofitClient.getInstance(requireContext()).roleApi

        roleApi.fetchroles(accessToken).enqueue(object : Callback<List<Role>> {
            override fun onResponse(call: Call<List<Role>>, response: Response<List<Role>>) {
                if (response.isSuccessful) {
                    roles = response.body() ?: listOf()
                    fetchUsers(null, currentPage, pageSize)
                } else {
                    showError(ErrorUtils.parseErrorMessage(response.errorBody()?.string()))
                }
            }

            override fun onFailure(call: Call<List<Role>>, t: Throwable) {
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun renderUsers() {
        userSearchResponse?.let { userRes ->

            binding.userListContainer.removeAllViews()

            val backgrounds = listOf(
                R.drawable.shape_yellow_card,
                R.drawable.shape_green_card,
                R.drawable.shape_blue_card,
                R.drawable.shape_pink_card,
            )

            for ((index, user) in userRes.items.withIndex()) {
                val userLayout = layoutInflater.inflate(R.layout.dropdown, binding.userListContainer, false) as LinearLayout
                val header = userLayout.findViewById<View>(R.id.header)
                val title = userLayout.findViewById<TextView>(R.id.headerTitle)
                val arrow = userLayout.findViewById<ImageView>(R.id.arrow)
                val optionsContainer = userLayout.findViewById<LinearLayout>(R.id.options)
                val selectedRole = userLayout.findViewById<TextView>(R.id.userRole)

                title.text = user.username
                selectedRole.text = if (user.roles.isNotEmpty()) user.roles[0].value else "No role"

                header.background = requireContext().getDrawable(backgrounds[index % backgrounds.size])

                val roleOptions = roles.map { it.value to it._id }

                ChangeRoleDropdown(
                    context = requireContext(),
                    container = optionsContainer,
                    header = header,
                    arrow = arrow,
                    selectedRole = selectedRole,
                    options = roleOptions,
                    onOptionSelected = { }
                ) { selectedRoleId, selectedRoleName ->
                    changeUserRole(user._id, selectedRoleId)
                }

                binding.userListContainer.addView(userLayout)
            }
        }
    }

    private fun changeUserRole(userId: String, roleId: String) {

        val changeRoleDto = ChangeRoleDto(
            userId = userId,
            roleId = roleId
        )

        val accessToken = encryptedPreferencesManager?.getAccessToken()
        val roleApi = RetrofitClient.getInstance(requireContext()).roleApi

        roleApi.changeRole(accessToken, changeRoleDto).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toasty.success(requireContext(), "Role updated", Toast.LENGTH_SHORT, true).show()
                } else {
                    showError(ErrorUtils.parseErrorMessage(response.errorBody()?.string()))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun showError(message: String?) {
        Toasty.error(requireContext(), message ?: "Unknown error", Toast.LENGTH_SHORT, true).show()
    }
}
