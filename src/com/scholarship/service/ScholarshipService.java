package com.scholarship.service;

import com.scholarship.algorithms.BacktrackingCommitteeAssigner;
import com.scholarship.algorithms.DynamicProgrammingScholarship;
import com.scholarship.algorithms.GreedyScholarshipAllocator;
import com.scholarship.datastructures.LinkedQueue;
import com.scholarship.datastructures.LinkedStack;
import com.scholarship.datastructures.StudentLinkedList;
import com.scholarship.expression.ExpressionParser;
import com.scholarship.graph.VerificationWorkflowGraph;
import com.scholarship.model.ActionRecord;
import com.scholarship.model.StudentRecord;
import com.scholarship.searchsort.AlgorithmBenchmark;
import com.scholarship.searchsort.SearchAlgorithms;
import com.scholarship.searchsort.SortAlgorithms;
import com.scholarship.tree.ScholarshipCategoryTree;
import com.scholarship.tree.StudentBST;

import java.util.*;

/**
 * Unified Coordinator for all 4 DSA Practical Phases:
 * Phase 1: Linear Data Structures (Linked List, FIFO Queue, LIFO Stack, Expression Parser)
 * Phase 2: Hierarchical & Graph Structures (BST, Category Binary Tree, Workflow Graph)
 * Phase 3: Searching & Sorting Operations (Linear & Binary Search, Merge/Quick/Insertion Sort, Benchmark)
 * Phase 4: Basic Algorithmic Problem Solving (Greedy, Dynamic Programming, Backtracking)
 */
public class ScholarshipService {

    public static final String DEFAULT_FORMULA = "((gpa / 10.0) * 50.0) + (eca * 0.30) + (attendance * 0.20)";

    private final StudentLinkedList studentStorage;
    private final LinkedQueue<StudentRecord> applicationQueue;
    private final LinkedStack<ActionRecord> actionHistoryStack;
    private final StudentBST studentBST;
    private final ScholarshipCategoryTree categoryTree;
    private final VerificationWorkflowGraph workflowGraph;
    private String activeFormula;

    public ScholarshipService() {
        this.studentStorage = new StudentLinkedList();
        this.applicationQueue = new LinkedQueue<>();
        this.actionHistoryStack = new LinkedStack<>();
        this.studentBST = new StudentBST();
        this.categoryTree = new ScholarshipCategoryTree();
        this.workflowGraph = new VerificationWorkflowGraph();
        this.activeFormula = DEFAULT_FORMULA;
    }

    public StudentLinkedList getStudentStorage() { return studentStorage; }
    public LinkedQueue<StudentRecord> getApplicationQueue() { return applicationQueue; }
    public LinkedStack<ActionRecord> getActionHistoryStack() { return actionHistoryStack; }
    public StudentBST getStudentBST() { return studentBST; }
    public ScholarshipCategoryTree getCategoryTree() { return categoryTree; }
    public VerificationWorkflowGraph getWorkflowGraph() { return workflowGraph; }
    public String getActiveFormula() { return activeFormula; }

    // =========================================================================
    // 1. Queue Management (FIFO Pipeline)
    // =========================================================================
    public String submitApplication(StudentRecord student) {
        student.setStatus("Pending");
        applicationQueue.enqueue(student);

        ActionRecord action = new ActionRecord(
                "SUBMIT_APPLICATION",
                student.getStudentId(),
                "Submitted scholarship application for " + student.getName(),
                null,
                student
        );
        actionHistoryStack.push(action);

        return String.format("Application for %s (ID: %s) queued at position #%d.",
                student.getName(), student.getStudentId(), applicationQueue.size());
    }

    public StudentRecord peekNextInQueue() {
        return applicationQueue.isEmpty() ? null : applicationQueue.peekFront();
    }

    public List<StudentRecord> getQueueRecords() {
        return applicationQueue.toList();
    }

    // =========================================================================
    // 2. Application Processing & Merit Evaluation
    // =========================================================================
    public static class ProcessResult {
        public final boolean success;
        public final StudentRecord student;
        public final String message;
        public final ExpressionParser.ParseResult formulaResult;

        public ProcessResult(boolean success, StudentRecord student, String message, ExpressionParser.ParseResult formulaResult) {
            this.success = success;
            this.student = student;
            this.message = message;
            this.formulaResult = formulaResult;
        }
    }

    public ProcessResult processNextApplication(String customFormula) {
        if (applicationQueue.isEmpty()) {
            return new ProcessResult(false, null, "Application queue is empty.", null);
        }

        StudentRecord student = applicationQueue.dequeue();
        String formula = (customFormula != null && !customFormula.trim().isEmpty()) ? customFormula : activeFormula;

        // Evaluate formula using Stack Expression Parser
        Map<String, Double> vars = new HashMap<>();
        vars.put("gpa", student.getGpa());
        vars.put("eca", student.getExtracurricularScore());
        vars.put("attendance", student.getAttendancePercentage());
        vars.put("income", student.getFamilyIncome());

        ExpressionParser.ParseResult parseRes = null;
        try {
            parseRes = ExpressionParser.evaluateFormula(formula, vars);
            double calculatedScore = Math.min(Math.max(parseRes.result, 0.0), 100.0);
            student.setMeritScore(Math.round(calculatedScore * 100.0) / 100.0);
        } catch (Exception e) {
            student.setMeritScore(student.computeDefaultMeritScore());
        }

        // Validate category criteria
        StudentRecord.EligibilityResult elig = student.checkCategoryEligibility();
        student.setStatus(elig.isEligible ? "Approved" : "Rejected");

        // Insert into Singly Linked List (Sorted by Merit Score)
        studentStorage.insertSortedByMerit(student);
        // Also insert/index into Binary Search Tree for O(log N) fast access
        studentBST.insert(student);

        // Record Undo action in Stack
        ActionRecord action = new ActionRecord(
                "PROCESS_APPLICATION",
                student.getStudentId(),
                String.format("Processed application for %s -> %s (Merit: %.2f)", student.getName(), student.getStatus(), student.getMeritScore()),
                null,
                student
        );
        actionHistoryStack.push(action);

        String msg = String.format("Processed %s (ID: %s) -> Status: [%s]. Reason: %s. Merit Score: %.2f",
                student.getName(), student.getStudentId(), student.getStatus().toUpperCase(), elig.reason, student.getMeritScore());

        return new ProcessResult(true, student, msg, parseRes);
    }

    public ProcessResult processNextApplication() {
        return processNextApplication(null);
    }

    // =========================================================================
    // 3. Linked List Storage Operations (CRUD)
    // =========================================================================
    public void addDirectRecord(StudentRecord student, boolean sorted) {
        if (sorted) {
            studentStorage.insertSortedByMerit(student);
        } else {
            studentStorage.insertAtTail(student);
        }
        studentBST.insert(student);

        ActionRecord action = new ActionRecord(
                "ADD_DIRECT",
                student.getStudentId(),
                "Directly added student " + student.getName(),
                null,
                student
        );
        actionHistoryStack.push(action);
    }

    public StudentRecord searchStudent(String studentId) {
        return studentStorage.searchById(studentId);
    }

    public List<StudentRecord> searchStudentByName(String query) {
        return studentStorage.searchByName(query);
    }

    public boolean deleteStudent(String studentId) {
        StudentRecord deleted = studentStorage.deleteById(studentId);
        if (deleted != null) {
            studentBST.delete(studentId);
            ActionRecord action = new ActionRecord(
                    "DELETE_STUDENT",
                    studentId,
                    "Deleted student " + deleted.getName(),
                    deleted,
                    null
            );
            actionHistoryStack.push(action);
            return true;
        }
        return false;
    }

    public boolean updateStudentStatus(String studentId, String newStatus) {
        StudentRecord existing = studentStorage.searchById(studentId);
        if (existing != null) {
            StudentRecord previousState = new StudentRecord(existing);
            existing.setStatus(newStatus);

            ActionRecord action = new ActionRecord(
                    "UPDATE_STATUS",
                    studentId,
                    String.format("Updated status of %s to '%s'", existing.getName(), newStatus),
                    previousState,
                    existing
            );
            actionHistoryStack.push(action);
            return true;
        }
        return false;
    }

    // =========================================================================
    // 4. Stack-Based Undo Operations
    // =========================================================================
    public static class UndoResult {
        public final boolean success;
        public final String message;

        public UndoResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    public UndoResult undoLastAction() {
        if (actionHistoryStack.isEmpty()) {
            return new UndoResult(false, "Undo Stack is empty. No actions to revert.");
        }

        ActionRecord lastAction = actionHistoryStack.pop();

        switch (lastAction.getActionType()) {
            case "PROCESS_APPLICATION": {
                // Delete from linked list and BST, return to front of queue
                studentStorage.deleteById(lastAction.getStudentId());
                studentBST.delete(lastAction.getStudentId());
                StudentRecord restored = lastAction.getNewStudentState();
                if (restored != null) {
                    restored.setStatus("Pending");
                    List<StudentRecord> currQueue = applicationQueue.toList();
                    applicationQueue.clear();
                    applicationQueue.enqueue(restored);
                    for (StudentRecord item : currQueue) {
                        applicationQueue.enqueue(item);
                    }
                }
                return new UndoResult(true, "Reverted application processing for " + lastAction.getStudentId() + ". Returned to queue front.");
            }

            case "SUBMIT_APPLICATION": {
                List<StudentRecord> items = applicationQueue.toList();
                applicationQueue.clear();
                for (StudentRecord r : items) {
                    if (!r.getStudentId().equalsIgnoreCase(lastAction.getStudentId())) {
                        applicationQueue.enqueue(r);
                    }
                }
                return new UndoResult(true, "Reverted submission for student " + lastAction.getStudentId() + ".");
            }

            case "DELETE_STUDENT": {
                if (lastAction.getPreviousStudentState() != null) {
                    studentStorage.insertSortedByMerit(lastAction.getPreviousStudentState());
                    studentBST.insert(lastAction.getPreviousStudentState());
                    return new UndoResult(true, "Restored deleted student " + lastAction.getPreviousStudentState().getName() + " to storage.");
                }
                return new UndoResult(false, "Failed to restore deleted record: missing previous state.");
            }

            case "ADD_DIRECT": {
                studentStorage.deleteById(lastAction.getStudentId());
                studentBST.delete(lastAction.getStudentId());
                return new UndoResult(true, "Reverted direct addition of student " + lastAction.getStudentId() + ".");
            }

            case "UPDATE_STATUS": {
                if (lastAction.getPreviousStudentState() != null) {
                    StudentRecord record = studentStorage.searchById(lastAction.getStudentId());
                    if (record != null) {
                        record.setStatus(lastAction.getPreviousStudentState().getStatus());
                        studentBST.insert(record);
                        return new UndoResult(true, "Reverted status of " + lastAction.getStudentId() + " back to '" + record.getStatus() + "'.");
                    }
                }
                return new UndoResult(false, "Could not revert status update.");
            }

            default:
                return new UndoResult(false, "Unknown action type: " + lastAction.getActionType());
        }
    }

    public List<ActionRecord> getActionHistory() {
        return actionHistoryStack.toList();
    }

    // =========================================================================
    // 5. Formula Customization & Re-evaluation
    // =========================================================================
    public boolean setActiveFormula(String formula) {
        if (!ExpressionParser.checkBalancedDelimiters(formula)) {
            return false;
        }
        try {
            Map<String, Double> testVars = new HashMap<>();
            testVars.put("gpa", 8.5);
            testVars.put("eca", 80.0);
            testVars.put("attendance", 90.0);
            testVars.put("income", 300000.0);
            ExpressionParser.evaluateFormula(formula, testVars);
            this.activeFormula = formula;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int recalculateAllMeritScores() {
        List<StudentRecord> records = studentStorage.toList();
        studentStorage.clear();

        for (StudentRecord r : records) {
            Map<String, Double> vars = new HashMap<>();
            vars.put("gpa", r.getGpa());
            vars.put("eca", r.getExtracurricularScore());
            vars.put("attendance", r.getAttendancePercentage());
            vars.put("income", r.getFamilyIncome());

            try {
                ExpressionParser.ParseResult res = ExpressionParser.evaluateFormula(activeFormula, vars);
                double score = Math.min(Math.max(res.result, 0.0), 100.0);
                r.setMeritScore(Math.round(score * 100.0) / 100.0);
            } catch (Exception e) {
                r.setMeritScore(r.computeDefaultMeritScore());
            }

            studentStorage.insertSortedByMerit(r);
        }
        studentBST.syncFromLinkedList(studentStorage);
        return records.size();
    }

    // =========================================================================
    // 6. Sample Dataset Loader
    // =========================================================================
    public int loadSampleData(boolean loadIntoQueue) {
        List<StudentRecord> samples = Arrays.asList(
                new StudentRecord("STU1001", "Aarav Sharma", 9.20, 350000.0, 96.5, 88.0, "Merit-Based", "Pending"),
                new StudentRecord("STU1002", "Priya Patel", 8.50, 220000.0, 92.0, 70.0, "Need-Based", "Pending"),
                new StudentRecord("STU1003", "Rohan Deshmukh", 7.40, 450000.0, 88.5, 95.0, "Sports/ECA", "Pending"),
                new StudentRecord("STU1004", "Ananya Sen", 9.50, 550000.0, 94.0, 82.0, "Merit-Based", "Pending"),
                new StudentRecord("STU1005", "Vikram Malhotra", 5.20, 780000.0, 68.0, 45.0, "Merit-Based", "Pending"),
                new StudentRecord("STU1006", "Sneha Kulkarni", 8.20, 180000.0, 91.0, 65.0, "Need-Based", "Pending"),
                new StudentRecord("STU1007", "Kabir Mehta", 7.10, 420000.0, 85.0, 92.0, "Sports/ECA", "Pending"),
                new StudentRecord("STU1008", "Divya Nair", 9.80, 280000.0, 98.0, 90.0, "Merit-Based", "Pending")
        );

        for (StudentRecord s : samples) {
            if (loadIntoQueue) {
                applicationQueue.enqueue(s);
            } else {
                studentStorage.insertSortedByMerit(s);
                studentBST.insert(s);
            }
        }
        return samples.size();
    }

    // =========================================================================
    // 7. Phase 2: Hierarchical (Tree) and Graph Operations (CLO2)
    // =========================================================================

    public StudentRecord searchStudentFastBST(String studentId) {
        return studentBST.search(studentId);
    }

    public List<StudentRecord> getBSTInorderStudents() {
        return studentBST.inorder();
    }

    public List<StudentRecord> getBSTPreorderStudents() {
        return studentBST.preorder();
    }

    public List<StudentRecord> getBSTPostorderStudents() {
        return studentBST.postorder();
    }

    public int syncBSTFromStorage() {
        return studentBST.syncFromLinkedList(studentStorage);
    }

    public List<ScholarshipCategoryTree.CategoryData> findEligibleCategoriesForStudent(StudentRecord student) {
        return categoryTree.findEligibleCategories(student);
    }

    public List<String> runWorkflowBFS(String startStage) {
        return workflowGraph.bfs(startStage != null ? startStage : VerificationWorkflowGraph.STAGE_PORTAL);
    }

    public List<String> runWorkflowDFS(String startStage) {
        return workflowGraph.dfs(startStage != null ? startStage : VerificationWorkflowGraph.STAGE_PORTAL);
    }

    public List<String> findWorkflowVerificationPath(String source, String destination) {
        return workflowGraph.findVerificationPath(source, destination);
    }

    // =========================================================================
    // 8. Phase 3: Searching & Sorting Operations (CLO2)
    // =========================================================================

    public SearchAlgorithms.SearchResult searchStudentLinear(String studentId) {
        StudentRecord[] array = studentStorage.toArray();
        return SearchAlgorithms.linearSearchById(array, studentId);
    }

    public SearchAlgorithms.SearchResult searchStudentBinary(String studentId) {
        StudentRecord[] array = studentStorage.toArray();
        SortAlgorithms.quickSort(array, SortAlgorithms.BY_ID_ASC);
        return SearchAlgorithms.binarySearchById(array, studentId);
    }

    public List<StudentRecord> searchStudentsByName(String query) {
        StudentRecord[] array = studentStorage.toArray();
        return SearchAlgorithms.linearSearchByName(array, query);
    }

    public StudentRecord[] sortStudentsMerge(boolean descending) {
        StudentRecord[] array = studentStorage.toArray();
        SortAlgorithms.mergeSort(array, descending ? SortAlgorithms.BY_MERIT_DESC : SortAlgorithms.BY_MERIT_ASC);
        return array;
    }

    public StudentRecord[] sortStudentsQuick(boolean byId) {
        StudentRecord[] array = studentStorage.toArray();
        SortAlgorithms.quickSort(array, byId ? SortAlgorithms.BY_ID_ASC : SortAlgorithms.BY_GPA_DESC);
        return array;
    }

    public StudentRecord[] sortStudentsInsertion() {
        StudentRecord[] array = studentStorage.toArray();
        SortAlgorithms.insertionSort(array, SortAlgorithms.BY_GPA_DESC);
        return array;
    }

    public AlgorithmBenchmark.BenchmarkReport runAlgorithmBenchmark(int size) {
        return AlgorithmBenchmark.runBenchmark(size);
    }

    // =========================================================================
    // 9. Phase 4: Basic Algorithmic Problem Solving (CLO3)
    // =========================================================================

    public GreedyScholarshipAllocator.AllocationResult allocateBudgetGreedy(double totalBudget, List<Double> requests) {
        List<StudentRecord> students = studentStorage.toList();
        return GreedyScholarshipAllocator.allocateBudget(students, requests, totalBudget);
    }

    public DynamicProgrammingScholarship.DPResult optimizeDiscreteGrantsDP(int[] grantCosts, int budgetCapacity) {
        List<StudentRecord> students = studentStorage.toList();
        return DynamicProgrammingScholarship.solveDiscreteGrants(students, grantCosts, budgetCapacity);
    }

    public BacktrackingCommitteeAssigner.AssignmentResult assignCommitteeBacktracking(List<BacktrackingCommitteeAssigner.SlotRequirement> slots) {
        List<StudentRecord> students = studentStorage.toList();
        BacktrackingCommitteeAssigner assigner = new BacktrackingCommitteeAssigner();
        return assigner.assignCommitteeSlots(students, slots);
    }
}
