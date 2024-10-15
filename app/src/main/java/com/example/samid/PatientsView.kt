package com.example.samid

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class PatientsView : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patients_view)

        // Inicializar DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        // Icono del menú (rayas/hamburguesa)
        val rayasIcon = findViewById<ImageView>(R.id.rayas)
        rayasIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)  // Abre el menú lateral al hacer clic
        }

        // Configuración del toggle para abrir/cerrar el menú lateral con el botón de "hamburguesa"
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
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

                    true
                }
                R.id.weekly_analysis -> {
                    val intent = Intent(this, WeeklyStats::class.java)
                    startActivity(intent)
                    true
                }
                R.id.check_now -> {
                    val intent = Intent(this, CheckNow::class.java) // Navegar a CheckNow
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
                    // Acción para "Logout"
                    true
                }
                else -> false
            }
        }

        // Configura el listener para el CardView
        findViewById<CardView>(R.id.cardPatients).setOnClickListener {
            val intent = Intent(this, CheckNow::class.java) // Cambia a la actividad CheckNow
            startActivity(intent) // Inicia la actividad
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        // Cierra el menú lateral si está abierto, en lugar de salir de la actividad directamente
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
