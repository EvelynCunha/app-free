package com.example.freela.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.freela.R
import com.example.freela.presentation.activities.LoginActivity
import com.example.freela.presentation.activities.RegisterActivity

class MainActivity : AppCompatActivity() {

    private lateinit var buttonRegister: AppCompatButton
    private lateinit var buttonLogin: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        buttonRegister = findViewById(R.id.signupButton)
        buttonLogin = findViewById(R.id.loginButton)

        buttonRegister.setOnClickListener {

            //Para iniciar uma tela partindo de outra tela, utiliza-se o intent
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        buttonLogin.setOnClickListener {
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