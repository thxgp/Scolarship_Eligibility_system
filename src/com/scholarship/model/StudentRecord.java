package com.scholarship.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a student scholarship application record.
 */
public class StudentRecord {
    private String studentId;
    private String name;
    private double gpa;                    // 0.0 to 4.0
    private double familyIncome;           // Annual family income
    private double attendancePercentage;   // 0.0 to 100.0
    private double extracurricularScore;   // 0.0 to 100.0
    private String category;               // "Merit-Based", "Need-Based", "Sports/ECA"
    private String status;                 // "Pending", "Under Review", "Approved", "Rejected"
    private double meritScore;             // Computed composite merit score
    private String createdAt;

    public StudentRecord(String studentId, String name, double gpa, double familyIncome,
                         double attendancePercentage, double extracurricularScore,
                         String category, String status) {
        this.studentId = studentId;
        this.name = name;
        this.gpa = gpa;
        this.familyIncome = familyIncome;
        this.attendancePercentage = attendancePercentage;
        this.extracurricularScore = extracurricularScore;
        this.category = (category != null && !category.isEmpty()) ? category : "Merit-Based";
        this.status = (status != null && !status.isEmpty()) ? status : "Pending";
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.meritScore = computeDefaultMeritScore();
    }

    public StudentRecord(String studentId, String name, double gpa, double familyIncome,
                         double attendancePercentage, double extracurricularScore,
                         String category, String status, double meritScore, String createdAt) {
        this.studentId = studentId;
        this.name = name;
        this.gpa = gpa;
        this.familyIncome = familyIncome;
        this.attendancePercentage = attendancePercentage;
        this.extracurricularScore = extracurricularScore;
        this.category = category;
        this.status = status;
        this.meritScore = meritScore;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Copy constructor for undo/redo state preservation.
     */
    public StudentRecord(StudentRecord other) {
        this.studentId = other.studentId;
        this.name = other.name;
        this.gpa = other.gpa;
        this.familyIncome = other.familyIncome;
        this.attendancePercentage = other.attendancePercentage;
        this.extracurricularScore = other.extracurricularScore;
        this.category = other.category;
        this.status = other.status;
        this.meritScore = other.meritScore;
        this.createdAt = other.createdAt;
    }

    /**
     * Computes normalized default composite merit score (out of 100 on a 10.0 CGPA scale).
     */
    public double computeDefaultMeritScore() {
        double normalizedGpa = Math.min(Math.max(gpa / 10.0, 0.0), 1.0) * 100.0;
        double score = (normalizedGpa * 0.50) + (extracurricularScore * 0.30) + (attendancePercentage * 0.20);
        return Math.round(score * 100.0) / 100.0;
    }

    public static class EligibilityResult {
        public final boolean isEligible;
        public final String reason;

        public EligibilityResult(boolean isEligible, String reason) {
            this.isEligible = isEligible;
            this.reason = reason;
        }
    }

    /**
     * Validates eligibility according to category criteria (10.0 CGPA Scale).
     */
    public EligibilityResult checkCategoryEligibility(double minGpa, double maxIncome, double minAttendance) {
        if (this.attendancePercentage < minAttendance) {
            return new EligibilityResult(false, String.format("Attendance (%.1f%%) below required %.1f%%", this.attendancePercentage, minAttendance));
        }

        switch (this.category) {
            case "Need-Based":
                if (this.familyIncome > maxIncome) {
                    return new EligibilityResult(false, String.format("Income (Rs. %,.2f) exceeds threshold (Rs. %,.2f)", this.familyIncome, maxIncome));
                }
                if (this.gpa < 6.0) {
                    return new EligibilityResult(false, String.format("CGPA (%.2f) below minimum need-based requirement of 6.00/10.0", this.gpa));
                }
                return new EligibilityResult(true, "Eligible for Need-Based Scholarship");

            case "Merit-Based":
                if (this.gpa < minGpa) {
                    return new EligibilityResult(false, String.format("CGPA (%.2f) below merit cutoff of %.2f/10.0", this.gpa, minGpa));
                }
                if (this.meritScore < 75.0) {
                    return new EligibilityResult(false, String.format("Composite merit score (%.2f) below cutoff 75.0", this.meritScore));
                }
                return new EligibilityResult(true, "Eligible for Merit-Based Scholarship");

            case "Sports/ECA":
                if (this.extracurricularScore < 70.0) {
                    return new EligibilityResult(false, String.format("ECA Score (%.1f) below required 70.0", this.extracurricularScore));
                }
                if (this.gpa < 5.0) {
                    return new EligibilityResult(false, String.format("CGPA (%.2f) below minimum requirement of 5.00/10.0", this.gpa));
                }
                return new EligibilityResult(true, "Eligible for Sports/ECA Scholarship");

            default:
                return new EligibilityResult(false, "Unrecognized scholarship category: " + this.category);
        }
    }

    public EligibilityResult checkCategoryEligibility() {
        return checkCategoryEligibility(7.5, 600000.0, 75.0); // 7.5 CGPA minimum for Merit-Based Scholarship
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) {
        this.gpa = gpa;
        this.meritScore = computeDefaultMeritScore();
    }

    public double getFamilyIncome() { return familyIncome; }
    public void setFamilyIncome(double familyIncome) { this.familyIncome = familyIncome; }

    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
        this.meritScore = computeDefaultMeritScore();
    }

    public double getExtracurricularScore() { return extracurricularScore; }
    public void setExtracurricularScore(double extracurricularScore) {
        this.extracurricularScore = extracurricularScore;
        this.meritScore = computeDefaultMeritScore();
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getMeritScore() { return meritScore; }
    public void setMeritScore(double meritScore) { this.meritScore = meritScore; }

    public String getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("[%s] %s | CGPA: %.2f | Income: Rs. %,.0f | Attend: %.1f%% | ECA: %.1f | Cat: %s | Status: %s | Merit: %.2f",
                studentId, name, gpa, familyIncome, attendancePercentage, extracurricularScore, category, status, meritScore);
    }
}
