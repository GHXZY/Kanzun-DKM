package com.kanzun.perbendaharaan.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kanzun.perbendaharaan.core.database.entity.AccountEntity
import com.kanzun.perbendaharaan.core.database.entity.AssetEntity
import com.kanzun.perbendaharaan.core.database.entity.AuditLogEntity
import com.kanzun.perbendaharaan.core.database.entity.BackupMetadataEntity
import com.kanzun.perbendaharaan.core.database.entity.BudgetEntity
import com.kanzun.perbendaharaan.core.database.entity.BudgetItemEntity
import com.kanzun.perbendaharaan.core.database.entity.CategoryEntity
import com.kanzun.perbendaharaan.core.database.entity.DonationEntity
import com.kanzun.perbendaharaan.core.database.entity.FundEntity
import com.kanzun.perbendaharaan.core.database.entity.FundraisingTargetEntity
import com.kanzun.perbendaharaan.core.database.entity.MosqueEntity
import com.kanzun.perbendaharaan.core.database.entity.MosqueLetterheadEntity
import com.kanzun.perbendaharaan.core.database.entity.MustahikEntity
import com.kanzun.perbendaharaan.core.database.entity.PermissionEntity
import com.kanzun.perbendaharaan.core.database.entity.RoleEntity
import com.kanzun.perbendaharaan.core.database.entity.SignatureEntity
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.database.entity.TransferEntity
import com.kanzun.perbendaharaan.core.database.entity.UserEntity
import com.kanzun.perbendaharaan.core.database.entity.ZakatTransactionEntity
import com.kanzun.perbendaharaan.core.model.TransactionType
import kotlinx.coroutines.flow.Flow



@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE accountId = :accountId AND isReversed = 0")
    suspend fun getActiveTransactionsForAccount(accountId: String): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE fundId = :fundId AND isReversed = 0")
    suspend fun getActiveTransactionsForFund(fundId: String): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)
}

@Dao
interface TransferDao {
    @Query("SELECT * FROM transfers ORDER BY timestamp DESC")
    fun getAllTransfers(): Flow<List<TransferEntity>>

    @Query("SELECT * FROM transfers WHERE fromAccountId = :accountId OR toAccountId = :accountId")
    suspend fun getTransfersForAccount(accountId: String): List<TransferEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: TransferEntity)
}

@Dao
interface FundDao {
    @Query("SELECT * FROM funds WHERE isActive = 1")
    fun getActiveFunds(): Flow<List<FundEntity>>

    @Query("SELECT * FROM funds WHERE id = :id")
    suspend fun getFundById(id: String): FundEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFund(fund: FundEntity)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE type = :type")
    fun getCategoriesByType(type: TransactionType): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY whenTimestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)
}

@Dao
interface FundraisingTargetDao {
    @Query("SELECT * FROM fundraising_targets WHERE status = 'Aktif' ORDER BY startDate DESC LIMIT 1")
    suspend fun getActiveTarget(): FundraisingTargetEntity?

    @Query("SELECT * FROM fundraising_targets WHERE id = :id")
    suspend fun getTargetById(id: String): FundraisingTargetEntity?

    @Query("SELECT * FROM fundraising_targets ORDER BY startDate DESC")
    fun getAllTargets(): Flow<List<FundraisingTargetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarget(target: FundraisingTargetEntity)

    @Update
    suspend fun updateTarget(target: FundraisingTargetEntity)
}

@Dao
interface AssetDao {
    @Query("SELECT * FROM assets ORDER BY acquisitionDate DESC")
    fun getAllAssets(): Flow<List<AssetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetEntity)

    @Update
    suspend fun updateAsset(asset: AssetEntity)

    @Delete
    suspend fun deleteAsset(asset: AssetEntity)
}

@Dao
interface ZakatDao {
    @Query("SELECT * FROM zakat_transactions ORDER BY timestamp DESC")
    fun getAllZakatTransactions(): Flow<List<ZakatTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZakatTransaction(tx: ZakatTransactionEntity)
}

@Dao
interface MustahikDao {
    @Query("SELECT * FROM mustahiks ORDER BY name ASC")
    fun getAllMustahiks(): Flow<List<MustahikEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMustahik(mustahik: MustahikEntity)

    @Update
    suspend fun updateMustahik(mustahik: MustahikEntity)

    @Delete
    suspend fun deleteMustahik(mustahik: MustahikEntity)
}

@Dao
interface DonationDao {
    @Query("SELECT * FROM donations ORDER BY timestamp DESC")
    fun getAllDonations(): Flow<List<DonationEntity>>

    @Query("SELECT * FROM donations WHERE targetId = :targetId ORDER BY timestamp DESC")
    fun getDonationsForTarget(targetId: String): Flow<List<DonationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: DonationEntity)
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets ORDER BY year DESC")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE year = :year LIMIT 1")
    suspend fun getBudgetByYear(year: Int): BudgetEntity?

    @Query("SELECT * FROM budget_items WHERE budgetId = :budgetId")
    fun getBudgetItemsForBudget(budgetId: String): Flow<List<BudgetItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgetItem(item: BudgetItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgetItems(items: List<BudgetItemEntity>)

    @Update
    suspend fun updateBudgetItem(item: BudgetItemEntity)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteBudgetById(id: String)

    @Query("DELETE FROM budget_items WHERE budgetId = :budgetId")
    suspend fun deleteBudgetItemsForBudget(budgetId: String)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM mosques LIMIT 1")
    fun getMosque(): Flow<MosqueEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMosque(mosque: MosqueEntity)

    @Query("SELECT * FROM mosque_letterheads LIMIT 1")
    fun getLetterhead(): Flow<MosqueLetterheadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLetterhead(letterhead: MosqueLetterheadEntity)

    @Query("SELECT * FROM signatures")
    fun getSignatures(): Flow<List<SignatureEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignature(signature: SignatureEntity)

    @Query("SELECT * FROM users")
    fun getUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM roles")
    fun getRoles(): Flow<List<RoleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRole(role: RoleEntity)

    @Query("SELECT * FROM permissions WHERE roleId = :roleId")
    suspend fun getPermissionsForRole(roleId: String): List<PermissionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermission(permission: PermissionEntity)

    @Query("SELECT * FROM backup_metadata ORDER BY backupTimestamp DESC")
    fun getBackupMetadataList(): Flow<List<BackupMetadataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackupMetadata(metadata: BackupMetadataEntity)
}

