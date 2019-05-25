package com.google.ar.sceneform.samples.hellosceneform;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailedView extends AppCompatActivity
{

    private static Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        ArrayList<Post> posts = Post.getPosts();
        Intent myIntent = getIntent();
        Integer position = myIntent.getIntExtra("position", 0);

        post = posts.get(position);
        TextView textViewTitle = (TextView) findViewById(R.id.textView_title);
        TextView textViewQuestion = (TextView) findViewById(R.id.textView_question);
        TextView textViewVote = (TextView) findViewById(R.id.textView_vote);
        TextView textViewAuthor = (TextView) findViewById(R.id.textView_author);

        textViewTitle.setText(post.getTitle());
        textViewQuestion.setText(post.getQuestion());
        textViewVote.setText(post.getVote());
        String author_text = "Posted by " + post.getAuthor();
        textViewAuthor.setText(author_text);
    }

    public void upVote(View view)
    {
        post.upVote();
        TextView textViewVote = (TextView) findViewById(R.id.textView_vote);
        textViewVote.setText(post.getVote());
    }
}