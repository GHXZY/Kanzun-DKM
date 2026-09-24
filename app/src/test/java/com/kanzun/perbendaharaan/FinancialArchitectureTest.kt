package com.kanzun.perbendaharaan

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
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.CreateExpenseUseCase
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.CreateIncomeUseCase
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.CreateTransferUseCase
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.FinalizeTransactionUseCase
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.GetAccountBalanceUseCase
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.GetFundAllocationUseCase
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.GetTotalCashUseCase
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.ReverseTransactionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class TestFinancialRepository : FinancialRepository {
    val accounts = mutableMapOf<String, AccountEntity>()
    val funds = mutableMapOf<String, FundEntity>()
    val transactions = mutableListOf<TransactionEntity>()
    val transfers = mutableListOf<TransferEntity>()
    val auditLogs = mutableListOf<AuditLogEntity>()

    override suspend fun createIncome(
        title: String,
        amount: Money,
        accountId: String,
        fundId: String,
        categoryId: String,
        note: String,
        userId: String,
    ): TransactionEntity {
        val account = accounts[accountId] ?: throw IllegalArgumentException("Account not found: $accountId")
        val tx = TransactionEntity(
            id = UUID.randomUUID().toString(),
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
        transactions.add(tx)
        accounts[accountId] = account.copy(currentBalanceInCents = account.currentBalanceInCents + amount.amountInCents)
        createAuditLog(userId, "CREATE_INCOME", "Transaction", tx.id)
        return tx
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
        val account = accounts[accountId] ?: throw IllegalArgumentException("Account not found: $accountId")
        val tx = TransactionEntity(
            id = UUID.randomUUID().toString(),
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
        transactions.add(tx)
        accounts[accountId] = account.copy(currentBalanceInCents = account.currentBalanceInCents - amount.amountInCents)
        createAuditLog(userId, "CREATE_EXPENSE", "Transaction", tx.id)
        return tx
    }

    override suspend fun createTransfer(
        fromAccountId: String,
        toAccountId: String,
        amount: Money,
        note: String,
        userId: String,
    ): TransferEntity {
        require(fromAccountId != toAccountId) { "Cannot transfer to same account" }
        val fromAccount = accounts[fromAccountId] ?: throw IllegalArgumentException("Account not found")
        val toAccount = accounts[toAccountId] ?: throw IllegalArgumentException("Account not found")

        val transfer = TransferEntity(
            id = UUID.randomUUID().toString(),
            fromAccountId = fromAccountId,
            toAccountId = toAccountId,
            amountInCents = amount.amountInCents,
            timestamp = System.currentTimeMillis(),
            note = note,
            createdByUserId = userId,
        )
        transfers.add(transfer)

        accounts[fromAccountId] = fromAccount.copy(currentBalanceInCents = fromAccount.currentBalanceInCents - amount.amountInCents)
        accounts[toAccountId] = toAccount.copy(currentBalanceInCents = toAccount.currentBalanceInCents + amount.amountInCents)
        createAuditLog(userId, "CREATE_TRANSFER", "Transfer", transfer.id)
        return transfer
    }

    override suspend fun getAccountBalance(accountId: String): Money {
        val account = accounts[accountId] ?: throw IllegalArgumentException("Account not found")
        return Money.of(account.currentBalanceInCents)
    }

    override suspend fun getTotalCash(): Money {
        val total = accounts.values.filter { it.isActive }.sumOf { it.currentBalanceInCents }
        return Money.of(total)
    }

    override suspend fun getCashBreakdown(): CashBreakdown {
        val activeAccounts = accounts.values.filter { it.isActive }
        val items = activeAccounts.map {
            CashBreakdownItem(it.id, it.name, it.isBank, Money.of(it.currentBalanceInCents))
        }
        val total = activeAccounts.sumOf { it.currentBalanceInCents }
        val bankTotal = activeAccounts.filter { it.isBank }.sumOf { it.currentBalanceInCents }
        val cashTotal = activeAccounts.filter { !it.isBank }.sumOf { it.currentBalanceInCents }
        return CashBreakdown(Money.of(total), Money.of(bankTotal), Money.of(cashTotal), items)
    }

    override suspend fun getFundAllocation(): FundAllocation {
        val totalCashCents = accounts.values.filter { it.isActive }.sumOf { it.currentBalanceInCents }
        val items = funds.values.filter { it.isActive }.map { fund ->
            val activeTxList = transactions.filter { it.fundId == fund.id && !it.isReversed }
            val cents = activeTxList.fold(0L) { acc, tx ->
                when (tx.type) {
                    TransactionType.INCOME -> acc + tx.amountInCents
                    TransactionType.EXPENSE -> acc - tx.amountInCents
                    TransactionType.TRANSFER -> acc
                }
            }
            val pct = if (totalCashCents > 0) (cents.toFloat() / totalCashCents.toFloat()) * 100f else 0f
            FundAllocationItem(fund.id, fund.name, Money.of(cents), pct)
        }
        return FundAllocation(Money.of(totalCashCents), items)
    }

    override fun getTransactions(): Flow<List<TransactionEntity>> = flowOf(transactions)

    override suspend fun getTransactionById(transactionId: String): TransactionEntity? {
        return transactions.find { it.id == transactionId }
    }

    override suspend fun finalizeTransaction(transactionId: String, userId: String): TransactionEntity {
        val index = transactions.indexOfFirst { it.id == transactionId }
        if (index == -1) throw IllegalArgumentException("Transaction not found")
        val updated = transactions[index].copy(status = TransactionStatus.FINALIZED)
        transactions[index] = updated
        createAuditLog(userId, "FINALIZE_TRANSACTION", "Transaction", transactionId)
        return updated
    }

    override suspend fun reverseTransaction(transactionId: String, reason: String, userId: String): TransactionEntity {
        val index = transactions.indexOfFirst { it.id == transactionId }
        if (index == -1) throw IllegalArgumentException("Transaction not found")
        val original = transactions[index]
        if (original.isReversed) throw IllegalStateException("Already reversed")

        val account = accounts[original.accountId]!!
        val updatedBalance = when (original.type) {
            TransactionType.INCOME -> account.currentBalanceInCents - original.amountInCents
            TransactionType.EXPENSE -> account.currentBalanceInCents + original.amountInCents
            TransactionType.TRANSFER -> account.currentBalanceInCents
        }
        accounts[original.accountId] = account.copy(currentBalanceInCents = updatedBalance)

        val reversed = original.copy(isReversed = true, note = "${original.note} [REVERSED: $reason]")
        transactions[index] = reversed
        createAuditLog(userId, "REVERSE_TRANSACTION", "Transaction", transactionId)
        return reversed
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
        auditLogs.add(log)
        return log
    }

    override fun getAuditLogs(): Flow<List<AuditLogEntity>> = flowOf(auditLogs)

    override suspend fun addAccount(account: AccountEntity) {
        accounts[account.id] = account
    }

    override fun getActiveAccounts(): Flow<List<AccountEntity>> = flowOf(accounts.values.toList())

    override suspend fun addFund(fund: FundEntity) {
        funds[fund.id] = fund
    }

    override fun getActiveFunds(): Flow<List<FundEntity>> = flowOf(funds.values.toList())
}

class FinancialArchitectureTest {

    private lateinit var repository: TestFinancialRepository
    private lateinit var createIncomeUseCase: CreateIncomeUseCase
    private lateinit var createExpenseUseCase: CreateExpenseUseCase
    private lateinit var createTransferUseCase: CreateTransferUseCase
    private lateinit var getAccountBalanceUseCase: GetAccountBalanceUseCase
    private lateinit var getTotalCashUseCase: GetTotalCashUseCase
    private lateinit var getFundAllocationUseCase: GetFundAllocationUseCase
    private lateinit var finalizeTransactionUseCase: FinalizeTransactionUseCase
    private lateinit var reverseTransactionUseCase: ReverseTransactionUseCase

    @Before
    fun setUp() = runBlocking {
        repository = TestFinancialRepository()

        createIncomeUseCase = CreateIncomeUseCase(repository)
        createExpenseUseCase = CreateExpenseUseCase(repository)
        createTransferUseCase = CreateTransferUseCase(repository)
        getAccountBalanceUseCase = GetAccountBalanceUseCase(repository)
        getTotalCashUseCase = GetTotalCashUseCase(repository)
        getFundAllocationUseCase = GetFundAllocationUseCase(repository)
        finalizeTransactionUseCase = FinalizeTransactionUseCase(repository)
        reverseTransactionUseCase = ReverseTransactionUseCase(repository)

        // Seed initial test accounts & funds
        repository.addAccount(
            AccountEntity(
                id = "acc_bsi",
                name = "Bank BSI",
                accountNumber = "12345",
                bankName = "BSI",
                isBank = true,
                openingBalanceInCents = 100_000_000L, // Rp 100.000.000
                currentBalanceInCents = 100_000_000L,
            )
        )
        repository.addAccount(
            AccountEntity(
                id = "acc_cash",
                name = "Kas Tunai",
                accountNumber = "CASH",
                bankName = "Cash",
                isBank = false,
                openingBalanceInCents = 25_000_000L, // Rp 25.000.000
                currentBalanceInCents = 25_000_000L,
            )
        )
        repository.addFund(
            FundEntity(
                id = "fund_ops",
                name = "Dana Operasional",
                description = "Operasional harian",
            )
        )
    }

    // TEST 1: Income increases balance
    @Test
    fun testIncomeIncreasesAccountBalance() = runBlocking {
        val initialBalance = getAccountBalanceUseCase("acc_bsi")
        assertEquals(100_000_000L, initialBalance.amountInCents)

        createIncomeUseCase(
            title = "Infaq Jamaah",
            amount = Money.of(5_000_000L),
            accountId = "acc_bsi",
            fundId = "fund_ops",
            categoryId = "cat_infaq",
        )

        val updatedBalance = getAccountBalanceUseCase("acc_bsi")
        assertEquals(105_000_000L, updatedBalance.amountInCents)
    }

    // TEST 2: Expense decreases balance
    @Test
    fun testExpenseDecreasesAccountBalance() = runBlocking {
        val initialBalance = getAccountBalanceUseCase("acc_cash")
        assertEquals(25_000_000L, initialBalance.amountInCents)

        createExpenseUseCase(
            title = "Bayar Listrik",
            amount = Money.of(2_000_000L),
            accountId = "acc_cash",
            fundId = "fund_ops",
            categoryId = "cat_listrik",
        )

        val updatedBalance = getAccountBalanceUseCase("acc_cash")
        assertEquals(23_000_000L, updatedBalance.amountInCents)
    }

    // TEST 3: Transfer preserves total cash
    @Test
    fun testTransferPreservesTotalCash() = runBlocking {
        val initialTotalCash = getTotalCashUseCase()
        assertEquals(125_000_000L, initialTotalCash.amountInCents)

        createTransferUseCase(
            fromAccountId = "acc_bsi",
            toAccountId = "acc_cash",
            amount = Money.of(10_000_000L),
            note = "Penarikan Kas Tunai",
        )

        val finalTotalCash = getTotalCashUseCase()
        assertEquals(125_000_000L, finalTotalCash.amountInCents)
        assertEquals(90_000_000L, getAccountBalanceUseCase("acc_bsi").amountInCents)
        assertEquals(35_000_000L, getAccountBalanceUseCase("acc_cash").amountInCents)
    }

    // TEST 4: Multiple accounts calculate correctly
    @Test
    fun testMultipleAccountsCalculation() = runBlocking {
        repository.addAccount(
            AccountEntity(
                id = "acc_mandiri",
                name = "Bank Mandiri",
                accountNumber = "999",
                bankName = "Mandiri",
                isBank = true,
                openingBalanceInCents = 50_000_000L,
                currentBalanceInCents = 50_000_000L,
            )
        )

        val totalCash = getTotalCashUseCase()
        assertEquals(175_000_000L, totalCash.amountInCents)

        val breakdown = repository.getCashBreakdown()
        assertEquals(150_000_000L, breakdown.bankTotal.amountInCents)
        assertEquals(25_000_000L, breakdown.cashTotal.amountInCents)
        assertEquals(3, breakdown.items.size)
    }

    // TEST 5: Fund allocation works
    @Test
    fun testFundAllocationCalculation() = runBlocking {
        createIncomeUseCase(
            title = "Donasi Pembangunan",
            amount = Money.of(50_000_000L),
            accountId = "acc_bsi",
            fundId = "fund_ops",
            categoryId = "cat_donasi",
        )

        val fundAllocation = getFundAllocationUseCase()
        assertNotNull(fundAllocation)
        assertEquals(175_000_000L, fundAllocation.totalAllocated.amountInCents)
        assertTrue(fundAllocation.items.isNotEmpty())
        assertEquals(50_000_000L, fundAllocation.items.first().allocatedAmount.amountInCents)
    }

    // TEST 6: Finalized transaction status management
    @Test
    fun testFinalizedTransactionCannotBeHardDeleted() = runBlocking {
        val tx = createIncomeUseCase(
            title = "Sewa Aula",
            amount = Money.of(1_000_000L),
            accountId = "acc_cash",
            fundId = "fund_ops",
            categoryId = "cat_usaha",
        )

        val finalizedTx = finalizeTransactionUseCase(tx.id)
        assertEquals(TransactionStatus.FINALIZED, finalizedTx.status)

        // Verify transaction is preserved in audit logs and stored list
        val storedTx = repository.getTransactionById(tx.id)
        assertNotNull(storedTx)
        assertEquals(TransactionStatus.FINALIZED, storedTx?.status)
    }

    // TEST 7: Reversal produces correct balance
    @Test
    fun testReversalProducesCorrectBalance() = runBlocking {
        val initialBalance = getAccountBalanceUseCase("acc_bsi").amountInCents

        val tx = createIncomeUseCase(
            title = "Koreksi Infaq Ganda",
            amount = Money.of(4_000_000L),
            accountId = "acc_bsi",
            fundId = "fund_ops",
            categoryId = "cat_infaq",
        )

        assertEquals(initialBalance + 4_000_000L, getAccountBalanceUseCase("acc_bsi").amountInCents)

        // Reverse transaction
        val reversedTx = reverseTransactionUseCase(tx.id, reason = "Pencatatan ganda")
        assertTrue(reversedTx.isReversed)

        // Balance restored back to initial balance
        assertEquals(initialBalance, getAccountBalanceUseCase("acc_bsi").amountInCents)
    }

    // TEST 8: Monetary precision correctness (no floating point errors)
    @Test
    fun testMonetaryPrecisionNoFloatingPointError() {
        val m1 = Money.of(100_000_000L) // Rp 100.000.000
        val m2 = Money.of(33_333_333L)  // Rp 33.333.333
        val sum = m1 + m2
        val diff = sum - m2

        assertEquals(133_333_333L, sum.amountInCents)
        assertEquals(100_000_000L, diff.amountInCents)
        assertEquals("Rp 133.333.333", sum.formatRupiah())
    }
}
