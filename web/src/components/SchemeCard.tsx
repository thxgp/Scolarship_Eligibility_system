import { Link } from 'react-router';
import { ArrowUpRightIcon } from '@heroicons/react/24/outline';
import type { Scheme } from '../schemes';
import { SchemeIcon } from './art';

export const lakhs = (n: number) => `₹${(n / 100000).toFixed(1)}L`;

// White and black cards alternate across the grid; `dark` is chosen by the caller from the card's position.
export function SchemeCard({ scheme, dark, kindLabel }: { scheme: Scheme; dark: boolean; kindLabel?: string }) {
  return (
    <Link
      to={`/apply?scheme=${encodeURIComponent(scheme.id)}`}
      className={`scard ${dark ? 'scard--dark' : ''}`}
    >
      <span className="scard__kind">{kindLabel ?? scheme.category}</span>
      <h3 className="scard__title">
        <span className="hl">{scheme.name}</span>
      </h3>
      <div className="scard__art">
        <SchemeIcon id={scheme.id} />
      </div>
      <p>{scheme.description}</p>
      <ul className="scard__rules">
        <li>CGPA {scheme.minGpa}+</li>
        <li>Attendance {scheme.minAttendance}%+</li>
        <li>Income up to {lakhs(scheme.maxIncome)}</li>
      </ul>
      <span className="scard__foot">
        <span className="scard__amt">
          {scheme.amount} <small>{scheme.period}</small>
        </span>
        <span className="scard__go">
          Apply
          <ArrowUpRightIcon aria-hidden="true" width={16} height={16} />
        </span>
      </span>
    </Link>
  );
}
