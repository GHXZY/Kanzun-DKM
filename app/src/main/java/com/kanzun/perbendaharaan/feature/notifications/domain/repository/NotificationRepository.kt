package com.kanzun.perbendaharaan.feature.notifications.domain.repository

import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationFilter
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationItem
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(filter: NotificationFilter = NotificationFilter.ALL, userRoleId: String? = null): Flow<List<NotificationItem>>
    fun getUnreadCount(): Flow<Int>
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
    suspend fun dismissNotification(id: String)

    // Event Triggers with Deterministic Key Generation (Duplicate Prevention)
    suspend fun notifyTransactionRecorded(transactionId: String, title: String, message: String)
    suspend fun notifyTransactionFinalized(transactionId: String, title: String, message: String)
    suspend fun notifyTransactionReversed(transactionId: String, title: String, message: String)

    suspend fun notifyTargetMilestone(targetId: String, targetTitle: String, milestonePercentage: Int, currentCents: Long, targetCents: Long)

    suspend fun notifyRapbmWarning(budgetId: String, categoryTitle: String, actualCents: Long, budgetCents: Long, percentage: Int)

    suspend fun notifyAuditLogged(auditLogId: String, action: String, entityName: String, entityId: String, actorName: String)

    suspend fun notifyBackupStatus(backupId: String, isSuccess: Boolean, message: String, timestamp: Long)

    suspend fun notifySecurityEvent(eventId: String, title: String, message: String)

    suspend fun notifySystemEvent(eventId: String, title: String, message: String)
}
