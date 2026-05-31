package com.cuangx.finance.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cuangx.finance.domain.repository.HoldingRepository
import com.cuangx.finance.domain.repository.PriceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PriceRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val holdingRepository: HoldingRepository,
    private val priceRepository: PriceRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val tickers = holdingRepository.getAllTickers()

        if (tickers.isNotEmpty()) {
            val result = priceRepository.refreshPrices(tickers)
            if (result.isFailure) return Result.retry()
        }

        val goldResult = priceRepository.refreshGoldPrice()
        if (goldResult.isFailure) return Result.retry()

        return Result.success()
    }
}
