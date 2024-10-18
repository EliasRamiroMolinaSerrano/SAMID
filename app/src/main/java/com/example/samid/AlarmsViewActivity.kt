package com.example.samid

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.samid.databinding.ActivityAlarmsViewBinding

class AlarmsViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmsViewBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar el binding con ActivityAlarmsViewBinding
        binding = ActivityAlarmsViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Extraer los datos pasados por el Intent
        val nombrePaciente = intent.getStringExtra("nombrePaciente")
        val nombreAlarma = intent.getStringExtra("nombreAlarma")
        val descripcion = intent.getStringExtra("descripcion")

        // Mostrar los datos en los campos correspondientes
        binding.nombrePaciente.setText(nombrePaciente)
        binding.nombreAlarma.setText(nombreAlarma)
        binding.descripcionAlarma.setText(descripcion)

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
