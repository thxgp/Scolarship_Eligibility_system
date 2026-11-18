# EduScholar design

Stack: existing Vite + React + Astryx. No Next/Astro. GSAP no longer used by the UI.

## Direction
Derived from three user-supplied reference pins (clean white pages, black line illustrations with lime accents, lime highlight labels, alternating white and black rounded cards). Nothing is copied: all illustrations are original SVG in `src/components/art.tsx`.

## System
- Palette: white `#ffffff`, ink `#121212`, lime `#c5f26b`, soft grey `#f1f1ef`, muted text `#55584f` (7:1 on white).
- Type: Space Grotesk Variable for everything; headings 500, display tracking -0.035em.
- Shape: cards 22px radius, 1.5px ink border with heavier right and bottom edge; buttons 10px; chips pill.
- Components: lime `.tag` section labels, `.hl` lime highlight on card titles (white on black cards), alternating white/black scheme cards, grey eligibility panel, black five-stage panel.
- Motion: hover lift 4px on cards, button color flood, reveal on scroll; all disabled under reduced motion.
- Illustrations stroke with currentColor so they flip on black cards.

## Content
No invented metrics or client logos. Eligibility checker uses each scheme's published minimums (CGPA, attendance, income) and names the closest scheme when none match.
