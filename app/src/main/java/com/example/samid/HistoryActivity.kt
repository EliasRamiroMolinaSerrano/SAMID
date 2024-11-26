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

class HistoryActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Inicializar DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

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
                    val intent = Intent(this, WeeklyStats::class.java)
                    startActivity(intent)
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

        // Configurar clics en las CardViews
        val card1 = findViewById<CardView>(R.id.card)
        card1.setOnClickListener {
            // Navegar a la actividad deseada al hacer clic en el primer CardView
            val intent = Intent(this, HistoryActivityCalls::class.java) // Reemplaza DesiredActivity1 con tu actividad real
            startActivity(intent)
        }

        val card2 = findViewById<CardView>(R.id.card2)
        card2.setOnClickListener {
            val intent = Intent(this, HistoryActivityResults::class.java) // Reemplaza DesiredActivity2 con tu actividad real
            startActivity(intent)
        }

        val card3 = findViewById<CardView>(R.id.card3)
        card3.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java) // Reemplaza DesiredActivity3 con tu actividad real
            startActivity(intent)
        }

        val card4 = findViewById<CardView>(R.id.card4)
        card4.setOnClickListener {
            val intent = Intent(this, HistoryActivityFalls::class.java) // Reemplaza DesiredActivity4 con tu actividad real
            startActivity(intent)
        }
    }}
