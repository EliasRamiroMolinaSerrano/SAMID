package com.example.samid

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class RegisterPatient : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var conditionSpinner: Spinner
    private lateinit var deviceIdEditText: EditText
    private lateinit var registerButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_patient)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Configuración de las vistas
        nameEditText = findViewById(R.id.textView1)
        surnameEditText = findViewById(R.id.textView2)
        addressEditText = findViewById(R.id.textView3)
        ageEditText = findViewById(R.id.textView4)
        conditionSpinner = findViewById(R.id.spinnerCondition)
        deviceIdEditText = findViewById(R.id.textView5)
        registerButton = findViewById(R.id.RegisterBtn)

        // Configurar el Spinner con las opciones
        val options = arrayOf(
            "Alzheimer",
            "Parkinson",
            "Diabetes",
            "Hipertensión",
            "Cáncer de pulmón",
            "Cáncer de mama",
            "Cáncer colorectal",
            "Cáncer de próstata",
            "Leucemia",
            "Linfoma"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        conditionSpinner.adapter = adapter

        // Configurar la vista para manejar los márgenes de las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar el botón de registro
        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val surname = surnameEditText.text.toString()
            val address = addressEditText.text.toString()
            val age = ageEditText.text.toString()
            val condition = conditionSpinner.selectedItem.toString()
            val deviceId = deviceIdEditText.text.toString()

            // Validar que todos los campos no estén vacíos
            if (name.isEmpty() || surname.isEmpty() || address.isEmpty() || age.isEmpty() || deviceId.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            } else {
                // Obtener el ID del usuario desde las preferencias compartidas
                val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                val userId = sharedPref.getString("userId", "") ?: ""

                // Crear un mapa con los datos del paciente
                val patientData = hashMapOf(
                    "Nombre" to name,
                    "Apellido" to surname,
                    "Direccion" to address,
                    "Edad" to age,
                    "Condicion" to condition,
                    "IDDispositivo" to deviceId,
                    "UID" to userId  // Agregar el ID de usuario de la sesión actual
                )

                // Guardar los datos en la colección "Pacientes"
                db.collection("Pacientes")
                    .add(patientData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Paciente registrado exitosamente.", Toast.LENGTH_SHORT).show()
                        finish() // Volver a la pantalla anterior
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al registrar el paciente: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // Configurar el botón de regresar
        val backButton = findViewById<ImageView>(R.id.flecha) // Asegúrate de tener un botón de regreso
        backButton.setOnClickListener {
            finish() // Volver a la pantalla anterior
        }
    }
}
