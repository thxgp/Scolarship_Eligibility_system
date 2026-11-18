import { useEffect, useState, type FormEvent } from 'react';
import { useSearchParams } from 'react-router';
import { Banner } from '@astryxdesign/core/Banner';
import { Badge } from '@astryxdesign/core/Badge';
import { Button } from '@astryxdesign/core/Button';
import { Card } from '@astryxdesign/core/Card';
import { Grid } from '@astryxdesign/core/Grid';
import { Heading } from '@astryxdesign/core/Heading';
import { Link } from '@astryxdesign/core/Link';
import { Section } from '@astryxdesign/core/Section';
import { Spinner } from '@astryxdesign/core/Spinner';
import { Step, Stepper } from '@astryxdesign/core/Stepper';
import { Text } from '@astryxdesign/core/Text';
import { TextInput } from '@astryxdesign/core/TextInput';
import { VStack } from '@astryxdesign/core/VStack';
import { HStack } from '@astryxdesign/core/HStack';
import { trackApplication, type TrackResult } from '../api';
import { SCHEMES } from '../schemes';

type LookupState =
  | { kind: 'idle' }
  | { kind: 'loading' }
  | { kind: 'error'; message: string }
  | { kind: 'notFound' }
  | { kind: 'found'; result: TrackResult };

const STATUS_VARIANT: Record<string, 'warning' | 'info' | 'success' | 'error'> = {
  Pending: 'warning',
  'Under Review': 'info',
  Approved: 'success',
  Rejected: 'error',
};

interface StageView {
  name: string;
  desc: string;
  completed: boolean;
}

function buildStages(status: string): StageView[] {
  return [
    { name: 'Application Submitted', desc: 'Received online', completed: true },
    { name: 'Document Verification', desc: 'Authenticated by Registrar', completed: status !== 'Pending' },
    {
      name: 'Academic & Financial Audit',
      desc: 'Income & CGPA validation',
      completed: status === 'Approved' || status === 'Under Review',
    },
    { name: 'Committee Review', desc: 'Allocation approval', completed: status === 'Approved' },
    { name: 'Grant Disbursement', desc: 'Direct Benefit Transfer (DBT)', completed: status === 'Approved' },
  ];
}

export function TrackPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const query = (searchParams.get('id') ?? '').trim().toUpperCase();
  const [input, setInput] = useState(query);
  const [inputError, setInputError] = useState<string | null>(null);
  const [attempt, setAttempt] = useState(0);
  const [lookup, setLookup] = useState<LookupState>({ kind: 'idle' });

  useEffect(() => {
    if (!query) {
      setLookup({ kind: 'idle' });
      return;
    }
    let cancelled = false;
    setLookup({ kind: 'loading' });
    trackApplication(query)
      .then((result) => {
        if (cancelled) return;
        setLookup(result ? { kind: 'found', result } : { kind: 'notFound' });
      })
      .catch((err: unknown) => {
        if (cancelled) return;
        setLookup({
          kind: 'error',
          message: err instanceof Error ? err.message : 'Request failed',
        });
      });
    return () => {
      cancelled = true;
    };
  }, [query, attempt]);

  function handleSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const id = input.trim().toUpperCase();
    if (!id) {
      setInputError('Enter your roll number or student ID.');
      return;
    }
    setInputError(null);
    setInput(id);
    setSearchParams({ id });
    setAttempt((n) => n + 1);
  }

  function retry() {
    setAttempt((n) => n + 1);
  }

  return (
    <Section variant="transparent" padding={0} paddingBlock={6}>
      <VStack gap={4}>
        <Heading level={1}>Track your application</Heading>
        <Text type="supporting">
          Enter your roll number or student ID, for example 26MCAD18, to see which stage it is in.
        </Text>

        <Card padding={4}>
          <form onSubmit={handleSubmit} noValidate>
            <HStack gap={3} align="end">
              <TextInput
                label="Roll number or student ID"
                value={input}
                onChange={(value) => {
                  setInput(value);
                  if (inputError) setInputError(null);
                }}
                placeholder="e.g. 26MCAD18"
                isRequired
                status={inputError ? { type: 'error', message: inputError } : undefined}
              />
              <Button
                type="submit"
                variant="primary"
                label="Check application status"
                isLoading={lookup.kind === 'loading'}
              />
            </HStack>
          </form>
        </Card>

        {lookup.kind === 'loading' && <Spinner label="Looking up your application" />}

        {lookup.kind === 'error' && (
          <Banner
            status="error"
            title="Could not reach the scholarship server"
            description={`${lookup.message}. Check that the server is running, then try again.`}
            endContent={<Button label="Retry" variant="secondary" clickAction={retry} />}
          />
        )}

        {lookup.kind === 'notFound' && (
          <Banner
            status="warning"
            title="No application found"
            description={`No application matches "${query}". Check the ID, or apply first.`}
            endContent={<Link href="/apply">Apply online</Link>}
          />
        )}

        {lookup.kind === 'found' && <ResultView result={lookup.result} />}
      </VStack>
    </Section>
  );
}

function ResultView({ result }: { result: TrackResult }) {
  const { student, inQueue } = result;
  const scheme = SCHEMES.find((s) => s.name === student.category);
  const stages = buildStages(student.status);
  const firstIncomplete = stages.findIndex((s) => !s.completed);
  const activeStep = firstIncomplete === -1 ? stages.length - 1 : firstIncomplete;

  return (
    <VStack gap={4}>
      <Card padding={4}>
        <VStack gap={3}>
          <HStack gap={3} align="center">
            <Heading level={2}>{student.name}</Heading>
            <Badge variant={STATUS_VARIANT[student.status] ?? 'neutral'} label={student.status} />
          </HStack>
          <Grid columns={{ minWidth: 180 }} gap={3}>
            <Field label="Student ID" value={student.studentId} />
            <Field label="Scheme" value={scheme?.name ?? student.category} />
            <Field label="CGPA" value={student.gpa.toFixed(2)} />
            <Field label="Merit score" value={student.meritScore.toFixed(2)} />
            <Field label="Grant amount" value={scheme?.amount ?? 'Not available'} />
            <Field label="Queue" value={inQueue ? 'In queue' : 'Not in queue'} />
          </Grid>
        </VStack>
      </Card>

      <Card padding={4}>
        <VStack gap={3}>
          <Heading level={3}>Application stages</Heading>
          <Stepper activeStep={activeStep} orientation="vertical" label="Application stages">
            {stages.map((stage, i) => (
              <Step
                key={stage.name}
                step={i}
                label={stage.name}
                description={stage.desc}
                status={stage.completed ? 'success' : undefined}
                endContent={
                  <Text type="supporting">{stage.completed ? 'Completed' : 'Not yet completed'}</Text>
                }
              />
            ))}
          </Stepper>
        </VStack>
      </Card>
    </VStack>
  );
}

function Field({ label, value }: { label: string; value: string }) {
  return (
    <VStack gap={1}>
      <Text type="supporting">{label}</Text>
      <Text weight="semibold">{value}</Text>
    </VStack>
  );
}
