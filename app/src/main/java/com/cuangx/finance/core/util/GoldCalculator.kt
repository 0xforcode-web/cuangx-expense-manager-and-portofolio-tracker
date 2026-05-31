package com.cuangx.finance.core.util

object GoldCalculator {

    private const val TROY_OZ_TO_GRAM = 31.1035

    fun calculatePricePerGram(goldUsdPerOz: Double, usdIdrRate: Double): Double {
        return (goldUsdPerOz / TROY_OZ_TO_GRAM) * usdIdrRate
    }

    fun calculateTotalValue(gramQuantity: Double, pricePerGram: Double): Double {
        return gramQuantity * pricePerGram
    }
}
