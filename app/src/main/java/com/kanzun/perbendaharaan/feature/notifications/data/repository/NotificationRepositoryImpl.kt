package com.kanzun.perbendaharaan.feature.notifications.data.repository

import com.kanzun.perbendaharaan.core.database.dao.NotificationDao
import com.kanzun.perbendaharaan.core.database.entity.NotificationEntity
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationCategory
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationFilter
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationItem
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationPriority
import com.kanzun.perbendaharaan.feature.notifications.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao,
) : NotificationRepository {

    override fun getNotifications(
        filter: NotificationFilter,
        userRoleId: String?,
    ): Flow<List<NotificationItem>> {
        return notificationDao.getAllNotifications().map { entities ->
            entities.map { it.toDomain() }
                .filter { item ->
                    // Role permission filter
                    val roleMatches = item.forRoleId == null || userRoleId == null || item.forRoleId == userRoleId
                    // Category & Read filter
                    val filterMatches = when (filter) {
                        NotificationFilter.ALL -> true
                        NotificationFilter.UNREAD -> !item.isRead
                        NotificationFilter.TRANSACTION -> item.category == NotificationCategory.TRANSAKSI
                        NotificationFilter.TARGET -> item.category == NotificationCategory.TARGET_DANA
                        NotificationFilter.RAPBM -> item.category == NotificationCategory.RAPBM
                        NotificationFilter.AUDIT -> item.category == NotificationCategory.AUDIT
                        NotificationFilter.BACKUP -> item.category == NotificationCategory.BACKUP
                        NotificationFilter.SECURITY -> item.category == NotificationCategory.SECURITY
                        NotificationFilter.SYSTEM -> item.category == NotificationCategory.SYSTEM
                    }
                    roleMatches && filterMatches
                }
        }
    }

    override fun getUnreadCount(): Flow<Int> = notificationDao.getUnreadCount()

    override suspend fun markAsRead(id: String) {
        notificationDao.markAsRead(id)
    }

    override suspend fun markAllAsRead() {
        notificationDao.markAllAsRead()
    }

    override suspend fun dismissNotification(id: String) {
        notificationDao.dismissNotification(id)
    }

    override suspend fun notifyTransactionRecorded(
        transactionId: String,
        title: String,
        message: String,
    ) {
        val entity = NotificationEntity(
            id = "notif_trx_created_$transactionId",
            title = title,
            message = message,
            category = NotificationCategory.TRANSAKSI.name,
            priority = NotificationPriority.INFO.name,
            timestamp = System.currentTimeMillis(),
            targetEntityType = "transaction",
            targetEntityId = transactionId,
            forRoleId = null, // Bendahara & Pengurus
        )
        notificationDao.insertNotification(entity)
    }

    override suspend fun notifyTransactionFinalized(
        transactionId: String,
        title: String,
        message: String,
    ) {
        val entity = NotificationEntity(
            id = "notif_trx_finalized_$transactionId",
            title = title,
            message = message,
            category = NotificationCategory.TRANSAKSI.name,
            priority = NotificationPriority.INFO.name,
            timestamp = System.currentTimeMillis(),
            targetEntityType = "transaction",
            targetEntityId = transactionId,
        )
        notificationDao.insertNotification(entity)
    }

    override suspend fun notifyTransactionReversed(
        transactionId: String,
        title: String,
        message: String,
    ) {
        val entity = NotificationEntity(
            id = "notif_trx_reversed_$transactionId",
            title = title,
            message = message,
            category = NotificationCategory.TRANSAKSI.name,
            priority = NotificationPriority.WARNING.name,
            timestamp = System.currentTimeMillis(),
            targetEntityType = "transaction",
            targetEntityId = transactionId,
        )
        notificationDao.insertNotification(entity)
    }

    override suspend fun notifyTargetMilestone(
        targetId: String,
        targetTitle: String,
        milestonePercentage: Int,
        currentCents: Long,
        targetCents: Long,
    ) {
        val currentFormatted = formatAmount(currentCents)
        val targetFormatted = formatAmount(targetCents)
        val title = "Target $targetTitle mencapai $milestonePercentage%"
        val message = "$currentFormatted dari target $targetFormatted."

        val priority = if (milestonePercentage >= 100) NotificationPriority.IMPORTANT else NotificationPriority.INFO

        val entity = NotificationEntity(
            id = "notif_target_${targetId}_m${milestonePercentage}",
            title = title,
            message = message,
            category = NotificationCategory.TARGET_DANA.name,
            priority = priority.name,
            timestamp = System.currentTimeMillis(),
            targetEntityType = "target",
            targetEntityId = targetId,
        )
        notificationDao.insertNotification(entity)
    }

    override suspend fun notifyRapbmWarning(
        budgetId: String,
        categoryTitle: String,
        actualCents: Long,
        budgetCents: Long,
        percentage: Int,
    ) {
        val actualFormatted = formatAmount(actualCents)
        val budgetFormatted = formatAmount(budgetCents)

        val (title, priority, message) = if (percentage >= 100) {
            val diffFormatted = formatAmount(actualCents - budgetCents)
            Triple(
                "Realisasi melebihi RAPBM",
                NotificationPriority.IMPORTANT,
                "Pengeluaran $categoryTitle: $actualFormatted. Anggaran: $budgetFormatted. Selisih: $diffFormatted",
            )
        } else {
            Triple(
                "Anggaran $categoryTitle mendekati batas RAPBM",
                NotificationPriority.WARNING,
                "Realisasi: $actualFormatted dari anggaran $budgetFormatted. Realisasi sudah mencapai $percentage%.",
            )
        }

        val entity = NotificationEntity(
            id = "notif_rapbm_${budgetId}_${categoryTitle.hashCode()}_p$percentage",
            title = title,
            message = message,
            category = NotificationCategory.RAPBM.name,
            priority = priority.name,
            timestamp = System.currentTimeMillis(),
            targetEntityType = "budget",
            targetEntityId = budgetId,
        )
        notificationDao.insertNotification(entity)
    }

    override suspend fun notifyAuditLogged(
        auditLogId: String,
        action: String,
        entityName: String,
        entityId: String,
        actorName: String,
    ) {
        val entity = NotificationEntity(
            id = "notif_audit_$auditLogId",
            title = "$entityName $action",
            message = "$entityName #$entityId telah di-$action oleh $actorName.",
            category = NotificationCategory.AUDIT.name,
            priority = NotificationPriority.INFO.name,
            timestamp = System.currentTimeMillis(),
            targetEntityType = "audit",
            targetEntityId = auditLogId,
            isDismissible = false, // Audit log notifications are retained as audit records!
        )
        notificationDao.insertNotification(entity)
    }

    override suspend fun notifyBackupStatus(
        backupId: String,
        isSuccess: Boolean,
        message: String,
        timestamp: Long,
    ) {
        val title = if (isSuccess) "Backup berhasil" else "Backup gagal"
        val priority = if (isSuccess) NotificationPriority.INFO else NotificationPriority.IMPORTANT

        val entity = NotificationEntity(
            id = "notif_backup_$backupId",
            title = title,
            message = message,
            category = NotificationCategory.BACKUP.name,
            priority = priority.name,
            timestamp = timestamp,
            targetEntityType = "backup",
            targetEntityId = backupId,
        )
        notificationDao.insertNotification(entity)
    }

    override suspend fun notifySecurityEvent(
        eventId: String,
        title: String,
        message: String,
    ) {
        val entity = NotificationEntity(
            id = "notif_sec_$eventId",
            title = title,
            message = message,
            category = NotificationCategory.SECURITY.name,
            priority = NotificationPriority.WARNING.name,
            timestamp = System.currentTimeMillis(),
            targetEntityType = "security",
            targetEntityId = eventId,
            isDismissible = false,
        )
        notificationDao.insertNotification(entity)
    }

    override suspend fun notifySystemEvent(
        eventId: String,
        title: String,
        message: String,
    ) {
        val entity = NotificationEntity(
            id = "notif_sys_$eventId",
            title = title,
            message = message,
            category = NotificationCategory.SYSTEM.name,
            priority = NotificationPriority.INFO.name,
            timestamp = System.currentTimeMillis(),
            targetEntityType = "system",
            targetEntityId = eventId,
        )
        notificationDao.insertNotification(entity)
    }

    private fun NotificationEntity.toDomain(): NotificationItem {
        return NotificationItem(
            id = id,
            title = title,
            message = message,
            category = try { NotificationCategory.valueOf(category) } catch (e: Exception) { NotificationCategory.SYSTEM },
            priority = try { NotificationPriority.valueOf(priority) } catch (e: Exception) { NotificationPriority.INFO },
            timestamp = timestamp,
            isRead = isRead,
            targetEntityType = targetEntityType,
            targetEntityId = targetEntityId,
            forRoleId = forRoleId,
            isDismissible = isDismissible,
        )
    }

    private fun formatAmount(cents: Long): String {
        val rupiah = cents / 100
        return String.format(java.util.Locale("id", "ID"), "Rp %,d", rupiah).replace(',', '.')
    }
}
