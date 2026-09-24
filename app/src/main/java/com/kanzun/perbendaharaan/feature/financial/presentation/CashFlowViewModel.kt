package com.kanzun.perbendaharaan.feature.financial.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanzun.perbendaharaan.core.database.dao.CategoryDao
import com.kanzun.perbendaharaan.core.database.entity.AccountEntity
import com.kanzun.perbendaharaan.core.database.entity.CategoryEntity
import com.kanzun.perbendaharaan.core.database.entity.FundEntity
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.model.Money
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.core.util.DevDataSeeder
import com.kanzun.perbendaharaan.feature.financial.domain.repository.FinancialRepository
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.CreateExpenseUseCase
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.CreateIncomeUseCase
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.CreateTransferUseCase
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.FinalizeTransactionUseCase
import com.kanzun.perbendaharaan.feature.financial.domain.usecase.ReverseTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CashFlowUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val userMessage: String? = null,
    val totalIncome: Money = Money.ZERO,
    val totalExpense: Money = Money.ZERO,
    val netCashFlow: Money = Money.ZERO,
    val allTransactions: List<TransactionEntity> = emptyList(),
    val filteredTransactions: List<TransactionEntity> = emptyList(),
    val accounts: List<AccountEntity> = emptyList(),
    val funds: List<FundEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val selectedSegment: Int = 0, // 0=Semua, 1=Pemasukan, 2=Pengeluaran, 3=Transfer
    val searchQuery: String = "",
    val selectedAccountId: String? = null,
    val selectedFundId: String? = null,
    val selectedCategoryId: String? = null,
    val selectedTransaction: TransactionEntity? = null,
    val isFormOpen: Boolean = false,
    val formType: TransactionType = TransactionType.INCOME,
    val isFinalizeConfirmOpen: Boolean = false,
    val isReversalConfirmOpen: Boolean = false,
)

@HiltViewModel
class CashFlowViewModel @Inject constructor(
    private val repository: FinancialRepository,
    private val categoryDao: CategoryDao,
    private val createIncomeUseCase: CreateIncomeUseCase,
    private val createExpenseUseCase: CreateExpenseUseCase,
    private val createTransferUseCase: CreateTransferUseCase,
    private val finalizeTransactionUseCase: FinalizeTransactionUseCase,
    private val reverseTransactionUseCase: ReverseTransactionUseCase,
    private val seeder: DevDataSeeder,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CashFlowUiState())
    val uiState: StateFlow<CashFlowUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                // Ensure initial seed data exists
                val breakdown = repository.getCashBreakdown()
                if (breakdown.items.isEmpty()) {
                    seeder.seedDevelopmentData()
                }

                val accounts = repository.getActiveAccounts().first()
                val funds = repository.getActiveFunds().first()
                val categories = categoryDao.getAllCategories().first()

                repository.getTransactions()
                    .catch { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Gagal memuat transaksi",
                        )
                    }
                    .collect { transactions ->
                        val activeTransactions = transactions.filter { !it.isReversed }

                        val incomeCents = activeTransactions
                            .filter { it.type == TransactionType.INCOME }
                            .sumOf { it.amountInCents }

                        val expenseCents = activeTransactions
                            .filter { it.type == TransactionType.EXPENSE }
                            .sumOf { it.amountInCents }

                        val netCents = incomeCents - expenseCents

                        val totalIncome = Money.of(incomeCents)
                        val totalExpense = Money.of(expenseCents)
                        val netCashFlow = Money.of(netCents)

                        val currentState = _uiState.value
                        val filtered = applyFilters(
                            transactions = transactions,
                            segment = currentState.selectedSegment,
                            query = currentState.searchQuery,
                            accountId = currentState.selectedAccountId,
                            fundId = currentState.selectedFundId,
                            categoryId = currentState.selectedCategoryId,
                        )

                        _uiState.value = currentState.copy(
                            isLoading = false,
                            totalIncome = totalIncome,
                            totalExpense = totalExpense,
                            netCashFlow = netCashFlow,
                            allTransactions = transactions,
                            filteredTransactions = filtered,
                            accounts = accounts,
                            funds = funds,
                            categories = categories,
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Terjadi kesalahan sistem",
                )
            }
        }
    }

    fun setSegment(segment: Int) {
        val currentState = _uiState.value
        val filtered = applyFilters(
            transactions = currentState.allTransactions,
            segment = segment,
            query = currentState.searchQuery,
            accountId = currentState.selectedAccountId,
            fundId = currentState.selectedFundId,
            categoryId = currentState.selectedCategoryId,
        )
        _uiState.value = currentState.copy(selectedSegment = segment, filteredTransactions = filtered)
    }

    fun setSearchQuery(query: String) {
        val currentState = _uiState.value
        val filtered = applyFilters(
            transactions = currentState.allTransactions,
            segment = currentState.selectedSegment,
            query = query,
            accountId = currentState.selectedAccountId,
            fundId = currentState.selectedFundId,
            categoryId = currentState.selectedCategoryId,
        )
        _uiState.value = currentState.copy(searchQuery = query, filteredTransactions = filtered)
    }

    fun setAccountFilter(accountId: String?) {
        val currentState = _uiState.value
        val filtered = applyFilters(
            transactions = currentState.allTransactions,
            segment = currentState.selectedSegment,
            query = currentState.searchQuery,
            accountId = accountId,
            fundId = currentState.selectedFundId,
            categoryId = currentState.selectedCategoryId,
        )
        _uiState.value = currentState.copy(selectedAccountId = accountId, filteredTransactions = filtered)
    }

    fun setFundFilter(fundId: String?) {
        val currentState = _uiState.value
        val filtered = applyFilters(
            transactions = currentState.allTransactions,
            segment = currentState.selectedSegment,
            query = currentState.searchQuery,
            accountId = currentState.selectedAccountId,
            fundId = fundId,
            categoryId = currentState.selectedCategoryId,
        )
        _uiState.value = currentState.copy(selectedFundId = fundId, filteredTransactions = filtered)
    }

    fun setCategoryFilter(categoryId: String?) {
        val currentState = _uiState.value
        val filtered = applyFilters(
            transactions = currentState.allTransactions,
            segment = currentState.selectedSegment,
            query = currentState.searchQuery,
            accountId = currentState.selectedAccountId,
            fundId = currentState.selectedFundId,
            categoryId = categoryId,
        )
        _uiState.value = currentState.copy(selectedCategoryId = categoryId, filteredTransactions = filtered)
    }

    fun openForm(type: TransactionType) {
        _uiState.value = _uiState.value.copy(isFormOpen = true, formType = type)
    }

    fun closeForm() {
        _uiState.value = _uiState.value.copy(isFormOpen = false)
    }

    fun openDetail(transaction: TransactionEntity) {
        _uiState.value = _uiState.value.copy(selectedTransaction = transaction)
    }

    fun closeDetail() {
        _uiState.value = _uiState.value.copy(selectedTransaction = null)
    }

    fun openFinalizeConfirm() {
        _uiState.value = _uiState.value.copy(isFinalizeConfirmOpen = true)
    }

    fun closeFinalizeConfirm() {
        _uiState.value = _uiState.value.copy(isFinalizeConfirmOpen = false)
    }

    fun openReversalConfirm() {
        _uiState.value = _uiState.value.copy(isReversalConfirmOpen = true)
    }

    fun closeReversalConfirm() {
        _uiState.value = _uiState.value.copy(isReversalConfirmOpen = false)
    }

    fun clearUserMessage() {
        _uiState.value = _uiState.value.copy(userMessage = null)
    }

    fun createIncome(
        title: String,
        amountInCents: Long,
        accountId: String,
        fundId: String,
        categoryId: String,
        note: String,
        ref: String,
    ) {
        viewModelScope.launch {
            try {
                val fullNote = if (ref.isNotBlank()) "$note [Ref: $ref]" else note
                createIncomeUseCase(
                    title = title,
                    amount = Money.of(amountInCents),
                    accountId = accountId,
                    fundId = fundId,
                    categoryId = categoryId,
                    note = fullNote,
                    userId = "user_bendahara",
                )
                _uiState.value = _uiState.value.copy(
                    isFormOpen = false,
                    userMessage = "Pemasukan berhasil dicatat",
                )
                loadData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Gagal mencatat pemasukan")
            }
        }
    }

    fun createExpense(
        title: String,
        amountInCents: Long,
        accountId: String,
        fundId: String,
        categoryId: String,
        note: String,
        ref: String,
    ) {
        viewModelScope.launch {
            try {
                val fullNote = if (ref.isNotBlank()) "$note [Ref: $ref]" else note
                createExpenseUseCase(
                    title = title,
                    amount = Money.of(amountInCents),
                    accountId = accountId,
                    fundId = fundId,
                    categoryId = categoryId,
                    note = fullNote,
                    userId = "user_bendahara",
                )
                _uiState.value = _uiState.value.copy(
                    isFormOpen = false,
                    userMessage = "Pengeluaran berhasil dicatat",
                )
                loadData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Gagal mencatat pengeluaran")
            }
        }
    }

    fun createTransfer(
        fromAccountId: String,
        toAccountId: String,
        amountInCents: Long,
        note: String,
        ref: String,
    ) {
        viewModelScope.launch {
            try {
                if (fromAccountId == toAccountId) {
                    _uiState.value = _uiState.value.copy(errorMessage = "Rekening asal dan tujuan tidak boleh sama")
                    return@launch
                }
                val fullNote = if (ref.isNotBlank()) "$note [Ref: $ref]" else note
                createTransferUseCase(
                    fromAccountId = fromAccountId,
                    toAccountId = toAccountId,
                    amount = Money.of(amountInCents),
                    note = fullNote,
                    userId = "user_bendahara",
                )
                _uiState.value = _uiState.value.copy(
                    isFormOpen = false,
                    userMessage = "Transfer kas berhasil dicatat",
                )
                loadData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Gagal mencatat transfer")
            }
        }
    }

    fun finalizeTransaction(transactionId: String) {
        viewModelScope.launch {
            try {
                finalizeTransactionUseCase(transactionId, "user_bendahara")
                _uiState.value = _uiState.value.copy(
                    isFinalizeConfirmOpen = false,
                    selectedTransaction = null,
                    userMessage = "Transaksi berhasil difinalisasi",
                )
                loadData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Gagal memfinalisasi transaksi")
            }
        }
    }

    fun reverseTransaction(transactionId: String, reason: String) {
        viewModelScope.launch {
            try {
                reverseTransactionUseCase(transactionId, reason, "user_bendahara")
                _uiState.value = _uiState.value.copy(
                    isReversalConfirmOpen = false,
                    selectedTransaction = null,
                    userMessage = "Koreksi/Koreksi Reversal berhasil diproses",
                )
                loadData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Gagal melakukan koreksi reversal")
            }
        }
    }

    private fun applyFilters(
        transactions: List<TransactionEntity>,
        segment: Int,
        query: String,
        accountId: String?,
        fundId: String?,
        categoryId: String?,
    ): List<TransactionEntity> {
        return transactions.filter { tx ->
            val matchesSegment = when (segment) {
                1 -> tx.type == TransactionType.INCOME
                2 -> tx.type == TransactionType.EXPENSE
                3 -> tx.type == TransactionType.TRANSFER
                else -> true
            }

            val matchesQuery = query.isBlank() ||
                tx.title.contains(query, ignoreCase = true) ||
                tx.note.contains(query, ignoreCase = true) ||
                tx.referenceNumber.contains(query, ignoreCase = true)

            val matchesAccount = accountId == null || tx.accountId == accountId
            val matchesFund = fundId == null || tx.fundId == fundId
            val matchesCategory = categoryId == null || tx.categoryId == categoryId

            matchesSegment && matchesQuery && matchesAccount && matchesFund && matchesCategory
        }
    }
}
