package com.example.ainwood.bechdel;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ainwo_000 on 7/8/2015.
 */
public class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView scoreView;
    public TextView titleView;
    public ImageView posterView;
    public ImageView scorePic;
    private Context context;
    public MovieHolder(Context context, View view) {
        super (view);
        this.context = context;
        view.setClickable(true);
        view.setOnClickListener(this);
        scoreView = (TextView) view.findViewById(R.id.scoreView);
        titleView = (TextView) view.findViewById(R.id.titleView);
        posterView = (ImageView) view.findViewById(R.id.posterView);
        scorePic = (ImageView) view.findViewById(R.id.scorePic);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent("itemClick");
        intent.putExtra("index", getPosition());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
