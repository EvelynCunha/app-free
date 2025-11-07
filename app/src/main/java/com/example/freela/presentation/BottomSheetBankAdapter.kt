package com.example.freela.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freela.R
import com.example.freela.viewModel.RegisterBankViewModel

class BottomSheetBankAdapter(
    private var bankList: List<RegisterBankViewModel.BankItem>,
    private val onItemClick: (RegisterBankViewModel.BankItem) -> Unit
) : RecyclerView.Adapter<BottomSheetBankAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameBank: TextView = view.findViewById(R.id.bank_List_View)

        fun bind(item: RegisterBankViewModel.BankItem) {
            nameBank.text = item.displayName
            itemView.setOnClickListener {
                if (item.code != 0) onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bank_name_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bankList[position])
    }

    override fun getItemCount(): Int = bankList.size

    fun updateList(newList: List<RegisterBankViewModel.BankItem>) {
        bankList = newList
        notifyDataSetChanged()
    }
}
