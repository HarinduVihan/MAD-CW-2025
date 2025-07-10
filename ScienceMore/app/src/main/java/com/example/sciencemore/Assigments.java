package com.example.sciencemore;

public class Assigments {
    private String Link;
    private String Name;

    public Assigments (String Link , String Name){
        this.Link = Link;
        this.Name = Name;
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
