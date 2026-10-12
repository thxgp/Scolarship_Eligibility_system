package com.scholarship.graph;

import com.scholarship.datastructures.LinkedQueue;
import com.scholarship.datastructures.LinkedStack;

import java.util.*;

/**
 * Institutional Scholarship Verification Workflow Graph.
 * Fulfills CLO2:
 * 1. Dual Graph Representation: Adjacency List & Adjacency Matrix
 * 2. Breadth-First Search (BFS) Traversal using custom LinkedQueue
 * 3. Depth-First Search (DFS) Traversal using custom LinkedStack
 * 4. Workflow route finding and stage reachability
 */
public class VerificationWorkflowGraph {

    // Standard Workflow Stages
    public static final String STAGE_PORTAL = "PortalSubmission";
    public static final String STAGE_DOC_AUDIT = "DocVerification";
    public static final String STAGE_INCOME_CHECK = "FinancialAudit";
    public static final String STAGE_ACAD_EVAL = "AcademicEval";
    public static final String STAGE_SPORTS_COUNCIL = "SportsCouncil";
    public static final String STAGE_DEAN_APPROVAL = "DeanApproval";
    public static final String STAGE_DISBURSEMENT = "FundDisburse";

    private final GraphAdjacencyList adjacencyList;
    private final GraphAdjacencyMatrix adjacencyMatrix;

    public VerificationWorkflowGraph() {
        this.adjacencyList = new GraphAdjacencyList();
        this.adjacencyMatrix = new GraphAdjacencyMatrix();
        buildDefaultWorkflow();
    }

    private void buildDefaultWorkflow() {
        // Directed stages with SLA duration (in business days)
        addWorkflowTransition(STAGE_PORTAL, STAGE_DOC_AUDIT, 1);
        addWorkflowTransition(STAGE_DOC_AUDIT, STAGE_ACAD_EVAL, 2);
        addWorkflowTransition(STAGE_DOC_AUDIT, STAGE_INCOME_CHECK, 3);
        addWorkflowTransition(STAGE_DOC_AUDIT, STAGE_SPORTS_COUNCIL, 2);
        addWorkflowTransition(STAGE_ACAD_EVAL, STAGE_DEAN_APPROVAL, 2);
        addWorkflowTransition(STAGE_INCOME_CHECK, STAGE_DEAN_APPROVAL, 2);
        addWorkflowTransition(STAGE_SPORTS_COUNCIL, STAGE_DEAN_APPROVAL, 1);
        addWorkflowTransition(STAGE_DEAN_APPROVAL, STAGE_DISBURSEMENT, 3);
    }

    public void addWorkflowTransition(String fromStage, String toStage, int slaDays) {
        adjacencyList.addEdge(fromStage, toStage, slaDays, true);
        adjacencyMatrix.addEdge(fromStage, toStage, slaDays, true);
    }

    public GraphAdjacencyList getAdjacencyList() { return adjacencyList; }
    public GraphAdjacencyMatrix getAdjacencyMatrix() { return adjacencyMatrix; }

    // =========================================================================
    // 1. Breadth-First Search (BFS) Traversal using custom LinkedQueue
    // =========================================================================

    /**
     * Performs Breadth-First Search starting from a given stage.
     * Uses custom LinkedQueue to visit stages level-by-level.
     *
     * @param startStage Stage to begin traversal
     * @return List of stages in BFS traversal order
     */
    public List<String> bfs(String startStage) {
        List<String> visitedOrder = new ArrayList<>();
        if (startStage == null || !adjacencyList.getVertices().contains(startStage)) {
            return visitedOrder;
        }

        Set<String> visited = new HashSet<>();
        LinkedQueue<String> queue = new LinkedQueue<>();

        visited.add(startStage);
        queue.enqueue(startStage);

        while (!queue.isEmpty()) {
            String current = queue.dequeue();
            visitedOrder.add(current);

            for (String neighbor : adjacencyList.getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.enqueue(neighbor);
                }
            }
        }
        return visitedOrder;
    }

    // =========================================================================
    // 2. Depth-First Search (DFS) Traversal using custom LinkedStack
    // =========================================================================

    /**
     * Performs Depth-First Search starting from a given stage.
     * Uses custom LinkedStack to explore verification branches deeply.
     *
     * @param startStage Stage to begin traversal
     * @return List of stages in DFS traversal order
     */
    public List<String> dfs(String startStage) {
        List<String> visitedOrder = new ArrayList<>();
        if (startStage == null || !adjacencyList.getVertices().contains(startStage)) {
            return visitedOrder;
        }

        Set<String> visited = new HashSet<>();
        LinkedStack<String> stack = new LinkedStack<>();

        stack.push(startStage);

        while (!stack.isEmpty()) {
            String current = stack.pop();

            if (!visited.contains(current)) {
                visited.add(current);
                visitedOrder.add(current);

                // Push neighbors onto stack (in reverse order to preserve canonical visit order)
                List<String> neighbors = adjacencyList.getNeighbors(current);
                for (int i = neighbors.size() - 1; i >= 0; i--) {
                    String neighbor = neighbors.get(i);
                    if (!visited.contains(neighbor)) {
                        stack.push(neighbor);
                    }
                }
            }
        }
        return visitedOrder;
    }

    // =========================================================================
    // 3. Reachability & Path Finding
    // =========================================================================

    public boolean isReachable(String source, String destination) {
        if (source == null || destination == null) return false;
        List<String> traversal = bfs(source);
        return traversal.contains(destination);
    }

    /**
     * Finds a valid verification path from source to target stage.
     */
    public List<String> findVerificationPath(String source, String destination) {
        List<String> path = new ArrayList<>();
        if (source == null || destination == null) return path;

        Map<String, String> parentMap = new HashMap<>();
        Set<String> visited = new HashSet<>();
        LinkedQueue<String> queue = new LinkedQueue<>();

        visited.add(source);
        queue.enqueue(source);

        boolean found = false;
        while (!queue.isEmpty()) {
            String curr = queue.dequeue();
            if (curr.equalsIgnoreCase(destination)) {
                found = true;
                break;
            }

            for (String neighbor : adjacencyList.getNeighbors(curr)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, curr);
                    queue.enqueue(neighbor);
                }
            }
        }

        if (found) {
            String curr = destination;
            while (curr != null) {
                path.add(0, curr);
                curr = parentMap.get(curr);
            }
        }
        return path;
    }

    public void displayWorkflowRepresentations() {
        adjacencyList.printGraph();
        System.out.println();
        adjacencyMatrix.printMatrix();
    }
}
