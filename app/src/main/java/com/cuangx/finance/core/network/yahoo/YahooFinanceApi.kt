package com.cuangx.finance.core.network.yahoo

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface YahooFinanceApi {

    @GET("v8/finance/chart/{ticker}")
    suspend fun getQuote(
        @Path("ticker") ticker: String,
        @Query("interval") interval: String = "1d",
        @Query("range") range: String = "1d"
    ): YahooChartResponse

    @GET("v6/finance/autocomplete")
    suspend fun searchSymbol(
        @Query("query") query: String
    ): YahooSearchResponse
}
