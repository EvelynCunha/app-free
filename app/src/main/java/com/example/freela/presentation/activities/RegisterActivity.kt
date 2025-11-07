package com.example.freela.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.freela.R
import com.example.freela.domain.usecase.ValidateBirthDateUseCase
import com.example.freela.domain.usecase.ValidateCpfUseCase
import com.example.freela.domain.usecase.ValidateEmailUseCase
import com.example.freela.domain.usecase.ValidateNameUseCase
import com.example.freela.domain.usecase.ValidatePhoneUseCase
import com.example.freela.domain.usecase.addCpfMask
import com.example.freela.domain.usecase.addDateMask
import com.example.freela.domain.usecase.addPhoneMask
import com.google.android.material.textfield.TextInputEditText
import com.example.freela.viewModel.RegisterViewModel
import com.example.freela.viewModel.RegisterViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlin.getValue

class RegisterActivity : AppCompatActivity() {

    private lateinit var buttonDataNext: AppCompatButton
    private lateinit var inputDataName: TextInputEditText
    private lateinit var inputDataNascimento: TextInputEditText
    private lateinit var inputDataCpf: TextInputEditText
    private lateinit var inputDataEmail: TextInputEditText
    private lateinit var inputDataConfirme: TextInputEditText
    private lateinit var inputDataTelefone: TextInputEditText
    private lateinit var registerDataBack: ImageView
    private lateinit var checkBox: CheckBox

    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(
            ValidateNameUseCase(),
            ValidateBirthDateUseCase(),
            ValidateCpfUseCase(),
            ValidateEmailUseCase(),
            ValidatePhoneUseCase()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        buttonDataNext = findViewById(R.id.buttonNextData)
        inputDataName = findViewById(R.id.registerEditNome)
        inputDataNascimento = findViewById(R.id.registerEditNascimento)
        inputDataCpf = findViewById(R.id.registerEditCpf)
        inputDataTelefone = findViewById(R.id.registerEditTelefone)
        inputDataEmail = findViewById(R.id.dataEditEmail)
        inputDataConfirme = findViewById(R.id.dataConfirmeEmail)
        registerDataBack = findViewById(R.id.registerDataBack)
        checkBox = findViewById(R.id.checkBox)


        inputDataNascimento.addDateMask()
        inputDataCpf.addCpfMask()
        inputDataTelefone.addPhoneMask()

        registerDataBack.setOnClickListener {
            finish()
        }

        buttonDataNext.setOnClickListener {
            val name = inputDataName.text.toString()
            val birthDate = inputDataNascimento.text.toString()
            val numberCpf = inputDataCpf.text.toString()
            val email = inputDataEmail.text.toString()
            val confirmarEmail = inputDataConfirme.text.toString()
            val phone = inputDataTelefone.text.toString()
            val isChecked = checkBox.isChecked

            viewModel.isErrorValid(name, birthDate, numberCpf, email, confirmarEmail, phone, isChecked)

            // Verifica email antes de prosseguir
            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val methods = task.result?.signInMethods
                        if (!methods.isNullOrEmpty()) {
                            Toast.makeText(this, "Este e-mail já está cadastrado. Tente outro ou faça login.", Toast.LENGTH_LONG).show()
                        } else {
                            // email não existe, o observer vai disparar
                            viewModel.allValid.value?.let { isValid ->
                                if (isValid) {
                                    val intent = Intent(this, RegisterAddressActivity::class.java)
                                    intent.putExtra(RegisterPasswordActivity.EXTRA_EMAIL, email)
                                    intent.putExtra("extra_name", name)
                                    intent.putExtra("extra_birthdate", birthDate)
                                    intent.putExtra("extra_cpf", numberCpf)
                                    intent.putExtra("extra_phone", phone)
                                    startActivity(intent)
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Erro ao verificar e-mail. Tente novamente.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Observers ficam fora do clique
        viewModel.errorInput.observe(this) { value ->
            val alert = AlertDialog.Builder(this)
                .setTitle("Erros nos seguintes campos:")
                .setMessage(value)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .setCancelable(false)
            alert.show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}