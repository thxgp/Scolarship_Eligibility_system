package com.scholarship.searchsort;

import com.scholarship.model.StudentRecord;

import java.util.Comparator;

/**
 * Phase 3: Sorting Operations (CLO2).
 * Implements Merge Sort, Quick Sort, and Insertion Sort.
 * Captures comparison counts, swap counts, and runtime metrics for efficiency comparison.
 */
public class SortAlgorithms {

    public static class SortMetrics {
        public final String algorithmName;
        public long comparisons = 0;
        public long swapsOrMoves = 0;
        public long executionTimeNs = 0;

        public SortMetrics(String algorithmName) {
            this.algorithmName = algorithmName;
        }

        @Override
        public String toString() {
            return String.format("[%s] Time: %,d ns (%.3f ms) | Comparisons: %,d | Swaps/Moves: %,d",
                    algorithmName, executionTimeNs, executionTimeNs / 1_000_000.0, comparisons, swapsOrMoves);
        }
    }

    // =========================================================================
    // 1. Merge Sort (O(N log N) Stable Divide-and-Conquer)
    // =========================================================================

    public static SortMetrics mergeSort(StudentRecord[] array, Comparator<StudentRecord> comparator) {
        SortMetrics metrics = new SortMetrics("Merge Sort");
        if (array == null || array.length <= 1) return metrics;

        long start = System.nanoTime();
        StudentRecord[] aux = new StudentRecord[array.length];
        mergeSortRecursive(array, aux, 0, array.length - 1, comparator, metrics);
        metrics.executionTimeNs = System.nanoTime() - start;
        return metrics;
    }

    private static void mergeSortRecursive(StudentRecord[] array, StudentRecord[] aux, int low, int high,
                                           Comparator<StudentRecord> comparator, SortMetrics metrics) {
        if (low >= high) return;
        int mid = low + (high - low) / 2;

        mergeSortRecursive(array, aux, low, mid, comparator, metrics);
        mergeSortRecursive(array, aux, mid + 1, high, comparator, metrics);
        merge(array, aux, low, mid, high, comparator, metrics);
    }

    private static void merge(StudentRecord[] array, StudentRecord[] aux, int low, int mid, int high,
                              Comparator<StudentRecord> comparator, SortMetrics metrics) {
        for (int k = low; k <= high; k++) {
            aux[k] = array[k];
            metrics.swapsOrMoves++;
        }

        int i = low;
        int j = mid + 1;

        for (int k = low; k <= high; k++) {
            if (i > mid) {
                array[k] = aux[j++];
                metrics.swapsOrMoves++;
            } else if (j > high) {
                array[k] = aux[i++];
                metrics.swapsOrMoves++;
            } else {
                metrics.comparisons++;
                if (comparator.compare(aux[j], aux[i]) < 0) {
                    array[k] = aux[j++];
                } else {
                    array[k] = aux[i++];
                }
                metrics.swapsOrMoves++;
            }
        }
    }

    // =========================================================================
    // 2. Quick Sort (O(N log N) Average In-Place Divide-and-Conquer)
    // =========================================================================

    public static SortMetrics quickSort(StudentRecord[] array, Comparator<StudentRecord> comparator) {
        SortMetrics metrics = new SortMetrics("Quick Sort");
        if (array == null || array.length <= 1) return metrics;

        long start = System.nanoTime();
        quickSortRecursive(array, 0, array.length - 1, comparator, metrics);
        metrics.executionTimeNs = System.nanoTime() - start;
        return metrics;
    }

    private static void quickSortRecursive(StudentRecord[] array, int low, int high,
                                           Comparator<StudentRecord> comparator, SortMetrics metrics) {
        if (low < high) {
            int pivotIndex = partition(array, low, high, comparator, metrics);
            quickSortRecursive(array, low, pivotIndex - 1, comparator, metrics);
            quickSortRecursive(array, pivotIndex + 1, high, comparator, metrics);
        }
    }

    private static int partition(StudentRecord[] array, int low, int high,
                                 Comparator<StudentRecord> comparator, SortMetrics metrics) {
        StudentRecord pivot = array[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            metrics.comparisons++;
            if (comparator.compare(array[j], pivot) <= 0) {
                i++;
                swap(array, i, j);
                metrics.swapsOrMoves++;
            }
        }
        swap(array, i + 1, high);
        metrics.swapsOrMoves++;
        return i + 1;
    }

    // =========================================================================
    // 3. Insertion Sort (O(N^2) Worst, O(N) Best Incremental Sort)
    // =========================================================================

    public static SortMetrics insertionSort(StudentRecord[] array, Comparator<StudentRecord> comparator) {
        SortMetrics metrics = new SortMetrics("Insertion Sort");
        if (array == null || array.length <= 1) return metrics;

        long start = System.nanoTime();
        for (int i = 1; i < array.length; i++) {
            StudentRecord key = array[i];
            int j = i - 1;

            while (j >= 0) {
                metrics.comparisons++;
                if (comparator.compare(array[j], key) > 0) {
                    array[j + 1] = array[j];
                    metrics.swapsOrMoves++;
                    j--;
                } else {
                    break;
                }
            }
            array[j + 1] = key;
            metrics.swapsOrMoves++;
        }
        metrics.executionTimeNs = System.nanoTime() - start;
        return metrics;
    }

    private static void swap(StudentRecord[] array, int i, int j) {
        StudentRecord temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    // =========================================================================
    // Standard Comparators for the Scholarship System
    // =========================================================================

    /**
     * Merit score descending: Highest merit score ranked first (#1 rank).
     */
    public static final Comparator<StudentRecord> BY_MERIT_DESC = (a, b) ->
            Double.compare(b.getMeritScore(), a.getMeritScore());

    /**
     * Merit score ascending: Lowest merit score first (useful for binary search bounds).
     */
    public static final Comparator<StudentRecord> BY_MERIT_ASC = (a, b) ->
            Double.compare(a.getMeritScore(), b.getMeritScore());

    /**
     * Alphanumeric Student ID ascending: STU1001 < STU1002 < ...
     */
    public static final Comparator<StudentRecord> BY_ID_ASC = (a, b) ->
            a.getStudentId().compareToIgnoreCase(b.getStudentId());

    /**
     * CGPA descending.
     */
    public static final Comparator<StudentRecord> BY_GPA_DESC = (a, b) ->
            Double.compare(b.getGpa(), a.getGpa());
}
