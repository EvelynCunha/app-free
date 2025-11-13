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

class ForgotCheckActivity : AppCompatActivity() {

    private lateinit var forgotCheckButtonBack: ImageView
    private lateinit var forgotCheckButtonVerify: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_check)

        forgotCheckButtonBack = findViewById(R.id.forgot_Check_Button_Back)
        forgotCheckButtonVerify = findViewById(R.id.check_Button_Verify)

        forgotCheckButtonBack.setOnClickListener {
            finish()
        }

        forgotCheckButtonVerify.setOnClickListener {
            val intent = Intent(this, ForgotResetActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}