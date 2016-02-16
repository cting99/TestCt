package com.test.ct.sky;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by cting on 15-12-31.
 */
public class FlickerView extends View {

    public static final String TAG = "Ct/StarView";
    ArrayList<Flicker> mFlickerList = new ArrayList<>();
    float[][] POSITION = new float[][]{
            {0.38f, 0.93f, 0.48f, 0.71f},{0.87f, 0.72f, 0.83f, 0.48f},{0.17f, 0.73f, 0.42f, 0.81f},
            {0.28f, 0.65f, 0.51f, 0.93f},{0.18f, 0.51f, 0.52f, 0.21f},{0.15f, 0.24f, 0.65f, 0.1f},
            {0.88f, 0.37f, 0.60f, 0.09f},{0.41f, 0.10f, 0.81f, 0.20f},{0.14f, 0.53f, 0.13f, 0.79f},
            {0.88f, 0.43f, 1.01f, 0.54f},{0.16f, 0.54f, 0.09f, 0.23f},{0.43f, 0.19f, 0.15f, 0.15f},
    };

    int mWidth;
    int mHeight;

    public FlickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        initFlickers();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Flicker flicker : mFlickerList) {
            flicker.draw(canvas);
        }
    }

    private int getRandomNumber(int min, int max) {
        return new Random().nextInt(max) % (max - min + 1) + min;
    }

    private int getFlickerRadius() {
        return getRandomNumber(6, 18);
    }

    private int getFlickerAlpha() {
        return getRandomNumber(180, 255);
    }

    private long getFlickerDuration(float distance) {
        return (int) (20000 * distance) / mWidth;
    }

    private static float check(float num) {
        return (float) (Math.round(num * 100) / 100.0);
    }

    private float checkX(float x){
        return check(x/mWidth);
    }

    private float checkY(float y){
        return check(y / mHeight);
    }

    private float scaleX(float disX) {
        return disX * mWidth;
    }

    private float scaleY(float disY) {
        return disY * mHeight;
    }

    private void initFlickers() {
        mFlickerList.clear();
        Flicker flicker;
        for (int i = 0; i < POSITION.length; i++) {
            flicker = new Flicker();
            flicker.fromInit(POSITION[i]);
            mFlickerList.add(flicker);
        }
    }


    private int[] mColors = new int[]{Color.WHITE, Color.WHITE, Color.argb(100, 255, 255, 255), Color.TRANSPARENT};
    private float[] mStops = new float[]{0, 0.1f, 0.1f, 1.0f};

    public class Flicker {
        public static final String TAG = "Ct/StarView/Flicker";

        float[] pos;

        float[] posStart;
        float[] posEnd;
        int radius;
        int alpha;
        Paint paint;

        RadialGradient radialGradient;
        Path path = new Path();
        PathMeasure pathMeasure;
        AnimatorSet animSet;

        public Flicker() {
        }

        public boolean fromInit(float[] point) {
            if (point != null) {
                int length = point.length;
                if (length == 2) {
                    setStart(point[0], point[1]);
                    return false;
                } else if (length == 4) {
                    setStart(point[0], point[1]);
                    return setEnd(point[2], point[3]);
                }
            }
            return false;
        }

        public void setStart(float x, float y) {
            posStart = new float[]{scaleX(x), scaleY(y)};
            pos = new float[]{posStart[0], posStart[1]};
            initPaint();
        }

        public boolean setEnd(float x, float y) {
            posEnd = new float[]{scaleX(x), scaleY(y)};
            path.reset();
            path.moveTo(posStart[0], posStart[1]);
            path.lineTo(posEnd[0], posEnd[1]);
            pathMeasure = new PathMeasure(path, false);

            final float distance = pathMeasure.getLength();
            if (distance < mWidth / 20) {
                return false;
            }
            long duration = getFlickerDuration(distance);
            Log.d(TAG, "distance=" + distance + ",duration=" + duration);
            initAnim(distance, duration);

            return true;
        }

        private void initPaint() {
            if (pos == null) {
                return;
            }
            radius = getFlickerRadius();
            alpha = getFlickerAlpha();

            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(2);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(alpha);
            radialGradient = new RadialGradient(pos[0], pos[1], radius, mColors, mStops, Shader.TileMode.MIRROR);
            paint.setShader(radialGradient);
//            paint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));//not support after sdk8,use radialGradient instead
        }

        private void initAnim(float distance, long duration) {
            long flyDuration = duration;
            long disapperDuration = duration / 4;
            final long disapperDelay = flyDuration - disapperDuration;
            final ValueAnimator flyAnim = initFlyPathAnim(distance, flyDuration);
            final ValueAnimator disapperAnim = initDisappearAnim(disapperDuration);
            animSet = new AnimatorSet();
            animSet.play(flyAnim);
            animSet.play(disapperAnim).after(disapperDelay);
            animSet.start();
        }

        public boolean isFlying() {
            return animSet != null && animSet.isRunning();
        }

        private ValueAnimator initFlyPathAnim(float distance, final long duration) {
            final ValueAnimator va = ValueAnimator.ofFloat(0, distance);
            va.setDuration(duration);
            va.setInterpolator(new LinearInterpolator());
//            va.setRepeatCount(ValueAnimator.INFINITE);
//            va.setRepeatMode(ValueAnimator.INFINITE);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    pathMeasure.getPosTan(value, pos, null);
                    radialGradient = new RadialGradient(pos[0], pos[1], radius, mColors, mStops, Shader.TileMode.MIRROR);
                    paint.setShader(radialGradient);
                    invalidate();
//                    Log.i(TAG, "anim fly?");
                }
            });
//            flyAnim.start();
            return va;
        }

        private ValueAnimator initDisappearAnim(long duration) {
            ValueAnimator va = ValueAnimator.ofInt(alpha, 0);
            va.setDuration(duration);
            va.setInterpolator(new AccelerateDecelerateInterpolator());
//            va.setRepeatCount(ValueAnimator.INFINITE);
//            va.setRepeatMode(ValueAnimator.INFINITE);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentAlpha = (int) animation.getAnimatedValue();
                    paint.setAlpha(currentAlpha);
                    invalidate();
//                    Log.i(TAG, "anim disappear?");
                }
            });
            va.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    paint.setAlpha(alpha);
                    animSet.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
//            va.start();
            return va;
        }


        public void draw(Canvas canvas) {
            if (pos != null && isFlying()) {
                canvas.drawCircle(pos[0], pos[1], radius, paint);
            }
        }

        @Override
        public String toString() {
            String str = "not init;";
            if (posStart != null && posEnd != null) {
                str = "{"
                        + checkX(posStart[0]) + "f, " + checkY(posStart[1]) + "f, "
                        + checkX(posEnd[0]) + "f, " + checkY(posEnd[1]) + "f"
                        + "},";
            }
            return str;
        }
    }

    //for test mode to get init data
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        float px = check(x / mWidth);
        float py = check(y / mHeight);
        Log.d(TAG, "touch [" + x + "," + y + "],action=" + action);

        //get init flickers' position
        touchForFlickers(action, px, py);

        return true;
    }

    Flicker flicker;
    private void touchForFlickers(int action, float x, float y) {
        if (action == MotionEvent.ACTION_DOWN) {
            Log.i(TAG, "-----------------[" + x + "," + y + "]");
            flicker = new Flicker();
            flicker.setStart(x, y);
        } else if (action == MotionEvent.ACTION_UP) {
            if (flicker.setEnd(x, y)) {
                mFlickerList.add(flicker);
                invalidate();
                dumpData();
            }
        }
    }

    private void dumpData() {
        String positionStr = "";
        int i = 0;
        for (Flicker flicker : mFlickerList) {
            i++;
            positionStr += flicker.toString();
            if (i == 3) {
                positionStr += "\n";
                i = 0;
            }
        }
        Log.d(TAG, "dump touch positions:" + positionStr);
    }

}
