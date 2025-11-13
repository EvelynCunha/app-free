package com.example.freela.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freela.R
import com.example.freela.presentation.addCepMask
import com.example.freela.presentation.dialogs.BottomSheetAdapter
import com.example.freela.repository.response.CityResponse
import com.example.freela.repository.response.StateResponse
import com.example.freela.viewModel.RegisterAddressViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText

class RegisterAddressActivity : AppCompatActivity() {

    private val viewModel: RegisterAddressViewModel by viewModels()

    private lateinit var buttonAddressNext: AppCompatButton
    private lateinit var inputAddressCep: TextInputEditText
    private lateinit var inputAddressEndereco: TextInputEditText
    private lateinit var inputAddressNumero: TextInputEditText
    private lateinit var inputAddressComplemento: TextInputEditText
    private lateinit var inputAddressBairro: TextInputEditText
    private lateinit var inputAddressCidade: TextView
    private lateinit var inputAddressEstado: TextView
    private lateinit var registerAddressImgBack: ImageView
    private lateinit var progressBar: ProgressBar

    private var cepValido = false
    private var email: String? = null
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_address)

        // --- Recebe o email e o nome da tela anterior ---
        email = intent.getStringExtra(RegisterPasswordActivity.EXTRA_EMAIL)
        name = intent.getStringExtra(RegisterPasswordActivity.EXTRA_NOME)
        if (email.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.error_email_missing), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // --- Bindings ---
        buttonAddressNext = findViewById(R.id.button_Next_Address)
        inputAddressCep = findViewById(R.id.register_Edit_Cep)
        inputAddressEndereco = findViewById(R.id.register_Edit_Address)
        inputAddressNumero = findViewById(R.id.register_Edit_Number)
        inputAddressComplemento = findViewById(R.id.register_Edit_Complement)
        inputAddressBairro = findViewById(R.id.register_Edit_District)
        inputAddressCidade = findViewById(R.id.register_Edit_City)
        inputAddressEstado = findViewById(R.id.register_Edit_State)
        registerAddressImgBack = findViewById(R.id.register_Address_Back)
        progressBar = findViewById(R.id.progressBar)

        setupObservers()
        registerAddressImgBack.setOnClickListener { finish() }

        // --- Filtros e máscara do CEP ---
        aplicarMascaraECepFilter()
        configurarBuscaCep()

        inputAddressEstado.setOnClickListener { viewModel.buscarEstados() }

        inputAddressCidade.setOnClickListener {
            val uf = inputAddressEstado.text.toString()
            if (uf.isEmpty()) {
                showAlert("Selecione primeiro o estado.")
            } else {
                viewModel.buscarCidades(uf)
            }
        }

        buttonAddressNext.setOnClickListener {
            val cep = inputAddressCep.text.toString().replace("-", "").trim()
            when {
                cep.length < 8 -> showAlert("Digite os 8 dígitos do CEP.")
                !cepValido -> showAlert("CEP inválido, tente novamente.")
                else -> {
                    viewModel.validarCampos(
                        inputAddressCep.text.toString(),
                        inputAddressEndereco.text.toString(),
                        inputAddressNumero.text.toString(),
                        inputAddressCidade.text.toString(),
                        inputAddressEstado.text.toString()
                    )
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun aplicarMascaraECepFilter() {
        val numbersAndDashFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.isEmpty()) return@InputFilter source
            val filtered = source.filter { it.isDigit() || it == '-' }
            if (filtered.length != source.length) {
                Toast.makeText(this, "Caractere não permitido!", Toast.LENGTH_SHORT).show()
            }
            filtered.toString()
        }

        inputAddressCep.filters = arrayOf(numbersAndDashFilter)
        inputAddressCep.addCepMask()
/*
        inputAddressCep.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true

                val clean = s.toString().replace(Regex("[^\\d]"), "")
                val formatted = StringBuilder()
                val digits = if (clean.length > 8) clean.substring(0, 8) else clean

                for (i in digits.indices) {
                    formatted.append(digits[i])
                    if (i == 4 && i < digits.length - 1) formatted.append("-")
                }

                val masked = formatted.toString()
                inputAddressCep.setText(masked)
                inputAddressCep.setSelection(masked.length)
                isUpdating = false
            }
        })
         */
    }



    private fun configurarBuscaCep() {
        inputAddressCep.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val cep = s.toString().replace("-", "").trim()
                if (cep.length == 8) {
                    viewModel.buscarCep(cep)
                } else {
                    cepValido = false
                }
            }
        })
    }

    private fun setupObservers() {
        viewModel.error.observe(this) { message ->
            if (!message.isNullOrBlank()) showAlert(message)
        }

        viewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.viaCepData.observe(this) { endereco ->
            if (endereco != null && !endereco.cep.isNullOrEmpty()) {
                cepValido = true
                inputAddressEndereco.setText(endereco.logradouro ?: "")
                inputAddressBairro.setText(endereco.bairro ?: "")
                inputAddressCidade.text = endereco.localidade ?: ""
                inputAddressEstado.text = endereco.uf ?: ""

                inputAddressCidade.isEnabled = false
                inputAddressEstado.isEnabled = false
                inputAddressCidade.alpha = 0.6f
                inputAddressEstado.alpha = 0.6f

            } else {
                cepValido = false
                showAlert("CEP inválido, tente novamente.")
                inputAddressCidade.isEnabled = true
                inputAddressEstado.isEnabled = true
                inputAddressCidade.alpha = 1f
                inputAddressEstado.alpha = 1f
            }
        }

        viewModel.allValid.observe(this) { valid ->
            if (valid) {
                val intent = Intent(this, RegisterPasswordActivity::class.java)
                intent.putExtra(RegisterPasswordActivity.EXTRA_EMAIL, email)
                intent.putExtra(RegisterPasswordActivity.EXTRA_NOME, name)
                startActivity(intent)
            }
        }

        viewModel.estados.observe(this) { estados -> showBottomSheetEstados(estados) }
        viewModel.cidades.observe(this) { cidades -> showBottomSheetCidades(cidades) }
    }

    private fun showBottomSheetEstados(estados: List<StateResponse>) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottomsheet_list, null)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = BottomSheetAdapter(estados.map { it.sigla }) { estadoSelecionado ->
            inputAddressEstado.text = estadoSelecionado
            inputAddressCidade.text = ""
            dialog.dismiss()
        }
        dialog.setContentView(view)
        dialog.show()
    }

    private fun showBottomSheetCidades(cidades: List<CityResponse>) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottomsheet_list, null)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = BottomSheetAdapter(cidades.map { it.nome }) { cidadeSelecionada ->
            inputAddressCidade.text = cidadeSelecionada
            dialog.dismiss()
        }
        dialog.setContentView(view)
        dialog.show()
    }

    private fun showAlert(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Erro de validação")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
