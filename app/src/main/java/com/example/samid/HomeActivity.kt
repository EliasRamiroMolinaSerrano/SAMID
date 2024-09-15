package com.example.samid

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Button to trigger the custom dialog
        val pacientBtn = findViewById<Button>(R.id.pacientBtn)

        // Set an onClickListener to show the custom dialog when pacientBtn is clicked
        pacientBtn.setOnClickListener {
            // Inflate the custom dialog layout
            val customDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)

            // Create the AlertDialog
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(customDialogView) // Set the custom dialog view
                .create()

            // Set transparent background for the dialog
            dialogBuilder.window?.setBackgroundDrawableResource(android.R.color.transparent)

            // Show the dialog
            dialogBuilder.show()

            // Find the OK button in the custom dialog and set its onClickListener
            val okBtn = customDialogView.findViewById<Button>(R.id.ok_btn)
            okBtn.setOnClickListener {
                dialogBuilder.dismiss() // Close the dialog when OK is clicked
            }
        }

        // Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }
}

