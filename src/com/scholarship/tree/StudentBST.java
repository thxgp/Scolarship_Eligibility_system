package com.scholarship.tree;

import com.scholarship.datastructures.LinkedQueue;
import com.scholarship.datastructures.ListNode;
import com.scholarship.datastructures.StudentLinkedList;
import com.scholarship.model.StudentRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Binary Search Tree (BST) implementation for efficient O(log N) Student Record access.
 * Keyed by unique studentId.
 *
 * Implements CLO2 requirements:
 * 1. Binary Search Tree data structure for fast access
 * 2. Tree Traversals (Inorder, Preorder, Postorder)
 * 3. Node Insertion, Searching, and 3-case Deletion
 * 4. Min / Max queries and Range queries
 */
public class StudentBST {

    private BSTNode root;
    private int size;

    public StudentBST() {
        this.root = null;
        this.size = 0;
    }

    public BSTNode getRoot() { return root; }
    public int size() { return size; }
    public boolean isEmpty() { return root == null; }

    // =========================================================================
    // 1. Insertion (O(log N) average, O(N) worst)
    // =========================================================================

    public boolean insert(StudentRecord student) {
        if (student == null || student.getStudentId() == null) return false;
        int initialSize = size;
        root = insertHelper(root, student);
        return size > initialSize;
    }

    private BSTNode insertHelper(BSTNode node, StudentRecord student) {
        if (node == null) {
            size++;
            return new BSTNode(student);
        }

        int cmp = student.getStudentId().compareToIgnoreCase(node.data.getStudentId());
        if (cmp < 0) {
            node.left = insertHelper(node.left, student);
        } else if (cmp > 0) {
            node.right = insertHelper(node.right, student);
        } else {
            // Duplicate ID: update existing record in place
            node.data = student;
        }
        return node;
    }

    // =========================================================================
    // 2. Search (O(log N) average)
    // =========================================================================

    public StudentRecord search(String studentId) {
        if (studentId == null || root == null) return null;
        return searchHelper(root, studentId.trim());
    }

    private StudentRecord searchHelper(BSTNode node, String studentId) {
        if (node == null) return null;

        int cmp = studentId.compareToIgnoreCase(node.data.getStudentId());
        if (cmp == 0) {
            return node.data;
        } else if (cmp < 0) {
            return searchHelper(node.left, studentId);
        } else {
            return searchHelper(node.right, studentId);
        }
    }

    public boolean contains(String studentId) {
        return search(studentId) != null;
    }

    // =========================================================================
    // 3. Deletion (3 Cases: Leaf, 1 Child, 2 Children with In-order Successor)
    // =========================================================================

    public boolean delete(String studentId) {
        if (studentId == null || root == null) return false;
        int initialSize = size;
        root = deleteHelper(root, studentId.trim());
        return size < initialSize;
    }

    private BSTNode deleteHelper(BSTNode node, String studentId) {
        if (node == null) return null;

        int cmp = studentId.compareToIgnoreCase(node.data.getStudentId());
        if (cmp < 0) {
            node.left = deleteHelper(node.left, studentId);
        } else if (cmp > 0) {
            node.right = deleteHelper(node.right, studentId);
        } else {
            // Node to delete found!
            size--;

            // Case 1: Leaf node (no children)
            if (node.left == null && node.right == null) {
                return null;
            }

            // Case 2: One child (right only)
            if (node.left == null) {
                return node.right;
            }

            // Case 2: One child (left only)
            if (node.right == null) {
                return node.left;
            }

            // Case 3: Two children
            // Find in-order successor (smallest node in right subtree)
            BSTNode successor = findMinHelper(node.right);
            node.data = successor.data;
            // Delete successor from right subtree (size was already decremented, compensate before recursive call)
            size++;
            node.right = deleteHelper(node.right, successor.data.getStudentId());
        }
        return node;
    }

    // =========================================================================
    // 4. Min / Max Queries
    // =========================================================================

    public StudentRecord findMin() {
        if (root == null) return null;
        return findMinHelper(root).data;
    }

    private BSTNode findMinHelper(BSTNode node) {
        BSTNode curr = node;
        while (curr.left != null) {
            curr = curr.left;
        }
        return curr;
    }

    public StudentRecord findMax() {
        if (root == null) return null;
        return findMaxHelper(root).data;
    }

    private BSTNode findMaxHelper(BSTNode node) {
        BSTNode curr = node;
        while (curr.right != null) {
            curr = curr.right;
        }
        return curr;
    }

    // =========================================================================
    // 5. Tree Traversals
    // =========================================================================

    /**
     * In-Order Traversal (LNR): yields student records sorted ascending by Student ID.
     */
    public List<StudentRecord> inorder() {
        List<StudentRecord> result = new ArrayList<>();
        inorderHelper(root, result);
        return result;
    }

    private void inorderHelper(BSTNode node, List<StudentRecord> result) {
        if (node == null) return;
        inorderHelper(node.left, result);
        result.add(node.data);
        inorderHelper(node.right, result);
    }

    /**
     * Pre-Order Traversal (NLR): Root before subtrees.
     */
    public List<StudentRecord> preorder() {
        List<StudentRecord> result = new ArrayList<>();
        preorderHelper(root, result);
        return result;
    }

    private void preorderHelper(BSTNode node, List<StudentRecord> result) {
        if (node == null) return;
        result.add(node.data);
        preorderHelper(node.left, result);
        preorderHelper(node.right, result);
    }

    /**
     * Post-Order Traversal (LRN): Subtrees before root.
     */
    public List<StudentRecord> postorder() {
        List<StudentRecord> result = new ArrayList<>();
        postorderHelper(root, result);
        return result;
    }

    private void postorderHelper(BSTNode node, List<StudentRecord> result) {
        if (node == null) return;
        postorderHelper(node.left, result);
        postorderHelper(node.right, result);
        result.add(node.data);
    }

    /**
     * Level-Order Traversal using custom LinkedQueue.
     */
    public List<StudentRecord> levelOrder() {
        List<StudentRecord> result = new ArrayList<>();
        if (root == null) return result;

        LinkedQueue<BSTNode> queue = new LinkedQueue<>();
        queue.enqueue(root);

        while (!queue.isEmpty()) {
            BSTNode curr = queue.dequeue();
            result.add(curr.data);

            if (curr.left != null) queue.enqueue(curr.left);
            if (curr.right != null) queue.enqueue(curr.right);
        }
        return result;
    }

    // =========================================================================
    // 6. Range Queries & Filtering
    // =========================================================================

    public List<StudentRecord> rangeSearch(String minId, String maxId) {
        List<StudentRecord> result = new ArrayList<>();
        rangeSearchHelper(root, minId, maxId, result);
        return result;
    }

    private void rangeSearchHelper(BSTNode node, String minId, String maxId, List<StudentRecord> result) {
        if (node == null) return;

        int cmpMin = node.data.getStudentId().compareToIgnoreCase(minId);
        int cmpMax = node.data.getStudentId().compareToIgnoreCase(maxId);

        if (cmpMin > 0) {
            rangeSearchHelper(node.left, minId, maxId, result);
        }
        if (cmpMin >= 0 && cmpMax <= 0) {
            result.add(node.data);
        }
        if (cmpMax < 0) {
            rangeSearchHelper(node.right, minId, maxId, result);
        }
    }

    // =========================================================================
    // 7. Synchronization with Linked List
    // =========================================================================

    public int syncFromLinkedList(StudentLinkedList list) {
        if (list == null || list.isEmpty()) return 0;
        this.root = null;
        this.size = 0;

        ListNode curr = list.getHead();
        int count = 0;
        while (curr != null) {
            insert(curr.data);
            count++;
            curr = curr.next;
        }
        return count;
    }

    // =========================================================================
    // 8. Height & Visualization
    // =========================================================================

    public int height() {
        return heightHelper(root);
    }

    private int heightHelper(BSTNode node) {
        if (node == null) return 0;
        return 1 + Math.max(heightHelper(node.left), heightHelper(node.right));
    }

    public void printTree() {
        if (root == null) {
            System.out.println("  (Binary Search Tree is empty)");
            return;
        }
        printTreeHelper(root, "", true);
    }

    private void printTreeHelper(BSTNode node, String prefix, boolean isTail) {
        if (node == null) return;
        System.out.printf("%s%s[%s] %s (Merit: %.1f)\n",
                prefix,
                (isTail ? "└── " : "├── "),
                node.data.getStudentId(),
                node.data.getName(),
                node.data.getMeritScore());

        if (node.left != null || node.right != null) {
            if (node.left != null) {
                printTreeHelper(node.left, prefix + (isTail ? "    " : "│   "), node.right == null);
            }
            if (node.right != null) {
                printTreeHelper(node.right, prefix + (isTail ? "    " : "│   "), true);
            }
        }
    }
}
