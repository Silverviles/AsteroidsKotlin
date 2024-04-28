package com.mad.platformer2d

import android.view.SurfaceHolder

class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GamePanel) : Thread() {
    var running = false

    override fun run() {
        while (running) {
            val canvas = surfaceHolder.lockCanvas()
            if (canvas != null) {
                synchronized(surfaceHolder) {
                    gameView.update()
                    gameView.updateBullets()
                    gameView.draw(canvas)
                }
                surfaceHolder.unlockCanvasAndPost(canvas)
                gameView.delay(500) { }
            }
        }
    }
}
