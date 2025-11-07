package com.example.freela.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.example.freela.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Animação :)
        binding.root.postDelayed({
            val moveUp = TranslateAnimation(0f, 0f, 0f, -300f).apply {
                duration = 1000
                fillAfter = true
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {

                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
            }

            binding.imgLogo.startAnimation(moveUp)
        }, 2000)
    }
}
