export interface Scheme {
  id: string;
  name: string;
  category: string;
  amount: string;
  numericAmount: number;
  period: string;
  minGpa: number;
  maxIncome: number;
  minAttendance: number;
  description: string;
  tags: string[];
}

// Eligibility thresholds and amounts for each scholarship scheme offered by the portal.
export const SCHEMES: Scheme[] = [
  {
    id: 'SCH-STEM',
    name: 'STEM Innovation & Research Grant',
    category: 'Merit-Based',
    amount: '₹75,000',
    numericAmount: 75000,
    period: 'per academic year',
    minGpa: 8.5,
    maxIncome: 800000,
    minAttendance: 85,
    description: 'For undergraduate and postgraduate students in science, technology, engineering and maths who can show research work.',
    tags: ['Engineering', 'Sciences', 'Innovation'],
  },
  {
    id: 'SCH-MCM',
    name: 'Merit-cum-Means Excellence Fellowship',
    category: 'Need-Based',
    amount: '₹50,000',
    numericAmount: 50000,
    period: 'per academic year',
    minGpa: 7.5,
    maxIncome: 350000,
    minAttendance: 80,
    description: 'Covers tuition for strong students from low-income families, so they do not need a loan.',
    tags: ['Financial Aid', 'All Degrees', 'Full Tuition'],
  },
  {
    id: 'SCH-WOMEN',
    name: 'Women in Technology & Leadership Award',
    category: 'Merit-Based',
    amount: '₹60,000',
    numericAmount: 60000,
    period: 'per academic year',
    minGpa: 8.0,
    maxIncome: 600000,
    minAttendance: 80,
    description: 'For women studying computing, AI or applied sciences.',
    tags: ['Women Scholars', 'Tech & AI', 'Mentorship'],
  },
  {
    id: 'SCH-FIRSTGEN',
    name: 'First-Generation Higher Education Grant',
    category: 'Need-Based',
    amount: '₹45,000',
    numericAmount: 45000,
    period: 'per academic year',
    minGpa: 7.0,
    maxIncome: 250000,
    minAttendance: 75,
    description: 'For students who are the first in their family to enrol in a degree. Covers books and living costs.',
    tags: ['First Generation', 'Undergraduate', 'Books & Living'],
  },
  {
    id: 'SCH-SPORTS',
    name: 'Sports & Arts Fellowship',
    category: 'Sports/ECA',
    amount: '₹40,000',
    numericAmount: 40000,
    period: 'per academic year',
    minGpa: 6.5,
    maxIncome: 900000,
    minAttendance: 75,
    description: 'For students with state or national results in sport, the arts or community leadership.',
    tags: ['Athletics', 'Cultural', 'National Rank'],
  },
  {
    id: 'SCH-RENEWAL',
    name: 'National Doctoral Research Stipend',
    category: 'Merit-Based',
    amount: '₹90,000',
    numericAmount: 90000,
    period: 'per academic year',
    minGpa: 8.8,
    maxIncome: 1000000,
    minAttendance: 90,
    description: 'Stipend for full-time postgraduate and PhD students doing peer-reviewed, patentable research.',
    tags: ['Doctoral', 'Postgraduate', 'Stipend'],
  },
];
