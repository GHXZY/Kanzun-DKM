package com.kanzun.perbendaharaan.core.util

import com.kanzun.perbendaharaan.core.database.dao.CategoryDao
import com.kanzun.perbendaharaan.core.database.dao.FundraisingTargetDao
import com.kanzun.perbendaharaan.core.database.entity.AccountEntity
import com.kanzun.perbendaharaan.core.database.entity.CategoryEntity
import com.kanzun.perbendaharaan.core.database.entity.FundEntity
import com.kanzun.perbendaharaan.core.database.entity.FundraisingTargetEntity
import com.kanzun.perbendaharaan.core.model.Money
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.feature.financial.domain.repository.FinancialRepository
import com.kanzun.perbendaharaan.core.database.dao.NotificationDao
import com.kanzun.perbendaharaan.core.database.entity.NotificationEntity
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationCategory
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationPriority
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DevDataSeeder @Inject constructor(
    private val repository: FinancialRepository,
    private val categoryDao: CategoryDao,
    private val targetDao: FundraisingTargetDao,
    private val notificationDao: NotificationDao,
) {
    suspend fun seedDevelopmentData() {
        val bsiAccount = AccountEntity(
            id = "acc_bsi",
            name = "Bank BSI Utama",
            accountNumber = "7123456789",
            bankName = "Bank Syariah Indonesia",
            isBank = true,
            openingBalanceInCents = 100_000_000L, // Rp 100.000.000
            currentBalanceInCents = 100_000_000L,
        )

        val cashAccount = AccountEntity(
            id = "acc_cash",
            name = "Kas Tunai Bendahara",
            accountNumber = "CASH-001",
            bankName = "Kas Tunai",
            isBank = false,
            openingBalanceInCents = 25_450_000L, // Rp 25.450.000
            currentBalanceInCents = 25_450_000L,
        )

        repository.addAccount(bsiAccount)
        repository.addAccount(cashAccount)

        val fundOperational = FundEntity("fund_ops", "Dana Operasional", "Pengeluaran harian dan operasional masjid")
        val fundConstruction = FundEntity("fund_const", "Dana Pembangunan", "Pembangunan & renovasi fisik masjid")
        val fundZakat = FundEntity("fund_zakat", "Dana Zakat", "Penerimaan & penyaluran zakat")
        val fundEducation = FundEntity("fund_edu", "Dana Pendidikan", "Beasiswa & pengajian")
        val fundSocial = FundEntity("fund_social", "Dana Sosial", "Bantuan masyarakat fakir miskin")
        val fundActivities = FundEntity("fund_act", "Dana Kegiatan", "Peringatan hari besar Islam & kajian")

        repository.addFund(fundOperational)
        repository.addFund(fundConstruction)
        repository.addFund(fundZakat)
        repository.addFund(fundEducation)
        repository.addFund(fundSocial)
        repository.addFund(fundActivities)

        val catInfaq = CategoryEntity("cat_infaq", "Infaq Jumat", TransactionType.INCOME, true)
        val catDonasi = CategoryEntity("cat_donasi", "Donasi Pembangunan", TransactionType.INCOME, true)
        val catListrik = CategoryEntity("cat_listrik", "Listrik & Daya", TransactionType.EXPENSE, true)
        val catKebersihan = CategoryEntity("cat_kebersihan", "Kebersihan & Taman", TransactionType.EXPENSE, true)

        categoryDao.insertCategory(catInfaq)
        categoryDao.insertCategory(catDonasi)
        categoryDao.insertCategory(catListrik)
        categoryDao.insertCategory(catKebersihan)

        val activeTarget = FundraisingTargetEntity(
            id = "target_renov_toilet",
            title = "Renovasi Toilet & Tempat Wudhu",
            targetAmountInCents = 100_000_000L,
            collectedAmountInCents = 75_000_000L,
            startDate = System.currentTimeMillis() - 30 * 86400000L,
            endDate = System.currentTimeMillis() + 60 * 86400000L,
            status = "Aktif",
        )
        targetDao.insertTarget(activeTarget)

        // Initial sample transactions
        repository.createIncome(
            title = "Infaq Jamaah Pekan ke-3",
            amount = Money.of(2_500_000L),
            accountId = "acc_cash",
            fundId = "fund_ops",
            categoryId = "cat_infaq",
            note = "Infaq Kotak Jumat",
        )

        repository.createIncome(
            title = "Donasi Hamba Allah",
            amount = Money.of(30_000_000L),
            accountId = "acc_bsi",
            fundId = "fund_const",
            categoryId = "cat_donasi",
            note = "Transfer BSI",
        )

        repository.createExpense(
            title = "Tagihan Listrik & Kebersihan",
            amount = Money.of(1_200_000L),
            accountId = "acc_bsi",
            fundId = "fund_ops",
            categoryId = "cat_listrik",
            note = "PLN September",
        )

        // Seed initial notifications
        val now = System.currentTimeMillis()
        val hour = 3600_000L
        val day = 86400_000L

        val seedNotifications = listOf(
            NotificationEntity(
                id = "notif_seed_1",
                title = "Target Renovasi Toilet & Tempat Wudhu mencapai 75%",
                message = "Rp75.000.000 dari target Rp100.000.000.",
                category = NotificationCategory.TARGET_DANA.name,
                priority = NotificationPriority.INFO.name,
                timestamp = now - 2 * hour,
                isRead = false,
                targetEntityType = "target",
                targetEntityId = "target_renov_toilet",
            ),
            NotificationEntity(
                id = "notif_seed_2",
                title = "Anggaran listrik mendekati batas RAPBM",
                message = "Realisasi: Rp4.500.000. Anggaran: Rp5.000.000. Realisasi sudah mencapai 90% dari anggaran.",
                category = NotificationCategory.RAPBM.name,
                priority = NotificationPriority.WARNING.name,
                timestamp = now - 5 * hour,
                isRead = false,
                targetEntityType = "budget",
                targetEntityId = "budget_2026",
            ),
            NotificationEntity(
                id = "notif_seed_3",
                title = "Backup berhasil",
                message = "Cadangan data otomatis berhasil dibuat pada 22 September 2026, 18:30.",
                category = NotificationCategory.BACKUP.name,
                priority = NotificationPriority.INFO.name,
                timestamp = now - 1 * day,
                isRead = true,
                targetEntityType = "backup",
                targetEntityId = "b_seed_1",
            ),
            NotificationEntity(
                id = "notif_seed_4",
                title = "Transaksi dikoreksi",
                message = "Transaksi #TRX-2026-00125 telah dikoreksi oleh Bendahara.",
                category = NotificationCategory.AUDIT.name,
                priority = NotificationPriority.INFO.name,
                timestamp = now - 1 * day - 2 * hour,
                isRead = true,
                targetEntityType = "audit",
                targetEntityId = "aud_seed_1",
                isDismissible = false,
            ),
            NotificationEntity(
                id = "notif_seed_5",
                title = "Donasi dicatat",
                message = "Pemasukan Rp30.000.000 dari Donatur Utama telah dicatat ke Rekening BSI.",
                category = NotificationCategory.TRANSAKSI.name,
                priority = NotificationPriority.INFO.name,
                timestamp = now - 3 * day,
                isRead = true,
                targetEntityType = "transaction",
                targetEntityId = "trx_seed_1",
            ),
            NotificationEntity(
                id = "notif_seed_6",
                title = "Pengaturan keamanan diubah",
                message = "PIN aplikasi telah diperbarui oleh Bendahara.",
                category = NotificationCategory.SECURITY.name,
                priority = NotificationPriority.INFO.name,
                timestamp = now - 5 * day,
                isRead = true,
                targetEntityType = "security",
                targetEntityId = "sec_seed_1",
                isDismissible = false,
            ),
        )

        notificationDao.insertNotifications(seedNotifications)
    }
}
