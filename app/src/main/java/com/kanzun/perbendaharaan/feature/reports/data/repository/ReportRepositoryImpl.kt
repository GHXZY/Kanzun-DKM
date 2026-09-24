package com.kanzun.perbendaharaan.feature.reports.data.repository

import com.kanzun.perbendaharaan.core.database.dao.AccountDao
import com.kanzun.perbendaharaan.core.database.dao.AssetDao
import com.kanzun.perbendaharaan.core.database.dao.AuditLogDao
import com.kanzun.perbendaharaan.core.database.dao.BudgetDao
import com.kanzun.perbendaharaan.core.database.dao.CategoryDao
import com.kanzun.perbendaharaan.core.database.dao.DonationDao
import com.kanzun.perbendaharaan.core.database.dao.FundDao
import com.kanzun.perbendaharaan.core.database.dao.FundraisingTargetDao
import com.kanzun.perbendaharaan.core.database.dao.TransactionDao
import com.kanzun.perbendaharaan.core.database.dao.ZakatDao
import com.kanzun.perbendaharaan.core.database.dao.SettingsDao
import com.kanzun.perbendaharaan.core.database.entity.AuditLogEntity
import com.kanzun.perbendaharaan.core.model.Money
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportContent
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportFilter
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportRow
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportSummaryItem
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportType
import com.kanzun.perbendaharaan.feature.reports.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.kanzun.perbendaharaan.core.util.formatCategoryName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val fundDao: FundDao,
    private val categoryDao: CategoryDao,
    private val budgetDao: BudgetDao,
    private val assetDao: AssetDao,
    private val zakatDao: ZakatDao,
    private val targetDao: FundraisingTargetDao,
    private val donationDao: DonationDao,
    private val auditLogDao: AuditLogDao,
    private val settingsDao: SettingsDao,
) : ReportRepository {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("id", "ID"))
    private val dateOnlyFormat = SimpleDateFormat("dd/MM/yyyy", Locale("id", "ID"))

    override suspend fun generateReport(reportType: ReportType, filter: ReportFilter): ReportContent {
        val periodLabel = buildPeriodLabel(filter)
        val mosque = settingsDao.getMosque().first()

        val baseReport = when (reportType) {
            ReportType.FINANCIAL_SUMMARY -> buildFinancialSummaryReport(filter, periodLabel)
            ReportType.CASH_FLOW -> buildCashFlowReport(filter, periodLabel)
            ReportType.TRANSACTION_HISTORY -> buildTransactionHistoryReport(filter, periodLabel)
            ReportType.RAPBM -> buildRapbmReport(filter, periodLabel)
            ReportType.BUDGET_VS_ACTUAL -> buildBudgetVsActualReport(filter, periodLabel)
            ReportType.ASSETS -> buildAssetReport(filter, periodLabel)
            ReportType.ZAKAT -> buildZakatReport(filter, periodLabel)
            ReportType.FUNDRAISING -> buildFundraisingReport(filter, periodLabel)
            ReportType.AUDIT_TRAIL -> buildAuditTrailReport(filter, periodLabel)
        }

        return baseReport.copy(
            mosqueName = mosque?.name?.ifBlank { "Masjid Agung Al-Mubarak" } ?: "Masjid Agung Al-Mubarak",
            mosqueAddress = mosque?.address?.ifBlank { "Jl. Ahmad Yani No. 45, Jakarta" } ?: "Jl. Ahmad Yani No. 45, Jakarta",
            mosquePhone = mosque?.phone?.ifBlank { "081234567890" } ?: "081234567890",
            mosqueEmail = mosque?.email?.ifBlank { "info@masjid-almubarak.org" } ?: "info@masjid-almubarak.org",
            chairmanName = mosque?.dkmChairmanName?.ifBlank { "H. Ahmad Dahlan" } ?: "H. Ahmad Dahlan",
            treasurerName = mosque?.treasurerName?.ifBlank { "H. Muhammad Hatta" } ?: "H. Muhammad Hatta",
            logoPath = mosque?.logoPath ?: "",
        )
    }

    override fun getAuditLogs(filter: ReportFilter): Flow<List<AuditLogEntity>> {
        return auditLogDao.getAllAuditLogs().map { logs ->
            logs.filter { log ->
                val matchStart = filter.startDate == null || log.whenTimestamp >= filter.startDate
                val matchEnd = filter.endDate == null || log.whenTimestamp <= filter.endDate
                matchStart && matchEnd
            }
        }
    }

    private suspend fun buildFinancialSummaryReport(filter: ReportFilter, periodLabel: String): ReportContent {
        val accounts = accountDao.getActiveAccounts().first()
        val allTxs = transactionDao.getAllTransactions().first()

        val totalCashBalance = accounts.sumOf { it.currentBalanceInCents }
        val incomeTxs = allTxs.filter { it.type == TransactionType.INCOME && !it.isReversed }
        val expenseTxs = allTxs.filter { it.type == TransactionType.EXPENSE && !it.isReversed }

        val totalIncome = incomeTxs.sumOf { it.amountInCents }
        val totalExpense = expenseTxs.sumOf { it.amountInCents }

        val headers = listOf("No", "Nama Rekening / Akun", "Tipe", "Saldo saat ini")
        val rows = accounts.mapIndexed { idx, acc ->
            ReportRow(
                listOf(
                    (idx + 1).toString(),
                    acc.name,
                    if (acc.isBank) "Bank" else "Kas Tunai",
                    Money(acc.currentBalanceInCents).formatted,
                )
            )
        }


        val summaries = listOf(
            ReportSummaryItem("Total Saldo Kas & Bank", Money(totalCashBalance).formatted),
            ReportSummaryItem("Total Akumulasi Pemasukan", Money(totalIncome).formatted),
            ReportSummaryItem("Total Akumulasi Pengeluaran", Money(totalExpense).formatted),
            ReportSummaryItem("Arus Kas Bersih (Surplus)", Money(totalIncome - totalExpense).formatted),
        )

        return ReportContent(
            reportType = ReportType.FINANCIAL_SUMMARY,
            periodLabel = periodLabel,
            summaries = summaries,
            tableHeaders = headers,
            tableRows = rows,
        )
    }

    private suspend fun buildCashFlowReport(filter: ReportFilter, periodLabel: String): ReportContent {
        val txs = getFilteredTransactions(filter)

        val totalIncome = txs.filter { it.type == TransactionType.INCOME }.sumOf { it.amountInCents }
        val totalExpense = txs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountInCents }

        val headers = listOf("No", "Tanggal", "Judul Transaksi", "Jenis", "Jumlah")
        val rows = txs.mapIndexed { idx, tx ->
            ReportRow(
                listOf(
                    (idx + 1).toString(),
                    dateOnlyFormat.format(Date(tx.timestamp)),
                    tx.title,
                    if (tx.type == TransactionType.INCOME) "Pemasukan" else "Pengeluaran",
                    Money(tx.amountInCents).formatted,
                )
            )
        }

        val summaries = listOf(
            ReportSummaryItem("Total Pemasukan", Money(totalIncome).formatted),
            ReportSummaryItem("Total Pengeluaran", Money(totalExpense).formatted),
            ReportSummaryItem("Arus Kas Bersih", Money(totalIncome - totalExpense).formatted),
        )

        return ReportContent(
            reportType = ReportType.CASH_FLOW,
            periodLabel = periodLabel,
            summaries = summaries,
            tableHeaders = headers,
            tableRows = rows,
        )
    }

    private suspend fun buildTransactionHistoryReport(filter: ReportFilter, periodLabel: String): ReportContent {
        val txs = getFilteredTransactions(filter)

        val headers = listOf("No", "Waktu", "Judul", "Kategori", "Tipe", "Status", "Nominal")
        val rows = txs.mapIndexed { idx, tx ->
            ReportRow(
                listOf(
                    (idx + 1).toString(),
                    dateFormat.format(Date(tx.timestamp)),
                    tx.title,
                    tx.categoryId.formatCategoryName(),
                    if (tx.type == TransactionType.INCOME) "Pemasukan" else "Pengeluaran",
                    tx.status.name,
                    Money(tx.amountInCents).formatted,
                )
            )
        }

        val summaries = listOf(
            ReportSummaryItem("Jumlah Transaksi", "${txs.size} transaksi"),
        )

        return ReportContent(
            reportType = ReportType.TRANSACTION_HISTORY,
            periodLabel = periodLabel,
            summaries = summaries,
            tableHeaders = headers,
            tableRows = rows,
        )
    }

    private suspend fun buildRapbmReport(filter: ReportFilter, periodLabel: String): ReportContent {
        val year = filter.year ?: 2027
        val budget = budgetDao.getBudgetByYear(year)
        val budgetItems = if (budget != null) {
            budgetDao.getBudgetItemsForBudget(budget.id).first()
        } else {
            emptyList()
        }

        val headers = listOf("No", "Kategori Anggaran", "Tipe Anggaran", "Rencana Alokasi (IDR)")
        val rows = budgetItems.mapIndexed { idx, item ->
            ReportRow(
                listOf(
                    (idx + 1).toString(),
                    item.categoryId.formatCategoryName(),
                    if (item.isExpense) "Belanja (Expense)" else "Pendapatan (Income)",
                    Money(item.plannedAmountInCents).formatted,
                )
            )
        }

        val summaries = listOf(
            ReportSummaryItem("Tahun RAPBM", year.toString()),
            ReportSummaryItem("Status Anggaran", if (budget?.isLocked == true) "Final / Dikunci" else "Draft"),
            ReportSummaryItem("Total Rencana Pendapatan", Money(budget?.totalIncomeBudgetInCents ?: 0L).formatted),
            ReportSummaryItem("Total Rencana Belanja", Money(budget?.totalExpenseBudgetInCents ?: 0L).formatted),
        )

        return ReportContent(
            reportType = ReportType.RAPBM,
            periodLabel = "Tahun $year",
            summaries = summaries,
            tableHeaders = headers,
            tableRows = rows,
        )
    }

    private suspend fun buildBudgetVsActualReport(filter: ReportFilter, periodLabel: String): ReportContent {
        val year = filter.year ?: 2027
        val budget = budgetDao.getBudgetByYear(year)
        val budgetItems = if (budget != null) budgetDao.getBudgetItemsForBudget(budget.id).first() else emptyList()
        val txs = transactionDao.getAllTransactions().first().filter { !it.isReversed }

        val actualsByCat = txs.groupBy { it.categoryId }.mapValues { entry -> entry.value.sumOf { it.amountInCents } }

        val headers = listOf("No", "Kategori", "Tipe", "Rencana", "Realisasi", "Selisih", "Status")
        val rows = budgetItems.mapIndexed { idx, item ->
            val actual = actualsByCat[item.categoryId] ?: 0L
            val planned = item.plannedAmountInCents
            val variance = actual - planned
            val statusStr = when {
                actual < planned -> "under budget"
                actual == planned -> "on budget"
                else -> "over budget"
            }

            ReportRow(
                listOf(
                    (idx + 1).toString(),
                    item.categoryId.formatCategoryName(),
                    if (item.isExpense) "Belanja" else "Pendapatan",
                    Money(planned).formatted,
                    Money(actual).formatted,
                    Money(variance).formatted,
                    statusStr,
                )
            )
        }

        val summaries = listOf(
            ReportSummaryItem("Tahun Anggaran", year.toString()),
            ReportSummaryItem("Total Rencana Pendapatan", Money(budget?.totalIncomeBudgetInCents ?: 0L).formatted),
            ReportSummaryItem("Total Rencana Belanja", Money(budget?.totalExpenseBudgetInCents ?: 0L).formatted),
        )

        return ReportContent(
            reportType = ReportType.BUDGET_VS_ACTUAL,
            periodLabel = "Tahun $year",
            summaries = summaries,
            tableHeaders = headers,
            tableRows = rows,
        )
    }

    private suspend fun buildAssetReport(filter: ReportFilter, periodLabel: String): ReportContent {
        val assets = assetDao.getAllAssets().first()

        val totalValue = assets.sumOf { it.acquisitionValueInCents }
        val headers = listOf("No", "Nama Aset", "Kategori", "Kondisi", "Lokasi", "Nilai Akuisisi")
        val rows = assets.mapIndexed { idx, asset ->
            ReportRow(
                listOf(
                    (idx + 1).toString(),
                    asset.name,
                    asset.categoryId.formatCategoryName(),
                    asset.conditionStatus,
                    asset.location,
                    Money(asset.acquisitionValueInCents).formatted,
                )
            )
        }

        val summaries = listOf(
            ReportSummaryItem("Total Unit Aset", "${assets.size} unit"),
            ReportSummaryItem("Total Nilai Inventaris", Money(totalValue).formatted),
        )

        return ReportContent(
            reportType = ReportType.ASSETS,
            periodLabel = periodLabel,
            summaries = summaries,
            tableHeaders = headers,
            tableRows = rows,
        )
    }

    private suspend fun buildZakatReport(filter: ReportFilter, periodLabel: String): ReportContent {
        val zakats = zakatDao.getAllZakatTransactions().first()

        val totalReceived = zakats.filter { !it.isDistribution }.sumOf { it.amountInCents }
        val totalDistributed = zakats.filter { it.isDistribution }.sumOf { it.amountInCents }

        val headers = listOf("No", "Waktu", "Jenis Zakat", "Tipe", "Muzaki/Mustahik", "Nominal")
        val rows = zakats.mapIndexed { idx, z ->
            ReportRow(
                listOf(
                    (idx + 1).toString(),
                    dateOnlyFormat.format(Date(z.timestamp)),
                    z.zakatType,
                    if (z.isDistribution) "Penyaluran" else "Penerimaan",
                    z.muzakiOrMustahikName,
                    Money(z.amountInCents).formatted,
                )
            )
        }

        val summaries = listOf(
            ReportSummaryItem("Total Penerimaan Zakat", Money(totalReceived).formatted),
            ReportSummaryItem("Total Penyaluran Zakat", Money(totalDistributed).formatted),
            ReportSummaryItem("Saldo Zakat Tersedia", Money(totalReceived - totalDistributed).formatted),
        )

        return ReportContent(
            reportType = ReportType.ZAKAT,
            periodLabel = periodLabel,
            summaries = summaries,
            tableHeaders = headers,
            tableRows = rows,
        )
    }

    private suspend fun buildFundraisingReport(filter: ReportFilter, periodLabel: String): ReportContent {
        val targets = targetDao.getAllTargets().first()

        val headers = listOf("No", "Program Target", "Target Nominal", "Terkumpul", "Status")
        val rows = targets.mapIndexed { idx, t ->
            ReportRow(
                listOf(
                    (idx + 1).toString(),
                    t.title,
                    Money(t.targetAmountInCents).formatted,
                    Money(t.collectedAmountInCents).formatted,
                    t.status,
                )
            )
        }

        val totalTargetSum = targets.sumOf { it.targetAmountInCents }
        val totalCollectedSum = targets.sumOf { it.collectedAmountInCents }

        val summaries = listOf(
            ReportSummaryItem("Total Target Program", Money(totalTargetSum).formatted),
            ReportSummaryItem("Total Terkumpul", Money(totalCollectedSum).formatted),
        )

        return ReportContent(
            reportType = ReportType.FUNDRAISING,
            periodLabel = periodLabel,
            summaries = summaries,
            tableHeaders = headers,
            tableRows = rows,
        )
    }

    private suspend fun buildAuditTrailReport(filter: ReportFilter, periodLabel: String): ReportContent {
        val logs = getAuditLogs(filter).first()

        val headers = listOf("No", "Waktu", "Pengguna", "Aksi", "Entitas", "ID Entitas", "Perubahan State")
        val rows = logs.mapIndexed { idx, log ->
            ReportRow(
                listOf(
                    (idx + 1).toString(),
                    dateFormat.format(Date(log.whenTimestamp)),
                    log.whoUserId,
                    log.action,
                    log.entityName,
                    log.entityId,
                    log.afterStateJson ?: (log.beforeStateJson ?: "-"),
                )
            )
        }

        val summaries = listOf(
            ReportSummaryItem("Total Catatan Audit", "${logs.size} log aktivitas"),
        )

        return ReportContent(
            reportType = ReportType.AUDIT_TRAIL,
            periodLabel = periodLabel,
            summaries = summaries,
            tableHeaders = headers,
            tableRows = rows,
        )
    }

    private suspend fun getFilteredTransactions(filter: ReportFilter) = transactionDao.getAllTransactions().first().filter { tx ->
        val matchStart = filter.startDate == null || tx.timestamp >= filter.startDate
        val matchEnd = filter.endDate == null || tx.timestamp <= filter.endDate
        val matchAccount = filter.accountId == null || tx.accountId == filter.accountId
        val matchFund = filter.fundId == null || tx.fundId == filter.fundId
        val matchCat = filter.categoryId == null || tx.categoryId == filter.categoryId
        val matchType = filter.transactionType == null || tx.type == filter.transactionType
        val matchStatus = filter.status == null || tx.status == filter.status

        matchStart && matchEnd && matchAccount && matchFund && matchCat && matchType && matchStatus
    }

    private fun buildPeriodLabel(filter: ReportFilter): String {
        return if (filter.startDate != null && filter.endDate != null) {
            "${dateOnlyFormat.format(Date(filter.startDate))} s.d. ${dateOnlyFormat.format(Date(filter.endDate))}"
        } else {
            "Semua Periode"
        }
    }
}
