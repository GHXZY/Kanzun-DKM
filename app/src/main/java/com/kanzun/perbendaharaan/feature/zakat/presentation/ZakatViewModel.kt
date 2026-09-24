package com.kanzun.perbendaharaan.feature.zakat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanzun.perbendaharaan.core.database.dao.MustahikDao
import com.kanzun.perbendaharaan.core.database.dao.ZakatDao
import com.kanzun.perbendaharaan.core.database.entity.MustahikEntity
import com.kanzun.perbendaharaan.core.database.entity.ZakatTransactionEntity
import com.kanzun.perbendaharaan.core.model.Money
import com.kanzun.perbendaharaan.feature.financial.domain.repository.FinancialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class ZakatActionForm {
    NONE,
    MUZAKKI,
    MUSTAHIK,
    PENYALURAN,
}

data class ZakatUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val totalReceived: Money = Money.ZERO,
    val totalDistributed: Money = Money.ZERO,
    val currentZakatBalance: Money = Money.ZERO,
    val selectedTab: Int = 0, // 0=Muzakki, 1=Mustahik, 2=Penyaluran
    val zakatTransactions: List<ZakatTransactionEntity> = emptyList(),
    val mustahiks: List<MustahikEntity> = emptyList(),
    val activeForm: ZakatActionForm = ZakatActionForm.NONE,
    val mustahikToEdit: MustahikEntity? = null,
    val mustahikToDelete: MustahikEntity? = null,
)

@HiltViewModel
class ZakatViewModel @Inject constructor(
    private val zakatDao: ZakatDao,
    private val mustahikDao: MustahikDao,
    private val financialRepository: FinancialRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ZakatUiState())
    val uiState: StateFlow<ZakatUiState> = _uiState.asStateFlow()

    init {
        loadZakatData()
    }

    fun loadZakatData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            mustahikDao.getAllMustahiks()
                .catch { e -> _uiState.value = _uiState.value.copy(errorMessage = e.message) }
                .collect { mustahikList ->
                    zakatDao.getAllZakatTransactions()
                        .catch { e -> _uiState.value = _uiState.value.copy(errorMessage = e.message) }
                        .collect { txList ->
                            val receivedCents = txList.filter { !it.isDistribution }.sumOf { it.amountInCents }
                            val distributedCents = txList.filter { it.isDistribution }.sumOf { it.amountInCents }
                            val balanceCents = receivedCents - distributedCents

                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                totalReceived = Money.of(receivedCents),
                                totalDistributed = Money.of(distributedCents),
                                currentZakatBalance = Money.of(balanceCents),
                                zakatTransactions = txList,
                                mustahiks = mustahikList,
                            )
                        }
                }
        }
    }

    fun setSelectedTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }

    fun openMuzakkiForm() {
        _uiState.value = _uiState.value.copy(activeForm = ZakatActionForm.MUZAKKI)
    }

    fun openMustahikForm(mustahik: MustahikEntity? = null) {
        _uiState.value = _uiState.value.copy(
            activeForm = ZakatActionForm.MUSTAHIK,
            mustahikToEdit = mustahik,
        )
    }

    fun openPenyaluranForm() {
        _uiState.value = _uiState.value.copy(activeForm = ZakatActionForm.PENYALURAN)
    }

    fun closeForm() {
        _uiState.value = _uiState.value.copy(
            activeForm = ZakatActionForm.NONE,
            mustahikToEdit = null,
        )
    }

    fun confirmDeleteMustahik(mustahik: MustahikEntity?) {
        _uiState.value = _uiState.value.copy(mustahikToDelete = mustahik)
    }

    fun deleteMustahik(mustahik: MustahikEntity) {
        viewModelScope.launch {
            mustahikDao.deleteMustahik(mustahik)
            _uiState.value = _uiState.value.copy(mustahikToDelete = null)
        }
    }

    fun addZakatReceipt(zakatType: String, amountCents: Long, donorName: String, accountId: String, note: String) {
        viewModelScope.launch {
            val name = if (donorName.isBlank()) "Hamba Allah" else donorName
            val tx = ZakatTransactionEntity(
                id = "z_${UUID.randomUUID()}",
                zakatType = zakatType,
                isDistribution = false,
                amountInCents = amountCents,
                muzakiOrMustahikName = name,
                timestamp = System.currentTimeMillis(),
                accountId = accountId,
                note = note,
            )
            zakatDao.insertZakatTransaction(tx)

            // Single Source of Truth: Create Income in Arus Kas & update account balance
            try {
                financialRepository.createIncome(
                    title = "Penerimaan $zakatType ($name)",
                    amount = Money.of(amountCents),
                    accountId = accountId,
                    fundId = "fund_zakat",
                    categoryId = "cat_zakat",
                    note = note.ifBlank { "Penerimaan $zakatType" },
                    userId = "system",
                )
            } catch (_: Exception) {}

            _uiState.value = _uiState.value.copy(activeForm = ZakatActionForm.NONE)
        }
    }

    fun addZakatDistribution(mustahikName: String, zakatType: String, amountCents: Long, accountId: String, note: String) {
        viewModelScope.launch {
            val tx = ZakatTransactionEntity(
                id = "z_${UUID.randomUUID()}",
                zakatType = zakatType,
                isDistribution = true,
                amountInCents = amountCents,
                muzakiOrMustahikName = mustahikName,
                timestamp = System.currentTimeMillis(),
                accountId = accountId,
                note = note,
            )
            zakatDao.insertZakatTransaction(tx)

            // Single Source of Truth: Create Expense in Arus Kas & update account balance
            try {
                financialRepository.createExpense(
                    title = "Penyaluran $zakatType ($mustahikName)",
                    amount = Money.of(amountCents),
                    accountId = accountId,
                    fundId = "fund_zakat",
                    categoryId = "cat_zakat",
                    note = note.ifBlank { "Penyaluran zakat kepada $mustahikName" },
                    userId = "system",
                )
            } catch (_: Exception) {}

            _uiState.value = _uiState.value.copy(activeForm = ZakatActionForm.NONE)
        }
    }

    fun saveMustahik(id: String?, name: String, asnaf: String, address: String, contact: String) {
        viewModelScope.launch {
            val mustahik = MustahikEntity(
                id = id ?: "m_${UUID.randomUUID()}",
                name = name,
                asnafCategory = asnaf,
                address = address,
                phone = contact,
            )
            if (id != null) {
                mustahikDao.updateMustahik(mustahik)
            } else {
                mustahikDao.insertMustahik(mustahik)
            }
            _uiState.value = _uiState.value.copy(
                activeForm = ZakatActionForm.NONE,
                mustahikToEdit = null,
            )
        }
    }
}
