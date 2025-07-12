package com.example.sciencemore;

public class AssignmentResultHelp {
    String assignmentId;
    String result;

    public AssignmentResultHelp(String assignmentId, String result) {
        this.assignmentId = assignmentId;
        this.result = result;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}