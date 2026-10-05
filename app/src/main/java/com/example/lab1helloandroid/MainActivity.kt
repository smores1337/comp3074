package com.example.lab1helloandroid

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var count = 0
    private var step = DEFAULT_STEP

    private lateinit var output: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            count = savedInstanceState.getInt(KEY_COUNT)
            step = savedInstanceState.getInt(KEY_STEP, DEFAULT_STEP)
        }

        output = findViewById(R.id.textViewOutput)
        updateOutput()

        findViewById<Button>(R.id.buttonAdd).setOnClickListener {
            count += step
            updateOutput()
        }

        findViewById<Button>(R.id.buttonSubtract).setOnClickListener {
            count -= step
            updateOutput()
        }

        // Reset the output to zero and return to the default behaviour
        findViewById<Button>(R.id.buttonReset).setOnClickListener {
            count = 0
            step = DEFAULT_STEP
            updateOutput()
        }

        // Change the behaviour to increase/decrease by two
        findViewById<Button>(R.id.buttonStep).setOnClickListener {
            step = STEP_TWO
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_COUNT, count)
        outState.putInt(KEY_STEP, step)
    }

    private fun updateOutput() {
        output.text = count.toString()
    }

    companion object {
        private const val DEFAULT_STEP = 1
        private const val STEP_TWO = 2
        private const val KEY_COUNT = "count"
        private const val KEY_STEP = "step"
    }
}
