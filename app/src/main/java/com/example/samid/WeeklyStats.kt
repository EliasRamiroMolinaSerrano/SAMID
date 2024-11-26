package com.example.samid

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class WeeklyStats : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weekly_stats)

        // Inicializar DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigation_view)

        // Icono del menú (rayas/hamburguesa)
        val rayasIcon = findViewById<ImageView>(R.id.rayas)
        rayasIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)  // Abre el menú lateral al hacer clic
        }

        // Configuración del toggle para abrir/cerrar el menú lateral con el botón de "hamburguesa"
        val toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Manejo de los clics en los elementos del menú lateral
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.patients_view -> {
                    val intent = Intent(this, PatientsView::class.java)
                    startActivity(intent)
                    true
                }
                R.id.weekly_analysis -> {

                    true
                }
                R.id.check_now -> {
                    val intent = Intent(this, CheckNow::class.java)
                    startActivity(intent)
                    true
                }
                R.id.history -> {
                    val intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.device_status -> {
                    val intent = Intent(this, DeviceStatus::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_logout -> {
                    // Limpiar datos de sesión
                    val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.clear() // Elimina los datos de la sesión
                    editor.apply()

                    // Redirigir al usuario a MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    // Finalizar la actividad actual
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        // Cierra el menú lateral si está abierto, en lugar de salir de la actividad directamente
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}

