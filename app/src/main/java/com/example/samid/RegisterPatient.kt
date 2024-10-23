package com.example.samid

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterPatient : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_patient)

        // Configurar la vista para manejar los márgenes de las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configuración del Spinner (ComboBox)
        val spinner = findViewById<Spinner>(R.id.spinnerCondition)

        // Lista de opciones
        val options = arrayOf("Alzheimer", "Parkinson")

        // Adaptador para el Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asignar el adaptador al Spinner
        spinner.adapter = adapter

        // Set up the back button
        val backButton = findViewById<ImageView>(R.id.flecha) // replace with the actual ID of your back button
        backButton.setOnClickListener {
            finish() // This will finish the current activity and go back to the previous one
        }



    }
}
