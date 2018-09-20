package com.chengbiao.calculator.common;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.chengbiao.calculator.LoginActivity;
import com.chengbiao.calculator.MainActivity;
import com.chengbiao.calculator.RegisterActivity;

import org.litepal.LitePal;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by Long on 2018/3/22.
 */

public class MyApplication extends Application {
    private static  Context context;
    private static String cachePath;
    //内存存储文件
    private static String fileDir;
    //私有文件
    private static MyApplication application;
    private LoginActivity loginActivity;
    private RegisterActivity registerActivity;
    private MainActivity mainActivity;
    public static MyApplication getApplication() {
        return application;
    }
    @Override
    public void onCreate() {
        if(application==null) {
            application = this;
        }
        LitePal.initialize(this);
        context=getApplicationContext();
        cachePath=Environment.getExternalStorageDirectory()+ File.separator+"ChengBiaoCache";
        fileDir=getFilesDir().getAbsolutePath()+File.separator+"model";
        super.onCreate();
    }
    public static String getFileDir(){
        if(new File(fileDir).exists()){
            return fileDir;
        }
        else
        {
            new File(fileDir).mkdirs();
            return fileDir;
        }

    }

    public  static Context getContext() {
        return context;
    }

    public static String getCachePath() {
        return cachePath;
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


    public LoginActivity getLoginActivity() {
        return loginActivity;
    }

    public void setLoginActivity(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    public RegisterActivity getRegisterActivity() {
        return registerActivity;
    }

    public void setRegisterActivity(RegisterActivity registerActivity) {
        this.registerActivity = registerActivity;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}
