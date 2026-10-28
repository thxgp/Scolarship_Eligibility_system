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
                new StudentRecord("26MCAD01", "Aarav Sharma", 9.40, 280000.0, 92.5, 85.0, "STEM Innovation & Research Grant", "Approved"),
                new StudentRecord("26MCAD02", "Diya Patel", 8.85, 190000.0, 95.0, 78.0, "Merit-cum-Means Excellence Fellowship", "Approved"),
                new StudentRecord("26MCAD03", "Rohan Deshmukh", 7.90, 240000.0, 86.0, 68.0, "First-Generation Higher Education Grant", "Approved"),
                new StudentRecord("26MCAD04", "Ananya Sen", 9.10, 320000.0, 90.0, 92.0, "Women in Technology & Leadership Award", "Approved"),
                new StudentRecord("26MCAD05", "Karthik Raja", 9.25, 210000.0, 94.0, 80.0, "STEM Innovation & Research Grant", "Approved"),
                new StudentRecord("26MCAD06", "Meera Nair", 9.60, 400000.0, 96.0, 88.0, "STEM Innovation & Research Grant", "Approved"),
                new StudentRecord("26MCAD07", "Tanvi Hegde", 9.30, 260000.0, 91.0, 84.0, "Women in Technology & Leadership Award", "Approved"),
                new StudentRecord("26MCAD08", "Yashvardhan Joshi", 8.75, 310000.0, 89.0, 76.0, "Merit-cum-Means Excellence Fellowship", "Approved"),
                new StudentRecord("26MCAD09", "Fatimah Sheikh", 8.65, 180000.0, 93.0, 72.0, "First-Generation Higher Education Grant", "Approved"),
                new StudentRecord("26MCAD10", "Arjun Sengupta", 9.05, 350000.0, 90.5, 86.0, "STEM Innovation & Research Grant", "Approved"),
                new StudentRecord("26MCAD11", "Ritu Chauhan", 8.40, 160000.0, 94.5, 70.0, "First-Generation Higher Education Grant", "Approved"),
                new StudentRecord("26MCAD12", "Devendra Rao", 6.20, 380000.0, 68.0, 55.0, "Sports & Arts Fellowship", "Rejected"),
                new StudentRecord("26MCAD13", "Sanjana Reddy", 9.50, 480000.0, 95.0, 90.0, "Women in Technology & Leadership Award", "Approved"),
                new StudentRecord("26MCAD14", "Mohammed Zaid", 8.90, 850000.0, 91.5, 74.0, "Merit-cum-Means Excellence Fellowship", "Rejected"),
                new StudentRecord("26MCAD15", "Swati Bhattacharya", 9.70, 550000.0, 98.0, 94.0, "National Doctoral Research Stipend", "Approved"),
                new StudentRecord("26MCAD16", "Vikramaditya Roy", 5.80, 210000.0, 88.0, 74.0, "STEM Innovation & Research Grant", "Rejected"),
                new StudentRecord("26MCAD17", "Kavita Nair", 9.35, 450000.0, 94.0, 88.0, "STEM Innovation & Research Grant", "Pending"),
                new StudentRecord("26MCAD18", "Siddharth Mehra", 7.80, 180000.0, 84.0, 70.0, "First-Generation Higher Education Grant", "Under Review"),
                new StudentRecord("26MCAD19", "Pooja Kulkarni", 7.20, 390000.0, 91.0, 96.0, "Sports & Arts Fellowship", "Pending"),
                new StudentRecord("26MCAD20", "Aditya Verma", 8.95, 340000.0, 89.5, 82.0, "STEM Innovation & Research Grant", "Approved"),
                new StudentRecord("26MCAD21", "Shalini Iyer", 8.80, 220000.0, 93.0, 75.0, "Merit-cum-Means Excellence Fellowship", "Under Review"),
                new StudentRecord("26MCAD22", "Harshil Chawla", 7.10, 720000.0, 82.0, 65.0, "First-Generation Higher Education Grant", "Rejected"),
                new StudentRecord("26MCAD23", "Neha Aggarwal", 9.15, 290000.0, 92.0, 88.0, "Women in Technology & Leadership Award", "Pending"),
                new StudentRecord("26MCAD24", "Pranav Kulkarni", 7.60, 270000.0, 85.0, 72.0, "First-Generation Higher Education Grant", "Under Review"),
                new StudentRecord("26MCAD25", "Gayatri Sundaram", 8.10, 320000.0, 90.0, 94.0, "Sports & Arts Fellowship", "Pending"),
                new StudentRecord("26MCAD26", "Abhinav Menon", 6.40, 620000.0, 72.0, 60.0, "Merit-cum-Means Excellence Fellowship", "Rejected"),
                new StudentRecord("26MCAD27", "Pallavi Jha", 9.00, 310000.0, 95.0, 80.0, "Women in Technology & Leadership Award", "Pending"),
                new StudentRecord("26MCAD28", "Tarun Balakrishnan", 8.70, 460000.0, 88.5, 86.0, "STEM Innovation & Research Grant", "Pending"),
                new StudentRecord("26MCAD29", "Snehal Patil", 6.10, 150000.0, 64.0, 52.0, "First-Generation Higher Education Grant", "Rejected"),
                new StudentRecord("26MCAD30", "Ishan Gupta", 9.65, 500000.0, 97.0, 92.0, "National Doctoral Research Stipend", "Pending")
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

    public int loadFromCsv(java.nio.file.Path csvPath, boolean loadIntoQueue) throws java.io.IOException {
        if (!java.nio.file.Files.exists(csvPath)) return 0;
        List<String> lines = java.nio.file.Files.readAllLines(csvPath);
        int count = 0;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(",");
            if (parts.length >= 10) {
                String id = parts[0].trim();
                String name = parts[1].trim();
                // parts[2]=college, parts[3]=course
                double gpa = Double.parseDouble(parts[4].trim());
                double income = Double.parseDouble(parts[5].trim());
                double att = Double.parseDouble(parts[6].trim());
                double eca = Double.parseDouble(parts[7].trim());
                String cat = parts[8].trim();
                String status = parts[9].trim();
                StudentRecord sr = new StudentRecord(id, name, gpa, income, att, eca, cat, status);
                if (loadIntoQueue) {
                    applicationQueue.enqueue(sr);
                } else {
                    studentStorage.insertSortedByMerit(sr);
                    studentBST.insert(sr);
                }
                count++;
            }
        }
        return count;
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
