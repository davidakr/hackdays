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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

// https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView

public class ForumOverviewActivity extends AppCompatActivity
{

    private ListView lv;
    private CustomAdapter adapter;
    private static String queryResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ArrayList<Post> posts = Post.getPosts();
        adapter = new CustomAdapter(this, posts);


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
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                queryResult = query;
                if(query.equals("Empire State Building")){
                    adapter.notifyDataSetChanged();
                }
                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                queryResult = "";
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "msg msg", Toast.LENGTH_SHORT).show();
                return true;//Should work.
            }
        });

        FloatingActionButton addQuestion = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        addQuestion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                new DialogOut().show(getSupportFragmentManager(),"tag");
            }
        });


        searchItem.expandActionView();
        searchView.setQuery("Empire State Building", true);
        return super.onCreateOptionsMenu(menu);
    }

    public static String returnQuery() {
        return queryResult;
    }
};



