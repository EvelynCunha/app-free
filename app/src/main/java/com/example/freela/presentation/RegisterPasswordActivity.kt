package com.example.freela.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import kotlinx.coroutines.flow.collect
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra(EXTRA_EMAIL)
        if (email.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.error_email_missing), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUi(email)
        observeViewModel()
    }

    private fun setupUi(email: String) {
        binding.registerEditPassword.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.registerEditConfirmPassword.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

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
            debounceJob?.cancel()
            debounceJob = lifecycleScope.launch {
                delay(400)
                binding.registerInputConfirmPassword.error = null
                val password = binding.registerEditPassword.text?.toString().orEmpty()
                val confirmPassword = binding.registerEditConfirmPassword.text?.toString().orEmpty()
                viewModel.register(email, password, confirmPassword)
            }
        }

        updateSaveButtonState()
    }

    private fun updateSaveButtonState() {
        val p = binding.registerEditPassword.text?.toString().orEmpty()
        val c = binding.registerEditConfirmPassword.text?.toString().orEmpty()
        binding.buttonNextPassword.isEnabled = p.isNotEmpty() && c.isNotEmpty() && p == c
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
    this.addTextChangedListener(object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            action(s, start, before, count)
        }
        override fun afterTextChanged(s: android.text.Editable?) {}
    })
}
