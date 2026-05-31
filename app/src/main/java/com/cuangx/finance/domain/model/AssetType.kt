package com.cuangx.finance.domain.model

enum class AssetType {
    STOCK,
    ETF,
    CRYPTO,
    GOLD,
    REAL_ESTATE,
    ART,
    COLLECTIBLE,
    OTHER;

    val displayName: String
        get() = when (this) {
            STOCK -> "Stock"
            ETF -> "ETF"
            CRYPTO -> "Crypto"
            GOLD -> "Gold (Logam Mulia)"
            REAL_ESTATE -> "Real Estate"
            ART -> "Art"
            COLLECTIBLE -> "Collectible"
            OTHER -> "Other"
        }

    val icon: String
        get() = when (this) {
            STOCK -> "candlestick_chart"
            ETF -> "pie_chart"
            CRYPTO -> "currency_bitcoin"
            GOLD -> "diamond"
            REAL_ESTATE -> "home"
            ART -> "palette"
            COLLECTIBLE -> "star"
            OTHER -> "category"
        }

    val hasTicker: Boolean
        get() = when (this) {
            STOCK, ETF, CRYPTO, GOLD -> true
            else -> false
        }
}
