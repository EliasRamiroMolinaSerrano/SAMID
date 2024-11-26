package com.example.samid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
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
                    // Navegar a la actividad DeviceStatus
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

        // Button to trigger the custom dialog
        val pacientBtn = findViewById<Button>(R.id.pacientBtn)

            // Set an onClickListener to show the custom dialog when pacientBtn is clicked
        pacientBtn.setOnClickListener {

            // Inflate the custom dialog layout
            val customDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)

            // Create the AlertDialog
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(customDialogView) // Set the custom dialog view
                .create()

            // Set transparent background for the dialog
            dialogBuilder.window?.setBackgroundDrawableResource(android.R.color.transparent)

            // Show the dialog
            dialogBuilder.show()

            // Find the OK button in the custom dialog and set its onClickListener
            val okBtn = customDialogView.findViewById<Button>(R.id.ok_btn)
            okBtn.setOnClickListener {
                dialogBuilder.dismiss() // Close the dialog when OK is clicked
            }

        }

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