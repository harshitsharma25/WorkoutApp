package com.example.a7minutesworkoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.a7minutesworkoutapp.databinding.ActivityBmiBinding
import com.example.a7minutesworkoutapp.databinding.ActivityExerciseBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BmiActivity : AppCompatActivity() {

    companion object{
        private const val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW"
        private const val US_UNITS_VIEW = "US_UNIT_VIEW"
    }

    private var binding : ActivityBmiBinding? = null
    private var currentVisibleView : String = METRIC_UNITS_VIEW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // code to enable our actionBar.
        setSupportActionBar(binding?.toolbarBmiActivity)
        // code to enable our actionBar's back button.
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Calculate BMI"
        }

        binding?.toolbarBmiActivity?.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding?.rgUnits?.setOnCheckedChangeListener{ _ , checkedId : Int ->
            if(checkedId == R.id.rbMetricUnits){
                makeVisibleMetricUnitsView()
            } else {
                makeVisibleUsUnitsView()
            }
        }

        binding?.btnCalculateUnits?.setOnClickListener{
            calculateUnits()
        }
    }

    private fun displayBmiResult(bmi:Float){
        binding?.llDisplayBMIResult?.visibility = View.VISIBLE

        val bmiLabel : String
        val bmiDescription : String

        if(bmi.compareTo(15f) <= 0){
            bmiLabel = "Very severly underweight"
            bmiDescription = "Oops! You really take care of yourself seriously!"
        } else if(bmi.compareTo(15f) > 0 && (bmi.compareTo(16f) <= 0)){
            bmiLabel = "Severly Underweight"
            bmiDescription = "Oops! Eat more!"
        } else if(bmi.compareTo(16f) > 0 && (bmi.compareTo(18.5f) <= 0 )){
            bmiLabel = "Underweight"
            bmiDescription = "Oops! Eat more!"
        } else if(bmi.compareTo(18.5f) > 0 && (bmi.compareTo(24.9f) <= 0 )){
            bmiLabel = "Normal"
            bmiDescription = "Good! you are fine!"
        } else if(bmi.compareTo(24.9f) > 0 && (bmi.compareTo(29.9f) <= 0 )){
            bmiLabel = "OverWeight"
            bmiDescription = "Oops! Do Workout"
        } else if(bmi.compareTo(29.9f) > 0 && (bmi.compareTo(34.9f) <= 0 )){
            bmiLabel = "Obese Class 1"
            bmiDescription = "Obese Class 1 , eat Healthy Food"
        } else if(bmi.compareTo(34.9f) > 0 && (bmi.compareTo(39.9f) <= 0 )){
            bmiLabel = "Obese Class 2"
            bmiDescription = "Obese Class 2, Eat Healthy and Do Workout"
        } else if(bmi.compareTo(39.9f) > 0){
            bmiLabel = "Obese Class 3"
            bmiDescription = "Obese Class 3, Eat Healthy and Do Workout"
        } else {
            // Provide default values as a safeguard
            bmiLabel = "Unknown"
            bmiDescription = "Please consult a healthcare provider for more information."
        }

        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2,RoundingMode.HALF_EVEN).toString()
        binding?.tvBMI?.text = bmiValue
        binding?.tvResultType?.text = bmiLabel
        binding?.tvBMIDescription?.text = bmiDescription
    }


    private fun makeVisibleMetricUnitsView(){
        currentVisibleView = METRIC_UNITS_VIEW
        binding?.tilMetricUnitWeight?.visibility = View.VISIBLE
        binding?.tilMetricUnitHeight?.visibility = View.VISIBLE
        binding?.tilUsMetricUnitWeight?.visibility = View.GONE
        binding?.tilUsMetricUnitHeightFeet?.visibility = View.GONE
        binding?.tilUsMetricUnitHeightInch?.visibility = View.GONE

        binding?.etMetricUnitWeight?.text!!.clear()
        binding?.etMetricUnitHeight?.text!!.clear()

        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE
    }

    private fun makeVisibleUsUnitsView(){
        currentVisibleView = US_UNITS_VIEW
        binding?.tilMetricUnitWeight?.visibility = View.INVISIBLE
        binding?.tilMetricUnitHeight?.visibility = View.INVISIBLE
        binding?.tilUsMetricUnitWeight?.visibility = View.VISIBLE
        binding?.tilUsMetricUnitHeightFeet?.visibility = View.VISIBLE
        binding?.tilUsMetricUnitHeightInch?.visibility = View.VISIBLE

        binding?.etUsMetricUnitWeight?.text!!.clear()
        binding?.etUsMetricUnitHeightFeet?.text!!.clear()
        binding?.etUsMetricUnitHeightInch?.text!!.clear()

        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE
    }

    private fun validateMetricUnits() : Boolean {
        var isValid = true

        if(binding?.etMetricUnitWeight?.text.toString().isEmpty()){
            isValid = false
        }
        else if(binding?.etMetricUnitHeight?.text.toString().isEmpty()){
            isValid = false
        }

        return isValid
    }


    private fun validateUsUnits() : Boolean {
        var isValid = true

        when{
            binding?.etUsMetricUnitWeight?.text.toString().isEmpty() -> {
                isValid = false
            }

            binding?.etUsMetricUnitHeightInch?.text.toString().isEmpty() -> {
                isValid = false
            }

            binding?.etUsMetricUnitHeightFeet?.text.toString().isEmpty() -> {
                isValid = false
            }

        }
        return isValid
    }

    private fun calculateUnits(){
        if(currentVisibleView == METRIC_UNITS_VIEW){
            if(validateMetricUnits()){
                val heightValue : Float = binding?.etMetricUnitHeight?.text.toString().toFloat() / 100  // in Meters
                val weightValue : Float = binding?.etMetricUnitWeight?.text.toString().toFloat()  // in KG

                val bmi = weightValue / (heightValue * heightValue)
                displayBmiResult(bmi)

            }else {
                Toast.makeText(this@BmiActivity,"Please Enter Valid numbers in the fields",
                    Toast.LENGTH_SHORT).show()
            }
        } else {
            if(validateUsUnits()){
                val usUnitHeightValueFeet : String  = binding?.etUsMetricUnitHeightFeet?.text.toString()
                val usUnitHeightValueInch : String = binding?.etUsMetricUnitHeightInch?.text.toString()
                val usUnitWeightValue : Float = binding?.etUsMetricUnitWeight?.text.toString().toFloat()

                val heightValue =
                    usUnitHeightValueInch.toFloat() + usUnitHeightValueFeet.toFloat() * 12

                val bmi = 703 * (usUnitWeightValue / (heightValue * heightValue))
                displayBmiResult(bmi)
            } else {
                Toast.makeText(this@BmiActivity,"Please Enter Valid numbers in the fields",
                    Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}



