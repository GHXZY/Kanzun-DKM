package com.kanzun.perbendaharaan.feature.budget.domain.repository

import com.kanzun.perbendaharaan.core.database.entity.AuditLogEntity
import com.kanzun.perbendaharaan.core.database.entity.BudgetEntity
import com.kanzun.perbendaharaan.core.database.entity.BudgetItemEntity
import com.kanzun.perbendaharaan.core.database.entity.CategoryEntity
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getAllBudgets(): Flow<List<BudgetEntity>>
    suspend fun getBudgetByYear(year: Int): BudgetEntity?
    fun getBudgetItemsForBudget(budgetId: String): Flow<List<BudgetItemEntity>>
    fun getCategories(): Flow<List<CategoryEntity>>
    fun getTransactions(): Flow<List<TransactionEntity>>
    suspend fun createOrUpdateBudget(
        year: Int,
        title: String,
        items: List<BudgetItemEntity>,
        userId: String = "system",
    ): BudgetEntity
    suspend fun lockBudget(budgetId: String, userId: String = "system"): BudgetEntity
    suspend fun deleteBudget(budgetId: String)
    fun getAuditLogs(): Flow<List<AuditLogEntity>>
}
