package com.feiyatsu.tipcalculatorapp

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.feiyatsu.tipcalculatorapp.databinding.ActivityMainBinding
import java.math.RoundingMode
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
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
    }

    private fun attachListeners() {
        binding.sbTip.setOnSeekBarChangeListener(seekBarChangeListener)
        binding.btCalculate.setOnClickListener {
            val billAmount = roundNumUsingDecimalFormat(binding.etPrice.text.toString()).toFloat()
            val tipPercent = binding.sbTip.progress.toFloat() / 100
            val peopleAmount = binding.etPerson.text.toString().toInt()

            val tipTotal = billAmount * tipPercent
            val tipEach = roundNumUsingDecimalFormat((tipTotal / peopleAmount).toString())
            val total = billAmount + tipTotal
            val totalEach = roundNumUsingDecimalFormat((total / peopleAmount).toString())

            // Display
            binding.answerTip.text = roundNumUsingDecimalFormat(tipTotal.toString())
            binding.answerTipEach.text = roundNumUsingDecimalFormat(tipEach)
            binding.answerTotal.text = roundNumUsingDecimalFormat(total.toString())
            binding.answerTotalEach.text = roundNumUsingDecimalFormat(totalEach)

//            println("tipTotal = $tipTotal, tipEach = $tipEach, total = $total, totalEach = $totalEach")
        }
    }

    private fun roundNumUsingDecimalFormat(num: String): String {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING

        return df.format(num.toFloat())
    }
}