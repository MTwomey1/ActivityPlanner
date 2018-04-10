package com.example.mark.activityplanner.model;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

/**
 * Created by Mark on 19/03/2018.
 */

public class ChatLayout extends RelativeLayout {



    public ChatLayout(Context context) {
        super(context);
    }

    public ChatLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public ChatLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final float adjustVal = (float) 12.667;

        if(getChildCount()<3)
            return;

        int imageViewWidth = getChildAt(0).getMeasuredWidth();
        int timeWidth = getChildAt(1).getMeasuredWidth();
        int messageHeight = getChildAt(2).getMeasuredHeight();
        int messageWidth = getChildAt(2).getMeasuredWidth();

        int layoutWidth = (int) (imageViewWidth + timeWidth + messageWidth + convertDpToPixel(adjustVal, getContext()));

        setMeasuredDimension(layoutWidth, messageHeight);
    }

    /*
    This method converts dp unit to equivalent pixels, depending on device density.
    */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

}

