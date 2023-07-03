package com.example.geoquiz

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: Button
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView
    private lateinit var previousButton: Button
    private lateinit var gradingTextView: TextView
    private lateinit var cheatAttemptsTextView: TextView
    // Add the following constants at the top of the MainActivity class


    companion object {
        const val EXTRA_GRADING_SCORE = "com.example.geoquiz.extra.GRADING_SCORE"
        const val EXTRA_CHEATING_ATTEMPTS = "com.example.geoquiz.extra.CHEATING_ATTEMPTS"
    }


    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    private var totalQuestionsAnswered: Int = 0
    private var gradingScore = 0
    private var cheatAttempts = 2
    private lateinit var cheatViewModel: CheatViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        cheatViewModel = ViewModelProvider(this).get(CheatViewModel::class.java)

        if (savedInstanceState != null) {
            gradingScore = savedInstanceState.getInt(EXTRA_GRADING_SCORE, 0)
            cheatAttempts = savedInstanceState.getInt(EXTRA_CHEATING_ATTEMPTS, 2)

        }

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        val isCheater = savedInstanceState?.getBoolean(KEY_IS_CHEATER, false) ?: false


        Log.i("TEST", savedInstanceState?.getBoolean(KEY_IS_CHEATER).toString())

        quizViewModel.isCheater = isCheater
        quizViewModel.currentIndex = currentIndex

        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        cheatButton = findViewById(R.id.cheat_button)
        previousButton = findViewById(R.id.previous_button)
        gradingTextView = findViewById(R.id.grading_text_view)
        cheatAttemptsTextView = findViewById(R.id.cheating_attempts_text_view)

        questionTextView = findViewById(R.id.question_text_view)

        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
            updateButton()
            updateQuestion()

        }
        falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
            updateButton()
            updateQuestion()
        }

        nextButton.setOnClickListener { view: View ->
            quizViewModel.moveToNext()
            updateQuestion()
            updateButton()
        }

        cheatButton.setOnClickListener {
            if (cheatAttempts > 0) {
                cheatAttempts--
                val answerIsTrue = quizViewModel.currentQuestionAnswer
                val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            } else {
                Toast.makeText(this, "No cheat attempts left", Toast.LENGTH_SHORT).show()
            }
            updateQuestion()
            updateButton()
            updateCheatAttemptsText()
        }


        previousButton.setOnClickListener { view: View ->
            quizViewModel.moveToBack()
            updateQuestion()
            updateButton()
        }

        updateQuestion()

    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        outState.putInt(EXTRA_GRADING_SCORE, gradingScore)
        outState.putInt(EXTRA_CHEATING_ATTEMPTS, cheatAttempts)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    /**
     * Dispatch onPause() to fragments.
     */
    override fun onPause() {
        super.onPause()
        quizViewModel.isCheater = true
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are *not* resumed.
     */
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            if (data?.getBooleanExtra(EXTRA_CHEATED, false) == true) {
                quizViewModel.isCheater = true
            }
        }
    }


    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        if (quizViewModel.isCheater) {
            if (userAnswer == correctAnswer) {
                Toast.makeText(this, R.string.judgment_toast, Toast.LENGTH_SHORT).show()
                quizViewModel.cheatingAttempts++
                quizViewModel.isCheater = false
            } else {
                Toast.makeText(this, R.string.judgment_toast, Toast.LENGTH_SHORT).show()
                quizViewModel.isCheater = false
            }
        } else {
            val messageResId = when {
                userAnswer == correctAnswer -> {
                    gradingScore++
                    totalQuestionsAnswered++
                    R.string.correct_toast
                }
                else -> {
                    totalQuestionsAnswered++
                    R.string.incorrect_toast
                }
            }
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()


        }

        quizViewModel.currentQuestionButton = false

        val nextIndex = quizViewModel.currentIndex + 1
        if (nextIndex < quizViewModel.questionBank.size) {
            quizViewModel.currentIndex = nextIndex
            gradingTextView.text = "Grading Score: $gradingScore"
            updateCheatAttemptsText()
            updateButton()
            updateQuestion()
        } else {
            // All questions answered, navigate to FinishActivity
            startFinishActivity(gradingScore, quizViewModel.cheatingAttempts)

        }
    }




    private fun updateCheatAttemptsText() {
        cheatAttemptsTextView.text = "Cheat Attempts Left: $cheatAttempts"
    }




    private fun startFinishActivity(gradingScore: Int, cheatingAttempts: Int) {
        val intent = Intent(this, FinishActivity::class.java)
        intent.putExtra(FinishActivity.EXTRA_GRADING_SCORE, gradingScore)
        intent.putExtra(FinishActivity.EXTRA_CHEATING_ATTEMPTS, cheatingAttempts)
        intent.putExtra(FinishActivity.EXTRA_TOTAL_QUESTIONS, totalQuestionsAnswered)
        startActivity(intent)
        finish()
    }



    private fun updateButton(){

        if (quizViewModel.currentQuestionButton){
            cheatButton.isEnabled = true
            trueButton.isEnabled = true
            falseButton.isEnabled = true

        }else{

            cheatButton.isEnabled = false
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        }
        updateQuestion()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }




}





