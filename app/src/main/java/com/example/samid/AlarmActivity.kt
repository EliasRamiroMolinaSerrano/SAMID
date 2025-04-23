package com.example.samid

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
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
    private lateinit var btnEliminar: Button

    private var isEditing = false
    private var editIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        // Enlazar vistas
        etNombre = findViewById(R.id.etNombre)
        etMedicamento = findViewById(R.id.etMedicamento)
        etCantidad = findViewById(R.id.etCantidad)
        tvFecha = findViewById(R.id.tvFecha)
        tvHora = findViewById(R.id.tvHora)
        btnFecha = findViewById(R.id.btnSeleccionarFecha)
        btnHora = findViewById(R.id.btnSeleccionarHora)
        btnGuardar = findViewById(R.id.btnGuardarAlarma)
        btnEliminar = findViewById(R.id.btnEliminarAlarma)

        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        // Selección de fecha
        btnFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                tvFecha.text = "$d/${m + 1}/$y"
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Selección de hora
        btnHora.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(this, { _, h, m ->
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, h)
                    set(Calendar.MINUTE, m)
                }
                val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
                tvHora.text = format.format(calendar.time)
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }

        // Verificar si estamos editando
        isEditing = intent.getBooleanExtra("editar", false)
        if (isEditing) {
            try {
                editIndex = intent.getIntExtra("index", -1)

                etNombre.setText(intent.getStringExtra("nombre") ?: "")
                etMedicamento.setText(intent.getStringExtra("medicamento") ?: "")
                etCantidad.setText(intent.getStringExtra("cantidad") ?: "")
                tvHora.text = intent.getStringExtra("hora") ?: ""
                tvFecha.text = intent.getStringExtra("fecha") ?: ""

                btnGuardar.text = "Actualizar Alarma"
                btnEliminar.visibility = View.VISIBLE

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error al cargar los datos de la alarma", Toast.LENGTH_LONG).show()
            }
        } else {
            btnEliminar.visibility = View.GONE
        }

        // Guardar o actualizar alarma
        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val medicamento = etMedicamento.text.toString()
            val cantidad = etCantidad.text.toString()
            val fecha = tvFecha.text.toString()
            val hora = tvHora.text.toString()

            if (nombre.isBlank() || medicamento.isBlank() || cantidad.isBlank()
                || hora.isBlank() || fecha.isBlank()) {
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

            if (isEditing && editIndex in 0 until alarmas.length()) {
                alarmas.put(editIndex, nueva)
            } else {
                alarmas.put(nueva)
            }

            sharedPref.edit().putString("alarmas", alarmas.toString()).apply()

            try {
                val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val parsedHora = sdf.parse(hora)
                val now = Calendar.getInstance()

                val cal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, Calendar.getInstance().apply { time = parsedHora!! }.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, Calendar.getInstance().apply { time = parsedHora }.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    if (before(now)) {
                        add(Calendar.DAY_OF_MONTH, 1)
                    }
                }

                Log.d("AlarmDebug", "⏰ Programando alarma para: ${cal.time}")
                programarNotificacion(nombre, medicamento, hora, cal)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error al programar notificación: ${e.message}", Toast.LENGTH_LONG).show()
            }

            startActivity(Intent(this, AlarmListActivity::class.java))
            finish()
        }

        // Eliminar alarma si estamos editando
        btnEliminar.setOnClickListener {
            if (editIndex >= 0) {
                val sharedPref = getSharedPreferences("alarmas_guardadas", MODE_PRIVATE)
                val alarmas = JSONArray(sharedPref.getString("alarmas", "[]"))
                val nuevaLista = JSONArray()
                for (i in 0 until alarmas.length()) {
                    if (i != editIndex) nuevaLista.put(alarmas.getJSONObject(i))
                }
                sharedPref.edit().putString("alarmas", nuevaLista.toString()).apply()
                Toast.makeText(this, "Alarma eliminada", Toast.LENGTH_SHORT).show()
            }
            startActivity(Intent(this, AlarmListActivity::class.java))
            finish()
        }
    }

    private fun programarNotificacion(nombre: String, medicamento: String, hora: String, calendar: Calendar) {
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("nombre", nombre)
            putExtra("medicamento", medicamento)
            putExtra("hora", hora)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "SamidAlarmChannel",
                "Canal de alarmas Samid",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios importantes de alarmas médicas"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
