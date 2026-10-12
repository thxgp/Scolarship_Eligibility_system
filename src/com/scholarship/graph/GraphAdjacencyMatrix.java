package com.scholarship.graph;

import java.util.*;

/**
 * Custom Graph representation using a 2D Adjacency Matrix.
 * Fulfills CLO2 requirements:
 * 1. Matrix-based graph representation
 * 2. O(1) edge lookup and weight retrieval
 * 3. Tabular 2D matrix rendering
 */
public class GraphAdjacencyMatrix {

    private static final int INITIAL_CAPACITY = 16;
    private static final int NO_EDGE = 0;

    private int[][] matrix;
    private final List<String> vertices;
    private final Map<String, Integer> vertexIndexMap;

    public GraphAdjacencyMatrix() {
        this.matrix = new int[INITIAL_CAPACITY][INITIAL_CAPACITY];
        this.vertices = new ArrayList<>();
        this.vertexIndexMap = new HashMap<>();
    }

    public List<String> getVertices() {
        return Collections.unmodifiableList(vertices);
    }

    public int getVertexCount() {
        return vertices.size();
    }

    public int addVertex(String vertex) {
        if (vertex == null || vertex.trim().isEmpty()) return -1;
        String v = vertex.trim();
        if (vertexIndexMap.containsKey(v)) {
            return vertexIndexMap.get(v);
        }

        int index = vertices.size();
        ensureCapacity(index + 1);
        vertices.add(v);
        vertexIndexMap.put(v, index);
        return index;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= matrix.length) return;
        int newCap = Math.max(matrix.length * 2, minCapacity);
        int[][] newMatrix = new int[newCap][newCap];
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, newMatrix[i], 0, matrix.length);
        }
        this.matrix = newMatrix;
    }

    public boolean addEdge(String source, String destination, int weight, boolean directed) {
        if (source == null || destination == null) return false;
        int u = addVertex(source);
        int v = addVertex(destination);

        matrix[u][v] = weight > 0 ? weight : 1;
        if (!directed) {
            matrix[v][u] = weight > 0 ? weight : 1;
        }
        return true;
    }

    public boolean hasEdge(String source, String destination) {
        Integer u = vertexIndexMap.get(source);
        Integer v = vertexIndexMap.get(destination);
        if (u == null || v == null) return false;
        return matrix[u][v] != NO_EDGE;
    }

    public int getWeight(String source, String destination) {
        Integer u = vertexIndexMap.get(source);
        Integer v = vertexIndexMap.get(destination);
        if (u == null || v == null) return NO_EDGE;
        return matrix[u][v];
    }

    public List<String> getNeighbors(String vertex) {
        List<String> neighbors = new ArrayList<>();
        Integer u = vertexIndexMap.get(vertex);
        if (u == null) return neighbors;

        for (int col = 0; col < vertices.size(); col++) {
            if (matrix[u][col] != NO_EDGE) {
                neighbors.add(vertices.get(col));
            }
        }
        return neighbors;
    }

    public void printMatrix() {
        System.out.println("  ==========================================================================");
        System.out.println("             WORKFLOW GRAPH REPRESENTATION: ADJACENCY MATRIX");
        System.out.println("  ==========================================================================");

        int n = vertices.size();
        if (n == 0) {
            System.out.println("  (Matrix is empty)");
            return;
        }

        // Header row
        System.out.print(String.format("  %-16s | ", "STAGE"));
        for (int j = 0; j < n; j++) {
            String shortName = abbreviate(vertices.get(j), 8);
            System.out.print(String.format("%-8s ", shortName));
        }
        System.out.println();
        System.out.print("  " + "-".repeat(17) + "+" + "-".repeat(9 * n + 1));
        System.out.println();

        // Data rows
        for (int i = 0; i < n; i++) {
            System.out.print(String.format("  %-16s | ", abbreviate(vertices.get(i), 16)));
            for (int j = 0; j < n; j++) {
                int val = matrix[i][j];
                if (val == 0) {
                    System.out.print(String.format("%-8s ", "."));
                } else {
                    System.out.print(String.format("%-8s ", val + "d"));
                }
            }
            System.out.println();
        }
        System.out.println("  ==========================================================================");
    }

    private String abbreviate(String str, int maxLen) {
        if (str == null) return "";
        return str.length() <= maxLen ? str : str.substring(0, maxLen - 1) + ".";
    }
}
