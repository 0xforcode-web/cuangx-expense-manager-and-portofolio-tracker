package com.cuangx.finance.core.network.swissquote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SwissquoteResponse(
    @Json(name = "spreadProfilePrices") val spreadProfilePrices: List<SwissquotePrice>?
)

@JsonClass(generateAdapter = true)
data class SwissquotePrice(
    @Json(name = "spreadProfile") val spreadProfile: String?,
    @Json(name = "bid") val bid: Double?,
    @Json(name = "ask") val ask: Double?
)
