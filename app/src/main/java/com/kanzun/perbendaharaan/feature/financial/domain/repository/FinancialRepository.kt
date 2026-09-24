package com.kanzun.perbendaharaan.feature.financial.domain.repository

import com.kanzun.perbendaharaan.core.database.entity.AccountEntity
import com.kanzun.perbendaharaan.core.database.entity.AuditLogEntity
import com.kanzun.perbendaharaan.core.database.entity.FundEntity
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.database.entity.TransferEntity
import com.kanzun.perbendaharaan.core.model.CashBreakdown
import com.kanzun.perbendaharaan.core.model.FundAllocation
import com.kanzun.perbendaharaan.core.model.Money
import kotlinx.coroutines.flow.Flow

interface FinancialRepository {
    suspend fun createIncome(
        title: String,
        amount: Money,
        accountId: String,
        fundId: String,
        categoryId: String,
        note: String = "",
        userId: String = "system",
    ): TransactionEntity

    suspend fun createExpense(
        title: String,
        amount: Money,
        accountId: String,
        fundId: String,
        categoryId: String,
        note: String = "",
        userId: String = "system",
    ): TransactionEntity

    suspend fun createTransfer(
        fromAccountId: String,
        toAccountId: String,
        amount: Money,
        note: String = "",
        userId: String = "system",
    ): TransferEntity

    suspend fun getAccountBalance(accountId: String): Money
    suspend fun getTotalCash(): Money
    suspend fun getCashBreakdown(): CashBreakdown
    suspend fun getFundAllocation(): FundAllocation

    fun getTransactions(): Flow<List<TransactionEntity>>
    suspend fun getTransactionById(transactionId: String): TransactionEntity?

    suspend fun finalizeTransaction(transactionId: String, userId: String = "system"): TransactionEntity
    suspend fun reverseTransaction(transactionId: String, reason: String, userId: String = "system"): TransactionEntity
    suspend fun createAuditLog(
        whoUserId: String,
        action: String,
        entityName: String,
        entityId: String,
        beforeStateJson: String? = null,
        afterStateJson: String? = null,
    ): AuditLogEntity

    fun getAuditLogs(): Flow<List<AuditLogEntity>>

    suspend fun addAccount(account: AccountEntity)
    fun getActiveAccounts(): Flow<List<AccountEntity>>
    suspend fun updateOpeningBalance(accountId: String, openingBalanceInCents: Long)
    suspend fun addFund(fund: FundEntity)
    fun getActiveFunds(): Flow<List<FundEntity>>
}
