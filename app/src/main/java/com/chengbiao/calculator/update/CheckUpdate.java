package com.chengbiao.calculator.update;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chengbiao.calculator.R;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 项目名称：BingWallPaper
 * Created by Long on 2018/5/11.
 * 修改时间：2018/5/11 9:24
 */
public class CheckUpdate {
    /**
     * 版本信息提示
     * @param mContext
     * **/
    public static void checkUpdate(final Context mContext) {
        final ProgressDialog progressBar=new ProgressDialog(mContext);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        progressBar.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressBar.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressBar.setTitle("检查更新中...");
        progressBar.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getResources().getString(R.string.baseUrl)) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();
        UpdateRequest_Interface request = retrofit.create(UpdateRequest_Interface.class);
        Call<UpdateInfor> call = request.getCall();
        call.enqueue(new Callback<UpdateInfor>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<UpdateInfor> call, Response<UpdateInfor> response) {
                // 步骤 ：处理返回的数据结果
                //  LogUtils.i( "onResponse: "+response.body().toString());
                if(response.body()!=null){
                    if(response.body().getVersion()>getLocalVersion(mContext)) {
                        //  LogUtils.i("updateonClick: 可更新");
                        MyCheckVersionLib.checkUpdate(mContext, response.body(),progressBar);
                    }
                    else {
                        //  LogUtils.i( "updateonClick: 暂无更新");
                        progressBar.dismiss();
                        Toast.makeText(mContext, "暂无更新！", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            //请求失败时回调
            @Override
            public void onFailure(Call<UpdateInfor> call, Throwable throwable) {
                // LogUtils.i( " call.enqueue onFailure: "+throwable);
            }
        });
    }
    public static void showAboutNoticeDialog(final Context mContext) {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(mContext, R.layout.dialog_about_infor, null);
        Button button=view.findViewById(R.id.check_update);
        TextView version=view.findViewById(R.id.version);
        try {
            version.setText("版本号："+mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName+"  ");
        } catch (PackageManager.NameNotFoundException e) {
            version.setText("null");
            e.printStackTrace();
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressBar=new ProgressDialog(mContext);
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
                progressBar.setCancelable(false);// 设置是否可以通过点击Back键取消
                progressBar.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                progressBar.setTitle("检查更新中...");
                progressBar.show();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(mContext.getResources().getString(R.string.baseUrl)) // 设置 网络请求 Url
                        .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                        .build();
                UpdateRequest_Interface request = retrofit.create(UpdateRequest_Interface.class);
                Call<UpdateInfor> call = request.getCall();
                call.enqueue(new Callback<UpdateInfor>() {
                    //请求成功时回调
                    @Override
                    public void onResponse(Call<UpdateInfor> call, Response<UpdateInfor> response) {
                        // 步骤 ：处理返回的数据结果
                        //  LogUtils.i( "onResponse: "+response.body().toString());
                        if(response.body()!=null){
                            if(response.body().getVersion()>getLocalVersion(mContext)) {
                                //  LogUtils.i("updateonClick: 可更新");
                                MyCheckVersionLib.checkUpdate(mContext, response.body(),progressBar);
                            }
                            else {
                                //  LogUtils.i( "updateonClick: 暂无更新");
                                progressBar.dismiss();
                                dialog.dismiss();
                                Toast.makeText(mContext, "暂无更新！", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    //请求失败时回调
                    @Override
                    public void onFailure(Call<UpdateInfor> call, Throwable throwable) {
                        // LogUtils.i( " call.enqueue onFailure: "+throwable);
                    }
                });
            }
        });
        dialog.setView(view,0,0,0,0);// 设置边距为0,保证在2.x的版本上运行没问题

        dialog.show();
    }
    /**
     * 获取应用程序版本（versionName）
     *
     * @return 当前应用的版本号
     */

    private static double getLocalVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            //Log.e(TAG, "获取应用程序版本失败，原因：" + e.getMessage());
            return 0.0;
        }

        return Double.valueOf(info.versionName);
    }

    /****
     * 获取服务器返回的更新信息
     * @param json
     * @return
     */
    public static UpdateInfor getUpdateInfor(String json){

        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(new StringReader(json));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if("string".equals(tagName))
                        {
                            json=parser.nextText();
                            Log.i("json", "onRequestVersionSuccess: "+json );
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        UpdateInfor updateInfor = gson.fromJson(json , UpdateInfor.class);
        return updateInfor;
    }
}
