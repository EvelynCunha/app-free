package com.example.freela.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.freela.R
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var buttonDataNext: AppCompatButton
    private lateinit var inputDataName: TextInputEditText
    private lateinit var inputDataNascimento: TextInputEditText
    private lateinit var inputDataCpf: TextInputEditText
    private lateinit var inputDataTelefone: TextInputEditText
    private lateinit var registerDataBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        buttonDataNext = findViewById(R.id.buttonNextData)
        inputDataName = findViewById(R.id.registerEditNome)
        inputDataNascimento = findViewById(R.id.registerEditNascimento)
        inputDataCpf = findViewById(R.id.registerEditCpf)
        inputDataTelefone = findViewById(R.id.registerEditTelefone)
        registerDataBack = findViewById(R.id.registerDataBack)

        registerDataBack.setOnClickListener {
            finish()
        }

        buttonDataNext.setOnClickListener {
            val intent = Intent(this, RegisterAddressActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}