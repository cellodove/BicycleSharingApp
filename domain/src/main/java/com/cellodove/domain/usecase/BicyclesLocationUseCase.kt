package com.cellodove.domain.usecase

import java.util.concurrent.ThreadLocalRandom
import javax.inject.Inject


interface BicyclesLocationUseCase {
    fun getRandomLocation(x:Double, y:Double) : List<List<Double>>
}

class BicyclesLocationUseCaseImpl @Inject constructor() : BicyclesLocationUseCase {

    override fun getRandomLocation(x:Double, y:Double) : List<List<Double>> {
        val xPlus = x + 0.0009
        val xMinus = x - 0.0009
        val yPlus = y + 0.0009
        val yMinus = y - 0.0009

        val bicyclesLocationList = arrayListOf<List<Double>>()

        for (i in 0 .. 7){
            val x = ThreadLocalRandom.current().nextDouble( xMinus, xPlus)
            val y = ThreadLocalRandom.current().nextDouble( yMinus, yPlus)
            bicyclesLocationList.add(mutableListOf(x,y))
        }
        return bicyclesLocationList
    }
}