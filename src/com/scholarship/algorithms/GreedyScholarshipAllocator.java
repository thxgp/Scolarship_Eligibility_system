package com.scholarship.algorithms;

import com.scholarship.model.StudentRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Phase 4: Basic Algorithmic Problem Solving - Greedy Technique (CLO3).
 * Solves the Fractional / Continuous Budget Allocation Problem.
 *
 * Strategy:
 * Evaluates the "Merit-per-Rupee" efficiency ratio:
 * Ratio = (Merit Score) / (Requested Grant Amount)
 * Greedily selects applicants with highest ratio to maximize overall academic merit supported.
 */
public class GreedyScholarshipAllocator {

    public static class AllocationItem {
        public final StudentRecord student;
        public final double requestedAmount;
        public final double allocatedAmount;
        public final double fractionAwarded; // 1.0 = Full, < 1.0 = Partial
        public final double meritAchieved;
        public final double efficiencyRatio;

        public AllocationItem(StudentRecord student, double requestedAmount, double allocatedAmount,
                              double fractionAwarded, double meritAchieved, double efficiencyRatio) {
            this.student = student;
            this.requestedAmount = requestedAmount;
            this.allocatedAmount = allocatedAmount;
            this.fractionAwarded = fractionAwarded;
            this.meritAchieved = meritAchieved;
            this.efficiencyRatio = efficiencyRatio;
        }

        @Override
        public String toString() {
            return String.format("[%s] %-16s | Requested: ₹%,.0f | Awarded: ₹%,.0f (%.1f%%) | Merit Gain: %.2f | Ratio: %.6f",
                    student.getStudentId(), student.getName(), requestedAmount, allocatedAmount,
                    fractionAwarded * 100.0, meritAchieved, efficiencyRatio);
        }
    }

    public static class AllocationResult {
        public final double totalBudget;
        public final double spentBudget;
        public final double remainingBudget;
        public final double totalMeritYield;
        public final List<AllocationItem> allocations;

        public AllocationResult(double totalBudget, double spentBudget, double remainingBudget,
                                double totalMeritYield, List<AllocationItem> allocations) {
            this.totalBudget = totalBudget;
            this.spentBudget = spentBudget;
            this.remainingBudget = remainingBudget;
            this.totalMeritYield = totalMeritYield;
            this.allocations = allocations;
        }
    }

    /**
     * Helper candidate class with requested grant and calculated ratio.
     */
    public static class Candidate {
        public final StudentRecord student;
        public final double requestedGrant;
        public final double ratio;

        public Candidate(StudentRecord student, double requestedGrant) {
            this.student = student;
            this.requestedGrant = requestedGrant > 0 ? requestedGrant : 50000.0;
            this.ratio = student.getMeritScore() / this.requestedGrant;
        }
    }

    /**
     * Executes the Greedy Allocation Algorithm.
     * Time Complexity: O(N log N) due to greedy ratio sorting.
     * Space Complexity: O(N) for candidate wrappers and result list.
     */
    public static AllocationResult allocateBudget(List<StudentRecord> students,
                                                  List<Double> requestedGrants,
                                                  double totalBudget) {
        if (students == null || students.isEmpty() || totalBudget <= 0) {
            return new AllocationResult(totalBudget, 0, totalBudget, 0, new ArrayList<>());
        }

        int n = students.size();
        Candidate[] candidates = new Candidate[n];
        for (int i = 0; i < n; i++) {
            double req = (requestedGrants != null && i < requestedGrants.size()) ? requestedGrants.get(i) : 50000.0;
            candidates[i] = new Candidate(students.get(i), req);
        }

        // Greedy choice: Sort descending by Merit-to-Grant ratio
        Arrays.sort(candidates, (a, b) -> Double.compare(b.ratio, a.ratio));

        double remaining = totalBudget;
        double spent = 0;
        double totalMerit = 0;
        List<AllocationItem> items = new ArrayList<>();

        for (Candidate c : candidates) {
            if (remaining <= 0) break;

            if (c.requestedGrant <= remaining) {
                // Award full grant
                double awarded = c.requestedGrant;
                double meritGain = c.student.getMeritScore();
                items.add(new AllocationItem(c.student, c.requestedGrant, awarded, 1.0, meritGain, c.ratio));
                spent += awarded;
                remaining -= awarded;
                totalMerit += meritGain;
            } else {
                // Award fractional grant with remaining budget
                double awarded = remaining;
                double fraction = awarded / c.requestedGrant;
                double meritGain = c.student.getMeritScore() * fraction;
                items.add(new AllocationItem(c.student, c.requestedGrant, awarded, fraction, meritGain, c.ratio));
                spent += awarded;
                remaining = 0;
                totalMerit += meritGain;
                break;
            }
        }

        return new AllocationResult(totalBudget, spent, remaining, totalMerit, items);
    }
}
