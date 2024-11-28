package com.example.samid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var analysisCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Obtener la referencia al CardView de "Análisis"
        analysisCard = findViewById(R.id.analisis)

        // Configurar el evento click del CardView
        analysisCard.setOnClickListener {
            // Obtener el nombre desde SharedPreferences
            val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val name = sharedPref.getString("name", "Usuario") // Valor por defecto "Usuario" si no se encuentra el nombre

            // Mostrar un mensaje con el nombre
            Toast.makeText(this, "Bienvenido/a, $name", Toast.LENGTH_SHORT).show()
        }

        // Configura el listener para el CardView
        findViewById<CardView>(R.id.analisis).setOnClickListener {
            val intent = Intent(this, WeeklyStats::class.java) // Cambia a la actividad CheckNow
            startActivity(intent) // Inicia la actividad
        }

        // Configura el listener para el CardView
        findViewById<CardView>(R.id.devices).setOnClickListener {
            val intent = Intent(this, DeviceStatus::class.java) // Cambia a la actividad CheckNow
            startActivity(intent) // Inicia la actividad
        }

        // Inicializar DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        // Obtener los datos guardados de la sesión (nombre y parentesco)
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val name = sharedPref.getString("name", "Usuario")
        val parentesco = sharedPref.getString("parentesco", "N/A")

        // Actualizar el NavHeader con los datos obtenidos
        val headerView = navView.getHeaderView(0) // Obtener la vista del header
        val usernameTextView = headerView.findViewById<TextView>(R.id.nav_header_username)
        val parentescoTextView = headerView.findViewById<TextView>(R.id.nav_header_parentesco)

        // Actualizar las vistas del header
        usernameTextView.text = name ?: "Usuario"
        parentescoTextView.text = parentesco ?: "N/A"

        // Icono del menú (rayas/hamburguesa)
        val rayasIcon = findViewById<ImageView>(R.id.rayas)
        rayasIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)  // Abre el menú lateral al hacer clic
        }

        // Obtener la referencia al TextView con el ID "nombre"
        val nombreTextView = findViewById<TextView>(R.id.nombre)



        // Asignar el nombre recuperado al TextView
        nombreTextView.text = name


        // Configuración del toggle para abrir/cerrar el menú lateral con el botón de "hamburguesa"
        val toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Manejo de los clics en los elementos del menú lateral
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> true
                R.id.patients_view -> {
                    startActivity(Intent(this, PatientsView::class.java))
                    true
                }
                R.id.weekly_analysis -> {
                    startActivity(Intent(this, WeeklyStats::class.java))
                    true
                }
                R.id.check_now -> {
                    startActivity(Intent(this, CheckNow::class.java))
                    true
                }
                R.id.history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.device_status -> {
                    startActivity(Intent(this, DeviceStatus::class.java))
                    true
                }
                R.id.nav_logout -> {
                    // Limpiar los datos de sesión
                    val editor = sharedPref.edit()
                    editor.clear() // Elimina los datos de la sesión
                    editor.apply()

                    // Redirigir a MainActivity
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

        // Button to trigger the custom dialog
        val pacientBtn = findViewById<Button>(R.id.pacientBtn)

        pacientBtn.setOnClickListener {
            // Iniciar la nueva actividad
            val intent = Intent(this, PatientsView::class.java)
            startActivity(intent)
        }

        // Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_home)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
