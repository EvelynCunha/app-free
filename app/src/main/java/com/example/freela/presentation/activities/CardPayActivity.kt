package com.example.freela.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import com.example.freela.R
import com.example.freela.databinding.ActivityCardPayBinding
import com.example.freela.presentation.fragments.CardPayBackFragment
import com.example.freela.presentation.fragments.CardPayFrontFragment
import com.example.freela.viewModel.CardPayViewModel
import androidx.core.widget.addTextChangedListener
import com.example.freela.presentation.addCardNumberMask
import com.example.freela.presentation.addDateValidityMask

class CardPayActivity : AppCompatActivity() {

    private lateinit var viewModel: CardPayViewModel
    private var showingFront = true
    private lateinit var binding: ActivityCardPayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCardPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(CardPayViewModel::class.java)

        binding.cardPayButtonBack.setOnClickListener {
            finish()
        }

        binding.cardPayEditCardNumber.addCardNumberMask()
        binding.cardPayEditCardValidity.addDateValidityMask()

        binding.buttonNextCardPay.setOnClickListener {

            Toast.makeText(
                this,
                getString(R.string.cardpay_account_created),
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Fragment inicial (frente)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_Card_Pay, CardPayFrontFragment())
            .commit()

        setupInputs()
    }

    private fun setupInputs() {
        val dropdownTypeCard = binding.dropdownTypeCard
        val cardPayEditName = binding.cardPayEditName
        val cardPayEditCardNumber = binding.cardPayEditCardNumber
        val cardPayEditCardValidity = binding.cardPayEditCardValidity
        val cardPayEditCardCvv = binding.cardPayEditCardCvv

        // Dropdown adapter
        val adapter = ArrayAdapter(
            this,
            R.layout.item_dropdown_option,
            listOf(
                getString(R.string.cardpay_type_card_debit),
                getString(R.string.cardpay_type_card_credit)
            )
        )
        dropdownTypeCard.setAdapter(adapter)

        // Observa mudanças e atualiza ViewModel
        dropdownTypeCard.addTextChangedListener { viewModel.updateCardType(it.toString()) }
        cardPayEditName.addTextChangedListener { viewModel.updateCardName(it.toString()) }
        cardPayEditCardNumber.addTextChangedListener { viewModel.updateCardNumber(it.toString()) }
        cardPayEditCardValidity.addTextChangedListener { viewModel.updateCardExpiry(it.toString()) }
        cardPayEditCardCvv.addTextChangedListener { viewModel.updateCardCvv(it.toString()) }

        // Quando o usuário focar em validade ou CVV → mostra o verso
        cardPayEditCardValidity.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && showingFront) flipCard()
        }

        cardPayEditCardCvv.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && showingFront) flipCard()
        }

        // Quando o usuário voltar a focar em campos da frente → volta o cartão
        cardPayEditName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !showingFront) flipCard()
        }

        cardPayEditCardNumber.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !showingFront) flipCard()
        }

        dropdownTypeCard.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !showingFront) flipCard()
        }
    }

    private fun flipCard() {
        val nextFragment = if (showingFront) CardPayBackFragment() else CardPayFrontFragment()
        val transaction = supportFragmentManager.beginTransaction()

        val enter = if (showingFront) R.animator.card_flip_right_in else R.animator.card_flip_left_in
        val exit = if (showingFront) R.animator.card_flip_right_out else R.animator.card_flip_left_out

        transaction.setCustomAnimations(enter, exit)
        transaction.replace(R.id.fragment_Card_Pay, nextFragment)
        transaction.commit()

        showingFront = !showingFront
    }
}
