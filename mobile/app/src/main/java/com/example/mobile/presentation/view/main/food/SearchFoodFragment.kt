package com.example.mobile.presentation.view.main.food

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mobile.R
import com.example.mobile.data.api.NutritionProductApi
import com.example.mobile.data.api.RetrofitClient
import com.example.mobile.data.dto.nutritionProduct.ProductSearchResponse
import com.example.mobile.domain.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFoodFragment : Fragment() {

    private lateinit var productsContainer: LinearLayout
    private lateinit var nutritionProductApi: NutritionProductApi
    private var selectedFilter: String? = null
    private var sortBy: String? = null
    private var sortOrder: String? = null
    private var currentQuery: String = ""
    private var currentPage = 1
    private val pageSize = 10
    private var isLoading = false
    private var isLastPage = false
    private var searchRunnable: Runnable? = null
    private val searchHandler = Handler()

    private val backgroundDrawables = listOf(
        R.drawable.shape_yellow_card,
        R.drawable.shape_green_card,
        R.drawable.shape_pink_card,
        R.drawable.shape_blue_card
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_food, container, false)
        productsContainer = view.findViewById(R.id.productsContainer)
        nutritionProductApi = RetrofitClient.getInstance(requireContext()).nutritionProductApi

        loadAllProducts(inflater)
        setupSearchAndFilters(view)

        return view
    }

    private fun loadAllProducts(inflater: LayoutInflater) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val products = withContext(Dispatchers.IO) {
                    nutritionProductApi.getAllNutritionProducts()
                }
                renderProducts(products.map {
                    Product(
                        _id = it._id,
                        name = it.name,
                        calories = it.calories,
                        protein = it.protein,
                        fat = it.fat,
                        carbs = it.carbs,
                        productType = it.productType
                    )
                })
            } catch (e: Exception) {
                Log.e("SearchFoodFragment", "Failed to fetch all products", e)
            }
        }
    }

    private fun setupSearchAndFilters(view: View) {
        val searchInput = view.findViewById<View>(R.id.searchFieldContainer)
            .findViewById<EditText>(R.id.searchInput)

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchRunnable = Runnable {
                    val query = s.toString().trim()
                    currentQuery = query
                    currentPage = 1
                    isLastPage = false
                    searchAndFilterProducts(query)
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
            }
        })

        val filterBtn =
            view.findViewById<View>(R.id.searchFieldContainer).findViewById<View>(R.id.filter)
        val sortBtn =
            view.findViewById<View>(R.id.searchFieldContainer).findViewById<View>(R.id.sort)

        filterBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putString("selected_filter", selectedFilter)
            }
            findNavController().navigate(R.id.navigation_filter, bundle)
        }

        sortBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putString("selected_sort_by", sortBy)
                putString("selected_sort_order", sortOrder)
            }
            findNavController().navigate(R.id.navigation_sort, bundle)
        }

        val navBackStackEntry = findNavController().currentBackStackEntry
        navBackStackEntry?.savedStateHandle?.getLiveData<String?>("selected_filter")
            ?.observe(viewLifecycleOwner) { selectedValue ->
                selectedFilter = selectedValue
                resetAndSearch()
            }

        navBackStackEntry?.savedStateHandle?.getLiveData<String?>("selected_sort_by")
            ?.observe(viewLifecycleOwner) { value ->
                sortBy = value
                resetAndSearch()
            }

        navBackStackEntry?.savedStateHandle?.getLiveData<String?>("selected_sort_order")
            ?.observe(viewLifecycleOwner) { value ->
                sortOrder = value
                resetAndSearch()
            }
    }

    private fun resetAndSearch() {
        currentPage = 1
        isLastPage = false
        searchAndFilterProducts(currentQuery.ifBlank { null })
    }

    private fun searchAndFilterProducts(query: String? = null, page: Int = 1) {
        if (isLoading || isLastPage) return
        isLoading = true

        nutritionProductApi.searchNutritionProducts(
            query = query,
            page = page,
            limit = pageSize,
            sortBy = sortBy,
            sortOrder = sortOrder,
            productType = selectedFilter
        ).enqueue(object : Callback<ProductSearchResponse> {
            override fun onResponse(
                call: Call<ProductSearchResponse>,
                response: Response<ProductSearchResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    val data = response.body()
                    data?.let {
                        if (page == 1) {
                            productsContainer.removeAllViews()
                        }
                        renderProducts(it.items)
                        if (it.items.size < pageSize) isLastPage = true else currentPage++
                    }
                }
            }

            override fun onFailure(call: Call<ProductSearchResponse>, t: Throwable) {
                isLoading = false
                Log.e("SearchFoodFragment", "Search failed", t)
            }
        })
    }

    private fun renderProducts(products: List<Product>) {
        val inflater = layoutInflater
        products.forEachIndexed { index, product ->
            val productView =
                inflater.inflate(R.layout.item_nutrition_product, productsContainer, false)
            val productName: TextView = productView.findViewById(R.id.productName)
            val addProductIcon: ImageView = productView.findViewById(R.id.addProductIcon)
            val container: View = productView.findViewById(R.id.productContainer)

            productName.text = product.name
            container.setBackgroundResource(backgroundDrawables[index % backgroundDrawables.size])

            container.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("productId", product._id)
                    putString("productName", product.name)
                }
                findNavController().navigate(R.id.navigation_addFoodToMeal, bundle)
            }

            productsContainer.addView(productView)
        }
    }
}
