package com.chengbiao.calculator.common;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.Field;

/**
 * Created by Long on 2018/3/22.
 */

public class MyApplication extends Application {
    private static  Context context;

    @Override
    public void onCreate() {
        context=getApplicationContext();
        super.onCreate();
    }
    public  static Context getContext() {
        return context;
    }

    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
}
