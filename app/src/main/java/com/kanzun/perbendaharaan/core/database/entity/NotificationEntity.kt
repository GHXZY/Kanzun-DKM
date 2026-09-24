package com.kanzun.perbendaharaan.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val category: String, // TRANSAKSI, TARGET_DANA, RAPBM, AUDIT, BACKUP, SECURITY, SYSTEM
    val priority: String, // INFO, WARNING, IMPORTANT
    val timestamp: Long,
    val isRead: Boolean = false,
    val targetEntityType: String? = null, // transaction, target, budget, audit, backup, security
    val targetEntityId: String? = null,
    val forRoleId: String? = null, // null = all roles, or specific role like "role_bendahara", "role_ketua", "role_auditor"
    val isDismissible: Boolean = true,
)
