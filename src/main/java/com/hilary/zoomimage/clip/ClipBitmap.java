package com.hilary.zoomimage.clip;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;

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
public class ClipBitmap {
    private Bitmap mBitmap;
    private Matrix mBtimapMatrix = new Matrix();
    private RectF mClipRect = new RectF();

    private ClipBitmapListener mListener;

    private AsyncClipBitamp mAsyncClipBitamp;

    private long id = 0;

    public ClipBitmap(Bitmap bitmap, Matrix cMatrix, RectF rect, ClipBitmapListener clipBitmapListener) {
        mBitmap = bitmap;
        mClipRect = rect;
        mBtimapMatrix.set(cMatrix);
        mListener = clipBitmapListener;
    }

    public void setBtimapMatrix(Matrix btimapMatrix) {
        mBtimapMatrix.set(btimapMatrix);
    }

    public void setListener(ClipBitmapListener listener) {
        mListener = listener;
    }

    public void clipBitmap(long id) {
        if (mAsyncClipBitamp == null) {
            mAsyncClipBitamp = new AsyncClipBitamp();
        }
        ClipBitmapModel clipModel = new ClipBitmapModel(id);
        mAsyncClipBitamp.execute(clipModel);
    }

    public void clipBitmap() {
        clipBitmap(id++);
    }

    public void setClipRect(RectF clipRect) {
        mClipRect = clipRect;
    }

    private class AsyncClipBitamp extends AsyncTask<ClipBitmapModel, String, ClipBitmapModel> {

        @Override
        protected void onPreExecute() {
            if (mListener != null) {
                mListener.onPreClip();
            }
        }

        @Override
        protected void onPostExecute(ClipBitmapModel bitmap) {
            if (bitmap != null && mListener != null) {
                mListener.onPostClip(bitmap);
            }
        }

        @Override
        protected ClipBitmapModel doInBackground(ClipBitmapModel... params) {
            ClipBitmapModel model = params[0];
            if (mClipRect.isEmpty()) {
                throw new NullPointerException("mClipRect is empty");
            }

            RectF rectF = new RectF();
            mBtimapMatrix.mapRect(rectF);
            float[] value = new float[9];
            mBtimapMatrix.getValues(value);
            float scale = value[Matrix.MSCALE_Y];
            int left = (int) ((mClipRect.left - rectF.left) / scale);
            int top = (int) ((mClipRect.top - rectF.top) / scale);
            int width = (int) (mClipRect.width() / scale);
            int height = (int) (mClipRect.height() / scale);
            Bitmap cBitamp = Bitmap.createBitmap(mBitmap, left, top, width, height);
            model.setBitmap(cBitamp);
            return model;
        }
    }

    public interface ClipBitmapListener {
        void onPreClip();
        void onPostClip(ClipBitmapModel bitmap);
    }

}
