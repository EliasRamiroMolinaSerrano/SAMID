package com.example.samid
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okio.ByteString
import org.json.JSONObject

class WebsocketImplementation : AppCompatActivity() {
    private lateinit var bpmTextView: TextView
    private lateinit var spo2TextView: TextView
    private lateinit var webSocket: WebSocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        bpmTextView = findViewById(R.id.bpmTextView)
        spo2TextView = findViewById(R.id.spo2TextView)

        setupWebSocket()

        // Set up the back button
        val backButton = findViewById<ImageView>(R.id.flecha) // replace with the actual ID of your back button
        backButton.setOnClickListener {
            finish() // This will finish the current activity and go back to the previous one
        }
    }

    private fun setupWebSocket() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("ws://192.168.0.116:8081") // Replace with your Raspberry Pi WebSocket URL
            .build()

        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "Connection established")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Message received: $text") // Log the received message
                runOnUiThread {
                    updateUIWithSensorData(text)
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                // Handle binary messages (if any)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                // WebSocket is closing
                webSocket.close(1000, null)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // Error in WebSocket
            }
        }

        webSocket = client.newWebSocket(request, webSocketListener)
    }

    private fun updateUIWithSensorData(data: String) {
        try {
            // Parse the JSON received from WebSocket
            val jsonObject = JSONObject(data)
            val bpm = jsonObject.getInt("pulse")
            val spo2 = jsonObject.getInt("spo2")

            // Update the TextViews
            bpmTextView.text = "$bpm BPM"
            spo2TextView.text = "$spo2 % SPO2"
        } catch (e: Exception) {
            Log.e("WebSocket", "Error parsing JSON: ${e.message}")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, null) // Close the WebSocket connection when activity is destroyed
    }
}