package com.scholarship.searchsort;

import com.scholarship.model.StudentRecord;

import java.util.Random;

/**
 * Phase 3: Algorithm Performance Benchmarking & Conceptual Comparison (CLO2).
 * Compares Linear Search vs Binary Search, and Merge Sort vs Quick Sort vs Insertion Sort.
 */
public class AlgorithmBenchmark {

    public static class BenchmarkReport {
        public SortAlgorithms.SortMetrics mergeSortMetrics;
        public SortAlgorithms.SortMetrics quickSortMetrics;
        public SortAlgorithms.SortMetrics insertionSortMetrics;
        public SearchAlgorithms.SearchResult linearSearchResult;
        public SearchAlgorithms.SearchResult binarySearchResult;
        public int datasetSize;
    }

    /**
     * Generates an array of synthetic StudentRecords with random GPA, ECA, income, attendance.
     */
    public static StudentRecord[] generateRandomDataset(int size) {
        StudentRecord[] records = new StudentRecord[size];
        Random rand = new Random(42); // Fixed seed for reproducible benchmarks

        String[] categories = {"Merit-Based", "Need-Based", "Sports/ECA", "General"};
        String[] statuses = {"Approved", "Pending", "Under Review"};

        for (int i = 0; i < size; i++) {
            String id = String.format("STU%05d", (i + 1));
            String name = "Student_" + (i + 1);
            double gpa = 5.0 + rand.nextDouble() * 5.0; // 5.0 to 10.0
            double income = 150000.0 + rand.nextDouble() * 850000.0;
            double attendance = 60.0 + rand.nextDouble() * 40.0;
            double eca = 40.0 + rand.nextDouble() * 60.0;
            String cat = categories[rand.nextInt(categories.length)];
            String status = statuses[rand.nextInt(statuses.length)];

            records[i] = new StudentRecord(id, name, gpa, income, attendance, eca, cat, status);
        }
        return records;
    }

    /**
     * Deep clones an array of StudentRecord objects.
     */
    public static StudentRecord[] cloneArray(StudentRecord[] original) {
        StudentRecord[] copy = new StudentRecord[original.length];
        for (int i = 0; i < original.length; i++) {
            copy[i] = new StudentRecord(original[i]);
        }
        return copy;
    }

    /**
     * Executes the comprehensive benchmark on a given dataset size.
     */
    public static BenchmarkReport runBenchmark(int size) {
        BenchmarkReport report = new BenchmarkReport();
        report.datasetSize = size;

        StudentRecord[] base = generateRandomDataset(size);

        // Sort benchmarks
        StudentRecord[] arrMerge = cloneArray(base);
        report.mergeSortMetrics = SortAlgorithms.mergeSort(arrMerge, SortAlgorithms.BY_MERIT_DESC);

        StudentRecord[] arrQuick = cloneArray(base);
        report.quickSortMetrics = SortAlgorithms.quickSort(arrQuick, SortAlgorithms.BY_MERIT_DESC);

        StudentRecord[] arrInsert = cloneArray(base);
        report.insertionSortMetrics = SortAlgorithms.insertionSort(arrInsert, SortAlgorithms.BY_MERIT_DESC);

        // Search benchmarks: Find target in the middle of dataset
        String targetId = String.format("STU%05d", size / 2);

        // Linear search on unsorted base array
        report.linearSearchResult = SearchAlgorithms.linearSearchById(base, targetId);

        // Binary search requires array sorted by studentId ascending
        StudentRecord[] arrForBinarySearch = cloneArray(base);
        SortAlgorithms.quickSort(arrForBinarySearch, SortAlgorithms.BY_ID_ASC);
        report.binarySearchResult = SearchAlgorithms.binarySearchById(arrForBinarySearch, targetId);

        return report;
    }

    /**
     * Prints a formatted ASCII comparative matrix.
     */
    public static void printComparisonTable(BenchmarkReport report) {
        System.out.println("========================================================================================================");
        System.out.printf("   ALGORITHM COMPLEXITY & PERFORMANCE BENCHMARK MATRIX (DATASET SIZE: %d STUDENTS)\n", report.datasetSize);
        System.out.println("========================================================================================================");
        System.out.printf("%-16s | %-10s | %-10s | %-10s | %-10s | %-8s | %-12s | %-12s\n",
                "Algorithm", "Best Case", "Avg Case", "Worst Case", "Space", "Stable?", "Empirical (ns)", "Comparisons");
        System.out.println("-----------------+------------+------------+------------+------------+----------+--------------+--------------");

        // Merge Sort
        System.out.printf("%-16s | %-10s | %-10s | %-10s | %-10s | %-8s | %,12d | %,12d\n",
                "Merge Sort", "O(N log N)", "O(N log N)", "O(N log N)", "O(N)", "Yes",
                report.mergeSortMetrics.executionTimeNs, report.mergeSortMetrics.comparisons);

        // Quick Sort
        System.out.printf("%-16s | %-10s | %-10s | %-10s | %-10s | %-8s | %,12d | %,12d\n",
                "Quick Sort", "O(N log N)", "O(N log N)", "O(N^2)", "O(log N)", "No",
                report.quickSortMetrics.executionTimeNs, report.quickSortMetrics.comparisons);

        // Insertion Sort
        System.out.printf("%-16s | %-10s | %-10s | %-10s | %-10s | %-8s | %,12d | %,12d\n",
                "Insertion Sort", "O(N)", "O(N^2)", "O(N^2)", "O(1)", "Yes",
                report.insertionSortMetrics.executionTimeNs, report.insertionSortMetrics.comparisons);

        System.out.println("-----------------+------------+------------+------------+------------+----------+--------------+--------------");

        // Linear Search
        System.out.printf("%-16s | %-10s | %-10s | %-10s | %-10s | %-8s | %,12d | %,12d\n",
                "Linear Search", "O(1)", "O(N)", "O(N)", "O(1)", "N/A",
                report.linearSearchResult.executionTimeNs, report.linearSearchResult.comparisons);

        // Binary Search
        System.out.printf("%-16s | %-10s | %-10s | %-10s | %-10s | %-8s | %,12d | %,12d\n",
                "Binary Search", "O(1)", "O(log N)", "O(log N)", "O(1)", "N/A",
                report.binarySearchResult.executionTimeNs, report.binarySearchResult.comparisons);

        System.out.println("========================================================================================================");
    }
}
