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

class RegisterAddressActivity : AppCompatActivity() {

    private lateinit var buttonAddressNext: AppCompatButton
    private lateinit var inputAddressCep: TextInputEditText
    private lateinit var inputAddressEndereco: TextInputEditText
    private lateinit var inputAddressNumero: TextInputEditText
    private lateinit var inputAddressBairro: TextInputEditText
    private lateinit var inputAddressCidade: TextInputEditText
    private lateinit var inputAddressEstado: TextInputEditText
    private lateinit var registerAddressImgBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_address)

        buttonAddressNext =findViewById(R.id.buttonNextAddress)
        inputAddressCep = findViewById(R.id.registerEditCep)
        inputAddressEndereco = findViewById(R.id.registerEditEndereco)
        inputAddressNumero = findViewById(R.id.registerEditNumero)
        inputAddressBairro = findViewById(R.id.registerEditBairro)
        inputAddressCidade = findViewById(R.id.registerEditCidade)
        inputAddressEstado = findViewById(R.id.registerEditEstado)
        registerAddressImgBack = findViewById(R.id.registerAddressBack)

        registerAddressImgBack.setOnClickListener {
            finish()
        }

        buttonAddressNext.setOnClickListener {
            val intent = Intent(this, PasswordActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}