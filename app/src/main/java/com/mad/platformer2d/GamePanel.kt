package com.mad.platformer2d

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import java.util.Random
import java.util.concurrent.CopyOnWriteArrayList

class GamePanel(context: Context) : SurfaceView(context), SurfaceHolder.Callback,
    View.OnTouchListener {
    private val gameThread: GameThread
    private val asteroids: MutableList<Asteroid> = CopyOnWriteArrayList()
    private val bullets: MutableList<Bullet> = CopyOnWriteArrayList()
    private val handler = Handler(Looper.getMainLooper())
    private var player: Player? = null
    private val backgroundBitmap: Bitmap
    private val playerBitMap: Bitmap
    private val asteroidBitMap: Bitmap
    private var score: Int = 0
    private var scorePaint: Paint = Paint().apply {
        Color.WHITE
        textSize = 40f
    }

    init {
        holder.addCallback(this)
        gameThread = GameThread(holder, this, context)
        isFocusable = true
        setOnTouchListener(this)

        backgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.space)
        playerBitMap = BitmapFactory.decodeResource(resources, R.drawable.player)
        asteroidBitMap = BitmapFactory.decodeResource(resources, R.drawable.asteroid)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameThread.running = true
        gameThread.start()
        player = Player(width / 2, height - 400, 300)
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
                spawnAsteroid()
                Thread.sleep(2000)
            }
        }.start()
    }

    private fun spawnAsteroid() {
        val newAsteroid = Asteroid(Random().nextInt(width), 0, 100, asteroidBitMap)
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
        canvas.drawBitmap(backgroundBitmap, 0f, 0f, null)

        val paint = Paint()
        paint.color = Color.WHITE

        val matrix = Matrix()
        val scaleFactor = 0.1f
        matrix.postScale(scaleFactor, scaleFactor)
        val scaledBitmap = Bitmap.createBitmap(
            asteroidBitMap,
            0,
            0,
            asteroidBitMap.width,
            asteroidBitMap.height,
            matrix,
            true
        )

        for (asteroid in asteroids) {
            canvas.drawBitmap(
                scaledBitmap,
                asteroid.posX.toFloat() - 20,
                asteroid.posY.toFloat(),
                null
            )
        }
        player?.draw(canvas, playerBitMap)

        for (bullet in bullets) {
            bullet.draw(canvas)
        }

        scorePaint.color = Color.WHITE
        canvas.drawText("High Score: $score", (width - 300).toFloat(), 100f, scorePaint)
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
        val bullet = Bullet(playerX + playerSize / 2, player!!.posY, 10)
        bullets.add(bullet)
    }

    fun updateBullets() {
        val bulletsToRemove = mutableListOf<Bullet>()
        for (bullet in bullets) {
            bullet.moveUp()
            if (bullet.isOutOfScreen(height)) {
                bulletsToRemove.add(bullet)
            } else {
                Rect(
                    bullet.posX,
                    bullet.posY,
                    bullet.posX + bullet.size,
                    bullet.posY + bullet.size
                )
                for (asteroid in asteroids) {
                    val asteroidRect = Rect(
                        asteroid.posX,
                        asteroid.posY,
                        asteroid.posX + asteroid.size,
                        asteroid.posY + asteroid.size
                    )
                    if (bullet.intersects(asteroidRect)) {
                        bulletsToRemove.add(bullet)
                        asteroids.remove(asteroid)
                        score++
                        break
                    }
                }
            }
        }

        bullets.removeAll(bulletsToRemove)
    }

    private fun checkCollisions() {
        if (player != null) {
            val playerRect = Rect(
                player!!.posX,
                player!!.posY,
                player!!.posX + player!!.size,
                player!!.posY + player!!.size
            )
            val iterator = asteroids.iterator()
            while (iterator.hasNext()) {
                val asteroid = iterator.next()
                val asteroidRect = Rect(
                    asteroid.posX,
                    asteroid.posY,
                    asteroid.posX + asteroid.size,
                    asteroid.posY + asteroid.size
                )
                if (playerRect.intersect(asteroidRect)) {
                    navigateToHighScore()
                    break
                }
            }
        }
    }

    private fun saveScoreToLocal(context: Context, score: Int) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("AsteroidScore", Context.MODE_PRIVATE)
        val currentScore = sharedPreferences.getInt("highScore", 0)

        if (score > currentScore) {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putInt("highScore", score)
            editor.apply()
        }
    }

    private fun navigateToHighScore() {
        val intent = Intent(context, ScoreActivity::class.java)
        intent.putExtra("score", score)
        context.startActivity(intent)

        saveScoreToLocal(context, score)

        player = null
        (context as Activity).finish()
    }
}
