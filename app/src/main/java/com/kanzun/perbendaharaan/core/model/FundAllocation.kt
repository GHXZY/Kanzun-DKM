package com.kanzun.perbendaharaan.core.model

data class FundAllocationItem(
    val fundId: String,
    val fundName: String,
    val allocatedAmount: Money,
    val percentage: Float,
)

data class FundAllocation(
    val totalAllocated: Money,
    val items: List<FundAllocationItem>,
)
