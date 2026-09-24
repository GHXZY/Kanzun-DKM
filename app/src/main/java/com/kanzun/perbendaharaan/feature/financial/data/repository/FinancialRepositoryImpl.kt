package com.kanzun.perbendaharaan.feature.financial.data.repository

import com.kanzun.perbendaharaan.core.database.dao.AccountDao
import com.kanzun.perbendaharaan.core.database.dao.AuditLogDao
import com.kanzun.perbendaharaan.core.database.dao.CategoryDao
import com.kanzun.perbendaharaan.core.database.dao.FundDao
import com.kanzun.perbendaharaan.core.database.dao.TransactionDao
import com.kanzun.perbendaharaan.core.database.dao.TransferDao
import com.kanzun.perbendaharaan.core.database.entity.AccountEntity
import com.kanzun.perbendaharaan.core.database.entity.AuditLogEntity
import com.kanzun.perbendaharaan.core.database.entity.FundEntity
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.database.entity.TransferEntity
import com.kanzun.perbendaharaan.core.model.CashBreakdown
import com.kanzun.perbendaharaan.core.model.CashBreakdownItem
import com.kanzun.perbendaharaan.core.model.FundAllocation
import com.kanzun.perbendaharaan.core.model.FundAllocationItem
import com.kanzun.perbendaharaan.core.model.Money
import com.kanzun.perbendaharaan.core.model.TransactionStatus
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.feature.financial.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinancialRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val transferDao: TransferDao,
    private val fundDao: FundDao,
    private val categoryDao: CategoryDao,
    private val auditLogDao: AuditLogDao,
) : FinancialRepository {

    override suspend fun createIncome(
        title: String,
        amount: Money,
        accountId: String,
        fundId: String,
        categoryId: String,
        note: String,
        userId: String,
    ): TransactionEntity {
        val account = accountDao.getAccountById(accountId)
            ?: throw IllegalArgumentException("Account with ID $accountId not found")

        val transactionId = UUID.randomUUID().toString()
        val transaction = TransactionEntity(
            id = transactionId,
            title = title,
            amountInCents = amount.amountInCents,
            type = TransactionType.INCOME,
            status = TransactionStatus.RECORDED,
            accountId = accountId,
            fundId = fundId,
            categoryId = categoryId,
            timestamp = System.currentTimeMillis(),
            note = note,
            createdByUserId = userId,
        )

        transactionDao.insertTransaction(transaction)

        val updatedBalance = account.currentBalanceInCents + amount.amountInCents
        accountDao.updateAccountBalance(accountId, updatedBalance)

        createAuditLog(
            whoUserId = userId,
            action = "CREATE_INCOME",
            entityName = "Transaction",
            entityId = transactionId,
            afterStateJson = "{\"amount\": ${amount.amountInCents}, \"accountId\": \"$accountId\"}",
        )

        return transaction
    }

    override suspend fun createExpense(
        title: String,
        amount: Money,
        accountId: String,
        fundId: String,
        categoryId: String,
        note: String,
        userId: String,
    ): TransactionEntity {
        val account = accountDao.getAccountById(accountId)
            ?: throw IllegalArgumentException("Account with ID $accountId not found")

        val transactionId = UUID.randomUUID().toString()
        val transaction = TransactionEntity(
            id = transactionId,
            title = title,
            amountInCents = amount.amountInCents,
            type = TransactionType.EXPENSE,
            status = TransactionStatus.RECORDED,
            accountId = accountId,
            fundId = fundId,
            categoryId = categoryId,
            timestamp = System.currentTimeMillis(),
            note = note,
            createdByUserId = userId,
        )

        transactionDao.insertTransaction(transaction)

        val updatedBalance = account.currentBalanceInCents - amount.amountInCents
        accountDao.updateAccountBalance(accountId, updatedBalance)

        createAuditLog(
            whoUserId = userId,
            action = "CREATE_EXPENSE",
            entityName = "Transaction",
            entityId = transactionId,
            afterStateJson = "{\"amount\": ${amount.amountInCents}, \"accountId\": \"$accountId\"}",
        )

        return transaction
    }

    override suspend fun createTransfer(
        fromAccountId: String,
        toAccountId: String,
        amount: Money,
        note: String,
        userId: String,
    ): TransferEntity {
        require(fromAccountId != toAccountId) { "Cannot transfer to the same account" }

        val fromAccount = accountDao.getAccountById(fromAccountId)
            ?: throw IllegalArgumentException("Account with ID $fromAccountId not found")
        val toAccount = accountDao.getAccountById(toAccountId)
            ?: throw IllegalArgumentException("Account with ID $toAccountId not found")

        val transferId = UUID.randomUUID().toString()
        val transfer = TransferEntity(
            id = transferId,
            fromAccountId = fromAccountId,
            toAccountId = toAccountId,
            amountInCents = amount.amountInCents,
            timestamp = System.currentTimeMillis(),
            note = note,
            createdByUserId = userId,
        )

        transferDao.insertTransfer(transfer)

        accountDao.updateAccountBalance(fromAccountId, fromAccount.currentBalanceInCents - amount.amountInCents)
        accountDao.updateAccountBalance(toAccountId, toAccount.currentBalanceInCents + amount.amountInCents)

        createAuditLog(
            whoUserId = userId,
            action = "CREATE_TRANSFER",
            entityName = "Transfer",
            entityId = transferId,
            afterStateJson = "{\"from\": \"$fromAccountId\", \"to\": \"$toAccountId\", \"amount\": ${amount.amountInCents}}",
        )

        return transfer
    }

    override suspend fun getAccountBalance(accountId: String): Money {
        val account = accountDao.getAccountById(accountId)
            ?: throw IllegalArgumentException("Account not found: $accountId")
        return Money.of(account.currentBalanceInCents, account.currency)
    }

    override suspend fun getTotalCash(): Money {
        val accounts = accountDao.getAllAccountsList()
        val totalCents = accounts.filter { it.isActive }.sumOf { it.currentBalanceInCents }
        return Money.of(totalCents)
    }

    override suspend fun getCashBreakdown(): CashBreakdown {
        val accounts = accountDao.getAllAccountsList().filter { it.isActive }
        val items = accounts.map {
            CashBreakdownItem(
                accountId = it.id,
                accountName = it.name,
                isBank = it.isBank,
                balance = Money.of(it.currentBalanceInCents, it.currency),
            )
        }

        val totalCents = accounts.sumOf { it.currentBalanceInCents }
        val bankCents = accounts.filter { it.isBank }.sumOf { it.currentBalanceInCents }
        val cashCents = accounts.filter { !it.isBank }.sumOf { it.currentBalanceInCents }

        return CashBreakdown(
            totalCash = Money.of(totalCents),
            bankTotal = Money.of(bankCents),
            cashTotal = Money.of(cashCents),
            items = items,
        )
    }

    override suspend fun getFundAllocation(): FundAllocation {
        val funds = fundDao.getActiveFunds().first()
        val accounts = accountDao.getAllAccountsList().filter { it.isActive }
        val totalCashCents = accounts.sumOf { it.currentBalanceInCents }

        val items = mutableListOf<FundAllocationItem>()

        for (fund in funds) {
            val transactions = transactionDao.getActiveTransactionsForFund(fund.id)
            val fundCents = transactions.fold(0L) { acc, tx ->
                when (tx.type) {
                    TransactionType.INCOME -> acc + tx.amountInCents
                    TransactionType.EXPENSE -> acc - tx.amountInCents
                    TransactionType.TRANSFER -> acc
                }
            }

            val percentage = if (totalCashCents > 0) {
                (fundCents.toFloat() / totalCashCents.toFloat()) * 100f
            } else {
                0f
            }

            items.add(
                FundAllocationItem(
                    fundId = fund.id,
                    fundName = fund.name,
                    allocatedAmount = Money.of(fundCents),
                    percentage = percentage,
                )
            )
        }

        return FundAllocation(
            totalAllocated = Money.of(totalCashCents),
            items = items,
        )
    }

    override fun getTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    override suspend fun getTransactionById(transactionId: String): TransactionEntity? {
        return transactionDao.getTransactionById(transactionId)
    }

    override suspend fun finalizeTransaction(transactionId: String, userId: String): TransactionEntity {
        val tx = transactionDao.getTransactionById(transactionId)
            ?: throw IllegalArgumentException("Transaction not found: $transactionId")

        val updated = tx.copy(status = TransactionStatus.FINALIZED)
        transactionDao.updateTransaction(updated)

        createAuditLog(
            whoUserId = userId,
            action = "FINALIZE_TRANSACTION",
            entityName = "Transaction",
            entityId = transactionId,
            beforeStateJson = "{\"status\": \"${tx.status}\"}",
            afterStateJson = "{\"status\": \"FINALIZED\"}",
        )

        return updated
    }

    override suspend fun reverseTransaction(transactionId: String, reason: String, userId: String): TransactionEntity {
        val tx = transactionDao.getTransactionById(transactionId)
            ?: throw IllegalArgumentException("Transaction not found: $transactionId")

        if (tx.isReversed) {
            throw IllegalStateException("Transaction $transactionId is already reversed")
        }

        val account = accountDao.getAccountById(tx.accountId)
            ?: throw IllegalArgumentException("Account not found: ${tx.accountId}")

        // Reversal logic: revert balance effect
        val newBalance = when (tx.type) {
            TransactionType.INCOME -> account.currentBalanceInCents - tx.amountInCents
            TransactionType.EXPENSE -> account.currentBalanceInCents + tx.amountInCents
            TransactionType.TRANSFER -> account.currentBalanceInCents
        }

        accountDao.updateAccountBalance(tx.accountId, newBalance)

        val reversedTx = tx.copy(isReversed = true, note = "${tx.note} [REVERSED: $reason]")
        transactionDao.updateTransaction(reversedTx)

        createAuditLog(
            whoUserId = userId,
            action = "REVERSE_TRANSACTION",
            entityName = "Transaction",
            entityId = transactionId,
            beforeStateJson = "{\"isReversed\": false}",
            afterStateJson = "{\"isReversed\": true, \"reason\": \"$reason\"}",
        )

        return reversedTx
    }

    override suspend fun createAuditLog(
        whoUserId: String,
        action: String,
        entityName: String,
        entityId: String,
        beforeStateJson: String?,
        afterStateJson: String?,
    ): AuditLogEntity {
        val log = AuditLogEntity(
            id = UUID.randomUUID().toString(),
            whoUserId = whoUserId,
            whenTimestamp = System.currentTimeMillis(),
            action = action,
            entityName = entityName,
            entityId = entityId,
            beforeStateJson = beforeStateJson,
            afterStateJson = afterStateJson,
        )
        auditLogDao.insertAuditLog(log)
        return log
    }

    override fun getAuditLogs(): Flow<List<AuditLogEntity>> {
        return auditLogDao.getAllAuditLogs()
    }

    override suspend fun addAccount(account: AccountEntity) {
        accountDao.insertAccount(account)
    }

    override fun getActiveAccounts(): Flow<List<AccountEntity>> {
        return accountDao.getActiveAccounts()
    }

    override suspend fun updateOpeningBalance(accountId: String, openingBalanceInCents: Long) {
        val account = accountDao.getAccountById(accountId) ?: return
        val activeTxs = transactionDao.getActiveTransactionsForAccount(accountId)
        val incomeCents = activeTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amountInCents }
        val expenseCents = activeTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountInCents }
        val newCurrentBalance = openingBalanceInCents + incomeCents - expenseCents
        accountDao.updateAccount(
            account.copy(
                openingBalanceInCents = openingBalanceInCents,
                currentBalanceInCents = newCurrentBalance,
            )
        )
        createAuditLog(
            whoUserId = "system",
            action = "UPDATE_OPENING_BALANCE",
            entityName = "Account",
            entityId = accountId,
            beforeStateJson = "{\"openingBalance\": ${account.openingBalanceInCents}}",
            afterStateJson = "{\"openingBalance\": $openingBalanceInCents, \"currentBalance\": $newCurrentBalance}",
        )
    }

    override suspend fun addFund(fund: FundEntity) {
        fundDao.insertFund(fund)
    }

    override fun getActiveFunds(): Flow<List<FundEntity>> {
        return fundDao.getActiveFunds()
    }
}
