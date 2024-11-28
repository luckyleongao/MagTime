package com.leongao.magtime.utils;

import android.content.res.Resources;

public class ScreenUtil {

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
}
