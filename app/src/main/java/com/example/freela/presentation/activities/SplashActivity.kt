package com.example.freela.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.freela.databinding.ActivitySplashBinding
import com.example.freela.presentation.MainActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgLogo.alpha = 0f
        binding.imgLogo.scaleX = 0.8f
        binding.imgLogo.scaleY = 0.8f

        binding.imgLogo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1000)
            .withEndAction {
                binding.imgLogo.animate()
                    .rotationBy(360f)
                    .scaleX(1.4f)
                    .scaleY(1.4f)
                    .alpha(0f)
                    .setDuration(1200)
                    .setStartDelay(300)
                    .withEndAction {
                        startActivity(Intent(this, MainActivity::class.java))
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
            }
    }
}
