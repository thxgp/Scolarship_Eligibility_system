package com.scholarship.datastructures;

/**
 * Node for generic Singly Linked Queue.
 */
public class QueueNode<T> {
    public T data;
    public QueueNode<T> next;

    public QueueNode(T data) {
        this.data = data;
        this.next = null;
    }
}
