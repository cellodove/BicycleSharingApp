package com.cellodove.presentation.ui.search

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cellodove.domain.data.DomainAddresses

class SearchAdapter : PagingDataAdapter<DomainAddresses,AddressViewHolder>(ADDRESS_COMPARATOR){
    private lateinit var itemClickListener : OnItemClickListener
    interface OnItemClickListener{
        fun onClick(domainAddresses : DomainAddresses)
    }
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null){
            holder.bind(item)
            holder.itemView.setOnClickListener {
                itemClickListener.onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
       return AddressViewHolder.create(parent)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    companion object{
        private val ADDRESS_COMPARATOR = object : DiffUtil.ItemCallback<DomainAddresses>(){
            override fun areItemsTheSame(
                oldItem: DomainAddresses, newItem: DomainAddresses
            ): Boolean = oldItem.roadAddress == newItem.roadAddress && oldItem.jibunAddress == newItem.jibunAddress

            override fun areContentsTheSame(
                oldItem: DomainAddresses, newItem: DomainAddresses
            ): Boolean =  oldItem == newItem
        }
    }
}