package com.example.geoquiz

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class FinishActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_GRADING_SCORE = "com.example.geoquiz.extra.GRADING_SCORE"
        const val EXTRA_CHEATING_ATTEMPTS = "com.example.geoquiz.extra.CHEATING_ATTEMPTS"
        const val EXTRA_TOTAL_QUESTIONS = "com.example.geoquiz.extra.TOTAL_QUESTIONS"
    }

    private lateinit var gradingTextView: TextView
    private lateinit var cheatingAttemptsTextView: TextView
    private lateinit var resetButton: Button

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        gradingTextView = findViewById(R.id.grading_text_view)
        cheatingAttemptsTextView = findViewById(R.id.cheating_attempts_text_view)
        resetButton = findViewById(R.id.reset_button)

        val gradingScore = intent.getIntExtra(EXTRA_GRADING_SCORE, 0)
        val cheatingAttempts = intent.getIntExtra(EXTRA_CHEATING_ATTEMPTS, 0)
        val totalQuestions = intent.getIntExtra(EXTRA_TOTAL_QUESTIONS, 0)

        gradingTextView.text = getString(R.string.grading_score, gradingScore)
        cheatingAttemptsTextView.text = getString(R.string.cheating_attempts, cheatingAttempts)

        resetButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(MainActivity.EXTRA_GRADING_SCORE, 0) // Reset grading score
            intent.putExtra(MainActivity.EXTRA_CHEATING_ATTEMPTS, 2) // Reset cheating attempts
            startActivity(intent)
            finishAffinity()
        }

        val summaryButton: Button = findViewById(R.id.summary_button)
        summaryButton.setOnClickListener {
            // Start SummaryActivity to show the summary
            val intent = Intent(this, SummaryActivity::class.java)
            intent.putExtra(SummaryActivity.EXTRA_TOTAL_QUESTIONS, totalQuestions)
            intent.putExtra(SummaryActivity.EXTRA_GRADING_SCORE, gradingScore)
            intent.putExtra(SummaryActivity.EXTRA_CHEATING_ATTEMPTS, cheatingAttempts)
            startActivity(intent)
        }
    }
}
