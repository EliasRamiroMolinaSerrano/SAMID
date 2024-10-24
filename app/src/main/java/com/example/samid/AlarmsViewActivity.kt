package com.example.samid

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.samid.databinding.ActivityAlarmsViewBinding

class AlarmsViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmsViewBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlarmsViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Extraer los datos pasados por el Intent
        val nombrePaciente = intent.getStringExtra("nombrePaciente")
        val nombreAlarma = intent.getStringExtra("nombreAlarma")
        val descripcion = intent.getStringExtra("descripcion")
        val Hora = intent.getStringExtra("HoraSeleccionada")  // Clave correcta para la hora seleccionada

        // Verificar si hay datos para mostrar
        if (nombrePaciente.isNullOrEmpty() || nombreAlarma.isNullOrEmpty() || descripcion.isNullOrEmpty()) {
            // Ocultar el CardView si no hay datos
            binding.cardPatients.visibility = View.GONE
            binding.texto2.text = "No hay alarmas configuradas."
        } else {
            // Mostrar el CardView con los datos de la alarma
            binding.cardPatients.visibility = View.VISIBLE
            binding.nombrePaciente.text = nombrePaciente
            binding.nombreAlarma.text = nombreAlarma
            binding.descripcionAlarma.text = descripcion
            binding.Hora.text = Hora  // Mostrar la hora seleccionada
        }

        // Configurar el botón de agregar alarma
        binding.agregarBtn.setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        }

        // Manejar el padding de las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
