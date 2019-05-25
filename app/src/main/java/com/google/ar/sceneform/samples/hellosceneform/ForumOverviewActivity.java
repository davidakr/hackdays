package com.google.ar.sceneform.samples.hellosceneform;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

// https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView

public class ForumOverviewActivity extends AppCompatActivity
{

    private ListView lv;
    private CustomAdapter adapter;
    private String modelTitle;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ArrayList<Post> posts = Post.getPosts();
        adapter = new CustomAdapter(this, posts);
        intent = getIntent();

        if (intent.hasExtra("MODEL_TITLE")) modelTitle = intent.getExtras().getString("MODEL_TITLE");


        lv = (ListView) findViewById(R.id.myListView);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        Intent myIntent = new Intent(view.getContext(), DetailedView.class);
                        myIntent.putExtra("position", position);
                        startActivityForResult(myIntent, 0);
                    }
                });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        lv.invalidateViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {

            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // perform query here
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }
        });

        FloatingActionButton addQuestion = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        addQuestion.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                new DialogOut().show(getSupportFragmentManager(), "tag");
            }
        });

        if (intent.hasExtra("MODEL_TITLE"))
        {
            searchItem.expandActionView();
            searchView.setQuery(modelTitle, true);
        }

        return super.onCreateOptionsMenu(menu);
    }
};



