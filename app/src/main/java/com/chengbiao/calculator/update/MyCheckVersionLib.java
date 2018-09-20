package com.chengbiao.calculator.update;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.allenliu.versionchecklib.callback.APKDownloadListener;
import com.allenliu.versionchecklib.core.http.HttpRequestMethod;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.NotificationBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.chengbiao.calculator.R;
import com.chengbiao.calculator.common.MyApplication;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * 项目名称：BingWallPaper
 * Created by Long on 2018/4/12.
 * 修改时间：2018/4/12 10:58
 */


public class MyCheckVersionLib {
private static String TAG="MyCheckVersionLib";
   private static NotificationManager notificationManager;
    /*****
     * 上下文   更新信息，url，title，content
     * @param mContext
     * @param updateInfor
     */
    public static void checkUpdate(Context mContext, UpdateInfor updateInfor, final ProgressDialog progressDialog){
        StringBuffer stringBuffer=new StringBuffer();
        final NotificationBuilder notificationBuilder=NotificationBuilder.create();
        notificationBuilder
                .setRingtone(true)
                .setIcon(R.drawable.nav_download)
                .setTicker("下载通知")
                .setContentTitle("下载中")
                .setContentText("请稍后");
        notificationManager=(NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        DownloadBuilder builder= AllenVersionChecker
                .getInstance()
                .downloadOnly(UIData.create().setDownloadUrl(updateInfor.getDownloadUrl()).setTitle(updateInfor.getTitle()).setContent(updateInfor.getContent()))
                .setSilentDownload(false)//静默下载默认false
                .setForceRedownload(true)//下载忽略本地缓存 默认false
                .setShowDownloadingDialog(true)//是否显示下载对话框 默认true
                .setShowNotification(true)//是否显示通知栏 默认true
                .setNotificationBuilder(notificationBuilder)//自定义通知栏
                .setShowDownloadFailDialog(true)// 默认true // 是否显示失败对话框
                .setDownloadAPKPath(MyApplication.getApplication().getCacheDir()+File.separator+"download")//自定义下载路径 默认 /storage/emulated/0/AllenVersionPath/
                .setApkDownloadListener(new APKDownloadListener() {
                    @Override
                    public void onDownloading(int progress) {
                        Log.i(TAG, "onDownloading: "+progress);
//                        notificationBuilder.setContentText(String.valueOf(progress));
//                        notificationBuilder.notify();
                        // notificationManager.notifyAll();
                    }

                    @Override
                    public void onDownloadSuccess(File file) {

                    }

                    @Override
                    public void onDownloadFail() {

                    }
                })//可以设置下载监听
                ;// AllenVersionChecker.getInstance().cancelAllMission(this);//取消任务
             builder. excuteMission(mContext);
        try {
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        progressDialog.dismiss();
    }

    public static void checkUpdatehttp(final Context mContext ){
        final DownloadBuilder builder=  AllenVersionChecker.getInstance()
                .requestVersion()
                .setRequestMethod(HttpRequestMethod.GET)
                .setRequestUrl(mContext.getResources().getString(R.string.updateinforurl))
                .request(new RequestVersionListener() {
                    @Nullable
                    @Override
                    public UIData onRequestVersionSuccess(String result) {
                        //拿到服务器返回的数据，解析，拿到downloadUrl和一些其他的UI数据
                        XmlPullParser parser = Xml.newPullParser();
                        String json="";
                        try {
                            parser.setInput(new StringReader(result));
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
                        UIData uiData = UIData
                                .create();
                         if(getLocalVersion(mContext)<updateInfor.getVersion()) {
                             Log.i("json", "onRequestVersionSuccess: "+updateInfor.toString());
                               uiData .setDownloadUrl(updateInfor.getDownloadUrl())
                                     .setTitle(updateInfor.getTitle())
                                     .setContent(updateInfor.getContent());
                             //放一些其他的UI参数，拿到后面自定义界面使用
                            // uiData.getVersionBundle().putString("key", "your value");
                         }
                         else
                         {
                             Toast.makeText(mContext,"亲，暂时没有更新哦！",Toast.LENGTH_SHORT).show();
                              return null;
                             //                             uiData
//                                     .setTitle("亲，暂时没有更新哦！")
//                                     .setContent("我们将继续努力寻找bug解决bug");
                         }
                        return uiData;
                    }

                    @Override
                    public void onRequestVersionFailure(String message) {
                        Log.i("connect fail", "onRequestVersionFailure: "+message);

                    }
                });
                builder.excuteMission(mContext);







//                AllenVersionChecker
//                .getInstance()
//                .downloadOnly(UIData.create().setDownloadUrl(UpDateInfo[0]).setTitle(UpDateInfo[1]).setContent(UpDateInfo[2]))
//                .setSilentDownload(false)//静默下载默认false
//                .setForceRedownload(true)//下载忽略本地缓存 默认false
//                .setShowDownloadingDialog(true)//是否显示下载对话框 默认true
//                .setShowNotification(true)//是否显示通知栏 默认true
//                .setNotificationBuilder(
//                        notificationBuilder
//                                .setRingtone(true)
//                                .setIcon(R.drawable.nav_download)
//                                .setTicker("下载通知")
//                                .setContentTitle("下载中")
//                                .setContentText("请稍后"))//自定义通知栏
//                .setShowDownloadFailDialog(true)// 默认true // 是否显示失败对话框
//                //  .setDownloadAPKPath("")//自定义下载路径 默认 /storage/emulated/0/AllenVersionPath/
//                .setApkDownloadListener(new APKDownloadListener() {
//                    @Override
//                    public void onDownloading(int progress) {
////                        ALog.e("111111111111111111111+  "+progress+"   "+notificationBuilder.getContentText());
////                        notificationBuilder.setContentText(String.valueOf(progress));
//                    }
//
//                    @Override
//                    public void onDownloadSuccess(File file) {
//
//                    }
//
//                    @Override
//                    public void onDownloadFail() {
//
//                    }
//                })//可以设置下载监听
//                ;// AllenVersionChecker.getInstance().cancelAllMission(this);//取消任务
//        builder. excuteMission(mContext);
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
}
