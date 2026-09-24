package com.kanzun.perbendaharaan

import com.kanzun.perbendaharaan.core.database.entity.AccountEntity
import com.kanzun.perbendaharaan.core.database.entity.FundEntity
import com.kanzun.perbendaharaan.core.model.Money
import com.kanzun.perbendaharaan.core.model.TransactionStatus
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.CreateExpenseUseCase
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.CreateIncomeUseCase
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.CreateTransferUseCase
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.FinalizeTransactionUseCase
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.ReverseTransactionUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CashFlowManagementTest {

    private lateinit var repository: TestFinancialRepository
    private lateinit var createIncomeUseCase: CreateIncomeUseCase
    private lateinit var createExpenseUseCase: CreateExpenseUseCase
    private lateinit var createTransferUseCase: CreateTransferUseCase
    private lateinit var finalizeTransactionUseCase: FinalizeTransactionUseCase
    private lateinit var reverseTransactionUseCase: ReverseTransactionUseCase

    @Before
    fun setUp() = runBlocking {
        repository = TestFinancialRepository()

        createIncomeUseCase = CreateIncomeUseCase(repository)
        createExpenseUseCase = CreateExpenseUseCase(repository)
        createTransferUseCase = CreateTransferUseCase(repository)
        finalizeTransactionUseCase = FinalizeTransactionUseCase(repository)
        reverseTransactionUseCase = ReverseTransactionUseCase(repository)

        // Seed initial accounts
        repository.addAccount(
            AccountEntity(
                id = "acc_bsi",
                name = "Bank BSI Utama",
                accountNumber = "7123456789",
                bankName = "Bank Syariah Indonesia",
                isBank = true,
                openingBalanceInCents = 100_000_000L,
                currentBalanceInCents = 100_000_000L,
            )
        )
        repository.addAccount(
            AccountEntity(
                id = "acc_cash",
                name = "Kas Tunai Bendahara",
                accountNumber = "CASH-001",
                bankName = "Kas Tunai",
                isBank = false,
                openingBalanceInCents = 25_000_000L,
                currentBalanceInCents = 25_000_000L,
            )
        )
        repository.addFund(
            FundEntity(
                id = "fund_ops",
                name = "Dana Operasional",
                description = "Pengeluaran operasional masjid",
            )
        )
    }

    @Test
    fun testIncomeCreationUpdatesAccountBalance() = runBlocking {
        val initialBalance = repository.getAccountBalance("acc_bsi")
        val incomeAmount = Money.of(15_000_000L)

        val tx = createIncomeUseCase(
            title = "Infaq Kotak Jumat Pekan 4",
            amount = incomeAmount,
            accountId = "acc_bsi",
            fundId = "fund_ops",
            categoryId = "cat_infaq",
            note = "Transfer BSI",
        )

        val finalBalance = repository.getAccountBalance("acc_bsi")
        assertEquals(TransactionType.INCOME, tx.type)
        assertEquals(initialBalance.amountInCents + incomeAmount.amountInCents, finalBalance.amountInCents)
    }

    @Test
    fun testExpenseCreationDecreasesAccountBalance() = runBlocking {
        val initialBalance = repository.getAccountBalance("acc_cash")
        val expenseAmount = Money.of(3_500_000L)

        val tx = createExpenseUseCase(
            title = "Pembayaran Listrik & Air",
            amount = expenseAmount,
            accountId = "acc_cash",
            fundId = "fund_ops",
            categoryId = "cat_listrik",
            note = "Kas Tunai",
        )

        val finalBalance = repository.getAccountBalance("acc_cash")
        assertEquals(TransactionType.EXPENSE, tx.type)
        assertEquals(initialBalance.amountInCents - expenseAmount.amountInCents, finalBalance.amountInCents)
    }

    @Test
    fun testTransferPreservesTotalCashAndIsIsolatedFromIncomeExpense() = runBlocking {
        val initialTotalCash = repository.getTotalCash()
        val transferAmount = Money.of(10_000_000L)

        createTransferUseCase(
            fromAccountId = "acc_bsi",
            toAccountId = "acc_cash",
            amount = transferAmount,
            note = "Penarikan Tunai dari Bank",
        )

        val finalTotalCash = repository.getTotalCash()

        // Total cash invariance rule
        assertEquals(initialTotalCash.amountInCents, finalTotalCash.amountInCents)
        assertEquals(90_000_000L, repository.getAccountBalance("acc_bsi").amountInCents)
        assertEquals(35_000_000L, repository.getAccountBalance("acc_cash").amountInCents)

        // Verify transfer is not recorded as income or expense in transactions list
        val incomeCount = repository.transactions.count { it.type == TransactionType.INCOME }
        val expenseCount = repository.transactions.count { it.type == TransactionType.EXPENSE }
        assertEquals(0, incomeCount)
        assertEquals(0, expenseCount)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testTransferBetweenIdenticalAccountsFails(): Unit = runBlocking {
        createTransferUseCase(
            fromAccountId = "acc_bsi",
            toAccountId = "acc_bsi",
            amount = Money.of(1_000_000L),
            note = "Transfer Ke Diri Sendiri",
        )
        Unit
    }

    @Test
    fun testFinalizedTransactionStatusLock() = runBlocking {
        val tx = createIncomeUseCase(
            title = "Donasi Pembangunan",
            amount = Money.of(50_000_000L),
            accountId = "acc_bsi",
            fundId = "fund_ops",
            categoryId = "cat_donasi",
        )

        assertEquals(TransactionStatus.RECORDED, tx.status)

        val finalizedTx = finalizeTransactionUseCase(tx.id)
        assertEquals(TransactionStatus.FINALIZED, finalizedTx.status)
    }

    @Test
    fun testReversalRestoresBalanceAndAuditTrail() = runBlocking {
        val initialBalance = repository.getAccountBalance("acc_bsi")

        val tx = createIncomeUseCase(
            title = "Infaq Salah Catat",
            amount = Money.of(8_000_000L),
            accountId = "acc_bsi",
            fundId = "fund_ops",
            categoryId = "cat_infaq",
        )

        assertNotEquals(initialBalance.amountInCents, repository.getAccountBalance("acc_bsi").amountInCents)

        val reversed = reverseTransactionUseCase(tx.id, reason = "Salah input nominal")
        assertTrue(reversed.isReversed)
        assertEquals(initialBalance.amountInCents, repository.getAccountBalance("acc_bsi").amountInCents)
    }
}
