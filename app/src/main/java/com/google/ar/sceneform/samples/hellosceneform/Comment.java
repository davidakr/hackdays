package com.google.ar.sceneform.samples.hellosceneform;

public class Comment {
    private String text;
    private String author;
    private int rating;

    public Comment(String text, String author, int rating) {
        this.text = text;
        this.author = author;
        this.rating = rating;
    }

    public void increaseRating() {
        this.rating++;
    }
}
