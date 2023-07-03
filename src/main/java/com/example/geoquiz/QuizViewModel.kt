package com.example.geoquiz

import android.content.Intent
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {
    val questionBank = listOf(
        Question(R.string.question_australia, true, button = true),
        Question(R.string.question_oceans, true, button = true),
        Question(R.string.question_mideast, false, button = true),
        Question(R.string.question_africa, false, button = true),
        Question(R.string.question_americas, true, button = true),
        Question(R.string.question_asia, true, button = true),
    )

    var currentIndex = 0
    var isCheater = false
    var cheatingAttempts = 0
    var currentQuestionCheated = false
    private var totalQuestionsAnswered = 0



    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer
    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId
    var currentQuestionButton: Boolean
        get() = questionBank[currentIndex].button
        set(j:Boolean) {
            questionBank[currentIndex].button = j
        }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
        if (currentIndex == 0) {
            currentIndex = questionBank.size - 1
        }
    }

    fun moveToBack() {
        if (currentIndex > 0) {
            currentIndex -= 1
        }
    }

    // Function to increment the total questions answered count
    fun incrementTotalQuestionsAnswered() {
        totalQuestionsAnswered++
    }

    // Function to get the total questions answered count
    fun getTotalQuestionsAnswered(): Int {
        return totalQuestionsAnswered
    }

}