package com.example.exercise_thread

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Thread.currentThread
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var isIncrement = false
    val mHandler = Handler(Looper.getMainLooper())
    private val DELAY = 80L
    var mValue = 0
    private var bg_Thread = Thread()
    private var thread = Thread {
        mHandler.postDelayed(bg_Thread, 80)
    }

    private fun pauseThread() {
        //synchronized(this) {}
        mHandler.removeCallbacksAndMessages(null)
        currentThread().interrupt()
        if (!thread.isInterrupted) {
            thread.interrupt()
        }
    }

    private fun resumeThread() {
        //synchronized(this) {}
        thread = Thread(bg_Thread)
        mHandler.postDelayed({ thread.start() }, 1000)
    }

    @SuppressLint("ClickableViewAccessibility", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mHandler2 = Handler(Looper.getMainLooper())
        //chang_color: 5s
        Thread(object : Runnable {
            override fun run() {
                mHandler2.postDelayed({
                    if (tvCount.text.toString().toInt() != 0) {
                        val random = Random()
                        tvCount.setTextColor(
                            Color.argb(
                                255, random.nextInt(256), random.nextInt(256),
                                random.nextInt(256)
                            )
                        )
                    }
                    mHandler2.post(this)
                }, 3200)
            }
        }).start()
        bg_Thread = Thread(object : Runnable {
            override fun run() {
                mHandler.post {
                    mValue = tvCount.text.toString().toInt()
                    if (mValue > 0) {
                        mValue--
                    } else if (mValue < 0) mValue++
                    tvCount.text = mValue.toString()
                }
                if (mValue != 0)
                    mHandler.postDelayed(this, DELAY)
                else mHandler.removeCallbacks(this)
            }
        })

        btnDown.setOnClickListener {
            mValue--
            tvCount.text = mValue.toString()
        }
        btnUp.setOnClickListener {
            mValue++
            tvCount.text = mValue.toString()
        }
        var a: Int = 0
        layoutTouch.setOnTouchListener { view, motionEvent ->
            Log.d(TAG, "onCreate: ${motionEvent.actionMasked}")
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    pauseThread()
                    a = motionEvent.y.toInt()
                }
                MotionEvent.ACTION_MOVE -> if (motionEvent.y >= a) {
                    mValue = tvCount.text.toString().toInt() - 1
                    tvCount.text = mValue.toString()
                } else {
                    mValue = tvCount.text.toString().toInt() + 1
                    tvCount.text = mValue.toString()
                }
                MotionEvent.ACTION_UP -> resumeThread()
            }
            true
        }
        val ruble = object : Runnable {
            override fun run() {
                if (isIncrement)
                    mValue++
                else mValue--
                tvCount.text = mValue.toString()
                mHandler.postDelayed(this, DELAY)
            }

        }
        btnUp.setOnTouchListener { view, motionEvent ->

            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    btnUp.setBackgroundColor(Color.GREEN)
                    pauseThread()
                    isIncrement = true
                    mHandler.postDelayed(ruble, DELAY)
                }
                MotionEvent.ACTION_UP -> {
                    btnUp.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.teal_200))
                    isIncrement = false
                    mHandler.removeCallbacks(ruble)
                    resumeThread()
                }
            }
            false
        }

        btnDown.setOnTouchListener { view, motionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    btnDown.setBackgroundColor(Color.GREEN)
                    pauseThread()
                    isIncrement = false
                    mHandler.postDelayed(ruble, DELAY)
                }
                MotionEvent.ACTION_UP -> {
                    btnDown.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.teal_200))
                    isIncrement = true
                    mHandler.removeCallbacks(ruble)
                    resumeThread()
                }
            }
            false
        }
    }


}