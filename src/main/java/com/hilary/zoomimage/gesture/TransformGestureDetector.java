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
public class TransformGestureDetector implements TouchGestureDetector.Listener {

    private final TouchGestureDetector mDetector;

    private Listener mListener = null;

    public TransformGestureDetector(TouchGestureDetector detector) {
        mDetector = detector;
        mDetector.setListener(this);
    }

    public static TransformGestureDetector newInstance() {
        return new TransformGestureDetector(TouchGestureDetector.newInstance());
    }

    @Override
    public void onGestureBegin(TouchGestureDetector detector) {
        if (mListener != null) {
            mListener.onGestureBegin(this);
        }
    }

    @Override
    public void onGestureUpdate(TouchGestureDetector detector) {
        if (mListener != null) {
            mListener.onGestureUpdate(this);
        }
    }

    @Override
    public void onGestureEnd(TouchGestureDetector detector) {
        if (mListener != null) {
            mListener.onGestureEnd(this);
        }
    }

    public boolean onTouchEvent(final MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    public void restartGesture() {
        mDetector.restartGesture();
    }

    public void reset() {
        mDetector.reset();
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public float getTranslationX() {
        return mDetector.getCurrentX()[0] - mDetector.getStartX()[0];
    }

    public float getTranslationY() {
        return mDetector.getCurrentY()[0] - mDetector.getStartY()[0];
    }

    public float getScale() {
        if (mDetector.getCount() < 2) {
            return 1;
        } else {
            float startDeltaX = mDetector.getStartX()[1] - mDetector.getStartX()[0];
            float startDeltaY = mDetector.getStartY()[1] - mDetector.getStartY()[0];
            float currentDeltaX = mDetector.getCurrentX()[1] - mDetector.getCurrentX()[0];
            float currentDeltaY = mDetector.getCurrentY()[1] - mDetector.getCurrentY()[0];
            float startDist = (float) Math.hypot(startDeltaX, startDeltaY);
            float currentDist = (float) Math.hypot(currentDeltaX, currentDeltaY);
            return currentDist / startDist;
        }
    }

    public float getRotation() {
        if (mDetector.getCount() < 2) {
            return 0;
        } else {
            float startDeltaX = mDetector.getStartX()[1] - mDetector.getStartX()[0];
            float startDeltaY = mDetector.getStartY()[1] - mDetector.getStartY()[0];
            float currentDeltaX = mDetector.getCurrentX()[1] - mDetector.getCurrentX()[0];
            float currentDeltaY = mDetector.getCurrentY()[1] - mDetector.getCurrentY()[0];
            float startAngle = (float) Math.atan2(startDeltaY, startDeltaX);
            float currentAngle = (float) Math.atan2(currentDeltaY, currentDeltaX);
            return currentAngle - startAngle;
        }
    }

    public float getPivotX() {
        return calcAverage(mDetector.getStartX(), mDetector.getCount());
    }

    public float getPivotY() {
        return calcAverage(mDetector.getStartY(), mDetector.getCount());
    }

    private float calcAverage(float[] arr, int len) {
        float sum = 0;
        for (int i = 0; i < len; i++) {
            sum += arr[i];
        }
        return (len > 0) ? sum / len : 0;
    }

    public interface Listener {
        public void onGestureBegin(TransformGestureDetector detector);
        public void onGestureUpdate(TransformGestureDetector detector);
        public void onGestureEnd(TransformGestureDetector detector);
    }

}
