package com.test.ct.sky;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.CycleInterpolator;

/**
 * Created by cting on 2016/1/28.
 */
public class Aquarius extends View{
    public static final String TAG="Aquarius";

    int mWidth;
    int mHeight;

    Paint mContainerPaint;
    Paint mWaterPaint;

    Path mContainerPath;
    Path mWaterPath;
    ValueAnimator mWaveAnim;

    int mCentX=-1;
    int mCentY=-1;
    int mRadius=-1;

    long mDuration=3000;
    float mCycles = 0.25f;
    int mWaterLevelPercent=50;
    float mWaveHeightRatio=6;

    int mWaterLevel;
    int mWaveHeight;
    PaintFlagsDrawFilter mDrawFilter;

    public Aquarius(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mContainerPaint=new Paint();
        mContainerPaint.setStrokeWidth(2);
        mContainerPaint.setColor(Color.BLACK);
        mContainerPaint.setStyle(Paint.Style.STROKE);
        mContainerPaint.setAntiAlias(true);

        mWaterPaint=new Paint();
        mWaterPaint.setColor(Color.BLUE);
        mWaterPaint.setStyle(Paint.Style.FILL);
        mWaterPaint.setAntiAlias(true);

        mContainerPath=new Path();
        mWaterPath =new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCentX = mWidth/2;
        mCentY = mHeight/2;
        mRadius = Math.min(mWidth,mHeight)/2;
        mContainerPath.addCircle(mCentX,mCentY,mRadius, Path.Direction.CCW);
        mWaterLevel =mHeight/2;
        initAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.setDrawFilter(mDrawFilter);
        canvas.clipPath(mContainerPath);
        canvas.drawCircle(mCentX, mCentY, mRadius, mContainerPaint);
        canvas.drawRect(0, mWaterLevel, mWidth, mHeight, mWaterPaint);

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.drawPath(mWaterPath, mWaterPaint);
        canvas.restore();

        //test control point
//        canvas.drawCircle(moveX,moveY,10,mContainerPaint);

        // test water level
//        canvas.drawLine(0, mWaterLevel,mWidth, mWaterLevel,mContainerPaint);


    }

//    int moveX,moveY;

    private void initAnim(){
        mWaveHeight = (int) (mHeight/mWaveHeightRatio);
        mWaterLevel = mHeight*(100-mWaterLevelPercent)/100;
        if(mWaveAnim!=null && mWaveAnim.isRunning()){
            mWaveAnim.cancel();
        }
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofInt("bezierX", 0, mWidth);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofInt("bezierY", 0, -mWaveHeight, 0);
        mWaveAnim=ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY);
        mWaveAnim.setDuration(mDuration);
        mWaveAnim.setRepeatCount(ValueAnimator.INFINITE);
        mWaveAnim.setRepeatMode(ValueAnimator.RESTART);
        mWaveAnim.setInterpolator(new CycleInterpolator(mCycles));
        mWaveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int x = (int) animation.getAnimatedValue("bezierX");
                int y = (int) animation.getAnimatedValue("bezierY");
                mWaterPath.reset();
                mWaterPath.moveTo(0, mWaterLevel);
                mWaterPath.quadTo(x, mWaterLevel + y, mWidth, mWaterLevel);
//                Log.d(TAG, "moveX:" + moveX);

//                moveX=x;
//                moveY=mWaterLevel+y;

                invalidate();
            }
        });
        mWaveAnim.start();
    }

    //for test
    public void setPaintFill(boolean fill){
        Paint.Style style=(fill?Paint.Style.FILL:Paint.Style.STROKE);
        mWaterPaint.setStyle(style);

    }

    public void setWaveDuration(long duration){
        mDuration=duration;
        initAnim();
    }
    public void setCycles(float cycles){
        mCycles=cycles;
        initAnim();
    }
    public void setWaveRatio(float heightRatio){
        mWaveHeightRatio = heightRatio;
        initAnim();
    }
    public void setWaterLevelPercent(int level){
        mWaterLevelPercent=level;
        initAnim();

    }
}
