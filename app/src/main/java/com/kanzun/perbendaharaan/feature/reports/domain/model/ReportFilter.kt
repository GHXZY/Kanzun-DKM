package com.kanzun.perbendaharaan.feature.reports.domain.model

import com.kanzun.perbendaharaan.core.model.TransactionStatus
import com.kanzun.perbendaharaan.core.model.TransactionType

data class ReportFilter(
    val startDate: Long? = null,
    val endDate: Long? = null,
    val accountId: String? = null,
    val fundId: String? = null,
    val categoryId: String? = null,
    val transactionType: TransactionType? = null,
    val status: TransactionStatus? = null,
    val year: Int? = null,
)
