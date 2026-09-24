package com.kanzun.perbendaharaan.feature.notifications.domain.model

enum class NotificationCategory(val label: String) {
    TRANSAKSI("Transaksi"),
    TARGET_DANA("Target Dana"),
    RAPBM("RAPBM"),
    AUDIT("Audit"),
    BACKUP("Backup"),
    SECURITY("Keamanan"),
    SYSTEM("Sistem"),
}

enum class NotificationPriority {
    INFO,
    WARNING,
    IMPORTANT,
}

enum class NotificationFilter(val label: String) {
    ALL("Semua"),
    UNREAD("Belum Dibaca"),
    TRANSACTION("Transaksi"),
    TARGET("Target"),
    RAPBM("RAPBM"),
    AUDIT("Audit"),
    BACKUP("Backup"),
    SECURITY("Security"),
    SYSTEM("System"),
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val category: NotificationCategory,
    val priority: NotificationPriority,
    val timestamp: Long,
    val isRead: Boolean = false,
    val targetEntityType: String? = null,
    val targetEntityId: String? = null,
    val forRoleId: String? = null,
    val isDismissible: Boolean = true,
)

data class NotificationDateGroup(
    val groupTitle: String, // "Hari Ini", "Kemarin", "Minggu Ini", "Sebelumnya"
    val items: List<NotificationItem>,
)
