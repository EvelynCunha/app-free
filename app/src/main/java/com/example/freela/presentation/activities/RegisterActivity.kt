package com.example.freela.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.freela.R
import com.example.freela.databinding.ActivityRegisterBinding
import com.example.freela.databinding.RegisterBottomSheetBinding
import com.example.freela.domain.usecase.ValidateBirthDateUseCase
import com.example.freela.domain.usecase.ValidateCpfUseCase
import com.example.freela.domain.usecase.ValidateEmailUseCase
import com.example.freela.domain.usecase.ValidateNameUseCase
import com.example.freela.domain.usecase.ValidatePhoneUseCase
import com.example.freela.presentation.addCpfMask
import com.example.freela.presentation.addDateMask
import com.example.freela.presentation.addPhoneMask
import com.google.android.material.textfield.TextInputEditText
import com.example.freela.viewModel.RegisterViewModel
import com.example.freela.viewModel.RegisterViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlin.getValue

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var buttonDataNext: AppCompatButton
    private lateinit var inputDataName: TextInputEditText
    private lateinit var inputDataBirthday: TextInputEditText
    private lateinit var inputDataCpf: TextInputEditText
    private lateinit var inputDataEmail: TextInputEditText
    private lateinit var inputDataConfirm: TextInputEditText
    private lateinit var inputDataPhone: TextInputEditText
    private lateinit var registerDataBack: ImageView
    private lateinit var registerTermBottomSheet : TextView
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
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buttonDataNext = findViewById(R.id.button_Next_Data)
        inputDataName = findViewById(R.id.registerEditNome)
        inputDataBirthday = findViewById(R.id.register_Edit_Birthday)
        inputDataCpf = findViewById(R.id.register_Edit_Cpf)
        inputDataPhone = findViewById(R.id.register_Edit_Phone)
        inputDataEmail = findViewById(R.id.dataEditEmail)
        inputDataConfirm = findViewById(R.id.data_Confirm_Email)
        registerDataBack = findViewById(R.id.register_Data_Back)
        registerTermBottomSheet = findViewById(R.id.register_Term_Bottom_Sheet)
        checkBox = findViewById(R.id.check_Box)


        inputDataBirthday.addDateMask()
        inputDataCpf.addCpfMask()
        inputDataPhone.addPhoneMask()

        // Atualiza o ícone de check conforme o usuário digita
        val layoutEmail = findViewById<TextInputLayout>(R.id.registerInputEmail)
        val layoutConfirm = findViewById<TextInputLayout>(R.id.registerInputConfirmarEmail)

        fun updateEmailEndIcons() {
            val email = inputDataEmail.text.toString()
            val confirm = inputDataConfirm.text.toString()

            // Validação individual
            val emailValid = viewModel.isEmailValid(email)
            val confirmValid = viewModel.isConfirmaEmailValid(confirm, email)

            // Mostra o ícone somente se o campo for válido
            layoutEmail.endIconMode = if (emailValid) TextInputLayout.END_ICON_CUSTOM else TextInputLayout.END_ICON_NONE
            layoutConfirm.endIconMode = if (confirmValid) TextInputLayout.END_ICON_CUSTOM else TextInputLayout.END_ICON_NONE
        }

        inputDataEmail.addTextChangedListener { updateEmailEndIcons() }
        inputDataConfirm.addTextChangedListener { updateEmailEndIcons() }
        updateEmailEndIcons()

        registerDataBack.setOnClickListener {
            finish()
        }

        buttonDataNext.setOnClickListener {
            val name = inputDataName.text.toString()
            val birthDate = inputDataBirthday.text.toString()
            val numberCpf = inputDataCpf.text.toString()
            val email = inputDataEmail.text.toString()
            val confirmEmail = inputDataConfirm.text.toString()
            val phone = inputDataPhone.text.toString()
            val isChecked = checkBox.isChecked

            val allValid = viewModel.isErrorValid(name, birthDate, numberCpf, email, confirmEmail, phone, isChecked)

            if (!allValid) {
                // Já vai mostrar o AlertDialog via observer
                return@setOnClickListener
            }

            // Verifica email antes de prosseguir
            @Suppress("DEPRECATION")
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

        binding.registerTermBottomSheet.setOnClickListener { showBottomSheetDialog() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(this)
        val sheetBinding: RegisterBottomSheetBinding =
            RegisterBottomSheetBinding.inflate(layoutInflater, null, false)

        dialog.setContentView(sheetBinding.root)

        sheetBinding.registerBottomClose.setOnClickListener {
            dialog.dismiss()
        }
        sheetBinding.registerTermButton.setOnClickListener {
            checkBox.isChecked = true
            dialog.dismiss()
        }

        // Permite fechar tocando fora (opcional)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        dialog.show()
    }
}