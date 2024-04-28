package com.mad.platformer2d

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class GamePanel(context: Context) : SurfaceView(context), SurfaceHolder.Callback, View.OnTouchListener {
    private val gameThread: GameThread
    private val asteroids: MutableList<Asteroid> = CopyOnWriteArrayList()
    private val bullets: MutableList<Bullet> = CopyOnWriteArrayList()
    private val handler = Handler(Looper.getMainLooper())
    private var player: Player? = null

    init {
        holder.addCallback(this)
        gameThread = GameThread(holder, this)
        isFocusable = true
        setOnTouchListener(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameThread.running = true
        gameThread.start()
        player = Player(width / 2, height - 100, 100)
        startAsteroidSpawningThread()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        gameThread.running = false
        while (retry) {
            try {
                gameThread.join()
                retry = false
            } catch (e: InterruptedException) {
                // Retry if interrupted
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Handle surface changes if needed
    }

    private fun startAsteroidSpawningThread() {
        Thread {
            while (gameThread.running) {
                spawnSquare()
                Thread.sleep(2000)
            }
        }.start()
    }

    private fun spawnSquare() {
        val newAsteroid = Asteroid(Random().nextInt(width), 0, 50)
        asteroids.add(newAsteroid)
    }

    fun update() {
        for (asteroid in asteroids) {
            asteroid.moveDown()
            if (asteroid.posY >= height) {
                asteroids.remove(asteroid)
            }
        }
        checkCollisions()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawColor(Color.BLACK)

        val paint = Paint()
        paint.color = Color.WHITE

        for (asteroid in asteroids) {
            canvas.drawRect(
                asteroid.posX.toFloat(),
                asteroid.posY.toFloat(),
                (asteroid.posX + asteroid.size).toFloat(),
                (asteroid.posY + asteroid.size).toFloat(),
                paint
            )
        }
        player?.draw(canvas)

        for (bullet in bullets) {
            bullet.draw(canvas)
        }
    }

    fun delay(milliseconds: Long, runnable: () -> Unit) {
        handler.postDelayed(runnable, milliseconds)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                player?.updatePosition(event.x.toInt(), width)
                fireBullet()
            }
        }
        return true
    }

    private fun fireBullet() {
        val playerX = player?.posX ?: return
        val playerSize = player?.size ?: return
        val bullet = Bullet(playerX + playerSize / 2, player!!.posY, 10) // Adjust speed as needed
        bullets.add(bullet)
    }

    fun updateBullets() {
        val bulletsToRemove = mutableListOf<Bullet>() // Collect bullets to remove
        for (bullet in bullets) {
            bullet.moveUp()
            if (bullet.isOutOfScreen(height)) {
                bulletsToRemove.add(bullet) // Add bullet to remove list
            } else {
                // Check collision with asteroids
                val bulletRect = Rect(bullet.posX, bullet.posY, bullet.posX + bullet.size, bullet.posY + bullet.size)
                for (asteroid in asteroids) {
                    val asteroidRect = Rect(asteroid.posX, asteroid.posY, asteroid.posX + asteroid.size, asteroid.posY + asteroid.size)
                    if (bullet.intersects(asteroidRect)) {
                        // Remove both bullet and asteroid on collision
                        bulletsToRemove.add(bullet) // Add bullet to remove list
                        asteroids.remove(asteroid)
                        break // No need to check further asteroids
                    }
                }
            }
        }

        // Remove bullets from the list
        bullets.removeAll(bulletsToRemove)
    }

    private fun checkCollisions() {
        val playerRect = Rect(player!!.posX, player!!.posY, player!!.posX + player!!.size, player!!.posY + player!!.size)
        val iterator = asteroids.iterator()
        while (iterator.hasNext()) {
            val asteroid = iterator.next()
            val asteroidRect = Rect(asteroid.posX, asteroid.posY, asteroid.posX + asteroid.size, asteroid.posY + asteroid.size)
            if (playerRect.intersect(asteroidRect)) {
                // Collision detected, remove the player
                player = null
                // Optionally, you can also remove the asteroid here
                iterator.remove()
                // You may want to handle other aspects of the game state here, such as game over logic
            }
        }
    }
}
