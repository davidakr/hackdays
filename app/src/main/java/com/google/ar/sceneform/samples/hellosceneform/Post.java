package com.google.ar.sceneform.samples.hellosceneform;

import java.util.ArrayList;

public class Post {

    //declare private data instead of public to ensure the privacy of data field of each class
    private String title;
    private String author;
    private String question;
    private Integer vote;

    static ArrayList<Post> users = new ArrayList<Post>();
    static {
        users.add(new Post("Where to go out in NY?", "I am staying in Brooklin for the week and I am looking for stuff", "Alan", 0));
        users.add(new Post("Where to go out in LA?", "I am staying in Campton for the week and I am looking for stuff", "Alan", 0));
        users.add(new Post("Where to go out in Boston?", "I am staying in some place in Boston for the week and I am looking for stuff","Alan", 0));
    }

    public Post(String title, String question, String author, Integer vote)
    {
        this.title = title;
        this.question = question;
        this.vote = vote;
        this.author = author;
    }

    //retrieve user's name
    public String getTitle(){
        return title;
    }

    //retrieve users' hometown
    public String getQuestion(){
        return question;
    }

    public String getVote(){
        return vote.toString();
    }

    public String getAuthor() { return author; }

    public void upVote()
    {
        vote++;
    }

    // load from url - same signature
    public static ArrayList<Post> getPosts()
    {
        return users;
    }
}

