// Composite Merit Index (0-100): 50% GPA (out of 10), 30% ECA score, 20% attendance.
// Matches calculateMeritScore in the Java service so the UI preview equals the stored score.
export function calculateMeritScore(gpa: number, eca: number, attendance: number): number {
  const normGpa = Math.min(Math.max(gpa / 10, 0), 1) * 100;
  const score = normGpa * 0.5 + eca * 0.3 + attendance * 0.2;
  return Math.round(score * 100) / 100;
}

import { SCHEMES, type Scheme } from './schemes';

export interface SchemeCheck {
  scheme: Scheme;
  // Human-readable reasons a scheme is not met; empty when eligible.
  gaps: string[];
}

// Checks every scheme against the three numbers it publishes: min CGPA, min attendance, max income.
export function checkSchemes(gpa: number, income: number, attendance: number): SchemeCheck[] {
  return SCHEMES.map((scheme) => {
    const gaps: string[] = [];
    if (gpa < scheme.minGpa) gaps.push(`CGPA ${scheme.minGpa} or higher`);
    if (attendance < scheme.minAttendance) gaps.push(`attendance ${scheme.minAttendance}% or higher`);
    if (income > scheme.maxIncome) gaps.push(`family income up to ₹${scheme.maxIncome.toLocaleString('en-IN')}`);
    return { scheme, gaps };
  });
}
