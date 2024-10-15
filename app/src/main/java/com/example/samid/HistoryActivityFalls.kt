package com.example.samid

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivityFalls : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_falls)

        // Set up the back button
        val backButton = findViewById<ImageView>(R.id.flecha) // replace with the actual ID of your back button
        backButton.setOnClickListener {
            finish() // This will finish the current activity and go back to the previous one
        }


    }
}