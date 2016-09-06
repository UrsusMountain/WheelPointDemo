package com.ursus.wheelpointdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.FloatRange;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class WheelPointView extends View {

    public final static int SCROLL_MODE_NORMAL = 0;
    public final static int SCROLL_MODE_VISCOSITY = 1;

    private int mWidth;
    private int mHeight;
    private int mPointRadius;
    private int mPointGap;
    private int mPointCount;
    private int mRealWidth;

    private int mSelectedPoint = 0;
    private float mMoveScale = 0;

    private Paint mPaint;

    private int mMode;

    public WheelPointView(Context context) {
        this(context, null);
    }

    public WheelPointView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelPointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPointCount = 5;
        mMode = SCROLL_MODE_NORMAL;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        mPointGap = Math.min(mWidth / (mPointCount * 2), mHeight);
        mPointRadius = mPointGap / 2;
        mRealWidth = mPointCount * (mPointRadius * 2 + mPointGap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPaint();
        canvas.translate(0, mHeight / 2);

        for (int i = 0; i < mPointCount; i++) {
            float x = (float) ((2 * mPointRadius + mPointGap) * (i + 0.5));
            canvas.drawCircle(x, 0, mPointRadius, mPaint);
        }

        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        float xPos = (float) ((2 * mPointRadius + mPointGap) * (mSelectedPoint + 0.5 + mMoveScale));
        if (xPos > mRealWidth) {
            xPos = xPos - mRealWidth;
        }

        switch (mMode) {
            case SCROLL_MODE_NORMAL:
                drawNormal(canvas,xPos);
                break;
            case SCROLL_MODE_VISCOSITY:
                drawViscosity(canvas, xPos);
                break;
        }
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(4);
    }

    private void drawNormal(Canvas canvas, float x) {
        canvas.drawCircle(x, 0, mPointRadius, mPaint);
    }

    private void drawViscosity(Canvas canvas, float x) {
        float fx = (float) (4 * Math.pow(mMoveScale, 2) - 4 * mMoveScale + 1);
        canvas.drawCircle(x, 0, mPointRadius * (fx), mPaint);
        Path path = new Path();
        float startX = (float) ((2 * mPointRadius + mPointGap) * (mSelectedPoint + 0.5));
        float startY = (float) (-mPointRadius * (1 - (mMoveScale * 0.9)));
        float endX = (float) ((2 * mPointRadius + mPointGap) * (mSelectedPoint + 0.5 + mMoveScale));
        float endY = -mPointRadius * (fx);
        float controlX = (startX + endX) / 2;
        float controlY = 0;
        path.moveTo(startX, startY);
        path.quadTo(controlX, controlY, endX, endY);
        path.lineTo(endX, -endY);
        path.quadTo(controlX, controlY, startX, -startY);
        path.close();
        canvas.drawCircle(startX, 0, (float) (mPointRadius * (1 - (mMoveScale * 0.9))), mPaint);
        canvas.drawPath(path, mPaint);
    }

    protected void setMoveScale(@FloatRange(from = 0.0, to = 1.0) float scale, int position) {
        mMoveScale = scale;
        mSelectedPoint = position;
        invalidate();
    }

    public void setMode(int mMode) {
        this.mMode = mMode;
    }

    public int getMode(){
        return mMode;
    }

    public void bindViewPager(ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i("Point", position + "__" + positionOffset + "__" + positionOffsetPixels);
                int selectedPosition = position;
                if (position > mPointCount - 1) {
                    selectedPosition = position % mPointCount;
                }
                setMoveScale(positionOffset, selectedPosition);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
