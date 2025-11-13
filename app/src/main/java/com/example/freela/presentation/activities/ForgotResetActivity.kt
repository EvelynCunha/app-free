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

class ForgotResetActivity : AppCompatActivity() {

    private lateinit var forgotResetButtonBack: ImageView
    private lateinit var forgotResetButtonRedefine: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_reset)


        forgotResetButtonBack = findViewById(R.id.forgot_Reset_Button_Back)
        forgotResetButtonRedefine = findViewById(R.id.reset_Button_Redefine)

        forgotResetButtonBack.setOnClickListener {
            finish()
        }

        forgotResetButtonRedefine.setOnClickListener {
            val intent = Intent(this, ForgotSucessActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}