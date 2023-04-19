package com.cellodove.presentation

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cellodove.domain.usecase.BicyclesLocationUseCase
import com.cellodove.domain.usecase.FindRootUseCase
import com.cellodove.domain.usecase.SearchAddressUseCase
import com.cellodove.presentation.ui.main.MainViewModel
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainViewModelTest {
    @MockK
    lateinit var context: Context


    lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var findRootUseCase: FindRootUseCase

    @MockK
    private lateinit var searchAddressUseCase: SearchAddressUseCase

    @MockK
    private lateinit var bicyclesLocationUseCase: BicyclesLocationUseCase


//    @get:Rule
//    var hiltRule = HiltAndroidRule(this)

    @Inject

    @Before
    fun init() {
//        hiltRule.inject()
        mainViewModel = spyk(
            MainViewModel(
                findRootUseCase = findRootUseCase,
                searchAddressUseCase = searchAddressUseCase,
                bicyclesLocationUseCase = bicyclesLocationUseCase
            )
        )
    }


    @Test
    fun getAddress_returnData() {
        mainViewModel.getAddress(37.39575041751,127.11134823329,context)
    }


}