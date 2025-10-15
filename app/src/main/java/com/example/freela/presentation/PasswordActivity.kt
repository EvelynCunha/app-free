package com.example.freela.presentation

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.freela.R
import com.google.android.material.textfield.TextInputEditText

class PasswordActivity : AppCompatActivity() {

    private lateinit var registerPasswordSenha: TextInputEditText
    private lateinit var registerPasswordConfirmeSenha: TextInputEditText
    private lateinit var registerPasswordEmail: TextInputEditText
    private lateinit var registerPasswordConfirmeEmail: TextInputEditText
    private lateinit var registerPasswordBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_password)

        registerPasswordSenha = findViewById(R.id.registerEditSenha)
        registerPasswordConfirmeSenha = findViewById(R.id.registerEditConfirmarSenha)
        registerPasswordEmail = findViewById(R.id.registerEditEmail)
        registerPasswordConfirmeEmail = findViewById(R.id.registerEditConfirmarEmail)
        registerPasswordBack = findViewById(R.id.registerPasswordBack)

        registerPasswordBack.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}