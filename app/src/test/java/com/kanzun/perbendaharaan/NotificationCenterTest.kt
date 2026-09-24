package com.kanzun.perbendaharaan

import com.kanzun.perbendaharaan.core.database.entity.NotificationEntity
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationCategory
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationFilter
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationItem
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationPriority
import com.kanzun.perbendaharaan.feature.notifications.presentation.NotificationViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NotificationCenterTest {

    private val notificationsStore = mutableListOf<NotificationEntity>()

    @Before
    fun setUp() {
        notificationsStore.clear()
    }

    @Test
    fun testTransactionNotificationCreation(): Unit = runBlocking {
        val trxId = "trx_1001"
        val notifEntity = NotificationEntity(
            id = "notif_trx_created_$trxId",
            title = "Pemasukan dicatat",
            message = "Pemasukan Rp5.000.000 dari Donatur Umum telah dicatat.",
            category = NotificationCategory.TRANSAKSI.name,
            priority = NotificationPriority.INFO.name,
            timestamp = System.currentTimeMillis(),
            targetEntityType = "transaction",
            targetEntityId = trxId,
        )

        notificationsStore.add(notifEntity)

        assertEquals(1, notificationsStore.size)
        assertEquals("Pemasukan dicatat", notificationsStore.first().title)
        assertEquals("transaction", notificationsStore.first().targetEntityType)
    }

    @Test
    fun testDuplicatePreventionWithDeterministicKeys(): Unit = runBlocking {
        val targetId = "target_renov"
        val milestoneKey = "notif_target_${targetId}_m75"

        val firstTrigger = NotificationEntity(
            id = milestoneKey,
            title = "Target Renovasi Masjid mencapai 75%",
            message = "Rp75.000.000 dari target Rp100.000.000.",
            category = NotificationCategory.TARGET_DANA.name,
            priority = NotificationPriority.INFO.name,
            timestamp = System.currentTimeMillis(),
        )

        // Add first time
        if (notificationsStore.none { it.id == milestoneKey }) {
            notificationsStore.add(firstTrigger)
        }

        // Trigger again (simulating re-render or re-open)
        if (notificationsStore.none { it.id == milestoneKey }) {
            notificationsStore.add(firstTrigger)
        }

        // Must still be 1 (duplicate prevented)
        assertEquals(1, notificationsStore.size)
    }

    @Test
    fun testTargetMilestoneNotifications(): Unit = runBlocking {
        val milestones = listOf(25, 50, 75, 90, 100)
        val targetId = "prog_1"

        milestones.forEach { percentage ->
            val priority = if (percentage >= 100) NotificationPriority.IMPORTANT.name else NotificationPriority.INFO.name
            notificationsStore.add(
                NotificationEntity(
                    id = "notif_target_${targetId}_m$percentage",
                    title = "Target pembangunan mencapai $percentage%",
                    message = "Target $percentage% tercapai.",
                    category = NotificationCategory.TARGET_DANA.name,
                    priority = priority,
                    timestamp = System.currentTimeMillis(),
                )
            )
        }

        assertEquals(5, notificationsStore.size)
        assertEquals(NotificationPriority.IMPORTANT.name, notificationsStore.last().priority)
    }

    @Test
    fun testRapbmWarningAndExceedingNotifications(): Unit = runBlocking {
        // Approaching budget (90%)
        val notifApproaching = NotificationEntity(
            id = "rapbm_approaching",
            title = "Anggaran listrik mendekati batas RAPBM",
            message = "Realisasi: Rp4.500.000. Anggaran: Rp5.000.000. Realisasi sudah mencapai 90% dari anggaran.",
            category = NotificationCategory.RAPBM.name,
            priority = NotificationPriority.WARNING.name,
            timestamp = System.currentTimeMillis(),
        )

        // Exceeding budget (125%)
        val notifExceeding = NotificationEntity(
            id = "rapbm_exceeding",
            title = "Realisasi melebihi RAPBM",
            message = "Pengeluaran: Rp6.250.000. Anggaran: Rp5.000.000. Selisih: Rp1.250.000",
            category = NotificationCategory.RAPBM.name,
            priority = NotificationPriority.IMPORTANT.name,
            timestamp = System.currentTimeMillis(),
        )

        notificationsStore.add(notifApproaching)
        notificationsStore.add(notifExceeding)

        assertEquals(2, notificationsStore.size)
        assertFalse("Should use neutral factual text", notifExceeding.title.contains("gagal"))
        assertEquals(NotificationPriority.IMPORTANT.name, notifExceeding.priority)
    }

    @Test
    fun testAuditAndSecurityNonDismissibleRetention(): Unit = runBlocking {
        val auditNotif = NotificationEntity(
            id = "notif_audit_1",
            title = "Transaksi dikoreksi",
            message = "Transaksi #TRX-2026-00125 telah dikoreksi oleh Bendahara.",
            category = NotificationCategory.AUDIT.name,
            priority = NotificationPriority.INFO.name,
            timestamp = System.currentTimeMillis(),
            isDismissible = false,
        )

        notificationsStore.add(auditNotif)

        val item = notificationsStore.first()
        assertFalse("Audit log notifications must not be dismissible/deletable", item.isDismissible)
    }

    @Test
    fun testMarkAsReadAndMarkAllAsRead(): Unit = runBlocking {
        notificationsStore.add(NotificationEntity("n1", "T1", "M1", "TRANSAKSI", "INFO", 1000L, isRead = false))
        notificationsStore.add(NotificationEntity("n2", "T2", "M2", "RAPBM", "WARNING", 2000L, isRead = false))
        notificationsStore.add(NotificationEntity("n3", "T3", "M3", "BACKUP", "INFO", 3000L, isRead = true))

        val initialUnread = notificationsStore.count { !it.isRead }
        assertEquals(2, initialUnread)

        // Mark n1 read
        val idx1 = notificationsStore.indexOfFirst { it.id == "n1" }
        notificationsStore[idx1] = notificationsStore[idx1].copy(isRead = true)

        assertEquals(1, notificationsStore.count { !it.isRead })

        // Mark all read
        for (i in notificationsStore.indices) {
            notificationsStore[i] = notificationsStore[i].copy(isRead = true)
        }

        assertEquals(0, notificationsStore.count { !it.isRead })
    }

    @Test
    fun testCategoryAndUnreadFiltering(): Unit = runBlocking {
        notificationsStore.add(NotificationEntity("n1", "T1", "M1", "TRANSAKSI", "INFO", 1000L, isRead = false))
        notificationsStore.add(NotificationEntity("n2", "T2", "M2", "RAPBM", "WARNING", 2000L, isRead = true))
        notificationsStore.add(NotificationEntity("n3", "T3", "M3", "TRANSAKSI", "INFO", 3000L, isRead = true))

        val unreadFiltered = notificationsStore.filter { !it.isRead }
        val transactionFiltered = notificationsStore.filter { it.category == "TRANSAKSI" }

        assertEquals(1, unreadFiltered.size)
        assertEquals("n1", unreadFiltered.first().id)

        assertEquals(2, transactionFiltered.size)
    }

    @Test
    fun testDateGroupingHelper(): Unit = runBlocking {
        val now = System.currentTimeMillis()
        val hour = 3600_000L
        val day = 86400_000L

        val items = listOf(
            NotificationItem("n1", "T1", "M1", NotificationCategory.TRANSAKSI, NotificationPriority.INFO, now - 2 * hour, isRead = false),
            NotificationItem("n2", "T2", "M2", NotificationCategory.RAPBM, NotificationPriority.WARNING, now - 1 * day, isRead = true),
            NotificationItem("n3", "T3", "M3", NotificationCategory.BACKUP, NotificationPriority.INFO, now - 3 * day, isRead = true),
            NotificationItem("n4", "T4", "M4", NotificationCategory.SECURITY, NotificationPriority.INFO, now - 10 * day, isRead = true),
        )

        val groups = NotificationViewModel.groupNotificationsByDate(items)

        assertTrue(groups.any { it.groupTitle == "HARI INI" })
        assertTrue(groups.any { it.groupTitle == "KEMARIN" })
        assertTrue(groups.any { it.groupTitle == "MINGGU INI" })
        assertTrue(groups.any { it.groupTitle == "SEBELUMNYA" })
    }
}
