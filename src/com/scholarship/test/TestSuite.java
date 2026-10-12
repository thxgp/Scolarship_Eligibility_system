package com.scholarship.test;

import com.scholarship.datastructures.LinkedQueue;
import com.scholarship.datastructures.LinkedStack;
import com.scholarship.datastructures.StudentLinkedList;
import com.scholarship.expression.ExpressionParser;
import com.scholarship.graph.GraphAdjacencyList;
import com.scholarship.graph.GraphAdjacencyMatrix;
import com.scholarship.graph.VerificationWorkflowGraph;
import com.scholarship.model.StudentRecord;
import com.scholarship.service.ScholarshipService;
import com.scholarship.tree.BinaryTree;
import com.scholarship.tree.BinaryTreeNode;
import com.scholarship.tree.ScholarshipCategoryTree;
import com.scholarship.tree.StudentBST;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Built-in Unit Test Suite for Phase 1 (Java).
 * Validates all operations across StudentLinkedList, LinkedStack, LinkedQueue,
 * ExpressionParser, and ScholarshipService.
 */
public class TestSuite {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        runAllTests();
    }

    public static void runAllTests() {
        testsPassed = 0;
        testsFailed = 0;

        testLinkedListOperations();
        testStackOperations();
        testQueueOperations();
        testExpressionParser();
        testScholarshipServiceWorkflow();
        testBinaryTreeAndCategoryHierarchy();
        testStudentBSTOperations();
        testGraphAdjacencyRepresentations();
        testGraphTraversalsAndWorkflows();

        System.out.println("\n--------------------------------------------------");
        System.out.printf("Test Execution Summary: %d PASSED, %d FAILED\n", testsPassed, testsFailed);
        if (testsFailed == 0) {
            System.out.println("[SUCCESS] ALL TESTS COMPLETED SUCCESSFULLY! (CLO1 & CLO2 Verified)");
        } else {
            System.out.println("[FAILURE] Some unit tests failed.");
        }
        System.out.println("--------------------------------------------------\n");
    }

    private static void assertEquals(Object expected, Object actual, String testName) {
        if ((expected == null && actual == null) || (expected != null && expected.equals(actual))) {
            System.out.println("  [PASS] " + testName);
            testsPassed++;
        } else {
            System.err.printf("  [FAIL] %s - Expected: %s, Got: %s\n", testName, expected, actual);
            testsFailed++;
        }
    }

    private static void assertTrue(boolean condition, String testName) {
        if (condition) {
            System.out.println("  [PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + testName + " - Condition was false");
            testsFailed++;
        }
    }

    private static void testLinkedListOperations() {
        System.out.println("\n1. Testing StudentLinkedList Operations...");
        StudentLinkedList list = new StudentLinkedList();

        assertTrue(list.isEmpty(), "Linked list initially empty");
        assertEquals(0, list.size(), "Initial list size is 0");

        StudentRecord s1 = new StudentRecord("STU01", "Alice", 9.2, 300000, 95, 85, "Merit-Based", "Pending");
        StudentRecord s2 = new StudentRecord("STU02", "Bob", 7.2, 200000, 85, 70, "Need-Based", "Pending");
        StudentRecord s3 = new StudentRecord("STU03", "Charlie", 8.4, 400000, 90, 80, "Sports/ECA", "Pending");

        s1.setMeritScore(90.0);
        s2.setMeritScore(70.0);
        s3.setMeritScore(80.0);

        // Test Sorted Insert
        list.insertSortedByMerit(s2); // 70.0
        list.insertSortedByMerit(s1); // 90.0
        list.insertSortedByMerit(s3); // 80.0

        assertEquals(3, list.size(), "List size after 3 sorted inserts");
        List<StudentRecord> records = list.toList();
        assertEquals("STU01", records.get(0).getStudentId(), "Top element has highest merit score (90.0)");
        assertEquals("STU03", records.get(1).getStudentId(), "Middle element has merit score (80.0)");
        assertEquals("STU02", records.get(2).getStudentId(), "Tail element has lowest merit score (70.0)");

        // Test Search
        StudentRecord found = list.searchById("STU03");
        assertTrue(found != null && found.getName().equals("Charlie"), "Linear search by ID finds Charlie");

        // Test Delete middle
        StudentRecord deleted = list.deleteById("STU03");
        assertTrue(deleted != null && deleted.getStudentId().equals("STU03"), "Delete middle node");
        assertEquals(2, list.size(), "List size after delete is 2");

        // Test Delete head
        deleted = list.deleteById("STU01");
        assertTrue(deleted != null && deleted.getStudentId().equals("STU01"), "Delete head node");
        assertEquals(1, list.size(), "List size after head delete is 1");
    }

    private static void testStackOperations() {
        System.out.println("\n2. Testing LinkedStack (LIFO) Operations...");
        LinkedStack<String> stack = new LinkedStack<>();

        assertTrue(stack.isEmpty(), "Stack initially empty");
        stack.push("Item 1");
        stack.push("Item 2");
        stack.push("Item 3");

        assertEquals(3, stack.size(), "Stack size after 3 pushes");
        assertEquals("Item 3", stack.peek(), "Peek returns top item without removing");
        assertEquals("Item 3", stack.pop(), "Pop returns top item (LIFO)");
        assertEquals("Item 2", stack.pop(), "Second pop returns next item");
        assertEquals("Item 1", stack.pop(), "Third pop returns first item");
        assertTrue(stack.isEmpty(), "Stack is empty after all pops");

        // Test underflow exception
        boolean threw = false;
        try {
            stack.pop();
        } catch (EmptyStackException e) {
            threw = true;
        }
        assertTrue(threw, "Pop on empty stack throws EmptyStackException");
    }

    private static void testQueueOperations() {
        System.out.println("\n3. Testing LinkedQueue (FIFO) Operations...");
        LinkedQueue<String> queue = new LinkedQueue<>();

        assertTrue(queue.isEmpty(), "Queue initially empty");
        queue.enqueue("First");
        queue.enqueue("Second");
        queue.enqueue("Third");

        assertEquals(3, queue.size(), "Queue size after 3 enqueues");
        assertEquals("First", queue.peekFront(), "PeekFront returns oldest item");
        assertEquals("Third", queue.peekRear(), "PeekRear returns newest item");
        assertEquals("First", queue.dequeue(), "Dequeue returns oldest item (FIFO)");
        assertEquals("Second", queue.dequeue(), "Second dequeue returns second item");
        assertEquals("Third", queue.dequeue(), "Third dequeue returns third item");
        assertTrue(queue.isEmpty(), "Queue is empty after all dequeues");

        // Test underflow exception
        boolean threw = false;
        try {
            queue.dequeue();
        } catch (NoSuchElementException e) {
            threw = true;
        }
        assertTrue(threw, "Dequeue on empty queue throws NoSuchElementException");
    }

    private static void testExpressionParser() {
        System.out.println("\n4. Testing Stack-based Expression Parser...");

        // Delimiter checking
        assertTrue(ExpressionParser.checkBalancedDelimiters("((a + b) * [c - d])"), "Valid nested parentheses and brackets");
        assertTrue(!ExpressionParser.checkBalancedDelimiters("(a + b * c"), "Detect unclosed parenthesis");
        assertTrue(!ExpressionParser.checkBalancedDelimiters("(a + [b)]"), "Detect mismatched brackets");

        // Infix to Postfix conversion
        List<String> postfix = ExpressionParser.infixToPostfix("((gpa / 10.0) * 50.0) + (eca * 0.30)");
        String rpn = String.join(" ", postfix);
        assertEquals("gpa 10.0 / 50.0 * eca 0.30 * +", rpn, "Infix to Postfix (RPN) conversion matches");

        // Postfix evaluation with variables
        Map<String, Double> vars = new HashMap<>();
        vars.put("gpa", 10.0);
        vars.put("eca", 80.0);
        vars.put("attendance", 90.0);

        ExpressionParser.ParseResult res = ExpressionParser.evaluateFormula("((gpa / 10.0) * 50.0) + (eca * 0.30) + (attendance * 0.20)", vars);
        // (10/10 * 50) + (80 * 0.3) + (90 * 0.2) = 50 + 24 + 18 = 92.0
        assertEquals(92.0, res.result, "Evaluate formula matches expected 92.0");
    }

    private static void testScholarshipServiceWorkflow() {
        System.out.println("\n5. Testing ScholarshipService & Undo History...");
        ScholarshipService service = new ScholarshipService();

        StudentRecord s1 = new StudentRecord("STU1001", "Aarav Sharma", 9.20, 350000.0, 96.5, 88.0, "Merit-Based", "Pending");
        StudentRecord s2 = new StudentRecord("STU1002", "Priya Patel", 8.50, 220000.0, 92.0, 70.0, "Need-Based", "Pending");

        service.submitApplication(s1);
        service.submitApplication(s2);
        assertEquals(2, service.getApplicationQueue().size(), "2 applications queued in FIFO queue");

        ScholarshipService.ProcessResult pRes = service.processNextApplication();
        assertTrue(pRes.success, "First application processed");
        assertEquals("STU1001", pRes.student.getStudentId(), "Dequeued STU1001 first (FIFO)");
        assertEquals("Approved", pRes.student.getStatus(), "STU1001 approved for merit scholarship");
        assertEquals(1, service.getStudentStorage().size(), "1 record stored in Linked List");
        assertEquals(1, service.getApplicationQueue().size(), "1 application remaining in Queue");

        // Test Undo operation via Action Stack
        ScholarshipService.UndoResult uRes = service.undoLastAction();
        assertTrue(uRes.success, "Undo succeeded");
        assertEquals(0, service.getStudentStorage().size(), "Record removed from Linked List after undo");
        assertEquals(2, service.getApplicationQueue().size(), "Record restored to front of Queue after undo");
    }

    // =========================================================================
    // Phase 2 Tests: Hierarchical & Graph Structures (CLO2)
    // =========================================================================

    private static void testBinaryTreeAndCategoryHierarchy() {
        System.out.println("\n6. Testing Binary Tree & Category Hierarchy...");
        BinaryTreeNode<String> n4 = new BinaryTreeNode<>("D");
        BinaryTreeNode<String> n5 = new BinaryTreeNode<>("E");
        BinaryTreeNode<String> n2 = new BinaryTreeNode<>("B", n4, n5);
        BinaryTreeNode<String> n3 = new BinaryTreeNode<>("C");
        BinaryTreeNode<String> root = new BinaryTreeNode<>("A", n2, n3);

        BinaryTree<String> tree = new BinaryTree<>(root);
        assertEquals(5, tree.size(), "Binary tree size is 5");
        assertEquals(3, tree.height(), "Binary tree height is 3");

        // Traversals
        List<String> in = tree.inorder();
        assertEquals(List.of("D", "B", "E", "A", "C"), in, "In-Order traversal (LNR) matches D B E A C");

        List<String> pre = tree.preorder();
        assertEquals(List.of("A", "B", "D", "E", "C"), pre, "Pre-Order traversal (NLR) matches A B D E C");

        List<String> post = tree.postorder();
        assertEquals(List.of("D", "E", "B", "C", "A"), post, "Post-Order traversal (LRN) matches D E B C A");

        List<String> level = tree.levelOrder();
        assertEquals(List.of("A", "B", "C", "D", "E"), level, "Level-Order traversal (Queue) matches A B C D E");

        // Category tree tests
        ScholarshipCategoryTree catTree = new ScholarshipCategoryTree();
        assertEquals(7, catTree.getTree().size(), "Scholarship category tree contains 7 nodes");
        assertEquals(3, catTree.getTree().height(), "Scholarship category tree height is 3");

        var stemCat = catTree.findByCode("STEM-01");
        assertTrue(stemCat != null, "Found category by code STEM-01");
        assertEquals(75000.0, stemCat.getGrantAmount(), "STEM grant amount is 75000");

        // Eligibility check
        StudentRecord highMeritStudent = new StudentRecord("TEST01", "Top Student", 3.9, 500000.0, 95.0, 90.0, "Merit-Based", "Pending");
        var eligibleCats = catTree.findEligibleCategories(highMeritStudent);
        assertTrue(eligibleCats.stream().anyMatch(c -> c.getCode().equals("STEM-01")), "Top student qualifies for STEM-01");
    }

    private static void testStudentBSTOperations() {
        System.out.println("\n7. Testing Student Binary Search Tree (BST) Operations...");
        StudentBST bst = new StudentBST();
        assertTrue(bst.isEmpty(), "BST initially empty");
        assertEquals(0, bst.size(), "Initial BST size is 0");

        StudentRecord s1 = new StudentRecord("STU1005", "Eve", 8.0, 300000.0, 90.0, 75.0, "General", "Approved");
        StudentRecord s2 = new StudentRecord("STU1002", "Bob", 8.5, 250000.0, 92.0, 80.0, "General", "Approved");
        StudentRecord s3 = new StudentRecord("STU1008", "Hank", 9.0, 200000.0, 95.0, 85.0, "General", "Approved");
        StudentRecord s4 = new StudentRecord("STU1001", "Alice", 9.5, 150000.0, 98.0, 90.0, "General", "Approved");
        StudentRecord s5 = new StudentRecord("STU1003", "Charlie", 7.8, 400000.0, 85.0, 70.0, "General", "Approved");

        bst.insert(s1);
        bst.insert(s2);
        bst.insert(s3);
        bst.insert(s4);
        bst.insert(s5);

        assertEquals(5, bst.size(), "BST size after 5 insertions is 5");

        // Search O(log N)
        StudentRecord foundAlice = bst.search("STU1001");
        assertTrue(foundAlice != null, "BST search finds STU1001");
        assertEquals("Alice", foundAlice.getName(), "Found student name is Alice");

        StudentRecord foundNone = bst.search("STU9999");
        assertTrue(foundNone == null, "BST search returns null for non-existent STU9999");

        // In-Order Traversal yields sorted order by ID
        List<StudentRecord> inorder = bst.inorder();
        assertEquals(5, inorder.size(), "Inorder list size is 5");
        assertEquals("STU1001", inorder.get(0).getStudentId(), "First element in inorder is STU1001");
        assertEquals("STU1002", inorder.get(1).getStudentId(), "Second element in inorder is STU1002");
        assertEquals("STU1003", inorder.get(2).getStudentId(), "Third element in inorder is STU1003");
        assertEquals("STU1005", inorder.get(3).getStudentId(), "Fourth element in inorder is STU1005");
        assertEquals("STU1008", inorder.get(4).getStudentId(), "Fifth element in inorder is STU1008");

        // Min and Max
        assertEquals("STU1001", bst.findMin().getStudentId(), "findMin returns leftmost STU1001");
        assertEquals("STU1008", bst.findMax().getStudentId(), "findMax returns rightmost STU1008");

        // Deletion Case 1: Leaf node (STU1001)
        boolean delLeaf = bst.delete("STU1001");
        assertTrue(delLeaf, "Delete leaf node STU1001 succeeded");
        assertEquals(4, bst.size(), "BST size after leaf deletion is 4");
        assertTrue(!bst.contains("STU1001"), "STU1001 is no longer in BST");

        // Deletion Case 2: Node with 1 child (STU1002 now has only right child STU1003)
        boolean delOneChild = bst.delete("STU1002");
        assertTrue(delOneChild, "Delete node with 1 child STU1002 succeeded");
        assertEquals(3, bst.size(), "BST size after 1-child deletion is 3");
        assertTrue(!bst.contains("STU1002"), "STU1002 is no longer in BST");
        assertTrue(bst.contains("STU1003"), "Child STU1003 remains in BST");

        // Deletion Case 3: Node with 2 children (STU1005 is root with STU1003 on left and STU1008 on right)
        boolean delRoot = bst.delete("STU1005");
        assertTrue(delRoot, "Delete node with 2 children (root STU1005) succeeded");
        assertEquals(2, bst.size(), "BST size after 2-children deletion is 2");
        assertTrue(!bst.contains("STU1005"), "STU1005 is no longer in BST");
        assertEquals("STU1003", bst.findMin().getStudentId(), "New min is STU1003");
        assertEquals("STU1008", bst.findMax().getStudentId(), "New max is STU1008");
    }

    private static void testGraphAdjacencyRepresentations() {
        System.out.println("\n8. Testing Graph Adjacency List & Matrix Representations...");
        GraphAdjacencyList adjList = new GraphAdjacencyList();
        adjList.addEdge("A", "B", 5, true);
        adjList.addEdge("A", "C", 3, true);
        adjList.addEdge("B", "D", 2, true);

        assertEquals(4, adjList.getVertexCount(), "Adjacency List has 4 vertices");
        assertTrue(adjList.hasEdge("A", "B"), "Adjacency List has edge A -> B");
        assertTrue(adjList.hasEdge("A", "C"), "Adjacency List has edge A -> C");
        assertTrue(!adjList.hasEdge("B", "A"), "Directed edge does not have reverse B -> A");
        assertEquals(List.of("C", "B"), adjList.getNeighbors("A"), "Neighbors of A retrieved correctly");

        GraphAdjacencyMatrix adjMatrix = new GraphAdjacencyMatrix();
        adjMatrix.addEdge("A", "B", 5, true);
        adjMatrix.addEdge("A", "C", 3, true);
        adjMatrix.addEdge("B", "D", 2, true);

        assertEquals(4, adjMatrix.getVertexCount(), "Adjacency Matrix has 4 vertices");
        assertTrue(adjMatrix.hasEdge("A", "B"), "Adjacency Matrix has edge A -> B");
        assertEquals(5, adjMatrix.getWeight("A", "B"), "Weight A -> B is 5");
        assertEquals(0, adjMatrix.getWeight("B", "A"), "Directed edge weight B -> A is 0");
    }

    private static void testGraphTraversalsAndWorkflows() {
        System.out.println("\n9. Testing Verification Workflow Graph (BFS & DFS Traversals)...");
        VerificationWorkflowGraph wf = new VerificationWorkflowGraph();

        // BFS Traversal (using LinkedQueue)
        List<String> bfsOrder = wf.bfs(VerificationWorkflowGraph.STAGE_PORTAL);
        assertTrue(!bfsOrder.isEmpty(), "BFS traversal produces non-empty list");
        assertEquals("PortalSubmission", bfsOrder.get(0), "BFS starts at PortalSubmission");
        assertEquals("DocVerification", bfsOrder.get(1), "BFS second stage is DocVerification");
        assertTrue(bfsOrder.contains("FundDisburse"), "BFS reaches FundDisburse milestone");

        // DFS Traversal (using LinkedStack)
        List<String> dfsOrder = wf.dfs(VerificationWorkflowGraph.STAGE_PORTAL);
        assertTrue(!dfsOrder.isEmpty(), "DFS traversal produces non-empty list");
        assertEquals("PortalSubmission", dfsOrder.get(0), "DFS starts at PortalSubmission");
        assertTrue(dfsOrder.contains("FundDisburse"), "DFS reaches FundDisburse milestone");

        // Reachability & Route finding
        assertTrue(wf.isReachable("PortalSubmission", "FundDisburse"), "FundDisburse is reachable from PortalSubmission");
        List<String> path = wf.findVerificationPath("PortalSubmission", "FundDisburse");
        assertTrue(!path.isEmpty(), "Valid verification path found from Portal to Disbursement");
        assertEquals("PortalSubmission", path.get(0), "Path starts at PortalSubmission");
        assertEquals("FundDisburse", path.get(path.size() - 1), "Path terminates at FundDisburse");
        assertTrue(path.contains("DocVerification"), "Path passes through DocVerification");
    }
}
