package com.example.freela.domain.usecase
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

// Máscara de data — formato dd/MM/yyyy
fun TextInputEditText.addDateMask() {
    this.addTextChangedListener(object : TextWatcher {
        private var isUpdating = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isUpdating) return
            isUpdating = true

            val clean = s.toString().replace(Regex("[^\\d]"), "")
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

            val clean = s.toString().replace(Regex("[^\\d]"), "")
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

// Máscara de telefone — formato (00) 00000-0000
fun TextInputEditText.addPhoneMask() {
    this.addTextChangedListener(object : TextWatcher {
        private var isUpdating = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isUpdating) return
            isUpdating = true

            val clean = s.toString().replace(Regex("[^\\d]"), "")
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

