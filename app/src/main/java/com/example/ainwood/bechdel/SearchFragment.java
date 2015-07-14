package com.example.ainwood.bechdel;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by ainwo_000 on 7/12/2015.
 */
public class SearchFragment extends Fragment {
    private MovieViewAdapter adapter;
    RecyclerView recList;
    private BechdelTask bechdelTask;
    private PosterTask posterTask;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, null);
        recList = (RecyclerView) view.findViewById(R.id.cardList);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        if (adapter == null) {
            adapter = new MovieViewAdapter();
        }
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onBechdel, new IntentFilter("bechdel"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onPoster, new IntentFilter("poster"));
        recList.setAdapter(adapter);
        return view;
    }

    private BroadcastReceiver onPoster = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            int index = extras.getInt("index");
            Bitmap bitmap = (Bitmap) extras.getParcelable("poster");
            adapter.setPoster(index, bitmap);
        }
    };
    private BroadcastReceiver onBechdel = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String responseString = extras.getString("bechdel");
            int size = 0;
            try {
                System.out.println("got response: " + responseString);
                //parse the JSON data
                JSONArray json = new JSONArray(responseString);
                size = json.length();
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
                    adapter.addData(info);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            posterTask = new PosterTask(responseString, 0, size - 1, getActivity());
            posterTask.execute();
        }
    };
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);
    }

    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            String str = "";
            try {
                str = URLEncoder.encode(query, "utf-8");
            } catch (Exception e) {

            }
            //clear task if we started it already
            if (bechdelTask != null) {
                bechdelTask.cancel(true);
            }
            if (posterTask != null) {
                posterTask.cancel(true);
            }
            adapter.clearAllData();
            bechdelTask = new BechdelTask(getActivity());
            bechdelTask.execute(str);
        }
    }
}
