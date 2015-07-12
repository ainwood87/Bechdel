package com.example.ainwood.bechdel;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    private static final String queryString = "http://bechdeltest.com/api/v1/getMoviesByTitle?title=";
    private MovieViewAdapter adapter;
    RecyclerView recList;
    private Thread workThread;
    boolean requestKill = false;
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
                setupThread(bechdelString);
                workThread.start();
            } else {
                requestKill = true;
                final Handler myHandler = new Handler();

                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (workThread.isAlive()) {
                            myHandler.postDelayed(this, 100);
                        } else {
                            setupThread(bechdelString);
                            workThread.start();
                            requestKill = false;
                        }
                    }
                }, 100);
            }
        }
    }
    private void setupThread(final String bechdelString) {
        workThread = new Thread(new Runnable() {
            private String getPosterURL(String jsonString) {
                String posterURL = "";
                try {
                    JSONObject json = new JSONObject(jsonString);
                    posterURL = json.getString("Poster");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return posterURL;
            }
            private String getimdbResult(long imdbid) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;
                String responseString = null;

                String formatted = String.format("%07d", imdbid);
                String query = "http://www.omdbapi.com/?i=tt" + formatted + "&plot=short&r=json";
                System.out.println("IMDB QUERY " + query);
                try {
                    response = httpclient.execute(new HttpGet(query));
                    StatusLine statusLine = response.getStatusLine();
                    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        responseString = out.toString();
                        out.close();
                    } else{
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (ClientProtocolException e) {
                    //TODO Handle problems..
                } catch (IOException e) {
                    //TODO Handle problems..
                }
                return responseString;
            }
            private Bitmap getPoster(String urldisplay) {
                Bitmap mIcon11 = null;
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                return mIcon11;
            }
            @Override
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;
                String responseString = null;
                try {
                    response = httpclient.execute(new HttpGet(bechdelString));
                    StatusLine statusLine = response.getStatusLine();
                    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        responseString = out.toString();
                        out.close();
                    } else{
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (ClientProtocolException e) {
                    //TODO Handle problems..
                } catch (IOException e) {
                    //TODO Handle problems..
                }
                if (null == responseString) return;
                try {
                    //parse the JSON data
                    JSONArray json = new JSONArray(responseString);
//                    JSONObject json = new JSONObject(responseString);
                    for (int i = 0; i < json.length(); ++i) {
                        JSONObject jsonObject = json.getJSONObject(i);
                        MovieInfo info = new MovieInfo();
                        info.setTitle(jsonObject.getString("title"));
                        info.setScore(Integer.parseInt(jsonObject.getString("rating")));
                        info.setImdbid(Long.parseLong(jsonObject.getString("imdbid")));
                        //get poster
                        System.out.println("Got imdb response: " + getimdbResult(info.getImdbid()));
                        String posterURL = getPosterURL(getimdbResult(info.getImdbid()));
                        System.out.println("got posterurl: " + posterURL);
                        if (requestKill) return;
                        info.setPoster(getPoster(posterURL));
                        if (requestKill) return;

//                            movieInfoArrayList.add(info);
                        final MovieInfo info2 = new MovieInfo(info);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.addData(info2);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
