package com.example.ainwood.bechdel;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by ainwo_000 on 7/13/2015.
 */
public class BechdelTask extends AsyncTask<String, Void, String> {
    private static final String baseString = "http://bechdeltest.com/api/v1/getMoviesByTitle?title=";
    private Context context;
    BechdelTask(Context context) {
        super();
        this.context = context;
    }
    @Override
    protected String doInBackground(String... query) {
        String queryString = query[0];
        final String bechdelString = baseString + queryString;
        String responseString = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
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
        return responseString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Intent intent = new Intent("bechdel");
        intent.putExtra("bechdel", s);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
