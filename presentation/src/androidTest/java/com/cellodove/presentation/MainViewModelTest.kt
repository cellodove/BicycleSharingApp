package com.cellodove.presentation

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cellodove.domain.usecase.BicyclesLocationUseCase
import com.cellodove.domain.usecase.FindRootUseCase
import com.cellodove.domain.usecase.SearchAddressUseCase
import com.cellodove.presentation.ui.main.MainViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainViewModelTest {

    @Mock
    private lateinit var context : Context

    @Inject
    private lateinit var mainViewModel : MainViewModel

    @Inject
    private lateinit var findRootUseCase : FindRootUseCase

    @Inject
    private lateinit var searchAddressUseCase : SearchAddressUseCase

    @Inject
    private lateinit var bicyclesLocationUseCase : BicyclesLocationUseCase


    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init(){
        hiltRule.inject()


    }


    @Test
    fun getAddress_returnData(){
        mainViewModel.getAddress(37.39575041751,127.11134823329,context)
    }


}