package com.cellodove.presentation.ui

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

    private lateinit var navController : NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navHostFragment =supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController
    }

    var waitTime = 0L
    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.fragment_main_map) {
            if (System.currentTimeMillis() - waitTime >= 1500) {
                waitTime = System.currentTimeMillis()
                Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            } else {
                finish()
            }
        } else {
            super.onBackPressed()
        }
    }

        override fun observeViewModel() = Unit
}