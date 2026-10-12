package com.scholarship.test;

import com.scholarship.datastructures.LinkedQueue;
import com.scholarship.datastructures.LinkedStack;
import com.scholarship.datastructures.StudentLinkedList;
import com.scholarship.expression.ExpressionParser;
import com.scholarship.model.StudentRecord;
import com.scholarship.service.ScholarshipService;

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

        System.out.println("\n--------------------------------------------------");
        System.out.printf("Test Execution Summary: %d PASSED, %d FAILED\n", testsPassed, testsFailed);
        if (testsFailed == 0) {
            System.out.println("[SUCCESS] ALL TESTS COMPLETED SUCCESSFULLY! (CLO1 Verified)");
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
}
