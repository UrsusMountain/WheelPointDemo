package com.ursus.wheelpointdemo;

import android.content.Context;

/**
 * Author:  ursus
 * Date:    16/9/7
 * Function:
 */
public class SizeUtils {

    /**
     * dp转px
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


}
