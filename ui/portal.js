/**
 * EDUSCHOLAR PORTAL — Unified Data & Application Engine
 * Works seamlessly with the Java HTTP Web Server or 100% offline via localStorage.
 */

(function () {
  'use strict';

  // Available Schemes Catalog
  const SCHEMES = [
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
      description: 'Awarded to high-achieving undergraduate and postgraduate scholars in Science, Technology, Engineering, and Mathematics demonstrating research acumen.',
      tags: ['Engineering', 'Sciences', 'Innovation']
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
      description: 'Comprehensive financial grant assisting bright, deserving students from economically modest backgrounds to pursue higher education without debt.',
      tags: ['Financial Aid', 'All Degrees', 'Full Tuition']
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
      description: 'Promoting gender diversity and recognizing future female leaders in computing, artificial intelligence, and applied sciences.',
      tags: ['Women Scholars', 'Tech & AI', 'Mentorship']
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
      description: 'Dedicated financial support for ambitious students who are the first in their immediate family to enroll in a university degree program.',
      tags: ['First Generation', 'Undergraduate', 'Books & Living']
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
      description: 'Recognizing national and state-level achievements in athletics, creative arts, community leadership, and co-curricular domains.',
      tags: ['Athletics', 'Cultural', 'National Rank']
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
      description: 'Prestige grant supporting full-time postgraduate scholars and PhD candidates conducting patentable peer-reviewed research.',
      tags: ['Doctoral', 'Postgraduate', 'Stipend']
    }
  ];

  // Default Initial Sample Dataset
  const INITIAL_STUDENTS = [
    {
      studentId: 'STU1001',
      name: 'Aarav Sharma',
      college: 'National Institute of Technology',
      course: 'B.Tech Computer Science',
      gpa: 9.40,
      familyIncome: 280000,
      attendancePercentage: 92.5,
      extracurricularScore: 85.0,
      category: 'STEM Innovation & Research Grant',
      status: 'Approved',
      meritScore: 91.80,
      appliedAt: '2026-10-15',
      verifiedBy: 'Officer Verma (OFF-4029)',
      remarks: 'All documents verified. High merit score.'
    },
    {
      studentId: 'STU1002',
      name: 'Diya Patel',
      college: 'Government Medical College',
      course: 'MBBS - 2nd Year',
      gpa: 8.85,
      familyIncome: 190000,
      attendancePercentage: 95.0,
      extracurricularScore: 78.0,
      category: 'Merit-cum-Means Excellence Fellowship',
      status: 'Approved',
      meritScore: 86.65,
      appliedAt: '2026-10-16',
      verifiedBy: 'Officer Verma (OFF-4029)',
      remarks: 'Income certificate verified through revenue portal.'
    },
    {
      studentId: 'STU1003',
      name: 'Rohan Deshmukh',
      college: 'State Technical University',
      course: 'B.E. Mechanical',
      gpa: 7.90,
      familyIncome: 240000,
      attendancePercentage: 86.0,
      extracurricularScore: 68.0,
      category: 'First-Generation Higher Education Grant',
      status: 'Approved',
      meritScore: 77.10,
      appliedAt: '2026-10-18',
      verifiedBy: 'Officer Verma (OFF-4029)',
      remarks: 'First graduate certificate attached and authenticated.'
    },
    {
      studentId: 'STU1004',
      name: 'Ananya Sen',
      college: 'Delhi University',
      course: 'B.Sc Data Analytics',
      gpa: 9.10,
      familyIncome: 320000,
      attendancePercentage: 90.0,
      extracurricularScore: 92.0,
      category: 'Women in Technology & Leadership Award',
      status: 'Approved',
      meritScore: 91.10,
      appliedAt: '2026-10-19',
      verifiedBy: 'Officer Verma (OFF-4029)',
      remarks: 'Hackathon winner, documentation complete.'
    }
  ];

  const INITIAL_QUEUE = [
    {
      studentId: 'STU1005',
      name: 'Vikramaditya Roy',
      college: 'Birla Institute of Technology',
      course: 'B.Tech Electrical & Electronics',
      gpa: 8.60,
      familyIncome: 210000,
      attendancePercentage: 88.0,
      extracurricularScore: 74.0,
      category: 'Merit-cum-Means Excellence Fellowship',
      status: 'Pending',
      meritScore: 82.80,
      appliedAt: '2026-10-22',
      remarks: 'Pending officer document check'
    },
    {
      studentId: 'STU1006',
      name: 'Kavita Nair',
      college: 'Indian Institute of Space Science',
      course: 'M.Sc Astrophysics',
      gpa: 9.35,
      familyIncome: 450000,
      attendancePercentage: 94.0,
      extracurricularScore: 88.0,
      category: 'STEM Innovation & Research Grant',
      status: 'Pending',
      meritScore: 92.00,
      appliedAt: '2026-10-23',
      remarks: 'Pending committee verification'
    },
    {
      studentId: 'STU1007',
      name: 'Siddharth Mehra',
      college: 'St. Xavier College',
      course: 'B.Com Honours',
      gpa: 7.80,
      familyIncome: 180000,
      attendancePercentage: 84.0,
      extracurricularScore: 70.0,
      category: 'First-Generation Higher Education Grant',
      status: 'Under Review',
      meritScore: 76.80,
      appliedAt: '2026-10-24',
      remarks: 'Income affidavit renewal requested'
    },
    {
      studentId: 'STU1008',
      name: 'Pooja Kulkarni',
      college: 'Symbiosis Sports Academy',
      course: 'B.P.Ed Physical Education',
      gpa: 7.20,
      familyIncome: 390000,
      attendancePercentage: 91.0,
      extracurricularScore: 96.0,
      category: 'Sports & Arts Fellowship',
      status: 'Pending',
      meritScore: 83.00,
      appliedAt: '2026-10-25',
      remarks: 'National level athletics certificate attached'
    }
  ];

  const STORAGE_KEY = 'eduscholar_portal_db_v1';
  let isBackendConnected = false;

  // Initialize Local Storage
  function loadLocalDb() {
    try {
      const data = localStorage.getItem(STORAGE_KEY);
      if (data) {
        return JSON.parse(data);
      }
    } catch (e) {
      console.warn('LocalStorage error:', e);
    }

    const defaultDb = {
      students: INITIAL_STUDENTS,
      queue: INITIAL_QUEUE,
      auditStack: [
        {
          action: 'BATCH_APPROVAL',
          studentId: 'STU1004',
          description: 'Officer verified and approved Ananya Sen for Women in Tech Award',
          timestamp: new Date().toLocaleTimeString()
        }
      ]
    };
    saveLocalDb(defaultDb);
    return defaultDb;
  }

  function saveLocalDb(db) {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(db));
    } catch (e) {
      console.warn('Could not save to localStorage:', e);
    }
  }

  // Calculate composite Merit Index (0 - 100)
  function calculateMeritScore(gpa, eca, attendance) {
    const normGpa = Math.min(Math.max((gpa / 10.0), 0), 1) * 100.0;
    const score = (normGpa * 0.50) + (eca * 0.30) + (attendance * 0.20);
    return Math.round(score * 100) / 100;
  }

  // Check Backend Server Status
  async function checkBackend() {
    try {
      const res = await fetch('/api/status', { cache: 'no-store' });
      if (res.ok) {
        const json = await res.json();
        isBackendConnected = (json.status === 'ONLINE');
        return true;
      }
    } catch (e) {
      isBackendConnected = false;
    }
    return false;
  }

  // Public EduScholar API
  window.EduScholar = {
    schemes: SCHEMES,

    async init() {
      await checkBackend();
      this.updateNavbarBadges();
    },

    isServerOnline() {
      return isBackendConnected;
    },

    updateNavbarBadges() {
      const badge = document.getElementById('serverStatusBadge');
      if (badge) {
        if (isBackendConnected) {
          badge.innerHTML = '<span class="status-dot live" style="background:#10b981; width:8px; height:8px; border-radius:50%; display:inline-block; margin-right:6px;"></span><span style="font-size:0.78rem; font-weight:600; color:#065f46;">Live Portal Server</span>';
        } else {
          badge.innerHTML = '<span class="status-dot offline" style="background:#3b82f6; width:8px; height:8px; border-radius:50%; display:inline-block; margin-right:6px;"></span><span style="font-size:0.78rem; font-weight:600; color:#1e40af;">Client Database</span>';
        }
      }
    },

    getDb() {
      return loadLocalDb();
    },

    async getState() {
      if (isBackendConnected) {
        try {
          const res = await fetch('/api/state', { cache: 'no-store' });
          if (res.ok) {
            const data = await res.json();
            return {
              students: data.students || [],
              queue: data.queue || [],
              auditStack: data.stack || []
            };
          }
        } catch (e) {
          console.warn('Backend fetch failed, falling back to local database.');
        }
      }
      return loadLocalDb();
    },

    // Submit new scholarship application
    async submitApplication(data) {
      const gpa = parseFloat(data.gpa) || 7.0;
      const attendance = parseFloat(data.attendancePercentage) || 85.0;
      const eca = parseFloat(data.extracurricularScore) || 70.0;
      const income = parseFloat(data.familyIncome) || 250000;
      const merit = calculateMeritScore(gpa, eca, attendance);

      const db = loadLocalDb();
      const nextId = 'STU' + (1000 + db.students.length + db.queue.length + 1);

      const newStudent = {
        studentId: nextId,
        name: data.name,
        email: data.email || 'applicant@student.edu',
        phone: data.phone || '+91 98765 43210',
        college: data.college || 'State University',
        course: data.course || 'Undergraduate Degree',
        gpa: gpa,
        familyIncome: income,
        attendancePercentage: attendance,
        extracurricularScore: eca,
        category: data.category || 'STEM Innovation & Research Grant',
        status: 'Pending',
        meritScore: merit,
        appliedAt: new Date().toISOString().split('T')[0],
        remarks: 'Application submitted successfully. Awaiting officer verification.'
      };

      if (isBackendConnected) {
        try {
          await fetch('/api/apply', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newStudent)
          });
        } catch (e) {
          console.warn('Could not post to backend:', e);
        }
      }

      // Keep local in sync
      db.queue.push(newStudent);
      db.auditStack.unshift({
        action: 'SUBMIT_APPLICATION',
        studentId: newStudent.studentId,
        description: `New online application submitted by ${newStudent.name} (${newStudent.studentId})`,
        timestamp: new Date().toLocaleTimeString()
      });
      saveLocalDb(db);

      return {
        success: true,
        studentId: newStudent.studentId,
        queuePosition: db.queue.length,
        student: newStudent
      };
    },

    // Verify application (Officer Action)
    async verifyApplication(studentId, decision, remarks = '') {
      const db = loadLocalDb();
      let target = null;
      let fromQueue = false;

      // Check queue
      const qIndex = db.queue.findIndex(s => s.studentId === studentId);
      if (qIndex !== -1) {
        target = db.queue.splice(qIndex, 1)[0];
        fromQueue = true;
      } else {
        target = db.students.find(s => s.studentId === studentId);
      }

      if (!target) {
        return { success: false, message: 'Applicant record not found' };
      }

      target.status = decision; // 'Approved', 'Rejected', 'Under Review'
      target.verifiedBy = 'Officer Verma (OFF-4029)';
      target.remarks = remarks || (decision === 'Approved' ? 'Application meets all eligibility thresholds. Grant sanctioned.' : 'Document discrepancies noted.');

      if (fromQueue) {
        db.students.push(target);
      }

      db.auditStack.unshift({
        action: 'OFFICER_DECISION',
        studentId: target.studentId,
        description: `Officer marked ${target.name} (${target.studentId}) as ${decision.toUpperCase()}`,
        timestamp: new Date().toLocaleTimeString(),
        previousData: { ...target }
      });

      saveLocalDb(db);

      if (isBackendConnected) {
        try {
          await fetch('/api/verify', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              studentId: target.studentId,
              status: decision,
              remarks: remarks
            })
          });
        } catch (e) {
          console.warn('Backend verify call failed:', e);
        }
      }

      return { success: true, student: target, message: `Applicant ${target.name} is now ${decision}.` };
    },

    // Undo last officer action
    async undoLastAction() {
      const db = loadLocalDb();
      if (!db.auditStack || db.auditStack.length === 0) {
        return { success: false, message: 'No actions available to rollback.' };
      }

      const lastAction = db.auditStack.shift();
      if (lastAction.studentId) {
        const student = db.students.find(s => s.studentId === lastAction.studentId);
        if (student) {
          // Move back to queue
          const idx = db.students.indexOf(student);
          if (idx !== -1) db.students.splice(idx, 1);
          student.status = 'Pending';
          db.queue.unshift(student);
        }
      }

      saveLocalDb(db);

      if (isBackendConnected) {
        try {
          await fetch('/api/undo', { method: 'POST' });
        } catch (e) {
          console.warn('Backend undo failed:', e);
        }
      }

      return { success: true, message: `Rolled back: ${lastAction.description}` };
    },

    // Look up application for status tracking
    async trackApplication(identifier) {
      if (!identifier) return null;
      const clean = identifier.trim().toUpperCase();

      const db = loadLocalDb();
      // Look in queue
      let found = db.queue.find(s => s.studentId.toUpperCase() === clean || s.name.toUpperCase().includes(clean));
      let inQueue = true;

      // Look in approved/reviewed storage
      if (!found) {
        found = db.students.find(s => s.studentId.toUpperCase() === clean || s.name.toUpperCase().includes(clean));
        inQueue = false;
      }

      if (!found) return null;

      // Determine stages
      const stages = [
        { name: 'Application Submitted', desc: 'Received online', completed: true, date: found.appliedAt || '2026-10-20' },
        { name: 'Document Verification', desc: 'Authenticated by Registrar', completed: found.status !== 'Pending', date: found.status !== 'Pending' ? 'Verified' : 'In Progress' },
        { name: 'Academic & Financial Audit', desc: 'Income & CGPA validation', completed: found.status === 'Approved' || found.status === 'Under Review', date: found.status === 'Approved' ? 'Cleared' : 'Under Review' },
        { name: 'Committee Review', desc: 'Allocation approval', completed: found.status === 'Approved', date: found.status === 'Approved' ? 'Approved' : 'Pending' },
        { name: 'Grant Disbursement', desc: 'Direct Benefit Transfer (DBT)', completed: found.status === 'Approved', date: found.status === 'Approved' ? 'Sanctioned' : 'Awaiting' }
      ];

      return {
        student: found,
        inQueue: inQueue,
        stages: stages,
        scheme: SCHEMES.find(s => s.name === found.category) || SCHEMES[0]
      };
    },

    // Reset database to initial sample dataset
    async resetData() {
      const defaultDb = {
        students: JSON.parse(JSON.stringify(INITIAL_STUDENTS)),
        queue: JSON.parse(JSON.stringify(INITIAL_QUEUE)),
        auditStack: [
          {
            action: 'RESET_SYSTEM',
            studentId: 'SYSTEM',
            description: 'System database restored to default institutional roster',
            timestamp: new Date().toLocaleTimeString()
          }
        ]
      };
      saveLocalDb(defaultDb);

      if (isBackendConnected) {
        try {
          await fetch('/api/reset', { method: 'POST' });
        } catch (e) {}
      }

      return { success: true, message: 'Database reset to default official roster.' };
    },

    showToast(message, type = 'info') {
      let container = document.getElementById('toastContainer');
      if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container';
        document.body.appendChild(container);
      }

      const toast = document.createElement('div');
      toast.className = `toast ${type}`;
      let icon = 'ℹ️';
      if (type === 'success') icon = '✓';
      if (type === 'error') icon = '✕';
      if (type === 'warning') icon = '⚠️';

      toast.innerHTML = `<span>${icon}</span><span>${escapeHtml(message)}</span>`;
      container.appendChild(toast);

      setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        toast.style.transition = 'all 0.25s ease';
        setTimeout(() => toast.remove(), 250);
      }, 3500);
    }
  };

  function escapeHtml(str) {
    if (!str) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  // Auto-init on page load
  document.addEventListener('DOMContentLoaded', () => {
    EduScholar.init();
  });
})();
