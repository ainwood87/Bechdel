package com.example.ainwood.bechdel;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ainwo_000 on 7/8/2015.
 */
public class MovieHolder extends RecyclerView.ViewHolder {
    public TextView scoreView;
    public TextView titleView;
    public ImageView posterView;
    public ImageView scorePic;

    public MovieHolder(View view) {
        super (view);
        scoreView = (TextView) view.findViewById(R.id.scoreView);
        titleView = (TextView) view.findViewById(R.id.titleView);
        posterView = (ImageView) view.findViewById(R.id.posterView);
        scorePic = (ImageView) view.findViewById(R.id.scorePic);
    }
}
