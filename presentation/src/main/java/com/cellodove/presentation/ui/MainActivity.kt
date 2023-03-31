package com.cellodove.presentation.ui

import android.os.Bundle
import com.cellodove.presentation.base.BaseActivity
import com.cellodove.presentation.databinding.ActivityMainBinding
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }




    override fun observeViewModel() = Unit
}