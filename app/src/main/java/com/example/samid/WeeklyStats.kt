package com.example.samid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class WeeklyStatsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var barChart: BarChart
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weekly_stats)

        // Inicializa las vistas según el XML
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigation_view)
        barChart = findViewById(R.id.weekly_bar_chart) // Cambiado a "weekly_bar_chart"
        db = FirebaseFirestore.getInstance()

        // Configura el menú lateral
        setupDrawerMenu()

        // Obtén los datos de Firebase y muestra la gráfica
        getWeeklyStats { weeklyAverages ->
            displayBarChart(weeklyAverages)
        }
    }

    private fun setupDrawerMenu() {
        // Configura el icono del menú
        val rayasIcon = findViewById<ImageView>(R.id.rayas)
        rayasIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START) // Abre el menú lateral
        }

        // Configura el toggle del DrawerLayout
        val toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Maneja los clics del menú
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.patients_view -> {
                    val intent = Intent(this, PatientsView::class.java)
                    startActivity(intent)
                    true
                }
                R.id.weekly_analysis -> {
                    // Ya estás aquí
                    true
                }
                R.id.check_now -> {
                    val intent = Intent(this, CheckNow::class.java)
                    startActivity(intent)
                    true
                }
                R.id.history -> {
                    val intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.device_status -> {
                    val intent = Intent(this, DeviceStatus::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_logout -> {
                    // Limpia datos de sesión y redirige al login
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

    private fun getWeeklyStats(callback: (List<Float>) -> Unit) {
        db.collection("monitoreos")
            .get()
            .addOnSuccessListener { documents ->
                val weeklyData = mutableMapOf<Int, MutableList<Float>>()

                for (document in documents) {
                    val week = document.getLong("week")?.toInt() ?: continue
                    val value = document.getDouble("value")?.toFloat() ?: continue

                    weeklyData.getOrPut(week) { mutableListOf() }.add(value)
                }

                // Calcular promedios semanales
                val weeklyAverages = weeklyData.map { (_, values) ->
                    values.average().toFloat()
                }

                callback(weeklyAverages)
            }
            .addOnFailureListener { e ->
                Log.e("WeeklyStatsActivity", "Error getting weekly stats: ${e.message}")
                callback(emptyList())
            }
    }

    private fun displayBarChart(weeklyAverages: List<Float>) {
        val entries = weeklyAverages.mapIndexed { index, average ->
            BarEntry(index.toFloat(), average)
        }

        val dataSet = BarDataSet(entries, "Promedio Semanal")
        val barData = BarData(dataSet)

        barChart.data = barData
        barChart.description.isEnabled = false

        // Configurar el eje X
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.setDrawGridLines(false)
        barChart.xAxis.granularity = 1f

        barChart.invalidate() // Actualiza la gráfica
    }

    override fun onBackPressed() {
        // Cierra el menú lateral si está abierto
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
