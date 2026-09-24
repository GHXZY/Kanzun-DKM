package com.kanzun.perbendaharaan.feature.assets.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanzun.perbendaharaan.core.database.dao.AssetDao
import com.kanzun.perbendaharaan.core.database.entity.AssetEntity
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

data class AssetsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val totalAssetValue: Money = Money.ZERO,
    val allAssets: List<AssetEntity> = emptyList(),
    val filteredAssets: List<AssetEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedConditionFilter: String? = null,
    val selectedCategoryFilter: String? = null,
    val selectedAsset: AssetEntity? = null,
    val isFormOpen: Boolean = false,
    val isEditMode: Boolean = false,
)

@HiltViewModel
class AssetsViewModel @Inject constructor(
    private val assetDao: AssetDao,
    private val financialRepository: FinancialRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssetsUiState())
    val uiState: StateFlow<AssetsUiState> = _uiState.asStateFlow()

    init {
        loadAssets()
    }

    fun loadAssets() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            assetDao.getAllAssets()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Gagal memuat aset",
                    )
                }
                .collect { assets ->
                    val totalCents = assets
                        .filter { it.conditionStatus != "Diarsipkan" && it.conditionStatus != "Disposed" }
                        .sumOf { it.acquisitionValueInCents }
                    val currentState = _uiState.value
                    val filtered = applyFilters(
                        assets = assets,
                        query = currentState.searchQuery,
                        condition = currentState.selectedConditionFilter,
                        category = currentState.selectedCategoryFilter,
                    )
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        totalAssetValue = Money.of(totalCents),
                        allAssets = assets,
                        filteredAssets = filtered,
                    )
                }
        }
    }

    fun setSearchQuery(query: String) {
        val currentState = _uiState.value
        val filtered = applyFilters(
            assets = currentState.allAssets,
            query = query,
            condition = currentState.selectedConditionFilter,
            category = currentState.selectedCategoryFilter,
        )
        _uiState.value = currentState.copy(searchQuery = query, filteredAssets = filtered)
    }

    fun setConditionFilter(condition: String?) {
        val currentState = _uiState.value
        val filtered = applyFilters(
            assets = currentState.allAssets,
            query = currentState.searchQuery,
            condition = condition,
            category = currentState.selectedCategoryFilter,
        )
        _uiState.value = currentState.copy(selectedConditionFilter = condition, filteredAssets = filtered)
    }

    fun setCategoryFilter(category: String?) {
        val currentState = _uiState.value
        val filtered = applyFilters(
            assets = currentState.allAssets,
            query = currentState.searchQuery,
            condition = currentState.selectedConditionFilter,
            category = category,
        )
        _uiState.value = currentState.copy(selectedCategoryFilter = category, filteredAssets = filtered)
    }

    fun openAddForm() {
        _uiState.value = _uiState.value.copy(isFormOpen = true, isEditMode = false, selectedAsset = null)
    }

    fun openEditForm(asset: AssetEntity) {
        _uiState.value = _uiState.value.copy(isFormOpen = true, isEditMode = true, selectedAsset = asset)
    }

    fun closeForm() {
        _uiState.value = _uiState.value.copy(isFormOpen = false)
    }

    fun openDetail(asset: AssetEntity) {
        _uiState.value = _uiState.value.copy(selectedAsset = asset)
    }

    fun closeDetail() {
        _uiState.value = _uiState.value.copy(selectedAsset = null)
    }

    fun saveAsset(
        name: String,
        category: String,
        valueCents: Long,
        fundSource: String,
        location: String,
        condition: String,
        inventoryNum: String,
    ) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val assetToSave = if (currentState.isEditMode && currentState.selectedAsset != null) {
                currentState.selectedAsset.copy(
                    name = name,
                    categoryId = category,
                    acquisitionValueInCents = valueCents,
                    fundSourceId = fundSource,
                    location = location,
                    conditionStatus = condition,
                    serialNumber = inventoryNum,
                )
            } else {
                AssetEntity(
                    id = "asset_${UUID.randomUUID()}",
                    name = name,
                    categoryId = category,
                    acquisitionDate = System.currentTimeMillis(),
                    acquisitionValueInCents = valueCents,
                    fundSourceId = fundSource,
                    location = location,
                    conditionStatus = condition,
                    serialNumber = inventoryNum,
                )
            }

            if (currentState.isEditMode) {
                assetDao.updateAsset(assetToSave)
            } else {
                assetDao.insertAsset(assetToSave)
                if (valueCents > 0) {
                    try {
                        financialRepository.createExpense(
                            title = "Pembelian Aset: $name",
                            amount = Money.of(valueCents),
                            accountId = "acc_cash",
                            fundId = "fund_ops",
                            categoryId = "cat_maintenance",
                            note = "Pengadaan aset inventaris $name (No. $inventoryNum)",
                            userId = "system",
                        )
                    } catch (_: Exception) {}
                }
            }
            _uiState.value = _uiState.value.copy(isFormOpen = false, selectedAsset = null)
        }
    }

    fun archiveOrDisposeAsset(asset: AssetEntity) {
        viewModelScope.launch {
            val updated = asset.copy(conditionStatus = "Diarsipkan")
            assetDao.updateAsset(updated)
            _uiState.value = _uiState.value.copy(selectedAsset = null)
        }
    }

    private fun applyFilters(
        assets: List<AssetEntity>,
        query: String,
        condition: String?,
        category: String?,
    ): List<AssetEntity> {
        return assets.filter { asset ->
            val matchesQuery = query.isBlank() ||
                asset.name.contains(query, ignoreCase = true) ||
                asset.location.contains(query, ignoreCase = true) ||
                asset.serialNumber.contains(query, ignoreCase = true)

            val matchesCondition = condition == null || asset.conditionStatus == condition
            val matchesCategory = category == null || asset.categoryId == category

            matchesQuery && matchesCondition && matchesCategory
        }
    }
}
