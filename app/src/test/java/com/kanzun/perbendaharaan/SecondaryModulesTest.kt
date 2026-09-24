package com.kanzun.perbendaharaan

import com.kanzun.perbendaharaan.core.database.entity.AssetEntity
import com.kanzun.perbendaharaan.core.database.entity.DonationEntity
import com.kanzun.perbendaharaan.core.database.entity.FundraisingTargetEntity
import com.kanzun.perbendaharaan.core.database.entity.MustahikEntity
import com.kanzun.perbendaharaan.core.database.entity.ZakatTransactionEntity
import com.kanzun.perbendaharaan.core.model.Money
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class SecondaryModulesTest {

    private val assets = mutableListOf<AssetEntity>()
    private val zakatTransactions = mutableListOf<ZakatTransactionEntity>()
    private val mustahiks = mutableListOf<MustahikEntity>()
    private val targets = mutableListOf<FundraisingTargetEntity>()
    private val donations = mutableListOf<DonationEntity>()

    @Before
    fun setUp() {
        assets.clear()
        zakatTransactions.clear()
        mustahiks.clear()
        targets.clear()
        donations.clear()
    }

    // MODULE A: ASET MASJID TESTS
    @Test
    fun testAssetCreationAndConditionFilter(): Unit = runBlocking {
        val asset1 = AssetEntity(
            id = "a_1",
            name = "Sound System Utama",
            categoryId = "Elektronik",
            acquisitionDate = System.currentTimeMillis(),
            acquisitionValueInCents = 35_000_000L,
            fundSourceId = "Dana Operasional",
            location = "Ruang Utama",
            conditionStatus = "Baik",
            serialNumber = "INV-001",
        )
        val asset2 = AssetEntity(
            id = "a_2",
            name = "AC Split 2 PK",
            categoryId = "Elektronik",
            acquisitionDate = System.currentTimeMillis(),
            acquisitionValueInCents = 18_000_000L,
            fundSourceId = "Dana Operasional",
            location = "Ruang Utama",
            conditionStatus = "Dalam Perbaikan",
            serialNumber = "INV-002",
        )
        assets.add(asset1)
        assets.add(asset2)

        assertEquals(2, assets.size)
        val baikAssets = assets.filter { it.conditionStatus == "Baik" }
        assertEquals(1, baikAssets.size)
        assertEquals("Sound System Utama", baikAssets.first().name)
    }

    @Test
    fun testAssetStatusUpdateToArchived(): Unit = runBlocking {
        val asset = AssetEntity(
            id = "a_3",
            name = "Kipas Angin Dinding",
            categoryId = "Elektronik",
            acquisitionDate = System.currentTimeMillis(),
            acquisitionValueInCents = 500_000L,
            fundSourceId = "Dana Operasional",
            location = "Gudang",
            conditionStatus = "Rusak",
            serialNumber = "INV-003",
        )
        assets.add(asset)

        val updated = asset.copy(conditionStatus = "Diarsipkan")
        val index = assets.indexOfFirst { it.id == asset.id }
        assets[index] = updated

        assertEquals("Diarsipkan", assets[index].conditionStatus)
    }

    // MODULE B: ZAKAT TESTS
    @Test
    fun testZakatReceiptAndDistributionBalanceIsolation(): Unit = runBlocking {
        val receipt1 = ZakatTransactionEntity(
            id = "z_1",
            zakatType = "Zakat Fitrah",
            isDistribution = false,
            amountInCents = 50_000_000L,
            muzakiOrMustahikName = "Jamaah Ramadan",
            timestamp = System.currentTimeMillis(),
            accountId = "acc_bsi",
            note = "Fitrah 1447H",
        )
        val receipt2 = ZakatTransactionEntity(
            id = "z_2",
            zakatType = "Zakat Maal",
            isDistribution = false,
            amountInCents = 20_000_000L,
            muzakiOrMustahikName = "Hamba Allah",
            timestamp = System.currentTimeMillis(),
            accountId = "acc_bsi",
            note = "Transfer BSI",
        )
        val distribution = ZakatTransactionEntity(
            id = "z_3",
            zakatType = "Penyaluran Fitrah",
            isDistribution = true,
            amountInCents = 40_000_000L,
            muzakiOrMustahikName = "Mustahik RT 01",
            timestamp = System.currentTimeMillis(),
            accountId = "acc_cash",
            note = "Distribusi beras & sembako",
        )

        zakatTransactions.add(receipt1)
        zakatTransactions.add(receipt2)
        zakatTransactions.add(distribution)

        val totalReceived = zakatTransactions.filter { !it.isDistribution }.sumOf { it.amountInCents }
        val totalDistributed = zakatTransactions.filter { it.isDistribution }.sumOf { it.amountInCents }
        val currentZakatBalance = totalReceived - totalDistributed

        assertEquals(70_000_000L, totalReceived)
        assertEquals(40_000_000L, totalDistributed)
        assertEquals(30_000_000L, currentZakatBalance)
    }

    @Test
    fun testMustahikRegistrationAndAssignment(): Unit = runBlocking {
        val mustahik = MustahikEntity(
            id = "m_1",
            name = "Bpk. Subarjo",
            asnafCategory = "Fakir",
            address = "RT 03 RW 05",
            phone = "081299998888",
        )
        mustahiks.add(mustahik)

        assertEquals(1, mustahiks.size)
        assertEquals("Fakir", mustahiks.first().asnafCategory)
    }

    // MODULE C: TARGET DANA & DONASI TESTS
    @Test
    fun testFundraisingTargetProgressAndStatusUpdate(): Unit = runBlocking {
        val target = FundraisingTargetEntity(
            id = "t_1",
            title = "Renovasi Wudhu",
            targetAmountInCents = 100_000_000L,
            collectedAmountInCents = 70_000_000L,
            startDate = System.currentTimeMillis(),
            endDate = System.currentTimeMillis() + 30 * 86400000L,
            status = "Aktif",
        )
        targets.add(target)

        val initialProgress = target.collectedAmountInCents.toFloat() / target.targetAmountInCents.toFloat()
        assertEquals(0.70f, initialProgress, 0.001f)

        // Record additional donation of 30.000.000
        val donation = DonationEntity(
            id = "d_1",
            targetId = target.id,
            donorName = "Donatur Dermawan",
            amountInCents = 30_000_000L,
            timestamp = System.currentTimeMillis(),
            accountId = "acc_bsi",
        )
        donations.add(donation)

        val updatedCollected = target.collectedAmountInCents + donation.amountInCents
        val updatedStatus = if (updatedCollected >= target.targetAmountInCents) "Tercapai" else target.status
        val updatedTarget = target.copy(collectedAmountInCents = updatedCollected, status = updatedStatus)
        targets[0] = updatedTarget

        assertEquals(100_000_000L, targets[0].collectedAmountInCents)
        assertEquals("Tercapai", targets[0].status)
    }
}
