package com.example.mobile.presentation.view.main.sport

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mobile.R
import com.example.mobile.data.api.ActivityApi
import com.example.mobile.data.api.RetrofitClient
import com.example.mobile.data.dto.activity.ActivitySearchResponse
import com.example.mobile.domain.model.Activity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchSportFragment : Fragment() {

    private lateinit var activitiesContainer: LinearLayout
    private lateinit var activityApi: ActivityApi
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
        val view = inflater.inflate(R.layout.fragment_search_sport, container, false)
        activitiesContainer = view.findViewById(R.id.productsContainer)
        activityApi = RetrofitClient.getInstance(requireContext()).activityApi

        loadAllActivities(inflater)
        setupSearchAndFilters(view)

        return view
    }

    private fun loadAllActivities(inflater: LayoutInflater) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val activities = withContext(Dispatchers.IO) {
                    activityApi.getAllActivities()
                }
                renderActivities(activities)
            } catch (e: Exception) {
                Log.e("SearchSportFragment", "Failed to fetch all activities", e)
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
                    searchActivities(query)
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
            }
        })

        val sortBtn =
            view.findViewById<View>(R.id.searchFieldContainer).findViewById<View>(R.id.sort)

        sortBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putString("selected_sort_by", sortBy)
                putString("selected_sort_order", sortOrder)
                putString("source", "sport")
            }
            findNavController().navigate(R.id.navigation_sort, bundle)
        }

        val filterBtn =
            view.findViewById<View>(R.id.searchFieldContainer).findViewById<View>(R.id.filter)
        filterBtn.visibility = View.GONE


        val navBackStackEntry = findNavController().currentBackStackEntry
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
        searchActivities(currentQuery.ifBlank { null })
    }

    private fun searchActivities(query: String? = null, page: Int = 1) {
        if (isLoading || isLastPage) return
        isLoading = true

        activityApi.searchActivities(
            query = query,
            page = page,
            limit = pageSize,
            sortBy = sortBy,
            sortOrder = sortOrder
        ).enqueue(object : Callback<ActivitySearchResponse> {
            override fun onResponse(
                call: Call<ActivitySearchResponse>,
                response: Response<ActivitySearchResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    val data = response.body()
                    data?.let {
                        if (page == 1) {
                            activitiesContainer.removeAllViews()
                        }
                        renderActivities(it.items)
                        if (it.items.size < pageSize) isLastPage = true else currentPage++
                    }
                }
            }

            override fun onFailure(call: Call<ActivitySearchResponse>, t: Throwable) {
                isLoading = false
                Log.e("SearchSportFragment", "Search failed", t)
            }
        })
    }

    private fun renderActivities(activities: List<Activity>) {
        val inflater = layoutInflater
        activities.forEachIndexed { index, activity ->
            val view = inflater.inflate(R.layout.item_activity, activitiesContainer, false)
            val nameText: TextView = view.findViewById(R.id.activityName)
            val container: View = view.findViewById(R.id.activityContainer)

            nameText.text = activity.name
            container.setBackgroundResource(backgroundDrawables[index % backgroundDrawables.size])

            container.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("activityId", activity._id)
                    putString("activityName", activity.name)
                    putDouble("caloriesPerMin", activity.caloriesPerMin)
                }
                findNavController().navigate(R.id.navigation_addSport, bundle)
            }

            activitiesContainer.addView(view)
        }
    }
}
