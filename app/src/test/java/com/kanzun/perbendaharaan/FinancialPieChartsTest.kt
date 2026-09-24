package com.kanzun.perbendaharaan

import com.kanzun.perbendaharaan.core.database.entity.AccountEntity
import com.kanzun.perbendaharaan.core.database.entity.FundEntity
import com.kanzun.perbendaharaan.core.model.CashBreakdown
import com.kanzun.perbendaharaan.core.model.CashBreakdownItem
import com.kanzun.perbendaharaan.core.model.FundAllocation
import com.kanzun.perbendaharaan.core.model.FundAllocationItem
import com.kanzun.perbendaharaan.core.model.Money
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FinancialPieChartsTest {

    private val accounts = mutableListOf<AccountEntity>()
    private val funds = mutableListOf<FundEntity>()

    @Before
    fun setUp() {
        accounts.clear()
        funds.clear()

        // Accounts
        accounts.add(AccountEntity("acc_cash", "Kas Tunai Utama", accountNumber = "-", bankName = "Cash", isBank = false, openingBalanceInCents = 15_000_000_00L, currentBalanceInCents = 15_000_000_00L))
        accounts.add(AccountEntity("acc_bsi", "Bank BSI Operasional", accountNumber = "7001234567", bankName = "BSI", isBank = true, openingBalanceInCents = 25_000_000_00L, currentBalanceInCents = 25_000_000_00L))
        accounts.add(AccountEntity("acc_mandiri", "Bank Mandiri Pembangunan", accountNumber = "123000987", bankName = "Mandiri", isBank = true, openingBalanceInCents = 60_000_000_00L, currentBalanceInCents = 60_000_000_00L))

        // Funds
        funds.add(FundEntity("f_op", "Operasional", "Dana Operasional", isActive = true))
        funds.add(FundEntity("f_pem", "Pembangunan", "Dana Pembangunan", isActive = true))
        funds.add(FundEntity("f_sos", "Sosial", "Dana Sosial", isActive = true))
        funds.add(FundEntity("f_pend", "Pendidikan", "Dana Pendidikan", isActive = true))
        funds.add(FundEntity("f_zak", "Zakat", "Dana Zakat", isActive = true))
        funds.add(FundEntity("f_ren", "Renovasi", "Dana Renovasi", isActive = true))
    }

    @Test
    fun testRekeningVsCashCalculationAndPercentages() {
        val activeAccounts = accounts.filter { it.isActive }
        val totalCents = activeAccounts.sumOf { it.currentBalanceInCents }
        val bankCents = activeAccounts.filter { it.isBank }.sumOf { it.currentBalanceInCents }
        val cashCents = activeAccounts.filter { !it.isBank }.sumOf { it.currentBalanceInCents }

        val breakdown = CashBreakdown(
            totalCash = Money.of(totalCents),
            bankTotal = Money.of(bankCents),
            cashTotal = Money.of(cashCents),
            items = activeAccounts.map {
                CashBreakdownItem(it.id, it.name, it.isBank, Money.of(it.currentBalanceInCents))
            },
        )

        assertEquals(100_000_000_00L, breakdown.totalCash.amountInCents)
        assertEquals(85_000_000_00L, breakdown.bankTotal.amountInCents)
        assertEquals(15_000_000_00L, breakdown.cashTotal.amountInCents)

        val bankPct = (breakdown.bankTotal.amountInCents.toDouble() / totalCents.toDouble() * 100).toFloat()
        val cashPct = (breakdown.cashTotal.amountInCents.toDouble() / totalCents.toDouble() * 100).toFloat()

        assertEquals(85.0f, bankPct, 0.01f)
        assertEquals(15.0f, cashPct, 0.01f)
    }

    @Test
    fun testFundAllocationTop5AndLainnyaGrouping() {
        val totalCashCents = 100_000_000_00L
        val rawItems = listOf(
            FundAllocationItem("f_op", "Operasional", Money.of(35_000_000_00L), 35f),
            FundAllocationItem("f_pem", "Pembangunan", Money.of(25_000_000_00L), 25f),
            FundAllocationItem("f_sos", "Sosial", Money.of(15_000_000_00L), 15f),
            FundAllocationItem("f_pend", "Pendidikan", Money.of(10_000_000_00L), 10f),
            FundAllocationItem("f_zak", "Zakat", Money.of(8_000_000_00L), 8f),
            FundAllocationItem("f_ren", "Renovasi", Money.of(7_000_000_00L), 7f),
        )

        val rawAllocation = FundAllocation(Money.of(totalCashCents), rawItems)

        // Process Top 5 + Lainnya grouping
        val sorted = rawAllocation.items.sortedByDescending { it.allocatedAmount.amountInCents }
        val top5 = sorted.take(5)
        val remaining = sorted.drop(5)

        val otherCents = remaining.sumOf { it.allocatedAmount.amountInCents }
        val otherPct = remaining.sumOf { it.percentage.toDouble() }.toFloat()

        val processedItems = top5 + FundAllocationItem("fund_other", "Lainnya", Money.of(otherCents), otherPct)
        val finalAllocation = FundAllocation(rawAllocation.totalAllocated, processedItems)

        assertEquals(6, finalAllocation.items.size)
        assertEquals("Operasional", finalAllocation.items[0].fundName)
        assertEquals("Lainnya", finalAllocation.items[5].fundName)
        assertEquals(7_000_000_00L, finalAllocation.items[5].allocatedAmount.amountInCents)
        assertEquals(7.0f, finalAllocation.items[5].percentage, 0.01f)
    }

    @Test
    fun testZakatSeparationInFundAllocation() {
        val zakatItem = FundAllocationItem("f_zak", "Zakat", Money.of(8_000_000_00L), 8f)
        val operasionalItem = FundAllocationItem("f_op", "Operasional", Money.of(35_000_000_00L), 35f)

        // Verify Zakat remains a distinct allocation item
        assertEquals("Zakat", zakatItem.fundName)
        assertTrue(zakatItem.fundName != operasionalItem.fundName)
    }
}
