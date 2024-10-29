package com.example.samid

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivityResults : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bpmTextView: TextView
    private lateinit var spo2TextView: TextView
    private lateinit var pulseValueTextView: TextView // Para BPM en la segunda CardView
    private lateinit var spo2ValueTextView: TextView // Para SpO2 en la segunda CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_results)

        // Inicializa SharedPreferences
        sharedPreferences = getSharedPreferences("HealthData", MODE_PRIVATE)

        // Inicializa los componentes de UI
        bpmTextView = findViewById(R.id.bpmTextView)
        spo2TextView = findViewById(R.id.spo2TextView)
        pulseValueTextView = findViewById(R.id.pulse_value) // Para la segunda CardView
        spo2ValueTextView = findViewById(R.id.spo2_value) // Para la segunda CardView

        // Recupera y muestra los datos guardados
        val lastBpm = sharedPreferences.getInt("last_bpm", 0)
        val lastSpo2 = sharedPreferences.getInt("last_spo2", 0)

        // Asigna los valores a las TextViews correspondientes
        bpmTextView.text = if (lastBpm != 0) lastBpm.toString() else "No Data"
        spo2TextView.text = if (lastSpo2 != 0) lastSpo2.toString() else "No Data"

        // Asigna los valores a las TextViews en la segunda CardView
        pulseValueTextView.text = if (lastBpm != 0) lastBpm.toString() else "No Data"
        spo2ValueTextView.text = if (lastSpo2 != 0) lastSpo2.toString() else "No Data"

        // Configura el botón de retroceso
        val backButton = findViewById<ImageView>(R.id.flecha)
        backButton.setOnClickListener {
            finish()
        }
    }
}

