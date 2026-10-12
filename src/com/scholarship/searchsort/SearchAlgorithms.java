package com.scholarship.searchsort;

import com.scholarship.model.StudentRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 3: Searching Operations (CLO2).
 * Implements Linear Search and Binary Search on Student Records.
 * Tracks comparison counts and execution time for asymptotic efficiency analysis.
 */
public class SearchAlgorithms {

    /**
     * Metrics envelope containing search outcomes and empirical performance data.
     */
    public static class SearchResult {
        public final StudentRecord record;
        public final int index;
        public final int comparisons;
        public final long executionTimeNs;
        public final String algorithmName;

        public SearchResult(StudentRecord record, int index, int comparisons, long executionTimeNs, String algorithmName) {
            this.record = record;
            this.index = index;
            this.comparisons = comparisons;
            this.executionTimeNs = executionTimeNs;
            this.algorithmName = algorithmName;
        }

        public boolean isFound() {
            return index != -1;
        }

        @Override
        public String toString() {
            return String.format("[%s] Found: %b (Index: %d, Comparisons: %d, Time: %d ns)",
                    algorithmName, isFound(), index, comparisons, executionTimeNs);
        }
    }

    // =========================================================================
    // 1. Linear Search (O(N) Time Complexity, Works on Unsorted Data)
    // =========================================================================

    /**
     * Performs Linear Search to find a student by unique ID.
     * Time Complexity: Best O(1), Average O(N), Worst O(N)
     */
    public static SearchResult linearSearchById(StudentRecord[] records, String targetId) {
        if (records == null || targetId == null) {
            return new SearchResult(null, -1, 0, 0, "Linear Search");
        }

        long start = System.nanoTime();
        int comparisons = 0;
        String trimmed = targetId.trim();

        for (int i = 0; i < records.length; i++) {
            comparisons++;
            if (records[i] != null && records[i].getStudentId().equalsIgnoreCase(trimmed)) {
                long elapsed = System.nanoTime() - start;
                return new SearchResult(records[i], i, comparisons, elapsed, "Linear Search");
            }
        }
        long elapsed = System.nanoTime() - start;
        return new SearchResult(null, -1, comparisons, elapsed, "Linear Search");
    }

    /**
     * Linear search matching students by full or partial name.
     */
    public static List<StudentRecord> linearSearchByName(StudentRecord[] records, String query) {
        List<StudentRecord> matches = new ArrayList<>();
        if (records == null || query == null) return matches;

        String q = query.trim().toLowerCase();
        for (StudentRecord r : records) {
            if (r != null && r.getName().toLowerCase().contains(q)) {
                matches.add(r);
            }
        }
        return matches;
    }

    // =========================================================================
    // 2. Binary Search (O(log N) Time Complexity, Requires Sorted Data)
    // =========================================================================

    /**
     * Performs Binary Search by unique studentId.
     * Precondition: records array MUST be sorted ascending by studentId.
     * Time Complexity: Best O(1), Average O(log N), Worst O(log N)
     */
    public static SearchResult binarySearchById(StudentRecord[] sortedRecords, String targetId) {
        if (sortedRecords == null || targetId == null || sortedRecords.length == 0) {
            return new SearchResult(null, -1, 0, 0, "Binary Search");
        }

        long start = System.nanoTime();
        int comparisons = 0;
        int low = 0;
        int high = sortedRecords.length - 1;
        String target = targetId.trim();

        while (low <= high) {
            comparisons++;
            int mid = low + (high - low) / 2;
            StudentRecord midRecord = sortedRecords[mid];

            if (midRecord == null) {
                break;
            }

            int cmp = midRecord.getStudentId().compareToIgnoreCase(target);
            if (cmp == 0) {
                long elapsed = System.nanoTime() - start;
                return new SearchResult(midRecord, mid, comparisons, elapsed, "Binary Search");
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        long elapsed = System.nanoTime() - start;
        return new SearchResult(null, -1, comparisons, elapsed, "Binary Search");
    }

    /**
     * Binary Search to find the first student whose merit score meets or exceeds a minimum cutoff.
     * Precondition: records sorted in ascending order of meritScore.
     */
    public static int binarySearchCutoffBound(StudentRecord[] sortedByMeritAscending, double minMerit) {
        if (sortedByMeritAscending == null || sortedByMeritAscending.length == 0) return -1;

        int low = 0;
        int high = sortedByMeritAscending.length - 1;
        int resultIndex = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (sortedByMeritAscending[mid].getMeritScore() >= minMerit) {
                resultIndex = mid;
                high = mid - 1; // Look for earlier eligible student
            } else {
                low = mid + 1;
            }
        }
        return resultIndex;
    }
}
