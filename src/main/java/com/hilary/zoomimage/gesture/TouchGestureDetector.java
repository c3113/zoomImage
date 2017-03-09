package com.hilary.zoomimage.gesture;

import android.view.MotionEvent;

//  Created by hilary on 2017/3/2.
//  Copyright (c) 2015 llspace.com. All rights reserved.
//
//                            _ooOoo_
//                           o8888888o
//                           88" . "88
//                           (| -_- |)
//                            O\ = /O
//                        ____/`---'\____
//                      .   ' \\| |// `.
//                       / \\||| : |||// \
//                     / _||||| -:- |||||- \
//                       | | \\\ - /// | |
//                     | \_| ''\---/'' | |
//                      \ .-\__ `-` ___/-. /
//                   ___`. .' /--.--\ `. . __
//                ."" '< `.___\_<|>_/___.' >'"".
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |
//                 \ \ `-. \_ __\ /__ _/ .-` / /
//         ======`-.____`-.___\_____/___.-`____.-'======
//                            `=---='
//
//         .............................................
//                  佛祖镇楼                  BUG辟易
//
public class TouchGestureDetector {

    private static final int MAX_POINTERS = 2;
    private boolean mGestureInProgress;
    private int mCount;
    private final int mId[] = new int[MAX_POINTERS];
    private final float mStartX[] = new float[MAX_POINTERS];
    private final float mStartY[] = new float[MAX_POINTERS];
    private final float mCurrentX[] = new float[MAX_POINTERS];
    private final float mCurrentY[] = new float[MAX_POINTERS];

    private TouchGestureDetector.Listener mListener = null;

    public TouchGestureDetector() {
        reset();
    }

    public static TouchGestureDetector newInstance() {
        return new TouchGestureDetector();
    }

    private void startGesture() {
        if (!mGestureInProgress) {
            mGestureInProgress = true;
            if (mListener != null) {
                mListener.onGestureBegin(this);
            }
        }
    }

    private void stopGesture() {
        if (mGestureInProgress) {
            mGestureInProgress = false;
            if (mListener != null) {
                mListener.onGestureEnd(this);
            }
        }
    }

    public void restartGesture() {
        if (!mGestureInProgress) {
            return;
        }
        stopGesture();
        for (int i = 0; i < MAX_POINTERS; i++) {
            mStartX[i] = mCurrentX[i];
            mStartY[i] = mCurrentY[i];
        }
        startGesture();
    }

    private int getPressedPointerIndex(MotionEvent event, int i) {
        final int count = event.getPointerCount();
        final int action = event.getActionMasked();
        final int index = event.getActionIndex();
        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_POINTER_UP) {
            if (i >= index) {
                i++;
            }
        }
        return (i < count) ? i : -1;
    }

    public boolean onTouchEvent(final MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE: {
                for (int i = 0; i < MAX_POINTERS; i++) {
                    int index = event.findPointerIndex(mId[i]);
                    if (index != -1) {
                        mCurrentX[i] = event.getX(index);
                        mCurrentY[i] = event.getY(index);
                    }
                }
                if (!mGestureInProgress) {
                    startGesture();
                }
                if (mGestureInProgress && mListener != null) {
                    mListener.onGestureUpdate(this);
                }
                break;
            }

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP: {
                boolean wasGestureInProgress = mGestureInProgress;
                stopGesture();
                reset();
                for (int i = 0; i < MAX_POINTERS; i++) {
                    int index = getPressedPointerIndex(event, i);
                    if (index == -1) {
                        break;
                    }
                    mId[i] = event.getPointerId(index);
                    mCurrentX[i] = mStartX[i] = event.getX(index);
                    mCurrentY[i] = mStartY[i] = event.getY(index);
                    mCount++;
                }
                if (wasGestureInProgress && mCount > 0) {
                    startGesture();
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                stopGesture();
                reset();
                break;
            }
        }
        return true;
    }

    public void setListener(TouchGestureDetector.Listener listener) {
        mListener = listener;
    }

    public void reset() {
        mGestureInProgress = false;
        mCount = 0;
        for (int i = 0; i < MAX_POINTERS; i++) {
            mId[i] = MotionEvent.INVALID_POINTER_ID;
        }
    }

    public boolean isGestureInProgress() {
        return mGestureInProgress;
    }

    public int getCount() {
        return mCount;
    }

    public float[] getStartX() {
        return mStartX;
    }

    public float[] getStartY() {
        return mStartY;
    }

    public float[] getCurrentX() {
        return mCurrentX;
    }

    public float[] getCurrentY() {
        return mCurrentY;
    }

    public interface Listener {
        public void onGestureBegin(TouchGestureDetector detector);
        public void onGestureUpdate(TouchGestureDetector detector);
        public void onGestureEnd(TouchGestureDetector detector);
    }
}
