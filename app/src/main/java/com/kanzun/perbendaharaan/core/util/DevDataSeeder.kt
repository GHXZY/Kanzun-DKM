package com.kanzun.perbendaharaan.core.util

import com.kanzun.perbendaharaan.core.database.KanzunDatabase
import com.kanzun.perbendaharaan.core.database.dao.AccountDao
import com.kanzun.perbendaharaan.core.database.dao.CategoryDao
import com.kanzun.perbendaharaan.core.database.dao.FundDao
import com.kanzun.perbendaharaan.core.database.dao.SettingsDao
import com.kanzun.perbendaharaan.core.database.entity.AccountEntity
import com.kanzun.perbendaharaan.core.database.entity.CategoryEntity
import com.kanzun.perbendaharaan.core.database.entity.FundEntity
import com.kanzun.perbendaharaan.core.database.entity.MosqueEntity
import com.kanzun.perbendaharaan.core.database.entity.RoleEntity
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.feature.financial.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DevDataSeeder @Inject constructor(
    private val database: KanzunDatabase,
    private val repository: FinancialRepository,
    private val accountDao: AccountDao,
    private val fundDao: FundDao,
    private val categoryDao: CategoryDao,
    private val settingsDao: SettingsDao,
) {
    suspend fun seedDevelopmentData() {
        initCleanMasterData()
    }

    suspend fun initCleanMasterData() {
        // 1. Purge any legacy dummy records from previous testing
        purgeLegacyDummyData()

        // 2. Initialize Clean Master Accounts if empty
        val accounts = accountDao.getAllAccountsList()
        if (accounts.isEmpty()) {
            val bsiAccount = AccountEntity(
                id = "acc_bsi",
                name = "Rekening Bank (BSI)",
                accountNumber = "7123456789",
                bankName = "Bank Syariah Indonesia",
                isBank = true,
                openingBalanceInCents = 0L,
                currentBalanceInCents = 0L,
            )

            val cashAccount = AccountEntity(
                id = "acc_cash",
                name = "Kas Tunai Bendahara",
                accountNumber = "CASH-001",
                bankName = "Kas Tunai",
                isBank = false,
                openingBalanceInCents = 0L,
                currentBalanceInCents = 0L,
            )

            repository.addAccount(bsiAccount)
            repository.addAccount(cashAccount)
        }

        // 3. Initialize Clean Master Funds if empty
        val existingFunds = fundDao.getActiveFunds().first()
        if (existingFunds.isEmpty()) {
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
        }

        // 4. Initialize Clean Master Categories if empty
        val existingCategories = categoryDao.getAllCategories().first()
        if (existingCategories.isEmpty()) {
            val catInfaq = CategoryEntity("cat_infaq", "Infaq Kotak Jumat", TransactionType.INCOME, true)
            val catDonasi = CategoryEntity("cat_donasi", "Donasi & Sedekah", TransactionType.INCOME, true)
            val catZakat = CategoryEntity("cat_zakat", "Penerimaan Zakat", TransactionType.INCOME, true)
            val catLainnyaIn = CategoryEntity("cat_lainnya_in", "Pemasukan Lainnya", TransactionType.INCOME, true)

            val catOperasional = CategoryEntity("cat_operasional", "Operasional Masjid", TransactionType.EXPENSE, true)
            val catListrik = CategoryEntity("cat_listrik", "Listrik & Daya", TransactionType.EXPENSE, true)
            val catKebersihan = CategoryEntity("cat_kebersihan", "Kebersihan & Taman", TransactionType.EXPENSE, true)
            val catPemeliharaan = CategoryEntity("cat_pemeliharaan", "Pemeliharaan & Aset", TransactionType.EXPENSE, true)
            val catSosial = CategoryEntity("cat_sosial", "Bantuan Sosial", TransactionType.EXPENSE, true)
            val catZakatOut = CategoryEntity("cat_penyaluran_zakat", "Penyaluran Zakat", TransactionType.EXPENSE, true)

            categoryDao.insertCategory(catInfaq)
            categoryDao.insertCategory(catDonasi)
            categoryDao.insertCategory(catZakat)
            categoryDao.insertCategory(catLainnyaIn)
            categoryDao.insertCategory(catOperasional)
            categoryDao.insertCategory(catListrik)
            categoryDao.insertCategory(catKebersihan)
            categoryDao.insertCategory(catPemeliharaan)
            categoryDao.insertCategory(catSosial)
            categoryDao.insertCategory(catZakatOut)
        }

        // 5. Initialize Clean Mosque Profile if empty
        val mosque = settingsDao.getMosque().first()
        if (mosque == null) {
            settingsDao.insertOrUpdateMosque(
                MosqueEntity(
                    id = "m_1",
                    name = "Masjid Agung Al-Mubarak",
                    address = "Jl. Ahmad Yani No. 45, Jakarta",
                    treasurerName = "H. Muhammad Hatta",
                    dkmChairmanName = "H. Ahmad Dahlan",
                )
            )
        }

        // 6. Initialize Clean Default Role if empty
        val roles = settingsDao.getRoles().first()
        if (roles.isEmpty()) {
            settingsDao.insertRole(
                RoleEntity("role_bendahara", "Bendahara", "Pengelola Aplikasi Perbendaharaan Masjid")
            )
        }
    }

    private fun purgeLegacyDummyData() {
        try {
            val db = database.openHelper.writableDatabase

            // Delete legacy dummy transactions
            db.execSQL("DELETE FROM transactions WHERE id LIKE 'trx_seed_%' OR title IN ('Infaq Jamaah Pekan ke-3', 'Donasi Hamba Allah', 'Tagihan Listrik & Kebersihan')")

            // Delete legacy dummy fundraising targets & donations
            db.execSQL("DELETE FROM fundraising_targets WHERE id IN ('target_renov_toilet', 'target_renov_masjid', 'target_toilet', 'target_yatim')")
            db.execSQL("DELETE FROM donations WHERE id LIKE 'don_renov_%' OR id LIKE 'don_kas_%' OR id LIKE 'don_salur_%'")

            // Delete legacy dummy budgets & budget items
            db.execSQL("DELETE FROM budgets WHERE id IN ('budget_sep_2026', 'budget_oct_2026')")
            db.execSQL("DELETE FROM budget_items WHERE budgetId IN ('budget_sep_2026', 'budget_oct_2026')")

            // Delete legacy dummy mustahiks & zakat transactions
            db.execSQL("DELETE FROM mustahiks WHERE id IN ('m_01', 'm_02', 'm_03')")
            db.execSQL("DELETE FROM zakat_transactions WHERE id IN ('z_01', 'z_02', 'z_03')")

            // Delete legacy dummy assets
            db.execSQL("DELETE FROM assets WHERE id IN ('asset_01', 'asset_02', 'asset_03')")

            // Delete legacy dummy notifications
            db.execSQL("DELETE FROM notifications WHERE id LIKE 'notif_seed_%'")

            // Reset legacy dummy opening balances from 100M / 25.45M to 0
            db.execSQL("UPDATE accounts SET openingBalanceInCents = 0 WHERE openingBalanceInCents IN (100000000, 25450000)")

            // Recalculate account balances based purely on actual remaining non-reversed transactions
            db.execSQL("""
                UPDATE accounts 
                SET currentBalanceInCents = openingBalanceInCents + 
                    COALESCE((SELECT SUM(amountInCents) FROM transactions WHERE accountId = accounts.id AND type = 'INCOME' AND isReversed = 0), 0) -
                    COALESCE((SELECT SUM(amountInCents) FROM transactions WHERE accountId = accounts.id AND type = 'EXPENSE' AND isReversed = 0), 0)
            """.trimIndent())
        } catch (_: Exception) {
            // Ignore if tables are not yet created or migration in progress
        }
    }
}
