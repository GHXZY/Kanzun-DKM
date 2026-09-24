---
name: Stripe
colors:
  primary: "#533afd"
  primary-deep: "#2e2b8c"
  secondary: "#061b31"
  background: "#ffffff"
  background-canvas: "#f8fafc"
  background-bone: "#f1f5f9"
  surface: "#ffffff"
  surface-card: "#ffffff"
  surface-dark: "#0e0f2e"
  surface-deep: "#080810"
  foreground: "#061b31"
  ink: "#061b31"
  body: "#64748d"
  charcoal: "#273951"
  mute: "#64748d"
  ash: "#64748d"
  stone: "#a0aec0"
  on-primary: "#ffffff"
  on-secondary: "#ffffff"
  on-background: "#061b31"
  on-surface: "#061b31"
  on-dark: "#fcfcfc"
  on-dark-mute: "rgba(252,252,252,0.72)"
  hairline: "#e5edf5"
  hairline-strong: "#b9b9f9"
  divider: "#e5edf5"
  divider-dark: "rgba(255,255,255,0.1)"
  hero-warm: "#ea2261"
  hero-glow: "#f96bee"
  hero-pink: "#ffd7ef"
  badge-success: "#15be53"
  link: "#533afd"
  ring-focus: "rgba(83,58,253,0.5)"
colors-dark:
  primary: "#665efd"
  primary-deep: "#533afd"
  secondary: "#e8ecf0"
  background: "#0e0f2e"
  background-canvas: "#0a0a20"
  background-bone: "#12122a"
  surface: "rgba(255,255,255,0.03)"
  surface-card: "rgba(255,255,255,0.05)"
  surface-dark: "#0e0f2e"
  surface-deep: "#060618"
  foreground: "#e8ecf0"
  ink: "#e8ecf0"
  body: "#8a95a8"
  charcoal: "#a0aec0"
  mute: "#8a95a8"
  ash: "#6b7a8e"
  stone: "#5a6a7e"
  on-primary: "#ffffff"
  on-secondary: "#e8ecf0"
  on-background: "#e8ecf0"
  on-surface: "#e8ecf0"
  on-dark: "#fcfcfc"
  on-dark-mute: "rgba(252,252,252,0.72)"
  hairline: "rgba(255,255,255,0.1)"
  hairline-strong: "rgba(185,185,249,0.3)"
  divider: "rgba(255,255,255,0.1)"
  divider-dark: "rgba(255,255,255,0.15)"
typography:
  display-xl:
    fontFamily: "sohne-var, SF Pro Display, system-ui, sans-serif"
    fontSize: "48px"
    fontWeight: 300
    lineHeight: 1.15
    letterSpacing: "-0.96px"
  heading-lg:
    fontFamily: "sohne-var, SF Pro Display, system-ui, sans-serif"
    fontSize: "32px"
    fontWeight: 300
    lineHeight: 1.10
    letterSpacing: "-0.64px"
  body-lg:
    fontFamily: "sohne-var, SF Pro Display, system-ui, sans-serif"
    fontSize: "18px"
    fontWeight: 300
    lineHeight: 1.40
    letterSpacing: "normal"
  body-md:
    fontFamily: "sohne-var, SF Pro Display, system-ui, sans-serif"
    fontSize: "16px"
    fontWeight: 300
    lineHeight: 1.40
    letterSpacing: "normal"
  button-md:
    fontFamily: "sohne-var, SF Pro Display, system-ui, sans-serif"
    fontSize: "16px"
    fontWeight: 400
    lineHeight: 1.00
    letterSpacing: "normal"
  button-sm:
    fontFamily: "sohne-var, SF Pro Display, system-ui, sans-serif"
    fontSize: "14px"
    fontWeight: 400
    lineHeight: 1.00
    letterSpacing: "normal"
  caption:
    fontFamily: "sohne-var, SF Pro Display, system-ui, sans-serif"
    fontSize: "12px"
    fontWeight: 300
    lineHeight: 1.45
    letterSpacing: "normal"
  code-md:
    fontFamily: "SourceCodePro, SFMono-Regular, ui-monospace, monospace"
    fontSize: "12px"
    fontWeight: 500
    lineHeight: 2.00
    letterSpacing: "normal"
spacing:
  xxs: "2px"
  xs: "4px"
  sm: "8px"
  md: "12px"
  lg: "16px"
  xl: "24px"
  xxl: "32px"
  xxxl: "48px"
  section: "96px"
  band: "160px"
rounded:
  none: "0px"
  xs: "2px"
  sm: "4px"
  md: "6px"
  lg: "8px"
  xl: "12px"
  full: "9999px"
components:
  button-primary:
    backgroundColor: "{colors.primary}"
    textColor: "{colors.on-primary}"
    typography: "{typography.button-md}"
    rounded: "{rounded.sm}"
    padding: "8px 16px"
    height: "40px"
  button-ghost:
    backgroundColor: "transparent"
    textColor: "{colors.primary}"
    typography: "{typography.button-md}"
    rounded: "{rounded.sm}"
    padding: "8px 16px"
    height: "40px"
    border: "1px solid {colors.hairline-strong}"
  text-input:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.ink}"
    typography: "{typography.body-md}"
    rounded: "{rounded.sm}"
    padding: "10px 12px"
    height: "44px"
  card:
    backgroundColor: "{colors.surface-card}"
    textColor: "{colors.ink}"
    typography: "{typography.body-md}"
    rounded: "{rounded.md}"
    padding: "24px"
    shadow: "{elevation.level1}"
  badge-success:
    backgroundColor: "rgba(21,190,83,0.2)"
    textColor: "#108c3d"
    typography: "{typography.caption}"
    rounded: "{rounded.sm}"
    padding: "1px 6px"
  nav-bar:
    backgroundColor: "rgba(255,255,255,0.9)"
    textColor: "{colors.ink}"
    typography: "{typography.button-sm}"
    rounded: "{rounded.none}"
    height: "60px"
  modal:
    backgroundColor: "{colors.surface-card}"
    textColor: "{colors.ink}"
    typography: "{typography.heading-lg}"
    rounded: "{rounded.lg}"
    padding: "24px"
    shadow: "{elevation.level4}"
---

## Overview

Stripe is a fintech design system built for the intersection of technical precision and typographic luxury. It reimagines the financial interface as something closer to a bespoke type specimen than a traditional banking dashboard — every pixel carries the weight of a world-class type foundry applied to the rigor of payments infrastructure.

The emotional register is one of **controlled intensity**: the deep navy of `{colors.secondary}` grounds the interface like a midnight suit, while `{colors.primary}` pulses with the electric authority of a trusted financial network. The proprietary sohne-var typeface lends editorial warmth to technical data — balances, transaction histories, and API responses all read with the clarity of a well-set magazine page. This is not the cold, clinical fintech of legacy banking portals. It is a financial institution redesigned by typographers who believe that a number should feel as beautiful as a letter.

Every design decision in Stripe is governed by three principles:

1. **The Accent Rule** — Purple (`{colors.primary}`) is reserved exclusively for interactive elements and brand moments. It never appears as a background wash or decorative flourish. When purple appears, it means "act here."
2. **The Editorial Frame** — Typography is the primary interface. Spacing, color, and elevation exist to serve type readability, not compete with it.
3. **The Precision Constraint** — Borders are sharp (`{rounded.sm}`), shadows are tight (`{elevation.level1}`), and motion is instantaneous (`120ms`). Nothing is ornamental — every element earns its place through utility.

---

## Colors

The Stripe palette is a study in controlled contrast: a deep, cool primary anchors a warm neutral system. The result is technical without being cold, luxurious without being ostentatious.

### Brand & Accent

The brand is defined by a singular purple anchor — `{colors.primary}` — a vivid indigo-violet that sits at the intersection of corporate trust and creative energy. It is supported by a warm hero gradient (`{colors.hero-warm}` → `{colors.hero-glow}` → `{colors.hero-pink}`) used exclusively for atmospheric brand moments: hero sections, loading states, and celebration screens. This gradient never appears on functional UI — only on surfaces where emotion matters.

```
Hero Gradient: {colors.hero-warm} → {colors.hero-glow} → {colors.hero-pink}
```

The secondary palette of deep navy (`{colors.secondary}`) serves as the primary text and structural color — the "suit" of the system against which everything else is measured.

### Surface Hierarchy

The surface system uses semantic naming to reflect real-world material depth:

| Token | Light Mode | Dark Mode | Use |
|-------|-----------|-----------|-----|
| Background | `{colors.background}` | `{colors-dark.background}` | Page-level canvas |
| Canvas | `{colors.background-canvas}` | `{colors-dark.background-canvas}` | Secondary page areas |
| Bone | `{colors.background-bone}` | `{colors-dark.background-bone}` | Inset card groups, code blocks |
| Surface | `{colors.surface}` | `{colors-dark.surface}` | Default interactive surface |
| Surface Card | `{colors.surface-card}` | `{colors-dark.surface-card}` | Elevated card containers |
| Surface Dark | `{colors.surface-dark}` | `{colors-dark.surface-dark}` | Dark bands, footers |
| Surface Deep | `{colors.surface-deep}` | `{colors-dark.surface-deep}` | Deepest structural surfaces |

In light mode, surfaces stack as pure whites and faint cools (`{colors.background}` → `{colors.background-canvas}` → `{colors.background-bone}`). In dark mode, the canvas shifts to a deep indigo-navy (`{colors-dark.background}`), and surfaces lift forward through translucent glass layers (`{colors-dark.surface}`, `{colors-dark.surface-card}`). This creates a **Frosted Glass** effect — surfaces feel like tinted glass panels floating above the deep navy void.

### Text Hierarchy

| Token | Light Mode | Dark Mode | Use |
|-------|-----------|-----------|-----|
| Foreground/Ink | `{colors.foreground}` | `{colors-dark.foreground}` | Primary headings, navigation |
| Body | `{colors.body}` | `{colors-dark.body}` | Long-form paragraph text |
| Charcoal | `{colors.charcoal}` | `{colors-dark.charcoal}` | Captions, secondary metadata |
| Mute | `{colors.mute}` | `{colors-dark.mute}` | Supporting label text |
| Ash | `{colors.ash}` | `{colors-dark.ash}` | Tertiary, placeholder text |
| Stone | `{colors.stone}` | `{colors-dark.stone}` | Disabled text, ghost indicators |

The text hierarchy follows a **six-level gradient** from `{colors.ink}` (headings, primary actions) to `{colors.stone}` (disabled, ghosted). In dark mode, the entire gradient inverts — `{colors-dark.ink}` becomes `#e8ecf0` (a warm off-white), and each step descends toward cooler, lower-contrast grays. The `{colors.on-dark}` value (`#fcfcfc`) and its muted counterpart (`{colors.on-dark-mute}`) ensure readability against the deepest surfaces.

### Semantic Colors

| Token | Value | Usage |
|-------|-------|-------|
| Success | `#15be53` | Positive confirmations, balances |
| Warning | `#9b6829` | Amber alerts, pending states |
| Danger | `#ea2261` | Destructive actions, errors |
| Info | `#2874ad` | Informational badges, helper text |

Semantic colors use translucent backgrounds (e.g., `rgba(21,190,83,0.2)` for success badges) with higher-saturation text variants (`#108c3d`). This creates legible badges without overwhelming the surface hierarchy.

### Dark Mode Comparison

| Surface Token | Light | Dark |
|-------|-------|------|
| Primary | `{colors.primary}` | `{colors-dark.primary}` |
| Surface | `{colors.surface}` | `{colors-dark.surface}` |
| Foreground | `{colors.foreground}` | `{colors-dark.foreground}` |
| Body | `{colors.body}` | `{colors-dark.body}` |
| Hairline | `{colors.hairline}` | `{colors-dark.hairline}` |
| Divider | `{colors.divider}` | `{colors-dark.divider}` |

The dark palette lifts primary to a brighter violet (`{colors-dark.primary}`) for contrast against the deep indigo background. Border opacities soften — translucent `{colors-dark.hairline}` creates depth through glass, not weight.

---

## Typography

### Font Family

Stripe's typography is anchored by **sohne-var** — a proprietary variable font originally crafted for editorial and brand contexts. Its combination of warmth and precision makes it equally at home setting a balance sheet or a headline. The full stack resolves as:

**Display & Body**: `sohne-var, SF Pro Display, system-ui, sans-serif`

**Monospace**: `SourceCodePro, SFMono-Regular, ui-monospace, monospace`

This is an intentionally **narrow stack** — no generic fallbacks like Arial or Helvetica. The system trusts Apple's SF Pro Display on macOS/iOS, and `system-ui` on all other platforms, ensuring consistent glyph metrics across environments.

### Hierarchy

| Token | Font Family | Size | Weight | Line Height | Letter Spacing | Use |
|-------|------------|------|--------|-------------|----------------|-----|
| `{typography.display-xl}` | sohne-var → SF Pro → system-ui | 48px | 300 (Light) | 1.15 | -0.96px | Hero headlines, landing titles |
| `{typography.heading-lg}` | sohne-var → SF Pro → system-ui | 32px | 300 (Light) | 1.10 | -0.64px | Section titles, modal headers |
| `{typography.body-lg}` | sohne-var → SF Pro → system-ui | 18px | 300 (Light) | 1.40 | normal | Featured content, lead paragraphs |
| `{typography.body-md}` | sohne-var → SF Pro → system-ui | 16px | 300 (Light) | 1.40 | normal | Default body, card content |
| `{typography.button-md}` | sohne-var → SF Pro → system-ui | 16px | 400 (Normal) | 1.00 | normal | Primary/secondary buttons |
| `{typography.button-sm}` | sohne-var → SF Pro → system-ui | 14px | 400 (Normal) | 1.00 | normal | Small buttons, tab labels |
| `{typography.caption}` | sohne-var → SF Pro → system-ui | 12px | 300 (Light) | 1.45 | normal | Labels, metadata, timestamps |
| `{typography.code-md}` | SourceCodePro → SFMono → ui-monospace | 12px | 500 (Medium) | 2.00 | normal | Code snippets, API responses |

### Principles

**The Light Weight Rule** — Display and body text use weight 300 (Light) exclusively. This is deliberate: Stripe's fintech content — numbers, balances, transaction data — benefits from the airiness of light letterforms. The reduced stroke contrast makes long-form financial data feel less dense and more approachable. Only buttons shift to weight 400 (Normal) for interactive clarity, and code blocks use 500 (Medium) for maximum legibility at small sizes.

**The Negative Tracking Signal** — Headlines use negative letter-spacing (`-0.96px` for `{typography.display-xl}`, `-0.64px` for `{typography.heading-lg}`) to create tight, editorial word clusters. This psychological compression signals authority — these are crafted headlines, not system-generated titles. Body text and buttons return to `normal` tracking for uninterrupted readability.

**The Type-As-Interface Principle** — Buttons and body text share the same size (`16px`) but diverge in weight (400 vs 300) and line-height (1.00 vs 1.40). This creates visual hierarchy without changing font size, preserving the editorial rhythm of the page while clearly distinguishing interactive elements from content.

**The Mono Precision Rule** — Code blocks use `{typography.code-md}` at 12px with 2.00 line-height and 500 weight. The generous line height creates breathing room for API responses and code samples, while the medium weight compensates for the small size. SourceCodePro's semi-condensed glyphs maximize information density in technical panels.

### Note on Font Substitutes

When sohne-var is unavailable (third-party integrations, open-source projects), replace with **SF Pro Display** on Apple platforms and **Inter** or **system-ui** elsewhere. Maintain the weight distribution (300 for display/body, 400 for buttons, 500 for code). Do not substitute with Arial, Helvetica, or Roboto — their metric properties shift line lengths and break the editorial grid.

For SourceCodePro substitutes, use `SFMono-Regular` on macOS and `Cascadia Code` or `Fira Code` on Windows. Maintain the 12px size and 500 weight regardless of monospace substitute.

---

## Layout & Spacing

### Semantic Spacing Scale

Stripe uses an **8px base grid** scaled across ten semantic intervals:

| Token | Value | Use |
|-------|-------|-----|
| `{spacing.xxs}` | 2px | Hairline separators, focus ring offset |
| `{spacing.xs}` | 4px | Micro padding, icon gap |
| `{spacing.sm}` | 8px | Tight element spacing, badge inset |
| `{spacing.md}` | 12px | Default inline gap, button icon spacing |
| `{spacing.lg}` | 16px | Card inner padding, form field gap |
| `{spacing.xl}` | 24px | Section heading margin, modal padding |
| `{spacing.xxl}` | 32px | Component group separation |
| `{spacing.xxxl}` | 48px | Feature block spacing |
| `{spacing.section}` | 96px | Between major sections |
| `{spacing.band}` | 160px | Hero band padding, maximum separation |

### Grid Model

The layout follows a **12-column responsive grid** with 16px gutters (`{spacing.lg}`). Content max-width caps at 1200px for readability — financial dashboards and data panels benefit from constrained line lengths that prevent eye fatigue across wide monitors.

### Whitespace Philosophy

**"The Generous Margin"** — Stripe uses whitespace as a signal of quality. Section spacing (`{spacing.section}` at 96px and `{spacing.band}` at 160px) creates deliberate breathing room between content areas. This is not wasted space — it is editorial pacing. A balance sheet with generous leading and wide margins conveys trust. A cramped one conveys urgency.

Internal component spacing uses tighter intervals (`{spacing.sm}` to `{spacing.lg}`) to keep related content visually coupled. Cards use `{spacing.lg}` (16px) inner padding for density, while modals use `{spacing.xl}` (24px) for containment. The section-level spacing (`{spacing.section}`, `{spacing.band}`) applies between blocks — never inside them.

---

## Elevation & Depth

### Level Table

Stripe's elevation system uses shadow-based depth with five levels. Shadows are intentionally restrained — this is a fintech interface, not a card game.

| Level | Light Treatment | Dark Treatment | Use Case |
|-------|----------------|----------------|----------|
| Level 0 | none | none | Page canvas, flat surfaces |
| Level 1 | `rgba(23,23,23,0.06) 0px 3px 6px 0px` | `rgba(0,0,0,0.2) 0px 3px 6px 0px` | Cards, dropdowns, subtle lift |
| Level 2 | `rgba(23,23,23,0.08) 0px 15px 35px 0px` | `rgba(0,0,0,0.3) 0px 15px 35px 0px` | Hovered cards, elevated panels |
| Level 3 | `rgba(50,50,93,0.25) 0px 30px 45px -30px, rgba(0,0,0,0.1) 0px 18px 36px -18px` | `rgba(0,0,0,0.4) 0px 30px 45px -30px, rgba(0,0,0,0.3) 0px 18px 36px -18px` | Modals, floating menus |
| Level 4 | `rgba(3,3,39,0.25) 0px 14px 21px -14px, rgba(0,0,0,0.1) 0px 8px 17px -8px` | `rgba(0,0,0,0.5) 0px 14px 21px -14px, rgba(0,0,0,0.3) 0px 8px 17px -8px` | Highest emphasis — dialogs, alerts |

**The Stripe Shadow Rule**: Shadows use colored undertones (indigo, navy blue) rather than pure black. At level 3, for example, the shadow uses `rgba(50,50,93,0.25)` — a deep indigo — which creates a shadow that feels like the surface is casting color onto the background. This subtle chromatic shift distinguishes Stripe's shadows from generic gray-box treatments.

In dark mode, all shadow opacities increase to compensate for the dark canvas, shifting toward pure black undertones (`rgba(0,0,0,0.2)` through `rgba(0,0,0,0.5)`).

### Decorative Depth

Beyond the shadow system, Stripe uses two decorative depth techniques:

1. **Frosted Glass Nav** — The navigation bar (`{component.nav-bar}`) uses `rgba(255,255,255,0.9)` background in light mode, creating a translucent glass effect with `backdrop-filter: blur(12px)` assumed. In dark mode, the glass effect inverts to `rgba(14,15,46,0.9)` with the same blur.

2. **Hero Gradient Bands** — Full-width hero sections use the three-stop warm gradient (`{colors.hero-warm}` → `{colors.hero-glow}` → `{colors.hero-pink}`) as an atmospheric backdrop. These bands appear only at the top of landing pages and celebration screens — never on functional dashboard surfaces.

3. **Dark Band Footer** — The deepest structural layer (`{colors.surface-deep}`) serves as a visual anchor at the bottom of the page. It creates a natural "floor" that prevents the page from feeling like it floats in infinite space.

---

## Shapes

### Border Radius Scale

Stripe's corner radii progress from sharp (functional) to generous (container) with calculated steps:

| Token | Value | Use |
|-------|-------|-----|
| `{rounded.none}` | 0px | Nav bars, hero bands, full-bleed sections |
| `{rounded.xs}` | 2px | Micro details, inline code tags, subtle corners |
| `{rounded.sm}` | 4px | Buttons, inputs, badges, default interactive |
| `{rounded.md}` | 6px | Cards, panels, dropdown containers |
| `{rounded.lg}` | 8px | Modals, dialogs, large containers |
| `{rounded.xl}` | 12px | Special emphasis containers, feature blocks |
| `{rounded.full}` | 9999px | Pills, tags, circular avatars, radio indicators |

**The Tight Corner Rule**: Stripe uses `{rounded.sm}` (4px) for buttons and form controls — deliberately tighter than the generic 8px roundness common in modern UI. This creates a precise, engineered feel appropriate for financial interfaces. Generous rounding (`{rounded.lg}` at 8px) is reserved for containers that need to feel contained and safe (modals, dialogs). Full rounding (`{rounded.full}`) is used exclusively for pill-shaped elements and circular controls — never for cards or panels.

Shape tokens map semantic roles to radius values:
- `shape-button`: `{rounded.sm}` (4px)
- `shape-input`: `{rounded.sm}` (4px)
- `shape-card`: `{rounded.md}` (6px)
- `shape-badge`: `{rounded.sm}` (4px)
- `shape-checkbox`: `{rounded.sm}` (4px)
- `shape-radio`: `{rounded.full}` (circle)
- `shape-checkbox-indicator`: 4px
- `shape-radio-indicator`: 50%

### Photography Geometry

Stripe uses photography sparingly, in line with its technical personality. When imagery appears:

- **Hero images**: 16:9 aspect ratio, `{rounded.none}`, full-bleed edge-to-edge
- **Card thumbnails**: 3:2 aspect ratio, `{rounded.md}` corners, contain crop behavior
- **Contributor/merchant avatars**: Circular, 40px diameter (`{rounded.full}`)
- **Logo lockups**: Contained within 2:1 bounding boxes, `{rounded.none}`

Images should never compete with typography for hierarchy. The editorial frame principle means text sits on a clear, uncluttered field — photography is a supporting layer, not the primary interface.

---

## Components

### Buttons & Interaction

**Button Primary** (`{component.button-primary}`)
- Background `{colors.primary}`, text `{colors.on-primary}`
- Type `{typography.button-md}`, rounded: `{rounded.sm}`
- Padding 8px 16px, height 40px
- Hover: darkens to `{colors.primary}` hover (`#4434d4`) via `200ms` standard easing
- Active/Pressed: shifts to `{colors.primary-deep}` (`#2e2b8c`) with `120ms` fast easing
- Focus: `{colors.ring-focus}` ring at 2px offset
- Reserved exclusively for the single most important action on a surface

**Button Ghost** (`{component.button-ghost}`)
- Background `transparent`, text `{colors.primary}`
- Type `{typography.button-md}`, rounded: `{rounded.sm}`
- Padding 8px 16px, height 40px
- Border: `1px solid {colors.hairline-strong}`
- Hover: background fills with `{colors.primary}` at 5% (`rgba(83,58,253,0.05)`)
- Active: background fills at 10% (`rgba(83,58,253,0.1)`)
- Used for secondary actions alongside primary buttons

### Inputs & Selection

**Text Input** (`{component.text-input}`)
- Background `{colors.surface}`, text `{colors.ink}`
- Type `{typography.body-md}`, rounded: `{rounded.sm}`
- Padding 10px 12px, height 44px
- Border: `1px solid {colors.hairline}` — the default hairline border
- Hover: border strengthens to `{colors.hairline-strong}` (`#b9b9f9`)
- Focus: `{colors.ring-focus}` ring + border shifts to `{colors.primary}`
- Placeholder: `{colors.mute}` (`#64748d`) at 300 weight
- Error: border shifts to `{colors.danger}` (`#ea2261`) + `rgba(234,34,97,0.1)` background
- The 44px height meets WCAG minimum touch target requirements

**Checkbox & Radio**
- Checkbox: `{rounded.sm}` corners (4px), `1.5px` border in `{colors.hairline}`
- Radio: `{rounded.full}` (circular), `1.5px` border in `{colors.hairline}`
- Checked: fill `{colors.primary}`, indicator `{colors.on-primary}`
- Focus: `{colors.ring-focus}` ring
- Disabled: `{colors.stone}` fill at 50% opacity

### Chips & Controls

**Badge Success** (`{component.badge-success}`)
- Background `rgba(21,190,83,0.2)`, text `#108c3d`
- Type `{typography.caption}`, rounded: `{rounded.sm}`
- Padding 1px 6px, inline height auto
- Used for status indicators, success confirmations, pill-style labels
- Warning variant: background `rgba(155,104,41,0.15)`, text `#7d5321`
- Error variant: background `rgba(234,34,97,0.1)`, text `#c71b50`

**Tabs**
- Active indicator: `2px` bottom border in `{colors.primary}`
- Inactive: `{colors.body}` text, `{colors.hairline}` bottom border
- Type `{typography.button-sm}`, height 40px
- Hover: text shifts to `{colors.ink}`

### Data & Containers

**Card** (`{component.card}`)
- Background `{colors.surface-card}`, text `{colors.ink}`
- Type `{typography.body-md}`, rounded: `{rounded.md}`
- Padding 24px, shadow: `{elevation.level1}`
- Border: `1px solid {colors.hairline}` when adjacent to surfaces of same color
- Hover: elevation lifts to `{elevation.level2}` with `200ms` transition
- Cards are the primary data container — used for transaction summaries, account balances, and feature panels

**Modal** (`{component.modal}`)
- Background `{colors.surface-card}`, text `{colors.ink}`
- Type `{typography.heading-lg}`, rounded: `{rounded.lg}`
- Padding 24px, shadow: `{elevation.level4}`
- Overlay: `{colors.overlay}` (`rgba(6,27,49,0.4)`) in light mode
- z-index: `{zIndex.modal}` (400)
- Enter animation: `200ms` with `cubic-bezier(0.2, 0.6, 0.25, 1)` — the enter easing curve

**Tables**
- Header: `{colors.background-bone}` background, `{typography.caption}` type, uppercase
- Body rows: `{typography.body-md}`, alternating with `{colors.surface}` background
- Border: `1px solid {colors.hairline}` between rows
- Hover row: `{colors.background-canvas}` background
- Padding: `{spacing.sm}` vertical, `{spacing.lg}` horizontal

### Feedback Components

**Alerts**
- Success: `rgba(21,190,83,0.1)` background, `#108c3d` text, `2px` left border in `{colors.badge-success}`
- Error: `rgba(234,34,97,0.1)` background, `#c71b50` text, `2px` left border in `{colors.danger}`
- Warning: `rgba(155,104,41,0.08)` background, `#7d5321` text, `2px` left border in `{colors.warning}`
- Rounded: `{rounded.sm}`, padding `{spacing.md} {spacing.lg}`
- Icon + title + message layout with 8px gap

**Tooltips**
- Background: `{colors.surface-dark}` (`#1c1e54`), text `{colors.on-dark}` (`#fcfcfc`)
- Type `{typography.caption}`, rounded: `{rounded.sm}`
- Padding `{spacing.xs} {spacing.sm}`, max-width 240px
- z-index: `{zIndex.tooltip}` (600)
- Enter: `120ms` fast easing, exit: `80ms`

**Loading States**
- Spinner: `{colors.primary}` with 2px stroke, 20px diameter
- Skeleton: `{colors.hairline}` background with shimmer animation using `{colors.background-canvas}` highlight
- Duration: `1200ms` shimmer cycle with `cubic-bezier(0.25, 0.1, 0.25, 1)` standard easing

### Navigation

**Nav Bar** (`{component.nav-bar}`)
- Background `rgba(255,255,255,0.9)` (frosted glass), text `{colors.ink}`
- Type `{typography.button-sm}`, rounded: `{rounded.none}`
- Height 60px
- Bottom border: `1px solid {colors.hairline}`
- Active item: bottom indicator `2px` solid `{colors.primary}`
- Dark mode: background `rgba(14,15,46,0.9)`, border `rgba(255,255,255,0.1)`
- z-index: `{zIndex.sticky}` (200), sticks to top on scroll

**Dropdown Menu**
- Background `{colors.surface-card}`, text `{colors.ink}`
- Type `{typography.body-md}`, rounded: `{rounded.md}`
- Shadow: `{elevation.level2}`, z-index: `{zIndex.dropdown}` (100)
- Item padding: `{spacing.sm} {spacing.lg}`
- Hover item: `{colors.background-bone}` background
- Separator: `1px solid {colors.hairline}`

---

## Do's and Don'ts

### Do

- **Do use `{colors.primary}` exclusively for interactive elements** — buttons, links, focus states, and selected indicators. Purple means actionable.
- **Do apply the light weight (300) for all display and body text** — the airiness is intentional; it reduces the density of financial data.
- **Do use `{rounded.sm}` (4px) for buttons and inputs** — the tight corner is a signature detail that signals precision.
- **Do use negative letter-spacing on headlines** — `-0.96px` for `{typography.display-xl}`, `-0.64px` for `{typography.heading-lg}`.
- **Do use `{elevation.level1}` for all card defaults** — shadows should be felt, not seen. Level 1 is just 3px of vertical offset.
- **Do use the semantic spacing scale** — `{spacing.section}` (96px) between major sections, `{spacing.lg}` (16px) inside cards.
- **Do apply the frosted glass effect** (`backdrop-filter: blur`) on nav bars and overlay panels — it creates material depth without added shadow weight.
- **Do maintain the 8px grid** for all spacing decisions — every measurement should be a multiple of 2px, preferably 8px.

### Don't

- **Don't use `{colors.primary}` as a background wash or decorative fill** — it is an interactive accent, not a surface color.
- **Don't apply the hero gradient (`{colors.hero-warm}` → `{colors.hero-glow}`) to functional UI elements** — gradients are for brand moments only.
- **Don't use black (`#000000`) for any text in light mode** — `{colors.ink}` (`#061b31`) is the darkest permitted value; pure black creates harsh contrast that breaks the editorial warmth.
- **Don't use `{rounded.lg}` (8px) as the default button radius** — `{rounded.sm}` (4px) is the system default. Generous rounding is for containers, not controls.
- **Don't use heavy shadows (`{elevation.level3}` or above) on cards or standard surfaces** — level 3 is reserved for modals, level 4 for dialogs.
- **Don't introduce additional brand colors beyond the primary/secondary/hero system** — every color addition dilutes the purple accent.
- **Don't use Arial, Helvetica, or Roboto as font substitutes** — they break the editorial grid. Use SF Pro Display or system-ui.
- **Don't override the `{colors.hairline}` token** — all borders should use the hairline value (`#e5edf5` in light mode) unless a stronger separator is explicitly needed (`{colors.hairline-strong}`).

---

## Responsive Behavior

### Breakpoints

| Name | Width | Key Changes |
|------|-------|-------------|
| xs | 0-639px | Single-column layout, stacked navigation, expanded touch targets |
| sm | 640px-767px | 2-column grid begins, compact nav |
| md | 768px-1023px | 4-column grid, sidebar appears, card grid at 2-up |
| lg | 1024px-1279px | 8-column grid, full nav bar, card grid at 3-up |
| xl | 1280px-1535px | 12-column grid, maximum content width 1200px |
| 2xl | 1536px+ | Full 12-column grid, content centered with max-width |

### Touch Targets

All interactive elements meet a **minimum 44px touch target** (WCAG 2.1 Success Criterion 2.5.5):

- **Buttons** (`{component.button-primary}`, `{component.button-ghost}`): 40px height at desktop; expand to 44px at `md` and below
- **Text inputs** (`{component.text-input}`): 44px at all breakpoints
- **Nav bar items**: 60px hit area (full nav bar height)
- **Icon-only buttons**: Minimum 44×44px touch area with icon centered inside
- **Bottom nav items** (mobile): Expanded to 56px height for thumb reach

### Collapsing Strategy

- **Navigation**: At `md` and below, the full horizontal nav collapses into a hamburger menu. The frosted glass effect persists in the mobile menu panel.
- **Card Grids**: 3-up cards collapse to 2-up at `md` and single-column at `sm`. Card padding reduces from 24px (`{spacing.xl}`) to 16px (`{spacing.lg}`) at `sm`.
- **Tables**: At `sm` breakpoint, wide tables switch to a stacked card layout — each row becomes a bordered card with labeled fields. The `overflow-x: auto` fallback is avoided in favor of semantic restacking.
- **Modals**: At `sm`, modals become full-screen sheets (`100vw × 100vh`) with `{rounded.none}` and the overlay removed. Padding reduces from 24px to 16px.
- **Forms**: Side-by-side label-input pairs stack vertically at `md` and below. Input width becomes `100%` of the container.

### Image Behavior

- **DPR Handling**: All images use `srcset` with 1x, 2x, and 3x variants. Default to 2x for modern displays.
- **Responsive Sizing**: Hero images use `width: 100vw; height: auto` with max-height constraints. Card thumbnails use `object-fit: cover` with `aspect-ratio: 3/2` set via CSS.
- **Gradient vs Asset Decisions**: The hero gradient is CSS-native (no image asset), ensuring zero-load rendering. Photography assets are deferred with `loading="lazy"` below the fold.
- **Avatar sizing**: Contributor/merchant avatars remain circular at all breakpoints, scaling from 40px (desktop) to 32px (mobile).

---

## Iteration Guide

1. **Focus on one component at a time.** Do not simultaneously redesign buttons, cards, and navigation. Choose a single component (e.g., `{component.button-primary}`), adjust its tokens in `design-token.json`, regenerate the CSS, and validate the output before moving to the next component.

2. **Reference tokens, not raw values.** All component specifications in code and documentation must use token paths (`{colors.primary}`, `{typography.button-md}`, `{rounded.sm}`) rather than raw hex or pixel values. This ensures a single source of truth and makes global changes a matter of updating one token file.

3. **Run validation after every token change.** Execute the theme-validation skill to verify: exact file counts, correct versioning (v2.4.0), and Style Dictionary schema conformance (value + type keys, no `$` prefix, no `$description`, no `$extensions`).

4. **When adding a new component variant, create a separate component entry.** Do not overload an existing component with conditional logic. For example, add `button-ghost` as a distinct entry rather than a `variant` property on `button-primary`. This keeps the token schema flat and predictable.

5. **Keep the purple accent scarce.** Every new use of `{colors.primary}` must pass the "is this interactive?" test. If the answer is no, use `{colors.secondary}` or a neutral surface token instead. The accent's power comes from its rarity.

6. **Test both light and dark modes after every change.** The dark palette is not an afterthought — components must be verified in both `:root` (light) and `[data-theme="dark"]` (dark) contexts. Pay special attention to `{colors.hairline}`/`{colors.divider}` opacity shifts and shadow opacity compensation.

7. **Maintain the 8px grid.** All spacing values should be multiples of 8px for the primary scale (sm=8, lg=16, xl=24, xxl=32, xxxl=48) with 2px and 4px permitted for micro spacing (xxs, xs) and border radii. If a value doesn't fit the grid, question whether it belongs in the system.

8. **Document new tokens in DesignSystem.md.** After adding tokens to `design-token.json`, update this document's YAML front matter and relevant prose sections. The documentation must remain in sync with the token file — out-of-sync docs are worse than no docs.

---

## Known Gaps

- **Dropdown menu component** — The dropdown pattern is described in prose but does not yet have a dedicated component token entry in `components`. Extract and formalize as `component.dropdown` with full spec (backgroundColor, textColor, typography, rounded, padding, height, shadow).
- **Tab component** — Tab styles are described in the Chips & Controls section but lack a formal component token entry. Extract as `component.tabs` with active/inactive variants.
- **Table component** — The table pattern is described in Data & Containers but lacks a structured `component.table` token definition. Header row, body row, and hover row need separate token entries.
- **Alert variants** — Semantic alert colors (success, error, warning, info) are documented in prose but need formal component token entries with `component.alert-success`, `component.alert-error`, etc.
- **Tooltip component** — Described in prose but not extracted as a token entry. Needs `component.tooltip` definition.
- **Skeleton/loading states** — Loading patterns are described but lack formal tokenization. Consider adding `component.skeleton` with backgroundColor and animation tokens.
- **Focus ring tokenization** — The `{colors.ring-focus}` token exists but pressed/active state tokens for components (beyond `primary-deep`) are not fully extracted. Each component should have documented hover, active, and disabled states.
- **Motion tokens** — `animation.duration` and `animation.easing` tokens exist in `design-token.json` but are not yet integrated into the component specs in this document. Each component should reference the appropriate duration/easing for enter, exit, and state transitions.
- **Pages and surfaces behind authentication** — Dashboard surfaces, transaction detail panels, and settings pages have not been analyzed for token extraction. These may introduce additional surface levels or component variants not yet documented.
- **Third-party integration surfaces** — Connect flows, marketplace onboarding, and embedded components for partner sites are not covered by the current token set.
