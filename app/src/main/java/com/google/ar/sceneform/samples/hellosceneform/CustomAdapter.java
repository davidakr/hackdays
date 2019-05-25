package com.google.ar.sceneform.samples.hellosceneform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Post> {
    public CustomAdapter(Context context, ArrayList<Post> posts) {
        super(context, 0, posts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_post, parent, false);
        }

        // Get the data item for this position
        Post post = getItem(position);

        // Lookup view for data population
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        // TextView tvQuestion = (TextView) convertView.findViewById(R.id.tvQuestion);
        TextView tvVote = (TextView) convertView.findViewById(R.id.tvVote);
        // Populate the data into the template view using the data object
        tvTitle.setText(post.getTitle());
        // tvQuestion.setText(post.getQuestion());
        tvVote.setText(post.getVote());
        // Return the completed view to render on screen
        return convertView;
    }
}