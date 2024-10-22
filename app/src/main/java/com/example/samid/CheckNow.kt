package com.example.samid

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.samid.databinding.CheckNowBinding

class CheckNow : AppCompatActivity() {

    // Create a binding reference
    private lateinit var binding: CheckNowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using the generated binding class
        binding = CheckNowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set a click listener on the 'check_btn' button to navigate to ResultsActivity
        binding.checkBtn.setOnClickListener {
            val intent = Intent(this, WebsocketImplementation::class.java) // Use the correct activity class
            startActivity(intent)
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
}
