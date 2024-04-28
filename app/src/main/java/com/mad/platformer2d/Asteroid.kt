package com.mad.platformer2d

import android.graphics.Bitmap
import android.graphics.Canvas

class Asteroid(var posX: Int, var posY: Int, val size: Int, val asteroidBitmap: Bitmap) {
    fun moveDown() {
        posY+=5
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(asteroidBitmap, posX.toFloat(), posY.toFloat(), null)
    }
}
