package com.chengbiao.calculator.ftp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.chengbiao.calculator.MainActivity;
import com.chengbiao.calculator.R;
import com.chengbiao.calculator.common.Common;
import com.chengbiao.calculator.common.MyApplication;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.chengbiao.calculator.common.Common.getFileCachePath;

/**
 * 项目名称：Calculator20180403
 * Created by Long on 2018/4/15.
 * 修改时间：2018/4/15 14:59
 */
public class MyFTP {
    private static final String TAG = "MyFTP";

    public static final String FTP_CONNECT_SUCCESSS = "ftp连接成功";
    public static final String FTP_CONNECT_FAIL = "ftp连接失败";
    public static final String FTP_DISCONNECT_SUCCESS = "ftp断开连接";
    public static final String FTP_FILE_NOTEXISTS = "ftp上文件不存在";

    public static final String FTP_UPLOAD_SUCCESS = "ftp文件上传成功";
    public static final String FTP_UPLOAD_FAIL = "ftp文件上传失败";
    public static final String FTP_UPLOAD_LOADING = "ftp文件正在上传";

    public static final String FTP_DOWN_LOADING = "ftp文件正在下载";
    public static final String FTP_DOWN_SUCCESS = "ftp文件下载成功";
    public static final String FTP_DOWN_FAIL = "ftp文件下载失败";

    public static final String FTP_DELETEFILE_SUCCESS = "ftp文件删除成功";
    public static final String FTP_DELETEFILE_FAIL = "ftp文件删除失败";

    /***
     * ftp上传文件需要指定文件路径和上下文
     * @param mContext
     * @param filePath
     */
    public  void ftpUpload(final Context mContext,String filePath){
        String []suffix=filePath.split("\\.");
        Log.i(TAG, "ftpUpload: "+suffix[suffix.length-1]);
        if(!"xml,doc,docx,txt,pdf".contains(suffix[suffix.length-1]))
        {
            new AlertDialog.Builder(mContext)
                    .setTitle("异常提示")
                    .setMessage("你选择的文件类型不对\n请选择类型为：\nxml,doc,docx,txt的文件！")
                    .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false) //设置点击外面消失
                    .show();
            return ;
        }
        final ProgressDialog pd6 = new ProgressDialog(mContext);
        pd6.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        pd6.setCancelable(true);// 设置是否可以通过点击Back键取消
        pd6.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        pd6.setIcon(R.mipmap.ic_launcher);// 设置提示的title的图标，默认是没有的
        pd6.setTitle("文件上传中");
        pd6.setMessage("上传进度");
        //  String filePath=getFileCachePath()+ File.separator+"20180409.xml";
        final File file = new File(filePath);

        pd6.setMax((int)file.length());
        pd6.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    //单文件上传
                    new FTP().uploadSingleFile(file, "/gh",new FTP.UploadProgressListener(){
                        @Override
                        public void onUploadProgress(String currentStep,long uploadSize,File file) {
                            // TODO Auto-generated method stub
                            Log.i(TAG, currentStep);
                            pd6.setProgress((int)uploadSize);
                            if(currentStep.equals(MyFTP.FTP_UPLOAD_SUCCESS)){
                                pd6.dismiss();
                                Toast.makeText(mContext,"上传完成！",Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "-----shanchuan--successful");
                            } else if(currentStep.equals(MyFTP.FTP_UPLOAD_LOADING)){
                                long fize = file.length();
                                float num = (float)uploadSize / (float)fize;
                                int result = (int)(num * 100);
                                Log.i(TAG, "-----shangchuan---"+result + "%");
                            }
                            else if(currentStep.equals(MyFTP.FTP_UPLOAD_FAIL))
                            {
                                pd6.dismiss();
                                new AlertDialog.Builder(mContext)
                                        .setTitle("上传失败")
                                        .setMessage("文件上传失败，请检查文件或路径！")
                                        .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                        }
                    });

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
        Log.e(TAG, "ftpUpload: "+"end");
    }

    /****
     * ftp下载服务器文件
     * @param serverPath
     * @param fileName 下载后的文件名
     */
    public void ftpDowmload(final Context context,  final String serverPath, final String fileName){
        final ProgressDialog pd6 = new ProgressDialog(context);
        pd6.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        pd6.setCancelable(true);// 设置是否可以通过点击Back键取消
        pd6.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        pd6.setIcon(R.mipmap.ic_launcher);// 设置提示的title的图标，默认是没有的
        pd6.setTitle("文件下载中");
        pd6.setMax(100);
        //  String filePath=getFileCachePath()+ File.separator+"20180409.xml";
        pd6.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 下载
                try {
                    //单文件下载
//                    new FTP().downloadSingleFile("/fff/ftpTest.docx","/mnt/sdcard/download/","ftpTest.docx",new FTP.DownLoadProgressListener(){
                    new FTP().downloadSingleFile(serverPath,MyApplication.getCachePath()+"/Model/",fileName,new FTP.DownLoadProgressListener(){
                        @Override
                        public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                            Log.i(TAG, currentStep);
                            pd6.setProgress((int)(100*downProcess));
                            if(currentStep.equals(MyFTP.FTP_DOWN_SUCCESS)){
                                Log.i(TAG, "-----xiazai--successful");
                                pd6.dismiss();
                                Toast.makeText(context,"下载完成！",Toast.LENGTH_SHORT).show();
                            } else if(currentStep.equals(MyFTP.FTP_DOWN_LOADING)){
                                Log.i(TAG, "-----xiazai---"+downProcess + "%");
                                pd6.setMessage("下载进度-----"+downProcess + "%");
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

    public void ftpMutiDowmload(final Context context, final String serverPath, final ArrayList<String>fileNameList){
        final ProgressDialog pd6 = new ProgressDialog(context);
        pd6.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        pd6.setCancelable(true);// 设置是否可以通过点击Back键取消
        pd6.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        pd6.setIcon(R.mipmap.ic_launcher);// 设置提示的title的图标，默认是没有的
        pd6.setTitle(fileNameList.size()+"个文件下载中...");
        pd6.setMax(100*fileNameList.size());
        //  String filePath=getFileCachePath()+ File.separator+"20180409.xml";
        pd6.show();
        for (final String fileName:fileNameList
             ) {
          final String remotePath=serverPath+fileName;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    // 下载
                    try {
                        //单文件下载
//                    new FTP().downloadSingleFile("/fff/ftpTest.docx","/mnt/sdcard/download/","ftpTest.docx",new FTP.DownLoadProgressListener(){
                        new FTP().downloadSingleFile(remotePath,MyApplication.getCachePath()+"/Model/",fileName,new FTP.DownLoadProgressListener(){
                            @Override
                            public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                Log.i(TAG, currentStep);
                                pd6.setProgress((int)(100*downProcess*(fileNameList.indexOf(fileName)+1)));
                                if(currentStep.equals(MyFTP.FTP_DOWN_SUCCESS)){
                                    Log.i(TAG, "-----xiazai--successful");
                                    if(fileNameList.indexOf(fileName)==(fileNameList.size()-1))
                                         pd6.dismiss();
                                    Toast.makeText(context,"下载完成！",Toast.LENGTH_SHORT).show();
                                } else if(currentStep.equals(MyFTP.FTP_DOWN_LOADING)){
                                    Log.i(TAG, "-----xiazai---"+downProcess + "%");
                                    pd6.setMessage("下载进度-----"+downProcess/fileNameList.size() + "%");
                                }
                            }

                        });

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
Looper.loop();
                }
            }).start();

        }

    }


    /****
     * ftp删除文件
     * @param serverPath
     */
    public void ftpDelete(final String serverPath){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 删除
                try {
//                    new FTP().deleteSingleFile("/fff/ftpTest.docx",new FTP.DeleteFileProgressListener(){
                    new FTP().deleteSingleFile(serverPath,new FTP.DeleteFileProgressListener(){

                        @Override
                        public void onDeleteProgress(String currentStep) {
                            Log.i(TAG, currentStep);
                            if(currentStep.equals(MyFTP.FTP_DELETEFILE_SUCCESS)){
                                Log.i(TAG, "-----shanchu--success");
                            } else if(currentStep.equals(MyFTP.FTP_DELETEFILE_FAIL)){
                                Log.i(TAG, "-----shanchu--fail");
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


    public void getFileSize(final String severPath, final int[] num , ArrayList<String> list)  {
       num[1] = new FTP().getFileList(severPath,list);
    }

}
