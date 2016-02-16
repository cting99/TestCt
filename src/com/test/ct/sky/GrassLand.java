package com.test.ct.sky;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.test.ct.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by cting on 2016/1/26.
 */
public class GrassLand extends View {

    public static final String TAG = "Ct/GrassLand";

    float[][] POSITION = new float[][]{
            {0.61f,0.77f,0.71f,1.0f},{0.47f,0.81f,0.57f,1.04f},{0.69f,0.72f,0.79f,0.97f},
            {0.42f,0.75f,0.5f,1.01f},{0.76f,0.56f,0.87f,0.98f},{0.33f,0.71f,0.41f,1.02f},
            {0.83f,0.45f,0.95f,0.97f},{0.19f,0.67f,0.3f,0.98f},{0.27f,0.69f,0.4f,1.02f},
            {0.15f,0.66f,0.24f,0.97f},{0.11f,0.49f,0.22f,0.92f},{0.01f,0.55f,0.15f,0.89f},
            /*{0.61f,0.77f,0.7f,1.01f},{0.46f,0.79f,0.58f,1.03f},{0.67f,0.73f,0.78f,1.0f},
            {0.68f,0.67f,0.82f,0.93f},{0.77f,0.58f,0.89f,0.93f},{0.42f,0.75f,0.51f,1.03f},
            {0.83f,0.46f,0.97f,0.95f},{0.18f,0.67f,0.31f,0.99f},{0.28f,0.7f,0.4f,1.01f},
            {0.02f,0.55f,0.14f,0.85f},{0.1f,0.49f,0.23f,0.91f},{0.14f,0.66f,0.22f,0.94f},*/
    };
    int mWidth;
    int mHeight;
    Bitmap[] mGrassSrc;
    Rect mGrassSrcRect;
    ArrayList<Grass> mGrassList = new ArrayList<>();

    Paint paint = new Paint();
    Path mPath;

    PaintFlagsDrawFilter mDrawFilter;

    public GrassLand(Context context, AttributeSet attrs) {
        super(context, attrs);
        Bitmap grass1 = BitmapFactory.decodeResource(getResources(), R.drawable.doov_grass1);
        Bitmap grass2 = BitmapFactory.decodeResource(getResources(), R.drawable.doov_grass2);
        mGrassSrc = new Bitmap[]{grass1, grass2};
        mGrassSrcRect = new Rect(0, 0, grass1.getWidth(), grass1.getHeight());

        //for test bounds
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mPath = new Path();
        mPath.addCircle(mWidth / 2, mHeight / 2, mWidth / 2-2, Path.Direction.CCW);
        initGrassData();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.setDrawFilter(mDrawFilter);
        canvas.clipPath(mPath, Region.Op.REPLACE);
        for (Grass grass : mGrassList) {
            grass.draw(canvas);
        }
    }

    private void initGrassData() {
        mGrassList.clear();
        Grass grass;

        float[] pos;
        for (int i = 0; i < POSITION.length; i++) {
            pos=POSITION[i];
            grass = new Grass(scaleX(pos[0]), scaleY(pos[1]), scaleX(pos[2]), scaleY(pos[3]));
            mGrassList.add(grass);
        }
    }

    private int getRandomNumber(int min, int max) {
        return new Random().nextInt(max) % (max - min + 1) + min;
    }

    private int getRandomNumber(int min, int max, int ignore, int delta) {
        int num = new Random().nextInt(max) % (max - min + 1) + min;
        if (Math.abs(num - ignore) < delta) {
            num = ignore - delta;
        }
        return num;
    }

    private float check(float num) {
        return (float) (Math.round(num * 100) / 100.00);
    }

    private float checkX(float x) {
        return check(x / mWidth);
    }

    private float checkY(float y) {
        return check(y / mHeight);
    }

    private int scaleX(float x) {
        return (int) (x * mWidth);
    }

    private int scaleY(float y) {
        return (int) (y * mHeight);
    }

    private int[] getGrassWaveRange() {
        int angle1 = getRandomNumber(2, 5);
        int angle2 = getRandomNumber(0, 10, angle1, 3);
        return new int[]{angle1, angle2};
    }

    private long getGrassWaveDuration() {
        return getRandomNumber(800, 1200);
    }

    private static int[] COLOR_ADD = new int[]{
            0,
            0xee0000FF,
            0xff0000FF,
            0xff4444FF,
    };
    private int COLOR_COUNT = COLOR_ADD.length;

    public ColorFilter getGrassColor() {
        int mul = 0xffffffff;
        int index = getRandomNumber(0, COLOR_COUNT - 1);
        Log.d(TAG, "color index:" + index);
        int add = COLOR_ADD[index];
        LightingColorFilter color = new LightingColorFilter(mul, add);
        return color;
    }

    static int mIndex = 0;

    private int getIndex() {
        return (mIndex++) % mGrassSrc.length;
    }

    public class Grass {
        public static final String TAG = "Ct/GrassLand/Grass";
        Rect bounds = new Rect(-1, -1, -1, -1);
        int index;
        int rotate=-1;
        Paint paint;
        ValueAnimator anim;

        public Grass() {
            index = getIndex();
            initPaint(true);
        }

        public Grass(int left, int top, int right, int bottom) {
            index = getIndex();
            bounds.set(left, top, right, bottom);
            initPaint(true);
            initShakeAnim();
        }

        private void initPaint(boolean colorFilter) {
            paint = new Paint();
            if (false) {
                paint.setColorFilter(getGrassColor());
            }
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
        }

        private void initShakeAnim() {
            int[] rotateRange = getGrassWaveRange();
            long duration = getGrassWaveDuration();
            Log.d(TAG, "rotateRange:" + rotateRange[0] + "~" + rotateRange[1] + ",duration:" + duration);
            anim = ValueAnimator.ofInt(rotateRange[0], rotateRange[1]);
            anim.setDuration(duration);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());//new CycleInterpolator(0.2f)
            anim.setRepeatCount(ValueAnimator.INFINITE);
            anim.setRepeatMode(ValueAnimator.REVERSE);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    rotate = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            anim.start();
        }

        public void draw(Canvas canvas) {
            if (bounds == null) {
                Log.e(TAG,"bounds null");
                return;
            }
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            if(rotate!=-1){
                canvas.rotate(rotate, bounds.centerX(), bounds.bottom);
            }
            canvas.drawBitmap(mGrassSrc[index], mGrassSrcRect, bounds, paint);
            //for test
//            canvas.drawRect(bounds.left,bounds.top,bounds.right,bounds.bottom,paint);
            canvas.restore();

        }

        @Override
        public String toString() {
            String str = "not init";
            if (bounds != null) {
                str = "{"
                        +checkX(bounds.left) + "f,"
                        + checkY(bounds.top) + "f,"
                        + checkX(bounds.right) + "f,"
                        + checkY(bounds.bottom) + "f},";
            }
            return str;
        }

    }

    //for test mode to get init data
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int)event.getX();
        int y = (int)event.getY();
//        Log.d(TAG, "touch [" + x + "," + y + "],action=" + action);
        if (action == MotionEvent.ACTION_DOWN) {
            downX = x;
            downY = y;
        }

        //get init grass' position
        touchToPlant(action, (int)x, (int)y);

        return true;
    }

    Grass grass;
    int downX = -1;
    int downY = -1;

    public void setMovePoint(int x, int y) {
        grass.bounds.set(Math.min(downX, x), Math.max(downY, y), Math.max(downX, x), Math.min(downY, y));
    }

    private void touchToPlant(int action, int x, int y) {
        if (action == MotionEvent.ACTION_DOWN) {
            grass = new Grass();
//            setMovePoint(x, y);
            grass.bounds.left=x;
            grass.bounds.top=y;
            mGrassList.add(grass);
            invalidate();
        } else if (action == MotionEvent.ACTION_MOVE) {
//            setMovePoint(x, y);
            grass.bounds.right=x;
            grass.bounds.bottom=y;
            invalidate();
        } else if (action == MotionEvent.ACTION_UP) {
//            setMovePoint(x, y);
            grass.bounds.right=x;
            grass.bounds.bottom=y;
            grass.initShakeAnim();
            invalidate();
            dumpData();
        }
    }

    private void dumpData() {
        String str = "";
        int i = 0;
        for (Grass grass : mGrassList) {
            i++;
            str += grass.toString();
            /*if (i == 3) {
                str += "\n";
                i = 0;
            }*/
        }
        Log.d(TAG, "dump touch positions:\n" + str);
    }

}
