IMPLEMENTATION GUARDRAILS

Before changing files:

1. Inspect the existing implementation.
2. Identify the smallest set of files that must change.
3. Do not rewrite unrelated files.
4. Do not replace existing architecture without justification.
5. Do not create duplicate components.
6. Do not create duplicate models.
7. Do not hardcode data that should come from the database.
8. Do not implement features belonging to later stages.
9. Do not modify working functionality unrelated to this task.
10. Preserve existing behavior unless this task explicitly changes it.

After implementation:

1. Build the project.
2. Run relevant tests.
3. Fix errors caused by your changes.
4. Verify the requested feature manually where possible.
5. Summarize exactly what changed.

IMPORTANT:
If an existing implementation conflicts with this specification, do not silently destroy it.
Explain the conflict and choose the least destructive migration path.

PROMPT 01
Project Foundation
       ↓
BUILD ✓
       ↓
PROMPT 02
Design System + App Shell
       ↓
BUILD ✓
       ↓
PROMPT 03
Database + Financial Engine
       ↓
TEST ✓
       ↓
PROMPT 04
Dashboard
       ↓
TEST ✓
       ↓
PROMPT 05
Arus Kas
       ↓
TEST ✓
       ↓
PROMPT 06
Aset + Zakat + Target
       ↓
TEST ✓
       ↓
PROMPT 07
RAPBM
       ↓
TEST ✓
       ↓
PROMPT 08
Laporan + PDF + Audit
       ↓
TEST ✓
       ↓
PROMPT 09
Settings + Backup + Security
       ↓
TEST ✓
       ↓
PROMPT 10
Final QA
       ↓
RELEASE CANDIDATE