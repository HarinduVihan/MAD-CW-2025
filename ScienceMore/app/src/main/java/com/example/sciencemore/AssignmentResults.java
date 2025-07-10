package com.example.sciencemore;

public class AssignmentResults {
    private String Subject;
    private String Description;
    private String Marks;

    public AssignmentResults(String Subject, String Description, String Marks){
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

    public String getMarks() {
        return Marks;
    }

    public void setMarks(String marks) {
        Marks = marks;
    }
}
