package com.example.samid

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterPatient : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var conditionSpinner: Spinner
    private lateinit var deviceIdEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_patient)

        // Inicializar vistas
        nameEditText = findViewById(R.id.textView1)
        surnameEditText = findViewById(R.id.textView2)
        addressEditText = findViewById(R.id.textView3)
        ageEditText = findViewById(R.id.textView4)
        conditionSpinner = findViewById(R.id.spinnerCondition)
        deviceIdEditText = findViewById(R.id.textView5)
        registerButton = findViewById(R.id.RegisterBtn)

        // Configurar Spinner
        val options = arrayOf(
            "Alzheimer", "Parkinson", "Diabetes", "Hipertensión",
            "Cáncer de pulmón", "Cáncer de mama", "Cáncer colorectal",
            "Cáncer de próstata", "Leucemia", "Linfoma"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        conditionSpinner.adapter = adapter

        // Botón de registro
        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val surname = surnameEditText.text.toString()
            val address = addressEditText.text.toString()
            val age = ageEditText.text.toString()
            val condition = conditionSpinner.selectedItem.toString()
            val deviceId = deviceIdEditText.text.toString()

            if (name.isEmpty() || surname.isEmpty() || address.isEmpty() || age.isEmpty() || deviceId.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            } else {
                // Enviar datos a PatientsView
                val intent = Intent(this, PatientsView::class.java).apply {
                    putExtra("name", name)
                    putExtra("surname", surname)
                    putExtra("address", address)
                    putExtra("age", age)
                    putExtra("condition", condition)
                    putExtra("deviceId", deviceId)
                }
                startActivity(intent)
                finish() // Cierra la actividad actual
            }
        }

        // Botón de regreso
        findViewById<ImageView>(R.id.flecha).setOnClickListener {
            finish() // Regresa a la pantalla anterior
        }
    }
}
