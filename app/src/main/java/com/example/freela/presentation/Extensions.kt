package com.example.freela.presentation

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText

private val NON_DIGIT_REGEX = Regex("[^\\d]")
private val VALID_INPUT_CHARS = Regex("[^\\dX-]")
private val ACCOUNT_VALIDATION_REGEX = Regex("^\\d{1,8}(-[\\dX])?$")

// Máscara de data — formato dd/MM/yyyy
fun TextInputEditText.addDateMask() {
    this.addTextChangedListener(object : TextWatcher {
        private var isUpdating = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isUpdating) return
            isUpdating = true

            val clean = s.toString().replace(NON_DIGIT_REGEX, "")
            val formatted = StringBuilder()

            for (i in clean.indices) {
                formatted.append(clean[i])
                if (i == 1 || i == 3) formatted.append('/')
            }

            val masked = if (formatted.length > 10) formatted.substring(0, 10) else formatted.toString()
            this@addDateMask.setText(masked)
            this@addDateMask.setSelection(masked.length)
            isUpdating = false
        }
    })
}

// Máscara de CPF — formato 000.000.000-00
fun TextInputEditText.addCpfMask() {
    this.addTextChangedListener(object : TextWatcher {
        private var isUpdating = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isUpdating) return
            isUpdating = true

            val clean = s.toString().replace(NON_DIGIT_REGEX, "")
            val formatted = StringBuilder()

            for (i in clean.indices) {
                formatted.append(clean[i])
                if (i == 2 || i == 5) formatted.append('.')
                if (i == 8) formatted.append('-')
            }

            val masked = if (formatted.length > 14) formatted.substring(0, 14) else formatted.toString()
            this@addCpfMask.setText(masked)
            this@addCpfMask.setSelection(masked.length)
            isUpdating = false
        }
    })
}

// Máscara de phone — (00) 00000-0000
fun TextInputEditText.addPhoneMask() {
    this.addTextChangedListener(object : TextWatcher {
        private var isUpdating = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isUpdating) return
            isUpdating = true

            val clean = s.toString().replace(NON_DIGIT_REGEX, "")
            val formatted = StringBuilder()

            for (i in clean.indices) {
                when (i) {
                    0 -> formatted.append("(").append(clean[i])
                    1 -> formatted.append(clean[i]).append(") ")
                    6 -> formatted.append(clean[i]).append("-")
                    else -> formatted.append(clean[i])
                }
            }

            val masked = if (formatted.length > 15) formatted.substring(0, 15) else formatted.toString()
            this@addPhoneMask.setText(masked)
            this@addPhoneMask.setSelection(masked.length)
            isUpdating = false
        }
    })
}

// Máscara de CEP — 00000-000
fun TextInputEditText.addCepMask() {
    this.addTextChangedListener(object : TextWatcher {
        private var isUpdating = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isUpdating) return
            isUpdating = true

            val clean = s.toString().replace(NON_DIGIT_REGEX, "")
            val formatted = StringBuilder()

            for (i in clean.indices) {
                formatted.append(clean[i]) // 1. Sempre anexa o dígito

                // 2. Verifica se:
                //    a) i é igual a 4 (depois do 5º dígito) E
                //    b) ainda há dígitos após este (para não colocar hífen no final)
                if (i == 4 && i < clean.length - 1) {
                    formatted.append("-") // 3. Anexa o hífen
                }
            }

            val masked = if (formatted.length > 9) formatted.substring(0, 9) else formatted.toString()
            this@addCepMask.setText(masked)
            this@addCepMask.setSelection(masked.length)
            isUpdating = false
        }
    })
}

/**
 * Máscara para Número de Conta: 8 dígitos + hífen + 1 caractere final (dígito ou X/x).
 * Formato esperado: XXXXXXXX-Y
 */
fun TextInputEditText.applyAccountMask() {
    filters = arrayOf(InputFilter.LengthFilter(10)) // 8 dígitos + '-' + 1 DV

    addTextChangedListener(object : TextWatcher {
        private var isUpdating = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isUpdating) return
            isUpdating = true

            val input = s.toString().uppercase()

            // 🔹 Remove tudo que não for número, X ou hífen
            var clean = input.replace(VALID_INPUT_CHARS, "")

            // 🔹 Garante no máximo 1 hífen
            if (clean.count { it == '-' } > 1) {
                clean = clean.replaceFirst("-", "")
            }

            // 🔹 Se tiver hífen, corta qualquer coisa após 1 caractere do hífen
            if (clean.contains("-")) {
                val parts = clean.split("-")
                val before = parts.getOrNull(0)?.take(8).orEmpty() // até 8 dígitos antes
                val after = parts.getOrNull(1)?.take(1).orEmpty()  // só 1 caractere depois
                clean = "$before-$after"
            } else if (clean.length > 8) {
                // adiciona hífen automático se já passou de 8 dígitos
                clean = clean.substring(0, 8) + "-" + clean.substring(8, 10.coerceAtMost(clean.length))
            }

            // Atualiza apenas se o texto mudou
            if (clean != input) {
                setText(clean)
                setSelection(clean.length)
            }

            // 🔹 Valida formato (1–9 dígitos) + opcional "-" + (1 dígito ou X)
            val isValidFormat = clean.matches(ACCOUNT_VALIDATION_REGEX)
            error = if (isValidFormat || clean.isEmpty()) null
            else "Formato inválido. Ex: 12345678-9"

            isUpdating = false
        }
    })
}

