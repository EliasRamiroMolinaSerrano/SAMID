package com.example.samid

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.samid.databinding.CheckNowBinding
import okhttp3.*
import java.io.IOException

class CheckNow : AppCompatActivity() {

    // Create a binding reference
    private lateinit var binding: CheckNowBinding
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using the generated binding class
        binding = CheckNowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set a click listener on the 'check_btn' button to navigate to ResultsActivity
        binding.checkBtn.setOnClickListener {
            val intent = Intent(this, WebsocketImplementation::class.java) // Use the correct activity class
            startActivity(intent)
            sendRequestToServer()
        }

        // Set a click listener on 'card3' to navigate to AlarmsViewActivity
        binding.card3.setOnClickListener {
            val intent = Intent(this, WebsocketImplementation::class.java)
            startActivity(intent)
        }

        // Set up the back button
        val backButton = findViewById<ImageView>(R.id.flecha)
        backButton.setOnClickListener {
            finish() // This will finish the current activity and go back to the previous one
        }

    }

    // Function to send request to the Raspberry Pi server
    private fun sendRequestToServer() {
        val url = "${Constants.RESTSERVER_URL}/start-readings"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CheckNow, "Failed to send request", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@CheckNow, "Request sent successfully", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@CheckNow, "Request failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
