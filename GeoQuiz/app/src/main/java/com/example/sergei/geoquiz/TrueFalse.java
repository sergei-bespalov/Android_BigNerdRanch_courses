package com.example.sergei.geoquiz;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sergei on 4/15/2015.
 */
public class TrueFalse implements Parcelable{
    private int mQuestion;
    private boolean mTrueQuestion;
    private boolean mCheater;

    public void setCheater(boolean cheater) {
        mCheater = cheater;
    }

    public boolean isCheater() {

        return mCheater;
    }

    public TrueFalse(int question, boolean trueQuestion){
        mQuestion = question;
        mTrueQuestion = trueQuestion;
    }

    public TrueFalse(int question, boolean trueQuestion, boolean cheater){
        mQuestion = question;
        mTrueQuestion = trueQuestion;
        mCheater = cheater;
    }

    public void setQuestion(int question) {
        mQuestion = question;
    }

    public void setTrueQuestion(boolean trueQuestion) {
        mTrueQuestion = trueQuestion;
    }

    public int getQuestion() {

        return mQuestion;
    }

    public boolean isTrueQuestion() {
        return mTrueQuestion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mQuestion);
        parcel.writeByte((byte) (mTrueQuestion? 1 : 0));
        parcel.writeByte((byte) (mCheater? 1 : 0));
    }

    public static TrueFalse readFromParcel(Parcel in){
        int question = in.readInt();
        boolean trueQuestion = in.readByte() == 1? true : false;
        boolean cheater = in.readByte() == 1? true : false;
        return new TrueFalse(question,trueQuestion, cheater);
    }

    public static final Creator<TrueFalse> CREATOR = new Creator<TrueFalse>() {
        @Override
        public TrueFalse createFromParcel(Parcel parcel) {
            return readFromParcel(parcel);
        }

        @Override
        public TrueFalse[] newArray(int i) {
            return new TrueFalse[i];
        }
    };
}
