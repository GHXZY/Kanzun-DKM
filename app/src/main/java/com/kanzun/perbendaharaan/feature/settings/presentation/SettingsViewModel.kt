package com.kanzun.perbendaharaan.feature.settings.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanzun.perbendaharaan.core.database.entity.AccountEntity
import com.kanzun.perbendaharaan.core.database.entity.BackupMetadataEntity
import com.kanzun.perbendaharaan.core.database.entity.MosqueEntity
import com.kanzun.perbendaharaan.core.database.entity.MosqueLetterheadEntity
import com.kanzun.perbendaharaan.core.database.entity.RoleEntity
import com.kanzun.perbendaharaan.core.database.entity.SignatureEntity
import com.kanzun.perbendaharaan.core.database.entity.UserEntity
import com.kanzun.perbendaharaan.feature.financial.domain.repository.FinancialRepository
import com.kanzun.perbendaharaan.feature.settings.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

enum class SettingsSection(val title: String) {
    PROFILE("Profil Masjid"),
    USERS_PERMISSIONS("Pengguna & Hak Akses"),
    CATEGORIES_FUNDS("Kategori & Dana"),
    BACKUP_RESTORE("Backup & Restore"),
    SECURITY("Security"),
    PREFERENCES("Preferensi"),
    SYSTEM("Sistem"),
    ABOUT("Tentang Aplikasi"),
}

data class SettingsUiState(
    val activeSection: SettingsSection = SettingsSection.PROFILE,
    val mosqueProfile: MosqueEntity? = null,
    val isIdentityCustomized: Boolean = false,
    val letterhead: MosqueLetterheadEntity? = null,
    val signatures: List<SignatureEntity> = emptyList(),
    val users: List<UserEntity> = emptyList(),
    val roles: List<RoleEntity> = emptyList(),
    val backups: List<BackupMetadataEntity> = emptyList(),
    val accounts: List<AccountEntity> = emptyList(),
    val cashOpeningBalance: Long = 0L,
    val bankOpeningBalance: Long = 0L,
    val securityPinEnabled: Boolean = false,
    val biometricEnabled: Boolean = true,
    val autoLockDurationMinutes: Int = 5,
    val themePreference: String = "SYSTEM", // LIGHT, DARK, SYSTEM
    val currencyFormat: String = "IDR",
    val dateFormat: String = "dd/MM/yyyy",
    val notificationsEnabled: Boolean = true,
    val showSetPinDialog: Boolean = false,
    val showResetConfirmDialog: Boolean = false,
    val showBackupConfirmDialog: Boolean = false,
    val showRestoreConfirmDialog: Boolean = false,
    val pinCode: String = "",
    val selectedBackupFileForRestore: File? = null,
    val isLoading: Boolean = false,
    val message: String? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val financialRepository: FinancialRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _activeSection = MutableStateFlow(SettingsSection.PROFILE)
    private val _themePref = MutableStateFlow("SYSTEM")
    private val _securityPinEnabled = MutableStateFlow(false)
    private val _pinCode = MutableStateFlow("")
    private val _showSetPinDialog = MutableStateFlow(false)
    private val _showResetConfirmDialog = MutableStateFlow(false)
    private val _biometricEnabled = MutableStateFlow(true)
    private val _showBackupConfirm = MutableStateFlow(false)
    private val _showRestoreConfirm = MutableStateFlow(false)
    private val _message = MutableStateFlow<String?>(null)
    private val _isIdentityCustomized = MutableStateFlow(
        context.getSharedPreferences("kanzun_app_prefs", Context.MODE_PRIVATE)
            .getBoolean("is_mosque_identity_customized", false)
    )

    val uiState: StateFlow<SettingsUiState> = combine(
        _activeSection,
        settingsRepository.getMosque(),
        settingsRepository.getLetterhead(),
        settingsRepository.getSignatures(),
        settingsRepository.getUsers(),
        settingsRepository.getRoles(),
        settingsRepository.getBackupMetadataList(),
        _themePref,
        _securityPinEnabled,
        _showSetPinDialog,
        _showResetConfirmDialog,
        _pinCode,
        financialRepository.getActiveAccounts(),
        _isIdentityCustomized,
    ) { flows ->
        val section = flows[0] as SettingsSection
        val mosque = flows[1] as MosqueEntity?
        val letterhead = flows[2] as MosqueLetterheadEntity?
        @Suppress("UNCHECKED_CAST")
        val signatures = flows[3] as List<SignatureEntity>
        @Suppress("UNCHECKED_CAST")
        val users = flows[4] as List<UserEntity>
        @Suppress("UNCHECKED_CAST")
        val roles = flows[5] as List<RoleEntity>
        @Suppress("UNCHECKED_CAST")
        val backups = flows[6] as List<BackupMetadataEntity>
        val theme = flows[7] as String
        val pinEnabled = flows[8] as Boolean
        val showSetPin = flows[9] as Boolean
        val showResetConfirm = flows[10] as Boolean
        val pinVal = flows[11] as String
        @Suppress("UNCHECKED_CAST")
        val accounts = flows[12] as List<AccountEntity>
        val isCustomized = flows[13] as Boolean

        val cashOpening = accounts.find { it.id == "acc_cash" }?.openingBalanceInCents ?: 0L
        val bankOpening = accounts.find { it.id == "acc_bsi" }?.openingBalanceInCents ?: 0L

        SettingsUiState(
            activeSection = section,
            isIdentityCustomized = isCustomized,
            mosqueProfile = mosque ?: MosqueEntity(
                id = "m_1",
                name = "Masjid Agung Al-Mubarak",
                address = "Jl. Ahmad Yani No. 45, Jakarta",
                city = "Jakarta",
                phone = "081234567890",
                email = "info@masjid-almubarak.org",
                treasurerName = "H. Muhammad Hatta",
                dkmChairmanName = "H. Ahmad Dahlan",
                logoPath = "",
            ),
            letterhead = letterhead ?: MosqueLetterheadEntity("lh_1", "MASJID AGUNG AL-MUBARAK", "Jl. Ahmad Yani No. 45, Jakarta", "Telp: 081234567890"),
            signatures = signatures.ifEmpty {
                listOf(
                    SignatureEntity("sig_1", "H. Ahmad Dahlan", "Ketua DKM", ""),
                    SignatureEntity("sig_2", "H. Muhammad Hatta", "Bendahara", ""),
                )
            },
            users = users.ifEmpty {
                listOf(
                    UserEntity("u_1", "bendahara", "H. Muhammad Hatta", "role_bendahara"),
                )
            },
            roles = roles.ifEmpty {
                listOf(
                    RoleEntity("role_bendahara", "Bendahara", "Pengelola Aplikasi Perbendaharaan Masjid"),
                )
            },
            backups = backups,
            accounts = accounts,
            cashOpeningBalance = cashOpening,
            bankOpeningBalance = bankOpening,
            themePreference = theme,
            securityPinEnabled = pinEnabled,
            showSetPinDialog = showSetPin,
            showResetConfirmDialog = showResetConfirm,
            pinCode = pinVal,
            biometricEnabled = _biometricEnabled.value,
            showBackupConfirmDialog = _showBackupConfirm.value,
            showRestoreConfirmDialog = _showRestoreConfirm.value,
            message = _message.value,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState(),
    )

    fun saveOpeningBalances(cashBalanceCents: Long, bankBalanceCents: Long) {
        viewModelScope.launch {
            financialRepository.updateOpeningBalance("acc_cash", cashBalanceCents)
            financialRepository.updateOpeningBalance("acc_bsi", bankBalanceCents)
            _message.value = "Saldo awal berhasil disimpan"
        }
    }

    fun selectSection(section: SettingsSection) {
        _activeSection.value = section
    }

    fun saveMosqueIdentity(
        name: String,
        address: String,
        treasurerName: String,
        dkmChairmanName: String,
        logoPath: String,
    ) {
        viewModelScope.launch {
            val current = uiState.value.mosqueProfile
            val updated = current?.copy(
                name = name,
                address = address,
                treasurerName = treasurerName,
                dkmChairmanName = dkmChairmanName,
                logoPath = logoPath,
            ) ?: MosqueEntity(
                id = "m_1",
                name = name,
                address = address,
                treasurerName = treasurerName,
                dkmChairmanName = dkmChairmanName,
                logoPath = logoPath,
            )

            settingsRepository.updateMosque(updated)
            context.getSharedPreferences("kanzun_app_prefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("is_mosque_identity_customized", true)
                .apply()
            _isIdentityCustomized.value = true
            _message.value = "Perubahan disimpan"
        }
    }

    fun saveLogoFromUri(context: Context, uri: android.net.Uri) {
        viewModelScope.launch {
            try {
                val logoFile = File(context.filesDir, "mosque_logo.png")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    logoFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                val current = uiState.value.mosqueProfile
                val updated = current?.copy(logoPath = logoFile.absolutePath)
                    ?: MosqueEntity(id = "m_1", name = "Masjid Agung Al-Mubarak", address = "Jl. Ahmad Yani No. 45", logoPath = logoFile.absolutePath)

                settingsRepository.updateMosque(updated)
                context.getSharedPreferences("kanzun_app_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("is_mosque_identity_customized", true)
                    .apply()
                _isIdentityCustomized.value = true
                _message.value = "Logo berhasil diperbarui"
            } catch (e: Exception) {
                _message.value = "Gagal menyimpan logo: ${e.localizedMessage}"
            }
        }
    }


    fun removeLogo() {
        viewModelScope.launch {
            val current = uiState.value.mosqueProfile
            if (current != null) {
                val updated = current.copy(logoPath = "")
                settingsRepository.updateMosque(updated)
                _message.value = "Logo dihapus"
            }
        }
    }

    fun createManualBackup(context: Context) {
        viewModelScope.launch {
            val backup = settingsRepository.createBackup(context)
            _message.value = "Backup Berhasil Dibuat (${backup.fileSizeInBytes / 1024} KB)"
        }
    }

    fun restoreBackupFile(context: Context, backupFile: File) {
        viewModelScope.launch {
            val success = settingsRepository.restoreBackup(context, backupFile)
            if (success) {
                _message.value = "Restore Berhasil (Pre-restore safety backup dibuat otomatis)"
            } else {
                _message.value = "Gagal Melakukan Restore File"
            }
        }
    }

    fun loadPinState(context: Context) {
        _securityPinEnabled.value = settingsRepository.isPinEnabled(context)
        _pinCode.value = settingsRepository.getPinCode(context)
    }

    fun openSetPinDialog() {
        _showSetPinDialog.value = true
    }

    fun closeSetPinDialog() {
        _showSetPinDialog.value = false
    }

    fun savePin(context: Context, enabled: Boolean, pinCode: String) {
        viewModelScope.launch {
            settingsRepository.setPin(context, enabled, pinCode)
            _securityPinEnabled.value = enabled
            _pinCode.value = pinCode
            _showSetPinDialog.value = false
            _message.value = if (enabled) "PIN Keamanan Berhasil Diaktifkan" else "PIN Keamanan Dinonaktifkan"
        }
    }

    fun openResetConfirmDialog() {
        _showResetConfirmDialog.value = true
    }

    fun closeResetConfirmDialog() {
        _showResetConfirmDialog.value = false
    }

    fun resetAllData(context: Context) {
        viewModelScope.launch {
            try {
                _showResetConfirmDialog.value = false
                settingsRepository.resetData(context)
                context.getSharedPreferences("kanzun_app_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("is_mosque_identity_customized", false)
                    .putBoolean("has_completed_initial_setup", false)
                    .apply()
                _isIdentityCustomized.value = false
                _message.value = "Data Aplikasi Berhasil Direset ke Kondisi Awal"
            } catch (e: Exception) {
                _message.value = "Gagal Mereset Data: ${e.localizedMessage}"
            }
        }
    }

    fun saveInitialOpeningBalances(cashBalanceCents: Long, bankBalanceCents: Long) {
        viewModelScope.launch {
            financialRepository.updateOpeningBalance("acc_cash", cashBalanceCents)
            financialRepository.updateOpeningBalance("acc_bsi", bankBalanceCents)
            context.getSharedPreferences("kanzun_app_prefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("has_completed_initial_setup", true)
                .apply()
        }
    }

    fun completeInitialSetup() {
        context.getSharedPreferences("kanzun_app_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("has_completed_initial_setup", true)
            .apply()
    }


    fun toggleSecurityPin(context: Context, enabled: Boolean) {
        if (enabled) {
            _showSetPinDialog.value = true
        } else {
            savePin(context, enabled = false, pinCode = "")
        }
    }

    fun toggleBiometric(enabled: Boolean) {
        _biometricEnabled.value = enabled
    }

    fun setThemePreference(theme: String) {
        _themePref.value = theme
    }

    fun clearMessage() {
        _message.value = null
    }
}
