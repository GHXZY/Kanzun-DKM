package com.kanzun.perbendaharaan.feature.settings.domain.repository

import android.content.Context
import com.kanzun.perbendaharaan.core.database.entity.BackupMetadataEntity
import com.kanzun.perbendaharaan.core.database.entity.MosqueEntity
import com.kanzun.perbendaharaan.core.database.entity.MosqueLetterheadEntity
import com.kanzun.perbendaharaan.core.database.entity.PermissionEntity
import com.kanzun.perbendaharaan.core.database.entity.RoleEntity
import com.kanzun.perbendaharaan.core.database.entity.SignatureEntity
import com.kanzun.perbendaharaan.core.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import java.io.File

interface SettingsRepository {
    fun getMosque(): Flow<MosqueEntity?>
    suspend fun updateMosque(mosque: MosqueEntity)
    fun getLetterhead(): Flow<MosqueLetterheadEntity?>
    suspend fun updateLetterhead(letterhead: MosqueLetterheadEntity)
    fun getSignatures(): Flow<List<SignatureEntity>>
    suspend fun addOrUpdateSignature(signature: SignatureEntity)
    fun getUsers(): Flow<List<UserEntity>>
    fun getRoles(): Flow<List<RoleEntity>>
    suspend fun hasPermission(roleId: String, permissionKey: String): Boolean
    suspend fun grantPermission(roleId: String, permissionKey: String)
    fun getBackupMetadataList(): Flow<List<BackupMetadataEntity>>
    suspend fun createBackup(context: Context): BackupMetadataEntity
    suspend fun restoreBackup(context: Context, backupFile: File): Boolean
    suspend fun resetData(context: Context)
    fun isPinEnabled(context: Context): Boolean
    fun getPinCode(context: Context): String
    suspend fun setPin(context: Context, enabled: Boolean, pinCode: String)
}
