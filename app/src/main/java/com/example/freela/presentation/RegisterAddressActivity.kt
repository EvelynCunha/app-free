package com.example.freela.presentation

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

    private var cepValido = false // controla se o CEP foi validado via API, estava deixando passar sem validação via API (testar se está dando certo)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_address)

        // --- Bindings ---
        buttonAddressNext = findViewById(R.id.buttonNextAddress)
        inputAddressCep = findViewById(R.id.register_edit_cep)
        inputAddressEndereco = findViewById(R.id.registerEditEndereco)
        inputAddressNumero = findViewById(R.id.registerEditNumero)
        inputAddressComplemento = findViewById(R.id.registerEditComplemento)
        inputAddressBairro = findViewById(R.id.registerEditBairro)
        inputAddressCidade = findViewById(R.id.registerEditCidade)
        inputAddressEstado = findViewById(R.id.registerEditEstado)
        registerAddressImgBack = findViewById(R.id.register_address_back)
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

    /**
      Aplica máscara + filtro numérico + toast para caracteres inválidos
     */
    private fun aplicarMascaraECepFilter() {
        // Filtro: permite números e o hífen
        val numbersAndDashFilter = InputFilter { source, start, end, dest, dstart, dend ->
            if (source.isEmpty()) return@InputFilter source // permite apagar

            val filtered = source.filter { it.isDigit() || it == '-' }

            // Mostra toast se tentou digitar caractere inválido
            if (filtered.length != source.length) {
                Toast.makeText(this, "Caractere não permitido!", Toast.LENGTH_SHORT).show()
            }
            filtered.toString()
        }

        inputAddressCep.filters = arrayOf(numbersAndDashFilter)


        // Máscara dinâmica (00000-000)
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
                    if (i == 4 && i < digits.length - 1) {
                        formatted.append("-")
                    }
                }

                val masked = formatted.toString()
                inputAddressCep.setText(masked)
                inputAddressCep.setSelection(masked.length)
                isUpdating = false
            }
        })
    }

    /**
      Busca CEP automaticamente quando completo (8 dígitos)
     */
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

    // --- Observers ---
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
            } else {
                cepValido = false
                showAlert("CEP inválido, tente novamente.")
            }
        }

        viewModel.allValid.observe(this) { valid ->
            if (valid) {
                val intent = Intent(this, PasswordActivity::class.java)
                startActivity(intent)
            }
        }

        viewModel.estados.observe(this) { estados -> showBottomSheetEstados(estados) }
        viewModel.cidades.observe(this) { cidades -> showBottomSheetCidades(cidades) }
    }

    // --- Bottom Sheets ---
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
