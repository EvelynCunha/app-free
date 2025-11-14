package com.example.freela.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.freela.R
import com.example.freela.viewModel.CardPayViewModel

class CardPayBackFragment : Fragment() {

    private lateinit var viewModel: CardPayViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_card_pay_back, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textExpiry = view.findViewById<TextView>(R.id.text_Card_Expiry)
        val textCvv = view.findViewById<TextView>(R.id.text_Card_Cvv)

        viewModel = ViewModelProvider(requireActivity())[CardPayViewModel::class.java]

        viewModel.cardExpiry.observe(viewLifecycleOwner) { expiry ->
            textExpiry.text = if (expiry.isNullOrBlank()) "MM/AA" else expiry
        }
        viewModel.cardCvv.observe(viewLifecycleOwner) { cvv ->
            textCvv.text = if (cvv.isNullOrBlank()) "• • •" else cvv
        }
    }
}
