package com.example.samid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import org.json.JSONArray

class PatientsView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patients_view)

        val container = findViewById<LinearLayout>(R.id.cardContainer)
        val sharedPref = getSharedPreferences("PatientDataList", MODE_PRIVATE)
        val jsonString = sharedPref.getString("patients", "[]")
        val pacientes = JSONArray(jsonString)

        for (i in 0 until pacientes.length()) {
            val paciente = pacientes.getJSONObject(i)

            val cardView = layoutInflater.inflate(R.layout.card_patient_template, container, false)

            val nombre = "${paciente.getString("name")} ${paciente.getString("surname")}"
            val condicion = paciente.getString("condition")

            val textViewName = cardView.findViewById<TextView>(R.id.textViewName)
            val textViewCondition = cardView.findViewById<TextView>(R.id.textViewCondition)
            val buttonCheck = cardView.findViewById<Button>(R.id.buttonCheck)
            val buttonDevice = cardView.findViewById<Button>(R.id.buttonDevice)
            val buttonDelete = cardView.findViewById<ImageView>(R.id.buttonDelete)
            val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
            val navView = findViewById<NavigationView>(R.id.nav_view)
            val headerLayout = findViewById<View>(R.id.header)
            val rayasIcon = headerLayout.findViewById<ImageView>(R.id.rayas)

            rayasIcon.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }


            textViewName.text = "Nombre: $nombre"
            textViewCondition.text = "Condición: $condicion"

            buttonCheck.setOnClickListener {
                startActivity(Intent(this, CheckNow::class.java))
            }

            buttonDevice.setOnClickListener {
                startActivity(Intent(this, DeviceStatus::class.java))
            }

            buttonDelete.setOnClickListener {
                val updatedList = JSONArray()
                for (j in 0 until pacientes.length()) {
                    if (j != i) updatedList.put(pacientes.getJSONObject(j))
                }
                sharedPref.edit().putString("patients", updatedList.toString()).apply()
                container.removeView(cardView)
                Toast.makeText(this, "Paciente eliminado", Toast.LENGTH_SHORT).show()
            }

            container.addView(cardView)
        }

        // Botón para agregar nuevo paciente
        findViewById<ImageView>(R.id.addButton).setOnClickListener {
            startActivity(Intent(this, RegisterPatient::class.java))
        }

        // Configuración del menú lateral
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        val rayasIcon = findViewById<ImageView>(R.id.rayas)
        rayasIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.patients_view -> true
                R.id.check_now -> {
                    startActivity(Intent(this, CheckNow::class.java))
                    true
                }
                R.id.history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.device_status -> {
                    startActivity(Intent(this, DeviceStatus::class.java))
                    true
                }
                R.id.nav_logout -> {
                    val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
                    sharedPref.edit().clear().apply()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
