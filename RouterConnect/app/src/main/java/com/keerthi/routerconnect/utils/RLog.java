package com.keerthi.routerconnect.utils;

import android.util.Log;

/**
 * Created by keerthi on 28/01/17.
 */

public class RLog {
    private static String mTag = "RouterConnect";

    public static void i(String msg){
        Log.i(mTag, msg);
    }

    public static void d(String msg){
        Log.d(mTag, msg);
    }

    public static void e(String msg){
        Log.e(mTag, msg);
    }

    public static void printStackTrace(String s, Exception e) {
        Log.e(mTag + s, e.getMessage());
    }
}
