package com.example.ainwood.bechdel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
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
 * Created by ainwo_000 on 7/13/2015.
 */
public class PosterTask extends AsyncTask<Void, PosterWrapper, Void> {
    String bechdelJson;
    int start;
    int end;
    Context context;
    PosterTask(String bechdelJson, int start, int end, Context context) {
        this.bechdelJson = bechdelJson;
        this.start = start;
        this.end = end;
        this.context = context;
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
//        System.out.println("imdb response: " + responseString);
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
    protected Void doInBackground(Void... params) {
        try {
            //parse the JSON data
            JSONArray json = new JSONArray(bechdelJson);
            for (int i = start; i < json.length() && i <= end; ++i) {
                JSONObject jsonObject = json.getJSONObject(i);
                String imdbid = jsonObject.getString("imdbid");
                if (imdbid == null) continue;

                long imdbid_val = Long.parseLong(imdbid);
                if (isCancelled()) return null;
                String imdbresult = getimdbResult(imdbid_val);
                String posterUrl = getPosterURL(imdbresult);
                if (posterUrl == "null") continue;

                if (isCancelled()) return null;
                Bitmap poster = getPoster(posterUrl);
                if (isCancelled()) return null;
                int index = i - start;
                PosterWrapper wrapper = new PosterWrapper(poster, index);
                publishProgress(wrapper);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(PosterWrapper... values) {
        super.onProgressUpdate(values);
        Intent intent = new Intent("poster");
        intent.putExtra("poster", values[0].getBitmap());
        intent.putExtra("index", values[0].getIndex());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
