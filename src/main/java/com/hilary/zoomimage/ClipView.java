package com.hilary.zoomimage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;

import com.hilary.zoomimage.clip.ClipBitmap;

//  Created by hilary on 2017/3/6.
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
public class ClipView extends ZoomView {

    private Path mClipPath = new Path();
    private float[] mCircle = new float[3];
    private int mBgColor = Color.parseColor("#222222");
    private final int CIRCLE_SIZE = 100;

    public ClipView(Context context) {
        super(context);
    }

    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        float density = getResources().getDisplayMetrics().density;
        mCircle[0] = (right - left) / 2;
        mCircle[1] = (bottom - top) / 2;
        mCircle[2] = CIRCLE_SIZE * density;
        mLimitBounds.set(mCircle[0] - mCircle[2], mCircle[1] - mCircle[2], mCircle[0] + mCircle[2], mCircle[1] + mCircle[2]);
        setLimitBounds(mLimitBounds);
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(mBgColor);
        super.onDraw(canvas);
        int restoreToCount = canvas.save();
        mClipPath.reset();
        mClipPath.addCircle(mCircle[0], mCircle[1], mCircle[2], Path.Direction.CW);
        canvas.clipPath(mClipPath, Region.Op.DIFFERENCE);
        canvas.drawColor(Color.argb(200, 255, 255, 255));
        canvas.restoreToCount(restoreToCount);
    }

    public void clipBitamp(ClipBitmap.ClipBitmapListener listener) {
        ClipBitmap mClipBitmap = new ClipBitmap(mBitmap, mMatrix, mLimitBounds, listener);
        mClipBitmap.clipBitmap();
    }
}
