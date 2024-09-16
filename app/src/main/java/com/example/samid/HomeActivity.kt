package com.example.samid

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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
                    // Acción para el ítem "Home"
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

        // Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_home)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
