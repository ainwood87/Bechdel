package com.example.ainwood.bechdel;

import android.graphics.Bitmap;

/**
 * Created by ainwo_000 on 7/13/2015.
 */
public class PosterWrapper {
    private Bitmap bitmap;
    private int index;
    public PosterWrapper(Bitmap bitmap, int index) {
        this.index = index;
        this.bitmap = bitmap;
    }
    public Bitmap getBitmap() {return bitmap;}
    public int getIndex() { return index; }
}
