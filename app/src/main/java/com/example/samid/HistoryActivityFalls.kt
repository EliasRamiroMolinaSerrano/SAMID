package com.example.samid

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.os.AsyncTask
import okhttp3.Response
import org.json.JSONArray
import java.text.ParseException
import java.util.*

class HistoryActivityFalls : AppCompatActivity() {

    private lateinit var cardContainer: LinearLayout  // Container for dynamically created cards
    private lateinit var webSocket: WebSocket  // WebSocket variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_falls)

        // Set up the back button
        val backButton = findViewById<ImageView>(R.id.flecha)
        backButton.setOnClickListener {
            finish()
        }

        // Initialize card container
        cardContainer = findViewById(R.id.cardContainer)

        fetchFallRecords()

        // Set up WebSocket listener
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${Constants.WSSERVER_URL}") // Ensure this URL is correct
            .build()

        val webSocketListener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received message: $text")
                try {
                    val jsonObject = JSONObject(text)
                    if (jsonObject.has("fall")) {
                        runOnUiThread {
                            addFallCard2()
                            sendFallNotification()  // Send notification on fall detection
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("WebSocket", "Error parsing message: ${e.message}")
                }
            }

            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                Log.d("WebSocket", "Connection opened")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                Log.e("WebSocket", "Error: ${t.message}")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                Log.d("WebSocket", "Connection closing: $reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Connection closed: $reason")
            }
        }

        // Create the WebSocket connection
        webSocket = client.newWebSocket(request, webSocketListener)
    }

    private fun sendFallNotification() {
        // Vibrate the device
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(500) // Vibrates for 500ms

        // Set up the notification channel (for Android 8.0 and above)
        val channelId = "fall_detection_channel"
        val channelName = "Fall Detection Alerts"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 100, 500) // Custom vibration pattern
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val notificationIntent = Intent(this, HistoryActivityFalls::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.fall_ic) // Make sure you have this icon in your resources
            .setContentTitle("Fall Detected")
            .setContentText("A fall has been detected. Tap to view details.")
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(0, 500, 100, 500)) // Custom vibration pattern
            .setContentIntent(pendingIntent)
            .build()

        // Show the notification
        notificationManager.notify(0, notification)
    }

    private fun fetchFallRecords() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${Constants.RESTSERVER_URL}/get_falls") // Ensure this URL matches your server's endpoint
            .build()

        // Usa AsyncTask para realizar la solicitud en un hilo secundario
        AsyncTask.execute {
            try {
                val response: Response = client.newCall(request).execute()
                val jsonResponse = response.body?.string()

                if (jsonResponse != null) {
                    val jsonArray = JSONArray(jsonResponse)
                    for (i in 0 until jsonArray.length()) {
                        val fallRecord = jsonArray.getJSONObject(i)

                        // Extrae el tiempo del registro desde el JSON
                        val time = fallRecord.getString("detected_at") // Asegúrate de que el campo se llame 'time' en la respuesta JSON

                        runOnUiThread {
                            // Añade una tarjeta para cada registro de caída con el tiempo extraído
                            addFallCard(time)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("HistoryActivityFalls", "Error fetching fall records: ${e.message}")
            }
        }
    }

    private fun addFallCard(time: String) {
        // Infla el diseño de la tarjeta
        val cardView = layoutInflater.inflate(R.layout.card_fall, null)

        // Encuentra las vistas dentro del diseño de la tarjeta
        val nameTextView = cardView.findViewById<TextView>(R.id.name)
        val timeTextView = cardView.findViewById<TextView>(R.id.time)

        // Formato de entrada (ISO 8601)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")  // Asegúrate de que esté en UTC

        // Formato de salida
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        // Convierte y formatea la fecha
        val formattedTime = try {
            val date = inputFormat.parse(time)  // Convierte a tipo Date
            outputFormat.format(date)           // Convierte a formato deseado
        } catch (e: ParseException) {
            Log.e("DateParseError", "Error parsing date: ${e.message}")
            time  // En caso de error, usa el tiempo original
        }

        // Establece los datos para la nueva tarjeta
        nameTextView.text = "Fall Detected"
        timeTextView.text = formattedTime

        // Crea parámetros de diseño para la tarjeta
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // Establece el margen superior
        layoutParams.setMargins(0, 20, 0, 0)
        cardView.layoutParams = layoutParams

        // Añade la tarjeta al contenedor
        cardContainer.addView(cardView)
    }

    private fun addFallCard2() {
        // Inflate the card layout
        val cardView = layoutInflater.inflate(R.layout.card_fall, null)

        // Find views in the card layout
        val nameTextView = cardView.findViewById<TextView>(R.id.name)
        val timeTextView = cardView.findViewById<TextView>(R.id.time)

        // Set the data for the new card
        nameTextView.text = "Fall Detected"
        timeTextView.text = getCurrentTime() // Call your function to get the current time

        // Create layout params for the card
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // Set top margin
        layoutParams.setMargins(0, 20, 0, 0)
        cardView.layoutParams = layoutParams

        // Add the card to the container
        cardContainer.addView(cardView)
    }

    // Function to get the current time
    private fun getCurrentTime(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, null) // Close the WebSocket connection when activity is destroyed
    }
}
