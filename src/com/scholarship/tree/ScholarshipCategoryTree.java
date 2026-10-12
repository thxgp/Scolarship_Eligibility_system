package com.scholarship.tree;

import com.scholarship.model.StudentRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Hierarchical Binary Tree for structured scholarship scheme categories and quota allocation.
 * Fulfills CLO2:
 * 1. Binary Tree structured hierarchical storage
 * 2. Tree Traversals (In-Order, Pre-Order, Post-Order)
 * 3. Categorical eligibility evaluation
 */
public class ScholarshipCategoryTree {

    public static class CategoryData {
        private final String code;
        private final String name;
        private final double minGpa;
        private final double maxIncome;
        private final double minEca;
        private final double grantAmount;
        private final String description;

        public CategoryData(String code, String name, double minGpa, double maxIncome,
                            double minEca, double grantAmount, String description) {
            this.code = code;
            this.name = name;
            this.minGpa = minGpa;
            this.maxIncome = maxIncome;
            this.minEca = minEca;
            this.grantAmount = grantAmount;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getName() { return name; }
        public double getMinGpa() { return minGpa; }
        public double getMaxIncome() { return maxIncome; }
        public double getMinEca() { return minEca; }
        public double getGrantAmount() { return grantAmount; }
        public String getDescription() { return description; }

        public boolean isEligible(StudentRecord student) {
            if (student == null) return false;
            boolean gpaOk = student.getGpa() >= minGpa;
            boolean incomeOk = maxIncome <= 0 || student.getFamilyIncome() <= maxIncome;
            boolean ecaOk = minEca <= 0 || student.getExtracurricularScore() >= minEca;
            return gpaOk && incomeOk && ecaOk;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s (Grant: ₹%,.0f)", code, name, grantAmount);
        }
    }

    private final BinaryTree<CategoryData> tree;

    public ScholarshipCategoryTree() {
        this.tree = new BinaryTree<>();
        buildDefaultHierarchy();
    }

    /**
     * Builds the institutional category binary tree:
     *                      [ROOT] University Scholarship Directorate
     *                               /                        \
     *           [ACAD] Academic & Merit Council        [WELFARE] Need & Inclusion Board
     *                   /              \                      /                  \
     *      [STEM] STEM Honors    [DEAN] Dean's List   [NEED] Income Grant   [SPORTS] Sports Talent
     */
    private void buildDefaultHierarchy() {
        // Level 3 - Leaves (Specific scholarship schemes)
        BinaryTreeNode<CategoryData> stemNode = new BinaryTreeNode<>(
                new CategoryData("STEM-01", "Presidential STEM Excellence", 3.8, 0, 0, 75000,
                        "High academic merit in STEM disciplines (GPA >= 3.8)")
        );

        BinaryTreeNode<CategoryData> deansNode = new BinaryTreeNode<>(
                new CategoryData("DEAN-02", "Dean's Merit Scholarship", 3.5, 0, 0, 45000,
                        "Distinguished semester academic performance (GPA >= 3.5)")
        );

        BinaryTreeNode<CategoryData> needNode = new BinaryTreeNode<>(
                new CategoryData("NEED-03", "Socio-Economic Need Grant", 2.5, 300000, 0, 50000,
                        "Income <= ₹300,000 and minimum GPA >= 2.5")
        );

        BinaryTreeNode<CategoryData> sportsNode = new BinaryTreeNode<>(
                new CategoryData("SPRT-04", "Athletic & Cultural Excellence", 2.0, 0, 80, 40000,
                        "Extracurricular/Sports rating >= 80 and GPA >= 2.0")
        );

        // Level 2 - Intermediate divisions
        BinaryTreeNode<CategoryData> academicDiv = new BinaryTreeNode<>(
                new CategoryData("DIV-ACAD", "Academic & Merit Division", 3.5, 0, 0, 0,
                        "Governs all merit-based scholarships"),
                stemNode,
                deansNode
        );

        BinaryTreeNode<CategoryData> welfareDiv = new BinaryTreeNode<>(
                new CategoryData("DIV-WELF", "Student Welfare & Opportunity Board", 2.0, 0, 0, 0,
                        "Governs need-based, regional, and extracurricular awards"),
                needNode,
                sportsNode
        );

        // Level 1 - Root
        BinaryTreeNode<CategoryData> rootNode = new BinaryTreeNode<>(
                new CategoryData("ROOT-HQ", "University Central Scholarship Council", 0, 0, 0, 0,
                        "Apex governing council for institutional scholarship disbursement"),
                academicDiv,
                welfareDiv
        );

        tree.setRoot(rootNode);
    }

    public BinaryTree<CategoryData> getTree() {
        return tree;
    }

    public List<CategoryData> getPreorderTravesal() {
        return tree.preorder();
    }

    public List<CategoryData> getInorderTraversal() {
        return tree.inorder();
    }

    public List<CategoryData> getPostorderTraversal() {
        return tree.postorder();
    }

    public List<CategoryData> getLevelOrderTraversal() {
        return tree.levelOrder();
    }

    /**
     * Finds matching eligible scholarship categories for a student.
     */
    public List<CategoryData> findEligibleCategories(StudentRecord student) {
        List<CategoryData> eligible = new ArrayList<>();
        findEligibleHelper(tree.getRoot(), student, eligible);
        return eligible;
    }

    private void findEligibleHelper(BinaryTreeNode<CategoryData> node, StudentRecord student, List<CategoryData> result) {
        if (node == null) return;
        // Check leaves (actual award categories have grant amounts > 0)
        if (node.left == null && node.right == null) {
            if (node.data.isEligible(student)) {
                result.add(node.data);
            }
        }
        findEligibleHelper(node.left, student, result);
        findEligibleHelper(node.right, student, result);
    }

    /**
     * Search category by code.
     */
    public CategoryData findByCode(String code) {
        return findByCodeHelper(tree.getRoot(), code);
    }

    private CategoryData findByCodeHelper(BinaryTreeNode<CategoryData> node, String code) {
        if (node == null) return null;
        if (node.data.getCode().equalsIgnoreCase(code)) {
            return node.data;
        }
        CategoryData leftMatch = findByCodeHelper(node.left, code);
        if (leftMatch != null) return leftMatch;
        return findByCodeHelper(node.right, code);
    }

    public void displayHierarchy() {
        System.out.println("  ==========================================================================");
        System.out.println("            INSTITUTIONAL SCHOLARSHIP CATEGORY BINARY TREE");
        System.out.println("  ==========================================================================");
        tree.printTree();
        System.out.println("  ==========================================================================");
    }
}
