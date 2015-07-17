package com.pangea.test.flowview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class FlowView extends View {

    private int color = Color.rgb(107, 185, 240);

    private Paint pathPaint = new Paint();
    private Path path = new Path();

    private float splits = 100;      // How many splits in horizontal axis
    private float theta = 0;        // Start angle at 0
    private float amplitude = 30;   // Height of the wave
    private float period = 500;     // How many pixels before the wave repeats
    private float[] yValues;        // Using an array to store height values for the wave
    private float offset = 100;

    public FlowView(Context context) {
        super(context);
    }

    public FlowView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FlowView,
                0, 0);

        try {
            color = a.getColor(R.styleable.FlowView_flowColor, Color.rgb(107, 185, 240));
        } finally {
            a.recycle();
        }
    }

    public FlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
        calcWave();
    }

    public float getTheta() {
        return theta;
    }

    public void setTheta(float theta) {
        this.theta = theta;
        calcWave();
    }

    public float getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
        calcWave();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        init(w);
        startAnimation();
    }

    private void init(int width) {
        period = width;

        pathPaint.setAntiAlias(true);
        pathPaint.setColor(color);
        pathPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, pathPaint);
    }

    private void startAnimation() {
        ObjectAnimator thetaAnimator = ObjectAnimator.ofFloat(this, "theta", 0, 360);
        thetaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        thetaAnimator.setInterpolator(new LinearInterpolator());
        thetaAnimator.setDuration(60000L);

        ObjectAnimator offsetAnimator = ObjectAnimator.ofFloat(this, "offset", getHeight(), (amplitude * 2));
        offsetAnimator.setRepeatMode(ValueAnimator.REVERSE);
        offsetAnimator.setInterpolator(new LinearInterpolator());
        offsetAnimator.setDuration(5000L);

        ObjectAnimator amplitudeAnimator = ObjectAnimator.ofFloat(this, "amplitude", 5, amplitude);
        amplitudeAnimator.setRepeatCount(ValueAnimator.INFINITE);
        amplitudeAnimator.setRepeatMode(ValueAnimator.REVERSE);
        amplitudeAnimator.setInterpolator(new LinearInterpolator());
        amplitudeAnimator.setDuration(5000L);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(thetaAnimator, amplitudeAnimator, offsetAnimator);
        set.start();
    }

    private void calcWave() {
        // For every x value, calculate a y value with sine function
        float xSpacing = getWidth() / splits;
        yValues = new float[(int) (getWidth() / xSpacing) + 1];
        float dx = (float) ((Math.PI * 2) / period) * xSpacing;
        float x = theta;
        for (int i = 0; i < yValues.length; i++) {
            yValues[i] = (float) Math.sin(x) * amplitude;
            x += dx;
        }

        path.reset();
        path.setFillType(Path.FillType.WINDING);
        path.moveTo(0, yValues[0] + offset);
        for (int xValue = 0; xValue < yValues.length - 1; xValue++) {
            path.lineTo((xValue+1) * xSpacing, yValues[xValue+1] + offset);
        }
        path.lineTo(getWidth(), getHeight());
        path.lineTo(0, getHeight());
        path.lineTo(0, yValues[0] + offset);

        path.close();

        invalidate();
    }


}
