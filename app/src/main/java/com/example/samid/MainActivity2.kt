package com.example.samid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject

class MainActivity2 : AppCompatActivity() {
    private lateinit var webSocket: WebSocket
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Start WebSocket connection
        webSocket = startWebSocket() // Initialize and assign the WebSocket

        // Ajustar padding por los insets del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener referencias a los campos de entrada
        val nameInput = findViewById<EditText>(R.id.name_input)
        val lastNameInput = findViewById<EditText>(R.id.last_name_input)
        val emailInput = findViewById<EditText>(R.id.email_register_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val addressInput = findViewById<EditText>(R.id.address_input)
        val relationshipInput = findViewById<EditText>(R.id.relationship_input)

        val registerButton = findViewById<Button>(R.id.register_btn)

        registerButton.setOnClickListener {
            val name = nameInput.text.toString()
            val lastName = lastNameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val address = addressInput.text.toString()
            val relationship = relationshipInput.text.toString()

            // Validar los datos de entrada
            if (name.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || relationship.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Enviar los datos a Firestore
                registerUser(name, lastName, email, password, address, relationship)
            }
        }
    }

    private fun registerUser(name: String, lastName: String, email: String, password: String, address: String, relationship: String) {
        val user = hashMapOf(
            "Nombre" to name,
            "Apellido" to lastName,
            "Email" to email,
            "Contraseña" to password,
            "Direccion" to address,
            "Parentesco" to relationship
        )

        db.collection("usuarios").add(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                // Navegar a HomeActivity
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error en el registro: ${e.message}", Toast.LENGTH_SHORT).show()
            }


        // Encontrar el botón de registro
        val registerBtn: Button = findViewById(R.id.register_btn)

        // Agregar el listener para manejar el clic en el botón
        registerBtn.setOnClickListener {
            // Crear un Intent para iniciar la HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }


    private fun startWebSocket(): WebSocket {
        val client = OkHttpClient()
        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.d("WebSocket", "Connected to server")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Log.d("WebSocket", "Message received: $text")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.e("WebSocket", "Error: ${t.message}")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Log.d("WebSocket", "Closed: $code $reason")
            }
        }

        // Return the initialized WebSocket
        return WebSocketManager.connect(client, "${Constants.WSSERVER_URL}", webSocketListener)
    }


}