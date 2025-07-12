package com.example.sciencemore_admin;

public class AssignmentEntry {
    private String assignmentName;
    private String result;
    private String studentName;

    public AssignmentEntry(String assignmentName, String result, String studentName) {
        this.assignmentName = assignmentName;
        this.result = result;
        this.studentName = studentName;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
