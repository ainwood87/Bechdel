package com.example.ainwood.bechdel;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by ainwo_000 on 7/12/2015.
 */
public class SearchFragment extends Fragment {
    private static final String queryString = "http://bechdeltest.com/api/v1/getMoviesByTitle?title=";
    private MovieViewAdapter adapter;
    RecyclerView recList;
    private QueryThread workThread;
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
        recList.setAdapter(adapter);
        return view;
    }
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
            adapter.clearAllData();
            final String bechdelString = queryString + str;
            ArrayList<MovieInfo> movieInfoArrayList = new ArrayList<MovieInfo>();
            if (workThread == null) {
                workThread = new QueryThread(getActivity(), adapter, bechdelString, null, 3, 0);
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
                            workThread = new QueryThread(getActivity(), adapter, bechdelString, null, 3, 0);
                            workThread.start();
                        }
                    }
                }, 100);
            }
        }
    }
}
