package com.example.ainwood.bechdel;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    private static final String queryString = "http://bechdeltest.com/api/v1/getMoviesByTitle?title=";
    private MovieViewAdapter adapter;
    RecyclerView recList;
    private QueryThread workThread;
    boolean requestKill = false;
    private final Activity activity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layouut.activity_main);
        handleIntent(getIntent());
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_main, null);
        recList = (RecyclerView) view.findViewById(R.id.cardList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        adapter = new MovieViewAdapter();
        recList.setAdapter(adapter);
        setContentView(view);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            String str = "";
            try {
                str = URLEncoder.encode(query, "utf-8");
            } catch (Exception e) {

            }
            adapter.clearAllData();
            final String bechdelString = queryString + str;
            ArrayList<MovieInfo> movieInfoArrayList = new ArrayList<MovieInfo>();
            if (workThread == null) {
                workThread = new QueryThread(this, adapter, bechdelString);
                workThread.start();
            } else {
                workThread.setRequestKill(true);
                final Handler myHandler = new Handler();

                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (workThread.isAlive()) {
                            myHandler.postDelayed(this, 100);
                        } else {
                            workThread = new QueryThread(activity, adapter, bechdelString);
                            workThread.start();
                        }
                    }
                }, 100);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        ComponentName name = getComponentName();
        SearchableInfo info = searchManager.getSearchableInfo(name);
        searchView.setSearchableInfo(info);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
