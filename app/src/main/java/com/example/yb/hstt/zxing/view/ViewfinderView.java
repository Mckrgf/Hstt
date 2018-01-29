/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.yb.hstt.zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.example.yb.hstt.R;
import com.example.yb.hstt.Utils.DisplayUtil;
import com.example.yb.hstt.zxing.camera.CameraManager;
import com.google.zxing.ResultPoint;

import java.util.Collection;
import java.util.HashSet;

/**
 * 自定义组件实现,扫描功能
 */
public final class ViewfinderView extends View {

    private static final long ANIMATION_DELAY = 20L;
    private static final int OPAQUE = 0xFF;

    private final Paint paint;
    private final Paint paint2;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int resultPointColor;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2=new Paint(Paint.ANTI_ALIAS_FLAG);

        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        possibleResultPoints = new HashSet<ResultPoint>(5);

        scanLight = BitmapFactory.decodeResource(resources,
                R.mipmap.scan_light);

        initInnerRect(context, attrs);
    }

    /**
     * 初始化内部框的大小
     * @param context
     * @param attrs
     */
    private void initInnerRect(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.innerrect);

        // 扫描框距离顶部
        float innerMarginTop = ta.getDimension(R.styleable.innerrect_inner_margintop, -1);
        if (innerMarginTop != -1) {
            CameraManager.FRAME_MARGINTOP = (int) innerMarginTop;
        }

        // 扫描框的宽度
        CameraManager.FRAME_WIDTH = (int) ta.getDimension(R.styleable.innerrect_inner_width, DisplayUtil.screenWidthPx / 2);

        // 扫描框的高度
        CameraManager.FRAME_HEIGHT = (int) ta.getDimension(R.styleable.innerrect_inner_height, DisplayUtil.screenWidthPx / 2);

        // 扫描框边角颜色
        innercornercolor = ta.getColor(R.styleable.innerrect_inner_corner_color, Color.parseColor("#45DDDD"));
        // 扫描框边角长度
        innercornerlength = (int) ta.getDimension(R.styleable.innerrect_inner_corner_length, 65);
        // 扫描框边角宽度
        innercornerwidth = (int) ta.getDimension(R.styleable.innerrect_inner_corner_width, 15);

        // 扫描bitmap
        Drawable drawable = ta.getDrawable(R.styleable.innerrect_inner_scan_bitmap);
        if (drawable != null) {
        }

        // 扫描控件
        scanLight = BitmapFactory.decodeResource(getResources(), ta.getResourceId(R.styleable.innerrect_inner_scan_bitmap, R.mipmap.scan_light));
        // 扫描速度
        SCAN_VELOCITY = ta.getInt(R.styleable.innerrect_inner_scan_speed, 5);

        isCircle = ta.getBoolean(R.styleable.innerrect_inner_scan_iscircle, true);
        paint2.setStrokeWidth(1);
        paint2.setColor(innercornercolor);

        ta.recycle();

    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {
            paint.setColor(innercornercolor);
            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(frame.left,frame.top,frame.right,frame.bottom,paint);

            drawFrameBounds(canvas, frame);
            drawTableBounds(canvas,frame);
            drawScanLight(canvas, frame);

            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<ResultPoint>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);

                if (isCircle) {
                    for (ResultPoint point : currentPossible) {
                        canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
                    }
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);

                if (isCircle) {
                    for (ResultPoint point : currentLast) {
                        canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
                    }
                }
            }

            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
        }
    }


    // 扫描线移动的y
    private int scanLineTop,tableLineTop;
    // 扫描线移动速度
    private int SCAN_VELOCITY;
    // 扫描线
    private Bitmap scanLight;
    // 是否展示小圆点
    private boolean isCircle;
    private float gradient_height;
    private LinearGradient mlinearGradient,mlinearGradient2;

    /**
     * 绘制移动扫描线
     *
     * @param canvas
     * @param frame
     */
    private void drawScanLight(Canvas canvas, Rect frame) {

        if (scanLineTop == 0) {
            scanLineTop = frame.top;
        }

        if (scanLineTop >= frame.bottom ) {
            scanLineTop = frame.top;
        } else {
            scanLineTop += SCAN_VELOCITY;
        }
        if (gradient_height>=50){//渐变的最大高度值
            gradient_height=0;//渐变的最小高度值
        }else{
            //移动次数，
            int move_count=CameraManager.FRAME_HEIGHT/SCAN_VELOCITY;
            //以50为长度平分，没一次增加的高度
            float move_firstcount_dis=50.0f/move_count;
            gradient_height=gradient_height+move_firstcount_dis;
        }

        mlinearGradient= new LinearGradient(frame.left,scanLineTop-gradient_height,frame.left,scanLineTop,Color.TRANSPARENT,innercornercolor,Shader.TileMode.CLAMP);
        paint2.setShader(mlinearGradient);
        paint.setColor(innercornercolor);
        paint.setStrokeWidth(2);
        //画渐变色
        if (scanLineTop>=(frame.top+50)){
            canvas.drawRect(frame.left,scanLineTop-gradient_height,frame.right,scanLineTop,paint2);
        }
        //画最下面的线
        canvas.drawLine(frame.left,scanLineTop,frame.right,scanLineTop,paint);
        //画一个图片当扫描线
//        Rect scanRect = new Rect(frame.left, scanLineTop, frame.right,
//                scanLineTop + 30);
//        canvas.drawBitmap(scanLight, null, scanRect, paint);
    }

    int space=15;
    private void drawTableBounds(Canvas canvas, Rect frame) {
        if (tableLineTop == 0) {
            tableLineTop = frame.top;
        }

        if (tableLineTop >= frame.bottom) {
            tableLineTop = frame.top;
        } else {
            tableLineTop += SCAN_VELOCITY;
        }

        mlinearGradient2=new LinearGradient(frame.left,frame.top,frame.left,tableLineTop,new int[]{Color.TRANSPARENT,innercornercolor},new float[]{0,1}, Shader.TileMode.CLAMP);
        paint2.setShader(mlinearGradient2);
        //先画横线
        for (int i=frame.top;i<=tableLineTop;i+=space){
            canvas.drawLine(frame.left,i,frame.right,i,paint2);
        }
       //再画竖线
        for (int i=frame.left;i<=frame.right;i+=space){
            canvas.drawLine(i,frame.top,i,tableLineTop,paint2);
        }

    }

    // 扫描框边角颜色
    private int innercornercolor;
    // 扫描框边角长度
    private int innercornerlength;
    // 扫描框边角宽度
    private int innercornerwidth;

    /**
     * 绘制取景框边框
     *
     * @param canvas
     * @param frame
     */
    private void drawFrameBounds(Canvas canvas, Rect frame) {

        /*paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawRect(frame, paint);*/

        paint.setColor(innercornercolor);
        paint.setStyle(Paint.Style.FILL);

        int corWidth = innercornerwidth;
        int corLength = innercornerlength;

        // 左上角
        canvas.drawRect(frame.left, frame.top, frame.left + corWidth, frame.top
                + corLength, paint);
        canvas.drawRect(frame.left+corWidth, frame.top, frame.left
                + corLength, frame.top + corWidth, paint);
        // 右上角
        canvas.drawRect(frame.right - corWidth, frame.top+corWidth, frame.right,
                frame.top + corLength, paint);
        canvas.drawRect(frame.right - corLength, frame.top,
                frame.right, frame.top + corWidth, paint);
        // 左下角
        canvas.drawRect(frame.left, frame.bottom - corLength,
                frame.left + corWidth, frame.bottom, paint);
        canvas.drawRect(frame.left+corWidth, frame.bottom - corWidth, frame.left
                + corLength, frame.bottom, paint);
        // 右下角
        canvas.drawRect(frame.right - corWidth, frame.bottom - corLength,
                frame.right, frame.bottom- corWidth, paint);
        canvas.drawRect(frame.right - corLength, frame.bottom - corWidth,
                frame.right, frame.bottom, paint);
    }


    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
