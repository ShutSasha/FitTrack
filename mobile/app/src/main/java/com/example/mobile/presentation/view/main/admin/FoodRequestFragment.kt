package com.example.mobile.presentation.view.main.admin

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.example.mobile.R
import com.example.mobile.data.api.RetrofitClient
import com.example.mobile.data.dto.productRequest.ProductRequestResponse
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.FragmentFoodRequestBinding
import com.example.mobile.dto.role.ChangeRoleDto
import com.example.mobile.presentation.view.util.ErrorUtils
import es.dmoral.toasty.Toasty
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodRequestFragment : Fragment() {

    private var _binding: FragmentFoodRequestBinding? = null
    private val binding get() = _binding!!
    private var encryptedPreferencesManager: EncryptedPreferencesManager? = null
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null
    private var selectedFilter: String? = null
    private var sortBy: String? = null
    private var sortOrder: String? = null
    private var productRequestResponse: ProductRequestResponse? = null
    private var currentQuery: String = ""
    private var currentPage = 1
    private val pageSize = 10
    private var isLoading = false
    private var isLastPage = false
    private var searchRunnable: Runnable? = null
    private val searchHandler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodRequestBinding.inflate(inflater, container, false)
        encryptedPreferencesManager = EncryptedPreferencesManager(requireContext())

        setUpFragment()

        val navBackStackEntry = findNavController().currentBackStackEntry

        navBackStackEntry?.savedStateHandle?.getLiveData<String?>("selected_filter")
            ?.observe(viewLifecycleOwner) { selectedValue ->
                if (selectedValue != null) {
                    binding.searchField.filter.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.shape_dark_blue_card_20dp
                    )
                    selectedFilter = selectedValue
                } else {
                    binding.searchField.filter.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.shape_blue_card_20dp
                    )
                    selectedFilter = null
                }
            }

        navBackStackEntry?.savedStateHandle?.getLiveData<String?>("selected_sort_by")
            ?.observe(viewLifecycleOwner) { value ->
                sortBy = value
                updateSortBackground()
            }

        navBackStackEntry?.savedStateHandle?.getLiveData<String?>("selected_sort_order")
            ?.observe(viewLifecycleOwner) { value ->
                sortOrder = value
                updateSortBackground()
            }

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
                    productRequestResponse = null

                    fetchProductRequests(
                        query = if (query.isEmpty()) null else query,
                        page = currentPage,
                        limit = pageSize
                    )
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }
        })

        setupScrollListener()

        return binding.root
    }

    private fun setUpFragment() {
        binding.searchField.filter.setOnClickListener {
            val bundle = Bundle().apply {
                putString("selected_filter", selectedFilter)
            }
            findNavController().navigate(R.id.navigation_filter, bundle)
        }

        binding.searchField.sort.setOnClickListener {
            val bundle = Bundle().apply {
                putString("selected_sort_by", sortBy)
                putString("selected_sort_order", sortOrder)
            }
            findNavController().navigate(R.id.navigation_sort, bundle)
        }

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
                    productRequestResponse = null
                    fetchProductRequests(
                        query = if (query.isEmpty()) null else query,
                        page = currentPage,
                        limit = pageSize
                    )
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }
        })
        fetchProductRequests(null, currentPage, pageSize)
        renderProductRequests()
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
                fetchProductRequests(currentQuery, currentPage, pageSize)
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

    private fun updateSortBackground() {
        val backgroundRes = if (sortBy != null || sortOrder != null) {
            R.drawable.shape_dark_blue_card_20dp
        } else {
            R.drawable.shape_blue_card_20dp
        }
        binding.searchField.sort.background = ContextCompat.getDrawable(requireContext(), backgroundRes)
    }

    private fun fetchProductRequests(query: String? = null, page: Int = 1, limit: Int = 10) {
        if (isLoading || isLastPage) return
        isLoading = true

        val accessToken = encryptedPreferencesManager?.getAccessToken()

        val productRequestApi = RetrofitClient.getInstance(requireContext()).productRequestApi

        productRequestApi.searchProductRequests(
            accessToken = accessToken,
            query = query,
            page = page,
            limit = limit,
            sortBy = sortBy,
            sortOrder = sortOrder,
            productType = selectedFilter
        ).enqueue(object : Callback<ProductRequestResponse> {
            override fun onResponse(
                call: Call<ProductRequestResponse>,
                response: Response<ProductRequestResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    val newData = response.body()
                    newData?.let { result ->
                        if (page == 1) {
                            productRequestResponse = result
                        } else {
                            val updatedItems = productRequestResponse?.items.orEmpty() + result.items
                            productRequestResponse = result.copy(items = updatedItems)
                        }

                        if (result.items.size < limit) {
                            isLastPage = true
                        } else {
                            currentPage++
                        }

                        setUpFragment()
                    }
                } else {
                    showError(ErrorUtils.parseErrorMessage(response.errorBody()?.string()))
                }
            }

            override fun onFailure(call: Call<ProductRequestResponse>, t: Throwable) {
                isLoading = false
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun renderProductRequests() {
        binding.requestsContainer.removeAllViews()

        val products = productRequestResponse?.items.orEmpty()

        if (products.isEmpty()) {
            binding.noRequestsText.visibility = View.VISIBLE
            binding.requestsContainer.visibility = View.GONE
        } else {
            binding.noRequestsText.visibility = View.GONE
            binding.requestsContainer.visibility = View.VISIBLE

            val inflater = LayoutInflater.from(requireContext())
            for (product in products) {
                val itemView = inflater.inflate(R.layout.request_item, binding.requestsContainer, false)

                itemView.findViewById<TextView>(R.id.textView).text = product.name
                itemView.findViewById<TextView>(R.id.caloriesNumber).text = "${product.calories} kcal"
                itemView.findViewById<TextView>(R.id.proteinNumber).text = "${product.protein} g"
                itemView.findViewById<TextView>(R.id.fatNumber).text = "${product.fat} g"
                itemView.findViewById<TextView>(R.id.carbsNumber).text = "${product.carbs} g"
                itemView.findViewById<TextView>(R.id.productType).text = "${product.productType}"

                itemView.findViewById<Button>(R.id.approveButton).setOnClickListener {
                    approveRequest(product._id)
                }
                itemView.findViewById<Button>(R.id.rejectButton).setOnClickListener {
                    rejectRequest(product._id)
                }

                binding.requestsContainer.addView(itemView)
            }
        }
    }

    private fun approveRequest(requestId: String) {

        val accessToken = encryptedPreferencesManager?.getAccessToken()
        val productRequestApi = RetrofitClient.getInstance(requireContext()).productRequestApi

        productRequestApi.approveRequest(accessToken, requestId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toasty.success(requireContext(), "Request approved successfully!", Toast.LENGTH_SHORT, true).show()
                    reloadRequests()
                } else {
                    showError(ErrorUtils.parseErrorMessage(response.errorBody()?.string()))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun rejectRequest(requestId: String) {

        val accessToken = encryptedPreferencesManager?.getAccessToken()
        val productRequestApi = RetrofitClient.getInstance(requireContext()).productRequestApi

        productRequestApi.rejectRequest(accessToken, requestId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toasty.success(requireContext(), "Request rejected successfully!", Toast.LENGTH_SHORT, true).show()
                    reloadRequests()
                } else {
                    showError(ErrorUtils.parseErrorMessage(response.errorBody()?.string()))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun reloadRequests() {
        currentPage = 1
        isLastPage = false
        productRequestResponse = null

        fetchProductRequests(
            query = if (currentQuery.isEmpty()) null else currentQuery,
            page = currentPage,
            limit = pageSize
        )
    }

    private fun showError(message: String?) {
        Toasty.error(requireContext(), message ?: "Unknown error", Toast.LENGTH_SHORT, true).show()
    }
}