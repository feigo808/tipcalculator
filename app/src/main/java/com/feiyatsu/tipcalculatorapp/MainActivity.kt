package com.feiyatsu.tipcalculatorapp

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.feiyatsu.tipcalculatorapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val EMPTY_STRING = ""
    }

    private lateinit var binding: ActivityMainBinding

    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        @SuppressLint("SetTextI18n")
        override fun onProgressChanged(seekBar: SeekBar?, percent: Int, fromUser: Boolean) {
            binding.tvTipPercentage.text = "$percent %"

            val tipDescription = when (percent) {
                in 0..9 -> getString(R.string.tipDescription_poor)
                in 10..14 -> getString(R.string.tipDescription_acceptable)
                in 15..19 -> getString(R.string.tipDescription_good)
                in 20..24 -> getString(R.string.tipDescription_great)
                else -> getString(R.string.tipDescription_amazing)
            }
            binding.tvTipDescription.text = tipDescription

            val descriptionColor = ArgbEvaluator().evaluate(
                percent.toFloat() / binding.sbTip.max,
                ContextCompat.getColor(this@MainActivity, R.color.worstTip),
                ContextCompat.getColor(this@MainActivity, R.color.bestTip)
            ) as Int
            binding.tvTipDescription.setTextColor(descriptionColor)
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {}
        override fun onStopTrackingTouch(p0: SeekBar?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        attachListeners()
        addWebView()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        currentFocus?.let { currFocus ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currFocus.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun addWebView() {
        val message: String =
            "*** Icon Info: <a href=\"https://www.flaticon.com/free-icons/tip\" title=\"tip icons\">Tip icons created by Freepik - Flaticon</a> ***"
        val mimeType: String = "text/html"
        val utfType: String = "UTF-8"
        binding.wvIconInfo.loadData(message, mimeType, utfType)
    }

    private fun attachListeners() {
        binding.sbTip.setOnSeekBarChangeListener(seekBarChangeListener)
        binding.btnCalculate.setOnClickListener {

            println("billAmount= " + binding.etPrice.text.toString())
            println("people= " + binding.etPerson.text.toString())


            val billAmount =
                if (binding.etPrice.text.toString() == EMPTY_STRING) 0.00f
                else binding.etPrice.text.toString().toFloat()
            val tipPercent = binding.sbTip.progress.toFloat() / 100
            val peopleAmount =
                if (binding.etPerson.text.toString() == EMPTY_STRING) 1
                else binding.etPerson.text.toString().toInt()

            val tipTotal = billAmount * tipPercent
            val tipEach = tipTotal / peopleAmount
            val total = billAmount + tipTotal
            val totalEach = total / peopleAmount

            // Display
            binding.answerTip.text = roundNum(tipTotal)
            binding.answerTipEach.text = roundNum(tipEach)
            binding.answerTotal.text = roundNum(total)
            binding.answerTotalEach.text = roundNum(totalEach)
        }
        binding.btnClear.setOnClickListener {
            binding.etPrice.setText(EMPTY_STRING)
            binding.etPerson.setText(EMPTY_STRING)
            binding.sbTip.progress = 0
            binding.etPrice.requestFocus()
        }
    }

    private fun roundNum(num: Float): String {
        return String.format("%.2f", num)
    }
}

