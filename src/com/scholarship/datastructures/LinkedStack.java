package com.scholarship.datastructures;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

/**
 * Custom Singly Linked Stack (LIFO - Last In First Out).
 * Provides O(1) push, pop, and peek operations via top pointer.
 */
public class LinkedStack<T> {
    private StackNode<T> top;
    private int size;

    public LinkedStack() {
        this.top = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }

    /**
     * Pushes an item onto the top of the stack.
     * Time Complexity: O(1)
     */
    public void push(T item) {
        StackNode<T> newNode = new StackNode<>(item, top);
        this.top = newNode;
        this.size++;
    }

    /**
     * Removes and returns the item from the top of the stack.
     * Throws EmptyStackException on underflow.
     * Time Complexity: O(1)
     */
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }

    /**
     * Returns top item without removing it.
     * Throws EmptyStackException on empty stack.
     * Time Complexity: O(1)
     */
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return top.data;
    }

    public void clear() {
        top = null;
        size = 0;
    }

    /**
     * Traverses stack from top to bottom into a List.
     */
    public List<T> toList() {
        List<T> items = new ArrayList<>();
        StackNode<T> curr = top;
        while (curr != null) {
            items.add(curr.data);
            curr = curr.next;
        }
        return items;
    }

    public String display() {
        if (isEmpty()) {
            return " [Stack is Empty] ";
        }
        StringBuilder sb = new StringBuilder("--- TOP OF STACK ---\n");
        List<T> list = toList();
        for (int i = 0; i < list.size(); i++) {
            sb.append(String.format(" [%d] %s\n", i, list.get(i)));
        }
        sb.append("--- BOTTOM OF STACK ---");
        return sb.toString();
    }
}
