package com.bespalov.sergey.draganddraw.box;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

public class Box implements Parcelable{
    private PointF mOrigin;

    public void setOrigin(PointF origin) {
        mOrigin = origin;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mOrigin,0);
        dest.writeParcelable(mCurrent,0);
    }

    public static final Parcelable.Creator<Box> CREATOR = new Creator<Box>() {
        @Override
        public Box createFromParcel(Parcel source) {
            return new Box(source);
        }

        @Override
        public Box[] newArray(int size) {
            return new Box[size];
        }
    };

    public Box(Parcel in){
        mOrigin = in.readParcelable(null);
        mCurrent = in.readParcelable(null);
    }
}
