Konsep Anda sudah memiliki fondasi yang kuat. Namun, agar aplikasi **Perbendaharaan Masjid** benar-benar siap dikembangkan, saya sarankan konsepnya tidak berhenti pada “aplikasi pencatatan kas”, tetapi diposisikan sebagai **sistem manajemen keuangan dan akuntabilitas masjid**.

Hal penting yang perlu ditambahkan adalah **pemisahan sumber dan peruntukan dana**, **audit trail**, **RAPBM vs realisasi**, **manajemen aset**, **target dana**, serta **laporan yang siap dipertanggungjawabkan kepada DKM/jamaah**.

Di bawah ini saya susun PRD yang bisa langsung dijadikan dasar UI/UX, database, dan development.

---

# PRD — Aplikasi Perbendaharaan Masjid

**Nama Produk:** Perbendaharaan Masjid
**Platform:** Android
**Target Pengguna:** Bendahara Masjid, Ketua DKM, Pengurus Masjid
**Kategori:** Mosque Financial Management / Treasury Management
**Status:** Product Requirement Document — Draft 1.0

---

# 1. Product Overview

## 1.1. Latar Belakang

Pengelolaan keuangan masjid sering dilakukan menggunakan buku kas, spreadsheet, atau aplikasi keuangan umum yang tidak dirancang untuk kebutuhan masjid.

Masalah yang sering muncul:

* pemasukan dan pengeluaran sulit ditelusuri;
* saldo kas tidak langsung menggambarkan kondisi dana yang sebenarnya;
* uang rekening dan uang tunai bercampur dalam pencatatan;
* dana untuk tujuan tertentu tidak terpantau dengan baik;
* aset masjid tidak terdokumentasi;
* RAPBM dan realisasi sulit dibandingkan;
* pembuatan laporan membutuhkan pekerjaan manual;
* laporan keuangan belum memiliki format yang konsisten;
* proses pergantian bendahara berisiko kehilangan data;
* sulit membuat laporan yang siap disampaikan kepada Ketua DKM maupun jamaah.

**Perbendaharaan Masjid** dirancang sebagai aplikasi Android yang membantu pengurus masjid mencatat, mengelola, memantau, dan melaporkan kondisi keuangan masjid secara terstruktur.

---

# 2. Product Vision

> **Membantu masjid mengelola amanah keuangan secara tertib, transparan, dan mudah dipertanggungjawabkan.**

Aplikasi tidak hanya menjawab:

> “Berapa uang masjid sekarang?”

Tetapi juga:

> “Dari mana uang tersebut berasal?”
> “Untuk apa dana tersebut dialokasikan?”
> “Berapa yang sudah digunakan?”
> “Bagaimana realisasinya dibandingkan RAPBM?”
> “Apa saja aset yang dimiliki masjid?”
> “Apakah target dana sudah tercapai?”
> “Bagaimana riwayat setiap transaksi?”
> “Bisakah seluruhnya dilaporkan secara profesional?”

---

# 3. Product Goals

## Primary Goals

1. Mencatat seluruh pemasukan dan pengeluaran masjid.
2. Mengetahui posisi kas secara real-time.
3. Memisahkan kas berdasarkan rekening, cash, sumber dana, dan peruntukan.
4. Membantu bendahara membuat RAPBM.
5. Membandingkan rencana anggaran dengan realisasi.
6. Mengelola aset masjid.
7. Mengelola target dana dan donasi.
8. Mendukung pengelolaan dana zakat secara terpisah.
9. Menghasilkan laporan keuangan profesional.
10. Menyediakan audit trail untuk meningkatkan akuntabilitas.
11. Menyediakan backup dan keamanan data.

---

# 4. Prinsip Produk

Aplikasi harus dibangun dengan prinsip:

### 4.1. Simple

Bendahara tidak harus memahami akuntansi secara mendalam untuk menggunakannya.

### 4.2. Transparent

Setiap transaksi dapat ditelusuri kembali.

### 4.3. Structured

Data keuangan memiliki kategori dan struktur yang jelas.

### 4.4. Accountable

Laporan dapat digunakan untuk pertanggungjawaban internal maupun eksternal.

### 4.5. Separate Funds

Dana dengan sumber atau peruntukan berbeda tidak boleh tercampur secara konseptual.

### 4.6. Auditability

Perubahan penting terhadap data harus dapat ditelusuri.

---

# 5. Target Users

## 5.1. Bendahara

Pengguna utama aplikasi.

Kebutuhan:

* mencatat pemasukan;
* mencatat pengeluaran;
* mengelola saldo;
* mengelola RAPBM;
* mengelola aset;
* membuat laporan;
* melakukan rekonsiliasi.

---

## 5.2. Ketua DKM

Kebutuhan:

* melihat kondisi keuangan;
* melihat laporan;
* memantau RAPBM;
* melihat realisasi;
* memantau target dana;
* melakukan review laporan.

Tidak harus memiliki akses untuk mengubah transaksi.

---

## 5.3. Pengurus

Akses terbatas sesuai izin.

Contoh:

* melihat dashboard;
* melihat laporan;
* membantu input transaksi;
* mengelola data tertentu.

---

## 5.4. Auditor / Pemeriksa Internal

Jika nantinya diperlukan.

Akses:

* melihat transaksi;
* melihat perubahan data;
* melihat laporan;
* melihat audit trail;

tanpa hak mengubah transaksi.

---

# 6. Information Architecture

Struktur utama aplikasi:

```text
PERBENDAHARAAN MASJID
│
├── Beranda
│
├── Bendahara
│   ├── Arus Kas
│   │   ├── Pemasukan
│   │   ├── Pengeluaran
│   │   └── Transfer Antar Kas
│   │
│   ├── Aset Masjid
│   │
│   ├── Zakat
│   │
│   ├── Target Dana & Donasi
│   │
│   └── RAPBM
│       ├── Pendapatan
│       ├── Belanja
│       └── Rencana vs Realisasi
│
├── Laporan
│   ├── Ringkasan Keuangan
│   ├── Arus Kas
│   ├── Transaksi
│   ├── RAPBM
│   ├── Aset
│   ├── Zakat
│   ├── Target Dana
│   ├── Rekening & Cash
│   └── Audit Laporan
│
└── Pengaturan
    ├── Profil Masjid
    ├── Pengguna & Hak Akses
    ├── Kategori
    ├── Backup & Restore
    ├── Keamanan
    ├── Preferensi
    ├── Sistem
    └── Tentang Aplikasi
```

---

# 7. Dashboard / Beranda

Dashboard menjadi pusat monitoring kondisi keuangan.

## 7.1. Total Kas

Card utama:

**Total Kas**

```text
Rp 125.450.000
```

Formula konseptual:

> Total Kas = seluruh saldo kas dan rekening yang termasuk dalam pembukuan masjid.

Dapat diberikan breakdown:

```text
Rekening     Rp 100.000.000
Cash         Rp 25.450.000
-------------------------
Total        Rp 125.450.000
```

---

# 8. Sumber Dana — Rekening vs Cash

Visualisasi:

**Distribusi Kas**

* Rekening Bank
* Cash / Kas Tunai

Contoh:

```text
Rekening
80%

Cash
20%
```

Pengguna dapat tap chart untuk melihat detail setiap rekening.

---

# 9. Chart Alokasi Dana

Jangan hanya menggunakan kategori “pemasukan/pengeluaran”.

Sebaiknya terdapat **Dana / Fund**.

Contoh:

```text
Dana Operasional       40%
Dana Pembangunan       25%
Dana Pendidikan        15%
Dana Sosial            10%
Dana Kegiatan          10%
```

Ini sangat penting karena:

> Saldo total yang besar belum tentu berarti seluruh uang tersebut bebas digunakan.

Dana tertentu bisa memiliki peruntukan khusus.

---

# 10. Status Keuangan

Dashboard menampilkan indikator kondisi keuangan.

Contoh:

### Kondisi Keuangan

**Stabil**

Informasi pendukung:

```text
Saldo saat ini        Rp125 jt
Pengeluaran bulan ini Rp18 jt
Pemasukan bulan ini   Rp32 jt
Net Cash Flow         +Rp14 jt
```

Status dapat dihitung berdasarkan indikator seperti:

* cash flow;
* saldo minimum;
* realisasi anggaran;
* pengeluaran vs pemasukan;
* target dana;
* kewajiban yang belum dibayar.

**Catatan:** status sebaiknya bersifat informatif, bukan “penilaian kesehatan keuangan” yang terlalu sederhana.

---

# 11. Progress Target Dana

Contoh:

### Renovasi Toilet

```text
Rp 75.000.000 / Rp 100.000.000

███████████████░░░░░
75%
```

Target dapat berasal dari:

* pembangunan;
* renovasi;
* pembelian aset;
* kegiatan Ramadan;
* beasiswa;
* bantuan sosial;
* kebutuhan lain.

---

# 12. Bendahara — Arus Kas

Ini adalah modul inti.

## 12.1. Pemasukan

Field:

* tanggal;
* nominal;
* sumber dana;
* kategori;
* rekening/kas;
* metode pembayaran;
* target dana;
* donatur *(opsional)*;
* nomor referensi;
* deskripsi;
* lampiran bukti;
* catatan.

Contoh:

```text
Tanggal       22 Sep 2026
Jenis         Pemasukan
Kategori      Infaq Jumat
Nominal       Rp2.500.000
Kas           Cash
Target        Operasional
Catatan       Infaq Jumat pekan ke-3
```

---

# 13. Pengeluaran

Field:

* tanggal;
* nominal;
* kategori;
* sumber/peruntukan dana;
* rekening/kas;
* penerima;
* nomor bukti;
* deskripsi;
* lampiran;
* catatan.

Contoh:

```text
Kategori:
- Listrik
- Air
- Internet
- Kebersihan
- Honor
- Kegiatan
- Pembangunan
- Sosial
- Pendidikan
- Pemeliharaan
- Administrasi
- Lainnya
```

Kategori harus dapat dibuat sendiri oleh administrator.

---

# 14. Transfer Antar Kas

Fitur ini **wajib ditambahkan**.

Misalnya:

```text
Bank BSI
Rp5.000.000
        ↓
Cash
Rp5.000.000
```

Transfer tidak boleh dianggap sebagai pemasukan atau pengeluaran.

Tujuannya hanya memindahkan saldo:

> Rekening A → Rekening B

Dengan demikian total kas tidak berubah.

---

# 15. Rekonsiliasi Kas

Fitur penting untuk bendahara.

Bendahara dapat memasukkan:

```text
Saldo sistem:
Rp25.450.000

Saldo fisik:
Rp25.400.000

Selisih:
-Rp50.000
```

Kemudian aplikasi meminta:

**Catatan Selisih**

dan menyimpan proses rekonsiliasi sebagai audit record.

---

# 16. Manajemen Aset Masjid

Aset tidak hanya berupa uang.

Contoh:

* tanah;
* bangunan;
* kendaraan;
* sound system;
* AC;
* komputer;
* laptop;
* kamera;
* karpet;
* meja;
* kursi;
* inventaris kantor.

## Data Aset

```text
Nama Aset
Kategori
Tanggal Perolehan
Nilai Perolehan
Sumber Dana
Lokasi
Kondisi
Penanggung Jawab
Nomor Inventaris
Foto
Dokumen
Catatan
```

Status:

* Aktif
* Rusak
* Dalam Perbaikan
* Hilang
* Dihapuskan

---

# 17. Zakat

Modul zakat sebaiknya **dipisahkan dari kas operasional biasa**.

Struktur:

```text
Zakat
├── Penerimaan
├── Penyaluran
├── Mustahik
├── Saldo Dana Zakat
└── Laporan Zakat
```

## Penerimaan

Data:

* tanggal;
* muzaki *(opsional)*;
* jenis zakat;
* nominal;
* metode pembayaran;
* rekening/kas;
* catatan.

## Penyaluran

Data:

* tanggal;
* mustahik;
* kategori/asnaf;
* nominal;
* bentuk bantuan;
* bukti;
* catatan.

Aplikasi harus mampu menunjukkan:

```text
Total Zakat Masuk
Rp50.000.000

Total Disalurkan
Rp42.000.000

Saldo Dana Zakat
Rp8.000.000
```

---

# 18. Target Dana & Donasi

Modul ini sebaiknya memiliki dua konsep:

### Target Dana

Tujuan yang ingin dicapai.

### Donasi

Transaksi yang berkontribusi terhadap target tersebut.

Contoh:

**Renovasi Masjid**

```text
Target:
Rp250.000.000

Terkumpul:
Rp175.000.000

Progress:
70%
```

Data target:

* nama target;
* deskripsi;
* target nominal;
* tanggal mulai;
* deadline;
* dana terkumpul;
* status;
* kategori;
* foto;
* catatan.

Status:

* Draft
* Aktif
* Tercapai
* Selesai
* Dibatalkan

---

# 19. RAPBM

**Rencana Anggaran Pendapatan dan Belanja Masjid** harus menjadi modul tersendiri.

Pengguna dapat membuat RAPBM berdasarkan periode:

```text
RAPBM 2027
```

## Pendapatan

Contoh:

* infaq;
* sedekah;
* donasi;
* zakat *(jika memang dicatat dalam struktur terpisah, jangan otomatis dicampurkan)*;
* wakaf;
* usaha masjid;
* sumber lainnya.

## Belanja

Contoh:

* operasional;
* listrik;
* air;
* kebersihan;
* honor;
* kegiatan;
* pendidikan;
* sosial;
* pembangunan;
* pemeliharaan;
* administrasi.

---

# 20. Rencana vs Realisasi

Ini menjadi salah satu fitur unggulan.

Contoh:

| Kategori     | Rencana | Realisasi | Selisih |
| ------------ | ------: | --------: | ------: |
| Listrik      | Rp12 jt |   Rp10 jt | +Rp2 jt |
| Kebersihan   |  Rp8 jt |    Rp9 jt | -Rp1 jt |
| Kegiatan     | Rp20 jt |   Rp17 jt | +Rp3 jt |
| Pemeliharaan | Rp15 jt |   Rp18 jt | -Rp3 jt |

Visualisasi:

**Budget vs Actual**

Dengan indikator:

* Under Budget
* On Budget
* Over Budget

Tetapi hindari membuat warna/status yang terlalu “menghakimi”. Tujuannya adalah membantu bendahara menemukan deviasi.

---

# 21. Laporan

Modul laporan menjadi pusat pertanggungjawaban.

## 21.1. Laporan Ringkasan Keuangan

```text
Periode:
1 Januari – 31 Januari 2027

Saldo Awal
Rp100.000.000

Total Pemasukan
Rp35.000.000

Total Pengeluaran
Rp20.000.000

Saldo Akhir
Rp115.000.000
```

---

# 22. Laporan Arus Kas

Menampilkan:

```text
Saldo Awal
+ Pemasukan
- Pengeluaran
± Transfer
= Saldo Akhir
```

Dapat difilter:

* periode;
* rekening;
* kategori;
* sumber dana;
* target;
* jenis transaksi.

---

# 23. Riwayat Transaksi

Tampilan seperti ledger:

```text
22 Sep
+ Rp2.500.000
Infaq Jumat

21 Sep
- Rp750.000
Pembelian Alat Kebersihan

20 Sep
- Rp1.200.000
Tagihan Listrik
```

Filter:

```text
Semua
Pemasukan
Pengeluaran
Transfer
```

---

# 24. Laporan Aset

Contoh:

```text
Total Aset
Rp850.000.000

Tanah
Rp500.000.000

Bangunan
Rp250.000.000

Inventaris
Rp100.000.000
```

Nilai aset perlu dapat dikonfigurasi karena tidak semua aset cocok diperlakukan dengan metode nilai yang sama.

---

# 25. Laporan Zakat

Menampilkan:

* total penerimaan;
* total penyaluran;
* saldo;
* distribusi berdasarkan asnaf;
* periode;
* daftar transaksi.

---

# 26. Laporan Target Dana

Contoh:

```text
Renovasi Masjid
Target       Rp250 jt
Terkumpul    Rp175 jt
Progress     70%

Pembelian Karpet
Target       Rp50 jt
Terkumpul    Rp50 jt
Progress     100%
```

---

# 27. Audit Laporan

Saya menyarankan **Audit Laporan** dibedakan dari sekadar “export PDF”.

Audit memiliki dua lapisan.

## 27.1. Audit Data

Mencatat:

```text
Siapa
Melakukan apa
Kapan
Data sebelum
Data sesudah
```

Contoh:

```text
22 Sep 2026 14:30
Bendahara

Mengubah transaksi #TRX-000123

Sebelum:
Rp500.000

Sesudah:
Rp550.000
```

---

# 28. Audit Trail

Event yang dicatat:

* membuat transaksi;
* mengedit transaksi;
* menghapus transaksi;
* membuat target;
* mengubah RAPBM;
* mengubah profil masjid;
* restore backup;
* perubahan pengguna;
* perubahan permission.

Idealnya transaksi yang sudah masuk laporan periode tertentu dapat:

> **dikunci / finalized**

Setelah dikunci, perubahan tidak boleh dilakukan tanpa proses koreksi yang tercatat.

Ini jauh lebih aman daripada memungkinkan bendahara menghapus transaksi begitu saja.

---

# 29. Export PDF

Format PDF harus menjadi salah satu fitur utama aplikasi.

Pilihan:

```text
Export Laporan
↓
Pilih Jenis
↓
Pilih Periode
↓
Preview
↓
Export PDF
```

Jenis:

* Laporan Keuangan;
* Arus Kas;
* Transaksi;
* RAPBM;
* Realisasi RAPBM;
* Aset;
* Zakat;
* Target Dana;
* Audit Trail.

---

# 30. Korp Surat

Korp surat dikonfigurasi melalui:

**Pengaturan → Profil Masjid**

Data:

```text
Logo
Nama Masjid
Alamat
Desa/Kelurahan
Kecamatan
Kabupaten/Kota
Provinsi
Nomor Telepon
Email
Website
```

Contoh PDF:

```text
[LOGO]

MASJID __________
Alamat __________
Telp __________ | Email __________

-----------------------------------
LAPORAN KEUANGAN MASJID
Periode __________
-----------------------------------
```

---

# 31. Tanda Tangan

Di bagian bawah laporan:

```text
Mengetahui,                         Bendahara,

Ketua DKM                           Bendahara


(________________)                  (________________)
Nama                                Nama
```

Pengaturan harus memungkinkan menentukan:

* jabatan;
* nama;
* tanda tangan;
* urutan;
* apakah tanda tangan ditampilkan.

Jika ingin lebih profesional, tanda tangan digital/image dapat disimpan sebagai aset profil dengan proteksi akses.

---

# 32. Pengaturan Profil Masjid

Menu:

**Pengaturan → Data Masjid**

Field:

```text
Logo
Nama Masjid
Alamat
RT/RW
Desa/Kelurahan
Kecamatan
Kabupaten/Kota
Provinsi
Kode Pos
Nomor Telepon
Email
Website
```

Pengurus:

```text
Ketua DKM
Bendahara
Sekretaris
```

---

# 33. Pengguna & Hak Akses

Ini perlu ditambahkan ke konsep awal.

Contoh:

| Role      | Dashboard | Transaksi   | RAPBM | Laporan | Setting  |
| --------- | --------- | ----------- | ----- | ------- | -------- |
| Ketua DKM | ✓         | View        | View  | ✓       | Terbatas |
| Bendahara | ✓         | ✓           | ✓     | ✓       | Terbatas |
| Pengurus  | ✓         | Sesuai izin | View  | View    | -        |
| Auditor   | ✓         | View        | View  | ✓       | -        |
| Admin     | ✓         | ✓           | ✓     | ✓       | ✓        |

Permission harus granular jika produk berkembang.

Contoh:

```text
transaction.create
transaction.edit
transaction.delete
report.export
budget.manage
asset.manage
zakat.manage
settings.manage
```

---

# 34. Backup & Restore

Data keuangan adalah data kritis.

Fitur:

### Backup

* Backup manual
* Backup otomatis
* Export database
* Backup terenkripsi

### Restore

```text
Pilih Backup
↓
Verifikasi
↓
Preview
↓
Restore
```

Sebelum restore:

> **Buat backup kondisi saat ini terlebih dahulu.**

Ini mencegah kehilangan data akibat restore yang salah.

---

# 35. Keamanan

Minimal:

* PIN;
* biometrik;
* session timeout;
* enkripsi data sensitif;
* secure local storage;
* backup terenkripsi;
* permission-based access;
* audit trail;
* konfirmasi transaksi sensitif.

Untuk transaksi yang sudah finalized, sebaiknya tidak ada hard delete.

---

# 36. Offline-First

Untuk aplikasi masjid, saya sangat menyarankan **offline-first**.

Bendahara harus tetap dapat:

* melihat saldo;
* memasukkan transaksi;
* melihat laporan lokal;

ketika tidak ada internet.

Kemudian:

```text
Offline
↓
Data tersimpan lokal
↓
Internet tersedia
↓
Sync
↓
Server
```

Ini akan sangat berguna untuk masjid yang koneksi internetnya tidak selalu stabil.

---

# 37. Struktur Data Utama

Secara konseptual database memiliki entity:

```text
Mosque
User
Role
Permission

Account
    ├── Bank Account
    └── Cash Account

Fund
Category
Transaction
TransactionAttachment
Transfer

Asset
AssetCategory

ZakatAccount
ZakatTransaction
Mustahik

FundraisingTarget
Donation

Budget
BudgetCategory
BudgetItem
BudgetRealization

Report
AuditLog

Signature
MosqueLetterhead

Backup
SystemSetting
```

---

# 38. Konsep Fund / Dana

Ini salah satu bagian terpenting dari arsitektur.

Jangan hanya menggunakan:

```text
Pemasukan
Pengeluaran
```

Gunakan:

```text
Account
+
Fund
+
Category
+
Transaction
```

Contoh:

### Account

```text
Bank BSI
Cash Masjid
Bank Mandiri
```

### Fund

```text
Operasional
Pembangunan
Pendidikan
Sosial
Zakat
Renovasi
```

### Category

```text
Listrik
Air
Kebersihan
Honor
Material
Kegiatan
```

Dengan struktur ini:

> Rp100 juta di rekening tidak otomatis berarti Rp100 juta bebas digunakan untuk apa saja.

---

# 39. Transaction Lifecycle

Transaksi sebaiknya mempunyai status.

```text
Draft
 ↓
Recorded
 ↓
Reviewed
 ↓
Finalized
```

Untuk aplikasi sederhana MVP:

```text
Recorded
Finalized
```

Transaksi finalized tidak dihapus.

Jika ada kesalahan:

```text
Transaksi Salah
↓
Reversal / Correction
↓
Transaksi Baru
```

Ini menghasilkan histori yang lebih dapat diaudit.

---

# 40. Dashboard KPI

Dashboard utama dapat memiliki:

### Financial Summary

**Total Kas**

**Pemasukan Bulan Ini**

**Pengeluaran Bulan Ini**

**Net Cash Flow**

### Distribution

**Rekening vs Cash**

### Fund Allocation

**Alokasi Dana**

### Budget

**RAPBM vs Realisasi**

### Fundraising

**Target Dana**

### Recent Activity

**Transaksi Terbaru**

### Alerts

Contoh:

> ⚠️ Pengeluaran pemeliharaan telah mencapai 92% dari RAPBM.

> ℹ️ Target renovasi telah mencapai 80%.

> ⚠️ Rekonsiliasi Cash terakhir dilakukan 32 hari lalu.

---

# 41. Notification

Notifikasi tidak perlu terlalu banyak.

Gunakan untuk:

* transaksi membutuhkan review;
* anggaran mendekati batas;
* target hampir tercapai;
* backup berhasil;
* backup gagal;
* rekonsiliasi jatuh tempo;
* laporan periode belum dibuat.

---

# 42. Search & Filter

Global search sebaiknya dapat mencari:

```text
Nominal
Nama transaksi
Kategori
Tanggal
Nomor transaksi
Donatur
Target
Aset
```

Filter:

```text
Periode
Jenis
Kategori
Dana
Akun
Status
```

---

# 43. Onboarding

Saat pertama kali membuka aplikasi:

### Step 1

**Buat Profil Masjid**

### Step 2

**Tambahkan Rekening/Kas**

Contoh:

```text
Cash Masjid
Bank BSI
Bank Mandiri
```

### Step 3

**Atur Dana**

```text
Operasional
Pembangunan
Sosial
Pendidikan
```

### Step 4

**Saldo Awal**

```text
Cash       Rp...
Bank BSI   Rp...
```

### Step 5

**Tambahkan Pengurus**

### Step 6

**Selesai**

---

# 44. UX Prinsip Input Transaksi

Input transaksi harus sangat cepat.

Tombol besar:

```text
+ Pemasukan
- Pengeluaran
↔ Transfer
```

Jangan membuat bendahara melewati terlalu banyak form.

Flow:

```text
Tambah Pengeluaran
        ↓
Nominal
        ↓
Kategori
        ↓
Akun
        ↓
Dana
        ↓
Catatan/Bukti
        ↓
Simpan
```

Field advanced dapat ditempatkan dalam:

**Detail Lanjutan**

---

# 45. Receipt / Bukti Transaksi

Setiap transaksi dapat memiliki:

* foto nota;
* PDF;
* dokumen;
* screenshot transfer.

Contoh:

```text
Pengeluaran #EXP-000321

Rp750.000
Pembelian Karpet

📎 nota.jpg
```

Ini sangat berguna ketika dilakukan audit.

---

# 46. Nomor Transaksi Otomatis

Sistem menghasilkan ID:

```text
INC-202609-00001
EXP-202609-00001
TRF-202609-00001
AST-2026-00001
ZKT-202609-00001
```

Nomor ini tampil dalam laporan.

---

# 47. Reporting Period

Laporan harus mendukung:

* harian;
* mingguan;
* bulanan;
* triwulanan;
* semester;
* tahunan;
* custom date range.

Selain itu, RAPBM sebaiknya memiliki:

```text
Tahun Anggaran
```

---

# 48. MVP

Agar proyek tidak terlalu besar sejak awal, saya sangat menyarankan membaginya.

## MVP Version 1

### Core

* Onboarding masjid
* Profil masjid
* Cash account
* Bank account
* Saldo awal
* Pemasukan
* Pengeluaran
* Transfer
* Kategori
* Dashboard
* Riwayat transaksi
* Laporan arus kas
* Export PDF
* Korp surat
* Tanda tangan
* Backup
* PIN/Biometric

---

# 49. Version 2

Tambahkan:

* RAPBM;
* Budget vs Actual;
* Target Dana;
* Donasi;
* Aset;
* Audit trail;
* User roles;
* Rekonsiliasi.

---

# 50. Version 3

Tambahkan:

* Zakat;
* multi-user;
* cloud sync;
* approval workflow;
* advanced audit;
* scheduled reports;
* dashboard Ketua DKM;
* statistik historis;
* attachment management.

---

# 51. Future Version

Potensi pengembangan:

### Public Transparency

Masjid dapat memilih laporan tertentu untuk dipublikasikan kepada jamaah.

Contoh:

```text
Laporan Keuangan Masjid
September 2026

Saldo Awal       Rp...
Pemasukan        Rp...
Pengeluaran      Rp...
Saldo Akhir      Rp...
```

Tanpa memperlihatkan informasi sensitif.

### QRIS Integration

Donasi melalui QRIS kemudian secara otomatis masuk ke sistem.

### Bank Integration

Jika secara teknis dan legal memungkinkan, transaksi bank dapat diimpor.

### Donor Portal

Donatur dapat melihat:

```text
Donasi Anda
Target Dana
Progress
Laporan Publik
```

### Multi-Masjid

Satu akun pengelola dapat mengelola beberapa masjid.

---

# 52. Non-Functional Requirements

## Performance

Target:

* dashboard < 2 detik pada data lokal normal;
* transaksi tersimpan < 1 detik;
* aplikasi tetap dapat digunakan offline.

## Reliability

Aplikasi harus mencegah:

* saldo negatif akibat race condition;
* duplikasi transaksi;
* kehilangan transaksi;
* double submission.

## Security

* encrypted local storage;
* secure authentication;
* encrypted backup;
* permission control;
* audit logging.

## Usability

Target pengguna:

> Bendahara yang tidak memiliki latar belakang akuntansi.

Karena itu istilah harus menggunakan bahasa yang mudah dipahami.

---

# 53. Prinsip Terminologi

Hindari istilah yang terlalu teknis.

Misalnya:

❌ `Ledger Account`

Gunakan:

✅ **Rekening / Kas**

---

❌ `Journal Entry`

Gunakan:

✅ **Transaksi**

---

❌ `Actual Expenditure`

Gunakan:

✅ **Realisasi Pengeluaran**

---

❌ `Restricted Fund`

Gunakan:

✅ **Dana Khusus**

Tetapi di dokumentasi teknis database tetap dapat menggunakan istilah accounting/finance yang standar.

---

# 54. Contoh Dashboard Final

Secara UX saya membayangkan halaman Beranda seperti:

```text
┌──────────────────────────────┐
│ Masjid Al-Ikhlas             │
│ Selasa, 22 September 2026    │
├──────────────────────────────┤
│                              │
│ TOTAL KAS                    │
│ Rp125.450.000                │
│                              │
│ ↑ Rp14.000.000 bulan ini     │
├──────────────────────────────┤
│ Rekening          Cash       │
│ Rp100 jt          Rp25,4 jt  │
│        [DONUT]               │
├──────────────────────────────┤
│ STATUS KEUANGAN              │
│ Stabil                       │
│                              │
│ Pemasukan     Rp32 jt        │
│ Pengeluaran   Rp18 jt        │
│ Net Cash Flow +Rp14 jt       │
├──────────────────────────────┤
│ ALOKASI DANA                 │
│ [Pie Chart]                  │
├──────────────────────────────┤
│ TARGET DANA                  │
│ Renovasi Masjid              │
│ Rp175 jt / Rp250 jt          │
│ ██████████████░░ 70%         │
├──────────────────────────────┤
│ RAPBM                        │
│ Rencana vs Realisasi         │
│ [Chart]                      │
├──────────────────────────────┤
│ TRANSAKSI TERBARU            │
│ + Rp2,5 jt  Infaq Jumat      │
│ - Rp750 rb  Kebersihan       │
│ - Rp1,2 jt  Listrik          │
└──────────────────────────────┘
```

---

# 55. Core User Journey

### Bendahara mencatat pemasukan

```text
Beranda
 ↓
+ Pemasukan
 ↓
Nominal
 ↓
Kategori
 ↓
Akun
 ↓
Dana
 ↓
Simpan
 ↓
Saldo otomatis berubah
 ↓
Dashboard diperbarui
 ↓
Laporan diperbarui
```

### Bendahara membuat laporan

```text
Laporan
 ↓
Pilih Laporan
 ↓
Pilih Periode
 ↓
Preview
 ↓
Audit Check
 ↓
Export PDF
 ↓
Korp Surat
 ↓
Tanda Tangan
 ↓
PDF
```

---

# 56. Success Metrics

Keberhasilan aplikasi dapat diukur dari:

### Adoption

* jumlah masjid yang menggunakan aplikasi;
* jumlah pengguna aktif;
* jumlah transaksi yang dicatat.

### Engagement

* transaksi per bulan;
* laporan yang dibuat;
* penggunaan RAPBM;
* penggunaan target dana.

### Data Quality

* transaksi tanpa kategori;
* transaksi yang dikoreksi;
* rekonsiliasi yang dilakukan.

### Reliability

* backup berhasil;
* data loss;
* sync failure;
* crash rate.

---

# 57. Product Positioning

Saya menyarankan positioning produk bukan:

> **“Aplikasi Kas Masjid.”**

Karena terlalu sempit.

Lebih kuat:

> **“Sistem Perbendaharaan dan Pelaporan Keuangan Masjid.”**

Karena produk Anda mencakup:

**Kas + Dana + Aset + Zakat + RAPBM + Donasi + Laporan + Audit**

---

# 58. Konsep Produk yang Saya Rekomendasikan

Pada akhirnya, arsitektur produk sebaiknya dibangun dengan model:

```text
                 MASJID
                    │
        ┌───────────┴───────────┐
        │                       │
      DANA                    ASET
        │                       │
 ┌──────┼──────┐                │
 │      │      │                │
Kas  Bank  Dana Khusus       Inventaris
 │      │      │
 └──────┴──────┘
        │
    TRANSAKSI
        │
 ┌──────┼───────────┐
 │      │           │
Infaq  Donasi    Zakat
 │
 └──────────────┐
                │
             RAPBM
                │
        ┌───────┴───────┐
        │               │
      Rencana        Realisasi
        │               │
        └───────┬───────┘
                │
             LAPORAN
                │
        ┌───────┼────────┐
        │       │        │
       PDF    Audit    Dashboard
```

Dengan struktur tersebut, aplikasi tidak sekadar menjadi **“buku kas digital”**, tetapi menjadi sistem yang menghubungkan **uang → sumber dana → peruntukan → transaksi → anggaran → aset → laporan → audit**.

### Prioritas yang menurut saya paling penting

Jika ini benar-benar akan Anda develop, saya akan menetapkan **5 fondasi utama** sejak awal:

1. **Account/Kas** — uang berada di mana?
2. **Fund/Dana** — uang tersebut diperuntukkan untuk apa?
3. **Transaction** — uang masuk/keluar karena apa?
4. **Budget/RAPBM** — uang tersebut direncanakan untuk apa?
5. **Audit Trail** — siapa melakukan perubahan terhadap data?

Kalau kelima fondasi ini benar sejak database dan UX pertama kali dibuat, fitur **Dashboard, Chart, Laporan PDF, Target Dana, RAPBM, dan Audit** nantinya akan jauh lebih mudah dibangun tanpa harus mengubah struktur data di tengah development.
