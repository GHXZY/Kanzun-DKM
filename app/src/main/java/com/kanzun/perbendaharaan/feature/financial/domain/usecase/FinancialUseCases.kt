package com.kanzun.perbendaharaan.feature.financial.domain.usecase

import com.kanzun.perbendaharaan.core.database.entity.AuditLogEntity
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.database.entity.TransferEntity
import com.kanzun.perbendaharaan.core.model.CashBreakdown
import com.kanzun.perbendaharaan.core.model.FundAllocation
import com.kanzun.perbendaharaan.core.model.Money
import com.kanzun.perbendaharaan.feature.financial.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateIncomeUseCase @Inject constructor(
    private val repository: FinancialRepository,
) {
    suspend operator fun invoke(
        title: String,
        amount: Money,
        accountId: String,
        fundId: String,
        categoryId: String,
        note: String = "",
        userId: String = "system",
    ): TransactionEntity {
        return repository.createIncome(title, amount, accountId, fundId, categoryId, note, userId)
    }
}

class CreateExpenseUseCase @Inject constructor(
    private val repository: FinancialRepository,
) {
    suspend operator fun invoke(
        title: String,
        amount: Money,
        accountId: String,
        fundId: String,
        categoryId: String,
        note: String = "",
        userId: String = "system",
    ): TransactionEntity {
        return repository.createExpense(title, amount, accountId, fundId, categoryId, note, userId)
    }
}

class CreateTransferUseCase @Inject constructor(
    private val repository: FinancialRepository,
) {
    suspend operator fun invoke(
        fromAccountId: String,
        toAccountId: String,
        amount: Money,
        note: String = "",
        userId: String = "system",
    ): TransferEntity {
        return repository.createTransfer(fromAccountId, toAccountId, amount, note, userId)
    }
}

class GetAccountBalanceUseCase @Inject constructor(
    private val repository: FinancialRepository,
) {
    suspend operator fun invoke(accountId: String): Money {
        return repository.getAccountBalance(accountId)
    }
}

class GetTotalCashUseCase @Inject constructor(
    private val repository: FinancialRepository,
) {
    suspend operator fun invoke(): Money {
        return repository.getTotalCash()
    }
}

class GetCashBreakdownUseCase @Inject constructor(
    private val repository: FinancialRepository,
) {
    suspend operator fun invoke(): CashBreakdown {
        return repository.getCashBreakdown()
    }
}

class GetFundAllocationUseCase @Inject constructor(
    private val repository: FinancialRepository,
) {
    suspend operator fun invoke(): FundAllocation {
        return repository.getFundAllocation()
    }
}

class GetTransactionsUseCase @Inject constructor(
    private val repository: FinancialRepository,
) {
    operator fun invoke(): Flow<List<TransactionEntity>> {
        return repository.getTransactions()
    }
}

class FinalizeTransactionUseCase @Inject constructor(
    private val repository: FinancialRepository,
) {
    suspend operator fun invoke(transactionId: String, userId: String = "system"): TransactionEntity {
        return repository.finalizeTransaction(transactionId, userId)
    }
}

class ReverseTransactionUseCase @Inject constructor(
    private val repository: FinancialRepository,
) {
    suspend operator fun invoke(transactionId: String, reason: String, userId: String = "system"): TransactionEntity {
        return repository.reverseTransaction(transactionId, reason, userId)
    }
}

class CreateAuditLogUseCase @Inject constructor(
    private val repository: FinancialRepository,
) {
    suspend operator fun invoke(
        whoUserId: String,
        action: String,
        entityName: String,
        entityId: String,
        beforeStateJson: String? = null,
        afterStateJson: String? = null,
    ): AuditLogEntity {
        return repository.createAuditLog(whoUserId, action, entityName, entityId, beforeStateJson, afterStateJson)
    }
}
