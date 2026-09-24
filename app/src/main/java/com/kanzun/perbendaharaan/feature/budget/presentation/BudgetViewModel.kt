package com.kanzun.perbendaharaan.feature.budget.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanzun.perbendaharaan.core.database.entity.AuditLogEntity
import com.kanzun.perbendaharaan.core.database.entity.BudgetEntity
import com.kanzun.perbendaharaan.core.database.entity.BudgetItemEntity
import com.kanzun.perbendaharaan.core.database.entity.CategoryEntity
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.feature.budget.domain.repository.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

enum class BudgetVarianceStatus {
    UNDER_BUDGET,
    ON_BUDGET,
    OVER_BUDGET,
}

data class CategoryBudgetVsActual(
    val categoryId: String,
    val categoryName: String,
    val isExpense: Boolean,
    val plannedAmountInCents: Long,
    val actualAmountInCents: Long,
) {
    val varianceInCents: Long
        get() = if (isExpense) {
            plannedAmountInCents - actualAmountInCents
        } else {
            actualAmountInCents - plannedAmountInCents
        }

    val varianceStatus: BudgetVarianceStatus
        get() = when {
            actualAmountInCents < plannedAmountInCents -> BudgetVarianceStatus.UNDER_BUDGET
            actualAmountInCents == plannedAmountInCents -> BudgetVarianceStatus.ON_BUDGET
            else -> BudgetVarianceStatus.OVER_BUDGET
        }
}

data class BudgetUiState(
    val searchQuery: String = "",
    val allBudgets: List<BudgetEntity> = emptyList(),
    val filteredBudgets: List<BudgetEntity> = emptyList(),
    val selectedBudgetForDetail: BudgetEntity? = null,
    val detailIncomeItems: List<CategoryBudgetVsActual> = emptyList(),
    val detailExpenseItems: List<CategoryBudgetVsActual> = emptyList(),
    
    val totalPlannedIncomeInCents: Long = 0,
    val totalActualIncomeInCents: Long = 0,
    val totalPlannedExpenseInCents: Long = 0,
    val totalActualExpenseInCents: Long = 0,
    val netPlannedBalanceInCents: Long = 0,
    val netActualBalanceInCents: Long = 0,
    
    val categories: List<CategoryEntity> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    
    val isCreateEditOpen: Boolean = false,
    val isDeleteConfirmOpen: Boolean = false,
    val selectedBudgetForAction: BudgetEntity? = null,
    
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedDetailBudget = MutableStateFlow<BudgetEntity?>(null)
    private val _isCreateEditOpen = MutableStateFlow(false)
    private val _isDeleteConfirmOpen = MutableStateFlow(false)
    private val _selectedActionBudget = MutableStateFlow<BudgetEntity?>(null)
    private val _errorMessage = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _detailBudgetItems: Flow<List<BudgetItemEntity>> = _selectedDetailBudget.flatMapLatest { detail ->
        if (detail == null) {
            flowOf(emptyList())
        } else {
            budgetRepository.getBudgetItemsForBudget(detail.id)
        }
    }



    val uiState: StateFlow<BudgetUiState> = combine(
        _searchQuery,
        _selectedDetailBudget,
        _isCreateEditOpen,
        _isDeleteConfirmOpen,
        _selectedActionBudget,
        _errorMessage,
        budgetRepository.getAllBudgets(),
        budgetRepository.getCategories(),
        budgetRepository.getTransactions(),
        _detailBudgetItems,
    ) { flows ->
        val query = flows[0] as String
        val detailBudget = flows[1] as BudgetEntity?
        val isCreateEdit = flows[2] as Boolean
        val isDelete = flows[3] as Boolean
        val actionBudget = flows[4] as BudgetEntity?
        val error = flows[5] as String?
        @Suppress("UNCHECKED_CAST")
        val budgets = flows[6] as List<BudgetEntity>
        @Suppress("UNCHECKED_CAST")
        val categories = flows[7] as List<CategoryEntity>
        @Suppress("UNCHECKED_CAST")
        val transactions = flows[8] as List<TransactionEntity>
        @Suppress("UNCHECKED_CAST")
        val budgetItems = flows[9] as List<BudgetItemEntity>

        val filtered = if (query.isBlank()) {
            budgets
        } else {
            budgets.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.year.toString().contains(query)
            }
        }

        // If detail view is open, calculate category-level actuals from Arus Kas
        var incItems = emptyList<CategoryBudgetVsActual>()
        var expItems = emptyList<CategoryBudgetVsActual>()
        var totalPlannedInc = 0L
        var totalActualInc = 0L
        var totalPlannedExp = 0L
        var totalActualExp = 0L

        val currentDetail = detailBudget?.let { b -> budgets.find { it.id == b.id } ?: b }

        if (currentDetail != null) {
            val (startMs, endMs) = getPeriodTimestampsForBudget(currentDetail)

            // Filter transactions by period date range
            val periodTxs = transactions.filter { tx ->
                tx.timestamp in startMs..endMs && !tx.isReversed
            }

            val actualsByCat = periodTxs.groupBy { it.categoryId }
                .mapValues { entry -> entry.value.sumOf { it.amountInCents } }

            val incBudgets = budgetItems.filter { !it.isExpense }
            val expBudgets = budgetItems.filter { it.isExpense }

            incItems = incBudgets.map { item ->
                val cat = categories.find { it.id == item.categoryId }
                val name = cat?.name ?: item.categoryId.replace("cat_", "").capitalize(Locale.ROOT)
                val actual = actualsByCat[item.categoryId] ?: 0L
                CategoryBudgetVsActual(
                    categoryId = item.categoryId,
                    categoryName = name,
                    isExpense = false,
                    plannedAmountInCents = item.plannedAmountInCents,
                    actualAmountInCents = actual,
                )
            }

            expItems = expBudgets.map { item ->
                val cat = categories.find { it.id == item.categoryId }
                val name = cat?.name ?: item.categoryId.replace("cat_", "").capitalize(Locale.ROOT)
                val actual = actualsByCat[item.categoryId] ?: 0L
                CategoryBudgetVsActual(
                    categoryId = item.categoryId,
                    categoryName = name,
                    isExpense = true,
                    plannedAmountInCents = item.plannedAmountInCents,
                    actualAmountInCents = actual,
                )
            }

            totalPlannedInc = incItems.sumOf { it.plannedAmountInCents }
            totalActualInc = incItems.sumOf { it.actualAmountInCents }
            totalPlannedExp = expItems.sumOf { it.plannedAmountInCents }
            totalActualExp = expItems.sumOf { it.actualAmountInCents }
        }

        BudgetUiState(
            searchQuery = query,
            allBudgets = budgets,
            filteredBudgets = filtered,
            selectedBudgetForDetail = currentDetail,
            detailIncomeItems = incItems,
            detailExpenseItems = expItems,
            totalPlannedIncomeInCents = totalPlannedInc,
            totalActualIncomeInCents = totalActualInc,
            totalPlannedExpenseInCents = totalPlannedExp,
            totalActualExpenseInCents = totalActualExp,
            netPlannedBalanceInCents = totalPlannedInc - totalPlannedExp,
            netActualBalanceInCents = totalActualInc - totalActualExp,
            categories = categories,
            transactions = transactions,
            isCreateEditOpen = isCreateEdit,
            isDeleteConfirmOpen = isDelete,
            selectedBudgetForAction = actionBudget,
            isLoading = false,
            errorMessage = error,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudgetUiState(),
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun openDetail(budget: BudgetEntity) {
        _selectedDetailBudget.value = budget
    }

    fun closeDetail() {
        _selectedDetailBudget.value = null
    }

    fun openCreateDialog() {
        _selectedActionBudget.value = null
        _isCreateEditOpen.value = true
    }

    fun openEditDialog(budget: BudgetEntity) {
        _selectedActionBudget.value = budget
        _isCreateEditOpen.value = true
    }

    fun closeCreateEditDialog() {
        _isCreateEditOpen.value = false
        _selectedActionBudget.value = null
    }

    fun openDeleteDialog(budget: BudgetEntity) {
        _selectedActionBudget.value = budget
        _isDeleteConfirmOpen.value = true
    }

    fun closeDeleteDialog() {
        _isDeleteConfirmOpen.value = false
        _selectedActionBudget.value = null
    }

    fun saveMonthlyBudget(
        monthYearTitle: String,
        year: Int,
        incomePlannedMap: Map<String, Long>,
        expensePlannedMap: Map<String, Long>,
        existingBudgetId: String? = null,
    ) {
        viewModelScope.launch {
            try {
                val budgetId = existingBudgetId ?: UUID.randomUUID().toString()

                val items = mutableListOf<BudgetItemEntity>()

                incomePlannedMap.forEach { (catId, plannedCents) ->
                    items.add(
                        BudgetItemEntity(
                            id = UUID.randomUUID().toString(),
                            budgetId = budgetId,
                            categoryId = catId,
                            plannedAmountInCents = plannedCents,
                            isExpense = false,
                        )
                    )
                }

                expensePlannedMap.forEach { (catId, plannedCents) ->
                    items.add(
                        BudgetItemEntity(
                            id = UUID.randomUUID().toString(),
                            budgetId = budgetId,
                            categoryId = catId,
                            plannedAmountInCents = plannedCents,
                            isExpense = true,
                        )
                    )
                }

                budgetRepository.createOrUpdateBudget(
                    year = year,
                    title = monthYearTitle,
                    items = items,
                )

                _isCreateEditOpen.value = false
                _selectedActionBudget.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            try {
                budgetRepository.deleteBudget(budget.id)
                _isDeleteConfirmOpen.value = false
                _selectedActionBudget.value = null
                if (_selectedDetailBudget.value?.id == budget.id) {
                    _selectedDetailBudget.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    private fun getPeriodTimestampsForBudget(budget: BudgetEntity): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        val year = if (budget.year > 2020) budget.year else 2026

        // Parse month index from title (e.g. "RAPBM September 2026")
        var monthIndex = Calendar.SEPTEMBER
        val title = budget.title
        val monthNames = listOf("januari", "februari", "maret", "april", "mei", "juni", "juli", "agustus", "september", "oktober", "november", "desember")
        monthNames.forEachIndexed { idx, m ->
            if (title.contains(m, ignoreCase = true)) {
                monthIndex = idx
            }
        }

        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthIndex)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startMs = cal.timeInMillis

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val endMs = cal.timeInMillis

        return Pair(startMs, endMs)
    }
}
