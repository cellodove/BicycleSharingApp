package com.cellodove.presentation.ui.search

import android.os.Bundle
import android.view.View
import com.cellodove.presentation.base.BaseFragment
import com.cellodove.presentation.databinding.FragmentAddressSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentAddressSearchBinding>(FragmentAddressSearchBinding::inflate){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }



    private fun initUi(){

    }

    override fun observeViewModel() {

    }
}