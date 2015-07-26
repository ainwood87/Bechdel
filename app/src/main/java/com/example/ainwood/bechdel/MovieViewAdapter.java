package com.example.ainwood.bechdel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
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
    private Context context;


    public MovieViewAdapter(Context context) {
        this.context = context;
        movieList = new ArrayList<MovieInfo>();
        LocalBroadcastManager.getInstance(context).registerReceiver(itemClick, new IntentFilter("itemClick"));

    }
    private BroadcastReceiver itemClick = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int index = intent.getExtras().getInt("index");
            long bechdelIndex = movieList.get(index).getId();
            Intent webIntent = new Intent("openPage");
            webIntent.putExtra("bechdelID", bechdelIndex);
            LocalBroadcastManager.getInstance(context).sendBroadcast(webIntent);
        }
    };
    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MovieHolder(context, v);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        MovieInfo info = movieList.get(position);
//        holder.textView.setText(info.getText());
        holder.scoreView.setText(Integer.toString(info.getScore()));
        holder.titleView.setText(info.getTitle());
        holder.posterView.setImageBitmap(info.getPoster());
        int res;
        switch (info.getScore()) {
            case 1:
                res = R.drawable.star1;
                break;
            case 2:
                res= R.drawable.star2;
                break;
            case 3:
                res = R.drawable.star3;
                break;
            default:
                res = R.drawable.star0;
        }
        holder.scorePic.setImageResource(res);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void addData(MovieInfo info) {
        movieList.add(info);
        notifyDataSetChanged();
    }

    public void setPoster(int index, Bitmap poster) {
        MovieInfo info = movieList.get(index);
        info.setPoster(poster);
        notifyDataSetChanged();
    }

    public void clearAllData() {
        movieList.clear();
        notifyDataSetChanged();
    }
}
