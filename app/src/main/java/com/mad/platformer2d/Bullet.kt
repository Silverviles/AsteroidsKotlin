package com.mad.platformer2d

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

class Bullet(internal var posX: Int, internal var posY: Int, private val speed: Int) {
    internal val size = 10

    fun moveUp() {
        posY -= speed
    }

    fun draw(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.RED
        }
        canvas.drawRect(
            posX.toFloat(),
            posY.toFloat(),
            (posX + size).toFloat(),
            (posY + size).toFloat(),
            paint
        )
    }

    fun isOutOfScreen(screenHeight: Int): Boolean {
        return posY + size < 0
    }

    fun intersects(rect: Rect): Boolean {
        return Rect(posX, posY, posX + size, posY + size).intersect(rect)
    }
}