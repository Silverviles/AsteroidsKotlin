package com.mad.platformer2d

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix

class Player(var posX: Int, var posY: Int, internal val size: Int) {
    private val borderSize = 10

    fun draw(canvas: Canvas, bitmap: Bitmap) {
        val matrix = Matrix().apply {
            postScale(0.4f, 0.4f)
        }

        val scaledBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        canvas.drawBitmap(scaledBitmap, posX.toFloat() - 50, posY.toFloat() - 200, null)
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
