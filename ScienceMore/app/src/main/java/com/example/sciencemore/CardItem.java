package com.example.sciencemore;

public class CardItem {
    private String link;
    private String description;

    public CardItem(String link, String description) {
        this.link = link;
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
