package com.kanzun.perbendaharaan

import com.kanzun.perbendaharaan.core.database.entity.AuditLogEntity
import com.kanzun.perbendaharaan.core.database.entity.BudgetEntity
import com.kanzun.perbendaharaan.core.database.entity.BudgetItemEntity
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.model.Money
import com.kanzun.perbendaharaan.core.model.TransactionStatus
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.feature.budget.presentation.BudgetVarianceStatus
import com.kanzun.perbendaharaan.feature.budget.presentation.CategoryBudgetVsActual
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone
import java.util.UUID

class BudgetVsActualTest {

    private val budgets = mutableListOf<BudgetEntity>()
    private val budgetItems = mutableListOf<BudgetItemEntity>()
    private val transactions = mutableListOf<TransactionEntity>()
    private val auditLogs = mutableListOf<AuditLogEntity>()

    @Before
    fun setUp() {
        budgets.clear()
        budgetItems.clear()
        transactions.clear()
        auditLogs.clear()
    }

    @Test
    fun testBudgetCreationAndSummaryTotals(): Unit = runBlocking {
        val year = 2027
        val budgetId = "b_2027"

        val items = listOf(
            BudgetItemEntity(id = "bi_1", budgetId = budgetId, categoryId = "cat_infaq", plannedAmountInCents = 200_000_000_00L, isExpense = false),
            BudgetItemEntity(id = "bi_2", budgetId = budgetId, categoryId = "cat_sedekah", plannedAmountInCents = 100_000_000_00L, isExpense = false),
            BudgetItemEntity(id = "bi_3", budgetId = budgetId, categoryId = "cat_operasional", plannedAmountInCents = 80_000_000_00L, isExpense = true),
            BudgetItemEntity(id = "bi_4", budgetId = budgetId, categoryId = "cat_listrik", plannedAmountInCents = 20_000_000_00L, isExpense = true),
        )

        val totalPlannedIncome = items.filter { !it.isExpense }.sumOf { it.plannedAmountInCents }
        val totalPlannedExpense = items.filter { it.isExpense }.sumOf { it.plannedAmountInCents }

        val budget = BudgetEntity(
            id = budgetId,
            year = year,
            title = "RAPBM $year",
            totalIncomeBudgetInCents = totalPlannedIncome,
            totalExpenseBudgetInCents = totalPlannedExpense,
            isLocked = false,
        )

        budgets.add(budget)
        budgetItems.addAll(items)

        assertEquals(300_000_000_00L, budget.totalIncomeBudgetInCents)
        assertEquals(100_000_000_00L, budget.totalExpenseBudgetInCents)
        assertEquals(200_000_000_00L, budget.totalIncomeBudgetInCents - budget.totalExpenseBudgetInCents)
        assertFalse(budget.isLocked)
    }

    @Test
    fun testActualDerivationFromTransactions(): Unit = runBlocking {
        val year = 2027
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(year, Calendar.MARCH, 15, 10, 0, 0)
        }
        val timestamp2027 = cal.timeInMillis

        // Add 2027 live transactions
        transactions.add(
            TransactionEntity(
                id = "tx_1",
                title = "Infaq Jumat",
                amountInCents = 150_000_000_00L,
                type = TransactionType.INCOME,
                status = TransactionStatus.FINALIZED,
                accountId = "acc_cash",
                fundId = "fund_op",
                categoryId = "cat_infaq",
                timestamp = timestamp2027,
            )
        )
        transactions.add(
            TransactionEntity(
                id = "tx_2",
                title = "Bayar Listrik Maret",
                amountInCents = 25_000_000_00L,
                type = TransactionType.EXPENSE,
                status = TransactionStatus.FINALIZED,
                accountId = "acc_bsi",
                fundId = "fund_op",
                categoryId = "cat_listrik",
                timestamp = timestamp2027,
            )
        )

        // Transactions in 2027 matching categories
        val yearTxs = transactions.filter { tx ->
            val c = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            c.timeInMillis = tx.timestamp
            c.get(Calendar.YEAR) == year && !tx.isReversed
        }

        val actualsByCat = yearTxs.groupBy { it.categoryId }.mapValues { entry -> entry.value.sumOf { it.amountInCents } }

        assertEquals(150_000_000_00L, actualsByCat["cat_infaq"])
        assertEquals(25_000_000_00L, actualsByCat["cat_listrik"])
        assertEquals(0L, actualsByCat["cat_sedekah"] ?: 0L)

        val totalActualIncome = yearTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amountInCents }
        val totalActualExpense = yearTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountInCents }

        assertEquals(150_000_000_00L, totalActualIncome)
        assertEquals(25_000_000_00L, totalActualExpense)
        assertEquals(125_000_000_00L, totalActualIncome - totalActualExpense)
    }

    @Test
    fun testVarianceClassification(): Unit = runBlocking {
        val itemOverBudget = CategoryBudgetVsActual(
            categoryId = "cat_listrik",
            categoryName = "Listrik",
            isExpense = true,
            plannedAmountInCents = 20_000_000_00L,
            actualAmountInCents = 25_000_000_00L,
        )

        val itemUnderBudget = CategoryBudgetVsActual(
            categoryId = "cat_infaq",
            categoryName = "Infaq",
            isExpense = false,
            plannedAmountInCents = 200_000_000_00L,
            actualAmountInCents = 150_000_000_00L,
        )

        val itemOnBudget = CategoryBudgetVsActual(
            categoryId = "cat_operasional",
            categoryName = "Operasional",
            isExpense = true,
            plannedAmountInCents = 80_000_000_00L,
            actualAmountInCents = 80_000_000_00L,
        )

        assertEquals(BudgetVarianceStatus.OVER_BUDGET, itemOverBudget.varianceStatus)
        assertEquals(BudgetVarianceStatus.UNDER_BUDGET, itemUnderBudget.varianceStatus)
        assertEquals(BudgetVarianceStatus.ON_BUDGET, itemOnBudget.varianceStatus)
    }

    @Test
    fun testBudgetLockingAndAuditLog(): Unit = runBlocking {
        val budget = BudgetEntity(
            id = "b_2027",
            year = 2027,
            title = "RAPBM 2027",
            totalIncomeBudgetInCents = 300_000_000_00L,
            totalExpenseBudgetInCents = 100_000_000_00L,
            isLocked = false,
        )

        val lockedBudget = budget.copy(isLocked = true)
        budgets.add(lockedBudget)

        auditLogs.add(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                whoUserId = "system",
                whenTimestamp = System.currentTimeMillis(),
                action = "LOCK_RAPBM",
                entityName = "Budget",
                entityId = lockedBudget.id,
                beforeStateJson = "isLocked=false",
                afterStateJson = "isLocked=true",
            )
        )

        assertTrue(lockedBudget.isLocked)
        assertEquals(1, auditLogs.size)
        assertEquals("LOCK_RAPBM", auditLogs[0].action)
    }
}
