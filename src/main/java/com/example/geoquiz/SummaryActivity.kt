package com.example.geoquiz

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SummaryActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_TOTAL_QUESTIONS = "com.example.geoquiz.extra.TOTAL_QUESTIONS"
        const val EXTRA_GRADING_SCORE = "com.example.geoquiz.extra.GRADING_SCORE"
        const val EXTRA_CHEATING_ATTEMPTS = "com.example.geoquiz.extra.CHEATING_ATTEMPTS"
    }

    private lateinit var totalQuestionsTextView: TextView
    private lateinit var gradingScoreTextView: TextView
    private lateinit var cheatingAttemptsTextView: TextView
    private lateinit var previousButton: Button
    private lateinit var shareScoreButton: Button


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        totalQuestionsTextView = findViewById(R.id.total_questions_text_view)
        gradingScoreTextView = findViewById(R.id.grading_score_text_view)
        cheatingAttemptsTextView = findViewById(R.id.cheating_attempts_text_view)
        previousButton = findViewById(R.id.previous_button)
        shareScoreButton = findViewById(R.id.share_score_button)

        val totalQuestionsAnswered = intent.getIntExtra(EXTRA_TOTAL_QUESTIONS, 0)
        val gradingScore = intent.getIntExtra(EXTRA_GRADING_SCORE, 0)
        val cheatingAttempts = intent.getIntExtra(EXTRA_CHEATING_ATTEMPTS, 0)

        totalQuestionsTextView.text = "Total Questions Answered: $totalQuestionsAnswered"
        gradingScoreTextView.text = "Total Score: $gradingScore"
        cheatingAttemptsTextView.text = "Total Cheat Attempts: $cheatingAttempts"


        // Calculate the percentage score
        val percentageScore = (gradingScore.toDouble() / 6) * 100

        val formattedScore = String.format("%.2f", percentageScore)
        // Display the percentage score using Toast
        val message = "Quiz Score: $formattedScore%"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        previousButton.setOnClickListener { view: View ->
            super.onBackPressed()
        }

        shareScoreButton.setOnClickListener { view: View ->
            shareScore(formattedScore)
        }
    }

    private fun shareScore(score: String) {
        // Create an intent to share the score
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Quiz Score")
        intent.putExtra(Intent.EXTRA_TEXT, "I scored $score% on the quiz!")
        startActivity(Intent.createChooser(intent, "Share via"))
    }
}
