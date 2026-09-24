# UI.md --- Framework UI Aplikasi Perbendaharaan Masjid

> **Status:** UI Framework / Design Specification\
> **Platform:** Android\
> **Product:** Perbendaharaan Masjid\
> **Visual Direction:** Elegant, modern, warm, approachable,
> trustworthy, rounded dashboard UI\
> **Primary Reference:** Uploaded visual reference --- modern
> educational dashboard with rounded cards, soft illustrated surfaces,
> floating navigation, large typography, pill controls, and playful but
> controlled visual accents.

------------------------------------------------------------------------

## 1. Design Direction

Aplikasi harus terasa seperti **aplikasi finansial modern yang mudah
digunakan**, bukan aplikasi administrasi pemerintahan yang kaku dan
bukan pula aplikasi akuntansi yang terlalu kompleks.

### Karakter visual

-   Elegant
-   Clean
-   Trustworthy
-   Friendly
-   Modern
-   Soft
-   Rounded
-   Structured
-   Islamic/Masjid context secara halus
-   Tidak menggunakan ornamen Islami secara berlebihan
-   Tidak menggunakan gradient sebagai dekorasi utama secara berlebihan
-   Informasi keuangan tetap menjadi fokus utama

### Prinsip visual utama

1.  **Large rounded cards**
2.  **Floating bottom navigation**
3.  **Strong visual hierarchy**
4.  **Large numerical KPI**
5.  **Soft secondary surfaces**
6.  **Primary blue sebagai identitas dan navigasi**
7.  **Hijau muda sebagai secondary/supportive surface**
8.  **Kuning sebagai accent untuk highlight**
9.  **Icon-first action**
10. **Whitespace yang cukup**
11. **Illustrative/decorative shapes digunakan sebagai aksen, bukan
    sebagai pengganggu data**
12. **Light dan dark mode harus menggunakan sistem warna yang sama
    secara konseptual**

------------------------------------------------------------------------

# 2. Identifikasi Referensi Visual

Referensi yang diberikan menunjukkan pola desain:

### A. Modern Educational Dashboard UI

Ciri:

-   dashboard berbasis card;
-   rounded container;
-   large numerical information;
-   section title yang tegas;
-   floating bottom navigation;
-   avatar/icon kecil di topbar;
-   pill-shaped selector;
-   chart yang sederhana;
-   ilustrasi dekoratif;
-   warna pastel dengan satu warna utama;
-   penggunaan whitespace yang luas.

### B. Soft Rounded App Design

Elemen yang perlu diadaptasi:

-   radius besar;
-   card dengan border halus;
-   elevated/floating navigation;
-   segmented control berbentuk pill;
-   icon circular container;
-   rounded CTA;
-   visual hierarchy yang jelas.

### C. Yang Tidak Perlu Ditiru Secara Literal

Referensi memiliki karakter playful dan ilustratif. Untuk aplikasi
keuangan masjid, gaya tersebut perlu **diturunkan tingkat playful-nya**.

Jangan menggunakan:

-   ilustrasi besar pada setiap card;
-   terlalu banyak warna;
-   terlalu banyak bentuk dekoratif;
-   typography yang terlalu eksperimental;
-   efek 3D berlebihan.

Target akhirnya:

> **70% modern finance dashboard + 20% soft educational UI + 10%
> Islamic/Mosque visual identity.**

------------------------------------------------------------------------

# 3. Color System

## 3.1. Brand Palette

### Primary --- 60%

``` text
Primary 900     #062E6B
Primary 800     #073B83
Primary 700     #084396
Primary 600     #0D47A1  ← Brand Primary
Primary 500     #2466B8
Primary 400     #4D83C5
Primary 300     #7FA5D5
Primary 200     #B5CAE7
Primary 100     #DCE8F6
```

Primary digunakan untuk:

-   Topbar accent;
-   navigation active state;
-   primary CTA;
-   important KPI;
-   selected state;
-   links;
-   active icons;
-   progress indicator;
-   chart utama;
-   focus state.

**Base brand color wajib tetap `#0D47A1`.**

------------------------------------------------------------------------

## 3.2. Secondary --- 30%

``` text
Secondary 900  #17351F
Secondary 800  #1D4828
Secondary 700  #245B31
Secondary 600  #2E6F3B
Secondary 500  #438650
Secondary 300  #A9D0B0
Secondary 200  #CBE5CF
Secondary 100  #E8F5E9  ← Base Secondary
```

`#E8F5E9` digunakan terutama sebagai:

-   supportive card;
-   background section;
-   positive state;
-   dana/target section;
-   empty state;
-   secondary button;
-   subtle financial growth indicator.

------------------------------------------------------------------------

## 3.3. Accent --- 10%

``` text
Accent 700      #8A5B00
Accent 600      #B57900
Accent 500      #D89A19
Accent 400      #F9B637  ← Brand Accent
Accent 300      #FAC75F
Accent 200      #FBDD91
Accent 100      #FEF1CC
```

Accent digunakan secara terbatas untuk:

-   warning ringan;
-   highlight;
-   target;
-   reward/achievement;
-   important attention point;
-   selected data point;
-   chart highlight.

Jangan menggunakan accent sebagai background mayoritas layar.

------------------------------------------------------------------------

# 4. Semantic Colors

Warna semantic tetap mengikuti sistem brand.

``` text
Success:
Light  #2E7D32
Surface #E8F5E9

Warning:
Light  #B7791F
Surface #FEF1CC

Error:
Light  #C62828
Surface #FDECEC

Info:
Light  #0D47A1
Surface #E3EEF9
```

### Rule

Semantic color tidak boleh menggantikan brand palette.

Contoh:

-   Primary Blue = identitas dan action
-   Green = kondisi positif / dana bertumbuh
-   Yellow = perhatian / target
-   Red = error / transaksi bermasalah

------------------------------------------------------------------------

# 5. Light Mode

## Base

``` text
Background       #F8FAFC
Surface          #FFFFFF
Surface Soft     #F1F5F9
Primary          #0D47A1
Secondary        #E8F5E9
Accent           #F9B637
Text Primary     #102033
Text Secondary   #526273
Text Tertiary    #7B8794
Border           #E3E8EF
```

### Light mode principle

Jangan membuat semua card berwarna putih murni tanpa struktur.

Gunakan:

``` text
Background
↓
White Card
↓
Soft Secondary Card
↓
Primary Highlight Card
```

------------------------------------------------------------------------

# 6. Dark Mode

Dark mode tidak boleh sekadar membalik warna.

``` text
Background       #08111F
Surface          #101C2B
Surface Elevated #16263A
Primary          #4D83C5
Primary Strong   #0D47A1
Secondary        #17351F
Accent           #F9B637
Text Primary     #F4F7FA
Text Secondary   #B6C2CF
Text Tertiary    #8290A0
Border           #243447
```

### Dark mode principle

Primary `#0D47A1` tetap menjadi identitas brand, tetapi untuk text/icon
di dark surface gunakan tonal primary yang lebih terang agar tetap
terbaca.

------------------------------------------------------------------------

# 7. Typography

## Recommended Font

### Primary

**Plus Jakarta Sans**

Alasan:

-   modern;
-   rounded;
-   profesional;
-   cocok untuk dashboard;
-   memiliki hierarchy yang kuat;
-   tetap nyaman untuk angka dan data.

### Fallback

``` text
Inter
Roboto
sans-serif
```

------------------------------------------------------------------------

# 8. Typography Hierarchy

## Display / Total Kas

``` text
Size: 30–36sp
Weight: Bold / 700
Line Height: 110–120%
```

Contoh:

> Rp125.450.000

------------------------------------------------------------------------

## H1

``` text
Size: 24sp
Weight: 700
Line Height: 120%
```

Contoh:

> Keuangan Masjid

------------------------------------------------------------------------

## H2

``` text
Size: 20sp
Weight: 700
Line Height: 125%
```

Contoh:

> Target Dana

------------------------------------------------------------------------

## H3

``` text
Size: 17–18sp
Weight: 600
Line Height: 130%
```

------------------------------------------------------------------------

## Body

``` text
Size: 15–16sp
Weight: 400
Line Height: 145–155%
```

------------------------------------------------------------------------

## Body Medium

``` text
Size: 14–15sp
Weight: 500
```

------------------------------------------------------------------------

## Caption

``` text
Size: 12–13sp
Weight: 400–500
```

------------------------------------------------------------------------

## Overline / Label

``` text
Size: 11–12sp
Weight: 600
Letter Spacing: 0.4–0.8sp
```

Gunakan untuk:

-   jenis transaksi;
-   kategori;
-   metadata;
-   periode;
-   status.

------------------------------------------------------------------------

# 9. Typography Rule

Urutan visual:

``` text
DISPLAY
↓
H1
↓
H2
↓
H3
↓
Body
↓
Caption
↓
Metadata
```

Jangan menggunakan lebih dari **3 ukuran font utama dalam satu card**.

------------------------------------------------------------------------

# 10. Spacing System

Gunakan basis **4dp**.

``` text
4dp   XS
8dp   SM
12dp  MD-S
16dp  MD
20dp  LG
24dp  XL
32dp  2XL
40dp  3XL
48dp  4XL
64dp  Section
```

### Default screen padding

``` text
16dp
```

Untuk layar tablet:

``` text
24–32dp
```

------------------------------------------------------------------------

# 11. Border Radius

Gunakan radius yang konsisten.

``` text
Small component       10dp
Input                 12dp
Button                14dp
Card                  20dp
Large Card            24dp
Floating Navbar       28dp
Bottom Sheet          28dp
Hero Card             28dp
Pill                  999dp
```

### Default Card

**20dp**

Ini menjadi radius standar utama aplikasi.

------------------------------------------------------------------------

# 12. Elevation & Shadow

Gunakan shadow secara minimal.

### Level 0

Flat surface.

### Level 1

Card:

``` text
Elevation: 1–2dp
```

### Level 2

Floating navbar / floating action:

``` text
Elevation: 4–8dp
```

### Level 3

Modal / Bottom Sheet:

``` text
Elevation: 8–16dp
```

Hindari shadow yang terlalu gelap.

Pada dark mode, gunakan:

-   tonal surface;
-   border;
-   sedikit elevation;

daripada shadow hitam.

------------------------------------------------------------------------

# 13. Standard Icon Size

``` text
12dp   Tiny / metadata
16dp   Inline icon
20dp   Small action
24dp   Standard icon
28dp   Navigation emphasis
32dp   Feature icon
40dp   Large feature icon
48dp   Hero icon
```

### Default

**24dp**

------------------------------------------------------------------------

# 14. Touch Target

Walaupun icon hanya 24dp, area klik harus:

``` text
Minimum: 48 × 48dp
Recommended: 48 × 48dp
```

Untuk primary action:

``` text
52–56dp
```

Jangan membuat icon button hanya berukuran 24 × 24dp secara keseluruhan.

------------------------------------------------------------------------

# 15. Button Framework

## Primary Button

``` text
Height: 48–52dp
Horizontal Padding: 20dp
Radius: 14dp
Background: Primary
Text: White
```

Digunakan untuk:

-   Simpan;
-   Buat;
-   Konfirmasi;
-   Export;
-   Tambah Data.

------------------------------------------------------------------------

## Secondary Button

``` text
Height: 48–52dp
Background: Secondary
Text: Primary
Radius: 14dp
```

------------------------------------------------------------------------

## Outline Button

``` text
Height: 48dp
Border: 1dp Primary
Background: Transparent
Radius: 14dp
```

------------------------------------------------------------------------

## Icon Button

Untuk perintah yang sudah jelas secara universal:

-   tambah;
-   edit;
-   hapus;
-   close;
-   back;
-   search;
-   notification;
-   filter;
-   more;
-   refresh.

``` text
Container: 48 × 48dp
Icon: 24dp
```

### Rule

Jangan menambahkan teks jika icon sudah cukup jelas.

Contoh:

``` text
✓  Simpan
```

boleh menggunakan text karena aksi tidak selalu universal.

Sedangkan:

``` text
✎
```

cukup untuk Edit.

------------------------------------------------------------------------

# 16. Topbar

Topbar selalu berada di bagian atas halaman utama.

## Structure

``` text
┌─────────────────────────────────────┐
│ Logo  Nama Masjid            🔔     │
│       Alamat Masjid                 │
└─────────────────────────────────────┘
```

### Height

``` text
64–72dp
```

### Left

Logo aplikasi / logo masjid.

Ukuran:

``` text
40 × 40dp
```

### Middle

Nama Masjid:

``` text
16sp / 600
```

Alamat:

``` text
12sp / 400
```

### Right

Notification:

``` text
48 × 48dp touch target
24dp icon
```

------------------------------------------------------------------------

# 17. Topbar Behavior

Topbar digunakan pada:

-   Beranda;
-   Bendahara;
-   Laporan;
-   Pengaturan.

Tetapi **tidak wajib ditampilkan pada halaman fitur yang terisolasi**.

------------------------------------------------------------------------

# 18. Floating Navbar

Navigasi utama menggunakan floating bottom navigation seperti referensi.

## Items

``` text
Beranda
Bendahara
Laporan
Pengaturan
```

### Dimensions

``` text
Height: 68–76dp
Margin Horizontal: 16dp
Margin Bottom: 12–16dp
Radius: 28dp
```

### Surface

Light:

``` text
#FFFFFF
```

Dark:

``` text
#101C2B
```

### Shadow

``` text
4–8dp
```

------------------------------------------------------------------------

# 19. Navbar Active State

Gunakan pill/capsule.

``` text
Active:
Primary surface
White icon
```

Inactive:

``` text
Transparent
Secondary text
```

Contoh:

``` text
┌──────────────────────────────────────────┐
│  🏠     ▣     ▤     ⚙                    │
│ [Home]                                  │
└──────────────────────────────────────────┘
```

Active state tidak perlu menggunakan label untuk semua item jika icon
sudah jelas.

Namun pada layar utama, label dapat muncul pada item aktif untuk
meningkatkan discoverability.

------------------------------------------------------------------------

# 20. Beranda

Beranda adalah **financial overview**, bukan halaman untuk memasukkan
banyak data.

Urutan:

``` text
Topbar
↓
Total Kas
↓
Rekening vs Cash
↓
Alokasi Dana
↓
Status Keuangan
↓
Target Dana
↓
Transaksi Terbaru
↓
Floating Navbar
```

------------------------------------------------------------------------

# 21. Hero Card --- Total Kas

Card paling dominan.

``` text
┌───────────────────────────────────┐
│ TOTAL KAS                         │
│                                   │
│ Rp125.450.000                     │
│                                   │
│ +Rp14.000.000 bulan ini           │
│                                   │
│                    ↗              │
└───────────────────────────────────┘
```

### Dimensions

``` text
Width: Full available width
Min Height: 160dp
Radius: 24dp
Padding: 20–24dp
```

Background:

Primary Blue.

Gunakan dekorasi abstrak sangat halus di belakang angka.

------------------------------------------------------------------------

# 22. Card Rekening vs Cash

Gunakan horizontal card atau split card.

``` text
┌───────────────┬───────────────┐
│ REKENING      │ CASH          │
│ Rp100 jt      │ Rp25,4 jt     │
│     80%       │      20%      │
└───────────────┴───────────────┘
```

Tambahkan mini donut/bar jika ruang mencukupi.

------------------------------------------------------------------------

# 23. Chart Pie Alokasi Dana

Card:

``` text
┌──────────────────────────────────┐
│ Alokasi Dana               ⋯     │
│                                  │
│          ◯                       │
│       ◯     ◯                    │
│                                  │
│ Operasional     40%              │
│ Pembangunan     25%              │
│ Sosial          15%              │
│ Pendidikan      10%              │
└──────────────────────────────────┘
```

### Rule

Pie chart maksimal:

**5 kategori utama**

Jika lebih dari 5:

> Others / Lainnya

------------------------------------------------------------------------

# 24. Status Keuangan

Gunakan card dengan status + data pendukung.

``` text
┌──────────────────────────────────┐
│ STATUS KEUANGAN                  │
│                                  │
│ ● Stabil                         │
│                                  │
│ Pemasukan        Rp32 jt         │
│ Pengeluaran      Rp18 jt         │
│ Net Cash Flow    +Rp14 jt        │
└──────────────────────────────────┘
```

Status tidak boleh hanya berdasarkan satu angka.

------------------------------------------------------------------------

# 25. Progress Target Dana

Card menggunakan progress bar.

``` text
┌──────────────────────────────────┐
│ Target Dana                      │
│ Renovasi Masjid                  │
│                                  │
│ Rp175 jt / Rp250 jt              │
│ ███████████████░░░░ 70%          │
│                                  │
│ 34 hari tersisa                  │
└──────────────────────────────────┘
```

Progress:

``` text
Height: 8–10dp
Radius: 999dp
```

Accent `#F9B637` dapat digunakan sebagai highlight target.

------------------------------------------------------------------------

# 26. Bendahara

Halaman Bendahara bukan langsung menampilkan seluruh data.

Gunakan **Feature Menu Dashboard**.

``` text
Bendahara

┌─────────────────┐ ┌─────────────────┐
│ ↕               │ │ ▣               │
│ Arus Kas        │ │ Aset Masjid     │
│ Pemasukan &     │ │ Inventaris      │
│ Pengeluaran     │ │                 │
└─────────────────┘ └─────────────────┘

┌─────────────────┐ ┌─────────────────┐
│ ◉               │ │ 🎯              │
│ Zakat           │ │ Target & Donasi │
│                 │ │                 │
└─────────────────┘ └─────────────────┘

┌──────────────────────────────────────┐
│ RAPBM                                │
│ Rencana vs Realisasi            →   │
└──────────────────────────────────────┘
```

------------------------------------------------------------------------

# 27. Bendahara --- Feature Card

### Standard

``` text
Width:
50% minus spacing

Height:
140–160dp

Radius:
20dp

Padding:
16dp
```

### Content

``` text
Icon
Title
Short description
Arrow
```

Jangan memasukkan terlalu banyak informasi.

------------------------------------------------------------------------

# 28. Isolated Feature Page

Ketika user memilih:

-   Arus Kas;
-   Aset;
-   Zakat;
-   Target Dana;
-   RAPBM;

halaman fitur harus **terisolasi dari Topbar dan Floating Navbar
utama**.

Layout:

``` text
┌──────────────────────────────────┐
│ ←   Arus Kas                 ⋯   │
├──────────────────────────────────┤
│                                  │
│ Content                          │
│                                  │
└──────────────────────────────────┘
```

Navigation menggunakan:

**Back Button + Contextual Topbar**

Tidak menggunakan floating navbar utama.

------------------------------------------------------------------------

# 29. Arus Kas

Header:

``` text
← Arus Kas
```

Segmented filter:

``` text
[ Semua ] [ Masuk ] [ Keluar ]
```

Summary:

``` text
Pemasukan
Rp32.000.000

Pengeluaran
Rp18.000.000
```

List transaksi:

``` text
┌──────────────────────────────────┐
│ +  Infaq Jumat             22 Sep│
│    Operasional             +2,5jt│
└──────────────────────────────────┘
```

FAB:

``` text
+
```

FAB dapat membuka:

``` text
+ Pemasukan
- Pengeluaran
↔ Transfer
```

------------------------------------------------------------------------

# 30. Aset Masjid

Gunakan card list.

``` text
┌──────────────────────────────────┐
│ [Foto]  AC Ruang Utama           │
│         Elektronik               │
│         Rp8.500.000              │
│         Kondisi: Baik            │
└──────────────────────────────────┘
```

Filter:

``` text
Semua | Baik | Perbaikan | Rusak
```

------------------------------------------------------------------------

# 31. Zakat

Gunakan dashboard khusus.

``` text
Zakat

Diterima
Rp50 jt

Disalurkan
Rp42 jt

Saldo
Rp8 jt
```

Tabs:

``` text
Penerimaan | Penyaluran | Mustahik
```

Zakat harus memiliki visual identity yang tenang dan tidak dicampur
secara visual dengan transaksi operasional.

------------------------------------------------------------------------

# 32. Target Dana & Donasi

Header:

``` text
Target Dana
```

Card:

``` text
Renovasi Masjid
Rp175 jt / Rp250 jt
██████████████░░ 70%
```

Donasi dapat ditampilkan:

``` text
Donasi Terbaru
+ Rp500.000
+ Rp1.000.000
+ Rp250.000
```

------------------------------------------------------------------------

# 33. RAPBM

RAPBM harus menggunakan tampilan yang lebih data-oriented.

Header:

``` text
RAPBM 2027
```

Tabs:

``` text
Pendapatan | Belanja | Rencana vs Realisasi
```

Card summary:

``` text
Total Pendapatan
Rp450 jt

Total Belanja
Rp380 jt

Surplus Rencana
Rp70 jt
```

------------------------------------------------------------------------

# 34. Chart Rencana vs Realisasi

Gunakan grouped bar chart.

``` text
Pendapatan
Rencana  ███████████████
Realita  ████████████

Belanja
Rencana  █████████████
Realita  ███████████████
```

### Chart color

Rencana:

Primary light

Realisasi:

Primary strong / Accent

Jangan menggunakan terlalu banyak warna.

------------------------------------------------------------------------

# 35. Laporan

Laporan merupakan pusat dokumentasi.

Struktur:

``` text
Laporan
│
├── Ringkasan
├── Arus Kas
├── Transaksi
├── RAPBM
├── Aset
├── Zakat
├── Target Dana
└── Audit
```

Gunakan list card, bukan dashboard yang terlalu padat.

------------------------------------------------------------------------

# 36. Laporan --- Filter

Filter bar:

``` text
September 2026       ↓
```

Secondary filter:

``` text
Semua | Pemasukan | Pengeluaran
```

Filter icon:

``` text
[⚲]
```

------------------------------------------------------------------------

# 37. Audit Laporan

Gunakan visual yang lebih formal.

``` text
Audit Laporan

✓ Data lengkap
✓ Saldo konsisten
✓ Tidak ada transaksi tanpa kategori
✓ Rekonsiliasi terakhir: 20 Sep 2026
```

Audit trail:

``` text
22 Sep 14:30
Bendahara
Mengubah transaksi EXP-000123

Sebelum
Rp500.000

Sesudah
Rp550.000
```

------------------------------------------------------------------------

# 38. Export PDF

Flow:

``` text
Laporan
↓
Export
↓
Jenis Laporan
↓
Periode
↓
Preview
↓
Export PDF
```

PDF menggunakan:

### Header / Korp Surat

``` text
[LOGO]

MASJID __________
Alamat __________
Kontak __________
```

### Body

Laporan.

### Footer

Dua kolom tanda tangan:

``` text
Mengetahui,                         Bendahara,

Ketua DKM                           Bendahara


(________________)                  (________________)
Nama                                Nama
```

------------------------------------------------------------------------

# 39. Pengaturan

Struktur:

``` text
Pengaturan

Profil Masjid
Pengguna & Hak Akses
Kategori & Dana
Backup & Keamanan
Preferensi
Sistem
Tentang Aplikasi
```

------------------------------------------------------------------------

# 40. Profil Masjid

Card:

``` text
[Logo Masjid]

Nama Masjid
Alamat
Ketua DKM
Bendahara
```

Action:

``` text
✎
```

Data ini menjadi sumber untuk:

-   Topbar;
-   laporan;
-   korp surat;
-   PDF;
-   identitas aplikasi.

------------------------------------------------------------------------

# 41. Backup & Keamanan

``` text
Keamanan
────────────────────
PIN
Biometrik
Auto Lock

Backup
────────────────────
Backup Sekarang
Backup Otomatis
Restore Data
```

Tindakan destruktif seperti Restore dan Reset Data harus menggunakan
confirmation dialog.

------------------------------------------------------------------------

# 42. Preferensi & Sistem

Preferensi:

``` text
Mode Tampilan
○ Sistem
○ Light
○ Dark

Format Mata Uang
Rp

Format Tanggal
DD/MM/YYYY

Notifikasi
ON/OFF
```

------------------------------------------------------------------------

# 43. Tentang Aplikasi

Minimal:

``` text
Logo
Nama Aplikasi
Versi

Tentang Perbendaharaan Masjid

Kebijakan Privasi
Ketentuan Penggunaan
Lisensi
Kontak Pengembang
```

------------------------------------------------------------------------

# 44. Card Specification

## Standard Card

``` text
Width:
Match Parent

Padding:
16dp

Radius:
20dp

Min Height:
96dp

Elevation:
1–2dp
```

## Hero Card

``` text
Radius:
24dp

Padding:
20–24dp

Min Height:
160dp
```

## Feature Card

``` text
Radius:
20dp

Padding:
16dp

Height:
140–160dp
```

## Compact Card

``` text
Radius:
16dp

Padding:
12–16dp

Min Height:
72dp
```

------------------------------------------------------------------------

# 45. Standard Screen Layout

``` text
┌─────────────────────────────┐
│          TOPBAR             │
├─────────────────────────────┤
│                             │
│      Screen Padding         │
│         16dp                │
│                             │
│       Content               │
│                             │
│                             │
│                             │
│                             │
│                             │
├─────────────────────────────┤
│    Floating Navigation      │
└─────────────────────────────┘
```

Content harus memiliki bottom padding tambahan agar tidak tertutup
floating navbar.

Recommended:

``` text
Bottom Content Padding:
100–120dp
```

------------------------------------------------------------------------

# 46. Grid System

Android phone:

``` text
Horizontal Padding: 16dp
Column Gap: 12dp
```

2-column:

``` text
┌──────────────┐  ┌──────────────┐
│              │  │              │
│    Card      │  │    Card      │
│              │  │              │
└──────────────┘  └──────────────┘
```

Gunakan 2-column untuk:

-   KPI;
-   Bendahara features;
-   small summary.

Gunakan full width untuk:

-   hero card;
-   charts;
-   reports;
-   tables;
-   long forms.

------------------------------------------------------------------------

# 47. Responsive Android

## Small Phone

``` text
320–359dp
```

-   single column;
-   horizontal scroll untuk chart/filter;
-   compact cards.

## Standard Phone

``` text
360–599dp
```

-   2-column cards;
-   full dashboard.

## Tablet

``` text
600dp+
```

-   2--3 column;
-   navigation dapat berubah menjadi navigation rail;
-   content max-width.

------------------------------------------------------------------------

# 48. Animation

Animation harus subtle.

### Screen transition

``` text
200–300ms
```

### Card interaction

``` text
150–200ms
```

### Progress

``` text
300–500ms
```

### Number counter

``` text
300–600ms
```

Hindari animasi dekoratif yang terus bergerak karena aplikasi
berhubungan dengan data keuangan.

------------------------------------------------------------------------

# 49. Interaction States

Semua component harus memiliki:

``` text
Default
Pressed
Focused
Selected
Disabled
Loading
Error
Success
```

Contoh Button:

``` text
Default     Primary
Pressed     Primary 900
Disabled    Primary 200
Loading     Spinner
```

------------------------------------------------------------------------

# 50. Loading State

Gunakan skeleton.

Jangan langsung menampilkan:

``` text
Rp0
```

jika data sebenarnya masih loading.

Gunakan:

``` text
████████████
```

untuk menjaga persepsi bahwa data sedang dimuat.

------------------------------------------------------------------------

# 51. Empty State

Contoh Arus Kas:

``` text
        [Illustration]

Belum Ada Transaksi

Mulai catat pemasukan atau
pengeluaran masjid.

             +
```

Illustration menggunakan gaya sederhana, bukan ilustrasi besar seperti
aplikasi pendidikan pada referensi.

------------------------------------------------------------------------

# 52. Error State

``` text
Terjadi masalah

Data belum dapat dimuat.
Periksa koneksi atau coba lagi.

        [ Coba Lagi ]
```

------------------------------------------------------------------------

# 53. Confirmation Dialog

Untuk:

-   hapus;
-   restore;
-   finalize;
-   reset;
-   tindakan irreversible.

Contoh:

``` text
Hapus Transaksi?

Transaksi ini akan dihapus dari
tampilan aktif dan tindakan ini
tidak dapat dibatalkan.

[Batal]     [Hapus]
```

Untuk transaksi finalized, gunakan mekanisme koreksi/reversal, bukan
hard delete.

------------------------------------------------------------------------

# 54. Iconography

Gunakan satu icon family secara konsisten.

Recommended style:

-   outlined;
-   rounded;
-   2px visual stroke;
-   minimal;
-   friendly;
-   bukan icon yang terlalu ornamental.

Contoh:

``` text
Home
Wallet
Receipt
Building
Mosque
Bank
Coins
Chart
File
Settings
Bell
Search
Filter
Plus
Minus
Edit
Delete
Arrow
```

Jangan mencampur:

-   Material Filled;
-   Font Awesome;
-   custom outline;
-   emoji;

dalam satu UI.

------------------------------------------------------------------------

# 55. Icon Button Rule

### Icon-only

Gunakan untuk:

``` text
← Back
+
−
✎
🗑
🔔
⋯
⌕
⚲
↗
↻
```

### Icon + Text

Gunakan jika:

-   action tidak universal;
-   membutuhkan konteks;
-   action bersifat consequential.

Contoh:

``` text
Export PDF
Simpan Perubahan
Buat RAPBM
Restore Data
```

------------------------------------------------------------------------

# 56. Financial Number Formatting

Gunakan:

``` text
Rp125.450.000
```

Bukan:

``` text
125450000 IDR
```

Untuk dashboard gunakan compact format jika angka terlalu panjang:

``` text
Rp125,4 jt
```

Tetapi laporan resmi harus menggunakan nominal lengkap.

------------------------------------------------------------------------

# 57. Financial Color Rule

### Pemasukan

Gunakan:

-   Primary;
-   Green semantic.

### Pengeluaran

Gunakan:

-   neutral dark;
-   red hanya jika membutuhkan warning/error.

Jangan menjadikan semua pengeluaran merah karena akan membuat dashboard
terlihat seperti halaman error.

### Target

Gunakan Accent Yellow.

### Dana Khusus

Gunakan Secondary Green.

------------------------------------------------------------------------

# 58. Data Visualization Rules

Charts harus:

-   simple;
-   readable;
-   memiliki label;
-   tidak menggunakan lebih dari 4--5 warna utama;
-   memiliki legend;
-   tidak bergantung hanya pada warna.

Gunakan juga:

-   icon;
-   label;
-   angka;
-   pattern bila diperlukan.

------------------------------------------------------------------------

# 59. Component Hierarchy

Prioritas visual:

``` text
1. Primary KPI
2. Primary Action
3. Important Financial Status
4. Chart / Visualization
5. Secondary Data
6. Metadata
7. Decorative Element
```

Dekorasi tidak boleh lebih dominan daripada data.

------------------------------------------------------------------------

# 60. Visual Density

Target:

> **Medium-low density**

Aplikasi harus memberikan ruang bernapas.

Rule:

-   satu card = satu tujuan utama;
-   satu section = satu pertanyaan;
-   satu screen = satu konteks;
-   jangan memasukkan seluruh laporan ke dashboard.

------------------------------------------------------------------------

# 61. Decorative Language

Mengadaptasi referensi, gunakan:

-   abstract rounded blob;
-   soft circle;
-   subtle arch;
-   thin curved line;
-   soft geometric shape.

Warna:

``` text
Primary 100
Secondary 100
Accent 100
```

Opacity:

``` text
10–25%
```

Dekorasi ditempatkan di:

-   hero card;
-   empty state;
-   onboarding;
-   section header.

Tidak digunakan pada tabel atau data audit.

------------------------------------------------------------------------

# 62. Islamic Visual Identity

Identitas masjid harus terasa melalui:

-   nama masjid;
-   logo;
-   typography;
-   warna;
-   foto/ilustrasi masjid jika diperlukan;
-   terminology.

Hindari memenuhi UI dengan:

-   bulan sabit;
-   kubah;
-   pattern Arab;
-   ornamen geometris;

secara berlebihan.

Tujuannya adalah:

> **Modern financial management for mosques**

bukan aplikasi dengan tema dekoratif Islami.

------------------------------------------------------------------------

# 63. Accessibility

Minimum:

``` text
Touch target ≥ 48dp
Body text ≥ 14sp
Primary body ≥ 16sp
Contrast harus memadai
```

Jangan menjadikan warna satu-satunya indikator.

Contoh:

``` text
↑ +Rp2.500.000
```

bukan hanya membuat angka hijau tanpa label.

------------------------------------------------------------------------

# 64. Main Navigation Model

``` text
                TOPBAR
                   │
       ┌───────────┼───────────┐
       │           │           │
    BERANDA    BENDAHARA    LAPORAN
       │           │           │
       │           └── Features
       │
       └────────────────────────────
                   │
          PENGATURAN
                   │
          FLOATING NAVBAR
```

------------------------------------------------------------------------

# 65. Navigation Rule

### Main Navigation

Persistent:

``` text
Beranda
Bendahara
Laporan
Pengaturan
```

### Secondary Navigation

Contextual:

``` text
Arus Kas
Aset
Zakat
Target Dana
RAPBM
```

### Feature Detail

Menggunakan:

``` text
Back
Contextual Header
```

Tanpa main floating navbar.

------------------------------------------------------------------------

# 66. Screen Hierarchy

## Level 1

``` text
Beranda
Bendahara
Laporan
Pengaturan
```

## Level 2

``` text
Bendahara
├── Arus Kas
├── Aset
├── Zakat
├── Target Dana
└── RAPBM
```

## Level 3

``` text
Arus Kas
├── Detail Transaksi
├── Tambah Pemasukan
├── Tambah Pengeluaran
└── Edit Transaksi
```

------------------------------------------------------------------------

# 67. Design Token Summary

``` text
PRIMARY
#0D47A1

SECONDARY
#E8F5E9

ACCENT
#F9B637

FONT
Plus Jakarta Sans

SCREEN PADDING
16dp

CARD RADIUS
20dp

HERO RADIUS
24dp

BUTTON HEIGHT
48–52dp

ICON
24dp

TOUCH TARGET
48dp

NAVBAR HEIGHT
68–76dp

NAVBAR RADIUS
28dp

TOPBAR
64–72dp
```

------------------------------------------------------------------------

# 68. Final Visual Formula

Aplikasi mengikuti formula:

``` text
60%  PRIMARY BLUE
     ↓
Trust + Navigation + Financial Focus

30%  SOFT GREEN
     ↓
Support + Positive Financial Context + Secondary Surface

10%  WARM YELLOW
     ↓
Attention + Target + Highlight
```

Dengan komposisi:

``` text
60% Structured
30% Soft
10% Highlight
```

dan bukan:

``` text
60% Blue everywhere
30% Green everywhere
10% Yellow everywhere
```

Persentase tersebut merupakan **visual weight**, bukan pembagian literal
setiap pixel layar.

------------------------------------------------------------------------

# 69. Final UI Character

Hasil akhir yang dituju:

> **Clean financial dashboard + rounded modern mobile UI + soft mosque
> identity.**

Visual harus terasa:

**Profesional** tetapi tidak kaku.\
**Modern** tetapi tidak futuristik berlebihan.\
**Islami** tetapi tidak ornamental.\
**Sederhana** tetapi bukan minim fitur.\
**Finansial** tetapi mudah digunakan oleh bendahara non-akuntansi.

Referensi visual yang diberikan terutama diambil pada aspek:

-   rounded card;
-   floating navigation;
-   large typography;
-   soft background;
-   pill selector;
-   dashboard hierarchy;
-   chart card;
-   playful subtle decoration;

sementara **struktur, warna, typography, dan visual density disesuaikan
khusus untuk aplikasi perbendaharaan masjid.**
