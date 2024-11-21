package com.example.a7minutesworkoutapp

import android.icu.lang.UCharacter.VerticalOrientation
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minutesworkoutapp.databinding.ActivityHistoryBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private var binding : ActivityHistoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarHistoryActivity)

        if(supportActionBar != null){   // this will enable the back button
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "History"
        }

        binding?.toolbarHistoryActivity?.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        val historyDao = (application as WorkoutApp).db.historyDao()

        getAllCompletedDates(historyDao)
    }

    private fun getAllCompletedDates(historyDao: HistoryDao){
        lifecycleScope.launch {
            historyDao.fetchAllDates().collect{allCompletedDatesList ->
                 if(allCompletedDatesList.isNotEmpty()){
                     binding?.tvHistory?.visibility = View.VISIBLE
                     binding?.rvHistory?.visibility = View.VISIBLE
                     binding?.tvNoDataAvailable?.visibility = View.INVISIBLE

                     binding?.rvHistory?.layoutManager = LinearLayoutManager(this@HistoryActivity,LinearLayoutManager.VERTICAL,false)

                     val dates = ArrayList<String>()
                     for(dateList in allCompletedDatesList){
                         dates.add(dateList.date)
                     }

                     val historyAdapter = HistoryAdapter(dates)
                     binding?.rvHistory?.adapter = historyAdapter

                 } else {
                     binding?.tvHistory?.visibility = View.GONE
                     binding?.rvHistory?.visibility = View.GONE
                     binding?.tvNoDataAvailable?.visibility = View.VISIBLE
                 }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}