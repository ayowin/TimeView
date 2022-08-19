package com.wz.timeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class TimeView extends View {

    private Context mContext;
    private Paint mPaint;
    private float mSecondDegree;
    private float mMinDegree;
    private float mHourDegree;
    private boolean mIsNight;
    private float mTotalSecond;

    /* 外边框 */
    private int borderColor;
    private float borderWidth;
    /* 圆背景 */
    private int circleBackground;
    /* 短刻度 */
    private int minScaleColor;
    private float minScaleLength;
    /* 中刻度 */
    private int midScaleColor;
    private float midScaleLength;
    /* 长刻度 */
    private int maxScaleColor;
    private float maxScaleLength;
    /* 数字 */
    private boolean isDrawText;
    private float textDistanceToBorder;
    private int textColor;
    private float textSize;
    /* 秒针 */
    private int secondPointerColor;
    private float secondPointerLength;
    private float secondPointerSize;
    /* 分针 */
    private int minPointerColor;
    private float minPointerLength;
    private float minPointerSize;
    /* 时针 */
    private int hourPointerColor;
    private float hourPointerLength;
    private float hourPointerSize;
    /* 中心 */
    private int centerPointColor;
    private float centerPointRadius;

    public TimeView(Context context) {
        super(context);
        this.mContext = context;
        initPainter();
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(context, attrs);
        initPainter();
    }

    /**
     * 初始化各参数
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeView);

        borderColor = typedArray.getColor(R.styleable.TimeView_borderColor, Color.BLACK);
        borderWidth = typedArray.getDimension(R.styleable.TimeView_borderWidth,
                SizeUtils.dp2px(context, 1));

        circleBackground = typedArray.getColor(R.styleable.TimeView_circleBackground, Color.WHITE);

        minScaleColor = typedArray.getColor(R.styleable.TimeView_minScaleColor, Color.BLACK);
        minScaleLength = typedArray.getDimension(R.styleable.TimeView_minScaleLength,
                SizeUtils.dp2px(context, 7));

        midScaleColor = typedArray.getColor(R.styleable.TimeView_midScaleColor, Color.BLACK);
        midScaleLength = typedArray.getDimension(R.styleable.TimeView_midScaleLength,
                SizeUtils.dp2px(context, 12));

        maxScaleColor = typedArray.getColor(R.styleable.TimeView_maxScaleColor, Color.BLACK);
        maxScaleLength = typedArray.getDimension(R.styleable.TimeView_maxScaleLength,
                SizeUtils.dp2px(context, 14));

        isDrawText = typedArray.getBoolean(R.styleable.TimeView_isDrawText, true);
        textDistanceToBorder = typedArray.getDimension(R.styleable.TimeView_textDistanceToBorder,
                SizeUtils.dp2px(context, 40));
        textColor = typedArray.getColor(R.styleable.TimeView_textColor, Color.BLACK);
        textSize = typedArray.getDimension(R.styleable.TimeView_textSize,
                SizeUtils.dp2px(context, 15));

        secondPointerColor = typedArray.getColor(R.styleable.TimeView_secondPointerColor, Color.RED);
        secondPointerLength = typedArray.getDimension(R.styleable.TimeView_secondPointerLength,
                SizeUtils.dp2px(context, getWidth() / 3 * 2 / 3));
        secondPointerSize = typedArray.getDimension(R.styleable.TimeView_secondPointerSize,
                SizeUtils.dp2px(context, 1));

        minPointerColor = typedArray.getColor(R.styleable.TimeView_minPointerColor, Color.BLACK);
        minPointerLength = typedArray.getDimension(R.styleable.TimeView_minPointerLength,
                SizeUtils.dp2px(context, getWidth() / 3 / 2));
        minPointerSize = typedArray.getDimension(R.styleable.TimeView_minPointerSize,
                SizeUtils.dp2px(context, 3));

        hourPointerColor = typedArray.getColor(R.styleable.TimeView_hourPointerColor, Color.BLACK);
        hourPointerLength = typedArray.getDimension(R.styleable.TimeView_hourPointerLength,
                SizeUtils.dp2px(context, getWidth() / 3 / 3));
        hourPointerSize = typedArray.getDimension(R.styleable.TimeView_hourPointerSize,
                SizeUtils.dp2px(context, 5));

        centerPointColor = typedArray.getColor(R.styleable.TimeView_centerPointColor, Color.BLACK);
        centerPointRadius = typedArray.getDimension(R.styleable.TimeView_centerPointRadius,
                SizeUtils.dp2px(context, 1));

        typedArray.recycle();
    }

    /**
     * 初始化画笔
     */
    private void initPainter() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
    }

    /**
     * 数字
     *
     * @param canvas
     * @param degree
     * @param text
     * @param paint
     */
    private void drawNum(Canvas canvas, float outterRadius, int degree, String text, Paint paint) {
        Rect textBound = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBound);
        canvas.rotate(degree);
        canvas.translate(0, textDistanceToBorder - outterRadius);
        canvas.rotate(-degree);
        canvas.drawText(text, -textBound.width() / 2,
                textBound.height() / 2, paint);
        canvas.rotate(degree);
        canvas.translate(0, outterRadius - textDistanceToBorder);
        canvas.rotate(-degree);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    /**
     * 测量控件尺寸
     *
     * @param measureSpec
     * @return
     */
    private int measureWidth(int measureSpec) {
        int result;
        int specSize = MeasureSpec.getSize(measureSpec);
        int specMode = MeasureSpec.getMode(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = SizeUtils.dp2px(mContext, 300);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * 测量控件尺寸
     *
     * @param measureSpec
     * @return
     */
    private int measureHeight(int measureSpec) {
        int result;
        int specSize = MeasureSpec.getSize(measureSpec);
        int specMode = MeasureSpec.getMode(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = SizeUtils.dp2px(mContext, 300);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        float outterRadius = width > height ? height/2.2f : width/2.2f;

        /* 外圆边界 */
        mPaint.setColor(borderColor);
        mPaint.setStrokeWidth(borderWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(width / 2, height / 2, outterRadius , mPaint);

        /* 圆背景 */
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(circleBackground);
        canvas.drawCircle(width / 2, height / 2, outterRadius - borderWidth/2, mPaint);

        /* 圆心 */
        mPaint.setColor(centerPointColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, height / 2, centerPointRadius, mPaint);

        /* 刻度 */
        canvas.save();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
        for (int i = 0; i < 360; i++) {
            if (i % 30 == 0) {
                /* 长刻度 */
                mPaint.setColor(maxScaleColor);
                canvas.drawLine(width / 2, height / 2 - (outterRadius - borderWidth/2),
                        width / 2, height / 2 - (outterRadius - borderWidth/2) + maxScaleLength, mPaint);
            } else if (i % 6 == 0) {
                /* 中刻度 */
                mPaint.setColor(midScaleColor);
                canvas.drawLine(width / 2, height / 2 - (outterRadius - borderWidth/2),
                        width / 2, height / 2 - (outterRadius -borderWidth/2) + midScaleLength, mPaint);
            } else {
                /* 短刻度 */
                mPaint.setColor(minScaleColor);
                canvas.drawLine(width / 2, height / 2 - (outterRadius - borderWidth/2),
                        width / 2, height / 2 - (outterRadius - borderWidth/2) + minScaleLength, mPaint);
            }
            canvas.rotate(1, width / 2, height / 2);
        }
        canvas.restore();

        /* 数字 */
        if(isDrawText){
            canvas.save();
            mPaint.setColor(textColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(1);
            mPaint.setTextSize(textSize);
            canvas.translate(width / 2, height / 2);
            for (int i = 1; i <= 12; i++) {
                drawNum(canvas, outterRadius,i * 30, String.valueOf(i), mPaint);
            }
            canvas.restore();
        }

        /* 时针 */
        canvas.save();
        mPaint.setColor(hourPointerColor);
        mPaint.setStrokeWidth(hourPointerSize);
        canvas.rotate(mHourDegree, width / 2, height / 2);
        canvas.drawLine(width / 2, height / 2, width / 2,
                height / 2 - hourPointerLength, mPaint);
        canvas.restore();

        /* 分针 */
        canvas.save();
        mPaint.setColor(minPointerColor);
        mPaint.setStrokeWidth(minPointerSize);
        canvas.rotate(mMinDegree, width / 2, height / 2);
        canvas.drawLine(width / 2, height / 2, width / 2,
                height / 2 - minPointerLength, mPaint);
        canvas.restore();

        /* 秒针 */
        canvas.save();
        mPaint.setColor(secondPointerColor);
        mPaint.setStrokeWidth(secondPointerSize);
        canvas.rotate(mSecondDegree, width / 2, height / 2);
        canvas.drawLine(width / 2, height / 2, width / 2,
                height / 2 - secondPointerLength, mPaint);
        canvas.restore();
    }

    /**
     * 设置时间
     *
     * @param hour
     * @param min
     * @param second
     */
    public void setTime(int hour, int min, int second) throws InvalidTimeException {
        if (hour >= 24 || hour < 0 || min >= 60 || min < 0 || second >= 60 || second < 0) {
            throw new InvalidTimeException("时间不合法");
        }

        if (hour >= 12) {
            mIsNight = true;
            mHourDegree = (hour + min * 1.0f/60f + second * 1.0f/3600f - 12)*30f;
        } else {
            mIsNight = false;
            mHourDegree = (hour + min * 1.0f/60f + second * 1.0f/3600f)*30f;
        }
        mMinDegree = (min + second * 1.0f/60f) *6f;
        mSecondDegree = second * 6f;
        invalidate();
    }

    /**
     * 获取总秒数
     *
     * @return
     */
    public float getTotalSecond() {
        if (mIsNight) {
            mTotalSecond = mHourDegree * 120 + 12 * 3600;
            return mTotalSecond;
        } else {
            mTotalSecond = mHourDegree * 120;
            return mTotalSecond;
        }
    }

    /**
     * 获取hour
     *
     * @return
     */
    public int getHour() {
        return (int) (getTotalSecond() / 3600);
    }

    /**
     * 获取Min
     *
     * @return
     */
    public int getMin() {
        return (int) ((getTotalSecond() - getHour() * 3600) / 60);
    }

    /**
     * 获取Second
     *
     * @return
     */
    public int getSecond() {
        return (int) (getTotalSecond() - getHour() * 3600 - getMin() * 60);
    }

    /**
     * 尺寸转换工具类
     */
    private static class SizeUtils {
        public static int dp2px(Context context, float dp) {
            final float density = context.getResources().getDisplayMetrics().density;
            return (int) (dp * density + 0.5);
        }

        public static int px2dp(Context context, float px) {
            final float density = context.getResources().getDisplayMetrics().density;
            return (int) (px / density + 0.5);
        }
    }

    /**
     * 无效时间异常类
     */
    public static class InvalidTimeException extends Exception{
        public InvalidTimeException(String message) {
            super(message);
        }
    }

}