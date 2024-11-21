package com.example.a7minutesworkoutapp

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minutesworkoutapp.databinding.ActivityDialogCustomBackConfirmationBinding
import com.example.a7minutesworkoutapp.databinding.ActivityExerciseBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.Queue

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    var binding : ActivityExerciseBinding? = null

    private var restTimer : CountDownTimer? = null
    private var restProgress = 0
    private var restTimerDuration : Long = 10
    private var exerciseTimerDuration : Long = 30

    private var tts : TextToSpeech? = null
    private var player : MediaPlayer? = null

    private var exerciseAdapter : ExerciseStatusAdapter? = null


    private var exerciseTimer : CountDownTimer? = null
    private var exerciseProgress = 0

    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if(supportActionBar != null){   // this will enable the back button
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        exerciseList = Constants.defaultExerciseList()  // loading the default List of exercises

        tts = TextToSpeech(this,this)

        binding?.toolbarExercise?.setNavigationOnClickListener {
            // Handle back button press
            customDialogForBackButton()
        }

        setupRestView()
        setupExerciseStatusRecyclerView()
    }

    override fun onBackPressed() {
        customDialogForBackButton()
    }

    private fun customDialogForBackButton(){
        val customDialog = Dialog(this)
        val dialogBinding = ActivityDialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.btnYes.setOnClickListener{

            //these both
            customDialog.dismiss()
            this@ExerciseActivity.finish()

//            OR

            //onBackPressedDispatcher.onBackPressed()
        }
        dialogBinding.btnNo.setOnClickListener{
            customDialog.dismiss()
        }

        customDialog.show()
    }


    private fun setupExerciseStatusRecyclerView(){

        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }


    private fun setupRestView(){

        try {
            val soundURI = Uri.parse(
                "android.resource://com.example.a7minutesworkoutapp/" + R.raw.app_src_main_res_raw_press_start
            )
            player = MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping = false
            player?.start()

        } catch (e: Exception){
            e.printStackTrace()
        }

        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.tvUpcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpComingExerciseName?.visibility = View.VISIBLE

        binding?.tvUpComingExerciseName?.text = exerciseList!![currentExercisePosition+1].getName()




        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        setRestProgressBar()
    }

    private fun setupExerciseView(){

        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvUpcomingLabel?.visibility = View.INVISIBLE
        binding?.tvUpComingExerciseName?.visibility = View.INVISIBLE

        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE




        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()


        setExerciseProgressBar()
    }


    private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress

        restTimer = object : CountDownTimer(restTimerDuration * 1000,1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()

                setupExerciseView()
            }

        }.start()
    }

    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 1000,1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvExerciseTimer?.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {

                if(currentExercisePosition < exerciseList?.size!! - 1){
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()

                    setupRestView()
                } else {

                    val intent = Intent(this@ExerciseActivity,FinishActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
        }.start()
    }


    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts?.setLanguage(Locale.US)

            if(result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_MISSING_DATA){
                Log.e("TTS","The Language specified is not supported")
            }
        } else {
            Log.e("TTS","Initialization failured!!!")
        }
    }


    private fun speakOut(text : String){
        lifecycleScope.launch(Dispatchers.Default){
            tts!!.setSpeechRate(0.75f)
            tts!!.speak(text, TextToSpeech.QUEUE_FLUSH,null,"")
        }

    }


    override fun onDestroy() {
        super.onDestroy()

        // stopping the progress bar of rest time
        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        // stopping the progress Bar of exercise
        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        // stopping the text to Speech converter
        if(tts != null){
            tts?.stop()
            tts?.shutdown()
        }

        // stopping the player
        if(player != null){
            player?.stop()
        }
        binding = null
    }

}