package com.karan.notesagain.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import com.karan.notesagain.MainActivity
import com.karan.notesagain.R
import com.karan.notesagain.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    private lateinit var binder: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binder = DataBindingUtil.setContentView(this@SplashScreen, R.layout.activity_splash_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fadeIn = AnimationUtils.loadAnimation(this@SplashScreen, R.anim.fade_in)
        binder.card.startAnimation(fadeIn)

        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                Intent(this@SplashScreen, MainActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
    }
}