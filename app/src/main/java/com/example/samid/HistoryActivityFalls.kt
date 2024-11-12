package com.example.samid

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

class HistoryActivityFalls : AppCompatActivity() {

    private lateinit var cardContainer: LinearLayout  // Container for dynamically created cards
    private lateinit var webSocket: WebSocket  // WebSocket variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_falls)

        // Set up the back button
        val backButton = findViewById<ImageView>(R.id.flecha)
        backButton.setOnClickListener {
            finish() // This will finish the current activity and go back to the previous one
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
                        runOnUiThread { addFallCard() } // Update UI on the main thread
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

    private fun fetchFallRecords() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${Constants.RESTSERVER_URL}/get_falls") // Ensure this URL matches your server's endpoint
            .build()

        // Use AsyncTask to fetch data on a background thread
        AsyncTask.execute {
            try {
                val response: Response = client.newCall(request).execute()
                val jsonResponse = response.body?.string()

                if (jsonResponse != null) {
                    val jsonArray = JSONArray(jsonResponse)
                    for (i in 0 until jsonArray.length()) {
                        val fallRecord = jsonArray.getJSONObject(i)
                        runOnUiThread {
                            // Add a card for each fall record
                            addFallCard()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("HistoryActivityFalls", "Error fetching fall records: ${e.message}")
            }
        }
    }

    private fun addFallCard() {
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
        // You can customize this method to return a formatted time string
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, null) // Close the WebSocket connection when activity is destroyed
    }
}
