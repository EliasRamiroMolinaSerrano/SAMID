@file:Suppress("DEPRECATION")

package com.example.samid

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.PreferenceManager
import android.preference.PreferenceManager.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var saveLocationButton: Button
    private var locations = mutableListOf<GeoPoint>() // Lista de ubicaciones
    private var locationNames = mutableListOf<String>()
    private var timestamps = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configurar la biblioteca de OSMDroid
        Configuration.getInstance().load(this, getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.map)
        saveLocationButton = findViewById(R.id.save_location_button)

        // Configurar el mapa
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(GeoPoint(19.4326, -99.1332)) // Configura tu ubicación inicial

        saveLocationButton.setOnClickListener {
            val currentLocation = GeoPoint(mapView.mapCenter.latitude, mapView.mapCenter.longitude) // Conversión a GeoPoint
            saveLocation(currentLocation)
        }
    }

    private fun saveLocation(location: GeoPoint) {
        locations.add(location)
        val locationName = "Ubicación ${locations.size}"
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        locationNames.add(locationName)
        timestamps.add(timestamp)

        // Agregar el marcador en el mapaz
        addMarker(location)

        // Actualizar las tarjetas
        updateCardViews()
    }

    private fun addMarker(location: GeoPoint) {
        val marker = Marker(mapView)
        marker.position = location
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM) // Centrar el marcador
        marker.title = "Ubicación" // Título del marcador (opcional)
        mapView.overlays.add(marker)
        mapView.invalidate() // Actualizar el mapa
        mapView.controller.animateTo(location) // Centrar el mapa en la ubicación
    }

    @SuppressLint("DiscouragedApi", "SetTextI18n")
    private fun updateCardViews() {
        for (i in locations.indices) {
            val nameTextView = findViewById<TextView>(resources.getIdentifier("name${i + 1}", "id", packageName))
            val locationTextView = findViewById<TextView>(resources.getIdentifier("location${i + 1}", "id", packageName))
            val timeTextView = findViewById<TextView>(resources.getIdentifier("time${i + 1}", "id", packageName))

            nameTextView.text = locationNames[i]
            "Ubicación: ${locations[i].latitude}, ${locations[i].longitude}".also { locationTextView.text = it }
            timeTextView.text = "Hora: ${timestamps[i]}"
        }
    }
}
