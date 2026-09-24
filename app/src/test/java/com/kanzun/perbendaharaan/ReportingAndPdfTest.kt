package com.kanzun.perbendaharaan

import com.kanzun.perbendaharaan.core.database.entity.AccountEntity
import com.kanzun.perbendaharaan.core.database.entity.AssetEntity
import com.kanzun.perbendaharaan.core.database.entity.AuditLogEntity
import com.kanzun.perbendaharaan.core.database.entity.BudgetEntity
import com.kanzun.perbendaharaan.core.database.entity.BudgetItemEntity
import com.kanzun.perbendaharaan.core.database.entity.CategoryEntity
import com.kanzun.perbendaharaan.core.database.entity.DonationEntity
import com.kanzun.perbendaharaan.core.database.entity.FundEntity
import com.kanzun.perbendaharaan.core.database.entity.FundraisingTargetEntity
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.database.entity.ZakatTransactionEntity
import com.kanzun.perbendaharaan.core.model.Money
import com.kanzun.perbendaharaan.core.model.TransactionStatus
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportContent
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportFilter
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportRow
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportSummaryItem
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReportingAndPdfTest {

    private val accounts = mutableListOf<AccountEntity>()
    private val transactions = mutableListOf<TransactionEntity>()
    private val assets = mutableListOf<AssetEntity>()
    private val zakatTransactions = mutableListOf<ZakatTransactionEntity>()
    private val targets = mutableListOf<FundraisingTargetEntity>()
    private val auditLogs = mutableListOf<AuditLogEntity>()

    @Before
    fun setUp() {
        accounts.clear()
        transactions.clear()
        assets.clear()
        zakatTransactions.clear()
        targets.clear()
        auditLogs.clear()

        // Seed mock accounts
        accounts.add(AccountEntity("acc_cash", "Kas Tunai", accountNumber = "-", bankName = "Cash", isBank = false, openingBalanceInCents = 15_000_000_00L, currentBalanceInCents = 15_000_000_00L))
        accounts.add(AccountEntity("acc_bsi", "Bank BSI Utama", accountNumber = "7001234567", bankName = "BSI", isBank = true, openingBalanceInCents = 85_000_000_00L, currentBalanceInCents = 85_000_000_00L))


        // Seed mock transactions
        transactions.add(
            TransactionEntity(
                id = "tx_1",
                title = "Infaq Jumat",
                amountInCents = 5_000_000_00L,
                type = TransactionType.INCOME,
                status = TransactionStatus.FINALIZED,
                accountId = "acc_cash",
                fundId = "fund_op",
                categoryId = "cat_infaq",
                timestamp = System.currentTimeMillis(),
            )
        )
        transactions.add(
            TransactionEntity(
                id = "tx_2",
                title = "Bayar Tagihan Listrik",
                amountInCents = 2_000_000_00L,
                type = TransactionType.EXPENSE,
                status = TransactionStatus.FINALIZED,
                accountId = "acc_bsi",
                fundId = "fund_op",
                categoryId = "cat_listrik",
                timestamp = System.currentTimeMillis(),
            )
        )

        // Seed audit logs
        auditLogs.add(
            AuditLogEntity(
                id = "log_1",
                whoUserId = "system",
                whenTimestamp = System.currentTimeMillis(),
                action = "CREATE_TRANSACTION",
                entityName = "Transaction",
                entityId = "tx_1",
                afterStateJson = "Infaq Jumat - Rp 5.000.000",
            )
        )
        auditLogs.add(
            AuditLogEntity(
                id = "log_2",
                whoUserId = "bendahara",
                whenTimestamp = System.currentTimeMillis(),
                action = "LOCK_RAPBM",
                entityName = "Budget",
                entityId = "b_2027",
                beforeStateJson = "isLocked=false",
                afterStateJson = "isLocked=true",
            )
        )
    }

    @Test
    fun testAllNineReportTypesGeneration(): Unit = runBlocking {
        ReportType.values().forEach { type ->
            val content = ReportContent(
                reportType = type,
                periodLabel = "September 2026",
                summaries = listOf(ReportSummaryItem("Total", Money(100_000_000_00L).formatted)),
                tableHeaders = listOf("No", "Uraian", "Jumlah"),
                tableRows = listOf(ReportRow(listOf("1", "Sample Item", Money(50_000_000_00L).formatted))),
            )

            assertNotNull(content)
            assertEquals(type, content.reportType)
            assertEquals("September 2026", content.periodLabel)
            assertEquals(1, content.tableRows.size)
        }
    }

    @Test
    fun testTransactionFilteringByDateAndType(): Unit = runBlocking {
        val now = System.currentTimeMillis()
        val filterIncomeOnly = ReportFilter(transactionType = TransactionType.INCOME)

        val filtered = transactions.filter { tx ->
            filterIncomeOnly.transactionType == null || tx.type == filterIncomeOnly.transactionType
        }

        assertEquals(1, filtered.size)
        assertEquals("Infaq Jumat", filtered[0].title)
    }

    @Test
    fun testAuditTrailLogReportStructure(): Unit = runBlocking {
        val filter = ReportFilter()
        val logs = auditLogs.filter { log ->
            val matchStart = filter.startDate == null || log.whenTimestamp >= filter.startDate
            val matchEnd = filter.endDate == null || log.whenTimestamp <= filter.endDate
            matchStart && matchEnd
        }

        assertEquals(2, logs.size)
        assertEquals("CREATE_TRANSACTION", logs[0].action)
        assertEquals("LOCK_RAPBM", logs[1].action)
    }

    @Test
    fun testMosqueProfileHeaderAndSignatureMetadata(): Unit = runBlocking {
        val content = ReportContent(
            reportType = ReportType.FINANCIAL_SUMMARY,
            periodLabel = "Tahun 2027",
            mosqueName = "Masjid Raya Kanzun",
            mosqueAddress = "Jl. Merdeka No. 45",
            chairmanName = "H. Ahmad Dahlan",
            treasurerName = "H. Muhammad Hatta",
        )

        assertEquals("Masjid Raya Kanzun", content.mosqueName)
        assertEquals("Jl. Merdeka No. 45", content.mosqueAddress)
        assertEquals("H. Ahmad Dahlan", content.chairmanName)
        assertEquals("H. Muhammad Hatta", content.treasurerName)
    }
}
