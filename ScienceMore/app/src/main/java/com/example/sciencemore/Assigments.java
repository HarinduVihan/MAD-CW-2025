package com.example.sciencemore;

public class Assigments {
    private String Link;
    private String Name;
    private String assignmentName;

    public Assigments (String Link , String Name, String assignmentName){
        this.Link = Link;
        this.Name = Name;
        this.assignmentName = assignmentName;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
