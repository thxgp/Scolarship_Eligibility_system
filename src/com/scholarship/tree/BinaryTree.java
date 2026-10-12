package com.scholarship.tree;

import com.scholarship.datastructures.LinkedQueue;

import java.util.ArrayList;
import java.util.List;

/**
 * General Binary Tree implementation for structured hierarchical storage.
 * Demonstrates:
 * 1. Tree construction and node linking
 * 2. Tree Traversals: In-Order (LNR), Pre-Order (NLR), Post-Order (LRN)
 * 3. Level-Order Traversal using custom LinkedQueue
 * 4. Height, Size, and formatted hierarchical visualization
 *
 * @param <T> Type of element stored in tree nodes
 */
public class BinaryTree<T> {

    protected BinaryTreeNode<T> root;

    public BinaryTree() {
        this.root = null;
    }

    public BinaryTree(BinaryTreeNode<T> root) {
        this.root = root;
    }

    public BinaryTreeNode<T> getRoot() {
        return root;
    }

    public void setRoot(BinaryTreeNode<T> root) {
        this.root = root;
    }

    public boolean isEmpty() {
        return root == null;
    }

    // =========================================================================
    // 1. Tree Traversals
    // =========================================================================

    /**
     * In-Order Traversal (Left, Root, Right).
     */
    public List<T> inorder() {
        List<T> result = new ArrayList<>();
        inorderHelper(root, result);
        return result;
    }

    private void inorderHelper(BinaryTreeNode<T> node, List<T> result) {
        if (node == null) return;
        inorderHelper(node.left, result);
        result.add(node.data);
        inorderHelper(node.right, result);
    }

    /**
     * Pre-Order Traversal (Root, Left, Right).
     */
    public List<T> preorder() {
        List<T> result = new ArrayList<>();
        preorderHelper(root, result);
        return result;
    }

    private void preorderHelper(BinaryTreeNode<T> node, List<T> result) {
        if (node == null) return;
        result.add(node.data);
        preorderHelper(node.left, result);
        preorderHelper(node.right, result);
    }

    /**
     * Post-Order Traversal (Left, Right, Root).
     */
    public List<T> postorder() {
        List<T> result = new ArrayList<>();
        postorderHelper(root, result);
        return result;
    }

    private void postorderHelper(BinaryTreeNode<T> node, List<T> result) {
        if (node == null) return;
        postorderHelper(node.left, result);
        postorderHelper(node.right, result);
        result.add(node.data);
    }

    /**
     * Level-Order Traversal (Breadth-First) using custom LinkedQueue.
     */
    public List<T> levelOrder() {
        List<T> result = new ArrayList<>();
        if (root == null) return result;

        LinkedQueue<BinaryTreeNode<T>> queue = new LinkedQueue<>();
        queue.enqueue(root);

        while (!queue.isEmpty()) {
            BinaryTreeNode<T> current = queue.dequeue();
            result.add(current.data);

            if (current.left != null) {
                queue.enqueue(current.left);
            }
            if (current.right != null) {
                queue.enqueue(current.right);
            }
        }
        return result;
    }

    // =========================================================================
    // 2. Metrics & Structural Queries
    // =========================================================================

    public int size() {
        return sizeHelper(root);
    }

    private int sizeHelper(BinaryTreeNode<T> node) {
        if (node == null) return 0;
        return 1 + sizeHelper(node.left) + sizeHelper(node.right);
    }

    public int height() {
        return heightHelper(root);
    }

    private int heightHelper(BinaryTreeNode<T> node) {
        if (node == null) return 0;
        return 1 + Math.max(heightHelper(node.left), heightHelper(node.right));
    }

    // =========================================================================
    // 3. Tree Visualization
    // =========================================================================

    public void printTree() {
        if (root == null) {
            System.out.println("  (Tree is empty)");
            return;
        }
        printTreeHelper(root, "", true);
    }

    private void printTreeHelper(BinaryTreeNode<T> node, String prefix, boolean isTail) {
        if (node == null) return;
        System.out.println(prefix + (isTail ? "└── " : "├── ") + node.data);
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
