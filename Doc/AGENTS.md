# PROJECT: PERBENDAHARAAN MASJID

You are developing an Android application called "Perbendaharaan Masjid".

The application is a modern mosque treasury and financial management system.

PRODUCT PURPOSE:
Help mosque administrators manage financial transactions, funds, assets, zakat, fundraising targets, RAPBM, reporting, and financial accountability in a simple and structured way.

PRIMARY USERS:
1. Bendahara Masjid
2. Ketua DKM
3. Pengurus
4. Auditor/Internal Reviewer

CORE PRINCIPLE:

The application is NOT merely a digital cash book.

The financial architecture must connect:

Masjid
→ Account/Kas
→ Fund/Dana
→ Category
→ Transaction
→ Budget/RAPBM
→ Asset
→ Target/Donation
→ Report
→ Audit Trail

IMPORTANT FINANCIAL PRINCIPLES:

1. Bank accounts and physical cash must be separate accounts.
2. Funds/peruntukan must be separated conceptually from accounts.
3. Transfers between accounts must NOT be treated as income or expenses.
4. Zakat must be isolated from ordinary operational funds.
5. Transactions should not be hard-deleted after finalization.
6. Corrections should use reversal/correction mechanisms where appropriate.
7. Every important modification must be auditable.
8. Reports must be reproducible from transaction data.
9. Dashboard values must be derived from the financial data layer, not hardcoded.
10. Monetary calculations must avoid floating-point precision errors.

MAIN NAVIGATION:

Beranda
Bendahara
Laporan
Pengaturan

BENDahara FEATURES:

- Kelola Arus Kas
  - Pemasukan
  - Pengeluaran
  - Transfer Antar Kas
- Kelola Aset Masjid
- Kelola Zakat
- Kelola Target Dana & Donasi
- RAPBM
- Rencana vs Realisasi

REPORTING:

- Ringkasan Keuangan
- Riwayat Transaksi
- Arus Kas
- RAPBM
- Aset
- Zakat
- Target Dana
- Audit Trail
- PDF Export

SETTINGS:

- Profil Masjid
- Logo
- Nama Masjid
- Alamat
- Ketua DKM
- Bendahara
- Korp Surat
- Tanda Tangan
- Pengguna & Hak Akses
- Backup
- Restore
- Security
- Preferences
- System
- About

UI DIRECTION:

The visual direction is based on the provided UI reference.

Take inspiration from:
- modern educational dashboard composition
- rounded cards
- large KPI typography
- soft backgrounds
- floating bottom navigation
- pill controls
- spacious layouts
- subtle decorative shapes

DO NOT copy the reference literally.

The final application should feel:

Modern Financial Dashboard
+
Soft Rounded Mobile UI
+
Subtle Mosque Identity

COLOR SYSTEM:

Primary:
#0D47A1

Secondary:
#E8F5E9

Accent:
#F9B637

Visual weight:
60% Primary
30% Secondary
10% Accent

LIGHT MODE:

Background #F8FAFC
Surface #FFFFFF
Surface Soft #F1F5F9
Primary #0D47A1
Secondary #E8F5E9
Accent #F9B637
Text Primary #102033
Text Secondary #526273
Border #E3E8EF

DARK MODE:

Background #08111F
Surface #101C2B
Surface Elevated #16263A
Primary #4D83C5
Primary Strong #0D47A1
Secondary #17351F
Accent #F9B637
Text Primary #F4F7FA
Text Secondary #B6C2CF
Border #243447

TYPOGRAPHY:

Preferred:
Plus Jakarta Sans

Fallback:
Inter / Roboto / sans-serif

STANDARD DIMENSIONS:

Screen horizontal padding: 16dp
Card radius: 20dp
Hero card radius: 24dp
Button height: 48–52dp
Icon: 24dp
Minimum touch target: 48dp
Floating navbar height: 68–76dp
Floating navbar radius: 28dp
Topbar height: 64–72dp
Feature card height: 140–160dp

ICON RULE:

Use icon-only buttons when the action is universally understandable:

- Add
- Edit
- Delete
- Back
- Close
- Search
- Filter
- Notification
- More
- Refresh

Use text + icon for contextual actions such as:

- Export PDF
- Save Changes
- Create RAPBM
- Restore Data

NAVIGATION RULE:

Main navigation remains persistent only on:

- Beranda
- Bendahara
- Laporan
- Pengaturan

Feature detail pages such as:

- Arus Kas
- Aset
- Zakat
- Target Dana
- RAPBM

must use an isolated contextual header with Back navigation and MUST NOT display the main floating navbar.

SOURCE OF TRUTH:

The project contains:
1. PRD for product requirements.
2. UI.md for visual and UI framework requirements.

Read and follow those documents before implementing UI.

GENERAL DEVELOPMENT RULES:

- Inspect the existing project before modifying anything.
- Do not rewrite working code unnecessarily.
- Do not create duplicate architecture.
- Do not hardcode financial values.
- Do not hardcode dashboard statistics.
- Keep business logic separate from UI.
- Keep database/repository logic separate from presentation.
- Use reusable components.
- Use centralized design tokens.
- Preserve light/dark mode.
- Build incrementally.
- After every stage, compile/build/test the project.
- Fix errors before continuing.
- Do not implement future stages unless explicitly instructed.
- At the end of each stage, report:
  1. What was implemented.
  2. Files created/modified.
  3. Tests/build result.
  4. Known limitations.
  5. Recommended next stage.

IMPORTANT:
Do not ask unnecessary confirmation questions.
Make reasonable implementation decisions consistent with this specification.
If an architectural decision is required, choose the simplest production-ready approach and document it.