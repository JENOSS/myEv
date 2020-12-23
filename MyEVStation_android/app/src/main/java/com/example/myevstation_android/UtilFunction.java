package com.example.myevstation_android;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class UtilFunction {

    public static int dpToPx(int dp){
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int getScreenWidth(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.widthPixels;
    }
}
