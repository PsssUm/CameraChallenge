package com.evgenyvyaz.cinaytaren.utils.view;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;

import com.evgenyvyaz.cinaytaren.utils.ClassElements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IconsView extends View {
    private final Paint text;
    private Map<String, Integer> elements = new HashMap<>();

    private Integer ANGLE = 40;

    Paint paint;

    int displayHeight;
    int displayWidth;

    Double currentDegrees;
    List<Pair<String, Map<String, Double>>> types;
    private int framesPerSecond = 60;

    public IconsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        ClassElements classElements = new ClassElements();
        elements = classElements.getMapElements();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        text = new Paint(Paint.ANTI_ALIAS_FLAG);
        text.setTextSize(50);
        text.setColor(Color.WHITE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        displayHeight = displaymetrics.heightPixels;
        displayWidth = displaymetrics.widthPixels;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawARGB(0, 0, displayWidth, displayHeight);

        if (types != null && currentDegrees != null) {

            for (Pair<String, Map<String, Double>> organization : types) {

                Double posX = (organization.second.get("angle") - currentDegrees);
                if (organization.second.get("angle") < ANGLE) {
                    if (posX < -360 + ANGLE) {
                        posX = 360 - Math.abs(posX);
                    }
                }
                if (organization.second.get("angle") > 360 - ANGLE) {
                    if (posX > 360 - ANGLE) {
                        posX = posX - 360;
                    }
                }
                Double finalPosX = posX * (displayWidth / ANGLE) + displayWidth / 2 - 128;

                Bitmap bitmapSource = BitmapFactory.decodeResource(getResources(), elements.get(organization.first));
                canvas.drawBitmap(bitmapSource, finalPosX.intValue(), 150, paint);
                canvas.drawText(organization.second.get("distance") + " м.", finalPosX.intValue(), 550, text);
            }
        }
        this.postInvalidateDelayed(1000 / framesPerSecond);
    }

    public void setTypes(List<Pair<String, Map<String, Double>>> organizations) {
        this.types = organizations;
    }

    public void setDegrees(Double currentDegrees) {
        this.currentDegrees = currentDegrees;
    }
}