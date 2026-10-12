package com.scholarship.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an audit trail entry pushed to the LIFO Action Stack for Undo operations.
 */
public class ActionRecord {
    private String actionType;      // "PROCESS_APPLICATION", "SUBMIT_APPLICATION", "DELETE_STUDENT", "UPDATE_STATUS", "ADD_DIRECT"
    private String studentId;
    private String description;
    private StudentRecord previousStudentState;
    private StudentRecord newStudentState;
    private String timestamp;

    public ActionRecord(String actionType, String studentId, String description,
                        StudentRecord previousStudentState, StudentRecord newStudentState) {
        this.actionType = actionType;
        this.studentId = studentId;
        this.description = description;
        this.previousStudentState = previousStudentState != null ? new StudentRecord(previousStudentState) : null;
        this.newStudentState = newStudentState != null ? new StudentRecord(newStudentState) : null;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getActionType() { return actionType; }
    public String getStudentId() { return studentId; }
    public String getDescription() { return description; }
    public StudentRecord getPreviousStudentState() { return previousStudentState; }
    public StudentRecord getNewStudentState() { return newStudentState; }
    public String getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("(%s) [%s] ID: %s - %s", timestamp, actionType, studentId, description);
    }
}
