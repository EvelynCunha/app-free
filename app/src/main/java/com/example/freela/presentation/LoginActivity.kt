package com.example.freela.presentation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.freela.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener { finish() }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.btnEntrar.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        val numbersOnlyFilter = InputFilter { source, _, _, _, _, _ ->
            val filtered = source.filter { it.isDigit() }
            if (filtered.length != source.length) {
                Toast.makeText(this, "Caractere não permitido!", Toast.LENGTH_SHORT).show()
            }
            filtered
        }

        binding.inputCpf.editText?.filters = arrayOf(numbersOnlyFilter)

        // TextWatcher para formatar CPF enquanto digita, tstar se tá funcionando
        binding.inputCpf.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true

                val digits = s?.toString()?.replace(Regex("[^\\d]"), "") ?: ""
                val length = digits.length
                val builder = StringBuilder()

                for (i in 0 until length) {
                    builder.append(digits[i])
                    if (i == 2 || i == 5) builder.append('.')
                    if (i == 8) builder.append('-')
                }

                val formatted = builder.toString()
                binding.inputCpf.editText?.setText(formatted)
                binding.inputCpf.editText?.setSelection(formatted.length.coerceAtMost(formatted.length))
                isUpdating = false
            }
        })
    }
}
