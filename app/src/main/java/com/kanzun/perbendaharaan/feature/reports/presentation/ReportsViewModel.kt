package com.kanzun.perbendaharaan.feature.reports.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.model.CashBreakdown
import com.kanzun.perbendaharaan.core.model.FundAllocation
import com.kanzun.perbendaharaan.core.model.Money
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.core.util.pdf.PdfReportGenerator
import com.kanzun.perbendaharaan.feature.financial.domain.repository.FinancialRepository
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportContent
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportFilter
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportType
import com.kanzun.perbendaharaan.feature.reports.domain.repository.ReportRepository
import com.kanzun.perbendaharaan.feature.reports.presentation.components.CashFlowDataPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ReportsUiState(
    val selectedReportType: ReportType = ReportType.FINANCIAL_SUMMARY,
    val selectedPresetIndex: Int = 2, // 0 = 7 Hari, 1 = 30 Hari, 2 = Bulan Ini, 3 = Bulan Lalu, 4 = Tahun Ini
    val filter: ReportFilter = ReportFilter(),
    val reportContent: ReportContent? = null,
    val cashBreakdown: CashBreakdown = CashBreakdown(Money.ZERO, Money.ZERO, Money.ZERO, emptyList()),
    val fundAllocation: FundAllocation = FundAllocation(Money.ZERO, emptyList()),
    val cashFlowDataPoints: List<CashFlowDataPoint> = emptyList(),
    val filteredTransactions: List<TransactionEntity> = emptyList(),
    val transactionSegment: Int = 0, // 0 = Semua, 1 = Pemasukan, 2 = Pengeluaran
    val transactionSearchQuery: String = "",
    val pdfFilterMode: Int = 0, // 0 = Pilihan Bulan, 1 = Rentang Tanggal, 2 = Preset Cepat
    val pdfSelectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val pdfSelectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val pdfStartDate: Long? = null,
    val pdfEndDate: Long? = null,
    val pdfReportType: ReportType = ReportType.FINANCIAL_SUMMARY,
    val isExportBottomSheetOpen: Boolean = false,
    val isPreviewMode: Boolean = false,
    val isLoading: Boolean = false,
    val isGeneratingPdf: Boolean = false,
    val generatedPdfFile: File? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val financialRepository: FinancialRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        applyPeriodPreset(2) // Default "Bulan Ini"
    }

    fun openExportBottomSheet() {
        _uiState.value = _uiState.value.copy(isExportBottomSheetOpen = true, isPreviewMode = false)
    }

    fun closeExportBottomSheet() {
        _uiState.value = _uiState.value.copy(isExportBottomSheetOpen = false, isPreviewMode = false)
    }

    fun togglePreviewMode(showPreview: Boolean) {
        _uiState.value = _uiState.value.copy(isPreviewMode = showPreview)
    }

    fun setTransactionSegment(segment: Int) {
        _uiState.value = _uiState.value.copy(transactionSegment = segment)
    }

    fun onTransactionSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(transactionSearchQuery = query)
    }

    fun selectReportType(reportType: ReportType) {
        _uiState.value = _uiState.value.copy(selectedReportType = reportType)
        loadReportContent()
    }

    fun applyPeriodPreset(presetIndex: Int) {
        val calendar = Calendar.getInstance()
        val filter = when (presetIndex) {
            0 -> { // 7 Hari
                val end = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val start = calendar.timeInMillis
                ReportFilter(startDate = start, endDate = end)
            }
            1 -> { // 30 Hari
                val end = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_YEAR, -30)
                val start = calendar.timeInMillis
                ReportFilter(startDate = start, endDate = end)
            }
            2 -> { // Bulan Ini
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                val start = calendar.timeInMillis
                calendar.add(Calendar.MONTH, 1)
                val end = calendar.timeInMillis
                ReportFilter(startDate = start, endDate = end)
            }
            3 -> { // Bulan Lalu
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                val start = calendar.timeInMillis
                calendar.add(Calendar.MONTH, 1)
                val end = calendar.timeInMillis
                ReportFilter(startDate = start, endDate = end)
            }
            4 -> { // Tahun Ini
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                val start = calendar.timeInMillis
                calendar.add(Calendar.YEAR, 1)
                val end = calendar.timeInMillis
                ReportFilter(startDate = start, endDate = end, year = calendar.get(Calendar.YEAR))
            }
            else -> ReportFilter()
        }

        _uiState.value = _uiState.value.copy(selectedPresetIndex = presetIndex, filter = filter)
        loadReportContent()
    }

    fun updateFilter(filter: ReportFilter) {
        _uiState.value = _uiState.value.copy(filter = filter)
        loadReportContent()
    }

    fun loadReportContent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val state = _uiState.value
                val content = reportRepository.generateReport(state.selectedReportType, state.filter)

                val breakdown = financialRepository.getCashBreakdown()
                val allocation = financialRepository.getFundAllocation()
                val transactions = financialRepository.getTransactions().first()

                val dataPoints = aggregateCashFlowTrend(transactions, state.filter)
                val activeTxs = transactions.filter { tx ->
                    val matchStart = state.filter.startDate == null || tx.timestamp >= state.filter.startDate
                    val matchEnd = state.filter.endDate == null || tx.timestamp <= state.filter.endDate
                    val matchAccount = state.filter.accountId == null || tx.accountId == state.filter.accountId
                    val matchFund = state.filter.fundId == null || tx.fundId == state.filter.fundId
                    val matchCat = state.filter.categoryId == null || tx.categoryId == state.filter.categoryId

                    matchStart && matchEnd && matchAccount && matchFund && matchCat && !tx.isReversed
                }

                _uiState.value = _uiState.value.copy(
                    reportContent = content,
                    cashBreakdown = breakdown,
                    fundAllocation = allocation,
                    cashFlowDataPoints = dataPoints,
                    filteredTransactions = activeTxs,
                    isLoading = false,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    private fun aggregateCashFlowTrend(
        allTransactions: List<TransactionEntity>,
        filter: ReportFilter,
    ): List<CashFlowDataPoint> {
        val activeTxs = allTransactions.filter { tx ->
            val matchStart = filter.startDate == null || tx.timestamp >= filter.startDate
            val matchEnd = filter.endDate == null || tx.timestamp <= filter.endDate
            val matchAccount = filter.accountId == null || tx.accountId == filter.accountId
            val matchFund = filter.fundId == null || tx.fundId == filter.fundId
            val matchCat = filter.categoryId == null || tx.categoryId == filter.categoryId

            matchStart && matchEnd && matchAccount && matchFund && matchCat && !tx.isReversed
        }

        val dateFormat = SimpleDateFormat("dd MMM", Locale("id", "ID"))

        val grouped = activeTxs.groupBy { tx ->
            dateFormat.format(Date(tx.timestamp))
        }

        return grouped.map { (dateLabel, txs) ->
            val income = txs.filter { it.type == TransactionType.INCOME }.sumOf { it.amountInCents }
            val expense = txs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountInCents }
            CashFlowDataPoint(dateLabel = dateLabel, incomeInCents = income, expenseInCents = expense)
        }
    }

    fun setPdfFilterMode(mode: Int) {
        _uiState.value = _uiState.value.copy(pdfFilterMode = mode)
        preparePdfReportContent()
    }

    fun setPdfReportType(reportType: ReportType) {
        _uiState.value = _uiState.value.copy(pdfReportType = reportType)
        preparePdfReportContent()
    }

    fun setPdfMonthYear(month: Int, year: Int) {
        _uiState.value = _uiState.value.copy(pdfSelectedMonth = month, pdfSelectedYear = year)
        preparePdfReportContent()
    }

    fun setPdfCustomDateRange(startMs: Long, endMs: Long) {
        _uiState.value = _uiState.value.copy(pdfStartDate = startMs, pdfEndDate = endMs)
        preparePdfReportContent()
    }

    fun preparePdfReportContent() {
        viewModelScope.launch {
            val state = _uiState.value
            val pdfFilter = when (state.pdfFilterMode) {
                0 -> { // Pilihan Bulan Tertentu
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.YEAR, state.pdfSelectedYear)
                    cal.set(Calendar.MONTH, state.pdfSelectedMonth)
                    cal.set(Calendar.DAY_OF_MONTH, 1)
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    val start = cal.timeInMillis
                    cal.add(Calendar.MONTH, 1)
                    cal.add(Calendar.MILLISECOND, -1)
                    val end = cal.timeInMillis
                    ReportFilter(startDate = start, endDate = end, year = state.pdfSelectedYear)
                }
                1 -> { // Rentang Tanggal Custom
                    ReportFilter(
                        startDate = state.pdfStartDate ?: System.currentTimeMillis(),
                        endDate = state.pdfEndDate ?: System.currentTimeMillis(),
                    )
                }
                else -> state.filter
            }

            try {
                val content = reportRepository.generateReport(state.pdfReportType, pdfFilter)
                _uiState.value = _uiState.value.copy(reportContent = content)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun exportPdf(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingPdf = true)
            try {
                val state = _uiState.value
                val content = state.reportContent
                    ?: reportRepository.generateReport(state.pdfReportType, state.filter)

                val dir = File(context.cacheDir, "pdf_reports")
                if (!dir.exists()) dir.mkdirs()

                val fileName = "Laporan_${content.reportType.name}_${System.currentTimeMillis()}.pdf"
                val outFile = File(dir, fileName)

                val pdfFile = PdfReportGenerator.generatePdf(context, content, outFile)
                _uiState.value = _uiState.value.copy(isGeneratingPdf = false, generatedPdfFile = pdfFile)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isGeneratingPdf = false, errorMessage = e.message)
            }
        }
    }

    fun clearPdfFile() {
        _uiState.value = _uiState.value.copy(generatedPdfFile = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
