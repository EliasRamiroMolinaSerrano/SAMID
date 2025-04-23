package com.example.samid

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
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
            startActivity(Intent(this, AlarmActivity::class.java))
        }

        val backButton = findViewById<ImageView>(R.id.flecha)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun crearCardAlarma(alarma: JSONObject, index: Int): CardView {
        val isActiva = alarma.getBoolean("activa")

        val card = CardView(this).apply {
            radius = 20f
            cardElevation = 6f
            setCardBackgroundColor(Color.parseColor("#DCF9F7")) // Amarillo claro
            useCompatPadding = true
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 32)
            layoutParams = params
        }

        val container = RelativeLayout(this).apply {
            setPadding(24, 32, 24, 32)
        }

        val nombre = TextView(this).apply {
            text = alarma.getString("nombre")
            textSize = 16f
            setTextColor(Color.DKGRAY)
            id = View.generateViewId()
        }

        val hora = TextView(this).apply {
            text = alarma.getString("hora")
            textSize = 40f
            setTextColor(Color.BLACK)
            id = View.generateViewId()
        }

        val fecha = TextView(this).apply {
            text = alarma.getString("fecha")
            textSize = 16f
            setTextColor(Color.GRAY)
            id = View.generateViewId()
        }

        val switch = Switch(this).apply {
            isChecked = isActiva
            id = View.generateViewId()
            setOnCheckedChangeListener { _, isChecked ->
                actualizarEstado(index, isChecked)
                recreate()
            }
        }

        container.addView(hora, RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_START)
        })

        container.addView(fecha, RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.BELOW, hora.id)
            addRule(RelativeLayout.ALIGN_PARENT_START)
            topMargin = 8
        })

        container.addView(nombre, RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_END)
            addRule(RelativeLayout.ALIGN_PARENT_TOP)
        })

        container.addView(switch, RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_END)
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        })

        card.addView(container)

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

    fun eliminarAlarma(index: Int) {
        val sharedPref = getSharedPreferences("alarmas_guardadas", MODE_PRIVATE)
        val alarmas = JSONArray(sharedPref.getString("alarmas", "[]"))
        val nuevaLista = JSONArray()
        for (i in 0 until alarmas.length()) {
            if (i != index) nuevaLista.put(alarmas.getJSONObject(i))
        }
        sharedPref.edit().putString("alarmas", nuevaLista.toString()).apply()
    }
}
