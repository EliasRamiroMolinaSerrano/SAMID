package com.example.samid

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

open class BaseActivity : AppCompatActivity() {

    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Setup del menú lateral
        setupDrawerMenu()
    }

    private fun setupDrawerMenu() {
        // Enlazar DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        // Configuración del toggle para abrir/cerrar el menú lateral
        val toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Manejo de los clics en los elementos del menú lateral
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Acción para "Home"
                    true
                }
                R.id.weekly_analysis -> {
                    // Acción para "Weekly Analysis"
                    true
                }
                R.id.check_now -> {
                    // Acción para "Check Now"
                    true
                }
                R.id.history -> {
                    // Acción para "History"
                    true
                }
                R.id.device_status -> {
                    // Acción para "Device Status"
                    true
                }
                R.id.configuration -> {
                    // Acción para "Configuration"
                    true
                }
                R.id.nav_logout -> {
                    // Acción para "Logout"
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        // Cierra el menú lateral si está abierto
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Método opcional para abrir el menú desde otras actividades
    protected fun openMenu() {
        drawerLayout.openDrawer(GravityCompat.START)
    }
}
