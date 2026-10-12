package com.scholarship.algorithms;

import com.scholarship.model.StudentRecord;

import java.util.*;

/**
 * Phase 4: Basic Algorithmic Problem Solving - Backtracking Technique (CLO3).
 * Solves the Multi-Constraint Scholarship Committee / Quota Assignment Problem.
 *
 * Problem:
 * Given K distinct scholarship quota evaluation panels, assign students from an applicant pool
 * such that:
 * 1. Each slot's minimum merit requirement is satisfied.
 * 2. Institutional Diversity Constraint: No two assigned students in the panel can belong to
 *    the same category (Merit-Based, Need-Based, Sports/ECA, General) to ensure balanced representation.
 * 3. Each student can be assigned to at most one slot.
 *
 * Backtracking Mechanism:
 * Uses recursive depth-first search with constraint-based pruning.
 * If a branch leads to an infeasible assignment, the algorithm backtracks
 * by restoring the candidate state and trying alternative branches.
 */
public class BacktrackingCommitteeAssigner {

    public static class SlotRequirement {
        public final String slotName;
        public final double minMeritRequired;

        public SlotRequirement(String slotName, double minMeritRequired) {
            this.slotName = slotName;
            this.minMeritRequired = minMeritRequired;
        }

        @Override
        public String toString() {
            return String.format("%s (Min Merit: %.1f)", slotName, minMeritRequired);
        }
    }

    public static class AssignmentResult {
        public final boolean success;
        public final Map<String, StudentRecord> slotAssignments;
        public final int statesExplored;
        public final int branchesPruned;

        public AssignmentResult(boolean success, Map<String, StudentRecord> slotAssignments,
                                int statesExplored, int branchesPruned) {
            this.success = success;
            this.slotAssignments = slotAssignments;
            this.statesExplored = statesExplored;
            this.branchesPruned = branchesPruned;
        }

        @Override
        public String toString() {
            if (!success) {
                return String.format("[BACKTRACKING FAILED] No valid assignment exists. States explored: %d, Pruned: %d",
                        statesExplored, branchesPruned);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("[SUCCESS] Committee Slots Assigned (States: %d, Pruned: %d):\n",
                    statesExplored, branchesPruned));
            slotAssignments.forEach((slot, student) ->
                    sb.append(String.format("   %-20s -> [%s] %-16s | Cat: %-12s | Merit: %.2f\n",
                            slot, student.getStudentId(), student.getName(), student.getCategory(), student.getMeritScore())));
            return sb.toString();
        }
    }

    private int statesExplored;
    private int branchesPruned;

    public AssignmentResult assignCommitteeSlots(List<StudentRecord> applicants, List<SlotRequirement> slots) {
        statesExplored = 0;
        branchesPruned = 0;

        if (applicants == null || slots == null || applicants.isEmpty() || slots.isEmpty()) {
            return new AssignmentResult(false, new LinkedHashMap<>(), 0, 0);
        }

        StudentRecord[] currentAssignment = new StudentRecord[slots.size()];
        boolean[] usedStudents = new boolean[applicants.size()];
        Set<String> assignedCategories = new HashSet<>();

        boolean solved = backtrack(0, applicants, slots, currentAssignment, usedStudents, assignedCategories);

        Map<String, StudentRecord> finalMap = new LinkedHashMap<>();
        if (solved) {
            for (int i = 0; i < slots.size(); i++) {
                finalMap.put(slots.get(i).slotName, currentAssignment[i]);
            }
        }

        return new AssignmentResult(solved, finalMap, statesExplored, branchesPruned);
    }

    private boolean backtrack(int slotIndex, List<StudentRecord> applicants, List<SlotRequirement> slots,
                              StudentRecord[] assignment, boolean[] used, Set<String> assignedCategories) {
        statesExplored++;

        // Base case: All slots successfully assigned!
        if (slotIndex == slots.size()) {
            return true;
        }

        SlotRequirement req = slots.get(slotIndex);

        for (int i = 0; i < applicants.size(); i++) {
            if (used[i]) continue;

            StudentRecord candidate = applicants.get(i);

            // Constraint Check 1: Minimum Merit Requirement
            if (candidate.getMeritScore() < req.minMeritRequired) {
                branchesPruned++;
                continue;
            }

            // Constraint Check 2: Diversity Requirement (Unique category per panel slot)
            if (assignedCategories.contains(candidate.getCategory())) {
                branchesPruned++;
                continue;
            }

            // Make Choice
            used[i] = true;
            assignment[slotIndex] = candidate;
            assignedCategories.add(candidate.getCategory());

            // Recurse to next slot
            if (backtrack(slotIndex + 1, applicants, slots, assignment, used, assignedCategories)) {
                return true; // Propagate success
            }

            // Undo Choice (Backtrack!)
            used[i] = false;
            assignment[slotIndex] = null;
            assignedCategories.remove(candidate.getCategory());
        }

        return false; // No candidate satisfies constraints for this slot
    }
}
