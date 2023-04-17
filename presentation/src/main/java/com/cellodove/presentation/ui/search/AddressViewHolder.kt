package com.cellodove.presentation.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cellodove.domain.model.DomainAddresses
import com.cellodove.presentation.databinding.AddressListItemBinding

class AddressViewHolder(private val binding : AddressListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(domainAddresses: DomainAddresses){
        showData(domainAddresses)
    }

    private fun showData(domainAddresses: DomainAddresses){
        binding.apply {
            roadAddress.text = domainAddresses.roadAddress
            jibunAddress.text = domainAddresses.jibunAddress
        }
    }

    companion object{
        fun create(parent : ViewGroup) : AddressViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            return AddressViewHolder(AddressListItemBinding.inflate(layoutInflater,parent,false))
        }
    }
}