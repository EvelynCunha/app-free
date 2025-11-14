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

class CardPayFrontFragment : Fragment() {

    private lateinit var viewModel: CardPayViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_card_pay_front, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textType = view.findViewById<TextView>(R.id.text_Card_Type)
        val textName = view.findViewById<TextView>(R.id.text_Card_Name)
        val textNumber = view.findViewById<TextView>(R.id.text_Card_Number)

        viewModel = ViewModelProvider(requireActivity())[CardPayViewModel::class.java]

        viewModel.cardType.observe(viewLifecycleOwner) { type ->
            textType.text = type.ifEmpty { "Débito" } // exemplo: placeholder
        }
        viewModel.cardName.observe(viewLifecycleOwner) { name ->
            textName.text = if (name.isNullOrBlank()) "Seu nome" else name
        }
        viewModel.cardNumber.observe(viewLifecycleOwner) { number ->
            textNumber.text = if (number.isNullOrBlank()) "•••• •••• •••• ••••" else number
        }
    }
}
