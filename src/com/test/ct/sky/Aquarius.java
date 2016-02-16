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

    boolean bClip=true;
    long mDuration=3000;
    float mWaveCycles = 0.25f;
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
        mContainerPath.addCircle(mCentX, mCentY, mRadius, Path.Direction.CCW);
        initAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.setDrawFilter(mDrawFilter);

        if(bClip){
            canvas.clipPath(mContainerPath);
            canvas.drawCircle(mCentX, mCentY, mRadius, mContainerPaint);
        }else{
            canvas.drawRect(0,0,mWidth,mHeight,mContainerPaint);
        }
        canvas.drawRect(0, mWaterLevel, mWidth, mHeight, mWaterPaint);

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.drawPath(mWaterPath, mWaterPaint);
        canvas.restore();

    }

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
        mWaveAnim.setInterpolator(new CycleInterpolator(mWaveCycles));
        mWaveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int x = (int) animation.getAnimatedValue("bezierX");
                int y = (int) animation.getAnimatedValue("bezierY");
                mWaterPath.reset();
                mWaterPath.moveTo(0, mWaterLevel);
                mWaterPath.quadTo(x, mWaterLevel + y, mWidth, mWaterLevel);
//                Log.d(TAG, "moveX:" + moveX);

                invalidate();
            }
        });
        mWaveAnim.start();
    }


    //for test
    public boolean isPaintFill(){
        return mWaterPaint.getStyle()==Paint.Style.FILL;
    }
    public void setPaintFill(boolean fill){
        Paint.Style style=(fill?Paint.Style.FILL:Paint.Style.STROKE);
        mWaterPaint.setStyle(style);

    }

    public long getWaveDuration(){
        return mDuration;
    }

    public void setWaveDuration(long duration){
        mDuration=duration;
    }

    public float getWaveCycles(){
        return mWaveCycles;
    }

    public void setWaveCycles(float cycles){
        mWaveCycles =cycles;
    }

    public float getWaveHeightRatio(){
        return mWaveHeightRatio;
    }

    public void setWaveHeightRatio(float heightRatio){
        mWaveHeightRatio = heightRatio;
    }

    public int getWaterLevelPercent(){
        return mWaterLevelPercent;
    }

    public void setWaterLevelPercent(int level){
        mWaterLevelPercent=level;
    }

    public boolean isClipped(){
        return bClip;
    }

    public void setClipped(boolean clip){
        bClip=clip;
    }

//    public boolean isAnim(){
//        return mWaveAnim!=null && mWaveAnim.isRunning();
//    }
    public void setAnim(boolean start){
        if(mWaveAnim!=null){
            if(start && !mWaveAnim.isRunning()){
                mWaveAnim.start();
            }else if(!start && mWaveAnim.isRunning()){
                mWaveAnim.cancel();
            }
        }
    }

    public void reset(){
        initAnim();
    }
}
