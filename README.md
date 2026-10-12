# Student Scholarship Eligibility and Merit System (Phase 1 - Java)
**Course**: 26MCC1L211: Data Structures and Algorithms Practical  
**Topic**: Linear Data Structures Implementation (CLO1)

---

## 📌 Project Overview
The **Student Scholarship Eligibility and Merit System** is implemented in **Java 21** using pure, node-based **Linear Data Structures** without high-level Java collection wrappers (e.g. no `java.util.LinkedList` or `java.util.Stack`).

### Phase 1 Core Implementations:
1. **`StudentRecord` & `ActionRecord`** (`com.scholarship.model`): Input schema, category eligibility rules, and state preservation.
2. **`StudentLinkedList`** (`com.scholarship.datastructures`): Custom Singly Linked List with head/tail insert, sorted insert by merit score, delete by ID, search by ID/name, and tabular traversal.
3. **`LinkedQueue<T>`** (`com.scholarship.datastructures`): Pure FIFO queue with front and rear pointers ($O(1)$ enqueue & dequeue) for the student application verification pipeline.
4. **`LinkedStack<T>`** (`com.scholarship.datastructures`): Pure LIFO stack with top pointer ($O(1)$ push & pop) for multi-level evaluator Undo history.
5. **`ExpressionParser`** (`com.scholarship.expression`): Stack-based Delimiter matching, Infix-to-Postfix (Shunting-Yard), and Postfix evaluation with dynamic variable substitution for merit scoring:
   $$\text{Merit Score} = \left(\frac{\text{gpa}}{4.0} \times 50.0\right) + (\text{eca} \times 0.30) + (\text{attendance} \times 0.20)$$
6. **`ScholarshipService` & `Main`** (`com.scholarship`): Unified coordinator and interactive CLI dashboard with quick demo mode.

---

## 📁 Directory Structure

```
scholarship_system_phase1_java/
├── src/
│   └── com/
│       └── scholarship/
│           ├── model/
│           │   ├── StudentRecord.java       # Student schema & validation
│           │   └── ActionRecord.java        # Audit record for Undo stack
│           ├── datastructures/
│           │   ├── ListNode.java            # Node for Linked List
│           │   ├── StudentLinkedList.java   # Custom Singly Linked List
│           │   ├── StackNode.java           # Node for Linked Stack
│           │   ├── LinkedStack.java         # Custom LIFO Stack
│           │   ├── QueueNode.java           # Node for Linked Queue
│           │   └── LinkedQueue.java         # Custom FIFO Queue
│           ├── expression/
│           │   └── ExpressionParser.java    # Stack-based expression parser
│           ├── service/
│           │   └── ScholarshipService.java  # Coordinator service
│           ├── test/
│           │   └── TestSuite.java           # Automated test suite
│           └── Main.java                    # Interactive Console Menu
└── README.md
```

---

## 🚀 How to Compile and Run

### 1. Compile all Java source files
```powershell
javac -d bin (Get-ChildItem -Path src -Filter *.java -Recurse | Select-Object -ExpandProperty FullName)
```

### 2. Run Interactive CLI Application
```powershell
java -cp bin com.scholarship.Main
```

### 3. Run Automated Unit Tests
```powershell
java -cp bin com.scholarship.test.TestSuite
```
