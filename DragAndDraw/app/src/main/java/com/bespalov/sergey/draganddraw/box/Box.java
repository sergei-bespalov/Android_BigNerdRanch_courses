package com.bespalov.sergey.draganddraw.box;

import android.graphics.PointF;

public class Box {
    private PointF mOrigin;
    private PointF mCurrent;


    public PointF getCurrent() {
        return mCurrent;
    }

    public Box(PointF origin) {
        mOrigin = mCurrent = origin;
    }

    public void setCurrent(PointF current){
        mCurrent = current;
    }

    public PointF getOrigin(){
        return mOrigin;
    }
}
