import {useMemo, useState} from 'react';
import {useSearchParams} from 'react-router';
import {Banner} from '@astryxdesign/core/Banner';
import {Button} from '@astryxdesign/core/Button';
import {Card} from '@astryxdesign/core/Card';
import {CheckboxInput} from '@astryxdesign/core/CheckboxInput';
import {FormLayout} from '@astryxdesign/core/FormLayout';
import {
  Layout,
  LayoutContent,
  LayoutFooter,
  LayoutHeader,
} from '@astryxdesign/core/Layout';
import {MetadataList, MetadataListItem} from '@astryxdesign/core/MetadataList';
import {NumberInput} from '@astryxdesign/core/NumberInput';
import {Selector} from '@astryxdesign/core/Selector';
import {HStack, StackItem, VStack} from '@astryxdesign/core/Stack';
import {Step, Stepper} from '@astryxdesign/core/Stepper';
import {Heading, Text} from '@astryxdesign/core/Text';
import {TextInput} from '@astryxdesign/core/TextInput';
import {
  ApiError,
  submitApplication,
  type ApplyResult,
} from '../api';
import {SCHEMES, type Scheme} from '../schemes';
import {calculateMeritScore} from '../eligibility';

// ── Data ──────────────────────────────────────────────────────────────────────

const STEPS = ['Personal', 'Academic', 'Scheme', 'Review & submit'];

const SCHEME_OPTIONS = SCHEMES.map(s => ({
  value: s.id,
  label: `${s.name} (${s.amount} / yr), min CGPA ${s.minGpa}`,
}));

interface FormState {
  name: string;
  email: string;
  phone: string;
  college: string;
  course: string;
  roll: string;
  gpa: number | null;
  attendance: number | null;
  eca: number | null;
  income: number | null;
  scheme: string;
  declared: boolean;
}

const EMPTY: FormState = {
  name: '',
  email: '',
  phone: '',
  college: '',
  course: '',
  roll: '',
  gpa: null,
  attendance: null,
  eca: null,
  income: null,
  scheme: '',
  declared: false,
};

// Fields validated on each step. Roll number is optional and not listed.
const STEP_FIELDS: Array<Array<keyof FormState>> = [
  ['name', 'email', 'phone', 'college', 'course'],
  ['gpa', 'attendance', 'eca', 'income'],
  ['scheme'],
  ['declared'],
];

type Errors = Partial<Record<keyof FormState, string>>;

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;

const inRange = (v: number | null, min: number, max: number) =>
  v !== null && v >= min && v <= max;

function validate(f: FormState): Errors {
  const e: Errors = {};
  if (!f.name.trim()) e.name = 'Enter your full name.';
  if (!f.email.trim()) e.email = 'Enter an email address.';
  else if (!EMAIL_PATTERN.test(f.email.trim())) e.email = 'Enter a valid email address.';
  if (!f.phone.trim()) e.phone = 'Enter a mobile number.';
  if (!f.college.trim()) e.college = 'Enter your college or university name.';
  if (!f.course.trim()) e.course = 'Enter your course and branch.';
  if (!inRange(f.gpa, 0, 10)) e.gpa = 'Enter a CGPA between 0 and 10.';
  if (!inRange(f.attendance, 0, 100)) e.attendance = 'Enter attendance between 0 and 100.';
  if (!inRange(f.eca, 0, 100)) e.eca = 'Enter an ECA score between 0 and 100.';
  if (f.income === null || f.income < 0) e.income = 'Enter the annual family income (0 or more).';
  if (!f.scheme) e.scheme = 'Select a scheme.';
  if (!f.declared) e.declared = 'Confirm the declaration to submit.';
  return e;
}

// Shortfalls are shown as a warning only; the legacy page did not block on them.
function shortfalls(f: FormState, s: Scheme): string[] {
  const out: string[] = [];
  if (f.gpa !== null && f.gpa < s.minGpa) out.push(`CGPA below ${s.minGpa}`);
  if (f.attendance !== null && f.attendance < s.minAttendance) out.push(`attendance below ${s.minAttendance}%`);
  if (f.income !== null && f.income > s.maxIncome) out.push(`income above ₹${s.maxIncome.toLocaleString('en-IN')}`);
  return out;
}

const num = (n: number | null, suffix = '') => (n === null ? 'Not given' : `${n}${suffix}`);
const inr = (n: number | null) => (n === null ? 'Not given' : `₹${n.toLocaleString('en-IN')}`);

// ── Page ──────────────────────────────────────────────────────────────────────

export function ApplyPage() {
  const [params] = useSearchParams();
  // Home and schemes pages link here with ?scheme=<id or name> to preselect a scheme.
  const requested = params.get('scheme');
  const [form, setForm] = useState<FormState>(() => ({
    ...EMPTY,
    scheme: SCHEMES.find(s => s.id === requested || s.name === requested)?.id ?? '',
  }));
  const [step, setStep] = useState(0);
  // Steps the user has tried to leave; errors stay hidden until then.
  const [attempted, setAttempted] = useState<ReadonlySet<number>>(new Set());
  const [submitTried, setSubmitTried] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [serverError, setServerError] = useState<string | null>(null);
  const [result, setResult] = useState<ApplyResult | null>(null);

  const errors = useMemo(() => validate(form), [form]);
  const stepHasErrors = (index: number) => STEP_FIELDS[index].some(k => errors[k]);
  const fieldStatus = (key: keyof FormState) => {
    const stepIndex = STEP_FIELDS.findIndex(keys => keys.includes(key));
    const message = attempted.has(stepIndex) ? errors[key] : undefined;
    return message ? {type: 'error' as const, message} : undefined;
  };

  const set =
    <K extends keyof FormState>(key: K) =>
    (value: FormState[K]) =>
      setForm(f => ({...f, [key]: value}));

  const next = () => {
    setAttempted(prev => new Set(prev).add(step));
    if (!stepHasErrors(step)) setStep(step + 1);
  };

  // Steps are only reachable backward from the stepper; forward moves go through Next.
  const goBack = (index: number) => {
    if (index < step) setStep(index);
  };

  const submit = async () => {
    setSubmitTried(true);
    setAttempted(new Set(STEPS.map((_, i) => i)));
    const firstBad = STEP_FIELDS.findIndex((_, i) => stepHasErrors(i));
    if (firstBad !== -1) {
      setStep(firstBad);
      return;
    }

    const scheme = SCHEMES.find(s => s.id === form.scheme);
    if (
      scheme === undefined ||
      form.gpa === null ||
      form.attendance === null ||
      form.eca === null ||
      form.income === null
    ) {
      return;
    }

    setSubmitting(true);
    setServerError(null);
    try {
      const res = await submitApplication({
        name: form.name.trim(),
        studentId: form.roll.trim().toUpperCase() || undefined,
        gpa: form.gpa,
        familyIncome: form.income,
        attendancePercentage: form.attendance,
        extracurricularScore: form.eca,
        category: scheme.name,
      });
      setResult(res);
    } catch (err) {
      setServerError(
        err instanceof ApiError
          ? err.message
          : 'Cannot reach the scholarship server. Your answers are kept; try again.',
      );
    } finally {
      setSubmitting(false);
    }
  };

  const scheme = SCHEMES.find(s => s.id === form.scheme);
  const merit =
    form.gpa !== null && form.attendance !== null && form.eca !== null
      ? calculateMeritScore(form.gpa, form.eca, form.attendance).toFixed(2)
      : '—';
  const isLastStep = step === STEPS.length - 1;
  const issues = STEP_FIELDS.flat()
    .map(k => errors[k])
    .filter((m): m is string => Boolean(m));
  const showSummary = submitTried && issues.length > 0;

  if (result) {
    return (
      <Layout
        height="auto"
        contentWidth={720}
        padding={5}
        content={
          <LayoutContent>
            <VStack gap={5}>
              <Banner
                status="success"
                title="Application submitted"
                description={result.message}
              />
              <MetadataList columns={2}>
                <MetadataListItem label="Tracking ID">{result.studentId}</MetadataListItem>
                <MetadataListItem label="Queue position">{String(result.queuePosition)}</MetadataListItem>
              </MetadataList>
              <HStack>
                <Button
                  label="Track application status"
                  variant="primary"
                  href={`/track?id=${encodeURIComponent(result.studentId)}`}
                />
              </HStack>
            </VStack>
          </LayoutContent>
        }
      />
    );
  }

  return (
    <Layout
      height="auto"
      contentWidth={720}
      padding={5}
      defaultHasDividers
      header={
        <LayoutHeader hasDivider={false}>
          <VStack gap={5}>
            <VStack gap={1}>
              <Heading level={1}>Scholarship application</Heading>
              <Text type="supporting" color="secondary">
                Academic year 2026 to 2027. Your application goes into the verification queue.
              </Text>
            </VStack>
            <Stepper
              activeStep={step}
              orientation="horizontal"
              onStepClick={goBack}
              label="Application progress"
              indicatorPosition="on-track">
              {STEPS.map((label, i) => (
                <Step
                  key={label}
                  step={i}
                  label={label}
                  status={attempted.has(i) && stepHasErrors(i) ? 'error' : undefined}
                />
              ))}
            </Stepper>
          </VStack>
        </LayoutHeader>
      }
      content={
        <LayoutContent>
          <VStack gap={5}>
            {showSummary && (
              <Banner
                status="error"
                title="Fix these before submitting"
                collapsible={false}
                description={
                  <VStack gap={1}>
                    {issues.map(m => (
                      <Text key={m} type="supporting">{m}</Text>
                    ))}
                  </VStack>
                }
              />
            )}

            {step === 0 && (
              <VStack gap={5}>
                <VStack gap={1}>
                  <Heading level={2}>Your details</Heading>
                  <Text type="supporting" color="secondary">
                    Use your name as on your 10th certificate. Verification notices go to this email.
                  </Text>
                </VStack>
                <FormLayout defaultOptionality="optional">
                  <TextInput
                    label="Full name (as per 10th certificate)"
                    isRequired
                    value={form.name}
                    onChange={set('name')}
                    placeholder="e.g. Priya Sharma"
                    status={fieldStatus('name')}
                  />
                  <TextInput
                    label="Email address"
                    type="email"
                    isRequired
                    value={form.email}
                    onChange={set('email')}
                    placeholder="e.g. student@college.edu"
                    status={fieldStatus('email')}
                  />
                  <TextInput
                    label="Mobile number (Aadhaar linked)"
                    type="tel"
                    isRequired
                    value={form.phone}
                    onChange={set('phone')}
                    placeholder="e.g. +91 98765 43210"
                    status={fieldStatus('phone')}
                  />
                  <TextInput
                    label="College / university name"
                    isRequired
                    value={form.college}
                    onChange={set('college')}
                    placeholder="e.g. RV College of Engineering"
                    status={fieldStatus('college')}
                  />
                  <TextInput
                    label="Course / degree and branch"
                    isRequired
                    value={form.course}
                    onChange={set('course')}
                    placeholder="e.g. B.Tech Computer Science"
                    status={fieldStatus('course')}
                  />
                  <TextInput
                    label="Student ID / university roll no."
                    value={form.roll}
                    onChange={set('roll')}
                    placeholder="e.g. 26MCAD18"
                    description="Optional. Saved in uppercase and used as your tracking ID."
                  />
                </FormLayout>
              </VStack>
            )}

            {step === 1 && (
              <VStack gap={5}>
                <VStack gap={1}>
                  <Heading level={2}>Academics and income</Heading>
                  <Text type="supporting" color="secondary">
                    Enter your latest cumulative figures. Scores are checked against the ranges shown.
                  </Text>
                </VStack>
                <FormLayout defaultOptionality="optional">
                  <NumberInput
                    label="Cumulative GPA (10-point scale)"
                    isRequired
                    hasClear
                    min={0}
                    max={10}
                    step={0.01}
                    value={form.gpa}
                    onChange={set('gpa')}
                    placeholder="e.g. 8.85"
                    status={fieldStatus('gpa')}
                  />
                  <NumberInput
                    label="Class attendance"
                    isRequired
                    hasClear
                    min={0}
                    max={100}
                    step={0.5}
                    units="%"
                    value={form.attendance}
                    onChange={set('attendance')}
                    placeholder="e.g. 85.0"
                    status={fieldStatus('attendance')}
                  />
                  <NumberInput
                    label="Extracurricular / co-curricular rating (0 to 100)"
                    isRequired
                    isIntegerOnly
                    hasClear
                    min={0}
                    max={100}
                    value={form.eca}
                    onChange={set('eca')}
                    placeholder="e.g. 75"
                    description="Sports, Olympiads, NSS/NCC, national or state hackathons, student council service."
                    status={fieldStatus('eca')}
                  />
                  <NumberInput
                    label="Annual family income (₹)"
                    isRequired
                    hasClear
                    min={0}
                    step={10000}
                    value={form.income}
                    onChange={set('income')}
                    placeholder="e.g. 240000"
                    description="As certified on the official revenue certificate."
                    status={fieldStatus('income')}
                  />
                </FormLayout>
              </VStack>
            )}

            {step === 2 && (
              <VStack gap={5}>
                <VStack gap={1}>
                  <Heading level={2}>Scholarship scheme</Heading>
                  <Text type="supporting" color="secondary">
                    Pick a scheme. Its grant and minimums appear below.
                  </Text>
                </VStack>
                <FormLayout defaultOptionality="optional">
                  <Selector
                    label="Target scholarship scheme"
                    isRequired
                    options={SCHEME_OPTIONS}
                    value={form.scheme}
                    onChange={set('scheme')}
                    placeholder="Select a scheme"
                    status={fieldStatus('scheme')}
                  />
                </FormLayout>
                {scheme && (
                  <Card padding={4}>
                    <VStack gap={3}>
                      <MetadataList title={scheme.name} columns={2}>
                        <MetadataListItem label="Grant amount">{`${scheme.amount} ${scheme.period}`}</MetadataListItem>
                        <MetadataListItem label="Minimum CGPA">{String(scheme.minGpa)}</MetadataListItem>
                        <MetadataListItem label="Minimum attendance">{`${scheme.minAttendance}%`}</MetadataListItem>
                        <MetadataListItem label="Maximum annual income">{inr(scheme.maxIncome)}</MetadataListItem>
                      </MetadataList>
                      <Text type="supporting" color="secondary">{scheme.description}</Text>
                    </VStack>
                  </Card>
                )}
                {scheme && shortfalls(form, scheme).length > 0 && (
                  <Banner
                    status="warning"
                    title="Below this scheme's eligibility limits"
                    collapsible={false}
                    description={`You can still submit. The committee will review: ${shortfalls(form, scheme).join('; ')}.`}
                  />
                )}
              </VStack>
            )}

            {step === 3 && (
              <VStack gap={5}>
                {serverError && (
                  <Banner
                    status="error"
                    title="Application not submitted"
                    collapsible={false}
                    description={serverError}
                  />
                )}
                <VStack gap={1}>
                  <Heading level={2}>Review and submit</Heading>
                  <Text type="supporting" color="secondary">
                    Check your details. The merit index below is an estimate from the values you entered.
                  </Text>
                </VStack>
                <Card padding={4}>
                  <VStack gap={1}>
                    <Text type="label">Estimated composite merit index</Text>
                    <Text type="display-3">{merit}</Text>
                    <Text type="supporting" color="secondary">
                      CGPA 50%, co-curricular rating 30%, attendance 20%.
                    </Text>
                  </VStack>
                </Card>
                <MetadataList columns={2}>
                  <MetadataListItem label="Candidate name">{form.name.trim() || 'Not given'}</MetadataListItem>
                  <MetadataListItem label="Target scheme">{scheme?.name ?? 'Not given'}</MetadataListItem>
                  <MetadataListItem label="Email">{form.email.trim() || 'Not given'}</MetadataListItem>
                  <MetadataListItem label="Mobile">{form.phone.trim() || 'Not given'}</MetadataListItem>
                  <MetadataListItem label="Institution">{form.college.trim() || 'Not given'}</MetadataListItem>
                  <MetadataListItem label="Course">{form.course.trim() || 'Not given'}</MetadataListItem>
                  <MetadataListItem label="Student ID">{form.roll.trim().toUpperCase() || 'Not provided'}</MetadataListItem>
                  <MetadataListItem label="Verified CGPA">{num(form.gpa)}</MetadataListItem>
                  <MetadataListItem label="Class attendance">{num(form.attendance, '%')}</MetadataListItem>
                  <MetadataListItem label="Co-curricular rating">{num(form.eca)}</MetadataListItem>
                  <MetadataListItem label="Annual family income">{inr(form.income)}</MetadataListItem>
                </MetadataList>
                <CheckboxInput
                  label="I declare that all marks, income statements and credentials in this application are authentic and true."
                  description="Submitting false information will result in disqualification from the grant."
                  value={form.declared}
                  onChange={set('declared')}
                  isRequired
                  status={fieldStatus('declared')}
                />
              </VStack>
            )}
          </VStack>
        </LayoutContent>
      }
      footer={
        <LayoutFooter>
          <HStack gap={3} vAlign="center">
            <StackItem size="fill">
              <Button label="Back" variant="secondary" isDisabled={step === 0} onClick={() => setStep(step - 1)} />
            </StackItem>
            <Text type="supporting" color="secondary">
              {step + 1} of {STEPS.length}
            </Text>
            <StackItem size="fill">
              <HStack gap={3} hAlign="end">
                {isLastStep ? (
                  <Button
                    label="Submit application"
                    variant="primary"
                    isLoading={submitting}
                    onClick={() => void submit()}
                  />
                ) : (
                  <Button label="Next" variant="primary" onClick={next} />
                )}
              </HStack>
            </StackItem>
          </HStack>
        </LayoutFooter>
      }
    />
  );
}
