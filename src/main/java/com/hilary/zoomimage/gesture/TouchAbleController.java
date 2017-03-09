package com.hilary.zoomimage.gesture;

import android.graphics.Matrix;
import android.graphics.RectF;
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
public class TouchAbleController implements ZoomableController, TransformGestureDetector.Listener {

    private TransformGestureDetector mDetector;

    private final RectF mImageBounds = new RectF();
    private final RectF mTransformedImageBounds = new RectF();
    private final RectF mViewBounds = new RectF();
    private final RectF mLimitBounds = new RectF();

    private Matrix mActiveTransform = new Matrix();
    private Matrix mPreviousTransform = new Matrix();
    private final float[] mTempValues = new float[9];

    private boolean mIsEnabled = true;
    private boolean mIsRotationEnabled = false;
    private boolean mIsScaleEnabled = true;
    private boolean mIsTranslationEnabled = true;

    private float mMinScaleFactor = 1.0f;
    private float mMaxScaleFactor = 3.0f;

    private ZoomableController.Listener mListener = null;

    public TouchAbleController(TransformGestureDetector gestureDetector) {
        mDetector = gestureDetector;
        mDetector.setListener(this);
    }

    public static TouchAbleController newInstance() {
        return new TouchAbleController(TransformGestureDetector.newInstance());
    }

    @Override
    public void setEnabled(boolean enabled) {
        mIsEnabled = enabled;
        if (!enabled) {
            reset();
        }
    }

    @Override
    public boolean isEnabled() {
        return mIsEnabled;
    }

    public void reset() {
        mDetector.reset();
        mPreviousTransform.reset();
        mActiveTransform.reset();
        onTransformChanged();
    }

    protected void onTransformChanged() {
        mActiveTransform.mapRect(mTransformedImageBounds, mImageBounds);
        if (mListener != null && isEnabled()) {
            mListener.onTransformChanged(mActiveTransform);
        }
    }

    @Override
    public void setListener(ZoomableController.Listener listener) {
        mListener = listener;
    }

    @Override
    public float getScaleFactor() {
        mActiveTransform.getValues(mTempValues);
        return mTempValues[Matrix.MSCALE_X];
    }

    public float getMinScaleFactor() {
        return mMinScaleFactor;
    }

    public void setMinScaleFactor(float minScaleFactor) {
        mMinScaleFactor = minScaleFactor;
    }

    public float getMaxScaleFactor() {
        return mMaxScaleFactor;
    }

    public void setMaxScaleFactor(float maxScaleFactor) {
        mMaxScaleFactor = maxScaleFactor;
    }

    @Override
    public Matrix getTransform() {
        return mActiveTransform;
    }

    @Override
    public void setImageBounds(RectF imageBounds) {
        mImageBounds.set(imageBounds);
    }

    @Override
    public void setViewBounds(RectF viewBounds) {
        mViewBounds.set(viewBounds);
    }

    @Override
    public void setLimitBounds(RectF limitBounds) {
        mLimitBounds.set(limitBounds);
    }

    public void resetScaleFactor() {

        float iWidth = mImageBounds.width();
        float iHeight = mImageBounds.height();

        float lWidth = mLimitBounds.width();
        float lHeight = mLimitBounds.height();
        if (iWidth <= 0 || lWidth <= 0) {
            return;
        }

        if (iWidth * lHeight > lWidth * iHeight) {
            mMinScaleFactor = lHeight / iHeight;
        } else {
            mMinScaleFactor = lWidth / iWidth;
        }
        mMaxScaleFactor = Math.max(mMinScaleFactor, mMaxScaleFactor);
    }

    public void zoomToImagePoint() {
        float scale;
        float dx = 0, dy = 0;
        float iWidth = mImageBounds.width();
        float iHeight = mImageBounds.height();

        float vWidth = mViewBounds.width();
        float vHeight = mViewBounds.height();
        resetScaleFactor();
        if (iWidth * vHeight > vWidth * iHeight) {
            scale = vHeight / iHeight;
            dx = (vWidth - iWidth * scale) * 0.5f;
        } else {
            scale = vWidth / iWidth;
            dy = (vHeight - iHeight * scale) * 0.5f;
        }
        mActiveTransform.reset();
        mActiveTransform.postScale(scale, scale);
        mActiveTransform.postTranslate(dx, dy);

        mPreviousTransform.set(mActiveTransform);
        onTransformChanged();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsEnabled) {
            return mDetector.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public void onGestureBegin(TransformGestureDetector detector) {
        mPreviousTransform.set(mActiveTransform);
    }

    @Override
    public void onGestureUpdate(TransformGestureDetector detector) {
        mActiveTransform.set(mPreviousTransform);
        if (mIsRotationEnabled) {
            float angle = detector.getRotation() * (float) (180 / Math.PI);
            mActiveTransform.postRotate(angle, detector.getPivotX(), detector.getPivotY());
        }
        if (mIsScaleEnabled) {
            float scale = detector.getScale();
            float pivotX = detector.getPivotX();
            float pivotY = detector.getPivotY();

            mActiveTransform.postScale(scale, scale, pivotX, pivotY);
            limitScale(pivotX, pivotY);
        }
        if (mIsTranslationEnabled) {
            mActiveTransform.postTranslate(detector.getTranslationX(), detector.getTranslationY());
        }
        if (limitTranslation(mActiveTransform, true, true)) {
            mDetector.restartGesture();
        }
        onTransformChanged();
    }

    @Override
    public void onGestureEnd(TransformGestureDetector detector) {
        mPreviousTransform.set(mActiveTransform);
    }

    private void limitScale(float pivotX, float pivotY) {
        float currentScale = getScaleFactor();
        float targetScale = limit(currentScale, mMinScaleFactor, mMaxScaleFactor);
        if (targetScale != currentScale) {
            float scale = targetScale / currentScale;
            mActiveTransform.postScale(scale, scale, pivotX, pivotY);
        }
    }

    private boolean limitTranslation(Matrix newTransform, boolean limitX, boolean limitY) {
        mActiveTransform.mapRect(mTransformedImageBounds, mImageBounds);
        RectF bounds = mTransformedImageBounds;
        bounds.set(mImageBounds);
        newTransform.mapRect(bounds);

        if (bounds.left > mLimitBounds.left || bounds.top > mLimitBounds.top) {
            float offsetLeft;
            float offsetTop;
            offsetLeft = mLimitBounds.left - bounds.left;
            offsetTop = mLimitBounds.top - bounds.top;
            newTransform.postTranslate(offsetLeft >= 0 ? 0 : offsetLeft, offsetTop >= 0 ? 0 : offsetTop);
        }
        if (bounds.right < mLimitBounds.right || bounds.bottom < mLimitBounds.bottom) {
            float offsetX = mLimitBounds.right - bounds.right;
            float offsetY = mLimitBounds.bottom - bounds.bottom;
            newTransform.postTranslate(offsetX <= 0 ? 0 : offsetX, offsetY <= 0 ? 0 : offsetY);
        }

        return false;
    }

    private float getOffsetX(float offset, float imageDimension) {
        return getOffset(offset, imageDimension, mLimitBounds.right, mLimitBounds.left);
    }

    private float getOffsetY(float offset, float imageDimension) {
        return getOffset(offset, imageDimension, mLimitBounds.bottom, mLimitBounds.top);
    }

    private float getOffset(float offset, float imageDimension, float limitWidth, float limitStart) {
        float diff = limitWidth - imageDimension;
        return (diff >= 0) ? diff : limit(offset, diff, limitStart);
    }

    private float limit(float value, float min, float max) {
        return Math.min(Math.max(min, value), max);
    }

}
