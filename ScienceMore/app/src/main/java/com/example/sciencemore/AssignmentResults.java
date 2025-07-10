package com.example.sciencemore;

public class AssignmentResults {
    private String Subject;
    private String Description;
    private double Marks;

    public AssignmentResults(String Subject, String Description, double Marks){
        this.Subject = Subject;
        this.Description = Description;
        this.Marks = Marks;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public double getMarks() {
        return Marks;
    }

    public void setMarks(double marks) {
        Marks = marks;
    }
}
