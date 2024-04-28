package com.mad.platformer2d

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_score)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        saveScoreToLocal(intent.getIntExtra("score", 0))

        val exitMenuButton : Button = findViewById(R.id.exitMenu)
        val exitGameButton : Button = findViewById(R.id.exitGame)
        val highScoreTextView : TextView = findViewById(R.id.high_score)
        val highScore = getHighScoreFromPreferences()
        val score = intent.getIntExtra("score", 0)
        val showScoreTextView = findViewById<TextView>(R.id.show_score)

        highScoreTextView.text = getString((R.string.high_score_text), highScore)
        showScoreTextView.text = getString(R.string.score_text, score)

        exitMenuButton.setOnClickListener{
            val intent = Intent(this, StartScreen::class.java)
            startActivity(intent)
        }
        exitGameButton.setOnClickListener{
            finishAndRemoveTask()
            finishAffinity()
        }
    }

    private fun getHighScoreFromPreferences(): Int {
        val sharedPreferences: SharedPreferences = getSharedPreferences("AsteroidScore", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("highScore", 0)
    }

    private fun saveScoreToLocal(score: Int) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("AsteroidScore", Context.MODE_PRIVATE)
        val currentScore = sharedPreferences.getInt("highScore", 0)

        if (score > currentScore) {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putInt("highScore", score)
            editor.apply()
        }
    }
}