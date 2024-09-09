package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Variables to control when operations and decimals can be added
    private var canAddOperation = false
    private var canAddDecimal = true

    // ViewBinding instance for accessing views
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Set the content view to the root view of the binding
        setContentView(binding.root)
    }

    // Function called when a number button is pressed
    fun numberAction(view: View) {
        if (view is Button) {
            // Append decimal if allowed
            if (view.text == ".") {
                if (canAddDecimal) {
                    binding.workingsTV.append(view.text)
                    canAddDecimal = false
                }
            } else {
                // Append the number
                binding.workingsTV.append(view.text)
            }
            // Allow operations to be added after a number
            canAddOperation = true
        }
    }

    // Function called when the backspace button is pressed
    fun backSpaceAction(view: View) {
        val length = binding.workingsTV.length()
        if (length > 0) {
            binding.workingsTV.text = binding.workingsTV.text.subSequence(0, length - 1)
        }
    }

    // Function called when an operation button is pressed
    fun operationAction(view: View) {
        if (view is Button && canAddOperation) {
            binding.workingsTV.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    // Function called when the all clear (AC) button is pressed
    fun allClearAction(view: View) {
        binding.workingsTV.text = ""
        binding.resultsTV.text = ""
    }

    // Function called when the equal (=) button is pressed
    fun equalAction(view: View) {
        binding.resultsTV.text = calculateResults()
    }

    // Function to calculate the results
    private fun calculateResults(): String {
        // Extract digits and operators from the input
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) return ""

        // Handle multiplication and division first
        val timesDivision = timesDivisionCalculate(digitsOperators)
        if (timesDivision.isEmpty()) return ""

        // Handle addition and subtraction
        val result = addSubtractCalculate(timesDivision)
        return result.toString()
    }

    // Function to handle addition and subtraction calculations
    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if (operator == '+') result += nextDigit
                if (operator == '-') result -= nextDigit
            }
        }

        return result
    }

    // Function to handle multiplication and division calculations
    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('x') || list.contains('/')) {
            list = calcTimesDiv(list)
        }
        return list
    }

    // Helper function for multiplication and division calculations
    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var i = 0

        while (i < passedList.size) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val prevDigit = newList.removeAt(newList.size - 1) as Float
                val nextDigit = passedList[i + 1] as Float
                when (operator) {
                    'x' -> newList.add(prevDigit * nextDigit)
                    '/' -> newList.add(prevDigit / nextDigit)
                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
                i += 2 // Skip the next digit since it's already processed
            } else {
                newList.add(passedList[i])
                i++
            }
        }
        return newList
    }

    // Function to extract digits and operators from the input text
    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in binding.workingsTV.text) {
            if (character.isDigit() || character == '.') {
                currentDigit += character
            } else {
                if (currentDigit.isNotEmpty()) {
                    list.add(currentDigit.toFloat())
                    currentDigit = ""
                }
                list.add(character)
            }
        }
        if (currentDigit.isNotEmpty()) {
            list.add(currentDigit.toFloat())
        }
        return list
    }
}
