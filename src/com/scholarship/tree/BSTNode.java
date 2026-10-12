package com.scholarship.tree;

import com.scholarship.model.StudentRecord;

/**
 * Node for Binary Search Tree storing StudentRecord.
 * Maintained with left and right child pointers for O(log N) data access.
 */
public class BSTNode {
    public StudentRecord data;
    public BSTNode left;
    public BSTNode right;

    public BSTNode(StudentRecord data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }

    public BSTNode(StudentRecord data, BSTNode left, BSTNode right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return data != null ? String.format("%s (Score: %.2f)", data.getStudentId(), data.getMeritScore()) : "null";
    }
}
