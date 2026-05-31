package com.cuangx.finance.domain.usecase

import com.cuangx.finance.domain.model.*
import com.cuangx.finance.domain.repository.JournalRepository
import com.cuangx.finance.domain.repository.PriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class HoldingPosition(
    val ticker: String,
    val name: String,
    val assetType: AssetType,
    val quantity: Double,
    val avgBuyPrice: Double,
    val currentPrice: Double?,
    val currentValue: Double,
    val totalCost: Double,
    val pnl: Double,
    val pnlPercent: Double
)

data class PortfolioSummary(
    val holdings: List<HoldingPosition>,
    val totalValue: Double,
    val totalCost: Double,
    val totalPnl: Double,
    val totalPnlPercent: Double
)

class PortfolioUseCase @Inject constructor(
    private val journalRepository: JournalRepository,
    private val priceRepository: PriceRepository
) {
    fun getPortfolioSummary(): Flow<PortfolioSummary> {
        return combine(
            journalRepository.getAll(),
            priceRepository.getAllCached()
        ) { journalEntries, prices ->
            val priceMap = prices.associateBy { it.ticker }
            val holdings = calculateHoldings(journalEntries, priceMap)

            val totalValue = holdings.sumOf { it.currentValue }
            val totalCost = holdings.sumOf { it.totalCost }
            val totalPnl = totalValue - totalCost
            val totalPnlPercent = if (totalCost > 0) (totalPnl / totalCost) * 100 else 0.0

            PortfolioSummary(
                holdings = holdings,
                totalValue = totalValue,
                totalCost = totalCost,
                totalPnl = totalPnl,
                totalPnlPercent = totalPnlPercent
            )
        }
    }

    private fun calculateHoldings(
        entries: List<JournalEntry>,
        priceMap: Map<String, PriceData>
    ): List<HoldingPosition> {
        val buySellEntries = entries.filter {
            it.action == JournalAction.BUY || it.action == JournalAction.SELL
        }

        return buySellEntries
            .groupBy { it.ticker ?: it.name }
            .mapNotNull { (ticker, assetEntries) ->
                val buys = assetEntries.filter { it.action == JournalAction.BUY }
                val sells = assetEntries.filter { it.action == JournalAction.SELL }

                val totalBuyQty = buys.sumOf { it.quantity }
                val totalSellQty = sells.sumOf { it.quantity }
                val remainingQty = totalBuyQty - totalSellQty

                if (remainingQty <= 0) return@mapNotNull null

                val firstEntry = assetEntries.first()
                val totalBuyCost = buys.sumOf { it.quantity * it.price + it.fee }
                val avgBuyPrice = if (totalBuyQty > 0) totalBuyCost / totalBuyQty else 0.0

                val priceData = priceMap[ticker]
                val goldPriceData = priceMap["GOLD_GRAM_IDR"]
                
                val currentPrice = when (firstEntry.assetType) {
                    AssetType.GOLD -> goldPriceData?.price ?: avgBuyPrice
                    else -> priceData?.price ?: avgBuyPrice
                }
                
                val currentValue = remainingQty * currentPrice
                val totalCost = remainingQty * avgBuyPrice
                val pnl = currentValue - totalCost
                val pnlPercent = if (totalCost > 0) (pnl / totalCost) * 100 else 0.0

                HoldingPosition(
                    ticker = ticker,
                    name = firstEntry.name,
                    assetType = firstEntry.assetType,
                    quantity = remainingQty,
                    avgBuyPrice = avgBuyPrice,
                    currentPrice = if (firstEntry.assetType == AssetType.GOLD) goldPriceData?.price else priceData?.price,
                    currentValue = currentValue,
                    totalCost = totalCost,
                    pnl = pnl,
                    pnlPercent = pnlPercent
                )
            }
            .sortedByDescending { it.currentValue }
    }
}
