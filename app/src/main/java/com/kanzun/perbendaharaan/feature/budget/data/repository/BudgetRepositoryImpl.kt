package com.kanzun.perbendaharaan.feature.budget.data.repository

import com.kanzun.perbendaharaan.core.database.dao.AuditLogDao
import com.kanzun.perbendaharaan.core.database.dao.BudgetDao
import com.kanzun.perbendaharaan.core.database.dao.CategoryDao
import com.kanzun.perbendaharaan.core.database.dao.TransactionDao
import com.kanzun.perbendaharaan.core.database.entity.AuditLogEntity
import com.kanzun.perbendaharaan.core.database.entity.BudgetEntity
import com.kanzun.perbendaharaan.core.database.entity.BudgetItemEntity
import com.kanzun.perbendaharaan.core.database.entity.CategoryEntity
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.feature.budget.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val auditLogDao: AuditLogDao,
) : BudgetRepository {

    override fun getAllBudgets(): Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()

    override suspend fun getBudgetByYear(year: Int): BudgetEntity? = budgetDao.getBudgetByYear(year)

    override fun getBudgetItemsForBudget(budgetId: String): Flow<List<BudgetItemEntity>> =
        budgetDao.getBudgetItemsForBudget(budgetId)

    override fun getCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    override fun getTransactions(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    override suspend fun createOrUpdateBudget(
        year: Int,
        title: String,
        items: List<BudgetItemEntity>,
        userId: String,
    ): BudgetEntity {
        val existing = budgetDao.getBudgetByYear(year)
        if (existing != null && existing.isLocked) {
            throw IllegalStateException("RAPBM tahun $year telah dikunci dan tidak dapat diubah.")
        }

        val budgetId = existing?.id ?: UUID.randomUUID().toString()

        val totalIncome = items.filter { !it.isExpense }.sumOf { it.plannedAmountInCents }
        val totalExpense = items.filter { it.isExpense }.sumOf { it.plannedAmountInCents }

        val updatedBudget = BudgetEntity(
            id = budgetId,
            year = year,
            title = title,
            totalIncomeBudgetInCents = totalIncome,
            totalExpenseBudgetInCents = totalExpense,
            isLocked = false,
        )

        val updatedItems = items.map { item ->
            if (item.budgetId.isEmpty() || item.budgetId != budgetId) {
                item.copy(budgetId = budgetId)
            } else {
                item
            }
        }

        budgetDao.insertBudget(updatedBudget)
        budgetDao.insertBudgetItems(updatedItems)

        val actionName = if (existing == null) "CREATE_RAPBM" else "UPDATE_RAPBM"
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                whoUserId = userId,
                whenTimestamp = System.currentTimeMillis(),
                action = actionName,
                entityName = "Budget",
                entityId = budgetId,
                afterStateJson = "RAPBM $year - Total Income: $totalIncome, Total Expense: $totalExpense",
            )
        )

        return updatedBudget
    }

    override suspend fun lockBudget(budgetId: String, userId: String): BudgetEntity {
        val budgets = budgetDao.getAllBudgets().first()
        val budget = budgets.find { it.id == budgetId }
            ?: throw IllegalArgumentException("Budget not found: $budgetId")

        val lockedBudget = budget.copy(isLocked = true)
        budgetDao.updateBudget(lockedBudget)

        auditLogDao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                whoUserId = userId,
                whenTimestamp = System.currentTimeMillis(),
                action = "LOCK_RAPBM",
                entityName = "Budget",
                entityId = budgetId,
                beforeStateJson = "isLocked=false",
                afterStateJson = "isLocked=true",
            )
        )

        return lockedBudget
    }

    override suspend fun deleteBudget(budgetId: String) {
        budgetDao.deleteBudgetItemsForBudget(budgetId)
        budgetDao.deleteBudgetById(budgetId)
    }

    override fun getAuditLogs(): Flow<List<AuditLogEntity>> = auditLogDao.getAllAuditLogs()
}
