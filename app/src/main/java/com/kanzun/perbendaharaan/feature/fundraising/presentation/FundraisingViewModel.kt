package com.kanzun.perbendaharaan.feature.fundraising.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanzun.perbendaharaan.core.database.dao.DonationDao
import com.kanzun.perbendaharaan.core.database.dao.FundraisingTargetDao
import com.kanzun.perbendaharaan.core.database.dao.TransactionDao
import com.kanzun.perbendaharaan.core.database.entity.DonationEntity
import com.kanzun.perbendaharaan.core.database.entity.FundraisingTargetEntity
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.model.Money
import com.kanzun.perbendaharaan.core.model.TransactionStatus
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.feature.financial.domain.repository.FinancialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class TargetDetailMetrics(
    val target: FundraisingTargetEntity,
    val collectedAmountInCents: Long,
    val distributedAmountInCents: Long,
    val availableRemainingInCents: Long,
    val donationAmountInCents: Long,
    val kasAllocationAmountInCents: Long,
    val collectedProgress: Float,
    val distributedProgress: Float,
    val incomingHistory: List<DonationEntity>,
    val distributionHistory: List<DonationEntity>,
)

data class FundraisingUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val totalTargetAmount: Money = Money.ZERO,
    val totalCollectedAmount: Money = Money.ZERO,
    val totalDistributedAmount: Money = Money.ZERO,
    val totalAvailableAmount: Money = Money.ZERO,
    val totalDonationAmount: Money = Money.ZERO,
    val totalKasAllocationAmount: Money = Money.ZERO,
    val overallProgress: Float = 0f,
    val allTargets: List<FundraisingTargetEntity> = emptyList(),
    val targetMetrics: Map<String, TargetDetailMetrics> = emptyMap(),
    val donations: List<DonationEntity> = emptyList(),
    
    // UI Dialog & View states
    val isTargetFormOpen: Boolean = false,
    val isFundFormOpen: Boolean = false,
    val isTransferFormOpen: Boolean = false,
    val isSalurkanFormOpen: Boolean = false,
    val isEditFormOpen: Boolean = false,
    val isCancelFormOpen: Boolean = false,
    
    val selectedTargetForDetail: FundraisingTargetEntity? = null,
    val selectedTargetForAction: FundraisingTargetEntity? = null,
)

@HiltViewModel
class FundraisingViewModel @Inject constructor(
    private val targetDao: FundraisingTargetDao,
    private val donationDao: DonationDao,
    private val transactionDao: TransactionDao,
    private val financialRepository: FinancialRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FundraisingUiState())
    val uiState: StateFlow<FundraisingUiState> = _uiState.asStateFlow()

    init {
        loadFundraisingData()
    }

    fun loadFundraisingData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            targetDao.getAllTargets()
                .catch { e -> _uiState.value = _uiState.value.copy(errorMessage = e.message) }
                .collect { targetList ->
                    if (targetList.isEmpty()) {
                        seedSampleTargets()
                    } else {
                        donationDao.getAllDonations()
                            .catch { e -> _uiState.value = _uiState.value.copy(errorMessage = e.message) }
                            .collect { donationList ->
                                computeAndEmitState(targetList, donationList)
                            }
                    }
                }
        }
    }

    private fun computeAndEmitState(
        targetList: List<FundraisingTargetEntity>,
        donationList: List<DonationEntity>,
    ) {
        val metricsMap = mutableMapOf<String, TargetDetailMetrics>()

        var totalTargetCents = 0L
        var totalCollectedCents = 0L
        var totalDistributedCents = 0L
        var totalDonationCents = 0L
        var totalKasAllocationCents = 0L

        targetList.forEach { target ->
            val targetDonations = donationList.filter { it.targetId == target.id }

            var collectedCents = 0L
            var distributedCents = 0L
            var donationCents = 0L
            var kasAllocationCents = 0L

            val incomingList = mutableListOf<DonationEntity>()
            val distributionList = mutableListOf<DonationEntity>()

            targetDonations.forEach { don ->
                val name = don.donorName
                when {
                    name.startsWith("Penyaluran:") || name.startsWith("Transfer Keluar:") -> {
                        distributedCents += don.amountInCents
                        distributionList.add(don)
                    }
                    name.startsWith("Alokasi Kas") || name.startsWith("Transfer Masuk:") -> {
                        kasAllocationCents += don.amountInCents
                        collectedCents += don.amountInCents
                        incomingList.add(don)
                    }
                    else -> {
                        donationCents += don.amountInCents
                        collectedCents += don.amountInCents
                        incomingList.add(don)
                    }
                }
            }

            val availableRemaining = (collectedCents - distributedCents).coerceAtLeast(0L)
            val collectedProgress = if (target.targetAmountInCents > 0) {
                (collectedCents.toFloat() / target.targetAmountInCents.toFloat()).coerceAtMost(1f)
            } else 0f
            val distributedProgress = if (target.targetAmountInCents > 0) {
                (distributedCents.toFloat() / target.targetAmountInCents.toFloat()).coerceAtMost(1f)
            } else 0f

            metricsMap[target.id] = TargetDetailMetrics(
                target = target,
                collectedAmountInCents = collectedCents,
                distributedAmountInCents = distributedCents,
                availableRemainingInCents = availableRemaining,
                donationAmountInCents = donationCents,
                kasAllocationAmountInCents = kasAllocationCents,
                collectedProgress = collectedProgress,
                distributedProgress = distributedProgress,
                incomingHistory = incomingList.sortedByDescending { it.timestamp },
                distributionHistory = distributionList.sortedByDescending { it.timestamp },
            )

            if (target.status != "Dibatalkan") {
                totalTargetCents += target.targetAmountInCents
                totalCollectedCents += collectedCents
                totalDistributedCents += distributedCents
                totalDonationCents += donationCents
                totalKasAllocationCents += kasAllocationCents
            }
        }

        val totalAvailableCents = (totalCollectedCents - totalDistributedCents).coerceAtLeast(0L)
        val overallProgress = if (totalTargetCents > 0) {
            (totalCollectedCents.toFloat() / totalTargetCents.toFloat()).coerceAtMost(1f)
        } else 0f

        // Keep active detail selection fresh if open
        val currentDetail = _uiState.value.selectedTargetForDetail
        val updatedDetail = currentDetail?.let { current -> targetList.find { it.id == current.id } }

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = null,
            totalTargetAmount = Money.of(totalTargetCents),
            totalCollectedAmount = Money.of(totalCollectedCents),
            totalDistributedAmount = Money.of(totalDistributedCents),
            totalAvailableAmount = Money.of(totalAvailableCents),
            totalDonationAmount = Money.of(totalDonationCents),
            totalKasAllocationAmount = Money.of(totalKasAllocationCents),
            overallProgress = overallProgress,
            allTargets = targetList,
            targetMetrics = metricsMap,
            donations = donationList,
            selectedTargetForDetail = updatedDetail,
        )
    }

    private suspend fun seedSampleTargets() {
        val now = System.currentTimeMillis()

        val t1 = FundraisingTargetEntity(
            id = "target_renov_masjid",
            title = "Renovasi Masjid",
            targetAmountInCents = 100_000_000L,
            collectedAmountInCents = 65_000_000L,
            startDate = now - 30 * 86400000L,
            endDate = now + 60 * 86400000L,
            status = "Aktif",
        )
        val t2 = FundraisingTargetEntity(
            id = "target_toilet",
            title = "Pembangunan Toilet",
            targetAmountInCents = 50_000_000L,
            collectedAmountInCents = 20_000_000L,
            startDate = now - 15 * 86400000L,
            endDate = now + 45 * 86400000L,
            status = "Aktif",
        )
        val t3 = FundraisingTargetEntity(
            id = "target_yatim",
            title = "Santunan Anak Yatim Ramadan",
            targetAmountInCents = 30_000_000L,
            collectedAmountInCents = 30_000_000L,
            startDate = now - 60 * 86400000L,
            endDate = now - 10 * 86400000L,
            status = "Selesai",
        )

        targetDao.insertTarget(t1)
        targetDao.insertTarget(t2)
        targetDao.insertTarget(t3)

        // Seed Donations & Kas Allocations & Distributions
        val d1 = DonationEntity(
            id = "don_renov_1",
            targetId = "target_renov_masjid",
            donorName = "Donasi: Bpk. H. Ahmad",
            amountInCents = 25_000_000L,
            timestamp = now - 22 * 86400000L,
            accountId = "acc_bsi",
        )
        val d2 = DonationEntity(
            id = "don_renov_2",
            targetId = "target_renov_masjid",
            donorName = "Donasi: Ibu Hj. Maryam",
            amountInCents = 15_000_000L,
            timestamp = now - 20 * 86400000L,
            accountId = "acc_bsi",
        )
        val d3 = DonationEntity(
            id = "don_renov_3",
            targetId = "target_renov_masjid",
            donorName = "Alokasi Kas Masjid",
            amountInCents = 25_000_000L,
            timestamp = now - 18 * 86400000L,
            accountId = "acc_cash",
        )
        val d4 = DonationEntity(
            id = "don_renov_4",
            targetId = "target_renov_masjid",
            donorName = "Penyaluran: Pembelian Material Semen & Cat",
            amountInCents = 20_000_000L,
            timestamp = now - 10 * 86400000L,
            accountId = "acc_cash",
        )

        val d5 = DonationEntity(
            id = "don_toilet_1",
            targetId = "target_toilet",
            donorName = "Donasi: Hamba Allah",
            amountInCents = 15_000_000L,
            timestamp = now - 12 * 86400000L,
            accountId = "acc_bsi",
        )
        val d6 = DonationEntity(
            id = "don_toilet_2",
            targetId = "target_toilet",
            donorName = "Alokasi Kas Masjid",
            amountInCents = 5_000_000L,
            timestamp = now - 8 * 86400000L,
            accountId = "acc_cash",
        )

        donationDao.insertDonation(d1)
        donationDao.insertDonation(d2)
        donationDao.insertDonation(d3)
        donationDao.insertDonation(d4)
        donationDao.insertDonation(d5)
        donationDao.insertDonation(d6)
    }

    // Modal Control Methods
    fun openTargetForm() {
        _uiState.value = _uiState.value.copy(isTargetFormOpen = true)
    }

    fun closeTargetForm() {
        _uiState.value = _uiState.value.copy(isTargetFormOpen = false)
    }

    fun openFundForm() {
        _uiState.value = _uiState.value.copy(isFundFormOpen = true)
    }

    fun closeFundForm() {
        _uiState.value = _uiState.value.copy(isFundFormOpen = false)
    }

    fun openTransferForm() {
        _uiState.value = _uiState.value.copy(isTransferFormOpen = true)
    }

    fun closeTransferForm() {
        _uiState.value = _uiState.value.copy(isTransferFormOpen = false)
    }

    fun openSalurkanForm(target: FundraisingTargetEntity) {
        _uiState.value = _uiState.value.copy(
            selectedTargetForAction = target,
            isSalurkanFormOpen = true,
        )
    }

    fun closeSalurkanForm() {
        _uiState.value = _uiState.value.copy(isSalurkanFormOpen = false, selectedTargetForAction = null)
    }

    fun openEditForm(target: FundraisingTargetEntity) {
        _uiState.value = _uiState.value.copy(
            selectedTargetForAction = target,
            isEditFormOpen = true,
        )
    }

    fun closeEditForm() {
        _uiState.value = _uiState.value.copy(isEditFormOpen = false, selectedTargetForAction = null)
    }

    fun openCancelForm(target: FundraisingTargetEntity) {
        _uiState.value = _uiState.value.copy(
            selectedTargetForAction = target,
            isCancelFormOpen = true,
        )
    }

    fun closeCancelForm() {
        _uiState.value = _uiState.value.copy(isCancelFormOpen = false, selectedTargetForAction = null)
    }

    fun selectTargetForDetail(target: FundraisingTargetEntity) {
        _uiState.value = _uiState.value.copy(selectedTargetForDetail = target)
    }

    fun closeDetail() {
        _uiState.value = _uiState.value.copy(selectedTargetForDetail = null)
    }

    // Business Logic Mutations
    fun createTarget(title: String, targetAmountCents: Long, durationDays: Int) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val target = FundraisingTargetEntity(
                id = "target_${UUID.randomUUID()}",
                title = title,
                targetAmountInCents = targetAmountCents,
                collectedAmountInCents = 0L,
                startDate = now,
                endDate = now + durationDays * 86400000L,
                status = "Aktif",
            )
            targetDao.insertTarget(target)
            _uiState.value = _uiState.value.copy(isTargetFormOpen = false)
        }
    }

    fun recordFund(
        targetId: String,
        isKasAllocation: Boolean,
        donorOrNote: String,
        amountCents: Long,
        accountId: String = "acc_bsi",
    ) {
        viewModelScope.launch {
            val target = _uiState.value.allTargets.find { it.id == targetId } ?: return@launch
            if (target.status == "Dibatalkan") return@launch

            val donorLabel = if (isKasAllocation) {
                if (donorOrNote.isBlank()) "Alokasi Kas Masjid" else "Alokasi Kas Masjid: $donorOrNote"
            } else {
                if (donorOrNote.isBlank()) "Donasi: Hamba Allah" else "Donasi: $donorOrNote"
            }

            val donation = DonationEntity(
                id = "don_${UUID.randomUUID()}",
                targetId = targetId,
                donorName = donorLabel,
                amountInCents = amountCents,
                timestamp = System.currentTimeMillis(),
                accountId = accountId,
            )
            donationDao.insertDonation(donation)

            val updatedCollected = target.collectedAmountInCents + amountCents
            val updatedStatus = if (updatedCollected >= target.targetAmountInCents && target.status == "Aktif") "Selesai" else target.status
            targetDao.updateTarget(target.copy(collectedAmountInCents = updatedCollected, status = updatedStatus))

            // Single Source of Truth: External donation creates Income in Arus Kas.
            // Internal Kas Allocation does NOT create new income (no double counting).
            if (!isKasAllocation) {
                try {
                    financialRepository.createIncome(
                        title = "Donasi Target: ${target.title}",
                        amount = Money.of(amountCents),
                        accountId = accountId,
                        fundId = "fund_const",
                        categoryId = "cat_donation",
                        note = "Penerimaan donasi untuk ${target.title} ($donorLabel)",
                        userId = "system",
                    )
                } catch (_: Exception) {}
            }

            _uiState.value = _uiState.value.copy(isFundFormOpen = false)
        }
    }

    fun transferFunds(
        fromTargetId: String,
        toTargetId: String,
        amountCents: Long,
        note: String,
    ) {
        viewModelScope.launch {
            if (fromTargetId == toTargetId || amountCents <= 0) return@launch
            val fromTarget = _uiState.value.allTargets.find { it.id == fromTargetId } ?: return@launch
            val toTarget = _uiState.value.allTargets.find { it.id == toTargetId } ?: return@launch
            if (fromTarget.status == "Dibatalkan" || toTarget.status == "Dibatalkan") return@launch

            val now = System.currentTimeMillis()

            // Outflow record on source
            val outflowDonation = DonationEntity(
                id = "don_${UUID.randomUUID()}",
                targetId = fromTargetId,
                donorName = "Transfer Keluar: Ke ${toTarget.title}${if (note.isNotBlank()) " ($note)" else ""}",
                amountInCents = amountCents,
                timestamp = now,
                accountId = "acc_internal",
            )
            donationDao.insertDonation(outflowDonation)

            // Inflow record on destination
            val inflowDonation = DonationEntity(
                id = "don_${UUID.randomUUID()}",
                targetId = toTargetId,
                donorName = "Transfer Masuk: Dari ${fromTarget.title}${if (note.isNotBlank()) " ($note)" else ""}",
                amountInCents = amountCents,
                timestamp = now + 1,
                accountId = "acc_internal",
            )
            donationDao.insertDonation(inflowDonation)

            // Update destination target collected amount
            val updatedToCollected = toTarget.collectedAmountInCents + amountCents
            val updatedToStatus = if (updatedToCollected >= toTarget.targetAmountInCents && toTarget.status == "Aktif") "Selesai" else toTarget.status
            targetDao.updateTarget(toTarget.copy(collectedAmountInCents = updatedToCollected, status = updatedToStatus))

            _uiState.value = _uiState.value.copy(isTransferFormOpen = false)
        }
    }

    fun salurkanDana(
        targetId: String,
        amountCents: Long,
        purpose: String,
        accountId: String = "acc_cash",
    ) {
        viewModelScope.launch {
            val target = _uiState.value.allTargets.find { it.id == targetId } ?: return@launch
            if (target.status == "Dibatalkan") return@launch

            val purposeText = purpose.ifBlank { "Penggunaan dana target ${target.title}" }

            val donation = DonationEntity(
                id = "don_${UUID.randomUUID()}",
                targetId = targetId,
                donorName = "Penyaluran: $purposeText",
                amountInCents = amountCents,
                timestamp = System.currentTimeMillis(),
                accountId = accountId,
            )
            donationDao.insertDonation(donation)

            // Single Source of Truth: Penyaluran Target creates Expense in Arus Kas & updates account balance
            try {
                financialRepository.createExpense(
                    title = "Penyaluran Target: ${target.title}",
                    amount = Money.of(amountCents),
                    accountId = accountId,
                    fundId = "fund_const",
                    categoryId = "cat_construction",
                    note = purposeText,
                    userId = "system",
                )
            } catch (_: Exception) {}

            _uiState.value = _uiState.value.copy(isSalurkanFormOpen = false, selectedTargetForAction = null)
        }
    }

    fun editTarget(
        targetId: String,
        newTitle: String,
        newTargetAmountCents: Long,
        durationDays: Int,
    ) {
        viewModelScope.launch {
            val target = _uiState.value.allTargets.find { it.id == targetId } ?: return@launch
            val now = System.currentTimeMillis()
            val updatedTarget = target.copy(
                title = newTitle,
                targetAmountInCents = newTargetAmountCents,
                endDate = if (durationDays > 0) now + durationDays * 86400000L else target.endDate,
            )
            targetDao.updateTarget(updatedTarget)
            _uiState.value = _uiState.value.copy(isEditFormOpen = false, selectedTargetForAction = null)
        }
    }

    fun cancelTarget(
        targetId: String,
        reason: String,
    ) {
        viewModelScope.launch {
            val target = _uiState.value.allTargets.find { it.id == targetId } ?: return@launch
            val metrics = _uiState.value.targetMetrics[targetId]

            // 1. Update status to Dibatalkan
            val updatedTarget = target.copy(status = "Dibatalkan")
            targetDao.updateTarget(updatedTarget)

            // 2. Record cancellation audit record
            val cancelDonation = DonationEntity(
                id = "don_${UUID.randomUUID()}",
                targetId = targetId,
                donorName = "Pembatalan Target: $reason",
                amountInCents = 0L,
                timestamp = System.currentTimeMillis(),
                accountId = "acc_system",
            )
            donationDao.insertDonation(cancelDonation)

            // 3. Return unspent donation funds to general cash (Arus Kas)
            if (metrics != null && metrics.donationAmountInCents > 0) {
                val unspentDonation = (metrics.donationAmountInCents - metrics.distributedAmountInCents).coerceAtLeast(0L)
                if (unspentDonation > 0) {
                    val returnTx = TransactionEntity(
                        id = "tx_return_${UUID.randomUUID()}",
                        title = "Pengembalian Dana Target / Pembatalan",
                        amountInCents = unspentDonation,
                        type = TransactionType.INCOME,
                        status = TransactionStatus.FINALIZED,
                        accountId = "acc_cash",
                        fundId = "fund_general",
                        categoryId = "cat_donation_return",
                        timestamp = System.currentTimeMillis(),
                        note = "Pengembalian dana donasi karena target ${target.title} dibatalkan. Alasan: $reason",
                    )
                    transactionDao.insertTransaction(returnTx)
                }
            }

            _uiState.value = _uiState.value.copy(
                isCancelFormOpen = false,
                selectedTargetForAction = null,
                selectedTargetForDetail = null,
            )
        }
    }
}
