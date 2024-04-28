package com.mad.platformer2d

class Asteroid(var posX: Int, var posY: Int, val size: Int) {
    fun moveDown() {
        posY++
    }
}
