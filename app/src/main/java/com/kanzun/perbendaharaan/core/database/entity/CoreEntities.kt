package com.kanzun.perbendaharaan.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kanzun.perbendaharaan.core.model.TransactionStatus
import com.kanzun.perbendaharaan.core.model.TransactionType

// 1. Mosque
@Entity(tableName = "mosques")
data class MosqueEntity(
    @PrimaryKey val id: String = "m_1",
    val name: String,
    val address: String,
    val city: String = "",
    val phone: String = "",
    val email: String = "",
    val logoPath: String = "",
    val treasurerName: String = "",
    val dkmChairmanName: String = "",
)

// 2. User
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val roleId: String,
    val isActive: Boolean = true,
)

// 3. Role
@Entity(tableName = "roles")
data class RoleEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
)

// 4. Permission
@Entity(tableName = "permissions")
data class PermissionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val roleId: String,
)

// 6. Fund
@Entity(tableName = "funds")
data class FundEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val isActive: Boolean = true,
)

// 7. Category
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: TransactionType, // INCOME or EXPENSE
    val isSystemDefault: Boolean = false,
)

// 8. Transaction
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val amountInCents: Long,
    val type: TransactionType,
    val status: TransactionStatus,
    val accountId: String,
    val fundId: String,
    val categoryId: String,
    val timestamp: Long,
    val note: String = "",
    val referenceNumber: String = "",
    val isReversed: Boolean = false,
    val reversalOfTransactionId: String? = null,
    val createdByUserId: String = "system",
)

// 9. TransactionAttachment
@Entity(tableName = "transaction_attachments")
data class TransactionAttachmentEntity(
    @PrimaryKey val id: String,
    val transactionId: String,
    val filePath: String,
    val fileType: String,
)

// 10. Transfer
@Entity(tableName = "transfers")
data class TransferEntity(
    @PrimaryKey val id: String,
    val fromAccountId: String,
    val toAccountId: String,
    val amountInCents: Long,
    val timestamp: Long,
    val note: String = "",
    val createdByUserId: String = "system",
)

// 11. Asset
@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey val id: String,
    val name: String,
    val categoryId: String,
    val acquisitionDate: Long,
    val acquisitionValueInCents: Long,
    val fundSourceId: String,
    val location: String,
    val conditionStatus: String, // e.g. "Baik", "Rusak", "Dalam Perbaikan"
    val serialNumber: String = "",
)

// 12. AssetCategory
@Entity(tableName = "asset_categories")
data class AssetCategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
)

// 13. ZakatTransaction
@Entity(tableName = "zakat_transactions")
data class ZakatTransactionEntity(
    @PrimaryKey val id: String,
    val zakatType: String, // e.g. "Zakat Fitrah", "Zakat Maal"
    val isDistribution: Boolean, // false = income, true = distribution
    val amountInCents: Long,
    val muzakiOrMustahikName: String,
    val timestamp: Long,
    val accountId: String,
    val note: String = "",
)

// 14. Mustahik
@Entity(tableName = "mustahiks")
data class MustahikEntity(
    @PrimaryKey val id: String,
    val name: String,
    val asnafCategory: String, // e.g. "Fakir", "Miskin", "Amil", "Muallaf"
    val address: String,
    val phone: String = "",
)

// 15. FundraisingTarget
@Entity(tableName = "fundraising_targets")
data class FundraisingTargetEntity(
    @PrimaryKey val id: String,
    val title: String,
    val targetAmountInCents: Long,
    val collectedAmountInCents: Long,
    val startDate: Long,
    val endDate: Long,
    val status: String, // e.g. "Aktif", "Tercapai", "Selesai"
)

// 16. Donation
@Entity(tableName = "donations")
data class DonationEntity(
    @PrimaryKey val id: String,
    val targetId: String,
    val donorName: String,
    val amountInCents: Long,
    val timestamp: Long,
    val accountId: String,
)

// 17. Budget
@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val id: String,
    val year: Int,
    val title: String,
    val totalIncomeBudgetInCents: Long,
    val totalExpenseBudgetInCents: Long,
    val isLocked: Boolean = false,
)

// 18. BudgetItem
@Entity(tableName = "budget_items")
data class BudgetItemEntity(
    @PrimaryKey val id: String,
    val budgetId: String,
    val categoryId: String,
    val plannedAmountInCents: Long,
    val isExpense: Boolean,
)

// 19. AuditLog
@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val whoUserId: String,
    val whenTimestamp: Long,
    val action: String, // e.g. "CREATE", "FINALIZE", "REVERSE"
    val entityName: String, // e.g. "Transaction"
    val entityId: String,
    val beforeStateJson: String? = null,
    val afterStateJson: String? = null,
)

// 20. Signature
@Entity(tableName = "signatures")
data class SignatureEntity(
    @PrimaryKey val id: String,
    val signerName: String,
    val titleOrRole: String, // e.g. "Bendahara", "Ketua DKM"
    val imagePath: String,
)

// 21. MosqueLetterhead
@Entity(tableName = "mosque_letterheads")
data class MosqueLetterheadEntity(
    @PrimaryKey val id: String,
    val line1: String,
    val line2: String,
    val line3: String,
    val logoPath: String = "",
)

// 22. BackupMetadata
@Entity(tableName = "backup_metadata")
data class BackupMetadataEntity(
    @PrimaryKey val id: String,
    val backupTimestamp: Long,
    val backupFilePath: String,
    val dbVersion: Int,
    val fileSizeInBytes: Long,
)
