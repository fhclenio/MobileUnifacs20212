package com.unifacs.projetoavaliacao;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.GnssStatus;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CircleView extends View {
    private int mXc, mYc;
    private int mRadius;
    private int mCircleColor;
    private int mTextColor;
    private String mText;
    private GnssStatus statusAtual;
    private int viewWidth;
    private int viewHeight;
    private int r;

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircleView, 0, 0);
        try {
            mXc = a.getInt(R.styleable.CircleView_Xc, 0);
            mYc = a.getInt(R.styleable.CircleView_Yc, 0);
            mRadius = a.getInt(R.styleable.CircleView_Radius, 0);
            mCircleColor = a.getInt(R.styleable.CircleView_CircleColor, 0);
            mTextColor = a.getInt(R.styleable.CircleView_TextColor, 0);
            mText = a.getString(R.styleable.CircleView_Text);
        } finally {
            a.recycle();
        }
    }

    public void AtualizaStatus(GnssStatus status) {
        this.statusAtual = status;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();

        this.viewWidth = this.getMeasuredWidth();
        this.viewHeight = this.getMeasuredHeight();

        if (viewWidth < viewHeight)
            r = (int) (viewWidth / 2 * 0.9);
        else
            r = (int) (viewHeight / 2 * 0.9);

        mXc = viewWidth;
        mYc = viewHeight;

        paint.setStyle(Paint.Style.FILL);

        int radius = r;
        paint.setColor(Color.rgb(147, 0, 47));
        paint.setAlpha(210);
        canvas.drawCircle(ValorXc(0), ValorYc(0), radius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);

        canvas.drawCircle(ValorXc(0), ValorYc(0), radius, paint);

        radius = (int) (radius * Math.cos(Math.toRadians(30)));
        canvas.drawCircle(ValorXc(0), ValorYc(0), radius, paint);

        radius = (int) (radius * Math.cos(Math.toRadians(45)));
        canvas.drawCircle(ValorXc(0), ValorYc(0), radius, paint);

        radius = (int) (radius * Math.cos(Math.toRadians(60)));
        canvas.drawCircle(ValorXc(0), ValorYc(0), radius, paint);

        radius = (int) (radius * Math.cos(Math.toRadians(75)));
        canvas.drawCircle(ValorXc(0), ValorYc(0), radius, paint);

        canvas.drawLine(ValorXc(0), ValorYc(-r), ValorXc(0), ValorYc(r), paint);
        canvas.drawLine(ValorXc(-r), ValorYc(0), ValorXc(r), ValorYc(0), paint);

        paint.setColor(Color.CYAN);
        paint.setStyle(Paint.Style.FILL);
        if (statusAtual != null) {
            for (int i = 0; i < statusAtual.getSatelliteCount(); i++) {
                float az = statusAtual.getAzimuthDegrees(i);
                float el = statusAtual.getElevationDegrees(i);
                float x = (float) (r * Math.cos(Math.toRadians(el)) * Math.sin(Math.toRadians(az)));
                float y = (float) (r * Math.cos(Math.toRadians(el)) * Math.cos(Math.toRadians(az)));

                int radiusSat = 0;
                if (statusAtual.getCn0DbHz(i) <= 6.3f) {
                    paint.setColor(Color.rgb(0, 247, 173));
                    radiusSat = 20;
                } else if (statusAtual.getCn0DbHz(i) <= 12.6f) {
                    paint.setColor(Color.rgb(0, 214, 247));
                    radiusSat = 30;
                } else if (statusAtual.getCn0DbHz(i) <= 18.9f) {
                    paint.setColor(Color.rgb(0, 91, 247));
                    radiusSat = 40;
                } else if (statusAtual.getCn0DbHz(i) < 31.5f) {
                    paint.setColor(Color.rgb(0, 16, 247));
                    radiusSat = 50;
                } else {
                    paint.setColor(Color.rgb(70, 0, 247));
                    radiusSat = 60;
                }
                paint.setAlpha(230);
                canvas.drawCircle(ValorXc(x), ValorYc(y), radiusSat, paint);
                if(statusAtual.usedInFix(i)){
                    paint.setColor(Color.rgb(255, 166, 0));
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(ValorXc(x), ValorYc(y), radiusSat + 1, paint);
                }
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.WHITE);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setFakeBoldText(true);
                paint.setTextSize(30);

                String satID = "S:" + statusAtual.getSvid(i) + " - C:" + statusAtual.getConstellationType(i);
                canvas.drawText(satID, ValorXc(x), ValorYc(y) + 30 + radiusSat, paint);
            }
        }
    }

    private int ValorXc(double X) {
        return (int) (X + viewWidth / 2);
    }

    private int ValorYc(double Y) {
        return (int) ((viewHeight / 2) - Y);
    }
}
