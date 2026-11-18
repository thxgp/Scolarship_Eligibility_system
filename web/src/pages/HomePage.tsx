import { useEffect, useRef, useState } from 'react';
import { Link } from 'react-router';
import { ArrowUpRightIcon, AcademicCapIcon, BanknotesIcon, BuildingLibraryIcon, IdentificationIcon } from '@heroicons/react/24/outline';
import { NumberInput } from '@astryxdesign/core/NumberInput';
import { HeroArt, SchemeIcon } from '../components/art';
import { SchemeCard } from '../components/SchemeCard';
import { checkSchemes } from '../eligibility';
import { SCHEMES } from '../schemes';

const STAGES = [
  ['Submitted', 'Your application joins the verification queue.'],
  ['Documents', 'An officer checks your marksheets and income slip.'],
  ['Audit', 'CGPA and family income are checked against the scheme.'],
  ['Committee', 'Applicants are ranked by merit index and approved.'],
  ['Paid', 'The grant goes to your Aadhaar-linked bank account.'],
];

const DOCUMENTS = [
  [AcademicCapIcon, 'Marksheets', 'Latest semester or 10+2 grade sheet, verified by your college.'],
  [BanknotesIcon, 'Income certificate', 'From the Tehsildar or revenue authority, issued this financial year.'],
  [IdentificationIcon, 'College bonafide and ID', 'States your course, roll number and attendance.'],
  [BuildingLibraryIcon, 'Aadhaar-linked passbook', 'The bank account that receives the transfer.'],
] as const;

// Fade sections in once as they enter the viewport.
function useReveal() {
  const ref = useRef<HTMLElement>(null);
  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    const io = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          el.classList.add('is-in');
          io.disconnect();
        }
      },
      { threshold: 0.1 },
    );
    io.observe(el);
    return () => io.disconnect();
  }, []);
  return ref;
}

export function HomePage() {
  const [gpa, setGpa] = useState(8);
  const [attendance, setAttendance] = useState(85);
  const [income, setIncome] = useState(300000);

  const results = checkSchemes(gpa, income, attendance);
  const eligible = results.filter((r) => r.gaps.length === 0).sort((a, b) => b.scheme.numericAmount - a.scheme.numericAmount);
  // Closest miss: fewest unmet conditions, then the larger grant.
  const closest = [...results].sort((a, b) => a.gaps.length - b.gaps.length || b.scheme.numericAmount - a.scheme.numericAmount)[0];
  const ranked = [...SCHEMES].sort((a, b) => b.numericAmount - a.numericAmount);

  const schemes = useReveal();
  const check = useReveal();
  const process = useReveal();
  const docs = useReveal();

  return (
    <>
      <section className="hero">
        <div className="wrap hero__grid">
          <div>
            <h1>Scholarships, from application to bank transfer.</h1>
            <p className="lede">
              See which of six schemes you qualify for, apply online, and follow each verification stage until the
              grant is paid.
            </p>
            <div className="cta-row">
              <Link to="/apply" className="btn btn--solid">
                Start application
              </Link>
              <Link to="/track" className="btn btn--line">
                Track status
              </Link>
            </div>
          </div>
          <HeroArt />
        </div>
      </section>

      <section className="sec reveal" ref={schemes} aria-labelledby="schemes-h">
        <div className="wrap">
          <div className="sec-head">
            <h2 id="schemes-h" className="tag">Schemes</h2>
            <p>Six grants with their own minimum CGPA, attendance and income limit. Open one to apply.</p>
          </div>
          <ul className="grid2">
            {ranked.map((s, i) => (
              <li key={s.id}>
                <SchemeCard scheme={s} dark={i % 4 === 1 || i % 4 === 2} />
              </li>
            ))}
          </ul>
        </div>
      </section>

      <section className="sec reveal" ref={check} aria-labelledby="check-h">
        <div className="wrap">
          <div className="sec-head">
            <h2 id="check-h" className="tag">Eligibility</h2>
            <p>Enter three numbers to see which schemes you meet today.</p>
          </div>
          <div className="panel">
            <form className="panel__form" onSubmit={(e) => e.preventDefault()}>
              <NumberInput label="CGPA (out of 10)" value={gpa} onChange={(v) => setGpa(v ?? 0)} min={0} max={10} step={0.1} />
              <NumberInput label="Attendance (%)" value={attendance} onChange={(v) => setAttendance(v ?? 0)} min={0} max={100} step={1} />
              <NumberInput label="Family income per year (₹)" value={income} onChange={(v) => setIncome(v ?? 0)} min={0} step={10000} />
            </form>
            <div className="panel__out" aria-live="polite">
              {eligible.length > 0 ? (
                <>
                  <p className="panel__count">
                    You meet the minimums for <span className="hl">{eligible.length} of {SCHEMES.length}</span> schemes.
                  </p>
                  <ul className="panel__list">
                    {eligible.map(({ scheme }) => (
                      <li key={scheme.id}>
                        <Link to={`/apply?scheme=${encodeURIComponent(scheme.id)}`}>
                          <span className="panel__ico"><SchemeIcon id={scheme.id} /></span>
                          <span>{scheme.name}</span>
                          <b>{scheme.amount}</b>
                          <ArrowUpRightIcon aria-hidden="true" width={18} height={18} />
                        </Link>
                      </li>
                    ))}
                  </ul>
                </>
              ) : (
                <p className="panel__count">
                  No scheme fits these numbers yet. The closest is <span className="hl">{closest.scheme.name}</span>, which needs{' '}
                  {closest.gaps.join(', ')}.
                </p>
              )}
              <p className="panel__note">Meeting the minimums does not guarantee a grant. The committee ranks applicants by merit index.</p>
            </div>
          </div>
        </div>
      </section>

      <section className="sec reveal" ref={process} aria-labelledby="process-h">
        <div className="wrap">
          <div className="sec-head">
            <h2 id="process-h" className="tag">Process</h2>
            <p>Five stages after you submit. The tracker shows which one you are in.</p>
          </div>
          <ol className="stages">
            {STAGES.map(([t, d], i) => (
              <li key={t}>
                <span className="stages__n">Stage {i + 1}</span>
                <h3>{t}</h3>
                <p>{d}</p>
              </li>
            ))}
          </ol>
        </div>
      </section>

      <section className="sec reveal" ref={docs} aria-labelledby="docs-h" style={{ paddingTop: 0 }}>
        <div className="wrap">
          <div className="sec-head">
            <h2 id="docs-h" className="tag">Documents</h2>
            <p>Keep scans of these four ready. The form takes about ten minutes.</p>
          </div>
          <ul className="docs">
            {DOCUMENTS.map(([Icon, t, d]) => (
              <li key={t}>
                <Icon aria-hidden="true" width={28} height={28} />
                <h3>{t}</h3>
                <p>{d}</p>
              </li>
            ))}
          </ul>
        </div>
      </section>
    </>
  );
}
