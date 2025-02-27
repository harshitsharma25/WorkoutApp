package com.example.a7minutesworkoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import com.example.a7minutesworkoutapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)  // it means binding references to the activity_main.xml file's root element

        binding?.flStart?.setOnClickListener {
           val intent = Intent(this,ExerciseActivity::class.java)
            startActivity(intent)
        }
        binding?.flBMI?.setOnClickListener {
            val intent = Intent(this,BmiActivity::class.java)
            startActivity(intent)
        }

        binding?.flHistory?.setOnClickListener{
            val intent = Intent(this,HistoryActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}