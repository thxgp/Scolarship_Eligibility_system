package com.scholarship.datastructures;

/**
 * Node for generic Singly Linked Stack.
 */
public class StackNode<T> {
    public T data;
    public StackNode<T> next;

    public StackNode(T data) {
        this.data = data;
        this.next = null;
    }

    public StackNode(T data, StackNode<T> next) {
        this.data = data;
        this.next = next;
    }
}
