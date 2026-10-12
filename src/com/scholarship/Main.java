package com.scholarship;

import com.scholarship.expression.ExpressionParser;
import com.scholarship.model.ActionRecord;
import com.scholarship.model.StudentRecord;
import com.scholarship.service.ScholarshipService;
import com.scholarship.test.TestSuite;

import java.util.*;

/**
 * Interactive Console Application for Phase 1: Linear Data Structures
 * Student Scholarship Eligibility and Merit System (26MCC1L211: CLO1)
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ScholarshipService service = new ScholarshipService();
        printBanner();

        while (true) {
            displayMenu();
            System.out.print("Enter your choice (0-17): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": handleViewRecords(service); break;
                case "2": handleSubmitApplication(service); break;
                case "3": handleViewQueue(service); break;
                case "4": handleProcessApplication(service); break;
                case "5": handleSearch(service); break;
                case "6": handleDelete(service); break;
                case "7": handleUndo(service); break;
                case "8": handleViewHistory(service); break;
                case "9": handleFormulaEvaluator(service); break;
                case "10": handleQuickDemo(service); break;
                case "11": handleLoadSampleData(service); break;
                case "12": handleRunTests(); break;
                case "13": handleBSTOperations(service); break;
                case "14": handleTreeTraversals(service); break;
                case "15": handleCategoryHierarchy(service); break;
                case "16": handleWorkflowGraph(service); break;
                case "17": handleGraphTraversals(service); break;
                case "0":
                    System.out.println("\nExiting Student Scholarship System. Goodbye!\n");
                    return;
                default:
                    System.out.println("[ERROR] Invalid choice. Please enter a number between 0 and 17.");
            }
        }
    }

    private static void printBanner() {
        System.out.println("================================================================================");
        System.out.println("   STUDENT SCHOLARSHIP ELIGIBILITY & MERIT SYSTEM (PHASE 1 & 2 - JAVA)");
        System.out.println("   Linear Data Structures (CLO1) & Hierarchical/Graph Structures (CLO2)");
        System.out.println("   [ Linked List | Stack | Queue | BST | Category Binary Tree | Workflow Graph ]");
        System.out.println("================================================================================");
    }

    private static void displayMenu() {
        System.out.println("\n  === PHASE 1: LINEAR DATA STRUCTURES (CLO1) ===");
        System.out.println("  [1] View All Stored Student Records (Linked List Traversal)");
        System.out.println("  [2] Submit New Scholarship Application (Enqueue to FIFO Queue)");
        System.out.println("  [3] View Application Verification Queue (FIFO Display)");
        System.out.println("  [4] Process Next Application (Evaluate & Store in Sorted Linked List)");
        System.out.println("  [5] Search Student Record (Linear Search in Linked List)");
        System.out.println("  [6] Delete Student Record from Storage (Linked List Deletion)");
        System.out.println("  [7] Undo Last Action (LIFO Action Stack Pop)");
        System.out.println("  [8] View Action History (Audit Stack Display)");
        System.out.println("  [9] Test / Configure Merit Formula (Stack-Based Expression Evaluator)");
        System.out.println("\n  === PHASE 2: HIERARCHICAL & GRAPH STRUCTURES (CLO2) ===");
        System.out.println("  [13] Binary Search Tree (BST): Fast Search O(log N), Insert, 3-Case Delete, Tree View");
        System.out.println("  [14] Tree Traversals: Inorder, Preorder, Postorder (BST & Category Tree)");
        System.out.println("  [15] Scholarship Category Binary Tree Hierarchy & Student Eligibility Check");
        System.out.println("  [16] Verification Workflow Graph: Adjacency List & Matrix View");
        System.out.println("  [17] Graph Traversals: BFS Level Traversal & DFS Deep Audit Traversal");
        System.out.println("\n  === SYSTEM UTILITIES ===");
        System.out.println("  [10] Run Automated Quick Demo (Full Phase 1 & Phase 2 End-to-End Walkthrough)");
        System.out.println("  [11] Load Sample Student Dataset");
        System.out.println("  [12] Run Automated Unit Test Suite (CLO1 & CLO2)");
        System.out.println("  [0] Exit\n");
    }

    private static void handleViewRecords(ScholarshipService service) {
        System.out.println("\n--- STORED STUDENT RECORDS (LINKED LIST) ---");
        System.out.println(service.getStudentStorage().displayTabular());
    }

    private static void handleSubmitApplication(ScholarshipService service) {
        System.out.println("\n--- SUBMIT NEW SCHOLARSHIP APPLICATION ---");
        try {
            System.out.print("Enter Student ID (e.g. STU1010): ");
            String id = scanner.nextLine().trim();
            if (id.isEmpty()) {
                System.out.println("[ERROR] Student ID cannot be empty.");
                return;
            }

            System.out.print("Enter Student Full Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter CGPA (0.0 - 10.0): ");
            double gpa = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter Annual Family Income (INR): ");
            double income = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter Attendance Percentage (0 - 100): ");
            double attendance = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter Extracurricular Score (0 - 100): ");
            double eca = Double.parseDouble(scanner.nextLine().trim());

            System.out.println("\nSelect Category:\n 1. Merit-Based\n 2. Need-Based\n 3. Sports/ECA");
            System.out.print("Choice (1-3, default 1): ");
            String catChoice = scanner.nextLine().trim();
            String category = "Merit-Based";
            if (catChoice.equals("2")) category = "Need-Based";
            else if (catChoice.equals("3")) category = "Sports/ECA";

            StudentRecord student = new StudentRecord(id, name, gpa, income, attendance, eca, category, "Pending");
            String msg = service.submitApplication(student);
            System.out.println("\n[OK] " + msg);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid numeric format entered.");
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to submit application: " + e.getMessage());
        }
    }

    private static void handleViewQueue(ScholarshipService service) {
        System.out.println("\n--- APPLICATION VERIFICATION QUEUE (FIFO) ---");
        List<StudentRecord> queue = service.getQueueRecords();
        if (queue.isEmpty()) {
            System.out.println(" [Application Queue is currently empty]");
            return;
        }

        System.out.println(" Total Applications Pending: " + queue.size());
        for (int i = 0; i < queue.size(); i++) {
            StudentRecord s = queue.get(i);
            System.out.printf("  #%d [ID: %s] %-18s | Category: %-12s | CGPA: %.2f | Income: Rs. %,.0f | Attend: %.1f%%\n",
                    i + 1, s.getStudentId(), s.getName(), s.getCategory(), s.getGpa(), s.getFamilyIncome(), s.getAttendancePercentage());
        }
    }

    private static void handleProcessApplication(ScholarshipService service) {
        System.out.println("\n--- PROCESS NEXT APPLICATION IN QUEUE ---");
        ScholarshipService.ProcessResult res = service.processNextApplication();
        System.out.println("\n" + res.message);
        if (res.success && res.formulaResult != null) {
            System.out.println("  Merit Score Computed via Stack Parser: " + res.student.getMeritScore());
        }
    }

    private static void handleSearch(ScholarshipService service) {
        System.out.println("\n--- SEARCH STUDENT RECORD ---");
        System.out.print("Enter Student ID to search: ");
        String id = scanner.nextLine().trim();
        StudentRecord found = service.searchStudent(id);
        if (found != null) {
            System.out.println("\n[FOUND] Student Record:");
            System.out.println("  * ID: " + found.getStudentId());
            System.out.println("  * Name: " + found.getName());
            System.out.println("  * Category: " + found.getCategory());
            System.out.println("  * Status: " + found.getStatus());
            System.out.printf("  * CGPA: %.2f\n", found.getGpa());
            System.out.printf("  * Income: Rs. %,.2f\n", found.getFamilyIncome());
            System.out.printf("  * Attendance: %.1f%%\n", found.getAttendancePercentage());
            System.out.printf("  * ECA Score: %.1f\n", found.getExtracurricularScore());
            System.out.printf("  * Merit Score: %.2f\n", found.getMeritScore());
        } else {
            System.out.println("[NOT FOUND] No record matching ID '" + id + "'.");
        }
    }

    private static void handleDelete(ScholarshipService service) {
        System.out.println("\n--- DELETE STUDENT RECORD ---");
        System.out.print("Enter Student ID to delete: ");
        String id = scanner.nextLine().trim();
        boolean ok = service.deleteStudent(id);
        if (ok) {
            System.out.println("[OK] Student with ID '" + id + "' deleted successfully.");
        } else {
            System.out.println("[ERROR] Student ID not found.");
        }
    }

    private static void handleUndo(ScholarshipService service) {
        System.out.println("\n--- UNDO LAST ACTION (STACK POP) ---");
        ScholarshipService.UndoResult res = service.undoLastAction();
        if (res.success) {
            System.out.println("[OK] Undo Successful: " + res.message);
        } else {
            System.out.println("[ERROR] Undo Failed: " + res.message);
        }
    }

    private static void handleViewHistory(ScholarshipService service) {
        System.out.println("\n--- AUDIT & ACTION HISTORY (LIFO STACK) ---");
        List<ActionRecord> history = service.getActionHistory();
        if (history.isEmpty()) {
            System.out.println(" [Action History Stack is empty]");
            return;
        }
        System.out.println(" Total Actions in Stack: " + history.size() + " (Top = Most Recent)\n");
        for (int i = 0; i < history.size(); i++) {
            System.out.printf("  [%d] %s\n", i + 1, history.get(i));
        }
    }

    private static void handleFormulaEvaluator(ScholarshipService service) {
        System.out.println("\n--- STACK-BASED EXPRESSION PARSER & MERIT FORMULA EVALUATOR ---");
        System.out.println(" Current Active Formula: " + service.getActiveFormula());
        System.out.println("\n Options:");
        System.out.println("  1. Test custom expression with step-by-step stack trace");
        System.out.println("  2. Set new active merit formula");
        System.out.println("  3. Recalculate all stored student merit scores");
        System.out.print(" Select option (1-3): ");
        String opt = scanner.nextLine().trim();

        if (opt.equals("1")) {
            System.out.print(" Enter arithmetic formula (press Enter for default): ");
            String formula = scanner.nextLine().trim();
            if (formula.isEmpty()) formula = service.getActiveFormula();

            boolean balanced = ExpressionParser.checkBalancedDelimiters(formula);
            System.out.println("\n [1] Delimiter Check (Stack): " + (balanced ? "[VALID]" : "[INVALID]"));
            if (!balanced) return;

            try {
                List<String> postfix = ExpressionParser.infixToPostfix(formula);
                System.out.println(" [2] Infix -> Postfix Conversion: " + String.join(" ", postfix));

                System.out.print("   Enter CGPA (default 8.5): ");
                String gpaStr = scanner.nextLine().trim();
                double gpa = gpaStr.isEmpty() ? 8.5 : Double.parseDouble(gpaStr);

                System.out.print("   Enter eca (default 85.0): ");
                String ecaStr = scanner.nextLine().trim();
                double eca = ecaStr.isEmpty() ? 85.0 : Double.parseDouble(ecaStr);

                System.out.print("   Enter attendance (default 92.0): ");
                String attStr = scanner.nextLine().trim();
                double att = attStr.isEmpty() ? 92.0 : Double.parseDouble(attStr);

                Map<String, Double> vars = new HashMap<>();
                vars.put("gpa", gpa);
                vars.put("eca", eca);
                vars.put("attendance", att);

                ExpressionParser.ParseResult res = ExpressionParser.evaluatePostfix(postfix, vars);
                System.out.printf("\n [3] Evaluation Result: %.4f\n", res.result);
                System.out.println("\n --- Detailed Stack Evaluation Trace ---");
                for (ExpressionParser.EvaluationTraceStep step : res.trace) {
                    System.out.printf("   Token: %-10s | Action: %-40s | Stack: %s\n", step.token, step.action, step.stackState);
                }
            } catch (Exception e) {
                System.out.println("[ERROR] Expression evaluation failed: " + e.getMessage());
            }
        } else if (opt.equals("2")) {
            System.out.print(" Enter new active formula: ");
            String newFormula = scanner.nextLine().trim();
            boolean ok = service.setActiveFormula(newFormula);
            if (ok) {
                System.out.println("[OK] Active formula updated to: " + newFormula);
            } else {
                System.out.println("[ERROR] Invalid formula syntax or unbalanced delimiters.");
            }
        } else if (opt.equals("3")) {
            int count = service.recalculateAllMeritScores();
            System.out.println("[OK] Recalculated merit scores for " + count + " records.");
        }
    }

    public static void handleQuickDemo(ScholarshipService service) {
        System.out.println("\n================================================================================");
        System.out.println(" STARTING AUTOMATED QUICK DEMO (PHASE 1 WORKFLOW WALKTHROUGH - JAVA)");
        System.out.println("================================================================================");

        // Step 1: Load sample records
        System.out.println("\n[Step 1] Loading sample applications into FIFO Verification Queue...");
        int count = service.loadSampleData(true);
        System.out.println("[OK] Loaded " + count + " applicants into queue.");

        // Step 2: Display Queue
        System.out.println("\n[Step 2] Displaying FIFO Application Queue:");
        handleViewQueue(service);

        // Step 3: Process 3 Applications
        System.out.println("\n[Step 3] Processing first 3 applications from Queue (FIFO):");
        for (int i = 0; i < 3; i++) {
            ScholarshipService.ProcessResult res = service.processNextApplication();
            System.out.printf("  -> Processed #%d: %s\n", i + 1, res.message);
        }

        // Step 4: Display Linked List Storage
        System.out.println("\n[Step 4] Displaying Stored Records in Singly Linked List (Sorted by Merit):");
        System.out.println(service.getStudentStorage().displayTabular());

        // Step 5: Test Search
        System.out.println("\n[Step 5] Performing Linear Search on Linked List for 'STU1001':");
        StudentRecord found = service.searchStudent("STU1001");
        if (found != null) {
            System.out.printf("[OK] Found: %s | Category: %s | Status: %s | Merit Score: %.2f\n",
                    found.getName(), found.getCategory(), found.getStatus(), found.getMeritScore());
        }

        // Step 6: Test Undo Operation via Stack
        System.out.println("\n[Step 6] Testing Undo Action via LIFO Action Stack (Reverting last processed application):");
        ScholarshipService.UndoResult undoRes = service.undoLastAction();
        System.out.println("[OK] " + undoRes.message);

        // Step 7: Display updated Linked List and Queue
        System.out.println("\n[Step 7] Current state after Undo:");
        System.out.println("  * Records in Linked List: " + service.getStudentStorage().size());
        System.out.println("  * Applicants in Queue: " + service.getApplicationQueue().size());

        // Step 8: Expression Parser Demonstration
        System.out.println("\n[Step 8] Stack Expression Parser Demonstration:");
        String demoExpr = "((gpa / 10.0) * 50.0) + (eca * 0.30) + (attendance * 0.20)";
        Map<String, Double> varsDemo = new HashMap<>();
        varsDemo.put("gpa", 9.20);
        varsDemo.put("eca", 88.0);
        varsDemo.put("attendance", 96.5);
        ExpressionParser.ParseResult demoParse = ExpressionParser.evaluateFormula(demoExpr, varsDemo);
        System.out.println("  * Formula: " + demoExpr);
        System.out.println("  * Postfix (RPN): " + String.join(" ", demoParse.postfixTokens));
        System.out.printf("  * Computed Score: %.2f\n", demoParse.result);

        // =====================================================================
        // PHASE 2 DEMONSTRATION: HIERARCHICAL & GRAPH STRUCTURES (CLO2)
        // =====================================================================
        System.out.println("\n--- [PHASE 2] HIERARCHICAL & GRAPH STRUCTURES DEMONSTRATION ---");

        // Step 9: BST Fast Lookup
        System.out.println("\n[Step 9] Binary Search Tree (BST) Fast O(log N) Access:");
        service.syncBSTFromStorage();
        StudentRecord bstStudent = service.searchStudentFastBST("STU1001");
        if (bstStudent != null) {
            System.out.printf("  [BST MATCH] Found ID: %s | Name: %s | Merit: %.2f | Height of BST: %d\n",
                    bstStudent.getStudentId(), bstStudent.getName(), bstStudent.getMeritScore(), service.getStudentBST().height());
        }

        // Step 10: BST In-Order Traversal
        System.out.println("\n[Step 10] BST In-Order Traversal (Sorted by Student ID):");
        List<StudentRecord> sortedBst = service.getBSTInorderStudents();
        for (StudentRecord s : sortedBst) {
            System.out.printf("  -> [%s] %-16s | Merit: %.2f | Status: %s\n",
                    s.getStudentId(), s.getName(), s.getMeritScore(), s.getStatus());
        }

        // Step 11: Category Binary Tree
        System.out.println("\n[Step 11] Scholarship Category Hierarchy (Binary Tree):");
        service.getCategoryTree().displayHierarchy();

        // Step 12: Graph Representations (Adjacency List & Matrix)
        System.out.println("\n[Step 12] Dual Verification Workflow Graph Representations:");
        service.getWorkflowGraph().getAdjacencyList().printGraph();
        System.out.println();
        service.getWorkflowGraph().getAdjacencyMatrix().printMatrix();

        // Step 13: Graph Traversals (BFS & DFS)
        System.out.println("\n[Step 13] Graph Traversal Execution:");
        List<String> bfsOrder = service.runWorkflowBFS("PortalSubmission");
        System.out.println("  * BFS (Breadth-First using LinkedQueue): " + String.join(" -> ", bfsOrder));
        List<String> dfsOrder = service.runWorkflowDFS("PortalSubmission");
        System.out.println("  * DFS (Depth-First using LinkedStack):   " + String.join(" -> ", dfsOrder));

        List<String> path = service.findWorkflowVerificationPath("PortalSubmission", "FundDisburse");
        System.out.println("  * End-to-End Approval Route: " + String.join(" -> ", path));

        System.out.println("\n================================================================================");
        System.out.println(" QUICK DEMO COMPLETE - BOTH PHASE 1 & PHASE 2 VERIFIED (JAVA)!");
        System.out.println("================================================================================");
    }

    // =========================================================================
    // Phase 2 Handlers: Hierarchical & Graph Structures
    // =========================================================================

    private static void handleBSTOperations(ScholarshipService service) {
        System.out.println("\n--- BINARY SEARCH TREE (BST) OPERATIONS (CLO2) ---");
        System.out.println("  [1] Fast Search Student Record by ID (O(log N))");
        System.out.println("  [2] Delete Student Record from BST (3 Cases Demonstration)");
        System.out.println("  [3] Display BST Visual Structure");
        System.out.println("  [4] Resync BST from Linked List Storage");
        System.out.println("  [5] Find Min and Max Student Records by ID");
        System.out.print("Enter choice (1-5): ");
        String opt = scanner.nextLine().trim();

        if (opt.equals("1")) {
            System.out.print("Enter Student ID to search in BST: ");
            String id = scanner.nextLine().trim();
            StudentRecord record = service.searchStudentFastBST(id);
            if (record != null) {
                System.out.printf("[BST FOUND] ID: %s | Name: %s | Merit: %.2f | Status: %s | GPA: %.2f\n",
                        record.getStudentId(), record.getName(), record.getMeritScore(), record.getStatus(), record.getGpa());
            } else {
                System.out.println("[NOT FOUND] No student with ID '" + id + "' found in BST.");
            }
        } else if (opt.equals("2")) {
            System.out.print("Enter Student ID to delete from BST: ");
            String id = scanner.nextLine().trim();
            boolean deleted = service.getStudentBST().delete(id);
            if (deleted) {
                System.out.println("[OK] Successfully deleted '" + id + "' from BST. Remaining BST size: " + service.getStudentBST().size());
            } else {
                System.out.println("[ERROR] Student ID '" + id + "' not found in BST.");
            }
        } else if (opt.equals("3")) {
            System.out.println("\n--- Current Binary Search Tree Visual Representation ---");
            service.getStudentBST().printTree();
            System.out.printf("Tree Metrics -> Total Nodes: %d | Height: %d\n",
                    service.getStudentBST().size(), service.getStudentBST().height());
        } else if (opt.equals("4")) {
            int count = service.syncBSTFromStorage();
            System.out.println("[OK] Resynced " + count + " records from Linked List into BST.");
        } else if (opt.equals("5")) {
            StudentRecord min = service.getStudentBST().findMin();
            StudentRecord max = service.getStudentBST().findMax();
            System.out.println("  * Min Student ID (Leftmost in BST): " + (min != null ? min.getStudentId() + " (" + min.getName() + ")" : "None"));
            System.out.println("  * Max Student ID (Rightmost in BST): " + (max != null ? max.getStudentId() + " (" + max.getName() + ")" : "None"));
        }
    }

    private static void handleTreeTraversals(ScholarshipService service) {
        System.out.println("\n--- TREE TRAVERSALS (INORDER, PREORDER, POSTORDER) (CLO2) ---");
        System.out.println("  [1] BST Traversals (Student Records)");
        System.out.println("  [2] Category Tree Traversals (Scholarship Schemes)");
        System.out.print("Enter choice (1-2): ");
        String opt = scanner.nextLine().trim();

        if (opt.equals("1")) {
            System.out.println("\n1. In-Order Traversal (LNR - Ascending by Student ID):");
            for (StudentRecord s : service.getBSTInorderStudents()) {
                System.out.printf("   [%s] %-16s (Score: %.2f)\n", s.getStudentId(), s.getName(), s.getMeritScore());
            }
            System.out.println("\n2. Pre-Order Traversal (NLR - Root First):");
            for (StudentRecord s : service.getBSTPreorderStudents()) {
                System.out.printf("   [%s] %-16s\n", s.getStudentId(), s.getName());
            }
            System.out.println("\n3. Post-Order Traversal (LRN - Leaves First):");
            for (StudentRecord s : service.getBSTPostorderStudents()) {
                System.out.printf("   [%s] %-16s\n", s.getStudentId(), s.getName());
            }
        } else if (opt.equals("2")) {
            System.out.println("\n1. Category Pre-Order Traversal (Top-Down Governance):");
            for (var c : service.getCategoryTree().getPreorderTravesal()) {
                System.out.println("   " + c);
            }
            System.out.println("\n2. Category In-Order Traversal (LNR):");
            for (var c : service.getCategoryTree().getInorderTraversal()) {
                System.out.println("   " + c);
            }
            System.out.println("\n3. Category Post-Order Traversal (Bottom-Up Rollup):");
            for (var c : service.getCategoryTree().getPostorderTraversal()) {
                System.out.println("   " + c);
            }
        }
    }

    private static void handleCategoryHierarchy(ScholarshipService service) {
        System.out.println("\n--- SCHOLARSHIP CATEGORY BINARY TREE (CLO2) ---");
        service.getCategoryTree().displayHierarchy();

        System.out.print("\nWould you like to check category eligibility for a stored student? (y/n): ");
        String ans = scanner.nextLine().trim();
        if (ans.equalsIgnoreCase("y")) {
            System.out.print("Enter Student ID: ");
            String id = scanner.nextLine().trim();
            StudentRecord student = service.searchStudent(id);
            if (student == null) {
                student = service.searchStudentFastBST(id);
            }

            if (student != null) {
                var eligible = service.findEligibleCategoriesForStudent(student);
                System.out.printf("\nEligibility results for %s (GPA: %.2f, Income: ₹%,.0f, ECA: %.1f):\n",
                        student.getName(), student.getGpa(), student.getFamilyIncome(), student.getExtracurricularScore());
                if (eligible.isEmpty()) {
                    System.out.println("  (No direct category criteria matched)");
                } else {
                    for (var cat : eligible) {
                        System.out.printf("  [QUALIFIED] %s -> Grant: ₹%,.0f | Criteria: %s\n",
                                cat.getName(), cat.getGrantAmount(), cat.getDescription());
                    }
                }
            } else {
                System.out.println("[ERROR] Student not found.");
            }
        }
    }

    private static void handleWorkflowGraph(ScholarshipService service) {
        System.out.println("\n--- VERIFICATION WORKFLOW GRAPH REPRESENTATIONS (CLO2) ---");
        service.getWorkflowGraph().displayWorkflowRepresentations();
    }

    private static void handleGraphTraversals(ScholarshipService service) {
        System.out.println("\n--- GRAPH TRAVERSALS & PATH FINDING (CLO2) ---");
        System.out.println("  [1] Breadth-First Search (BFS) Traversal");
        System.out.println("  [2] Depth-First Search (DFS) Traversal");
        System.out.println("  [3] Find End-to-End Verification Routing Path");
        System.out.print("Enter choice (1-3): ");
        String opt = scanner.nextLine().trim();

        if (opt.equals("1")) {
            System.out.print("Enter starting stage (press Enter for 'PortalSubmission'): ");
            String start = scanner.nextLine().trim();
            if (start.isEmpty()) start = "PortalSubmission";
            List<String> bfs = service.runWorkflowBFS(start);
            System.out.println("\n[BFS Traversal Order (using custom LinkedQueue)]:");
            System.out.println("  " + String.join(" -> ", bfs));
        } else if (opt.equals("2")) {
            System.out.print("Enter starting stage (press Enter for 'PortalSubmission'): ");
            String start = scanner.nextLine().trim();
            if (start.isEmpty()) start = "PortalSubmission";
            List<String> dfs = service.runWorkflowDFS(start);
            System.out.println("\n[DFS Traversal Order (using custom LinkedStack)]:");
            System.out.println("  " + String.join(" -> ", dfs));
        } else if (opt.equals("3")) {
            System.out.print("Enter source stage (default: PortalSubmission): ");
            String src = scanner.nextLine().trim();
            if (src.isEmpty()) src = "PortalSubmission";
            System.out.print("Enter target stage (default: FundDisburse): ");
            String dest = scanner.nextLine().trim();
            if (dest.isEmpty()) dest = "FundDisburse";

            List<String> path = service.findWorkflowVerificationPath(src, dest);
            if (path.isEmpty()) {
                System.out.printf("[UNREACHABLE] No valid verification route exists from '%s' to '%s'.\n", src, dest);
            } else {
                System.out.printf("[ROUTE FOUND] Path (%d stages):\n", path.size());
                System.out.println("  " + String.join(" -> ", path));
            }
        }
    }

    private static void handleLoadSampleData(ScholarshipService service) {
        int count = service.loadSampleData(true);
        System.out.println("[OK] Loaded " + count + " sample student applications into verification queue.");
    }

    public static void handleRunTests() {
        System.out.println("\n--- RUNNING AUTOMATED UNIT TESTS (JAVA) ---");
        TestSuite.runAllTests();
    }
}
