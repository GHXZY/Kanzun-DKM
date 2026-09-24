package com.kanzun.perbendaharaan

import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.model.TransactionStatus
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportFilter
import com.kanzun.perbendaharaan.feature.reports.presentation.components.CashFlowDataPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportsFinancialChartsTest {

    private val transactions = mutableListOf<TransactionEntity>()

    @Before
    fun setUp() {
        transactions.clear()

        val now = System.currentTimeMillis()

        transactions.add(
            TransactionEntity(
                id = "tx_inc_1",
                title = "Infaq Subuh",
                amountInCents = 2_000_000_00L,
                type = TransactionType.INCOME,
                status = TransactionStatus.FINALIZED,
                accountId = "acc_cash",
                fundId = "fund_op",
                categoryId = "cat_infaq",
                timestamp = now,
            )
        )

        transactions.add(
            TransactionEntity(
                id = "tx_exp_1",
                title = "Beli Sapu & Pembersih",
                amountInCents = 500_000_00L,
                type = TransactionType.EXPENSE,
                status = TransactionStatus.FINALIZED,
                accountId = "acc_cash",
                fundId = "fund_op",
                categoryId = "cat_kebersihan",
                timestamp = now,
            )
        )
    }

    @Test
    fun testCashFlowDataPointAggregationAndNetCalculation() {
        val dateFormat = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        val now = System.currentTimeMillis()
        val dateLabel = dateFormat.format(Date(now))

        val activeTxs = transactions.filter { !it.isReversed }
        val income = activeTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amountInCents }
        val expense = activeTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountInCents }

        val dataPoint = CashFlowDataPoint(dateLabel = dateLabel, incomeInCents = income, expenseInCents = expense)

        assertEquals(2_000_000_00L, dataPoint.incomeInCents)
        assertEquals(500_000_00L, dataPoint.expenseInCents)
        assertEquals(1_500_000_00L, dataPoint.netInCents)
    }

    @Test
    fun testTransfersExcludedFromNetIncomeAndExpense() {
        // Adding a transfer transaction
        transactions.add(
            TransactionEntity(
                id = "tx_trf_1",
                title = "Setor Tunai ke BSI",
                amountInCents = 1_000_000_00L,
                type = TransactionType.TRANSFER,
                status = TransactionStatus.FINALIZED,
                accountId = "acc_cash",
                fundId = "fund_op",
                categoryId = "cat_transfer",
                timestamp = System.currentTimeMillis(),
            )
        )

        val activeTxs = transactions.filter { !it.isReversed }
        val income = activeTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amountInCents }
        val expense = activeTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountInCents }

        // Transfer of 1,000,000 IDR must NOT alter income (2,000,000) or expense (500,000)
        assertEquals(2_000_000_00L, income)
        assertEquals(500_000_00L, expense)
        assertEquals(1_500_000_00L, income - expense)
    }

    @Test
    fun testEmptyTransactionsPeriod() {
        val emptyList = emptyList<TransactionEntity>()
        val income = emptyList.filter { it.type == TransactionType.INCOME }.sumOf { it.amountInCents }
        val expense = emptyList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountInCents }

        assertEquals(0L, income)
        assertEquals(0L, expense)
        assertEquals(0L, income - expense)
    }
}
