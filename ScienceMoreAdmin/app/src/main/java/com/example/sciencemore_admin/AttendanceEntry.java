package com.example.sciencemore_admin;

public class AttendanceEntry {
    private String date;
    private String studentName;
    private String subject;

    public AttendanceEntry(String date, String studentName, String subject) {
        this.date = date;
        this.studentName = studentName;
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}