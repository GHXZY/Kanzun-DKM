package com.kanzun.perbendaharaan.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanzun.perbendaharaan.core.database.dao.FundraisingTargetDao
import com.kanzun.perbendaharaan.core.database.entity.FundraisingTargetEntity
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.model.CashBreakdown
import com.kanzun.perbendaharaan.core.model.FundAllocation
import com.kanzun.perbendaharaan.core.model.FundAllocationItem
import com.kanzun.perbendaharaan.core.model.Money
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.core.util.DevDataSeeder
import com.kanzun.perbendaharaan.feature.financial.domain.repository.FinancialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Error(val message: String) : DashboardUiState
    data class Success(
        val totalCash: Money,
        val monthlyChange: Money,
        val cashBreakdown: CashBreakdown,
        val fundAllocation: FundAllocation,
        val incomeThisMonth: Money,
        val expenseThisMonth: Money,
        val netCashFlowThisMonth: Money,
        val activeTarget: FundraisingTargetEntity?,
        val recentTransactions: List<TransactionEntity>,
        val isEmpty: Boolean,
    ) : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: FinancialRepository,
    private val targetDao: FundraisingTargetDao,
    private val seeder: DevDataSeeder,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            try {
                // Check if accounts exist; if not, seed initial development data
                val breakdownBefore = repository.getCashBreakdown()
                if (breakdownBefore.items.isEmpty()) {
                    seeder.seedDevelopmentData()
                }

                repository.getTransactions()
                    .catch { e ->
                        _uiState.value = DashboardUiState.Error(e.message ?: "Gagal memuat data perbendaharaan")
                    }
                    .collect { transactions ->
                        val activeTransactions = transactions.filter { !it.isReversed }

                        val totalCash = repository.getTotalCash()
                        val cashBreakdown = repository.getCashBreakdown()
                        val rawFundAllocation = repository.getFundAllocation()

                        // Limit visible categories to top 5 and group the rest into "Lainnya"
                        val fundAllocation = processFundAllocationLimit(rawFundAllocation)

                        // Calculate income & expense for current month
                        val incomeCents = activeTransactions
                            .filter { it.type == TransactionType.INCOME }
                            .sumOf { it.amountInCents }

                        val expenseCents = activeTransactions
                            .filter { it.type == TransactionType.EXPENSE }
                            .sumOf { it.amountInCents }

                        val netCents = incomeCents - expenseCents

                        val incomeThisMonth = Money.of(incomeCents)
                        val expenseThisMonth = Money.of(expenseCents)
                        val netCashFlowThisMonth = Money.of(netCents)

                        val activeTarget = targetDao.getActiveTarget()
                        val recentTransactions = activeTransactions.take(5)

                        _uiState.value = DashboardUiState.Success(
                            totalCash = totalCash,
                            monthlyChange = netCashFlowThisMonth,
                            cashBreakdown = cashBreakdown,
                            fundAllocation = fundAllocation,
                            incomeThisMonth = incomeThisMonth,
                            expenseThisMonth = expenseThisMonth,
                            netCashFlowThisMonth = netCashFlowThisMonth,
                            activeTarget = activeTarget,
                            recentTransactions = recentTransactions,
                            isEmpty = activeTransactions.isEmpty() && cashBreakdown.items.isEmpty(),
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(e.message ?: "Terjadi kesalahan sistem")
            }
        }
    }

    private fun processFundAllocationLimit(allocation: FundAllocation): FundAllocation {
        val sorted = allocation.items.sortedByDescending { it.allocatedAmount.amountInCents }
        if (sorted.size <= 5) return allocation

        val top5 = sorted.take(5)
        val remaining = sorted.drop(5)

        val otherCents = remaining.sumOf { it.allocatedAmount.amountInCents }
        val otherPct = remaining.sumOf { it.percentage.toDouble() }.toFloat()

        val items = top5 + FundAllocationItem(
            fundId = "fund_other",
            fundName = "Lainnya",
            allocatedAmount = Money.of(otherCents),
            percentage = otherPct,
        )

        return FundAllocation(allocation.totalAllocated, items)
    }
}
