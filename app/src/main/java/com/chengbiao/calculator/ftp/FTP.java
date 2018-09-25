package com.chengbiao.calculator.ftp;

/**
 * 项目名称：Calculator20180403
 * Created by Long on 2018/4/9.
 * 修改时间：2018/4/9 23:08
 */


import com.chengbiao.calculator.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FTP {
    /**
     * 服务器名.
     */
    private String hostName;

    /**
     * 端口号
     */
    private int serverPort;

    /**
     * 用户名.
     */
    private String userName;

    /**
     * 密码.
     */
    private String password;

    /**
     * FTP连接.
     */
    private FTPClient ftpClient;

    public FTP() {
//        this.hostName = "182.92.239.46";
//        this.serverPort = 21;
//        this.userName = "byw1278120001";
//        this.password = "zhanglei904";
//
        this.hostName = "118.89.112.146";
        this.serverPort = 21;
        this.userName = "mml";
        this.password = "mml";
        this.ftpClient = new FTPClient();
//        this.hostName = "192.168.1.167";
//        this.serverPort = 21;
//        this.userName = "1519269558@qq.com";
//        this.password = "xiaolong1996.";
//        this.ftpClient = new FTPClient();
    }

    // -------------------------------------------------------文件上传方法------------------------------------------------

    /**
     * 上传单个文件.
     *
     * @param singleFile
     *      本地文件
     * @param remotePath
     *      FTP目录
     * @param listener
     *      监听器
     * @throws IOException
     */
    public void uploadSingleFile(File singleFile, String remotePath,
                                 UploadProgressListener listener) throws IOException {

        // 上传之前初始化
        this.uploadBeforeOperate(remotePath, listener);

        boolean flag;
        flag = uploadingSingle(singleFile, listener);
        if (flag) {
            listener.onUploadProgress(MyFTP.FTP_UPLOAD_SUCCESS, 0,
                    singleFile);
        } else {
            listener.onUploadProgress(MyFTP.FTP_UPLOAD_FAIL, 0,
                    singleFile);
        }

        // 上传完成之后关闭连接
        this.uploadAfterOperate(listener);
    }

    /**
     * 上传多个文件.
     *
     * @param fileList
     *      本地文件
     * @param remotePath
     *      FTP目录
     * @param listener
     *      监听器
     * @throws IOException
     */
    public void uploadMultiFile(LinkedList<File> fileList, String remotePath,
                                UploadProgressListener listener) throws IOException {

        // 上传之前初始化
        this.uploadBeforeOperate(remotePath, listener);

        boolean flag;

        for (File singleFile : fileList) {
            flag = uploadingSingle(singleFile, listener);
            if (flag) {
                listener.onUploadProgress(MyFTP.FTP_UPLOAD_SUCCESS, 0,
                        singleFile);
            } else {
                listener.onUploadProgress(MyFTP.FTP_UPLOAD_FAIL, 0,
                        singleFile);
            }
        }

        // 上传完成之后关闭连接
        this.uploadAfterOperate(listener);
    }

    /**
     * 上传单个文件.
     *
     * @param localFile
     *      本地文件
     * @return true上传成功, false上传失败
     * @throws IOException
     */
    private boolean uploadingSingle(File localFile,
                                    UploadProgressListener listener) throws IOException {
        boolean flag = true;
        // 不带进度的方式
        // // 创建输入流
        // InputStream inputStream = new FileInputStream(localFile);
        // // 上传单个文件
        // flag = ftpClient.storeFile(localFile.getName(), inputStream);
        // // 关闭文件流
        // inputStream.close();

        // 带有进度的方式
        BufferedInputStream buffIn = new BufferedInputStream(
                new FileInputStream(localFile));
        ProgressInputStream progressInput = new ProgressInputStream(buffIn,
                listener, localFile);
        flag = ftpClient.storeFile(localFile.getName(), progressInput);
        buffIn.close();

        return flag;
    }

    /**
     * 上传文件之前初始化相关参数
     *
     * @param remotePath
     *      FTP目录
     * @param listener
     *      监听器
     * @throws IOException
     */
    private void uploadBeforeOperate(String remotePath,
                                     UploadProgressListener listener) throws IOException {

        // 打开FTP服务
        try {
            this.openConnect();
            listener.onUploadProgress(MyFTP.FTP_CONNECT_SUCCESSS, 0,
                    null);
        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onUploadProgress(MyFTP.FTP_CONNECT_FAIL, 0, null);
            return;
        }

        // 设置模式
        ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
        // FTP下创建文件夹
        ftpClient.makeDirectory(remotePath);
        // 改变FTP目录
        ftpClient.changeWorkingDirectory(remotePath);
        // 上传单个文件

    }

    /**
     * 上传完成之后关闭连接
     *
     * @param listener
     * @throws IOException
     */
    private void uploadAfterOperate(UploadProgressListener listener)
            throws IOException {
        this.closeConnect();
        listener.onUploadProgress(MyFTP.FTP_DISCONNECT_SUCCESS, 0, null);
    }

    // -------------------------------------------------------文件下载方法------------------------------------------------

    /****
     * 获取远程文件列表
     * @param serverPath
     * @return
     * @throws Exception
     */
    public int getFileList(String serverPath, ArrayList<String> list) {
        int num=0 ;
        // 打开FTP服务
        try {
            this.openConnect();
            FTPFile[]files= ftpClient.listFiles(serverPath);
            if (files.length != 0) {
                num=files.length;
                for (FTPFile F:files
                        ) {
                    list.add(F.getName());
                }
            }
        }
        catch (IOException e1){
            e1.printStackTrace();

        }
        // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉


        try {
            this.closeConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 先判断服务器文件是否存在
        return num;
    }

    /**
     * 下载单个文件，可实现断点下载.
     *
     * @param serverPath
     *      Ftp目录及文件路径
     * @param localPath
     *      本地目录
     * @param fileName
     *      下载之后的文件名称
     * @param listener
     *      监听器
     * @throws IOException
     */
    public void downloadSingleFile(String serverPath, String localPath, String fileName, DownLoadProgressListener listener)
            throws Exception {

        // 打开FTP服务
        try {
            this.openConnect();
            listener.onDownLoadProgress(MyFTP.FTP_CONNECT_SUCCESSS, 0,fileName, null);
        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onDownLoadProgress(MyFTP.FTP_CONNECT_FAIL, 0,fileName, null);
            return;
        }

        // 先判断服务器文件是否存在
        FTPFile[] files = ftpClient.listFiles(serverPath);
        if (files.length == 0) {
            listener.onDownLoadProgress(MyFTP.FTP_FILE_NOTEXISTS, 0,fileName, null);
            return;
        }

        //创建本地文件夹
        File mkFile = new File(localPath);
        if (!mkFile.exists()) {
            mkFile.mkdirs();
        }

        localPath = localPath+File.separator + fileName;
        // 接着判断下载的文件是否能断点下载
        long serverSize = files[0].getSize(); // 获取远程文件的长度
        File localFile = new File(localPath);
        long localSize = 0;
        if (localFile.exists()) {
            localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
            if (localSize >= serverSize) {
                File file = new File(localPath);
                file.delete();
            }
        }

        // 进度
        // long step = (serverSize+100)/100;
        long process = 0;
        long currentSize = 0;
        // 开始准备下载文件
        RandomAccessFile raf = new RandomAccessFile(localFile, "rwd");
        raf.seek(raf.length());
        OutputStream out = new FileOutputStream(localFile, true);
        ftpClient.setRestartOffset(0);//不进行断点续传
        InputStream input = ftpClient.retrieveFileStream(serverPath);
        LogUtils.i("FTP","allsize:"+serverSize);
        byte[] b = new byte[1024];
        int length = 0;
        while ((length = input.read(b)) != -1) {
            LogUtils.i("FTP"," @@@currentSize:"+currentSize+"    length:"+length);
            raf.write(b,0,length);
            //out.write(b, 0, length);
            currentSize = currentSize + length;
            if (currentSize*100  / serverSize != process) {
                process = currentSize*100/ serverSize;
                if (process % 5 == 0) { //每隔%5的进度返回一次
                    listener.onDownLoadProgress(MyFTP.FTP_DOWN_LOADING, process,fileName, null);
                }
            }
        }
        raf.close();
//        out.flush();
//        out.close();
        input.close();

        // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
        if (ftpClient.completePendingCommand()) {
            listener.onDownLoadProgress(MyFTP.FTP_DOWN_SUCCESS, 0,fileName, new File(localPath));
        } else {
            listener.onDownLoadProgress(MyFTP.FTP_DOWN_FAIL, 0, fileName,null);
        }

        // 下载完成之后关闭连接
        this.closeConnect();
        listener.onDownLoadProgress(MyFTP.FTP_DISCONNECT_SUCCESS, 0,fileName, null);

        return;
    }

    public void downloadMutiFile(String serverPath, String localPath1, ArrayList<String>fileNameList, DownLoadProgressListener listener)
            throws Exception {
        // 打开FTP服务
        try {
            this.openConnect();
            listener.onDownLoadProgress(MyFTP.FTP_CONNECT_SUCCESSS, 0,"", null);
        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onDownLoadProgress(MyFTP.FTP_CONNECT_FAIL, 0, "",null);
            return;
        }

        for (String fileName:fileNameList
                ) {
            String localPath=localPath1;
            // 先判断服务器文件是否存在
            FTPFile[] files = ftpClient.listFiles(serverPath+fileName);
            if (files.length == 0) {
                listener.onDownLoadProgress(MyFTP.FTP_FILE_NOTEXISTS, 0, fileName,null);
                return;
            }

            //创建本地文件夹
            File mkFile = new File(localPath);
            if (!mkFile.exists()) {
                mkFile.mkdirs();
            }

            localPath = localPath+File.separator + fileName;
            // 接着判断下载的文件是否能断点下载
            long serverSize = files[0].getSize(); // 获取远程文件的长度
            File localFile = new File(localPath);
            long localSize = 0;
            if (localFile.exists()) {
                localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
                if (localSize >= serverSize) {
                    File file = new File(localPath);
                    file.delete();
                }
            }

            // 进度
            // long step = (serverSize+100)/100;
            long process = 0;
            long currentSize = 0;
            // 开始准备下载文件
            OutputStream out = new FileOutputStream(localFile, true);
            ftpClient.setRestartOffset(localSize);
            InputStream input = ftpClient.retrieveFileStream(serverPath+fileName);
            byte[] b = new byte[1024];
            int length = 0;
            while ((length = input.read(b)) != -1) {
                out.write(b, 0, length);
                currentSize = currentSize + length;
                if (currentSize*100  / serverSize != process) {
                    process = currentSize*100/ serverSize;
                    if (process % 5 == 0) { //每隔%5的进度返回一次
                        listener.onDownLoadProgress(MyFTP.FTP_DOWN_LOADING, process,fileName, null);
                    }
                }
            }
            out.flush();
            out.close();
            input.close();

            // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
            if (ftpClient.completePendingCommand()) {
                listener.onDownLoadProgress(MyFTP.FTP_DOWN_SUCCESS, 0,fileName, new File(localPath));
            } else {
                listener.onDownLoadProgress(MyFTP.FTP_DOWN_FAIL, 0, fileName,null);
            }
        }
        // 下载完成之后关闭连接
        this.closeConnect();
        listener.onDownLoadProgress(MyFTP.FTP_DISCONNECT_SUCCESS, 0, "",null);

        return;
    }
    // -------------------------------------------------------文件删除方法------------------------------------------------

    /**
     * 删除Ftp下的文件.
     *
     * @param serverPath
     *      Ftp目录及文件路径
     * @param listener
     *      监听器
     * @throws IOException
     */
    public void deleteSingleFile(String serverPath, DeleteFileProgressListener listener)
            throws Exception {

        // 打开FTP服务
        try {
            this.openConnect();
            listener.onDeleteProgress(MyFTP.FTP_CONNECT_SUCCESSS);
        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onDeleteProgress(MyFTP.FTP_CONNECT_FAIL);
            return;
        }

        // 先判断服务器文件是否存在
        FTPFile[] files = ftpClient.listFiles(serverPath);
        if (files.length == 0) {
            listener.onDeleteProgress(MyFTP.FTP_FILE_NOTEXISTS);
            return;
        }

        //进行删除操作
        boolean flag = true;
        flag = ftpClient.deleteFile(serverPath);
        if (flag) {
            listener.onDeleteProgress(MyFTP.FTP_DELETEFILE_SUCCESS);
        } else {
            listener.onDeleteProgress(MyFTP.FTP_DELETEFILE_FAIL);
        }

        // 删除完成之后关闭连接
        this.closeConnect();
        listener.onDeleteProgress(MyFTP.FTP_DISCONNECT_SUCCESS);

        return;
    }

    // -------------------------------------------------------打开关闭连接------------------------------------------------

    /**
     * 打开FTP服务.
     *
     * @throws IOException
     */
    public void openConnect() throws IOException {
        // 中文转码
        ftpClient.setControlEncoding("UTF-8");
        int reply; // 服务器响应值
        // 连接至服务器
        ftpClient.connect(hostName, serverPort);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        }
        // 登录到服务器
        ftpClient.login(userName, password);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        } else {
            // 获取登录信息
            FTPClientConfig config = new FTPClientConfig(ftpClient
                    .getSystemType().split(" ")[0]);
            config.setServerLanguageCode("zh");
            ftpClient.configure(config);
            // 使用被动模式设为默认
            ftpClient.enterLocalPassiveMode();
            // 二进制文件支持
            ftpClient
                    .setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
        }
    }

    /**
     * 关闭FTP服务.
     *
     * @throws IOException
     */
    public void closeConnect() throws IOException {
        if (ftpClient != null) {
            // 退出FTP
            ftpClient.logout();
            // 断开连接
            ftpClient.disconnect();
        }
    }

    // ---------------------------------------------------上传、下载、删除监听---------------------------------------------

    /*
     * 上传进度监听
     */
    public interface UploadProgressListener {
        public void onUploadProgress(String currentStep, long uploadSize, File file);
    }

    /*
     * 下载进度监听
     */
    public interface DownLoadProgressListener {
        public void onDownLoadProgress(String currentStep, long downProcess, String fileName,File file);
    }

    /*
     * 文件删除监听
     */
    public interface DeleteFileProgressListener {
        public void onDeleteProgress(String currentStep);
    }

}