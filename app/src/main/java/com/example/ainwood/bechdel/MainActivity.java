package com.example.ainwood.bechdel;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ImageView;

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
//    private TextView resultView;
    private MovieViewAdapter adapter;
    RecyclerView recList;
    ArrayList<MovieInfo> movieInfoArrayList;
    class RequestTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
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
            if (responseString != null) {
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
                        movieInfoArrayList.add(info);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
            System.out.println("Got result: " + result);

            //insert MovieInfo into list
            for (MovieInfo info : movieInfoArrayList) {
                //TODO request OMDB info for each movie
                new RequestOMDB().execute(info.getImdbid());
                //TODO Download the bitmap from OMDB output and insert into View
                adapter.addData(info);
            }
        }
    }

    //TODO implement RequestOMDB. Mainly looking for poster URL
    private class RequestOMDB extends AsyncTask<Long, String, String> {
        @Override
        protected String doInBackground(Long... imdbid) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
//            StringBuilder sb = new StringBuilder();
//            sb.append("http://www.omdbapi.com/?i=tt");
//            sb.append(imdbid[0]);
//            sb.append("&plot=short&r=json");
//            String query = sb.toString();
            String query = "http://www.omdbapi.com/?i=tt" + imdbid[0].toString() + "&plot=short&r=json";
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
        @Override
        protected void onPostExecute(String result) {
            System.out.println("got imdb response: " + result);
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
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

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieInfoArrayList = new ArrayList<MovieInfo>();
//        setContentView(R.layouut.activity_main);
        handleIntent(getIntent());
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_main, null);
//        resultView = (TextView) view.findViewById(R.id.resultView);
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
            new RequestTask().execute(queryString + str);
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
