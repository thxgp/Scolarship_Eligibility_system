import { useState } from 'react';
import { Badge } from '@astryxdesign/core/Badge';
import { Card } from '@astryxdesign/core/Card';
import { Center } from '@astryxdesign/core/Center';
import { Divider } from '@astryxdesign/core/Divider';
import { Grid } from '@astryxdesign/core/Grid';
import { Heading } from '@astryxdesign/core/Heading';
import { Section } from '@astryxdesign/core/Section';
import { Text } from '@astryxdesign/core/Text';
import { VStack } from '@astryxdesign/core/VStack';
import { SCHEMES } from '../schemes';
import { SchemeCard } from '../components/SchemeCard';

const ALL = 'ALL';

// Legacy display labels for the category values in SCHEMES.
const CATEGORY_LABELS: Record<string, string> = {
  'Merit-Based': 'Merit-Based',
  'Need-Based': 'Need & Income Aid',
  'Sports/ECA': 'Sports & Talent',
};

const CATEGORIES = [...new Set(SCHEMES.map((s) => s.category))];

const DOCUMENTS = [
  {
    title: 'Academic Marksheets',
    description:
      'Latest semester or qualifying 10+2 / Diploma grade sheet verified by your college principal.',
  },
  {
    title: 'Income Certificate',
    description:
      'Issued by Tehsildar / Competent Revenue Authority within the current financial year.',
  },
  {
    title: 'College Bonafide & ID',
    description:
      'Official bonafide student letter stating current course, roll number, and attendance record.',
  },
  {
    title: 'Aadhaar Linked Bank Passbook',
    description:
      'Savings bank account linked with Aadhaar for Direct Benefit Transfer (DBT) grant disbursement.',
  },
];

export function SchemesPage() {
  const [category, setCategory] = useState<string>(ALL);

  const visible =
    category === ALL ? SCHEMES : SCHEMES.filter((s) => s.category === category);

  return (
    <VStack gap={0}>
      <Section variant="transparent" padding={0} paddingBlock={6}>
        <VStack gap={3} align="start">
          <Badge label="Academic year 2026 to 2027" variant="info" />
          <Heading level={1}>Scholarship schemes</Heading>
          <Text as="p" type="large" color="secondary">
            Six schemes, each with its own minimum CGPA, attendance and income limit. Check the numbers, then apply.
          </Text>
        </VStack>
      </Section>

      <Section variant="transparent" padding={0} paddingBlock={6}>
        <VStack gap={6}>
          <VStack gap={2}>
            <Text type="label" as="p">
              Category
            </Text>
            <div className="chips" role="group" aria-label="Filter schemes by category">
              {[ALL, ...CATEGORIES].map((cat) => (
                <button
                  key={cat}
                  type="button"
                  className="chip"
                  aria-pressed={category === cat}
                  onClick={() => setCategory(cat)}
                >
                  {cat === ALL ? `All schemes (${SCHEMES.length})` : CATEGORY_LABELS[cat] ?? cat}
                </button>
              ))}
            </div>
          </VStack>

          {visible.length === 0 ? (
            <Center padding={8}>
              <Text type="supporting" color="secondary">
                No scholarship schemes match this category.
              </Text>
            </Center>
          ) : (
            <ul className="grid2">
                {visible.map((scheme, i) => (
                  <li key={scheme.id}>
                    <SchemeCard scheme={scheme} dark={i % 4 === 1 || i % 4 === 2} kindLabel={CATEGORY_LABELS[scheme.category]} />
                  </li>
                ))}
              </ul>
          )}
        </VStack>
      </Section>

      <Section variant="muted" padding={6}>
        <VStack gap={6}>
          <VStack gap={2}>
            <Heading level={2}>Documents to keep ready</Heading>
            <Text as="p" type="body" color="secondary">
              Keep scans of these four documents ready before you start the application.
            </Text>
          </VStack>
          <Grid columns={{ minWidth: 240 }} gap={4}>
            {DOCUMENTS.map((doc) => (
              <Card key={doc.title} padding={5}>
                <VStack gap={2}>
                  <Heading level={3}>{doc.title}</Heading>
                  <Text as="p" type="body" color="secondary">
                    {doc.description}
                  </Text>
                </VStack>
              </Card>
            ))}
          </Grid>
        </VStack>
      </Section>

      <Section variant="transparent" padding={0} paddingBlock={6}>
        <VStack gap={4}>
          <Divider />
          <Text type="supporting" color="secondary">
            © 2026 EduScholar National Portal. Department of Higher Education.
          </Text>
        </VStack>
      </Section>
    </VStack>
  );
}
