package com.google.ar.sceneform.samples.hellosceneform;

import java.util.ArrayList;

public class Post {

    //declare private data instead of public to ensure the privacy of data field of each class
    private String title;
    private String author;
    private Integer vote;
    private String id;

    static ArrayList<Post> users = new ArrayList<Post>();

    private ArrayList<Comment> comments;

    public Post(String title, ArrayList<Comment> comments, String author, Integer vote, String id) {
        this.title = title;
        this.vote = vote;
        this.author = author;
        this.comments = comments;
        this.id = id;
    }

    public Post(String title, String author, Integer vote, String id) {
        this.title = title;
        this.vote = vote;
        this.author = author;
        this.comments = new ArrayList<>();
        this.id = id;
    }

    //retrieve user's name
    public String getTitle(){
        return title;
    }

    //retrieve users' hometown

    public String getVote(){
        return vote.toString();
    }

    public String getAuthor() { return author; }

    public void upVote()
    {
        vote++;
    }

    public String getId() { return id; }

    // load from url - same signature
    public static ArrayList<Post> getPosts()
    {
        return users;
    }

    public static void setPosts(ArrayList<Post> p) {
        users = p;
    }

    public static void addPost(Post post) { users.add(post); }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }
}

