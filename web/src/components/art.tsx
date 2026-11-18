import type { ReactNode } from 'react';

// Original line illustrations. Strokes follow currentColor, so the same art works on white and black cards.
// .p = paper fill, .l = lime fill, .k = solid ink fill (see site.css).

function Svg({ viewBox, className, children }: { viewBox: string; className: string; children: ReactNode }) {
  return (
    <svg
      className={className}
      viewBox={viewBox}
      fill="none"
      stroke="currentColor"
      strokeWidth={2.5}
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
      focusable="false"
    >
      {children}
    </svg>
  );
}

// Four-point sparkle centred on (x, y).
function Sparkle({ x, y, r, cls }: { x: number; y: number; r: number; cls: 'k' | 'l' | 'p' }) {
  return (
    <path
      className={cls}
      strokeWidth={2}
      d={`M${x} ${y - r}Q${x} ${y} ${x + r} ${y}Q${x} ${y} ${x} ${y + r}Q${x} ${y} ${x - r} ${y}Q${x} ${y} ${x} ${y - r}Z`}
    />
  );
}

export function HeroArt() {
  return (
    <Svg viewBox="0 0 520 440" className="art art--hero">
      <ellipse cx="260" cy="225" rx="235" ry="75" transform="rotate(-18 260 225)" strokeWidth={2} />
      <ellipse cx="260" cy="225" rx="205" ry="62" transform="rotate(24 260 225)" strokeWidth={2} />

      {/* laptop */}
      <path className="p" strokeWidth={3} d="M140 245h240l-14 20a10 10 0 0 1-8 4H162a10 10 0 0 1-8-4z" />
      <rect className="p" strokeWidth={3} x="165" y="120" width="190" height="125" rx="12" />
      <circle className="k" cx="182" cy="136" r="3" />
      <circle className="k" cx="194" cy="136" r="3" />
      <circle className="k" cx="206" cy="136" r="3" />
      <rect className="l" x="185" y="155" width="96" height="11" rx="5.5" strokeWidth={2} />
      <rect x="185" y="176" width="150" height="11" rx="5.5" strokeWidth={2} />
      <rect x="185" y="197" width="150" height="11" rx="5.5" strokeWidth={2} />
      <rect className="k" x="185" y="217" width="58" height="16" rx="8" />
      <circle className="l" cx="326" cy="161" r="17" strokeWidth={2.5} />
      <path d="m318 161 6 6 11-13" strokeWidth={3} />

      {/* graduation cap */}
      <g transform="translate(62 62) rotate(-8 60 40)">
        <path className="p" d="M26 54v26c0 8 20 16 34 16s34-8 34-16V54" />
        <path className="k" d="M0 40 60 12l60 28-60 28z" />
        <path d="M118 42v34" />
        <circle className="l" cx="118" cy="82" r="7" />
      </g>

      {/* certificate */}
      <g transform="translate(378 64) rotate(9 48 35)">
        <rect className="p" width="100" height="74" rx="7" />
        <path d="M14 20h52M14 34h38M14 48h24" />
        <path className="p" d="M64 62l-5 18 11-6 11 6-5-18" />
        <circle className="l" cx="70" cy="54" r="14" />
      </g>

      {/* coin */}
      <circle className="l" cx="96" cy="318" r="38" strokeWidth={3} />
      <circle cx="96" cy="318" r="28" strokeWidth={2} />
      <text x="96" y="330" textAnchor="middle" fontSize="34" fontWeight="700" fill="currentColor" stroke="none" fontFamily="inherit">
        ₹
      </text>

      {/* approved badge */}
      <circle className="k" cx="430" cy="326" r="26" />
      <path d="m419 326 8 8 15-17" stroke="var(--paper)" strokeWidth={3.5} />

      <Sparkle x={330} y={46} r={15} cls="k" />
      <Sparkle x={474} y={214} r={12} cls="l" />
      <Sparkle x={48} y={216} r={13} cls="k" />
      <Sparkle x={262} y={396} r={11} cls="l" />
      <circle className="l" cx="90" cy="150" r="8" strokeWidth={2} />
      <circle className="k" cx="446" cy="404" r="6" />
    </Svg>
  );
}

function CoinStack({ x, n }: { x: number; n: number }) {
  return (
    <>
      {Array.from({ length: n }, (_, i) => {
        const y = 94 - i * 12;
        return (
          <g key={i}>
            <path className="p" d={`M${x - 22} ${y}v10a22 8 0 0 0 44 0V${y}`} />
            <ellipse className={i === n - 1 ? 'l' : 'p'} cx={x} cy={y} rx="22" ry="8" />
          </g>
        );
      })}
    </>
  );
}

const ICONS: Record<string, ReactNode> = {
  'SCH-STEM': (
    <>
      <path className="p" d="M64 14h32v30l28 50c3 6-1 12-8 12H44c-7 0-11-6-8-12l28-50z" />
      <path className="l" d="M50 74h60l14 22c3 5 0 10-6 10H42c-6 0-9-5-6-10z" />
      <path d="M60 14h40" />
      <circle cx="84" cy="62" r="4" />
      <circle cx="98" cy="48" r="3" />
      <Sparkle x={134} y={26} r={11} cls="k" />
      <Sparkle x={26} y={38} r={8} cls="l" />
    </>
  ),
  'SCH-MCM': (
    <>
      <CoinStack x={40} n={2} />
      <CoinStack x={82} n={4} />
      <CoinStack x={124} n={3} />
      <Sparkle x={124} y={26} r={10} cls="k" />
      <Sparkle x={34} y={40} r={8} cls="l" />
    </>
  ),
  'SCH-WOMEN': (
    <>
      <path className="p" d="M22 84h116l-8 14H30z" />
      <rect className="p" x="36" y="20" width="88" height="64" rx="8" />
      <path d="m66 44-12 10 12 10M94 44l12 10-12 10" />
      <path className="l" d="M86 38 76 70" />
      <Sparkle x={134} y={18} r={11} cls="l" />
      <circle className="k" cx="26" cy="30" r="5" />
    </>
  ),
  'SCH-FIRSTGEN': (
    <>
      <path className="p" d="M80 36c-14-8-30-10-48-8v62c18-2 34 0 48 8z" />
      <path className="p" d="M80 36c14-8 30-10 48-8v62c-18-2-34 0-48 8z" />
      <path d="M42 46h26M42 58h26M42 70h20" />
      <path className="l" d="M104 29v24l8-6 8 6V28" />
      <Sparkle x={136} y={14} r={9} cls="k" />
      <Sparkle x={26} y={18} r={8} cls="l" />
    </>
  ),
  'SCH-SPORTS': (
    <>
      <path d="M52 28H38c0 14 6 22 16 24M108 28h14c0 14-6 22-16 24" />
      <path className="p" d="M52 18h56v30c0 16-12 28-28 28S52 64 52 48z" />
      <path d="M80 76v14" />
      <rect className="k" x="58" y="90" width="44" height="12" rx="4" />
      <Sparkle x={80} y={44} r={14} cls="l" />
      <Sparkle x={134} y={30} r={9} cls="k" />
      <Sparkle x={24} y={52} r={7} cls="l" />
    </>
  ),
  'SCH-RENEWAL': (
    <>
      <rect className="p" x="30" y="14" width="70" height="90" rx="7" />
      <rect className="l" x="40" y="28" width="46" height="13" rx="3" strokeWidth={1.5} />
      <path d="M42 36h44M42 54h30M42 68h20" />
      <circle className="p" cx="104" cy="70" r="24" />
      <path d="M122 88l20 20" strokeWidth={6} />
      <Sparkle x={136} y={22} r={10} cls="k" />
    </>
  ),
};

export function SchemeIcon({ id }: { id: string }) {
  return (
    <Svg viewBox="0 0 160 120" className="art art--icon">
      {ICONS[id] ?? ICONS['SCH-STEM']}
    </Svg>
  );
}
