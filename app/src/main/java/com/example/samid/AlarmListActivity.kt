package com.example.samid

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import org.json.JSONArray
import org.json.JSONObject

class AlarmListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_list)

        val container = findViewById<LinearLayout>(R.id.alarmListContainer)
        val alarmas = cargarAlarmas(this)

        for (i in 0 until alarmas.length()) {
            val alarma = alarmas.getJSONObject(i)
            container.addView(crearCardAlarma(alarma, i))
        }

        val addButton = findViewById<ImageView>(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        }

        val backButton = findViewById<ImageView>(R.id.flecha)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun crearCardAlarma(alarma: JSONObject, index: Int): CardView {
        val card = CardView(this).apply {
            radius = 20f
            cardElevation = 8f
            setCardBackgroundColor(Color.parseColor("#DCF9F7")) // Verde menta claro
            setContentPadding(24, 24, 24, 24)
            useCompatPadding = true
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 32)
            layoutParams = params
            isClickable = true
            isFocusable = true
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        // ── Línea 1: Nombre + eliminar
        val filaNombreEliminar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL

            val nombre = TextView(this@AlarmListActivity).apply {
                text = "👤 ${alarma.getString("nombre")}"
                textSize = 24f
                setTextColor(Color.BLACK)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val deleteIcon = ImageView(this@AlarmListActivity).apply {
                setImageResource(R.drawable.delete_ic)
                val params = LinearLayout.LayoutParams(60, 60)
                params.gravity = Gravity.END
                layoutParams = params
                setOnClickListener {
                    it.animate().alpha(0f).setDuration(300).withEndAction {
                        eliminarAlarma(index)
                        Toast.makeText(this@AlarmListActivity, "Alarma eliminada", Toast.LENGTH_SHORT).show()
                        recreate()
                    }.start()
                }
            }

            addView(nombre)
            addView(deleteIcon)
        }

        // ── Línea 2: Hora + medicamento centrado
        val filaHoraMed = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 8, 0, 0)
            weightSum = 3f

            val hora = TextView(this@AlarmListActivity).apply {
                text = "⏰ ${alarma.getString("hora")}"
                textSize = 20f
                setTextColor(Color.BLACK)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val medicamento = TextView(this@AlarmListActivity).apply {
                text = "💊 ${alarma.getString("medicamento")} - ${alarma.getString("cantidad")}"
                textSize = 20f
                setTextColor(Color.BLACK)
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val espacio = TextView(this@AlarmListActivity).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            addView(hora)
            addView(medicamento)
            addView(espacio)
        }

        // ── Línea 3: Fecha
        val fecha = TextView(this).apply {
            text = "📅 ${alarma.getString("fecha")}"
            textSize = 14f
            setTextColor(Color.DKGRAY)
            setPadding(0, 8, 0, 8)
        }

        // ── Línea 4: Estado + switch
        val filaEstadoSwitch = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL

            val estado = TextView(this@AlarmListActivity).apply {
                text = if (alarma.getBoolean("activa")) "🟢 Activa" else "🔴 Inactiva"
                textSize = 16f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val switch = Switch(this@AlarmListActivity).apply {
                isChecked = alarma.getBoolean("activa")
                setOnCheckedChangeListener { _, isChecked ->
                    estado.text = if (isChecked) "🟢 Activa" else "🔴 Inactiva"
                    actualizarEstado(index, isChecked)
                }
            }

            addView(estado)
            addView(switch)
        }

        // ── Editar al hacer clic
        card.setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java).apply {
                putExtra("editar", true)
                putExtra("index", index)
                putExtra("nombre", alarma.getString("nombre"))
                putExtra("hora", alarma.getString("hora"))
                putExtra("fecha", alarma.getString("fecha"))
                putExtra("medicamento", alarma.getString("medicamento"))
                putExtra("cantidad", alarma.getString("cantidad"))
            }
            startActivity(intent)
        }

        container.apply {
            addView(filaNombreEliminar)
            addView(filaHoraMed)
            addView(fecha)
            addView(filaEstadoSwitch)
        }

        card.addView(container)
        return card
    }

    private fun cargarAlarmas(context: Context): JSONArray {
        val sharedPref = context.getSharedPreferences("alarmas_guardadas", MODE_PRIVATE)
        val json = sharedPref.getString("alarmas", "[]")
        return JSONArray(json)
    }

    private fun actualizarEstado(index: Int, activo: Boolean) {
        val sharedPref = getSharedPreferences("alarmas_guardadas", MODE_PRIVATE)
        val alarmas = JSONArray(sharedPref.getString("alarmas", "[]"))
        alarmas.getJSONObject(index).put("activa", activo)
        sharedPref.edit().putString("alarmas", alarmas.toString()).apply()
    }

    private fun eliminarAlarma(index: Int) {
        val sharedPref = getSharedPreferences("alarmas_guardadas", MODE_PRIVATE)
        val alarmas = JSONArray(sharedPref.getString("alarmas", "[]"))
        val nuevaLista = JSONArray()
        for (i in 0 until alarmas.length()) {
            if (i != index) nuevaLista.put(alarmas.getJSONObject(i))
        }
        sharedPref.edit().putString("alarmas", nuevaLista.toString()).apply()
    }
}
