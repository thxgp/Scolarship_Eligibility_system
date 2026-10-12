package com.scholarship.datastructures;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Custom Singly Linked Queue (FIFO - First In First Out).
 * Maintained with front and rear pointers for O(1) enqueue and dequeue.
 */
public class LinkedQueue<T> {
    private QueueNode<T> front;
    private QueueNode<T> rear;
    private int size;

    public LinkedQueue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }

    /**
     * Adds an item to the rear of the queue.
     * Time Complexity: O(1)
     */
    public void enqueue(T item) {
        QueueNode<T> newNode = new QueueNode<>(item);
        if (rear == null) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    /**
     * Removes and returns the item from the front of the queue.
     * Throws NoSuchElementException on queue underflow.
     * Time Complexity: O(1)
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue Underflow: Cannot dequeue from empty queue.");
        }
        T data = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return data;
    }

    /**
     * Returns the front item without removing it.
     * Time Complexity: O(1)
     */
    public T peekFront() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty: Cannot peek front.");
        }
        return front.data;
    }

    /**
     * Returns the rear item without removing it.
     * Time Complexity: O(1)
     */
    public T peekRear() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty: Cannot peek rear.");
        }
        return rear.data;
    }

    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }

    /**
     * Traverses queue from front to rear into a List.
     */
    public List<T> toList() {
        List<T> items = new ArrayList<>();
        QueueNode<T> curr = front;
        while (curr != null) {
            items.add(curr.data);
            curr = curr.next;
        }
        return items;
    }

    public String display() {
        if (isEmpty()) {
            return " [Queue is Empty] ";
        }
        StringBuilder sb = new StringBuilder();
        List<T> list = toList();
        sb.append(String.format("=== FRONT (Queue Size: %d) ===\n", list.size()));
        for (int i = 0; i < list.size(); i++) {
            sb.append(String.format(" Pos [%d]: %s\n", i + 1, list.get(i)));
        }
        sb.append("=== REAR ===");
        return sb.toString();
    }
}
