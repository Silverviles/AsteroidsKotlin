package com.mad.platformer2d

import android.os.Bundle
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gamePanel : GamePanel = GamePanel(this)
        setContentView(gamePanel)
    }
}