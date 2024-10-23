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
import java.util.Date
import java.util.Locale

class HistoryActivityFalls : AppCompatActivity() {

    private lateinit var cardContainer: LinearLayout  // Container for dynamically created cards

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_falls)

        // Set up the back button
        val backButton = findViewById<ImageView>(R.id.flecha) // Replace with the actual ID of your back button
        backButton.setOnClickListener {
            finish() // This will finish the current activity and go back to the previous one
        }

        // Initialize card container
        cardContainer = findViewById(R.id.cardContainer) // Ensure you have a LinearLayout in your XML for cards

        // Set up WebSocket listener
        val client = OkHttpClient()
        val webSocketListener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received message: $text")
                try {
                    val jsonObject = JSONObject(text)
                    if (jsonObject.has("fall")) {
                        runOnUiThread { addFallCard() } // Update UI on the main thread
                    }
                    // Handle other messages similarly
                } catch (e: JSONException) {
                    Log.e("WebSocket", "Error parsing message: ${e.message}")
                }
            }


            // Implement other WebSocket callbacks if necessary
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
    }

    private fun addFallCard() {
        // Inflate the card layout
        val cardView = layoutInflater.inflate(R.layout.card_fall, null) // Inflate card_fall.xml

        // Find views in the card layout
        val nameTextView = cardView.findViewById<TextView>(R.id.name)
        val timeTextView = cardView.findViewById<TextView>(R.id.time)

        // Set the data for the new card
        nameTextView.text = "Fall Detected"
        timeTextView.text = getCurrentTime() // Implement this function to get the current time as needed

        // Add the card to the container
        cardContainer.addView(cardView)
    }

    // Function to get the current time
    private fun getCurrentTime(): String {
        // You can customize this method to return a formatted time string
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(Date(currentTime))
    }

}
