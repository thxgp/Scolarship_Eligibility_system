package com.scholarship.datastructures;

import com.scholarship.model.StudentRecord;

/**
 * Node for custom Singly Linked List storing StudentRecord.
 */
public class ListNode {
    public StudentRecord data;
    public ListNode next;

    public ListNode(StudentRecord data) {
        this.data = data;
        this.next = null;
    }

    public ListNode(StudentRecord data, ListNode next) {
        this.data = data;
        this.next = next;
    }
}
