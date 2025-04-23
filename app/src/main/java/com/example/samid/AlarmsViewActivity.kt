package com.example.samid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import org.json.JSONArray
import org.json.JSONObject

class AlarmsViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarms_view)

        val container = findViewById<LinearLayout>(R.id.alarmListContainer)
        val addButton = findViewById<ImageView>(R.id.addButton)

        val alarmas = cargarAlarmas(this)

        for (i in 0 until alarmas.length()) {
            val alarma = alarmas.getJSONObject(i)
            if (alarma.getBoolean("activa")) {
                container.addView(crearCardAlarma(alarma))
            }
        }

        addButton.setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        }
    }

    private fun crearCardAlarma(alarma: JSONObject): CardView {
        val card = CardView(this).apply {
            radius = 16f
            cardElevation = 8f
            setContentPadding(32, 24, 32, 24)
            useCompatPadding = true
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 32)
            layoutParams = params
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val nombre = TextView(this).apply {
            text = "👤 Paciente: ${alarma.getString("nombre")}"
            textSize = 18f
            setPadding(0, 0, 0, 8)
        }

        val medicamento = TextView(this).apply {
            text = "💊 Medicamento: ${alarma.getString("medicamento")}"
        }

        val cantidad = TextView(this).apply {
            text = "📦 Cantidad: ${alarma.getString("cantidad")}"
        }

        val fecha = TextView(this).apply {
            text = "📅 Fecha: ${alarma.getString("fecha")}"
        }

        val hora = TextView(this).apply {
            text = "⏰ Hora: ${alarma.getString("hora")}"
        }

        layout.apply {
            addView(nombre)
            addView(medicamento)
            addView(cantidad)
            addView(fecha)
            addView(hora)
        }

        card.addView(layout)
        return card
    }

    private fun cargarAlarmas(context: Context): JSONArray {
        val sharedPref = context.getSharedPreferences("alarmas_guardadas", MODE_PRIVATE)
        val json = sharedPref.getString("alarmas", "[]")
        return JSONArray(json)
    }
}
