import { useCallback, useEffect, useMemo, useState, type ReactNode } from 'react';
import {
  ArrowPathIcon,
  ArrowUturnLeftIcon,
  CheckCircleIcon,
  ClockIcon,
  ExclamationTriangleIcon,
  MagnifyingGlassIcon,
  UsersIcon,
  XCircleIcon,
} from '@heroicons/react/24/outline';
import { AlertDialog } from '@astryxdesign/core/AlertDialog';
import { Badge } from '@astryxdesign/core/Badge';
import { Banner } from '@astryxdesign/core/Banner';
import { Button } from '@astryxdesign/core/Button';
import { Card } from '@astryxdesign/core/Card';
import { EmptyState } from '@astryxdesign/core/EmptyState';
import { Grid } from '@astryxdesign/core/Grid';
import { Heading } from '@astryxdesign/core/Heading';
import { Icon } from '@astryxdesign/core/Icon';
import { Spinner } from '@astryxdesign/core/Spinner';
import { Stack } from '@astryxdesign/core/Stack';
import { Table, pixel, proportional, useTableSortable, useTableSortableState } from '@astryxdesign/core/Table';
import type { TableColumn } from '@astryxdesign/core/Table';
import { Text } from '@astryxdesign/core/Text';
import { TextInput } from '@astryxdesign/core/TextInput';
import { VisuallyHidden } from '@astryxdesign/core/VisuallyHidden';
import {
  getState,
  resetData,
  undoLastAction,
  verifyApplication,
  type ActionEntry,
  type AppState,
  type OperationResult,
  type Student,
} from '../api';

type Decision = 'Approved' | 'Rejected' | 'Under Review';

// The single request in flight. Row actions, undo and reset stay disabled while it is set.
type Busy = { kind: 'verify'; studentId: string; status: Decision } | { kind: 'undo' } | { kind: 'reset' };

type Notice = { tone: 'success' | 'error'; message: string };

// Astryx table rows need an index signature; the api.ts interfaces do not declare one.
type StudentRow = Student & Record<string, unknown>;

const STATUS_VARIANT: Record<string, 'neutral' | 'info' | 'success' | 'warning' | 'error'> = {
  Pending: 'neutral',
  'Under Review': 'warning',
  Approved: 'success',
  Rejected: 'error',
};

function dataColumns(sortableMerit: boolean): TableColumn<StudentRow>[] {
  return [
    { key: 'studentId', header: 'Student ID', width: pixel(120) },
    { key: 'name', header: 'Name', width: proportional(2) },
    { key: 'category', header: 'Scheme', width: proportional(2) },
    { key: 'gpa', header: 'CGPA', align: 'end', renderCell: (s) => s.gpa.toFixed(2) },
    {
      key: 'attendancePercentage',
      header: 'Attendance',
      width: pixel(115),
      align: 'end',
      renderCell: (s) => `${s.attendancePercentage.toFixed(1)}%`,
    },
    {
      key: 'extracurricularScore',
      header: 'ECA',
      align: 'end',
      renderCell: (s) => s.extracurricularScore.toFixed(1),
    },
    {
      key: 'familyIncome',
      header: 'Income',
      width: pixel(125),
      align: 'end',
      renderCell: (s) => `₹${s.familyIncome.toLocaleString('en-IN')}`,
    },
    {
      key: 'meritScore',
      header: 'Merit index',
      width: pixel(115),
      align: 'end',
      sortable: sortableMerit,
      renderCell: (s) => s.meritScore.toFixed(2),
    },
    {
      key: 'status',
      header: 'Status',
      width: pixel(115),
      renderCell: (s) => <Badge label={s.status} variant={STATUS_VARIANT[s.status] ?? 'neutral'} />,
    },
  ];
}

function SummaryTile({ icon, label, value }: { icon: ReactNode; label: string; value: number }) {
  return (
    <Card>
      <Stack direction="vertical" gap={2}>
        {icon}
        <Heading level={3}>{value}</Heading>
        <Text type="supporting" color="secondary">
          {label}
        </Text>
      </Stack>
    </Card>
  );
}

function SummaryTiles({ state }: { state: AppState }) {
  const { queue, students } = state;
  // Under Review can sit in either list: seed data queues it, but any decision moves the item to students.
  const underReview = [...queue, ...students].filter((s) => s.status === 'Under Review').length;
  return (
    <Grid columns={{ minWidth: 200, repeat: 'fit' }} gap={4}>
      <SummaryTile
        icon={<Icon icon={ClockIcon} />}
        label="Pending"
        value={queue.filter((s) => s.status === 'Pending').length}
      />
      <SummaryTile icon={<Icon icon={ExclamationTriangleIcon} />} label="Under Review" value={underReview} />
      <SummaryTile
        icon={<Icon icon={CheckCircleIcon} color="success" />}
        label="Approved"
        value={students.filter((s) => s.status === 'Approved').length}
      />
      <SummaryTile
        icon={<Icon icon={XCircleIcon} color="error" />}
        label="Rejected"
        value={students.filter((s) => s.status === 'Rejected').length}
      />
      <SummaryTile
        icon={<Icon icon={UsersIcon} />}
        label="Total applications"
        value={queue.length + students.length}
      />
    </Grid>
  );
}

type QueueCardProps = {
  queue: Student[];
  busy: Busy | null;
  remarks: string;
  onRemarksChange: (value: string) => void;
  onDecide: (studentId: string, status: Decision) => Promise<void>;
};

function QueueCard({ queue, busy, remarks, onRemarksChange, onDecide }: QueueCardProps) {
  const rows = useMemo<StudentRow[]>(() => queue.map((s) => ({ ...s })), [queue]);
  const columns: TableColumn<StudentRow>[] = [
    ...dataColumns(false),
    {
      key: 'actions',
      header: 'Decision',
      width: pixel(250),
      align: 'end',
      renderCell: (s) => {
        const isDeciding = (status: Decision) =>
          busy?.kind === 'verify' && busy.studentId === s.studentId && busy.status === status;
        return (
          <Stack direction="horizontal" gap={1} hAlign="end" wrap="nowrap">
            <Button
              label={`Approve ${s.name}`}
              size="sm"
              variant="secondary"
              isDisabled={busy !== null}
              isLoading={isDeciding('Approved')}
              onClick={() => onDecide(s.studentId, 'Approved')}
            >
              Approve
            </Button>
            <Button
              label={`Reject ${s.name}`}
              size="sm"
              variant="secondary"
              isDisabled={busy !== null}
              isLoading={isDeciding('Rejected')}
              onClick={() => onDecide(s.studentId, 'Rejected')}
            >
              Reject
            </Button>
            <Button
              label={`Mark under review ${s.name}`}
              size="sm"
              variant="secondary"
              isDisabled={busy !== null}
              isLoading={isDeciding('Under Review')}
              onClick={() => onDecide(s.studentId, 'Under Review')}
            >
              Review
            </Button>
          </Stack>
        );
      },
    },
  ];

  return (
    <Card>
      <Stack direction="vertical" gap={4}>
        <Stack direction="vertical" gap={1}>
          <Heading level={2}>Awaiting Verification</Heading>
          <Text type="supporting" color="secondary">
            The note below is saved with whichever decision you pick.
          </Text>
        </Stack>
        <TextInput
          label="Note for this decision"
          isOptional
          placeholder="Optional. Saved with the decision."
          value={remarks}
          onChange={onRemarksChange}
        />
        {queue.length === 0 ? (
          <EmptyState title="No applications awaiting verification" headingLevel={3} />
        ) : (
          <div className="scroll-x"><Table data={rows} columns={columns} idKey="studentId" density="compact" /></div>
        )}
      </Stack>
    </Card>
  );
}

function DecidedCard({ students }: { students: Student[] }) {
  const [query, setQuery] = useState('');
  const rows = useMemo<StudentRow[]>(() => {
    const needle = query.trim().toLowerCase();
    const matching = needle
      ? students.filter((s) => s.name.toLowerCase().includes(needle) || s.studentId.toLowerCase().includes(needle))
      : students;
    return matching.map((s) => ({ ...s }));
  }, [students, query]);
  const { sortedData, sortConfig } = useTableSortableState<StudentRow>({
    data: rows,
    defaultSort: [{ sortKey: 'meritScore', direction: 'descending' }],
  });
  const sortable = useTableSortable<StudentRow>(sortConfig);
  const columns = useMemo(() => dataColumns(true), []);

  let content: ReactNode;
  if (students.length === 0) {
    content = <EmptyState title="No decided applications yet" headingLevel={3} />;
  } else if (sortedData.length === 0) {
    content = <EmptyState title="No applications match your search." headingLevel={3} />;
  } else {
    content = (
      <div className="scroll-x"><Table data={sortedData} columns={columns} idKey="studentId" density="compact" plugins={{ sortable }} /></div>
    );
  }

  return (
    <Card>
      <Stack direction="vertical" gap={4}>
        <Stack direction="vertical" gap={1}>
          <Heading level={2}>Decided Applications</Heading>
          <Text type="supporting" color="secondary">
            Approved, rejected and under-review applications. Click Merit index to sort.
          </Text>
        </Stack>
        <TextInput
          type="search"
          label="Search by name or ID"
          startIcon={MagnifyingGlassIcon}
          hasClear
          value={query}
          onChange={setQuery}
        />
        {content}
      </Stack>
    </Card>
  );
}

function ActivityCard({ stack, busy, onUndo }: { stack: ActionEntry[]; busy: Busy | null; onUndo: () => void }) {
  // The server lists the stack top first, so index 0 is the newest action.
  return (
    <Card>
      <Stack direction="vertical" gap={4}>
        <Stack direction="horizontal" hAlign="between" vAlign="center" wrap="wrap" gap={4}>
          <Stack direction="vertical" gap={1}>
            <Heading level={2}>Activity log</Heading>
            <Text type="supporting" color="secondary">
              Recorded officer actions. Undo reverses the latest one.
            </Text>
          </Stack>
          <Button
            label="Undo last action"
            variant="secondary"
            icon={<Icon icon={ArrowUturnLeftIcon} />}
            isDisabled={stack.length === 0 || busy !== null}
            isLoading={busy?.kind === 'undo'}
            onClick={onUndo}
          />
        </Stack>
        {stack.length === 0 ? (
          <Text type="supporting" color="secondary">
            No recent actions recorded.
          </Text>
        ) : (
          <Stack direction="vertical" gap={3}>
            {stack.map((entry, index) => (
              <Stack key={`${index}-${entry.actionType}-${entry.studentId}`} direction="vertical" gap={1}>
                <Text type="body" weight="semibold">
                  {entry.description}
                </Text>
                <Text type="supporting" color="secondary">
                  {`${entry.actionType} · ${entry.studentId}`}
                </Text>
              </Stack>
            ))}
          </Stack>
        )}
      </Stack>
    </Card>
  );
}

export function AdminPage() {
  const [state, setState] = useState<AppState | null>(null);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [busy, setBusy] = useState<Busy | null>(null);
  const [notice, setNotice] = useState<Notice | null>(null);
  const [remarks, setRemarks] = useState('');
  const [isResetOpen, setIsResetOpen] = useState(false);

  const refresh = useCallback(async () => {
    try {
      setState(await getState());
      setLoadError(null);
    } catch (err) {
      setLoadError((err instanceof Error && err.message) || 'Unexpected error.');
    }
  }, []);

  useEffect(() => {
    void refresh();
  }, [refresh]);

  // Runs one server action, shows its outcome, then reloads so the tables match the server.
  const run = async (next: Busy, action: () => Promise<OperationResult>, onSuccess?: () => void) => {
    setBusy(next);
    setNotice(null);
    try {
      const res = await action();
      setNotice({ tone: res.success ? 'success' : 'error', message: res.message });
      if (res.success) onSuccess?.();
    } catch (err) {
      setNotice({ tone: 'error', message: (err instanceof Error && err.message) || 'Unexpected error.' });
    } finally {
      await refresh();
      setBusy(null);
    }
  };

  const reset = async () => {
    await run({ kind: 'reset' }, resetData);
    setIsResetOpen(false);
  };

  return (
    <Stack direction="vertical" gap={6}>
      <Stack direction="horizontal" hAlign="between" vAlign="center" wrap="wrap" gap={4}>
        <Stack direction="vertical" gap={1}>
          <Heading level={1}>Officer console</Heading>
          <Text type="supporting" color="secondary">
            Verify applications and review decisions.
          </Text>
        </Stack>
        <Button
          label="Reset Data"
          variant="destructive"
          icon={<Icon icon={ArrowPathIcon} />}
          isDisabled={state === null || busy !== null}
          onClick={() => setIsResetOpen(true)}
        />
      </Stack>

      <VisuallyHidden as="div" role="status" aria-live="polite">
        {notice?.message ?? ''}
      </VisuallyHidden>
      {notice && (
        <Banner
          status={notice.tone}
          title={notice.message}
          isDismissable
          dismissLabel="Dismiss message"
          onDismiss={() => setNotice(null)}
        />
      )}
      {loadError && (
        <Banner
          status="error"
          title={state ? 'Could not refresh applications' : 'Could not load applications'}
          description={`Check that the portal server is running, then retry. ${loadError}`}
          endContent={<Button label="Retry" variant="secondary" clickAction={refresh} />}
        />
      )}
      {state === null && loadError === null && <Spinner label="Loading applications" />}

      {state && (
        <>
          <SummaryTiles state={state} />
          <QueueCard
            queue={state.queue}
            busy={busy}
            remarks={remarks}
            onRemarksChange={setRemarks}
            onDecide={(studentId, status) =>
              run(
                { kind: 'verify', studentId, status },
                () => verifyApplication(studentId, status, remarks.trim()),
                () => setRemarks(''),
              )
            }
          />
          <DecidedCard students={state.students} />
          <ActivityCard
            stack={state.stack}
            busy={busy}
            onUndo={() => run({ kind: 'undo' }, undoLastAction)}
          />
        </>
      )}

      <AlertDialog
        isOpen={isResetOpen}
        onOpenChange={setIsResetOpen}
        title="Reset to the sample data?"
        description="Reloads the original sample applications. Officer decisions, applications submitted through the portal, the activity log and the undo history are discarded. This cannot be undone."
        actionLabel="Reset Data"
        actionVariant="destructive"
        isActionLoading={busy?.kind === 'reset'}
        onAction={reset}
      />
    </Stack>
  );
}
