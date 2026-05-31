package com.cuangx.finance.domain.model

enum class AccountType {
    CASH,
    BANK,
    CREDIT_CARD,
    E_WALLET,
    INVESTMENT,
    BROKER;

    val displayName: String
        get() = when (this) {
            CASH -> "Cash"
            BANK -> "Bank"
            CREDIT_CARD -> "Credit Card"
            E_WALLET -> "E-Wallet"
            INVESTMENT -> "Investment"
            BROKER -> "Broker"
        }

    val icon: String
        get() = when (this) {
            CASH -> "payments"
            BANK -> "account_balance"
            CREDIT_CARD -> "credit_card"
            E_WALLET -> "account_balance_wallet"
            INVESTMENT -> "trending_up"
            BROKER -> "candlestick_chart"
        }
}
