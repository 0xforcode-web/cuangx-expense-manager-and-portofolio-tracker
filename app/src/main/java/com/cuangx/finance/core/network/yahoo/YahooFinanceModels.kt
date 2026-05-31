package com.cuangx.finance.core.network.yahoo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YahooChartResponse(
    @Json(name = "chart") val chart: YahooChart
)

@JsonClass(generateAdapter = true)
data class YahooChart(
    @Json(name = "result") val result: List<YahooResult>?,
    @Json(name = "error") val error: YahooError?
)

@JsonClass(generateAdapter = true)
data class YahooResult(
    @Json(name = "meta") val meta: YahooMeta
)

@JsonClass(generateAdapter = true)
data class YahooMeta(
    @Json(name = "currency") val currency: String?,
    @Json(name = "symbol") val symbol: String?,
    @Json(name = "regularMarketPrice") val regularMarketPrice: Double?,
    @Json(name = "chartPreviousClose") val chartPreviousClose: Double?,
    @Json(name = "regularMarketDayHigh") val regularMarketDayHigh: Double?,
    @Json(name = "regularMarketDayLow") val regularMarketDayLow: Double?,
    @Json(name = "shortName") val shortName: String?
)

@JsonClass(generateAdapter = true)
data class YahooError(
    @Json(name = "code") val code: String?,
    @Json(name = "description") val description: String?
)

@JsonClass(generateAdapter = true)
data class YahooSearchResponse(
    @Json(name = "ResultSet") val resultSet: YahooResultSet?
)

@JsonClass(generateAdapter = true)
data class YahooResultSet(
    @Json(name = "Query") val query: String?,
    @Json(name = "Result") val result: List<YahooSearchResult>?
)

@JsonClass(generateAdapter = true)
data class YahooSearchResult(
    @Json(name = "symbol") val symbol: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "typeDisp") val typeDisp: String?,
    @Json(name = "exchange") val exchange: String?
)
