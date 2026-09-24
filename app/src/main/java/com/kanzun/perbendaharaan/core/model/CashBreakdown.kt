package com.kanzun.perbendaharaan.core.model

data class CashBreakdownItem(
    val accountId: String,
    val accountName: String,
    val isBank: Boolean,
    val balance: Money,
)

data class CashBreakdown(
    val totalCash: Money,
    val bankTotal: Money,
    val cashTotal: Money,
    val items: List<CashBreakdownItem>,
)
