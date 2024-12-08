package com.example.samid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class PatientsView : BaseActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patients_view)

// Recibir datos del Intent (solo la primera vez)
        val name = intent.getStringExtra("name")
        val surname = intent.getStringExtra("surname")
        val address = intent.getStringExtra("address")
        val age = intent.getStringExtra("age")
        val condition = intent.getStringExtra("condition")
        val deviceId = intent.getStringExtra("deviceId")

        // Si los datos existen, guárdalos en SharedPreferences
        if (name != null && surname != null && address != null && age != null && condition != null && deviceId != null) {
            val sharedPref = getSharedPreferences("PatientData", MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("name", name)
            editor.putString("surname", surname)
            editor.putString("address", address)
            editor.putString("age", age)
            editor.putString("condition", condition)
            editor.putString("deviceId", deviceId)
            editor.apply()  // Guardamos los datos
        }

        // Recuperar los datos desde SharedPreferences al iniciar la actividad
        val sharedPref = getSharedPreferences("PatientData", MODE_PRIVATE)
        val storedName = sharedPref.getString("name", "No disponible")
        val storedCondition = sharedPref.getString("condition", "No disponible")

        // Mostrar los datos en el CardView
        findViewById<TextView>(R.id.textViewName).text = "Nombre: $storedName"
        findViewById<TextView>(R.id.textViewCondition).text = "Condición: $storedCondition"

        // Llamamos al método para actualizar el NavHeader
        updateNavHeader()

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
                /*R.id.weekly_analysis -> {
                    val intent = Intent(this, WeeklyStats::class.java)
                    startActivity(intent)
                    true
                }*/
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


        // Configura el listener para el CardView
        findViewById<Button>(R.id.buttonCheck).setOnClickListener {
            val intent = Intent(this, CheckNow::class.java) // Cambia a la actividad CheckNow
            startActivity(intent) // Inicia la actividad
        }

        // Configura el listener para el CardView
        findViewById<Button>(R.id.buttonDevice).setOnClickListener {
            val intent = Intent(this, DeviceStatus::class.java) // Cambia a la actividad CheckNow
            startActivity(intent) // Inicia la actividad
        }

        // Configura el listener para el botón addButton
        findViewById<ImageView>(R.id.addButton).setOnClickListener {
            val intent = Intent(this, RegisterPatient::class.java) // Cambia a la actividad RegisterPatient
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
