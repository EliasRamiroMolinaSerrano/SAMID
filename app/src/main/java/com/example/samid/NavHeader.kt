package com.example.samid

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NavHeader : AppCompatActivity() {

    private lateinit var usernameTextView: TextView
    private lateinit var descriptionTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_header)  // Asegúrate de usar el layout adecuado

        // Inicializar los TextViews
        usernameTextView = findViewById(R.id.nav_header_username)
        descriptionTextView = findViewById(R.id.nav_header_parentesco)

        // Recibir los datos del Intent
        val userName = intent.getStringExtra("userName") ?: "Usuario"
        val userParentesco = intent.getStringExtra("userParentesco") ?: "N/A"

        // Asignar los valores a los TextViews
        usernameTextView.text = userName
        descriptionTextView.text = userParentesco
    }
}
