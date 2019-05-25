package com.google.ar.sceneform.samples.hellosceneform;

import android.content.Intent;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView

public class ForumOverviewActivity extends AppCompatActivity
{

    private ListView lv;
    private CustomAdapter adapter;
    private ArrayList<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        posts = Post.getPosts();
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

        new GetAsyncTask().execute("https://lhfl.herokuapp.com/threads");
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
                // perform query here
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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



    private class GetAsyncTask extends AsyncTask<String, Void, ArrayList<Post>> {

        private String readStream(InputStream is) {
            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = is.read();
                while (i != -1) {
                    bo.write(i);
                    i = is.read();
                }
                return bo.toString();
            } catch (IOException e) {
                return "";
            }
        }

        @Override
        protected ArrayList<Post> doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            ArrayList<Post> posts = new ArrayList<Post>();

            try {
                urlConnection = (HttpURLConnection) new URL(urls[0]).openConnection();
                InputStream in = null;
                if (urlConnection != null) {
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    String s = readStream(in);
                    JSONArray jsonData = new JSONArray(s);

                    // fill JSON data into ArrayList
                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject x = jsonData.getJSONObject(i);
                        String title = String.valueOf(x.get(x.names().getString(1)));
                        String question = String.valueOf(x.get(x.names().getString(1)));
                        ArrayList<Comment> comments = new ArrayList<>();
                        String commentString = String.valueOf(x.get(x.names().getString(4)));
                        String[] coms = commentString.split("\\],\\[");
                        for(String com : coms) {
                            com = com.replace("\"", "");
                            com = com.replace("[", "");
                            com = com.replace("]", "");
                            String[] co = com.split(",");
                            comments.add(new Comment(co[0], co[2], Integer.parseInt(co[1])));
                        }
                        String author = String.valueOf(x.get(x.names().getString(2)));
                        Integer rating = Integer.parseInt(String.valueOf(x.get(x.names().getString(3))));
                        posts.add(new Post(title, question, comments, author, rating));
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return posts;
        }

        protected void onPostExecute(ArrayList<Post> value) {
            posts.clear();
            posts.addAll(value);
            adapter.notifyDataSetChanged();
            lv.setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }
}



