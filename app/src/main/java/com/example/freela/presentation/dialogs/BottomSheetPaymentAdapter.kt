package com.example.freela.presentation.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freela.R
import com.example.freela.viewModel.RegisterPaymentViewModel

class BottomSheetPaymentAdapter(
    private var bankList: List<RegisterPaymentViewModel.BankItem>,
    private val onItemClick: (RegisterPaymentViewModel.BankItem) -> Unit
) : RecyclerView.Adapter<BottomSheetPaymentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameBank: TextView = view.findViewById(R.id.bank_List_View)

        fun bind(item: RegisterPaymentViewModel.BankItem) {
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

    fun updateList(newList: List<RegisterPaymentViewModel.BankItem>) {
        bankList = newList
        notifyDataSetChanged()
    }
}