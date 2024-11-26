package com.example.samid

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var registerBtn: TextView
    private lateinit var db: FirebaseFirestore

    @SuppressLint("CutPasteId", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Verificar si existe una sesión activa
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // Redirigir al Home si ya está logueado
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContentView(R.layout.activity_main)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        registerBtn = findViewById(R.id.RegisterBtn)

        // Botón de inicio de sesión
        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            // Validar si los campos están vacíos
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                validateUser(username, password)
            }
        }

        // Botón de registro
        registerBtn.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        // Ajustar padding para las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun validateUser(username: String, password: String) {
        // Realizar una consulta a Firestore
        db.collection("usuarios")
            .whereEqualTo("Email", username)
            .whereEqualTo("Contraseña", password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Usuario encontrado
                    val userDoc = documents.documents[0] // Asumimos que solo hay un documento por usuario

                    // Obtener todos los datos del usuario
                    val id = userDoc.id // Guardar el ID del documento
                    val name = userDoc.getString("Nombre") ?: "Usuario"
                    val surname = userDoc.getString("Apellido") ?: "Apellido"
                    val email = userDoc.getString("Email") ?: "Email"
                    val password = userDoc.getString("Contraseña") ?: "Contraseña"
                    val address = userDoc.getString("Direccion") ?: "Dirección"
                    val parentesco = userDoc.getString("Parentesco") ?: "N/A"

                    // Guardar sesión con todos los datos
                    saveSession(id, username, name, surname, email, password, address, parentesco)

                    // Mostrar mensaje personalizado
                    Toast.makeText(
                        this,
                        "Inicio de sesión exitoso. Bienvenido/a ",
                        Toast.LENGTH_LONG
                    ).show()

                    // Redirigir al HomeActivity
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Usuario no encontrado
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Error en la consulta
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Error al validar usuario: ${e.message}")
            }
    }

    private fun saveSession(id: String, username: String, name: String, surname: String, email: String, password: String, address: String, parentesco: String) {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("userId", id) // Guardar el ID
        editor.putString("username", username)
        editor.putString("name", name)
        editor.putString("surname", surname)
        editor.putString("email", email)
        editor.putString("password", password)
        editor.putString("address", address)
        editor.putString("parentesco", parentesco)
        editor.apply() // Guardar cambios
    }

    // Método para cerrar sesión y borrar los datos guardados
    fun logout() {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()  // Elimina todos los datos guardados
        editor.apply()
    }
}
