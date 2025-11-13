package com.example.freela.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.freela.R
import com.example.freela.databinding.ActivityPasswordBinding
import com.example.freela.domain.usecase.RegisterPasswordUseCase
import com.example.freela.repository.auth.AuthRepository
import com.example.freela.viewModel.RegisterPasswordState
import com.example.freela.viewModel.RegisterPasswordViewModel
import com.example.freela.viewModel.RegisterPasswordViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterPasswordActivity : ComponentActivity() {

    private lateinit var binding: ActivityPasswordBinding
    private val viewModel: RegisterPasswordViewModel by viewModels {
        RegisterPasswordViewModelFactory(
            RegisterPasswordUseCase(AuthRepository(FirebaseAuth.getInstance()))
        )
    }

    private var debounceJob: Job? = null

    companion object {
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_NOME = "extra_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra(EXTRA_EMAIL)
        if (email.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.error_email_missing), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // botão voltar funcional - testar se está funcionando
        binding.registerPasswordBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupUi(email)
        observeViewModel()
    }

    private fun setupUi(email: String) {
        binding.registerEditPassword.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.registerEditConfirmPassword.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        binding.registerEditPassword.doOnTextChanged { _, _, _, _ -> updateSaveButtonState() }
        binding.registerEditConfirmPassword.doOnTextChanged { _, _, _, _ -> updateSaveButtonState() }

        binding.registerEditConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val p = binding.registerEditPassword.text?.toString().orEmpty()
                val c = binding.registerEditConfirmPassword.text?.toString().orEmpty()
                if (p.isNotEmpty() && c.isNotEmpty() && p != c) {
                    binding.registerInputConfirmPassword.error = getString(R.string.passwords_do_not_match)
                } else {
                    binding.registerInputConfirmPassword.error = null
                }
            }
        }

        binding.buttonNextPassword.setOnClickListener {
            val password = binding.registerEditPassword.text?.toString().orEmpty()
            val confirmPassword = binding.registerEditConfirmPassword.text?.toString().orEmpty()
            val name = intent.getStringExtra("extra_name").orEmpty()

            if (password != confirmPassword) {
                Toast.makeText(
                    this,
                    getString(R.string.passwords_do_not_match),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            debounceJob?.cancel()
            debounceJob = lifecycleScope.launch {
                delay(400)
                binding.registerInputConfirmPassword.error = null
                viewModel.register(email, password, confirmPassword, name)
            }
        }


        // alterna visibilidade da senha principal
        binding.registerInputPassword.setEndIconOnClickListener {
            val editText = binding.registerEditPassword
            if (editText.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                editText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                editText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            editText.setSelection(editText.text?.length ?: 0)
        }

        // p alternar visibilidade da confirmação
        binding.registerInputConfirmPassword.setEndIconOnClickListener {
            val editText = binding.registerEditConfirmPassword
            if (editText.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                editText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                editText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            editText.setSelection(editText.text?.length ?: 0)
        }

        updateSaveButtonState()
    }

    private fun updateSaveButtonState() {
        val p = binding.registerEditPassword.text?.toString().orEmpty()
        val c = binding.registerEditConfirmPassword.text?.toString().orEmpty()
        binding.buttonNextPassword.isEnabled = p.isNotEmpty() && c.isNotEmpty()
    }


    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is RegisterPasswordState.Idle -> {
                        binding.progressBar?.visibility = View.GONE
                        binding.buttonNextPassword.isEnabled = true
                    }
                    is RegisterPasswordState.Loading -> {
                        binding.progressBar?.visibility = View.VISIBLE
                        binding.buttonNextPassword.isEnabled = false
                    }
                    is RegisterPasswordState.Success -> {
                        binding.progressBar?.visibility = View.GONE
                        Toast.makeText(
                            this@RegisterPasswordActivity,
                            getString(R.string.register_success),
                            Toast.LENGTH_SHORT
                        ).show()

                        val name = intent.getStringExtra("extra_name")
                        val user = FirebaseAuth.getInstance().currentUser
                        val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                            displayName = name
                        }
                        user?.updateProfile(profileUpdates)
                        startActivity(Intent(this@RegisterPasswordActivity, PaymentActivity::class.java))
                        finish()
                    }
                    is RegisterPasswordState.Error -> {
                        binding.progressBar?.visibility = View.GONE
                        binding.buttonNextPassword.isEnabled = true

                        val msg = state.message
                        val toShow = when {
                            msg.contains("already in use") -> getString(R.string.error_email_in_use)
                            msg.contains("As senhas não conferem") -> getString(R.string.passwords_do_not_match)
                            msg.contains("Senha fraca") -> getString(R.string.weak_password)
                            else -> getString(R.string.unexpected_error)
                        }

                        binding.registerInputConfirmPassword.error = null
                        Toast.makeText(this@RegisterPasswordActivity, toShow, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

private fun TextInputEditText.doOnTextChanged(action: (text: CharSequence?, start: Int, before: Int, count: Int) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            action(s, start, before, count)
        }
        override fun afterTextChanged(s: Editable?) {}
    })
}
