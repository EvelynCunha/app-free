package com.example.freela.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.freela.R
import com.google.firebase.auth.FirebaseAuth
import com.example.freela.repository.auth.AuthRepository
import com.example.freela.databinding.FragmentHomeBinding
import com.example.freela.domain.usecase.GetUserNameUseCase
import com.example.freela.viewModel.HomeViewModel
import com.example.freela.viewModel.HomeViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        val auth = FirebaseAuth.getInstance()
        val authRepository = AuthRepository(auth)
        val getUserNameUseCase = GetUserNameUseCase(authRepository)
        HomeViewModelFactory(getUserNameUseCase)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadUserName()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userName.collectLatest { name ->
                    val displayName = name ?: getString(R.string.default_user_name)
                    binding.textHelloUser.text = getString(R.string.hello_user, displayName)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
