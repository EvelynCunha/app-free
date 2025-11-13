package com.example.freela.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.freela.R

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var forgotButtonBack: ImageView
    private lateinit var forgotButtonNext: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        forgotButtonBack = findViewById(R.id.forgot_Button_Back)
        forgotButtonNext = findViewById(R.id.forgot_Button_Next)

        forgotButtonBack.setOnClickListener {
            finish()
        }
        forgotButtonNext.setOnClickListener {
            val intent = Intent(this, ForgotCheckActivity::class.java)
            startActivity(intent)
        }

    }
}

