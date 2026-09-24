package com.kanzun.perbendaharaan.feature.reports.domain.model

enum class ReportType(val title: String, val description: String) {
    FINANCIAL_SUMMARY("Ringkasan Keuangan", "Ringkasan total saldo kas, bank, pendapatan, dan pengeluaran"),
    CASH_FLOW("Arus Kas", "Laporan arus kas masuk, keluar, dan selisih bersih per periode"),
    TRANSACTION_HISTORY("Riwayat Transaksi", "Daftar rinci seluruh transaksi keuangan masjid"),
    RAPBM("RAPBM", "Rencana Anggaran Pendapatan dan Belanja Masjid tahunan"),
    BUDGET_VS_ACTUAL("Rencana vs Realisasi", "Perbandingan ketercapaian target anggaran vs transaksi aktual"),
    ASSETS("Aset Masjid", "Inventarisasi aset, kondisi barang, dan nilai akuisisi"),
    ZAKAT("Zakat", "Laporan penerimaan, penyaluran zakat, dan daftar mustahik"),
    FUNDRAISING("Target Dana & Donasi", "Perkembangan target penggalangan dana dan donasi diterima"),
    AUDIT_TRAIL("Audit Trail", "Catatan riwayat perubahan data, transaksi, dan aktivitas sistem"),
}
