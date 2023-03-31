package com.cellodove.presentation.ui

import com.cellodove.presentation.base.BaseActivity
import com.cellodove.presentation.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate){
    override fun observeViewModel() = Unit
}