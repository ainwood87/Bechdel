package com.example.ainwood.bechdel;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by ainwo_000 on 7/8/2015.
 */
public class MovieViewAdapter extends RecyclerView.Adapter<MovieHolder> {
    private ArrayList<MovieInfo> movieList;

    public MovieViewAdapter(ArrayList<MovieInfo> list) {
        movieList = list;
    }
    public MovieViewAdapter() {
        movieList = new ArrayList<MovieInfo>();
    }
    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MovieHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        MovieInfo info = movieList.get(position);
//        holder.textView.setText(info.getText());
        holder.scoreView.setText(Integer.toString(info.getScore()));
        holder.titleView.setText(info.getTitle());
        holder.posterView.setImageBitmap(info.getPoster());
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void addData(MovieInfo info) {
        movieList.add(info);
        notifyDataSetChanged();
    }
}
