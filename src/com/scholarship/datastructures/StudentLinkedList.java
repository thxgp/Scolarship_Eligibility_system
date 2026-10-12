package com.scholarship.datastructures;

import com.scholarship.model.StudentRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Singly Linked List specifically implemented for storing Student Records.
 * Pure node-pointer implementation without high-level Java collection wrappers.
 */
public class StudentLinkedList {
    private ListNode head;
    private int size;

    public StudentLinkedList() {
        this.head = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return size;
    }

    public ListNode getHead() {
        return head;
    }

    /**
     * Inserts at the beginning (Head) of the list.
     * Time Complexity: O(1)
     */
    public void insertAtHead(StudentRecord record) {
        ListNode newNode = new ListNode(record, head);
        this.head = newNode;
        this.size++;
    }

    /**
     * Inserts at the end (Tail) of the list.
     * Time Complexity: O(N)
     */
    public void insertAtTail(StudentRecord record) {
        ListNode newNode = new ListNode(record);
        if (head == null) {
            head = newNode;
        } else {
            ListNode curr = head;
            while (curr.next != null) {
                curr = curr.next;
            }
            curr.next = newNode;
        }
        size++;
    }

    /**
     * Inserts maintaining descending order of merit score.
     * Time Complexity: O(N)
     */
    public void insertSortedByMerit(StudentRecord record) {
        ListNode newNode = new ListNode(record);

        // Case 1: Empty list or higher merit score than current head
        if (head == null || record.getMeritScore() > head.data.getMeritScore()) {
            newNode.next = head;
            head = newNode;
            size++;
            return;
        }

        // Case 2: Traverse to find correct insertion point
        ListNode curr = head;
        while (curr.next != null && curr.next.data.getMeritScore() >= record.getMeritScore()) {
            curr = curr.next;
        }

        newNode.next = curr.next;
        curr.next = newNode;
        size++;
    }

    /**
     * Deletes a student record by student ID.
     * Returns deleted StudentRecord, or null if not found.
     * Time Complexity: O(N)
     */
    public StudentRecord deleteById(String studentId) {
        if (head == null || studentId == null) {
            return null;
        }

        String target = studentId.trim().toUpperCase();

        // Case 1: Head matches
        if (head.data.getStudentId().trim().toUpperCase().equals(target)) {
            StudentRecord deleted = head.data;
            head = head.next;
            size--;
            return deleted;
        }

        // Case 2: Match in subsequent nodes
        ListNode curr = head;
        while (curr.next != null) {
            if (curr.next.data.getStudentId().trim().toUpperCase().equals(target)) {
                StudentRecord deleted = curr.next.data;
                curr.next = curr.next.next;
                size--;
                return deleted;
            }
            curr = curr.next;
        }

        return null;
    }

    /**
     * Deletes and returns the head student record.
     * Time Complexity: O(1)
     */
    public StudentRecord deleteAtHead() {
        if (head == null) {
            return null;
        }
        StudentRecord deleted = head.data;
        head = head.next;
        size--;
        return deleted;
    }

    /**
     * Searches student by unique ID.
     * Time Complexity: O(N)
     */
    public StudentRecord searchById(String studentId) {
        if (head == null || studentId == null) {
            return null;
        }
        String target = studentId.trim().toUpperCase();
        ListNode curr = head;
        while (curr != null) {
            if (curr.data.getStudentId().trim().toUpperCase().equals(target)) {
                return curr.data;
            }
            curr = curr.next;
        }
        return null;
    }

    /**
     * Searches students by name (case-insensitive substring match).
     */
    public List<StudentRecord> searchByName(String nameQuery) {
        List<StudentRecord> matches = new ArrayList<>();
        if (head == null || nameQuery == null) {
            return matches;
        }
        String target = nameQuery.trim().toLowerCase();
        ListNode curr = head;
        while (curr != null) {
            if (curr.data.getName().toLowerCase().contains(target)) {
                matches.add(curr.data);
            }
            curr = curr.next;
        }
        return matches;
    }

    /**
     * Filters student records matching custom predicate.
     */
    public List<StudentRecord> filter(Predicate<StudentRecord> predicate) {
        List<StudentRecord> matched = new ArrayList<>();
        ListNode curr = head;
        while (curr != null) {
            if (predicate.test(curr.data)) {
                matched.add(curr.data);
            }
            curr = curr.next;
        }
        return matched;
    }

    /**
     * Updates an existing student record's status.
     */
    public boolean updateStatus(String studentId, String newStatus) {
        StudentRecord record = searchById(studentId);
        if (record != null) {
            record.setStatus(newStatus);
            return true;
        }
        return false;
    }

    /**
     * Traverses the list and returns records as a standard List.
     */
    public List<StudentRecord> toList() {
        List<StudentRecord> list = new ArrayList<>();
        ListNode curr = head;
        while (curr != null) {
            list.add(curr.data);
            curr = curr.next;
        }
        return list;
    }

    /**
     * Traverses the list and returns records as an array.
     */
    public StudentRecord[] toArray() {
        StudentRecord[] arr = new StudentRecord[size];
        ListNode curr = head;
        int i = 0;
        while (curr != null && i < size) {
            arr[i++] = curr.data;
            curr = curr.next;
        }
        return arr;
    }

    public void clear() {
        head = null;
        size = 0;
    }

    /**
     * Displays records in a formatted ASCII table.
     */
    public String displayTabular() {
        List<StudentRecord> records = toList();
        if (records.isEmpty()) {
            return " [No records found in Linked List] ";
        }

        String[] headers = {"ID", "Name", "CGPA", "Income (INR)", "Attend %", "ECA Score", "Category", "Status", "Merit Score"};
        int[] widths = {10, 20, 6, 14, 10, 10, 14, 14, 12};

        StringBuilder sb = new StringBuilder();
        String sep = buildSeparator(widths);
        sb.append(sep).append("\n");
        sb.append(buildRow(headers, widths)).append("\n");
        sb.append(sep).append("\n");

        for (StudentRecord r : records) {
            String[] vals = {
                    r.getStudentId(),
                    r.getName().length() > 18 ? r.getName().substring(0, 18) : r.getName(),
                    String.format("%.2f", r.getGpa()),
                    String.format("Rs. %,.0f", r.getFamilyIncome()),
                    String.format("%.1f%%", r.getAttendancePercentage()),
                    String.format("%.1f", r.getExtracurricularScore()),
                    r.getCategory(),
                    r.getStatus(),
                    String.format("%.2f", r.getMeritScore())
            };
            sb.append(buildRow(vals, widths)).append("\n");
        }

        sb.append(sep).append("\n");
        sb.append(" Total Records in Linked List: ").append(records.size());
        return sb.toString();
    }

    private String buildSeparator(int[] widths) {
        StringBuilder sb = new StringBuilder("+-");
        for (int i = 0; i < widths.length; i++) {
            for (int j = 0; j < widths[i]; j++) sb.append("-");
            if (i < widths.length - 1) sb.append("-+-");
        }
        sb.append("-+");
        return sb.toString();
    }

    private String buildRow(String[] items, int[] widths) {
        StringBuilder sb = new StringBuilder("| ");
        for (int i = 0; i < widths.length; i++) {
            sb.append(String.format("%-" + widths[i] + "s", items[i]));
            if (i < widths.length - 1) sb.append(" | ");
        }
        sb.append(" |");
        return sb.toString();
    }
}
