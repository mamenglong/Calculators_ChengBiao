package com.chengbiao.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.chengbiao.calculator.ftp.FTP;

import java.io.File;
import java.io.IOException;

import static com.chengbiao.calculator.common.Common.getFileCachePath;

public class FTPActivity extends AppCompatActivity {
    private static final String TAG = "FTPActivity";

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp);
        initView();
    }
    private void initView() {

        //上传功能
        //new FTP().uploadMultiFile为多文件上传
        //new FTP().uploadSingleFile为单文件上传
        Button buttonUpload = (Button) findViewById(R.id.button_upload);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String filePath=getFileCachePath()+File.separator+"20180409.xml";
                        // 上传
                        File file = new File(filePath);
                        try {
                            //单文件上传
                            new FTP().uploadSingleFile(file, "/gh",new FTP.UploadProgressListener(){
                                @Override
                                public void onUploadProgress(String currentStep,long uploadSize,File file) {
                                    // TODO Auto-generated method stub
                                    Log.i(TAG, currentStep);
                                    if(currentStep.equals(FTPActivity.FTP_UPLOAD_SUCCESS)){
                                        Log.i(TAG, "-----shanchuan--successful");
                                    } else if(currentStep.equals(FTPActivity.FTP_UPLOAD_LOADING)){
                                        long fize = file.length();
                                        float num = (float)uploadSize / (float)fize;
                                        int result = (int)(num * 100);
                                        Log.i(TAG, "-----shangchuan---"+result + "%");
                                    }
                                }
                            });
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });

        //下载功能
        Button buttonDown = (Button)findViewById(R.id.button_down);
        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 下载
                        try {

                            //单文件下载
                            new FTP().downloadSingleFile("/fff/ftpTest.docx","/mnt/sdcard/download/","ftpTest.docx",new FTP.DownLoadProgressListener(){

                                @Override
                                public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                    Log.i(TAG, currentStep);
                                    if(currentStep.equals(FTPActivity.FTP_DOWN_SUCCESS)){
                                        Log.i(TAG, "-----xiazai--successful");
                                    } else if(currentStep.equals(FTPActivity.FTP_DOWN_LOADING)){
                                        Log.i(TAG, "-----xiazai---"+downProcess + "%");
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
        });

        //删除功能
        Button buttonDelete = (Button)findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 删除
                        try {

                            new FTP().deleteSingleFile("/fff/ftpTest.docx",new FTP.DeleteFileProgressListener(){

                                @Override
                                public void onDeleteProgress(String currentStep) {
                                    Log.i(TAG, currentStep);
                                    if(currentStep.equals(FTPActivity.FTP_DELETEFILE_SUCCESS)){
                                        Log.i(TAG, "-----shanchu--success");
                                    } else if(currentStep.equals(FTPActivity.FTP_DELETEFILE_FAIL)){
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
        });

    }
}