package com.scholarship.graph;

import java.util.*;

/**
 * Custom Graph representation using an Adjacency List.
 * Fulfills CLO2 requirements:
 * 1. Graph vertex and edge modeling
 * 2. Pure linked edge list nodes for each vertex
 * 3. Neighborhood inspection and graph visualization
 */
public class GraphAdjacencyList {

    /**
     * Node representing a directed/weighted edge in the adjacency list.
     */
    public static class EdgeNode {
        public String destination;
        public int weight; // e.g. SLA processing turnaround in days
        public EdgeNode next;

        public EdgeNode(String destination, int weight) {
            this.destination = destination;
            this.weight = weight;
            this.next = null;
        }

        public EdgeNode(String destination, int weight, EdgeNode next) {
            this.destination = destination;
            this.weight = weight;
            this.next = next;
        }
    }

    private final Map<String, EdgeNode> adjList;
    private final List<String> vertices;

    public GraphAdjacencyList() {
        this.adjList = new LinkedHashMap<>();
        this.vertices = new ArrayList<>();
    }

    public List<String> getVertices() {
        return Collections.unmodifiableList(vertices);
    }

    public int getVertexCount() {
        return vertices.size();
    }

    public boolean addVertex(String vertex) {
        if (vertex == null || vertex.trim().isEmpty()) return false;
        String v = vertex.trim();
        if (!adjList.containsKey(v)) {
            adjList.put(v, null);
            vertices.add(v);
            return true;
        }
        return false;
    }

    public boolean addEdge(String source, String destination, int weight, boolean directed) {
        if (source == null || destination == null) return false;
        addVertex(source);
        addVertex(destination);

        // Add edge source -> destination
        EdgeNode current = adjList.get(source);
        adjList.put(source, new EdgeNode(destination, weight, current));

        if (!directed) {
            // Add reverse edge destination -> source
            EdgeNode revCurrent = adjList.get(destination);
            adjList.put(destination, new EdgeNode(source, weight, revCurrent));
        }
        return true;
    }

    public List<String> getNeighbors(String vertex) {
        List<String> neighbors = new ArrayList<>();
        EdgeNode curr = adjList.get(vertex);
        while (curr != null) {
            neighbors.add(curr.destination);
            curr = curr.next;
        }
        return neighbors;
    }

    public EdgeNode getEdgeHead(String vertex) {
        return adjList.get(vertex);
    }

    public boolean hasEdge(String source, String destination) {
        EdgeNode curr = adjList.get(source);
        while (curr != null) {
            if (curr.destination.equalsIgnoreCase(destination)) {
                return true;
            }
            curr = curr.next;
        }
        return false;
    }

    public void printGraph() {
        System.out.println("  ==========================================================================");
        System.out.println("              WORKFLOW GRAPH REPRESENTATION: ADJACENCY LIST");
        System.out.println("  ==========================================================================");
        for (String v : vertices) {
            System.out.printf("  [%-16s] -> ", v);
            EdgeNode curr = adjList.get(v);
            if (curr == null) {
                System.out.println("(No outbound stages)");
            } else {
                StringBuilder sb = new StringBuilder();
                while (curr != null) {
                    sb.append(String.format("[%s (SLA: %dd)]", curr.destination, curr.weight));
                    if (curr.next != null) sb.append(" -> ");
                    curr = curr.next;
                }
                System.out.println(sb);
            }
        }
        System.out.println("  ==========================================================================");
    }
}
