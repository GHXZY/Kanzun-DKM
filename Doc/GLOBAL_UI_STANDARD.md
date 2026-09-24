# GLOBAL UI STANDARD — NEOMORPHISM

Scope: presentation only. Preserve callbacks, state, models, calculations, persistence, routing, and dependencies. Reference image informs soft depth, not its low contrast or turquoise palette. Existing brand blue #0D47A1, green #E8F5E9 and amber #F9B637 remain.

## Audit before implementation
- Shared cards currently combine hard outlines with Material elevation; no unified raised/inset treatment.
- Buttons use 17sp title typography plus 16dp vertical padding; fixed 48/50dp callers can clip labels.
- ResponsiveContentContainer and navbar apply fill before width cap, preventing the cap from constraining wide screens.
- Navbar active label expands in a row of four unweighted items and can exceed small phone width.
- Dashboard paired KPIs reserve icon width beside long currency amounts.
- Forms use raw Material inputs, inconsistent radii, and fixed 50/56dp heights.
- Dialogs use 92% width, duplicated padding, different elevations, fixed height limits and inconsistent scrolling. Target modal has no scroll constraint. Asset/transaction details can exceed a short viewport.
- Budget/target text uses hardcoded dark blue/green, unsuitable for dark mode.
- Export sheet and profile dialog need scrolling for keyboard/large text.
- Filter and segmented controls need explicit selected semantics and 48dp touch targets.
- Audited surfaces: Beranda, Bendahara, Arus Kas, Aset, Zakat, Target Dana, RAPBM, Laporan, Pengaturan, Notifikasi, Audit, preview, shared states/charts, form/detail/confirmation dialogs.

## Global standards
Spacing: 4 micro; 8 icon/text; 12 compact gaps; 16 gutters/card/form padding; 20 popup padding; 24 section gaps; 32 major sections; 40 visual separation; 48 large separation. Existing named tokens remain.
Layout: mobile gutter 16dp, content cap 840dp, topbar minimum 72dp including 48dp controls, navbar 72dp minimum with 12dp outer vertical margin. FAB 56dp with Scaffold 16dp safe offsets. Main scroll content reserves 112dp for navbar; feature content 88dp for FAB.
Typography (sp size/line): display 30/36 bold; H1 24/32 bold; H2 20/28 bold; H3 18/24 semibold; body large 16/24 normal; body 15/24 normal; body small 14/20 normal; action/label 14/20 semibold; caption 12/16 medium. Plus Jakarta Sans with existing platform fallback. No fixed text box height.
Buttons: primary filled brand, secondary soft surface, outline minor; 48dp minimum, 12dp vertical/16dp horizontal padding, radius14, icon20. Icon button48/icon24. Labels may wrap rather than clip.
Cards: standard/interactive/summary radius20 padding16; compact padding12; featured radius24 padding20. Only interactive cards have click semantics. Avoid stacked shadows on nested passive surfaces.
Forms: minimum56dp inputs, radius12, 16dp text, gap16, multiline grows. Existing labels, errors, filtering, keyboard options and callbacks preserved.
Popup: viewport width minus32dp, cap560dp, max90% available height, safe system/keyboard insets, radius20, padding20, readable title and 48dp close. Long content scrolls; no forced full-screen layout.
Icons: standard24, compact20, touch48. Keep existing Material icons.
Accessibility: selected semantics for tabs/chips, native focus/ripple, theme contrast, no color-only status, respect large fonts.

## NEOMORPHISM DESIGN SYSTEM
Soft blue-gray light canvas and matching raised surfaces; dark navy tonal surfaces. Brand colors retained. Raised surfaces use low-opacity top-left highlight and bottom-right ambient depth. Inset controls reverse light direction with a subtle inner edge. Boundaries and focus indicators remain visible. Primary actions retain filled color. Shadows are cached drawing, not layout or data state. Material ripple provides press feedback; disabled controls retain native disabled semantics. No added animation dependency.

## Layout wireframes
Main: safe topbar -> primary summary -> section title/content -> subsequent sections -> scroll clearance -> floating navigation.
Feature: safe back/title -> summary -> filters -> list/chart -> bottom FAB clearance.
Form popup: inset-safe container -> title/close -> bounded scrolling fields -> existing actions (fixed where already supported).
Detail popup: title/close -> scrollable labelled values -> actions. Stack long data rows rather than force columns.
Small screens: preserve text sizes; stack financial cards when horizontal space is insufficient, equal-weight navigation, flexible height fields/buttons. Test widths320/360/375/390/412, both themes, large fonts, landscape and keyboard.

## Verification plan
Build debug APK, unit regression tests and lint. Compare source baseline to ensure no domain/data/ViewModel/navigation changes. Runtime screenshots and interaction checks require a working emulator/device; do not mark unexecuted checks PASS.
