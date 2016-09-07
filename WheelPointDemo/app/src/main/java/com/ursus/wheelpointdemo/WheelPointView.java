package com.ursus.wheelpointdemo;

import android.content.Context;
import android.content.res.TypedArray;
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
    private float mPointRadius;
    private float mPointGap;
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
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.wheelPointView, defStyleAttr, 0);
        mPointRadius = typedArray.getDimension(R.styleable.wheelPointView_point_radius, 0.0f);
        mPointGap = typedArray.getDimension(R.styleable.wheelPointView_point_gap, 0.0f);
        mPointCount = typedArray.getInt(R.styleable.wheelPointView_point_count, 0);
        mMode = SCROLL_MODE_NORMAL;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        float wantWidth = mPointCount * (mPointGap + mPointRadius * 2);
        float wantHeight = mPointRadius * 2.5f;

        float wScale = 1;
        float hScale = 1;
        float scale;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < wantHeight) {
            hScale = (float) widthSize / wantWidth;
        }
        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < wantHeight) {
            wScale = (float) heightSize / wantHeight;
        }

        scale = Math.min(wScale, hScale);

        setMeasuredDimension(
                resolveSizeAndState((int) (wantWidth * scale), widthMeasureSpec, 0),
                resolveSizeAndState((int) (wantHeight * scale), heightMeasureSpec, 0)
        );

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        float wantWidth = mPointCount * (mPointGap + mPointRadius * 2);
        if (wantWidth > mWidth) {
            float scale = (float) mWidth / wantWidth;
            mPointRadius = mPointRadius * scale;
            mPointGap = mPointGap * scale;
        }
        mRealWidth = (int) (mPointCount * (mPointRadius * 2 + mPointGap));
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
                drawNormal(canvas, xPos);
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

    public int getMode() {
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
