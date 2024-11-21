package com.example.samid

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NavHeader : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // Referencias a los TextViews del layout
    private lateinit var usernameTextView: TextView
    private lateinit var relationshipTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_header) // Asegúrate de que la ruta al archivo XML sea correcta

        // Inicializa Firestore y FirebaseAuth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Encuentra las vistas en el layout
        usernameTextView = findViewById(R.id.username)
        relationshipTextView = findViewById(R.id.description)

        // Cargar los datos de usuario desde Firestore
        loadUserData()
    }

    private fun loadUserData() {
        // Obtén el ID del usuario autenticado
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("NavHeader", "No user is authenticated.")
            usernameTextView.text = "Usuario no autenticado"
            relationshipTextView.text = "Error en la carga de datos"
            return
        }

        // Consulta la base de datos para obtener el documento del usuario
        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Extrae los datos de la base de datos
                    val name = document.getString("Nombre") ?: "Nombre no disponible"
                    val relationship = document.getString("Parentesco") ?: "Parentesco no disponible"

                    // Actualiza los TextViews con los datos
                    usernameTextView.text = name
                    relationshipTextView.text = relationship
                } else {
                    Log.e("NavHeader", "No such document!")
                    usernameTextView.text = "Documento no encontrado"
                    relationshipTextView.text = "Parentesco no disponible"
                }
            }
            .addOnFailureListener { exception ->
                // Maneja el error si ocurre
                Log.e("NavHeader", "Error getting document: ${exception.message}")
                usernameTextView.text = "Error al cargar el usuario"
                relationshipTextView.text = "Error al cargar el parentesco"
            }
    }
}
