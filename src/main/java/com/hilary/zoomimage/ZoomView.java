package com.hilary.zoomimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.hilary.zoomimage.gesture.TouchAbleController;

//  Created by hilary on 2017/2/27.
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
public class ZoomView extends ImageView implements com.hilary.zoomimage.gesture.ZoomableController.Listener {

    private TouchAbleController mTouchAbleController;

    protected final RectF mImageBounds = new RectF();
    protected final RectF mViewBounds = new RectF();
    protected final RectF mLimitBounds = new RectF();

    protected Bitmap mBitmap;

    protected Matrix mMatrix = new Matrix();

    public ZoomView(Context context) {
        this(context, null);
    }

    public ZoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void onTransformChanged(Matrix transform) {
        mMatrix.set(transform);
        invalidate();
    }

    protected void init() {
        mTouchAbleController = TouchAbleController.newInstance();
        mTouchAbleController.setListener(this);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mTouchAbleController.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateZoomableControllerBounds();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.concat(mMatrix);
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);

    }

    private void updateZoomableControllerBounds() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            mImageBounds.setEmpty();
        } else {
            mImageBounds.set(getDrawable().getBounds());
        }
        mViewBounds.set(0, 0, getWidth(), getHeight());
        if (mImageBounds.isEmpty()) {
            return;
        }
        mTouchAbleController.setImageBounds(mImageBounds);
        mTouchAbleController.setViewBounds(mViewBounds);
        if (mLimitBounds.isEmpty()) {
            mLimitBounds.set(mViewBounds);
        }
        mTouchAbleController.setLimitBounds(mLimitBounds);
        mTouchAbleController.zoomToImagePoint();
    }

    public void setLimitBounds(RectF limitBounds) {
        mLimitBounds.set(limitBounds);
        mTouchAbleController.setLimitBounds(mLimitBounds);
        mTouchAbleController.resetScaleFactor();
        mTouchAbleController.reset();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        mMatrix.reset();
        mTouchAbleController.reset();
    }

}