package com.example.mobile.presentation.view.main

import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.mobile.R
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navItems: List<Triple<FrameLayout, View, ImageView>>
    private var encryptedPreferencesManager: EncryptedPreferencesManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        encryptedPreferencesManager = EncryptedPreferencesManager(this)

        val navController = findNavController(R.id.nav_host_fragment)

        navItems = listOf(
            Triple(
                findViewById(R.id.nav_home),
                findViewById(R.id.nav_home_bg),
                findViewById(R.id.nav_home_icon)
            ),
            Triple(
                findViewById(R.id.nav_food),
                findViewById(R.id.nav_food_bg),
                findViewById(R.id.nav_food_icon)
            ),
            Triple(
                findViewById(R.id.nav_activity), findViewById(R.id.nav_activity_bg), findViewById(
                    R.id.nav_activity_icon
                )
            ),
            Triple(
                findViewById(R.id.nav_profile),
                findViewById(R.id.nav_profile_bg),
                findViewById(R.id.nav_profile_icon)
            ),
            Triple(
                findViewById(R.id.nav_admin),
                findViewById(R.id.nav_admin_bg),
                findViewById(R.id.nav_admin_icon)
            )
        )

        val role = encryptedPreferencesManager?.getRoleFromAccessToken()

        if (role != "ADMIN" && role != "MODERATOR") {
            navItems[4].first.visibility = View.GONE
        } else {
            navItems[4].first.visibility = View.VISIBLE
        }

        navItems[0].first.setOnClickListener {
            navController.navigate(R.id.navigation_home)
            updateSelection(0)
        }
        navItems[1].first.setOnClickListener {
            navController.navigate(R.id.navigation_food)
            updateSelection(1)
        }
        navItems[2].first.setOnClickListener {
            navController.navigate(R.id.navigation_admin)
            updateSelection(2)
        }
        navItems[3].first.setOnClickListener {
            navController.navigate(R.id.navigation_profile)
            updateSelection(3)
        }
        navItems[4].first.setOnClickListener {
            navController.navigate(R.id.navigation_admin)
            updateSelection(4)
        }

        updateSelection(0)
    }

    private fun updateSelection(selectedIndex: Int) {
        navItems.forEachIndexed { index, triple ->
            val (container, bgView, iconView) = triple

            if (index == selectedIndex) {
                bgView.visibility = View.VISIBLE
                bgView.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(200)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
                iconView.isSelected = true
            } else {
                bgView.animate()
                    .scaleX(0.1f).scaleY(0.1f)
                    .setDuration(150)
                    .withEndAction { bgView.visibility = View.INVISIBLE }
                    .start()
                iconView.isSelected = false
            }
        }
    }
}
