package com.example.ainwood.bechdel;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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

/**
 * Created by ainwo_000 on 7/11/2015.
 */
public class QueryThread extends Thread {
    private String bechdelString;
    private Activity activity;
    MovieViewAdapter adapter;
    public boolean isRequestKill() {
        return requestKill;
    }

    public void setRequestKill(boolean requestKill) {
        this.requestKill = requestKill;
    }

    private boolean requestKill;

    QueryThread(Activity activity, MovieViewAdapter adapter, String bechdelString) {
        this.bechdelString = bechdelString;
        this.adapter = adapter;
        this.activity = activity;
        requestKill = false;
    }

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

    private String getYear(String jsonString) {
        String year = "";
        try {
            JSONObject json = new JSONObject(jsonString);
            year = json.getString("Year");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return year;
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
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            } else {
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
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            } else {
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
            System.out.println("got response: " + responseString);
            //parse the JSON data
            JSONArray json = new JSONArray(responseString);
//                    JSONObject json = new JSONObject(responseString);
            for (int i = 0; i < json.length(); ++i) {
                JSONObject jsonObject = json.getJSONObject(i);
                MovieInfo info = new MovieInfo();
                System.out.println("Going to set stuff: ");
                info.setTitle(jsonObject.getString("title"));
                System.out.println("Title: " + info.getTitle());
                String score = jsonObject.getString("rating");
                String imdbid = jsonObject.getString("imdbid");
                if (score != "null") {
                    info.setScore(Integer.parseInt(score));
                }
                if (imdbid != "null") {
                    info.setImdbid(Long.parseLong(imdbid));
                }
                //get poster
                String imdbResult = getimdbResult(info.getImdbid());
                info.setTitle(info.getTitle() + " (" + getYear(imdbResult) + ")");
                System.out.println("Got imdb response: " + imdbResult);
                String posterURL = getPosterURL(imdbResult);
                System.out.println("got posterurl: " + posterURL);
                if (requestKill) return;
                info.setPoster(getPoster(posterURL));
                if (requestKill) return;

//                            movieInfoArrayList.add(info);
                final MovieInfo info2 = new MovieInfo(info);
                activity.runOnUiThread(new Runnable() {
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
}
