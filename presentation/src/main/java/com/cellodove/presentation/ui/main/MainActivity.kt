package com.cellodove.presentation.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.cellodove.presentation.R
import com.cellodove.presentation.base.BaseActivity
import com.cellodove.presentation.databinding.ActivityMainBinding
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate){
    override fun observeViewModel() = Unit

    companion object{
        const val PATH_STATUS = "PATH_STATUS"

        const val STARTING_POINT = "STARTING_POINT"
        const val ENDING_POINT = "ENDING_POINT"

        const val FIND_ROOT = "FIND_ROOT"
        const val FIND_BICYCLES = "FIND_BICYCLES"
        const val START_USING = "START_USING"

        const val X_VALUE = "X_VALUE"
        const val Y_VALUE = "Y_VALUE"
    }
}