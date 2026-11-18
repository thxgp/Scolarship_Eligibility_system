// Typed client for the Java ScholarshipWebServer (/api/*). Shapes mirror studentToJson and the handlers in ScholarshipWebServer.java.

export interface Student {
  studentId: string;
  name: string;
  gpa: number;
  familyIncome: number;
  attendancePercentage: number;
  extracurricularScore: number;
  category: string;
  status: string;
  meritScore: number;
}

export interface ActionEntry {
  actionType: string;
  studentId: string;
  description: string;
}

export interface AppState {
  activeFormula: string;
  students: Student[];
  queue: Student[];
  stack: ActionEntry[];
}

export interface ApplyInput {
  name: string;
  studentId?: string;
  gpa: number;
  familyIncome: number;
  attendancePercentage: number;
  extracurricularScore: number;
  category: string;
}

export interface ApplyResult {
  success: true;
  studentId: string;
  message: string;
  queuePosition: number;
}

export interface TrackResult {
  found: true;
  inQueue: boolean;
  student: Student;
}

export interface OperationResult {
  success: boolean;
  message: string;
}

export class ApiError extends Error {
  readonly status: number;

  constructor(status: number, message: string) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(path, { cache: 'no-store', ...init });
  const body = (await res.json().catch(() => ({}))) as { error?: string; message?: string };
  if (!res.ok) {
    throw new ApiError(res.status, body.error ?? body.message ?? `Request failed (${res.status})`);
  }
  return body as T;
}

function postJson<T>(path: string, payload: unknown): Promise<T> {
  return request<T>(path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
}

export function getStatus(): Promise<{ status: string }> {
  return request('/api/status');
}

export function getState(): Promise<AppState> {
  return request('/api/state');
}

export function submitApplication(input: ApplyInput): Promise<ApplyResult> {
  return postJson('/api/apply', input);
}

export function verifyApplication(studentId: string, status: string, remarks = ''): Promise<OperationResult> {
  return postJson('/api/verify', { studentId, status, remarks });
}

export function undoLastAction(): Promise<OperationResult> {
  return request('/api/undo', { method: 'POST' });
}

export function resetData(): Promise<OperationResult> {
  return request('/api/reset', { method: 'POST' });
}

// Resolves to null when the server has no matching application (HTTP 404).
export async function trackApplication(id: string): Promise<TrackResult | null> {
  try {
    return await request<TrackResult>(`/api/track?id=${encodeURIComponent(id)}`);
  } catch (err) {
    if (err instanceof ApiError && err.status === 404) return null;
    throw err;
  }
}
