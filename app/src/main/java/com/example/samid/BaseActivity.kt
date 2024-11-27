package com.example.samid

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

open class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // Setup toolbar, DrawerLayout, and NavigationView
    protected fun setupMenu(drawerLayout: DrawerLayout, toolbar: Toolbar, navView: NavigationView) {
        this.drawerLayout = drawerLayout
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_nav, R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
    }

    protected fun updateNavHeader() {
        val navView: NavigationView = findViewById(R.id.nav_view)
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val name = sharedPref.getString("name", "Usuario")
        val parentesco = sharedPref.getString("parentesco", "N/A")

        val headerView = navView.getHeaderView(0)
        val usernameTextView = headerView.findViewById<TextView>(R.id.nav_header_username)
        val parentescoTextView = headerView.findViewById<TextView>(R.id.nav_header_parentesco)

        usernameTextView.text = name ?: "Usuario"
        parentescoTextView.text = parentesco ?: "N/A"
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            R.id.weekly_analysis -> {}
            R.id.check_now -> {}
            R.id.history -> {}
            R.id.device_status -> {}
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
