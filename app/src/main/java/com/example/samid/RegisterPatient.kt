package com.example.samid

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class RegisterPatient : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var deviceIdEditText: EditText
    private lateinit var conditionEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_patient)

        // Inicializar vistas
        nameEditText = findViewById(R.id.textView1)
        surnameEditText = findViewById(R.id.textView2)
        addressEditText = findViewById(R.id.textView3)
        ageEditText = findViewById(R.id.textView4)
        conditionEditText = findViewById(R.id.textViewCondition)
        deviceIdEditText = findViewById(R.id.textView5)
        registerButton = findViewById(R.id.RegisterBtn)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val surname = surnameEditText.text.toString()
            val address = addressEditText.text.toString()
            val age = ageEditText.text.toString()
            val condition = conditionEditText.text.toString()
            val deviceId = deviceIdEditText.text.toString()

            if (name.isEmpty() || surname.isEmpty() || address.isEmpty() || age.isEmpty() || deviceId.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            } else {
                val sharedPref = getSharedPreferences("PatientDataList", MODE_PRIVATE)
                val jsonString = sharedPref.getString("patients", "[]")
                val pacientes = JSONArray(jsonString)

                val nuevo = JSONObject().apply {
                    put("name", name)
                    put("surname", surname)
                    put("address", address)
                    put("age", age)
                    put("condition", condition)
                    put("deviceId", deviceId)
                }

                pacientes.put(nuevo)
                sharedPref.edit().putString("patients", pacientes.toString()).apply()

                startActivity(Intent(this, PatientsView::class.java))
                finish()
            }
        }

        // Regresar
        findViewById<ImageView>(R.id.flecha).setOnClickListener {
            finish()
        }
    }
}
