package com.chengbiao.calculator.update.model;

import android.os.Looper;
import android.util.Log;

import com.chengbiao.calculator.common.MyApplication;
import com.chengbiao.calculator.ftp.FTP;
import com.chengbiao.calculator.ftp.MyFTP;

import java.io.File;

/**
 * 项目名称：Calculator20180415
 * Created by Long on 2018/9/20.
 * 修改时间：2018/9/20 14:59
 */
public class CheckModel {
    public static String TAG="CheckModel";
    public static String remotePath="/gh/Model/update.txt";
    public static String localPath=MyApplication.getCachePath()+"/Model/";
    private DownloadListener listener;
  public void getRemoteInfor(){
      new Thread(new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            // 下载
            try {
                //单文件下载
//                    new FTP().downloadSingleFile("/fff/ftpTest.docx","/mnt/sdcard/download/","ftpTest.docx",new FTP.DownLoadProgressListener(){
                new FTP().downloadSingleFile(remotePath,localPath,"reupdate.txt",new FTP.DownLoadProgressListener(){
                    @Override
                    public void onDownLoadProgress(String currentStep, long downProcess, String fileName, File file) {
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

    public void setDownloadListener(DownloadListener downloadListener) {
        this.listener = downloadListener;
    }
}
