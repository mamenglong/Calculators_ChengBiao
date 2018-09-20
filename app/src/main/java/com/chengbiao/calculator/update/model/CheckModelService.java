package com.chengbiao.calculator.update.model;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.chengbiao.calculator.MainActivity;
import com.chengbiao.calculator.common.Common;
import com.chengbiao.calculator.common.MyApplication;
import com.chengbiao.calculator.ftp.FTP;
import com.chengbiao.calculator.ftp.MyFTP;
import com.chengbiao.calculator.utils.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class CheckModelService extends Service {
    public static final String TAG = "CheckModelService";
    public static String remotePath="/gh/Model/update.txt";
    public static String localPath= MyApplication.getFileDir();
    public static String exlocalPath=MyApplication.getCachePath()+"/Model/";
    private DownloadListener listener=new DownloadListener() {
        @Override
        public void onFinish() {
            Toast.makeText(MyApplication.getContext(),"onFinish",Toast.LENGTH_SHORT).show();
            File update=new File( localPath+File.separator+"update.txt");
            if(!update.exists())
            {
                update.mkdirs();
            }
            File reupdate=new File(exlocalPath+"reupdate.txt");

            InputStreamReader updateReader = null; // 建立一个输入流对象reader
            InputStreamReader reupdateReader = null; // 建立一个输入流对象reader
            try {
                updateReader = new InputStreamReader( new FileInputStream(update));
                // 创建对象
                reupdateReader = new InputStreamReader( new FileInputStream(reupdate));
                BufferedReader br = new BufferedReader(updateReader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                BufferedReader rebr = new BufferedReader(reupdateReader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                String updateline = "";
                String reupdateline="";
                updateline = br.readLine();
                reupdateline=rebr.readLine();
                if(updateline.equals(reupdateline))
                {
                    showToast("无需更新，请放心使用！");
                    LogUtils.i("........................onFINISHequal");
                }
                else
                {
                    showToast("发现新模板，请更新！");
                    showDialog();
                    LogUtils.i("....................111....onFINISHnotequal");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        @Override
        public void onFail() {

        }
    };

    private CheckModelBinder mBinder = new CheckModelBinder();
    public CheckModelService() {
    }
    @Override
    public void onCreate() {
        super.onCreate();

        LogUtils.i(TAG, "onCreate() executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "onDestroy() executed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.i(TAG, "onBind() executed");
        return mBinder;
        // TODO: Return the communication channel to the service.

    }


    public class CheckModelBinder extends Binder {

        public void startCheck() {
            showToast("正在检查模板是否需要更新...");
            LogUtils.i("TAG", "startCheck() executed");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 下载
                    Looper.prepare();
                    try {
                        //单文件下载
//                    new FTP().downloadSingleFile("/fff/ftpTest.docx","/mnt/sdcard/download/","ftpTest.docx",new FTP.DownLoadProgressListener(){
                        new FTP().downloadSingleFile(remotePath,exlocalPath,"reupdate.txt",new FTP.DownLoadProgressListener(){
                            @Override
                            public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                Log.i(TAG, currentStep);

                                if(currentStep.equals(MyFTP.FTP_DOWN_SUCCESS)){
                                    Log.i(TAG, "-----xiazai--successful");
                                    listener.onFinish();
                                } else if(currentStep.equals(MyFTP.FTP_DOWN_LOADING)){
                                    Log.i(TAG, "-----xiazai---"+downProcess + "%");

                                }
                                else if(currentStep.equals(MyFTP.FTP_DOWN_FAIL)){
                                    listener.onFail();
                                }
                            }

                        });

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();
        }

    }
    public void showToast(final String message){
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run(){
                Toast.makeText(MyApplication.getApplication().getMainActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void showDialog(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                MyApplication.getApplication().getMainActivity().getRemoteFileSize(false);
            }
        });
    }
}
