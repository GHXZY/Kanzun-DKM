package com.kanzun.perbendaharaan.feature.settings.data.repository

import android.content.Context
import com.kanzun.perbendaharaan.core.database.KanzunDatabase
import com.kanzun.perbendaharaan.core.database.dao.AuditLogDao
import com.kanzun.perbendaharaan.core.database.dao.SettingsDao
import com.kanzun.perbendaharaan.core.database.entity.AuditLogEntity
import com.kanzun.perbendaharaan.core.database.entity.BackupMetadataEntity
import com.kanzun.perbendaharaan.core.database.entity.MosqueEntity
import com.kanzun.perbendaharaan.core.database.entity.MosqueLetterheadEntity
import com.kanzun.perbendaharaan.core.database.entity.PermissionEntity
import com.kanzun.perbendaharaan.core.database.entity.RoleEntity
import com.kanzun.perbendaharaan.core.database.entity.SignatureEntity
import com.kanzun.perbendaharaan.core.database.entity.UserEntity
import com.kanzun.perbendaharaan.core.util.DevDataSeeder
import com.kanzun.perbendaharaan.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao,
    private val auditLogDao: AuditLogDao,
    private val database: KanzunDatabase,
    private val devDataSeeder: DevDataSeeder,
) : SettingsRepository {

    override fun getMosque(): Flow<MosqueEntity?> = settingsDao.getMosque()

    override suspend fun updateMosque(mosque: MosqueEntity) {
        settingsDao.insertOrUpdateMosque(mosque)
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                whoUserId = "system",
                whenTimestamp = System.currentTimeMillis(),
                action = "UPDATE_MOSQUE_PROFILE",
                entityName = "Mosque",
                entityId = mosque.id,
                afterStateJson = "${mosque.name} - ${mosque.address}",
            )
        )
    }

    override fun getLetterhead(): Flow<MosqueLetterheadEntity?> = settingsDao.getLetterhead()

    override suspend fun updateLetterhead(letterhead: MosqueLetterheadEntity) {
        settingsDao.insertOrUpdateLetterhead(letterhead)
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                whoUserId = "system",
                whenTimestamp = System.currentTimeMillis(),
                action = "UPDATE_LETTERHEAD",
                entityName = "MosqueLetterhead",
                entityId = letterhead.id,
            )
        )
    }

    override fun getSignatures(): Flow<List<SignatureEntity>> = settingsDao.getSignatures()

    override suspend fun addOrUpdateSignature(signature: SignatureEntity) {
        settingsDao.insertSignature(signature)
    }

    override fun getUsers(): Flow<List<UserEntity>> = settingsDao.getUsers()

    override fun getRoles(): Flow<List<RoleEntity>> = settingsDao.getRoles()

    override suspend fun hasPermission(roleId: String, permissionKey: String): Boolean {
        val permissions = settingsDao.getPermissionsForRole(roleId)
        return permissions.any { it.name == permissionKey }
    }

    override suspend fun grantPermission(roleId: String, permissionKey: String) {
        settingsDao.insertPermission(
            PermissionEntity(
                id = UUID.randomUUID().toString(),
                name = permissionKey,
                roleId = roleId,
            )
        )
    }

    override fun getBackupMetadataList(): Flow<List<BackupMetadataEntity>> = settingsDao.getBackupMetadataList()

    override suspend fun createBackup(context: Context): BackupMetadataEntity {
        val backupDir = File(context.filesDir, "backups")
        if (!backupDir.exists()) backupDir.mkdirs()

        val timestamp = System.currentTimeMillis()
        val backupFile = File(backupDir, "kanzun_backup_$timestamp.db")

        // Copy current Room DB file
        val currentDbFile = context.getDatabasePath("kanzun_perbendaharaan.db")
        if (currentDbFile.exists()) {
            currentDbFile.copyTo(backupFile, overwrite = true)
        } else {
            backupFile.writeText("MOCK_BACKUP_CONTENT")
        }

        val metadata = BackupMetadataEntity(
            id = UUID.randomUUID().toString(),
            backupTimestamp = timestamp,
            backupFilePath = backupFile.absolutePath,
            dbVersion = 4,
            fileSizeInBytes = if (backupFile.exists()) backupFile.length() else 0L,
        )

        settingsDao.insertBackupMetadata(metadata)

        auditLogDao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                whoUserId = "system",
                whenTimestamp = timestamp,
                action = "CREATE_BACKUP",
                entityName = "BackupMetadata",
                entityId = metadata.id,
                afterStateJson = "File: ${backupFile.name}",
            )
        )

        return metadata
    }

    override suspend fun restoreBackup(context: Context, backupFile: File): Boolean {
        if (!backupFile.exists()) return false

        // MANDATORY: Create pre-restore safety backup first
        createBackup(context)

        // Perform restore copy
        val currentDbFile = context.getDatabasePath("kanzun_perbendaharaan.db")
        backupFile.copyTo(currentDbFile, overwrite = true)

        auditLogDao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                whoUserId = "system",
                whenTimestamp = System.currentTimeMillis(),
                action = "RESTORE_BACKUP",
                entityName = "BackupMetadata",
                entityId = backupFile.name,
                afterStateJson = "Restored from: ${backupFile.name}",
            )
        )

        return true
    }

    override suspend fun resetData(context: Context) {
        database.clearAllTables()
        devDataSeeder.initCleanMasterData()
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                whoUserId = "system",
                whenTimestamp = System.currentTimeMillis(),
                action = "RESET_ALL_DATA",
                entityName = "Database",
                entityId = "kanzun_perbendaharaan.db",
                afterStateJson = "Database reset to clean state (all dummy data removed)",
            )
        )
    }

    private fun getSecurityPrefs(context: Context) =
        context.getSharedPreferences("kanzun_security_prefs", Context.MODE_PRIVATE)

    override fun isPinEnabled(context: Context): Boolean {
        return getSecurityPrefs(context).getBoolean("security_pin_enabled", false)
    }

    override fun getPinCode(context: Context): String {
        return getSecurityPrefs(context).getString("security_pin_code", "") ?: ""
    }

    override suspend fun setPin(context: Context, enabled: Boolean, pinCode: String) {
        getSecurityPrefs(context).edit()
            .putBoolean("security_pin_enabled", enabled)
            .putString("security_pin_code", pinCode)
            .apply()
    }
}
