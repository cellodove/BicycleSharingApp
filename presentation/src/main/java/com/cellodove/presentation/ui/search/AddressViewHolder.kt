package com.cellodove.presentation.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cellodove.domain.data.DomainAddresses
import com.cellodove.presentation.databinding.AddressListItemBinding

class AddressViewHolder(private val binding : AddressListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(domainAddresses: DomainAddresses){
        showData(domainAddresses)
    }

    private fun showData(domainAddresses: DomainAddresses){
        binding.apply {
            roadAddress.text = domainAddresses.roadAddress.toString()
            jibunAddress.text = domainAddresses.jibunAddress.toString()
        }
    }

    companion object{
        fun create(parent : ViewGroup) : AddressViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            return AddressViewHolder(AddressListItemBinding.inflate(layoutInflater,parent,false))
        }
    }
}