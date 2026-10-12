# Student Scholarship Eligibility and Merit System (Phases 1, 2, 3 & 4 - Java)
**Course**: 26MCC1L211: Data Structures and Algorithms Practical  
**All 4 Phases Fully Completed**:
- **Phase 1**: Linear Data Structures Implementation (CLO1)
- **Phase 2**: Hierarchical and Graph Structures (CLO2)
- **Phase 3**: Searching and Sorting Operations (CLO2)
- **Phase 4**: Basic Algorithmic Problem Solving (CLO3)

---

## 📌 Project Overview
The **Student Scholarship Eligibility and Merit System** is developed in **Java 21** using pure, node-based and algorithmic techniques without relying on standard collection wrappers (such as `java.util.LinkedList`, `java.util.Stack`, `java.util.TreeSet`).

---

## 🎯 Syllabus Mapping & Deliverables

### Phase 1: Linear Data Structures Implementation (CLO1)
| Syllabus Requirement | Implementation Details | Time / Space Complexity |
| :--- | :--- | :--- |
| **Input Data Schema & Records** | [`StudentRecord.java`](file:///src/com/scholarship/model/StudentRecord.java), [`ActionRecord.java`](file:///src/com/scholarship/model/ActionRecord.java) | $O(1)$ schema validation, state snapshots for undo history |
| **Linked List Storage** | [`StudentLinkedList.java`](file:///src/com/scholarship/datastructures/StudentLinkedList.java), [`ListNode.java`](file:///src/com/scholarship/datastructures/ListNode.java) | $O(1)$ Head Insert, $O(N)$ Tail & Sorted Insert by Merit, $O(N)$ Delete by ID |
| **Application Pipeline (FIFO)** | [`LinkedQueue.java`](file:///src/com/scholarship/datastructures/LinkedQueue.java), [`QueueNode.java`](file:///src/com/scholarship/datastructures/QueueNode.java) | $O(1)$ Enqueue & Dequeue verification pipeline |
| **Evaluator Undo History (LIFO)**| [`LinkedStack.java`](file:///src/com/scholarship/datastructures/LinkedStack.java), [`StackNode.java`](file:///src/com/scholarship/datastructures/StackNode.java) | $O(1)$ Push & Pop multi-level action rollback |
| **Stack Expression Evaluator** | [`ExpressionParser.java`](file:///src/com/scholarship/expression/ExpressionParser.java) | $O(N)$ Delimiter balancing, Infix-to-Postfix conversion (Shunting-Yard), Postfix dynamic evaluation |

### Phase 2: Hierarchical and Graph Structures (CLO2)
| Syllabus Requirement | Implementation Details | Time / Space Complexity |
| :--- | :--- | :--- |
| **Binary Tree for Structured Storage** | [`BinaryTree.java`](file:///src/com/scholarship/tree/BinaryTree.java), [`BinaryTreeNode.java`](file:///src/com/scholarship/tree/BinaryTreeNode.java), [`ScholarshipCategoryTree.java`](file:///src/com/scholarship/tree/ScholarshipCategoryTree.java) | $O(N)$ Hierarchical category tree representing institutional grant divisions, leaves, and criteria matching |
| **Tree Traversals** | `inorder()`, `preorder()`, `postorder()`, `levelOrder()` | $O(N)$ time traversals. Level-order implemented using custom Phase 1 `LinkedQueue` |
| **Binary Search Tree (BST)** | [`StudentBST.java`](file:///src/com/scholarship/tree/StudentBST.java), [`BSTNode.java`](file:///src/com/scholarship/tree/BSTNode.java) | $O(\log N)$ average insertion, search by student ID, and 3-case deletion (leaf, 1 child, 2 children with successor) |
| **Graph Representation** | [`GraphAdjacencyList.java`](file:///src/com/scholarship/graph/GraphAdjacencyList.java), [`GraphAdjacencyMatrix.java`](file:///src/com/scholarship/graph/GraphAdjacencyMatrix.java) | Dual representation: Linked-node Adjacency List + 2D Adjacency Matrix with SLA processing times |
| **Graph Traversals (BFS & DFS)** | [`VerificationWorkflowGraph.java`](file:///src/com/scholarship/graph/VerificationWorkflowGraph.java) | $O(V + E)$ BFS (using `LinkedQueue`) for level milestones and $O(V + E)$ DFS (using `LinkedStack`) for deep audit routes |

### Phase 3: Searching and Sorting Operations (CLO2)
| Syllabus Requirement | Implementation Details | Time / Space Complexity |
| :--- | :--- | :--- |
| **Linear Search & Binary Search** | [`SearchAlgorithms.java`](file:///src/com/scholarship/searchsort/SearchAlgorithms.java) | Linear Search ($O(N)$) on unsorted arrays, Binary Search ($O(\log N)$) by unique student ID and merit cutoff bounds |
| **Sorting Techniques** | [`SortAlgorithms.java`](file:///src/com/scholarship/searchsort/SortAlgorithms.java) | **Merge Sort** ($O(N \log N)$ stable merit ranking), **Quick Sort** ($O(N \log N)$ ID sorting), **Insertion Sort** ($O(N^2)$ GPA sort) |
| **Conceptual & Empirical Benchmark** | [`AlgorithmBenchmark.java`](file:///src/com/scholarship/searchsort/AlgorithmBenchmark.java) | Automated runtime benchmark (ns), comparison counters, and asymptotic complexity matrix |
| **Workflow Integration** | [`ScholarshipService.java`](file:///src/com/scholarship/service/ScholarshipService.java) | Automated sorting & binary searching integrated into the service pipeline |

### Phase 4: Basic Algorithmic Problem Solving (CLO3)
| Syllabus Requirement | Implementation Details | Time / Space Complexity |
| :--- | :--- | :--- |
| **Greedy Technique** | [`GreedyScholarshipAllocator.java`](file:///src/com/scholarship/algorithms/GreedyScholarshipAllocator.java) | Fractional Knapsack budget allocation maximizing total academic merit per rupee spent ($O(N \log N)$) |
| **Dynamic Programming** | [`DynamicProgrammingScholarship.java`](file:///src/com/scholarship/algorithms/DynamicProgrammingScholarship.java) | 0/1 Knapsack discrete grant optimization for full indivisible awards ($O(N \cdot W)$ time and space) |
| **Backtracking Technique** | [`BacktrackingCommitteeAssigner.java`](file:///src/com/scholarship/algorithms/BacktrackingCommitteeAssigner.java) | Constraint Satisfaction Problem (CSP) assigning applicants to committee quota panels with diversity pruning |

---

## 📁 Directory Structure

```
scholarship_system_phase1_java/
├── src/
│   └── com/
│       └── scholarship/
│           ├── model/
│           │   ├── StudentRecord.java                  # Student record schema & validation
│           │   └── ActionRecord.java                   # Audit snapshot for Undo stack
│           ├── datastructures/
│           │   ├── ListNode.java                       # Node for Singly Linked List
│           │   ├── StudentLinkedList.java              # Custom Singly Linked List
│           │   ├── StackNode.java                      # Node for Linked Stack
│           │   ├── LinkedStack.java                    # Custom LIFO Stack
│           │   ├── QueueNode.java                      # Node for Linked Queue
│           │   └── LinkedQueue.java                    # Custom FIFO Queue
│           ├── expression/
│           │   └── ExpressionParser.java               # Stack-based expression parser
│           ├── tree/
│           │   ├── BinaryTreeNode.java                 # Generic Binary Tree node
│           │   ├── BinaryTree.java                     # Tree traversals & height/size utilities
│           │   ├── ScholarshipCategoryTree.java        # Category allocation binary tree
│           │   ├── BSTNode.java                        # Node for Student BST
│           │   └── StudentBST.java                     # O(log N) Student BST with 3-case delete
│           ├── graph/
│           │   ├── GraphAdjacencyList.java             # Linked Edge Adjacency List
│           │   ├── GraphAdjacencyMatrix.java           # 2D Grid Adjacency Matrix
│           │   └── VerificationWorkflowGraph.java      # Institutional workflow with BFS & DFS
│           ├── searchsort/
│           │   ├── SearchAlgorithms.java               # Linear Search and Binary Search
│           │   ├── SortAlgorithms.java                 # Merge Sort, Quick Sort, Insertion Sort
│           │   └── AlgorithmBenchmark.java             # Runtime & Complexity Benchmark Matrix
│           ├── algorithms/
│           │   ├── GreedyScholarshipAllocator.java     # Greedy budget & ratio maximizer
│           │   ├── DynamicProgrammingScholarship.java  # DP 0/1 Knapsack discrete grant optimizer
│           │   └── BacktrackingCommitteeAssigner.java  # Backtracking multi-constraint panel solver
│           ├── service/
│           │   └── ScholarshipService.java             # Unified coordinator service
│           ├── test/
│           │   └── TestSuite.java                      # Automated test suite (150 tests)
│           └── Main.java                               # Interactive Console CLI (Options 0-23)
├── .gitignore                                          # Excludes compiler build artifacts (bin/)
└── README.md
```

---

## 🚀 How to Compile and Run

### 1. Compile all Java source files
```powershell
javac -d bin (Get-ChildItem -Path src -Filter *.java -Recurse | Select-Object -ExpandProperty FullName)
```

### 2. Run Interactive CLI Application (Options 0-23)
```powershell
java -cp bin com.scholarship.Main
```

### 3. Run Automated Unit Test Suite (150 Tests)
```powershell
java -cp bin com.scholarship.test.TestSuite
```

### 4. Run Automated Walkthrough Demo Across All 4 Phases
```powershell
"10`n0" | java -cp bin com.scholarship.Main
```

---

## 🧪 Automated Unit Test Results
```
1. Testing StudentLinkedList Operations...                 (10 PASS)
2. Testing LinkedStack (LIFO) Operations...                (8 PASS)
3. Testing LinkedQueue (FIFO) Operations...                (9 PASS)
4. Testing Stack-based Expression Parser...                (5 PASS)
5. Testing ScholarshipService & Undo History...            (10 PASS)
6. Testing Binary Tree & Category Hierarchy...             (9 PASS)
7. Testing Student Binary Search Tree (BST)...             (25 PASS)
8. Testing Graph Adjacency List & Matrix...                (9 PASS)
9. Testing Verification Workflow Graph (BFS & DFS)...       (15 PASS)
10. Testing Searching Operations (Linear & Binary)...      (8 PASS)
11. Testing Sorting Operations (Merge, Quick, Insertion)...(12 PASS)
12. Testing Algorithm Complexity Benchmarking...           (6 PASS)
13. Testing Phase 4: Greedy Scholarship Allocation...      (8 PASS)
14. Testing Phase 4: DP Discrete Grant Optimization...     (5 PASS)
15. Testing Phase 4: Backtracking Committee Assignment...  (11 PASS)

--------------------------------------------------
Test Execution Summary: 150 PASSED, 0 FAILED
[SUCCESS] ALL TESTS COMPLETED SUCCESSFULLY! (CLO1, CLO2 & CLO3 Verified)
--------------------------------------------------
```
