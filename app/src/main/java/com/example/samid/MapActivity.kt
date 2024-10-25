@file:Suppress("DEPRECATION")

package com.example.samid

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    private lateinit var sharedPreferences: SharedPreferences
    private var locations = mutableListOf<GeoPoint>() // Lista de ubicaciones
    private var locationNames = mutableListOf<String>()
    private var timestamps = mutableListOf<String>()
    private val maxLocations = 5 // Límite de ubicaciones
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configurar la biblioteca de OSMDroid
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.map)
        saveLocationButton = findViewById(R.id.save_location_button)

        // Inicializar el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Cargar ubicaciones guardadas
        loadSavedLocations()

        // Actualizar las tarjetas inmediatamente después de cargar ubicaciones
        updateCardViews()

        // Set up the back button
        val backButton = findViewById<ImageView>(R.id.flecha)
        backButton.setOnClickListener {
            finish() // This will finish the current activity and go back to the previous one
        }

        // Configurar el mapa
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(GeoPoint(19.4326, -99.1332)) // Configura tu ubicación inicial

        saveLocationButton.setOnClickListener {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        // Verificar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        // Obtener la última ubicación conocida
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val currentLocation = GeoPoint(location.latitude, location.longitude)
                saveLocation(currentLocation)
                mapView.controller.animateTo(currentLocation) // Centrar el mapa en la ubicación actual
            }
        }
    }

    private fun saveLocation(location: GeoPoint) {
        // Si ya hay 5 ubicaciones, eliminar la más antigua
        if (locations.size == maxLocations) {
            removeOldestLocation()
        }

        // Agregar la nueva ubicación
        locations.add(location)
        val locationName = getAddressFromLocation(location) // Obtener la dirección
        // Cambiado a dd/MM/yyyy HH:mm:ss.SSS para incluir milisegundos
        val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        locationNames.add(locationName)
        timestamps.add(timestamp)

        // Agregar el marcador en el mapa
        addMarker(location)

        // Guardar datos en SharedPreferences
        saveDataToPreferences()

        // Actualizar las tarjetas
        updateCardViews()
    }

    private fun removeOldestLocation() {
        // Eliminar la ubicación más antigua y su información asociada
        if (locations.isNotEmpty()) {
            locations.removeAt(0)
            locationNames.removeAt(0)
            timestamps.removeAt(0)
        }
    }

    private fun addMarker(location: GeoPoint) {
        val marker = Marker(mapView)
        marker.position = location
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM) // Centrar el marcador
        marker.title = "Ubicación" // Título del marcador (opcional)
        mapView.overlays.add(marker)
        mapView.invalidate() // Actualizar el mapa
    }

    @SuppressLint("DiscouragedApi", "SetTextI18n")
    private fun updateCardViews() {
        for (i in locations.indices) {
            // Obtener los identificadores de los TextViews correspondientes a cada tarjeta
            val nameTextView = findViewById<TextView>(resources.getIdentifier("name${i + 1}", "id", packageName))
            val locationTextView = findViewById<TextView>(resources.getIdentifier("location${i + 1}", "id", packageName))
            val timeTextView = findViewById<TextView>(resources.getIdentifier("time${i + 1}", "id", packageName))

            // Actualizar la información de la tarjeta
            nameTextView?.text = "Manuel" // Cambiar a "Manuel"
            locationTextView?.text = locationNames[i] // Mostrar dirección
            timeTextView?.text = timestamps[i] // Mostrar la fecha y hora en el nuevo formato
        }
    }

    private fun getAddressFromLocation(location: GeoPoint): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        return try {
            val addresses: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                addresses[0].getAddressLine(0) ?: "Dirección no encontrada"
            } else {
                "Dirección no encontrada"
            }
        } catch (e: Exception) {
            "Error al obtener dirección"
        }
    }

    private fun saveDataToPreferences() {
        val editor = sharedPreferences.edit()
        editor.putInt("locations_size", locations.size)
        for (i in locations.indices) {
            editor.putString("location_$i", "${locations[i].latitude},${locations[i].longitude}")
            editor.putString("name_$i", locationNames[i])
            editor.putString("timestamp_$i", timestamps[i])
        }
        editor.apply()
    }

    private fun loadSavedLocations() {
        val size = sharedPreferences.getInt("locations_size", 0)
        for (i in 0 until size) {
            val locationData = sharedPreferences.getString("location_$i", null)
            val name = sharedPreferences.getString("name_$i", null)
            val timestamp = sharedPreferences.getString("timestamp_$i", null)

            if (locationData != null && name != null && timestamp != null) {
                val latLng = locationData.split(",")
                val geoPoint = GeoPoint(latLng[0].toDouble(), latLng[1].toDouble())
                locations.add(geoPoint)
                locationNames.add(name)
                timestamps.add(timestamp)
                addMarker(geoPoint) // Agregar el marcador al mapa
            }
        }
    }
}
