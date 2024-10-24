package com.example.samid

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.samid.databinding.ActivityAlarmBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class AlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmBinding
    private lateinit var picker: MaterialTimePicker
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)  // Ajustar el binding
        setContentView(binding.root)
        createNotificationChannel()

        // Configurar el botón de agregar alarma
        binding.setAlarmBtn.setOnClickListener {
            val HoraSeleccionada = binding.selectedTime.text.toString() // Obtener la hora seleccionada desde el binding
            val nombrePaciente = binding.nombrePaciente?.text.toString() // Cambia aquí para usar el binding
            val descripcion = binding.descripcionAlarma.text.toString() // Cambia aquí para usar el binding
            val nombreAlarma = binding.nombreAlarma.text.toString() // Cambia aquí para usar el binding

            // Llamar a la función que configura la alarma
            setAlarm()

            // Crear un Intent para iniciar AlarmsViewActivity
            val intent = Intent(this, AlarmsViewActivity::class.java)

            // Pasar los datos a AlarmsViewActivity
            intent.putExtra("nombrePaciente", nombrePaciente)
            intent.putExtra("nombreAlarma", nombreAlarma)
            intent.putExtra("descripcion", descripcion)
            intent.putExtra("HoraSeleccionada", HoraSeleccionada) // Asegúrate de que esta clave sea la misma

            // Iniciar AlarmsViewActivity
            startActivity(intent)
        }


        // Configurar el botón de retroceso
        val backButton = findViewById<ImageView>(R.id.flecha) // Reemplaza con el ID real de tu botón de retroceso
        backButton.setOnClickListener {
            finish() // Esto cerrará la actividad actual y volverá a la anterior
        }

        // Solicitar permisos para notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        binding.selectTimeBtn.setOnClickListener {
            showTimePicker()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setAlarm() {
        if (!::calendar.isInitialized) {
            Toast.makeText(this, "Por favor, selecciona una hora primero", Toast.LENGTH_SHORT).show()
            return
        }

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Log para verificar el tiempo de la alarma
        Log.d("AlarmActivity", "Alarma configurada para: ${calendar.time}")

        alarmManager.set(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            pendingIntent
        )

        Toast.makeText(this, "Alarma configurada exitosamente", Toast.LENGTH_SHORT).show()
    }

    private fun showTimePicker() {
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Selecciona la hora de tu alarma")
            .build()

        picker.show(supportFragmentManager, "Samid")

        picker.addOnPositiveButtonClickListener {
            binding.selectedTime.text = if (picker.hour > 12) {
                String.format("%02d", picker.hour - 12) + " : " + String.format("%02d", picker.minute) + " PM"
            } else {
                String.format("%02d", picker.hour) + " : " + String.format("%02d", picker.minute) + " AM"
            }

            calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "SamidReminderChannel"
            val description = "Channel for alarm manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("Samid", name, importance).apply {
                this.description = description
            }

            // Registrar el canal de notificación
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Manejo de resultados de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permiso concedido
                    Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show()
                } else {
                    // Permiso denegado
                    Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}
