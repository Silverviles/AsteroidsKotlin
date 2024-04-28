package com.mad.platformer2d

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Player(internal var posX: Int, internal var posY: Int, internal val size: Int) {
    fun draw(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.GREEN
        }
        canvas.drawRect(
            posX.toFloat(),
            posY.toFloat(),
            (posX + size).toFloat(),
            (posY + size).toFloat(),
            paint
        )
    }

    fun updatePosition(newX: Int, screenWidth: Int) {
        posX = newX - size / 2
        if (posX < 0) {
            posX = 0
        } else if (posX + size > screenWidth) {
            posX = screenWidth - size
        }
    }
}
