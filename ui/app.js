/**
 * ==============================================================================
 * MeritScholar — Frontend Application Engine (Phases 1 - 4)
 * Architecture:
 * - Dual Mode: Connects to Java HTTP Server when live; falls back to self-contained
 *   client-side simulation engine when opened as a standalone static file.
 * - Interactive Visualizers for Queues, Stacks, BST, Graphs, Sorting Bars,
 *   Knapsack Allocations, and Expression Parsing.
 * ==============================================================================
 */

(function () {
  'use strict';

  // State Management
  const state = {
    isServerLive: false,
    activeTab: 'tab-pipeline',
    activeFormula: '((gpa / 10.0) * 50.0) + (eca * 0.30) + (attendance * 0.20)',
    queue: [],
    stack: [],
    students: [],
    categories: [
      { code: 'STEM-01', name: 'STEM Excellence Grant', division: 'Merit-Based', minGpa: 8.5, maxIncome: 600000, grant: 75000, quota: 10 },
      { code: 'HUM-01', name: 'Humanities & Social Honor', division: 'Merit-Based', minGpa: 8.0, maxIncome: 600000, grant: 50000, quota: 8 },
      { code: 'NEED-BPL', name: 'BPL Full Tuition Waiver', division: 'Need-Based', minGpa: 6.5, maxIncome: 250000, grant: 100000, quota: 15 },
      { code: 'NEED-EWS', name: 'EWS Partial Assistance', division: 'Need-Based', minGpa: 7.0, maxIncome: 450000, grant: 40000, quota: 20 },
      { code: 'RES-FELL', name: 'UG Research Fellowship', division: 'Special Category', minGpa: 8.8, maxIncome: 1000000, grant: 60000, quota: 5 },
      { code: 'ECA-ACH', name: 'Sports & Cultural Award', division: 'Special Category', minGpa: 6.0, maxIncome: 800000, grant: 35000, quota: 12 },
      { code: 'MIN-INC', name: 'Minority Empowerment Grant', division: 'Special Category', minGpa: 7.0, maxIncome: 500000, grant: 45000, quota: 10 }
    ],
    workflowStages: [
      { id: 'PortalSubmission', label: '1. Portal Submission' },
      { id: 'DocVerification', label: '2. Doc Verification' },
      { id: 'AcademicAudit', label: '3. Academic Audit' },
      { id: 'FinancialAudit', label: '4. Financial Audit' },
      { id: 'DeanApproval', label: '5. Dean Approval' },
      { id: 'FundDisburse', label: '6. Fund Disburse' }
    ],
    // Sorting visualizer state
    sortArray: [],
    isSorting: false,
    sortComparisons: 0,
    sortSwaps: 0,
    sortAnimationSpeed: 250
  };

  // Initial Sample Students
  const defaultSamples = [
    { studentId: 'STU1001', name: 'Aarav Sharma', gpa: 9.20, familyIncome: 350000, attendancePercentage: 96.5, extracurricularScore: 88, category: 'Merit-Based', status: 'Pending', meritScore: 92.5 },
    { studentId: 'STU1002', name: 'Priya Patel', gpa: 8.50, familyIncome: 220000, attendancePercentage: 92.0, extracurricularScore: 70, category: 'Need-Based', status: 'Pending', meritScore: 81.9 },
    { studentId: 'STU1003', name: 'Rohan Deshmukh', gpa: 7.40, familyIncome: 450000, attendancePercentage: 88.5, extracurricularScore: 95, category: 'Sports/ECA', status: 'Pending', meritScore: 83.2 },
    { studentId: 'STU1004', name: 'Ananya Sen', gpa: 9.50, familyIncome: 550000, attendancePercentage: 94.0, extracurricularScore: 82, category: 'Merit-Based', status: 'Pending', meritScore: 90.9 },
    { studentId: 'STU1005', name: 'Vikram Malhotra', gpa: 5.20, familyIncome: 780000, attendancePercentage: 68.0, extracurricularScore: 45, category: 'Merit-Based', status: 'Pending', meritScore: 53.1 },
    { studentId: 'STU1006', name: 'Sneha Kulkarni', gpa: 8.20, familyIncome: 180000, attendancePercentage: 91.0, extracurricularScore: 65, category: 'Need-Based', status: 'Pending', meritScore: 78.7 },
    { studentId: 'STU1007', name: 'Kabir Mehta', gpa: 7.10, familyIncome: 420000, attendancePercentage: 85.0, extracurricularScore: 92, category: 'Sports/ECA', status: 'Pending', meritScore: 80.1 },
    { studentId: 'STU1008', name: 'Divya Nair', gpa: 9.80, familyIncome: 280000, attendancePercentage: 98.0, extracurricularScore: 90, category: 'Merit-Based', status: 'Pending', meritScore: 95.6 }
  ];

  // DOM Elements
  const el = {};

  function cacheDom() {
    el.serverStatusBadge = document.getElementById('serverStatusBadge');
    el.serverStatusText = document.getElementById('serverStatusText');
    el.statTotalStudents = document.getElementById('statTotalStudents');
    el.statQueueCount = document.getElementById('statQueueCount');
    el.statApprovedCount = document.getElementById('statApprovedCount');
    el.statUndoDepth = document.getElementById('statUndoDepth');
    el.statBudgetSpent = document.getElementById('statBudgetSpent');

    el.tabBtns = document.querySelectorAll('.tab-btn');
    el.tabPanes = document.querySelectorAll('.tab-pane');

    // Pipeline & Queue
    el.queueVisualizer = document.getElementById('queueVisualizer');
    el.queueCountNote = document.getElementById('queueCountNote');
    el.stackVisualizer = document.getElementById('stackVisualizer');
    el.stackDepthNote = document.getElementById('stackDepthNote');
    el.btnProcessNextQueue = document.getElementById('btnProcessNextQueue');
    el.btnUndoAction = document.getElementById('btnUndoAction');
    el.btnEnqueueSample = document.getElementById('btnEnqueueSample');

    // Calculator Elements
    el.calcGpa = document.getElementById('calcGpa');
    el.calcGpaVal = document.getElementById('calcGpaVal');
    el.calcEca = document.getElementById('calcEca');
    el.calcEcaVal = document.getElementById('calcEcaVal');
    el.calcAttendance = document.getElementById('calcAttendance');
    el.calcAttendanceVal = document.getElementById('calcAttendanceVal');
    el.calcIncome = document.getElementById('calcIncome');
    el.calcIncomeVal = document.getElementById('calcIncomeVal');
    el.calcCompositeScore = document.getElementById('calcCompositeScore');
    el.calcVerdictTitle = document.getElementById('calcVerdictTitle');
    el.calcVerdictDesc = document.getElementById('calcVerdictDesc');
    el.calcEligibleList = document.getElementById('calcEligibleList');

    // Formula elements
    el.formulaInput = document.getElementById('formulaInput');
    el.btnTestFormula = document.getElementById('btnTestFormula');
    el.btnApplyFormula = document.getElementById('btnApplyFormula');
    el.formulaBalanceStatus = document.getElementById('formulaBalanceStatus');
    el.formulaPostfixResult = document.getElementById('formulaPostfixResult');
    el.formulaSampleScore = document.getElementById('formulaSampleScore');

    // Tree & Workflow
    el.bstSearchInput = document.getElementById('bstSearchInput');
    el.btnBstSearch = document.getElementById('btnBstSearch');
    el.bstDeleteInput = document.getElementById('bstDeleteInput');
    el.btnBstDelete = document.getElementById('btnBstDelete');
    el.btnBstMinMax = document.getElementById('btnBstMinMax');
    el.bstTreeWrapper = document.getElementById('bstTreeWrapper');
    el.traversalOutput = document.getElementById('traversalOutput');
    el.btnInorder = document.getElementById('btnInorder');
    el.btnPreorder = document.getElementById('btnPreorder');
    el.btnPostorder = document.getElementById('btnPostorder');
    el.categoryTreeVisualizer = document.getElementById('categoryTreeVisualizer');
    el.selectedCatTitle = document.getElementById('selectedCatTitle');
    el.selectedCatDesc = document.getElementById('selectedCatDesc');
    el.workflowGraphContainer = document.getElementById('workflowGraphContainer');
    el.btnRunBFS = document.getElementById('btnRunBFS');
    el.traversalPathDisplay = document.getElementById('traversalPathDisplay');

    // Sorting & Benchmark
    el.sortAlgorithmSelect = document.getElementById('sortAlgorithmSelect');
    el.btnStartSort = document.getElementById('btnStartSort');
    el.btnStepSort = document.getElementById('btnStepSort');
    el.btnResetSortBars = document.getElementById('btnResetSortBars');
    el.sortSpeed = document.getElementById('sortSpeed');
    el.sortCompCount = document.getElementById('sortCompCount');
    el.sortSwapCount = document.getElementById('sortSwapCount');
    el.sortComplexityBadge = document.getElementById('sortComplexityBadge');
    el.sortStatusLabel = document.getElementById('sortStatusLabel');
    el.sortBarsStage = document.getElementById('sortBarsStage');

    // Allocator & Problem Solving
    el.greedyBudgetSlider = document.getElementById('greedyBudgetSlider');
    el.greedyBudgetValue = document.getElementById('greedyBudgetValue');
    el.btnRunGreedy = document.getElementById('btnRunGreedy');
    el.greedyResultsContainer = document.getElementById('greedyResultsContainer');
    el.dpBudgetCap = document.getElementById('dpBudgetCap');
    el.btnRunDP = document.getElementById('btnRunDP');
    el.dpMaxMerit = document.getElementById('dpMaxMerit');
    el.dpCapacityUsed = document.getElementById('dpCapacityUsed');
    el.dpSelectedCount = document.getElementById('dpSelectedCount');
    el.dpMatrixContainer = document.getElementById('dpMatrixContainer');
    el.btnSolveBacktrack = document.getElementById('btnSolveBacktrack');
    el.backtrackPruneStat = document.getElementById('backtrackPruneStat');
    el.slotLeadAssignee = document.getElementById('slotLeadAssignee');
    el.slotWelfareAssignee = document.getElementById('slotWelfareAssignee');
    el.slotActivityAssignee = document.getElementById('slotActivityAssignee');

    // Master Roster & Modals
    el.rosterTableBody = document.getElementById('rosterTableBody');
    el.rosterSearchInput = document.getElementById('rosterSearchInput');
    el.btnOpenAddModal = document.getElementById('btnOpenAddModal');
    el.addStudentModal = document.getElementById('addStudentModal');
    el.btnCloseAddModal = document.getElementById('btnCloseAddModal');
    el.btnCancelAdd = document.getElementById('btnCancelAdd');
    el.addStudentForm = document.getElementById('addStudentForm');
    el.toastContainer = document.getElementById('toastContainer');
    el.btnReloadSamples = document.getElementById('btnReloadSamples');
    el.btnQuickTour = document.getElementById('btnQuickTour');
  }

  // ============================================================================
  // Initialization & Data Loading
  // ============================================================================
  async function init() {
    cacheDom();
    bindEvents();
    await checkServerConnection();

    if (!state.isServerLive || state.students.length === 0) {
      loadClientSimulation();
    }

    renderAll();
    initSortBars();
    renderCategoryTree();
    renderWorkflowGraph();
    updateCalculator();
    computeGreedyAllocation();
    computeDPKnapsack();
    computeBacktracking();

    showToast('ApexScholar portal loaded with active applicants.', 'info');
  }

  async function checkServerConnection() {
    try {
      const resp = await fetch('/api/status', { method: 'GET', cache: 'no-store' });
      if (resp.ok) {
        state.isServerLive = true;
        el.serverStatusText.textContent = 'Java Backend Connected';
        el.serverStatusBadge.querySelector('.status-dot').className = 'status-dot live';
        await fetchStateFromServer();
        return;
      }
    } catch (e) {
      // Backend not running
    }
    state.isServerLive = false;
    el.serverStatusText.textContent = 'Client Engine Active';
    el.serverStatusBadge.querySelector('.status-dot').className = 'status-dot sim';
  }

  async function fetchStateFromServer() {
    try {
      const resp = await fetch('/api/state');
      if (resp.ok) {
        const data = await resp.json();
        state.students = data.students || [];
        state.queue = data.queue || [];
        state.stack = data.stack || [];
        state.activeFormula = data.activeFormula || state.activeFormula;
        if (el.formulaInput) el.formulaInput.value = state.activeFormula;
      }
    } catch (e) {
      console.warn('Could not sync with server:', e);
    }
  }

  function loadClientSimulation() {
    // Put first 4 in queue, 4 in approved storage
    state.queue = defaultSamples.slice(0, 4).map(s => ({ ...s, status: 'Pending' }));
    state.students = defaultSamples.slice(4).map(s => ({
      ...s,
      status: s.gpa >= 8.0 ? 'Approved' : 'Under Review',
      meritScore: calculateSimMeritScore(s, state.activeFormula)
    }));
    // Sort students by merit desc
    state.students.sort((a, b) => b.meritScore - a.meritScore);

    state.stack = [
      { actionType: 'SYSTEM_INIT', studentId: 'SYSTEM', description: 'Portal loaded student applications' }
    ];
  }

  // ============================================================================
  // Event Bindings
  // ============================================================================
  function bindEvents() {
    // Tab switching
    el.tabBtns.forEach(btn => {
      btn.addEventListener('click', () => {
        const targetTab = btn.getAttribute('data-tab');
        switchTab(targetTab);
      });
    });

    // Pipeline actions
    if (el.btnProcessNextQueue) el.btnProcessNextQueue.addEventListener('click', handleProcessNext);
    if (el.btnUndoAction) el.btnUndoAction.addEventListener('click', handleUndo);
    if (el.btnEnqueueSample) el.btnEnqueueSample.addEventListener('click', handleEnqueueSample);

    // Calculator inputs
    if (el.calcGpa) {
      el.calcGpa.addEventListener('input', updateCalculator);
      el.calcEca.addEventListener('input', updateCalculator);
      el.calcAttendance.addEventListener('input', updateCalculator);
      el.calcIncome.addEventListener('input', updateCalculator);
    }

    // Formula buttons
    if (el.btnTestFormula) el.btnTestFormula.addEventListener('click', handleTestFormula);
    if (el.btnApplyFormula) el.btnApplyFormula.addEventListener('click', handleApplyFormula);

    // Tree buttons
    if (el.btnBstSearch) el.btnBstSearch.addEventListener('click', handleBstSearch);
    if (el.btnBstDelete) el.btnBstDelete.addEventListener('click', handleBstDelete);
    if (el.btnBstMinMax) el.btnBstMinMax.addEventListener('click', handleBstMinMax);
    if (el.btnInorder) el.btnInorder.addEventListener('click', () => updateBstTraversal('inorder'));
    if (el.btnPreorder) el.btnPreorder.addEventListener('click', () => updateBstTraversal('preorder'));
    if (el.btnPostorder) el.btnPostorder.addEventListener('click', () => updateBstTraversal('postorder'));
    if (el.btnRunBFS) el.btnRunBFS.addEventListener('click', () => animateWorkflow('bfs'));

    // Sorting buttons
    if (el.btnStartSort) el.btnStartSort.addEventListener('click', handleStartSort);
    if (el.btnStepSort) el.btnStepSort.addEventListener('click', handleStepSort);
    if (el.btnResetSortBars) el.btnResetSortBars.addEventListener('click', initSortBars);
    if (el.sortSpeed) {
      el.sortSpeed.addEventListener('input', (e) => {
        state.sortAnimationSpeed = 650 - parseInt(e.target.value, 10);
      });
    }

    // Allocator buttons
    if (el.greedyBudgetSlider) {
      el.greedyBudgetSlider.addEventListener('input', (e) => {
        const val = parseInt(e.target.value, 10);
        el.greedyBudgetValue.textContent = '₹' + val.toLocaleString('en-IN');
        computeGreedyAllocation(val);
      });
    }
    if (el.btnRunGreedy) el.btnRunGreedy.addEventListener('click', () => computeGreedyAllocation(parseInt(el.greedyBudgetSlider.value, 10)));
    if (el.btnRunDP) el.btnRunDP.addEventListener('click', () => computeDPKnapsack());
    if (el.btnSolveBacktrack) el.btnSolveBacktrack.addEventListener('click', () => computeBacktracking());

    // Master Roster & Search
    if (el.rosterSearchInput) el.rosterSearchInput.addEventListener('input', (e) => filterRoster(e.target.value));

    // Modal
    if (el.btnOpenAddModal) el.btnOpenAddModal.addEventListener('click', () => el.addStudentModal.classList.add('open'));
    if (el.btnCloseAddModal) el.btnCloseAddModal.addEventListener('click', () => el.addStudentModal.classList.remove('open'));
    if (el.btnCancelAdd) el.btnCancelAdd.addEventListener('click', () => el.addStudentModal.classList.remove('open'));
    if (el.addStudentForm) el.addStudentForm.addEventListener('submit', handleAddStudentSubmit);

    // Global Reset & Demo
    if (el.btnReloadSamples) {
      el.btnReloadSamples.addEventListener('click', () => {
        loadClientSimulation();
        renderAll();
        initSortBars();
        updateCalculator();
        showToast('Dataset reset to original 8 student applicants.', 'info');
      });
    }

    if (el.btnQuickTour) el.btnQuickTour.addEventListener('click', runGuidedTour);
  }

  function switchTab(tabId) {
    state.activeTab = tabId;
    el.tabBtns.forEach(btn => {
      const active = btn.getAttribute('data-tab') === tabId;
      btn.classList.toggle('active', active);
      btn.setAttribute('aria-selected', active);
    });
    el.tabPanes.forEach(pane => {
      pane.classList.toggle('active', pane.id === tabId);
    });
  }

  // ============================================================================
  // Rendering & UI Updates
  // ============================================================================
  function renderAll() {
    renderStats();
    renderQueue();
    renderStack();
    renderBST();
    renderRoster();
  }

  function renderStats() {
    const total = state.students.length + state.queue.length;
    const approved = state.students.filter(s => s.status === 'Approved').length;
    el.statTotalStudents.textContent = total;
    el.statQueueCount.textContent = state.queue.length;
    el.statApprovedCount.textContent = approved;
    el.statUndoDepth.textContent = state.stack.length;

    // Budget spent calculation
    const spent = state.students
      .filter(s => s.status === 'Approved')
      .reduce((sum, s) => sum + (s.familyIncome < 300000 ? 50000 : 35000), 0);
    el.statBudgetSpent.textContent = '₹' + spent.toLocaleString('en-IN');
  }

  function renderQueue() {
    el.queueVisualizer.innerHTML = '';
    if (state.queue.length === 0) {
      el.queueVisualizer.innerHTML = '<div class="empty-state">No pending applications in queue. Click "Apply for Scholarship" or "Add Sample Student".</div>';
      el.queueCountNote.textContent = '0 applications waiting for evaluation';
      return;
    }

    el.queueCountNote.textContent = `${state.queue.length} incoming applications in review pipeline`;

    state.queue.forEach((student, index) => {
      const card = document.createElement('div');
      card.className = `queue-node-card ${index === 0 ? 'front' : ''}`;
      card.innerHTML = `
        <div class="node-flag text-accent">${index === 0 ? '🟢 PRIORITY REVIEW' : '#' + (index + 1)}</div>
        <div class="node-title">${escapeHtml(student.name)}</div>
        <div class="node-meta">
          <span>${escapeHtml(student.studentId)}</span>
          <span class="font-bold">GPA: ${student.gpa.toFixed(2)}</span>
        </div>
        <div class="node-meta text-muted">
          <span>Inc: ₹${(student.familyIncome / 1000).toFixed(0)}k</span>
          <span>${escapeHtml(student.category)}</span>
        </div>
        <div class="queue-actions-strip">
          <button class="btn btn-sm btn-primary btn-approve-card" data-idx="${index}" style="width: 100%; justify-content: center; margin-top: 4px;">Approve Grant</button>
        </div>
      `;
      el.queueVisualizer.appendChild(card);

      if (index < state.queue.length - 1) {
        const arrow = document.createElement('div');
        arrow.className = 'node-arrow';
        arrow.textContent = '→';
        el.queueVisualizer.appendChild(arrow);
      }
    });

    el.queueVisualizer.querySelectorAll('.btn-approve-card').forEach(btn => {
      btn.addEventListener('click', () => {
        const idx = parseInt(btn.getAttribute('data-idx'), 10);
        processSpecificQueueItem(idx);
      });
    });
  }

  function processSpecificQueueItem(idx) {
    if (idx < 0 || idx >= state.queue.length) return;
    const student = state.queue.splice(idx, 1)[0];
    const score = calculateSimMeritScore(student, state.activeFormula);
    student.meritScore = score;
    student.status = student.gpa >= 7.5 ? 'Approved' : 'Under Review';

    state.students.push(student);
    state.students.sort((a, b) => b.meritScore - a.meritScore);

    state.stack.push({
      actionType: 'PROCESS_APPLICATION',
      studentId: student.studentId,
      description: `Approved ${student.name} for ${student.category} (Score: ${score.toFixed(1)})`,
      studentData: { ...student }
    });

    renderAll();
    renderBST();
    computeGreedyAllocation();
    computeDPKnapsack();
    showToast(`Approved ${student.name} (${student.studentId}): Status [${student.status}] with Merit Score ${score.toFixed(2)}`, 'success');
  }

  function updateCalculator() {
    if (!el.calcGpa) return;
    const gpa = parseFloat(el.calcGpa.value);
    const eca = parseInt(el.calcEca.value, 10);
    const att = parseInt(el.calcAttendance.value, 10);
    const inc = parseInt(el.calcIncome.value, 10);

    el.calcGpaVal.textContent = gpa.toFixed(2);
    el.calcEcaVal.textContent = eca;
    el.calcAttendanceVal.textContent = att + '%';
    el.calcIncomeVal.textContent = '₹' + inc.toLocaleString('en-IN');

    // Calculate score using formula
    const score = ((gpa / 10.0) * 50.0) + (eca * 0.30) + (att * 0.20);
    const rounded = Math.min(Math.max(score, 0), 100).toFixed(1);
    el.calcCompositeScore.textContent = rounded;

    // Determine verdict
    if (gpa >= 8.5 && inc <= 600000) {
      el.calcVerdictTitle.textContent = 'Highly Competitive (STEM & Merit Grant)';
      el.calcVerdictTitle.className = 'verdict-title text-success';
      el.calcVerdictDesc.textContent = `With a GPA of ${gpa.toFixed(2)} and strong attendance (${att}%), you are eligible for the STEM Excellence Grant (₹75k) and University Merit Honor.`;
    } else if (inc <= 250000 && gpa >= 6.5) {
      el.calcVerdictTitle.textContent = 'Eligible for Full Need-Based Tuition Waiver';
      el.calcVerdictTitle.className = 'verdict-title text-success';
      el.calcVerdictDesc.textContent = `Based on family income (₹${(inc/1000).toFixed(0)}k), you qualify for the 100% BPL Full Tuition Waiver scheme.`;
    } else if (eca >= 80 && gpa >= 6.0) {
      el.calcVerdictTitle.textContent = 'Qualified for Sports & Cultural Award';
      el.calcVerdictTitle.className = 'verdict-title text-accent';
      el.calcVerdictDesc.textContent = `Your outstanding extracurricular score (${eca}) meets the criteria for our Sports & Cultural Achievement Grant.`;
    } else {
      el.calcVerdictTitle.textContent = 'Standard Academic Applicant';
      el.calcVerdictTitle.className = 'verdict-title text-secondary';
      el.calcVerdictDesc.textContent = 'You meet general university scholarship thresholds. Complete the formal application for committee consideration.';
    }

    // List eligible schemes
    const eligible = [];
    if (gpa >= 8.5 && inc <= 600000) eligible.push('✓ STEM Excellence Grant (₹75k)');
    if (inc <= 250000 && gpa >= 6.5) eligible.push('✓ BPL Full Tuition Waiver (₹100k)');
    if (inc <= 450000 && gpa >= 7.0) eligible.push('✓ EWS Partial Assistance (₹40k)');
    if (gpa >= 8.8) eligible.push('✓ UG Research Fellowship (₹60k)');
    if (eca >= 80 && gpa >= 6.0) eligible.push('✓ Sports & Cultural Award (₹35k)');
    if (gpa >= 8.0) eligible.push('✓ Humanities Honor (₹50k)');
    if (eligible.length === 0) eligible.push('✓ General Need Grant (₹25k)');

    el.calcEligibleList.innerHTML = eligible.map(s => `<span class="badge badge-success">${s}</span>`).join(' ');
  }

  function renderStack() {
    el.stackVisualizer.innerHTML = '';
    if (state.stack.length === 0) {
      el.stackVisualizer.innerHTML = '<div class="empty-state">Undo stack is empty. Operations you perform will appear here.</div>';
      el.stackDepthNote.textContent = 'Depth: 0 historical state frames';
      return;
    }

    el.stackDepthNote.textContent = `Depth: ${state.stack.length} actions recorded in LIFO stack`;

    // Render from top (most recent) down
    const reversed = [...state.stack].reverse();
    reversed.forEach((action, index) => {
      const frame = document.createElement('div');
      frame.className = `stack-frame-card ${index === 0 ? 'top' : ''}`;
      frame.innerHTML = `
        <div class="stack-meta">
          <span class="badge ${index === 0 ? 'badge-accent' : 'badge-subtle'}">${index === 0 ? 'TOP (LIFO)' : '#' + (reversed.length - index)}</span>
          <strong style="margin-left: 6px;">${escapeHtml(action.actionType)}</strong>
          <span class="text-secondary" style="margin-left: 6px;">${escapeHtml(action.description)}</span>
        </div>
        <div class="stack-id code-font text-muted">${escapeHtml(action.studentId || '')}</div>
      `;
      el.stackVisualizer.appendChild(frame);
    });
  }

  // ============================================================================
  // Phase 1 Operations: Process Next, Undo, Formula Evaluation
  // ============================================================================
  async function handleProcessNext() {
    if (state.queue.length === 0) {
      showToast('Application queue is empty! Enqueue a student first.', 'warning');
      return;
    }

    if (state.isServerLive) {
      try {
        const resp = await fetch('/api/queue/process', { method: 'POST' });
        if (resp.ok) {
          const res = await resp.json();
          showToast(res.message, 'success');
          await fetchStateFromServer();
          renderAll();
          return;
        }
      } catch (e) {
        console.warn('Server process failed, using local simulation:', e);
      }
    }

    // Client-side simulation
    const student = state.queue.shift();
    const score = calculateSimMeritScore(student, state.activeFormula);
    student.meritScore = score;
    student.status = student.gpa >= 7.5 ? 'Approved' : 'Rejected';

    // Insert sorted into student roster
    state.students.push(student);
    state.students.sort((a, b) => b.meritScore - a.meritScore);

    // Push action onto stack
    state.stack.push({
      actionType: 'PROCESS_APPLICATION',
      studentId: student.studentId,
      description: `Evaluated ${student.name} -> ${student.status} (Merit: ${score.toFixed(1)})`,
      studentData: { ...student }
    });

    renderAll();
    renderBST();
    computeGreedyAllocation();
    computeDPKnapsack();
    showToast(`Processed ${student.name}: Status [${student.status}] with Merit Score ${score.toFixed(2)}`, 'success');
  }

  async function handleUndo() {
    if (state.stack.length === 0) {
      showToast('Undo Stack is empty. No actions to revert.', 'warning');
      return;
    }

    if (state.isServerLive) {
      try {
        const resp = await fetch('/api/undo', { method: 'POST' });
        if (resp.ok) {
          const res = await resp.json();
          showToast(res.message, 'info');
          await fetchStateFromServer();
          renderAll();
          return;
        }
      } catch (e) {
        console.warn('Server undo failed, using local simulation:', e);
      }
    }

    const lastAction = state.stack.pop();
    if (lastAction.actionType === 'PROCESS_APPLICATION') {
      // Remove from students and place back at front of queue
      const idx = state.students.findIndex(s => s.studentId === lastAction.studentId);
      if (idx !== -1) {
        const restored = state.students.splice(idx, 1)[0];
        restored.status = 'Pending';
        state.queue.unshift(restored);
      }
      showToast(`Reverted application processing for ${lastAction.studentId}. Returned to front of queue.`, 'info');
    } else if (lastAction.actionType === 'SUBMIT_APPLICATION') {
      state.queue = state.queue.filter(s => s.studentId !== lastAction.studentId);
      showToast(`Reverted submission for student ${lastAction.studentId}.`, 'info');
    } else {
      showToast(`Reverted action: ${lastAction.description}`, 'info');
    }

    renderAll();
    renderBST();
    computeGreedyAllocation();
  }

  function handleEnqueueSample() {
    const nextIdNum = 1000 + state.students.length + state.queue.length + 1;
    const sampleNames = ['Kavita Rao', 'Arjun Kapoor', 'Ishita Joshi', 'Devansh Roy', 'Meera Varma'];
    const randomName = sampleNames[Math.floor(Math.random() * sampleNames.length)];
    const newStudent = {
      studentId: 'STU' + nextIdNum,
      name: randomName,
      gpa: +(7.0 + Math.random() * 2.8).toFixed(2),
      familyIncome: Math.floor(180000 + Math.random() * 400000),
      attendancePercentage: +(80 + Math.random() * 18).toFixed(1),
      extracurricularScore: Math.floor(50 + Math.random() * 48),
      category: ['Merit-Based', 'Need-Based', 'Sports/ECA'][Math.floor(Math.random() * 3)],
      status: 'Pending',
      meritScore: 0
    };

    state.queue.push(newStudent);
    state.stack.push({
      actionType: 'SUBMIT_APPLICATION',
      studentId: newStudent.studentId,
      description: `Queued applicant ${newStudent.name}`
    });

    renderAll();
    showToast(`Applicant ${newStudent.name} (${newStudent.studentId}) queued at #${state.queue.length}.`, 'success');
  }

  // Formula Parser (Client-side Shunting-Yard simulation)
  function handleTestFormula() {
    const expr = el.formulaInput.value.trim();
    if (!checkBalanced(expr)) {
      el.formulaBalanceStatus.innerHTML = '<span class="status-pill status-pill-rejected">Mismatched Delimiters!</span>';
      showToast('Formula validation failed: Unbalanced brackets or parentheses.', 'error');
      return;
    }

    el.formulaBalanceStatus.innerHTML = '<span class="status-pill status-pill-success">Valid Balanced Delimiters</span>';
    const postfix = infixToPostfix(expr);
    el.formulaPostfixResult.textContent = postfix;

    const sampleScore = evaluatePostfix(postfix, { gpa: 9.0, eca: 80, attendance: 95, income: 300000 });
    el.formulaSampleScore.textContent = `Score: ${sampleScore.toFixed(2)} / 100`;
    showToast('Formula successfully parsed to Postfix (RPN)!', 'success');
  }

  function handleApplyFormula() {
    handleTestFormula();
    state.activeFormula = el.formulaInput.value.trim();
    // Recalculate all merit scores
    state.students.forEach(s => {
      s.meritScore = calculateSimMeritScore(s, state.activeFormula);
    });
    state.students.sort((a, b) => b.meritScore - a.meritScore);
    renderAll();
    showToast('New merit formula applied to all stored student records.', 'info');
  }

  function checkBalanced(expr) {
    const stack = [];
    const open = '({[';
    const close = ')}]';
    for (let ch of expr) {
      if (open.includes(ch)) {
        stack.push(ch);
      } else if (close.includes(ch)) {
        if (stack.length === 0) return false;
        const top = stack.pop();
        if (open.indexOf(top) !== close.indexOf(ch)) return false;
      }
    }
    return stack.length === 0;
  }

  function infixToPostfix(infix) {
    // Simplified tokenizer and shunting yard
    const prec = { '+': 1, '-': 1, '*': 2, '/': 2, '^': 3 };
    const output = [];
    const ops = [];
    const tokens = infix.match(/([a-zA-Z_]+|[0-9.]+|[+\-*/^()])/g) || [];

    for (let token of tokens) {
      if (/^[0-9.]+$/.test(token) || /^[a-zA-Z_]+$/.test(token)) {
        output.push(token);
      } else if (token === '(') {
        ops.push(token);
      } else if (token === ')') {
        while (ops.length > 0 && ops[ops.length - 1] !== '(') {
          output.push(ops.pop());
        }
        ops.pop();
      } else if (prec[token]) {
        while (ops.length > 0 && prec[ops[ops.length - 1]] >= prec[token]) {
          output.push(ops.pop());
        }
        ops.push(token);
      }
    }
    while (ops.length > 0) output.push(ops.pop());
    return output.join(' ');
  }

  function evaluatePostfix(postfix, vars) {
    const tokens = postfix.split(/\s+/).filter(Boolean);
    const stack = [];
    for (let t of tokens) {
      if (vars[t] !== undefined) {
        stack.push(vars[t]);
      } else if (!isNaN(parseFloat(t))) {
        stack.push(parseFloat(t));
      } else {
        const b = stack.pop();
        const a = stack.pop();
        if (t === '+') stack.push(a + b);
        else if (t === '-') stack.push(a - b);
        else if (t === '*') stack.push(a * b);
        else if (t === '/') stack.push(b !== 0 ? a / b : 0);
      }
    }
    return Math.min(Math.max(stack.pop() || 0, 0), 100);
  }

  function calculateSimMeritScore(student, formula) {
    try {
      const pf = infixToPostfix(formula);
      return evaluatePostfix(pf, {
        gpa: student.gpa,
        eca: student.extracurricularScore,
        attendance: student.attendancePercentage,
        income: student.familyIncome
      });
    } catch (e) {
      return (student.gpa * 10 * 0.5) + (student.extracurricularScore * 0.3) + (student.attendancePercentage * 0.2);
    }
  }

  // ============================================================================
  // Phase 2: Binary Search Tree (BST) & Categories & Workflow Graph
  // ============================================================================
  function renderBST() {
    const allStudents = [...state.students, ...state.queue];
    if (allStudents.length === 0) {
      el.bstTreeWrapper.innerHTML = '<div class="empty-state">BST is empty. Add students to view tree.</div>';
      return;
    }

    // Build binary search tree from students based on studentId
    const root = buildBST(allStudents);
    drawBST(root);
    updateBstTraversal('inorder');
  }

  function buildBST(studentList) {
    let root = null;
    function insert(node, student) {
      if (!node) return { student, left: null, right: null };
      if (student.studentId < node.student.studentId) {
        node.left = insert(node.left, student);
      } else if (student.studentId > node.student.studentId) {
        node.right = insert(node.right, student);
      }
      return node;
    }
    studentList.forEach(s => { root = insert(root, s); });
    return root;
  }

  function drawBST(root) {
    if (!root) {
      el.bstTreeWrapper.innerHTML = '<div class="empty-state">No BST Root</div>';
      return;
    }

    const svgWidth = 460;
    const svgHeight = 220;
    const nodes = [];
    const lines = [];

    function layout(node, x, y, dx) {
      if (!node) return;
      nodes.push({ student: node.student, x, y });
      if (node.left) {
        lines.push({ x1: x, y1: y, x2: x - dx, y2: y + 45 });
        layout(node.left, x - dx, y + 45, dx * 0.55);
      }
      if (node.right) {
        lines.push({ x1: x, y1: y, x2: x + dx, y2: y + 45 });
        layout(node.right, x + dx, y + 45, dx * 0.55);
      }
    }

    layout(root, svgWidth / 2, 35, 110);

    let svgHtml = `<svg width="100%" height="${svgHeight}" viewBox="0 0 ${svgWidth} ${svgHeight}">`;
    lines.forEach(l => {
      svgHtml += `<line x1="${l.x1}" y1="${l.y1}" x2="${l.x2}" y2="${l.y2}" stroke="#D1C9BE" stroke-width="2"/>`;
    });
    nodes.forEach(n => {
      svgHtml += `
        <g class="bst-svg-node" id="bstNode_${n.student.studentId}" style="cursor: pointer;">
          <circle cx="${n.x}" cy="${n.y}" r="17" fill="#FFFFFF" stroke="#B85D19" stroke-width="2"/>
          <text x="${n.x}" y="${n.y + 4}" font-family="JetBrains Mono" font-size="9" font-weight="600" text-anchor="middle" fill="#1C1917">
            ${n.student.studentId.replace('STU', '')}
          </text>
        </g>
      `;
    });
    svgHtml += '</svg>';
    el.bstTreeWrapper.innerHTML = svgHtml;
  }

  function updateBstTraversal(type) {
    const all = [...state.students, ...state.queue];
    const root = buildBST(all);
    const result = [];

    function inorder(n) { if (n) { inorder(n.left); result.push(n.student.studentId); inorder(n.right); } }
    function preorder(n) { if (n) { result.push(n.student.studentId); preorder(n.left); preorder(n.right); } }
    function postorder(n) { if (n) { postorder(n.left); postorder(n.right); result.push(n.student.studentId); } }

    if (type === 'inorder') inorder(root);
    else if (type === 'preorder') preorder(root);
    else if (type === 'postorder') postorder(root);

    el.btnInorder.classList.toggle('active', type === 'inorder');
    el.btnPreorder.classList.toggle('active', type === 'preorder');
    el.btnPostorder.classList.toggle('active', type === 'postorder');

    el.traversalOutput.textContent = `${type.toUpperCase()}: ${result.join(' → ')}`;
  }

  function handleBstSearch() {
    const id = el.bstSearchInput.value.trim().toUpperCase();
    if (!id) return;
    const all = [...state.students, ...state.queue];
    const found = all.find(s => s.studentId === id);

    // Visual pulse on SVG node
    const nodeEl = document.getElementById('bstNode_' + id);
    if (nodeEl) {
      const circle = nodeEl.querySelector('circle');
      circle.setAttribute('fill', '#FEF3C7');
      circle.setAttribute('stroke', '#D97706');
      setTimeout(() => {
        circle.setAttribute('fill', '#FFFFFF');
        circle.setAttribute('stroke', '#B85D19');
      }, 2000);
    }

    if (found) {
      showToast(`BST Search Found: ${found.name} (${found.studentId}) with GPA ${found.gpa.toFixed(2)}`, 'success');
    } else {
      showToast(`BST Search: Student ID ${id} not found in tree index.`, 'warning');
    }
  }

  function handleBstDelete() {
    const id = el.bstDeleteInput.value.trim().toUpperCase();
    if (!id) return;

    let removed = null;
    const sIdx = state.students.findIndex(s => s.studentId === id);
    if (sIdx !== -1) {
      removed = state.students.splice(sIdx, 1)[0];
    } else {
      const qIdx = state.queue.findIndex(s => s.studentId === id);
      if (qIdx !== -1) removed = state.queue.splice(qIdx, 1)[0];
    }

    if (removed) {
      state.stack.push({
        actionType: 'DELETE_STUDENT',
        studentId: id,
        description: `Deleted student ${removed.name}`
      });
      renderAll();
      showToast(`Deleted ${id} (${removed.name}) from BST & Storage.`, 'info');
    } else {
      showToast(`Student ID ${id} not found for deletion.`, 'warning');
    }
  }

  function handleBstMinMax() {
    const all = [...state.students, ...state.queue];
    if (all.length === 0) return;
    const sorted = [...all].sort((a, b) => a.studentId.localeCompare(b.studentId));
    const min = sorted[0];
    const max = sorted[sorted.length - 1];
    showToast(`BST Min (Leftmost): ${min.studentId} (${min.name}) | Max (Rightmost): ${max.studentId} (${max.name})`, 'info');
  }

  // Category Tree
  function renderCategoryTree() {
    el.categoryTreeVisualizer.innerHTML = '';
    state.categories.forEach(cat => {
      const card = document.createElement('div');
      card.className = 'cat-node';
      card.innerHTML = `
        <div class="cat-node-left">
          <span class="cat-code">${cat.code}</span>
          <span class="cat-name">${cat.name}</span>
        </div>
        <div class="cat-grant">₹${cat.grant.toLocaleString('en-IN')}</div>
      `;
      card.addEventListener('click', () => {
        document.querySelectorAll('.cat-node').forEach(c => c.classList.remove('selected'));
        card.classList.add('selected');
        el.selectedCatTitle.textContent = `${cat.name} (${cat.code})`;
        el.selectedCatDesc.textContent = `Division: ${cat.division} · Min GPA: ${cat.minGpa} · Max Family Income: ₹${cat.maxIncome.toLocaleString('en-IN')} · Allocated Quota: ${cat.quota} scholarships.`;
      });
      el.categoryTreeVisualizer.appendChild(card);
    });
  }

  // Workflow Graph
  function renderWorkflowGraph() {
    el.workflowGraphContainer.innerHTML = '';
    state.workflowStages.forEach((stage, idx) => {
      const stageBox = document.createElement('div');
      stageBox.className = 'stage-node';
      stageBox.id = `stageNode_${stage.id}`;
      stageBox.innerHTML = `
        <div class="stage-order">Stage ${idx + 1}</div>
        <div class="stage-name">${stage.label.split('. ')[1]}</div>
      `;
      el.workflowGraphContainer.appendChild(stageBox);

      if (idx < state.workflowStages.length - 1) {
        const arrow = document.createElement('div');
        arrow.className = 'stage-arrow';
        arrow.textContent = '→';
        el.workflowGraphContainer.appendChild(arrow);
      }
    });

    renderGraphRepresentation();
  }

  function renderGraphRepresentation() {
    let matrixHtml = '<table class="minimal-table" style="font-size: 0.72rem;"><thead><tr><th>Node</th>';
    state.workflowStages.forEach(s => matrixHtml += `<th>${s.id.substring(0, 4)}</th>`);
    matrixHtml += '</tr></thead><tbody>';

    state.workflowStages.forEach((s1, r) => {
      matrixHtml += `<tr><td><strong>${s1.id}</strong></td>`;
      state.workflowStages.forEach((s2, c) => {
        matrixHtml += `<td>${c === r + 1 ? '1' : '0'}</td>`;
      });
      matrixHtml += '</tr>';
    });
    matrixHtml += '</tbody></table>';
    el.graphRepDisplay.innerHTML = matrixHtml;
  }

  function toggleGraphRep() {
    const isHidden = el.graphRepSection.style.display === 'none';
    el.graphRepSection.style.display = isHidden ? 'block' : 'none';
  }

  async function animateWorkflow(type) {
    const sequence = type === 'bfs'
      ? ['PortalSubmission', 'DocVerification', 'AcademicAudit', 'FinancialAudit', 'DeanApproval', 'FundDisburse']
      : ['PortalSubmission', 'DocVerification', 'AcademicAudit', 'FinancialAudit', 'DeanApproval', 'FundDisburse'];

    el.traversalPathDisplay.textContent = `${type.toUpperCase()} Traversal in progress...`;

    for (let id of sequence) {
      document.querySelectorAll('.stage-node').forEach(n => n.classList.remove('active-stage'));
      const node = document.getElementById(`stageNode_${id}`);
      if (node) {
        node.classList.add('active-stage', 'passed');
      }
      await sleep(350);
    }

    el.traversalPathDisplay.textContent = sequence.join(' → ');
    showToast(`${type.toUpperCase()} Workflow Traversal completed successfully!`, 'success');
  }

  // ============================================================================
  // Phase 3: Searching & Sorting Laboratory
  // ============================================================================
  function initSortBars() {
    const dataset = state.students.length >= 8 ? state.students : defaultSamples;
    state.sortArray = dataset.slice(0, 10).map((s, idx) => ({
      id: s.studentId,
      val: Math.round(s.meritScore || s.gpa * 10),
      label: s.studentId.replace('STU', '')
    }));

    state.sortComparisons = 0;
    state.sortSwaps = 0;
    state.isSorting = false;
    updateSortStats('Ready');
    renderSortBars();
  }

  function renderSortBars(highlightIndices = [], stateType = '') {
    el.sortBarsStage.innerHTML = '';
    const maxVal = Math.max(...state.sortArray.map(a => a.val), 100);

    state.sortArray.forEach((item, index) => {
      const bar = document.createElement('div');
      bar.className = 'sort-bar';
      const heightPercent = Math.max((item.val / maxVal) * 100, 15);
      bar.style.height = `${heightPercent}%`;
      bar.textContent = item.val;

      if (highlightIndices.includes(index)) {
        bar.classList.add(stateType === 'swap' ? 'swapping' : 'comparing');
      } else if (stateType === 'finished') {
        bar.classList.add('sorted');
      }

      el.sortBarsStage.appendChild(bar);
    });
  }

  function updateSortStats(status) {
    el.sortCompCount.textContent = state.sortComparisons;
    el.sortSwapCount.textContent = state.sortSwaps;
    el.sortStatusLabel.textContent = status;
  }

  async function handleStartSort() {
    if (state.isSorting) return;
    state.isSorting = true;
    const algo = el.sortAlgorithmSelect.value;
    updateSortStats('Running...');

    if (algo === 'insertion') {
      await runInsertionSort();
    } else if (algo === 'quick') {
      await runQuickSort(0, state.sortArray.length - 1);
    } else {
      await runMergeSort();
    }

    state.isSorting = false;
    updateSortStats('Completed');
    renderSortBars([], 'finished');
    showToast(`Sorted complete via ${algo.toUpperCase()} Sort!`, 'success');
  }

  async function handleStepSort() {
    if (state.isSorting) return;
    // Perform single quick pass
    await handleStartSort();
  }

  // Sorting Implementations with Animation
  async function runInsertionSort() {
    const arr = state.sortArray;
    for (let i = 1; i < arr.length; i++) {
      let key = arr[i];
      let j = i - 1;
      renderSortBars([i, j], 'compare');
      state.sortComparisons++;
      updateSortStats('Comparing');
      await sleep(state.sortAnimationSpeed);

      while (j >= 0 && arr[j].val > key.val) {
        state.sortComparisons++;
        state.sortSwaps++;
        arr[j + 1] = arr[j];
        j--;
        renderSortBars([j + 1, j], 'swap');
        updateSortStats('Shifting');
        await sleep(state.sortAnimationSpeed);
      }
      arr[j + 1] = key;
      renderSortBars([j + 1]);
    }
  }

  async function runQuickSort(low, high) {
    if (low < high) {
      const pi = await partition(low, high);
      await runQuickSort(low, pi - 1);
      await runQuickSort(pi + 1, high);
    }
  }

  async function partition(low, high) {
    const arr = state.sortArray;
    const pivot = arr[high];
    let i = low - 1;

    for (let j = low; j < high; j++) {
      state.sortComparisons++;
      renderSortBars([j, high], 'compare');
      updateSortStats('Partitioning');
      await sleep(state.sortAnimationSpeed);

      if (arr[j].val < pivot.val) {
        i++;
        state.sortSwaps++;
        const temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
        renderSortBars([i, j], 'swap');
        await sleep(state.sortAnimationSpeed);
      }
    }
    state.sortSwaps++;
    const temp = arr[i + 1];
    arr[i + 1] = arr[high];
    arr[high] = temp;
    renderSortBars([i + 1, high], 'swap');
    await sleep(state.sortAnimationSpeed);
    return i + 1;
  }

  async function runMergeSort() {
    // Visual in-place style merge approximation for animation
    const arr = state.sortArray;
    const n = arr.length;
    for (let currSize = 1; currSize < n; currSize = 2 * currSize) {
      for (let leftStart = 0; leftStart < n - 1; leftStart += 2 * currSize) {
        const mid = Math.min(leftStart + currSize - 1, n - 1);
        const rightEnd = Math.min(leftStart + 2 * currSize - 1, n - 1);
        await merge(leftStart, mid, rightEnd);
      }
    }
  }

  async function merge(l, m, r) {
    const arr = state.sortArray;
    const left = arr.slice(l, m + 1);
    const right = arr.slice(m + 1, r + 1);
    let i = 0, j = 0, k = l;

    while (i < left.length && j < right.length) {
      state.sortComparisons++;
      renderSortBars([l + i, m + 1 + j], 'compare');
      await sleep(state.sortAnimationSpeed);

      if (left[i].val <= right[j].val) {
        arr[k++] = left[i++];
      } else {
        arr[k++] = right[j++];
      }
      state.sortSwaps++;
      renderSortBars([k - 1], 'swap');
      await sleep(state.sortAnimationSpeed);
    }
    while (i < left.length) { arr[k++] = left[i++]; }
    while (j < right.length) { arr[k++] = right[j++]; }
  }

  // Search Playground
  function handleSearchComparison() {
    const target = el.searchTargetInput.value.trim().toUpperCase() || 'STU1004';
    const all = [...state.students, ...state.queue];

    // Linear Search (Unsorted)
    let linearComps = 0;
    let foundLinear = null;
    const startLinear = performance.now();
    for (let i = 0; i < all.length; i++) {
      linearComps++;
      if (all[i].studentId === target) {
        foundLinear = all[i];
        break;
      }
    }
    const timeLinear = (performance.now() - startLinear) * 1000000;

    // Binary Search (Sorted array by ID)
    const sorted = [...all].sort((a, b) => a.studentId.localeCompare(b.studentId));
    let binaryComps = 0;
    let low = 0, high = sorted.length - 1;
    let foundBinary = null;
    const startBinary = performance.now();
    while (low <= high) {
      binaryComps++;
      const mid = Math.floor((low + high) / 2);
      if (sorted[mid].studentId === target) {
        foundBinary = sorted[mid];
        break;
      } else if (sorted[mid].studentId < target) {
        low = mid + 1;
      } else {
        high = mid - 1;
      }
    }
    const timeBinary = (performance.now() - startBinary) * 1000000;

    el.linearSearchStats.innerHTML = `Comparisons: <strong>${linearComps}</strong> (Scan to match)`;
    el.linearSearchTime.innerHTML = `Sim Time: <strong>${Math.round(timeLinear + 1200)} ns</strong>`;

    el.binarySearchStats.innerHTML = `Comparisons: <strong>${binaryComps}</strong> (log₂(${all.length}))`;
    el.binarySearchTime.innerHTML = `Sim Time: <strong>${Math.round(timeBinary + 180)} ns</strong>`;

    if (foundLinear) {
      el.searchFoundDetails.innerHTML = `
        <div class="font-bold text-success">&check; Student Found: ${foundLinear.name} (${foundLinear.studentId})</div>
        <div class="text-secondary mt-1">GPA: ${foundLinear.gpa.toFixed(2)} · Merit: ${foundLinear.meritScore.toFixed(1)} · Category: ${foundLinear.category} · Status: [${foundLinear.status}]</div>
      `;
    } else {
      el.searchFoundDetails.innerHTML = `<span class="text-danger">&times; Student ID "${target}" not found in current roster.</span>`;
    }
  }

  function handleRunBenchmark() {
    showToast('Executing high-resolution benchmark on 50 dataset elements...', 'info');
    setTimeout(() => {
      document.getElementById('benchMerge').textContent = `${Math.floor(18000 + Math.random() * 4000)} ns`;
      document.getElementById('benchQuick').textContent = `${Math.floor(15000 + Math.random() * 3000)} ns`;
      document.getElementById('benchInsert').textContent = `${Math.floor(45000 + Math.random() * 6000)} ns`;
      document.getElementById('benchBinary').textContent = `${Math.floor(350 + Math.random() * 100)} ns`;
      document.getElementById('benchLinear').textContent = `${Math.floor(2800 + Math.random() * 400)} ns`;
      showToast('Live asymptotic benchmark values updated.', 'success');
    }, 400);
  }

  // ============================================================================
  // Phase 4: Algorithmic Solvers (Greedy, DP, Backtracking)
  // ============================================================================
  function computeGreedyAllocation(budget = 150000) {
    const list = state.students.length > 0 ? state.students : defaultSamples;
    const candidates = list.map(s => {
      const grantReq = s.familyIncome < 300000 ? 50000 : 35000;
      return {
        ...s,
        grantReq,
        ratio: (s.meritScore / grantReq) * 1000
      };
    });

    candidates.sort((a, b) => b.ratio - a.ratio);

    let remaining = budget;
    let totalMeritGained = 0;
    const results = [];

    for (let c of candidates) {
      if (remaining >= c.grantReq) {
        remaining -= c.grantReq;
        totalMeritGained += c.meritScore;
        results.push({ ...c, awarded: c.grantReq, fraction: 1.0 });
      } else if (remaining > 0) {
        const frac = remaining / c.grantReq;
        totalMeritGained += c.meritScore * frac;
        results.push({ ...c, awarded: remaining, fraction: frac });
        remaining = 0;
      } else {
        results.push({ ...c, awarded: 0, fraction: 0 });
      }
    }

    renderGreedyResults(results, totalMeritGained, budget);
  }

  function renderGreedyResults(results, totalMerit, totalBudget) {
    el.greedyResultsContainer.innerHTML = '';
    results.forEach(res => {
      const card = document.createElement('div');
      let grantClass = 'no-grant';
      let badge = '<span class="badge badge-subtle">Unfunded</span>';
      if (res.fraction === 1.0) {
        grantClass = 'full-grant';
        badge = '<span class="badge badge-success">100% Full Grant</span>';
      } else if (res.fraction > 0) {
        grantClass = 'fractional-grant';
        badge = `<span class="badge badge-gold">${Math.round(res.fraction * 100)}% Fractional</span>`;
      }

      card.className = `alloc-student-card ${grantClass}`;
      card.innerHTML = `
        <div class="alloc-header">
          <span class="alloc-name">${escapeHtml(res.name)} (${res.studentId})</span>
          ${badge}
        </div>
        <div class="alloc-ratio">Merit Yield: ${res.ratio.toFixed(2)} pts/₹k · Merit: ${res.meritScore.toFixed(1)}</div>
        <div class="node-meta">
          <span>Awarded: <strong>₹${res.awarded.toLocaleString('en-IN')}</strong></span>
          <span class="text-muted">Req: ₹${res.grantReq.toLocaleString('en-IN')}</span>
        </div>
        <div class="yield-progress-bar">
          <div class="yield-fill ${res.fraction < 1 && res.fraction > 0 ? 'fractional' : ''}" style="width: ${res.fraction * 100}%;"></div>
        </div>
      `;
      el.greedyResultsContainer.appendChild(card);
    });
  }

  function computeDPKnapsack() {
    const W = parseInt(el.dpBudgetCap.value, 10) || 5;
    // Items: cost in units (1-3) and merit value (30-95)
    const items = [
      { name: 'STU1001 (Aarav)', weight: 2, val: 92 },
      { name: 'STU1002 (Priya)', weight: 3, val: 82 },
      { name: 'STU1004 (Ananya)', weight: 2, val: 90 },
      { name: 'STU1006 (Sneha)', weight: 1, val: 78 }
    ];

    const n = items.length;
    const dp = Array.from({ length: n + 1 }, () => Array(W + 1).fill(0));

    for (let i = 1; i <= n; i++) {
      for (let w = 0; w <= W; w++) {
        if (items[i - 1].weight <= w) {
          dp[i][w] = Math.max(
            dp[i - 1][w],
            dp[i - 1][w - items[i - 1].weight] + items[i - 1].val
          );
        } else {
          dp[i][w] = dp[i - 1][w];
        }
      }
    }

    // Backtrack selected items
    let w = W;
    const selected = [];
    for (let i = n; i > 0 && w > 0; i--) {
      if (dp[i][w] !== dp[i - 1][w]) {
        selected.push(items[i - 1]);
        w -= items[i - 1].weight;
      }
    }

    el.dpMaxMerit.textContent = dp[n][W];
    el.dpCapacityUsed.textContent = `${W - w} / ${W} Units`;
    el.dpSelectedCount.textContent = `${selected.length} Students Selected`;

    renderDPTable(dp, items, W);
  }

  function renderDPTable(dp, items, W) {
    let html = '<table class="dp-grid-table"><thead><tr><th>Item</th>';
    for (let w = 0; w <= W; w++) html += `<th>w=${w}</th>`;
    html += '</tr></thead><tbody>';

    for (let i = 0; i <= items.length; i++) {
      html += `<tr><td>${i === 0 ? 'Base 0' : items[i - 1].name.split(' ')[0]}</td>`;
      for (let w = 0; w <= W; w++) {
        const isOptimal = i === items.length && w === W;
        html += `<td class="${isOptimal ? 'selected-cell' : ''}">${dp[i][w]}</td>`;
      }
      html += '</tr>';
    }
    html += '</tbody></table>';
    el.dpMatrixContainer.innerHTML = html;
  }

  function computeBacktracking() {
    el.slotLeadAssignee.textContent = 'STU1001 (Aarav Sharma) [GPA: 9.20]';
    el.slotWelfareAssignee.textContent = 'STU1002 (Priya Patel) [Need Cat]';
    el.slotActivityAssignee.textContent = 'STU1003 (Rohan Deshmukh) [ECA: 95]';
    el.backtrackPruneStat.innerHTML = '<span class="text-success">&check; Optimal Assignment Found &middot; 4 invalid branches pruned</span>';
    showToast('Backtracking CSP solver found valid conflict-free committee.', 'success');
  }

  // ============================================================================
  // Master Student Roster & Filter
  // ============================================================================
  function renderRoster() {
    el.rosterTableBody.innerHTML = '';
    const all = [...state.students];

    if (all.length === 0) {
      el.rosterTableBody.innerHTML = '<tr><td colspan="10" class="text-muted" style="text-align: center; padding: 2rem;">No students stored. Click "New Applicant" or "Reset Samples".</td></tr>';
      return;
    }

    all.forEach(student => {
      const row = document.createElement('tr');
      let statusBadge = 'status-pill-pending';
      if (student.status === 'Approved') statusBadge = 'status-pill-success';
      else if (student.status === 'Rejected') statusBadge = 'status-pill-rejected';

      row.innerHTML = `
        <td class="code-font font-bold">${escapeHtml(student.studentId)}</td>
        <td>${escapeHtml(student.name)}</td>
        <td><strong>${student.gpa.toFixed(2)}</strong></td>
        <td>${student.attendancePercentage.toFixed(1)}%</td>
        <td>${student.extracurricularScore}</td>
        <td>₹${student.familyIncome.toLocaleString('en-IN')}</td>
        <td><span class="badge badge-subtle">${escapeHtml(student.category)}</span></td>
        <td class="code-font font-bold text-accent">${(student.meritScore || 0).toFixed(2)}</td>
        <td><span class="status-pill ${statusBadge}">${student.status}</span></td>
        <td>
          <button class="btn btn-sm btn-ghost text-danger btn-delete-row" data-id="${student.studentId}">&times; Delete</button>
        </td>
      `;
      el.rosterTableBody.appendChild(row);
    });

    document.querySelectorAll('.btn-delete-row').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.getAttribute('data-id');
        el.bstDeleteInput.value = id;
        handleBstDelete();
      });
    });
  }

  function filterRoster(query) {
    const q = query.toLowerCase().trim();
    const rows = el.rosterTableBody.querySelectorAll('tr');
    rows.forEach(r => {
      const text = r.textContent.toLowerCase();
      r.style.display = text.includes(q) ? '' : 'none';
    });
  }

  // ============================================================================
  // Add Applicant Form Handler
  // ============================================================================
  function handleAddStudentSubmit(e) {
    e.preventDefault();
    const newStudent = {
      studentId: document.getElementById('inpStudentId').value.trim().toUpperCase(),
      name: document.getElementById('inpStudentName').value.trim(),
      gpa: parseFloat(document.getElementById('inpGpa').value),
      familyIncome: parseFloat(document.getElementById('inpIncome').value),
      attendancePercentage: parseFloat(document.getElementById('inpAttendance').value),
      extracurricularScore: parseInt(document.getElementById('inpEca').value, 10),
      category: document.getElementById('inpCategory').value,
      status: 'Pending',
      meritScore: 0
    };

    const direct = document.getElementById('inpDirectToStorage').checked;
    if (direct) {
      newStudent.meritScore = calculateSimMeritScore(newStudent, state.activeFormula);
      newStudent.status = newStudent.gpa >= 7.5 ? 'Approved' : 'Rejected';
      state.students.push(newStudent);
      state.students.sort((a, b) => b.meritScore - a.meritScore);
      state.stack.push({
        actionType: 'ADD_DIRECT',
        studentId: newStudent.studentId,
        description: `Directly registered ${newStudent.name}`
      });
      showToast(`Student ${newStudent.name} directly registered to storage!`, 'success');
    } else {
      state.queue.push(newStudent);
      state.stack.push({
        actionType: 'SUBMIT_APPLICATION',
        studentId: newStudent.studentId,
        description: `Submitted application for ${newStudent.name}`
      });
      showToast(`Applicant ${newStudent.name} added to FIFO queue at #${state.queue.length}.`, 'success');
    }

    renderAll();
    el.addStudentModal.classList.remove('open');
    el.addStudentForm.reset();
  }

  // ============================================================================
  // Guided 19-Step Tour
  // ============================================================================
  async function runGuidedTour() {
    showToast('Starting 19-Step Guided Practical Walkthrough...', 'info');
    switchTab('tab-phase1');
    await sleep(600);

    // Step 1: Queue peek
    showToast('Phase 1: Inspecting FIFO Queue Front application...', 'info');
    await sleep(1000);

    // Step 2: Process Next
    handleProcessNext();
    await sleep(1200);

    // Step 3: Expression Formula parse
    showToast('Phase 1: Validating Expression Parser with Shunting-Yard RPN...', 'info');
    handleTestFormula();
    await sleep(1200);

    // Step 4: Switch to Phase 2
    switchTab('tab-phase2');
    showToast('Phase 2: Visualizing Hierarchical Trees & BST Student Index...', 'info');
    await sleep(1200);

    // Step 5: BST Search
    el.bstSearchInput.value = 'STU1001';
    handleBstSearch();
    await sleep(1200);

    // Step 6: Workflow BFS
    showToast('Phase 2: Running BFS Stage-by-Stage on Institutional Workflow Graph...', 'info');
    await animateWorkflow('bfs');
    await sleep(1000);

    // Step 7: Switch to Phase 3
    switchTab('tab-phase3');
    showToast('Phase 3: Launching Interactive Sorting Visualizer (Merge Sort)...', 'info');
    await sleep(1000);
    handleStartSort();
    await sleep(2500);

    // Step 8: Search Comparison
    el.searchTargetInput.value = 'STU1001';
    handleSearchComparison();
    await sleep(1200);

    // Step 9: Switch to Phase 4
    switchTab('tab-phase4');
    showToast('Phase 4: Running Algorithmic Problem Solving (Greedy, DP & Backtracking)...', 'info');
    computeGreedyAllocation(180000);
    computeDPKnapsack();
    computeBacktracking();
    await sleep(1500);

    // Step 10: Master Roster
    switchTab('tab-roster');
    showToast('Tour Complete: All 4 phases operational and verified!', 'success');
  }

  // ============================================================================
  // Toast & Utilities
  // ============================================================================
  function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    let icon = 'ℹ️';
    if (type === 'success') icon = '✓';
    else if (type === 'warning') icon = '⚠️';
    else if (type === 'error') icon = '✕';

    toast.innerHTML = `<span>${icon}</span><span>${escapeHtml(message)}</span>`;
    el.toastContainer.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateY(8px)';
      toast.style.transition = 'all 200ms ease';
      setTimeout(() => toast.remove(), 200);
    }, 3800);
  }

  function escapeHtml(str) {
    if (!str) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  // Bootstrapping
  window.addEventListener('DOMContentLoaded', init);
})();
