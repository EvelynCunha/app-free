package com.example.freela.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.freela.R

class ForgotSucessActivity : AppCompatActivity() {

    private lateinit var forgotSucessButtonBack: ImageView
    private lateinit var forgotSucessButtonLogin: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_sucess)

        forgotSucessButtonBack = findViewById(R.id.forgot_Sucess_Button_Back)
        forgotSucessButtonLogin = findViewById(R.id.sucess_Button_Login)

        forgotSucessButtonBack.setOnClickListener {
            finish()
        }

        forgotSucessButtonLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}