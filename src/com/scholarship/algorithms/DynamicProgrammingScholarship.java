package com.scholarship.algorithms;

import com.scholarship.model.StudentRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 4: Basic Algorithmic Problem Solving - Dynamic Programming (CLO3).
 * Solves the 0/1 Discrete Knapsack Scholarship Selection Problem.
 *
 * Problem:
 * Fixed budget B. Each student requires a full indivisible grant amount (cost C_i)
 * and contributes merit value M_i.
 * We must choose an optimal subset of students to maximize total merit without exceeding B.
 *
 * Recurrence Relation:
 * DP[i][w] = DP[i-1][w]                                      if C_i > w
 * DP[i][w] = max(DP[i-1][w], DP[i-1][w - C_i] + M_i)        if C_i <= w
 */
public class DynamicProgrammingScholarship {

    public static class DPResult {
        public final int totalBudget;
        public final int spentBudget;
        public final double maxMeritAchieved;
        public final List<StudentRecord> selectedStudents;
        public final double[][] dpTable; // For educational inspection

        public DPResult(int totalBudget, int spentBudget, double maxMeritAchieved,
                        List<StudentRecord> selectedStudents, double[][] dpTable) {
            this.totalBudget = totalBudget;
            this.spentBudget = spentBudget;
            this.maxMeritAchieved = maxMeritAchieved;
            this.selectedStudents = selectedStudents;
            this.dpTable = dpTable;
        }
    }

    /**
     * Solves the 0/1 Knapsack problem for discrete scholarship grant allocation.
     *
     * @param students List of applicants
     * @param grantCosts Array of grant costs corresponding to each student (in integer units e.g. thousands)
     * @param budgetCapacity Maximum budget available (in same units)
     * @return DPResult with optimal students and maximum merit
     */
    public static DPResult solveDiscreteGrants(List<StudentRecord> students, int[] grantCosts, int budgetCapacity) {
        if (students == null || students.isEmpty() || grantCosts == null || budgetCapacity <= 0) {
            return new DPResult(budgetCapacity, 0, 0.0, new ArrayList<>(), new double[0][0]);
        }

        int n = Math.min(students.size(), grantCosts.length);
        double[][] dp = new double[n + 1][budgetCapacity + 1];

        // Fill DP Table
        for (int i = 1; i <= n; i++) {
            StudentRecord student = students.get(i - 1);
            int cost = grantCosts[i - 1];
            double merit = student.getMeritScore();

            for (int w = 0; w <= budgetCapacity; w++) {
                if (cost <= w) {
                    dp[i][w] = Math.max(dp[i - 1][w], dp[i - 1][w - cost] + merit);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        double maxMerit = dp[n][budgetCapacity];

        // Backtrack to find the exact set of selected students
        List<StudentRecord> selected = new ArrayList<>();
        int w = budgetCapacity;
        int spent = 0;

        for (int i = n; i > 0 && maxMerit > 0; i--) {
            // If value differs from the cell above, item i was included
            if (dp[i][w] != dp[i - 1][w]) {
                StudentRecord picked = students.get(i - 1);
                selected.add(picked);
                int cost = grantCosts[i - 1];
                spent += cost;
                w -= cost;
            }
        }

        Collections.reverse(selected);
        return new DPResult(budgetCapacity, spent, maxMerit, selected, dp);
    }
}
