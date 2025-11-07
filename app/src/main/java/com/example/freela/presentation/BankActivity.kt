package com.example.freela.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freela.R
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import com.example.freela.databinding.ActivityBankBinding
import com.example.freela.databinding.CustomBottomSheetBinding
import com.example.freela.presentation.adapter.BottomSheetBankAdapter
import com.example.freela.viewModel.RegisterBankViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import androidx.core.widget.doOnTextChanged
import com.example.freela.viewModel.RegisterBankViewModel.ValidationError
import com.google.android.material.textfield.TextInputLayout

class BankActivity : AppCompatActivity() {

    private val viewModel: RegisterBankViewModel by viewModels()

    private lateinit var registerBankBack: ImageView
    private lateinit var layoutBankName: TextInputLayout

    private lateinit var bankInputEditListBank: TextView
    private lateinit var bankInputAgency: TextInputEditText
    private lateinit var bankInputAccount: TextInputEditText
    private lateinit var bankInputTypeAccount: AutoCompleteTextView
    private lateinit var bankInputPix: TextInputEditText
    private lateinit var bankButtonNext: AppCompatButton
    private lateinit var binding: ActivityBankBinding
    private lateinit var layoutAgency: TextInputLayout
    private lateinit var layoutAccount: TextInputLayout
    private lateinit var layoutAccountType: TextInputLayout
    private lateinit var layoutPix: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObservers()
        binding = ActivityBankBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bank)
        setContentView(binding.root)

// --- Bindings ---
        registerBankBack = findViewById(R.id.bank_Data_Back)
        bankInputEditListBank = findViewById(R.id.bank_Edit_Name_Bank)
        bankInputAgency = findViewById(R.id.bank_Edit_Agency)
        bankInputAccount = findViewById(R.id.bank_Edit_Account)
        bankInputTypeAccount = findViewById(R.id.dropdown_Account_Type)
        bankInputPix = findViewById(R.id.bank_Edit_Pix)
        bankButtonNext = findViewById(R.id.bank_Button_Next)

        layoutBankName = findViewById(R.id.bank_Input_Name_Bank)
        layoutAgency = findViewById(R.id.bank_Input_Agency)
        layoutAccount = findViewById(R.id.bank_Input_Account)
        layoutAccountType = findViewById(R.id.bank_Input_Account_Type)
        layoutPix = findViewById(R.id.bank_Input_Pix)

        binding.bankEditAccount.applyAccountMask()

        registerBankBack.setOnClickListener {
            finish()
        }

        val bankAccountType = listOf(getString(R.string.bank_account_type_current), getString(R.string.bank_account_type_savings))

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bankAccountType)

        binding.dropdownAccountType.setAdapter(adapter)

        bankButtonNext.setOnClickListener {

            viewModel.validateFields(
                bankInputEditListBank.text.toString(),
                bankInputAgency.text.toString(),
                bankInputAccount.text.toString(),
                bankInputTypeAccount.text.toString(),
                bankInputPix.text.toString()
            )
        }

        binding.bankEditNameBank.setOnClickListener {
            viewModel.seekBanks()
        }
        viewModel.banks.observe(this) { lista ->
            showButtonSheetDialog(lista)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun setupObservers() {
        // 1. Observa a lista de erros
        viewModel.errorsList.observe(this) { errorsList ->
            if (errorsList.isNotEmpty()) {
                showFieldErrors(errorsList)
            }
        }

        viewModel.isButtonEnabled.observe(this) { enabled ->
            bankButtonNext.isEnabled = enabled
            bankButtonNext.alpha = if (enabled) 1f else 0.5f // visual feedback
        }


        viewModel.allValid.observe(this) { valid ->
            if (valid) {
                val intent = Intent(this, PaymentActivity::class.java)
                startActivity(intent)
            } else {
                // Remove erros em tempo real
                bankInputEditListBank.doOnTextChanged { text, _, _, _ ->
                    if (!text.isNullOrBlank()) layoutBankName.error = null
                    checkFieldsToEnableButton()
                }

                bankInputAgency.doOnTextChanged { text, _, _, _ ->
                    if (!text.isNullOrBlank()) layoutAgency.error = null
                    checkFieldsToEnableButton()
                }

                bankInputAccount.doOnTextChanged { text, _, _, _ ->
                    if (!text.isNullOrBlank() &&
                        text.matches(Regex("^\\d{1,8}(-[\\dX])?$"))
                    ) layoutAccount.error = null
                    checkFieldsToEnableButton()
                }

                bankInputTypeAccount.doOnTextChanged { text, _, _, _ ->
                    if (!text.isNullOrBlank()) layoutAccountType.error = null
                    checkFieldsToEnableButton()
                }

                bankInputPix.doOnTextChanged { text, _, _, _ ->
                    if (!text.isNullOrBlank()) layoutPix.error = null
                    checkFieldsToEnableButton()
                }
            }
        }
    }
    private fun checkFieldsToEnableButton() {
        val hasError = listOf(
            layoutBankName.error,
            layoutAgency.error,
            layoutAccount.error,
            layoutAccountType.error,
            layoutPix.error
        ).any { it != null }

        val allFilled = listOf(
            bankInputEditListBank.text,
            bankInputAgency.text,
            bankInputAccount.text,
            bankInputTypeAccount.text,
            bankInputPix.text
        ).all { !it.isNullOrBlank() }

        bankButtonNext.isEnabled = allFilled && !hasError
        bankButtonNext.alpha = if (bankButtonNext.isEnabled) 1f else 0.5f
    }

    private fun showFieldErrors(errorsList: List<ValidationError>) {
        // limpa erros anteriores
        listOf(layoutBankName, layoutAgency, layoutAccount, layoutAccountType, layoutPix).forEach {

        it.error = null
            it.isErrorEnabled = false
        }

        // marca os erros específicos
        errorsList.forEach { error ->
            when (error) {
                is ValidationError.EmptyBank -> {
                    layoutBankName.error = getString(R.string.error_empty_bank)
                }
                is ValidationError.EmptyAgency -> {
                    layoutAgency.error = getString(R.string.error_empty_agency)
                }
                is ValidationError.EmptyAccount -> {
                    layoutAccount.error = getString(R.string.error_empty_account)
                }
                is ValidationError.InvalidAccount -> {
                    layoutAccount.error = getString(R.string.error_empty_account)
                }
                is ValidationError.EmptyAccountType -> {
                    layoutAccountType.error = getString(R.string.error_empty_account_type)
                }
                is ValidationError.EmptyPix -> {
                    layoutPix.error = getString(R.string.error_empty_pix)
                }
            }
        }
    }


    private fun showButtonSheetDialog(bankName: List<RegisterBankViewModel.BankItem>) {
        val dialog = BottomSheetDialog(this)
        val sheetBinding = CustomBottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        val recycler = sheetBinding.recyclerViewBankList
        recycler.layoutManager = LinearLayoutManager(this)
        val adapter = BottomSheetBankAdapter(bankName) { bankSelected ->
            binding.bankEditNameBank.text = bankSelected.displayName
            dialog.dismiss()
        }
        recycler.adapter = adapter

        //  Observa o campo de pesquisa
        sheetBinding.textInputEditText.doOnTextChanged { text, _, _, _ ->
            val query = text?.toString()?.trim().orEmpty()

            val filtered = if (query.isEmpty()) {
                bankName
            } else {
                val qInt = query.toIntOrNull()
                bankName.filter { bank ->
                    bank.displayName.contains(query, ignoreCase = true) ||
                            (qInt != null && bank.code == qInt) ||
                            bank.code.toString().contains(query)
                }
            }

            if (filtered.isEmpty()) {
                // Se nada for encontrado, mostra um item "falso"
                adapter.updateList(
                    listOf(
                        RegisterBankViewModel.BankItem(
                            code = 0,
                            name = "",
                            displayName = getString(R.string.bank_list_search_not_find)
                        )
                    )
                )
            } else {
                adapter.updateList(filtered)
            }
        }
        dialog.show()
    }

}