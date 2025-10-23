package com.example.freela.domain.usecase

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ValidateBirthDateUseCase {

    private val LENGTH = 10
    operator fun invoke(date: String): Boolean {
        // 1️⃣ Verifica se o campo foi preenchido
        if (date.isBlank()) return false
        if (date.length != LENGTH ) return false

        return try {
            // 2️⃣ Tenta converter a string em uma data válida no formato brasileiro
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false // evita aceitar datas inválidas, como 32/13/2022
            val birthDate: Date = sdf.parse(date) ?: return false

            // 3️⃣ Cria instâncias de calendário para comparar idades
            val birthCalendar = Calendar.getInstance().apply { time = birthDate }
            val today = Calendar.getInstance()

            // 4️⃣ Calcula a idade
            var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

            // Se ainda não fez aniversário neste ano, diminui 1
            if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--
            }

            // 5️⃣ Retorna verdadeiro apenas se tiver 18 anos ou mais
            age >= 18
        } catch (e: Exception) {
            false
        }
    }
}
