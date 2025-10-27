package com.example.freela.domain.usecase

class ValidateCpfUseCase {

    operator fun invoke(cpf: String): Boolean {
        // 1️⃣ Remove qualquer caractere que não seja número
        val cleanCpf = cpf.replace(Regex("[^\\d]"), "")

        // 2️⃣ Verifica se tem exatamente 11 dígitos
        if (cleanCpf.length != 11) return false

        // 3️⃣ Elimina CPFs com todos os dígitos iguais (ex: 11111111111)
        if (cleanCpf.all { it == cleanCpf[0] }) return false

        // 4️⃣ Calcula os dois dígitos verificadores
        val firstNineDigits = cleanCpf.substring(0, 9)
        val firstVerifier = calculateVerifierDigit(firstNineDigits, 10)
        val secondVerifier = calculateVerifierDigit(firstNineDigits + firstVerifier, 11)

        // 5️⃣ Compara com os dígitos informados
        val calculatedCpf = firstNineDigits + firstVerifier + secondVerifier
        return cleanCpf == calculatedCpf
    }

    // 🔢 Função auxiliar para calcular os dígitos verificadores do CPF
    private fun calculateVerifierDigit(cpfPart: String, weightStart: Int): Char {
        var sum = 0
        var weight = weightStart

        for (digitChar in cpfPart) {
            sum += (digitChar.digitToInt() * weight--)
        }

        val remainder = sum % 11
        val result = if (remainder < 2) 0 else 11 - remainder
        return result.digitToChar()
    }
}
