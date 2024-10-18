package com.example.samid

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.samid.databinding.CheckNowBinding

class CheckNow : AppCompatActivity() {

    private lateinit var binding: CheckNowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CheckNowBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.card3.setOnClickListener {
            val intent = Intent(this, AlarmsViewActivity::class.java)
            startActivity(intent)
        }
    }
}
