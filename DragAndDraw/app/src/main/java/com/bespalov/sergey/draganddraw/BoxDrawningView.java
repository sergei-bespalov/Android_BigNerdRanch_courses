package com.bespalov.sergey.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.bespalov.sergey.draganddraw.box.Box;

import java.util.ArrayList;


public class BoxDrawningView extends View {
    public static final String TAG = "BoxDrawingView";
    private static final String BOXES = "Boxes";
    private static final String STATE = "ViewState";
    private Box mCurrentBox;
    private ArrayList<Box> mBoxes = new ArrayList<>();
    private Paint mBackgroundPaint;
    private Paint mBoxPaint;
    private int mPointerId_1 = -1;
    private int mPointerId_2 = -1;

    public BoxDrawningView(Context context) {
        super(context);
    }

    public BoxDrawningView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

        Log.i(TAG, "Pointer id: " + event.getPointerId(event.getActionIndex()));
        Log.i(TAG, "Pointer count: " + event.getPointerCount());

        if (mPointerId_1 == -1) {
            mPointerId_1 = event.getPointerId(event.getActionIndex());
            mPointerId_2 = -1;
        }

        PointF point = new PointF(event.getX(mPointerId_1), event.getY(mPointerId_1));
        PointF point2;
        Log.i(TAG, "Receive event at x=" + point.x + ", y" + point.y);

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_POINTER_DOWN:
                mPointerId_2 = event.getPointerId(event.getActionIndex());
                point2 = new PointF(event.getX(mPointerId_2), event.getY(mPointerId_2));
                mCurrentBox.setOrigin(point2);
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mPointerId_2 = -1;
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, " ACTION_DOWN");
                //Reset drawing state
                mCurrentBox = new Box(point);
                mBoxes.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, " ACTION_MOVE");
                if (mCurrentBox != null) {
                    mCurrentBox.setCurrent(point);
                    if (mPointerId_2 >= 0){
                        point2 = new PointF(event.getX(mPointerId_2), event.getY(mPointerId_2));
                        mCurrentBox.setOrigin(point2);
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, " ACTION_UP");
                mCurrentBox = null;
                mPointerId_1 = -1;
                mPointerId_2 = -1;
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG, " ACTION_CANCEL");
                mCurrentBox = null;
                mPointerId_1 = -1;
                mPointerId_2 = -1;
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //fill background
        canvas.drawPaint(mBackgroundPaint);

        for (Box box : mBoxes) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);

            canvas.drawRect(left, top, right, bottom, mBoxPaint);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE, super.onSaveInstanceState());
        bundle.putParcelableArrayList(BOXES, mBoxes);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        mBoxes = bundle.getParcelableArrayList(BOXES);
        state = bundle.getParcelable(STATE);
        super.onRestoreInstanceState(state);
    }
}
