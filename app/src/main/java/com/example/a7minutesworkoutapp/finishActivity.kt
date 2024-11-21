package com.example.a7minutesworkoutapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.a7minutesworkoutapp.databinding.FinishActivityBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FinishActivity : AppCompatActivity() {

    private var binding : FinishActivityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FinishActivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)

       // setSupportActionBar(binding?.toolbarFinishActivity)

        if(supportActionBar != null){   // this will enable the back button
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val dao = (application as WorkoutApp).db.historyDao()
        addDateToDatabase(dao)

        binding?.tvFinish?.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun addDateToDatabase(historyDao: HistoryDao){
        val myCalendar = Calendar.getInstance()
        val dateTime = myCalendar.time
        Log.e("Date:",""+dateTime)

        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
        val date = sdf.format(dateTime)
        lifecycleScope.launch {
            historyDao.insert(
                HistoryEntity(
                    date = date
                )
            )
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}