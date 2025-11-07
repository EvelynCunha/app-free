package com.example.freela.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.freela.R
import com.example.freela.domain.usecase.LoginUseCase
import com.example.freela.repository.auth.AuthRepository
import com.example.freela.databinding.ActivityLoginBinding
import com.example.freela.viewModel.LoginViewModel
import com.example.freela.viewModel.LoginViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LoginActivity : ComponentActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(LoginUseCase(AuthRepository(FirebaseAuth.getInstance())))
    }

    private var debounceJob: Job? = null

    companion object {
        private const val USER_NOT_FOUND = "Usuário não encontrado"
        private const val WRONG_PASSWORD = "Senha incorreta"
        private const val WRONG_EMAIL = "Email inválido"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            debounceJob?.cancel()
            debounceJob = lifecycleScope.launch {
                delay(500) // debounce
                handleLogin()
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.login(email, password)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
                binding.btnLogin.isEnabled = !isLoading
            }
        }

        lifecycleScope.launch {
            viewModel.loginResult.collect { result ->
                result?.onSuccess {
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                }?.onFailure { e ->
                    val message = when ((e as? FirebaseAuthException)?.errorCode) {
                        "ERROR_USER_NOT_FOUND" -> USER_NOT_FOUND
                        "ERROR_WRONG_PASSWORD" -> WRONG_PASSWORD
                        "ERROR_INVALID_EMAIL" -> WRONG_EMAIL
                        else -> getString(R.string.unexpected_error)
                    }

                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
