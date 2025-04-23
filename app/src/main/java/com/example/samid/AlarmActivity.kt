package com.example.samid

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class AlarmActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etMedicamento: EditText
    private lateinit var etCantidad: EditText
    private lateinit var tvFecha: TextView
    private lateinit var tvHora: TextView
    private lateinit var btnFecha: Button
    private lateinit var btnHora: Button
    private lateinit var btnGuardar: Button

    private var isEditing = false
    private var editIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        etNombre = findViewById(R.id.etNombre)
        etMedicamento = findViewById(R.id.etMedicamento)
        etCantidad = findViewById(R.id.etCantidad)
        tvFecha = findViewById(R.id.tvFecha)
        tvHora = findViewById(R.id.tvHora)
        btnFecha = findViewById(R.id.btnSeleccionarFecha)
        btnHora = findViewById(R.id.btnSeleccionarHora)
        btnGuardar = findViewById(R.id.btnGuardarAlarma)

        btnFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                tvFecha.text = "$d/${m + 1}/$y"
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnHora.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(this, { _, h, m ->
                tvHora.text = String.format("%02d:%02d", h, m)
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        isEditing = intent.getBooleanExtra("editar", false)
        if (isEditing) {
            editIndex = intent.getIntExtra("index", -1)
            etNombre.setText(intent.getStringExtra("nombre"))
            etMedicamento.setText(intent.getStringExtra("medicamento"))
            etCantidad.setText(intent.getStringExtra("cantidad"))
            tvHora.text = intent.getStringExtra("hora")
            tvFecha.text = intent.getStringExtra("fecha")
            btnGuardar.text = "Actualizar Alarma"
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val medicamento = etMedicamento.text.toString()
            val cantidad = etCantidad.text.toString()
            val fecha = tvFecha.text.toString()
            val hora = tvHora.text.toString()

            if (nombre.isBlank() || medicamento.isBlank() || cantidad.isBlank()
                || hora == "Hora no seleccionada" || fecha == "Fecha no seleccionada"
            ) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences("alarmas_guardadas", MODE_PRIVATE)
            val alarmas = JSONArray(sharedPref.getString("alarmas", "[]"))

            val nueva = JSONObject().apply {
                put("nombre", nombre)
                put("medicamento", medicamento)
                put("cantidad", cantidad)
                put("fecha", fecha)
                put("hora", hora)
                put("activa", true)
            }

            if (isEditing && editIndex >= 0) {
                alarmas.put(editIndex, nueva)
            } else {
                alarmas.put(nueva)
            }

            sharedPref.edit().putString("alarmas", alarmas.toString()).apply()

            startActivity(Intent(this, AlarmListActivity::class.java))
            finish()
        }
    }
}
