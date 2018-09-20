package com.chengbiao.calculator.update.version;

/**
 * 项目名称：BingWallPaper
 * Created by Long on 2018/4/23.
 * 修改时间：2018/4/23 10:56
 */
public class UpdateInfor {
    private String version;
    private String title;
    private String content;
    private String downloadUrl;

    public double getVersion() {
        return Double.parseDouble(version);
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public String toString() {
        String str="";
        str="version:"+getVersion();
        str+="\r\rtitle:"+getTitle();
        str+="\r\rcontent:"+getContent();
        str+="\r\rurl:"+getDownloadUrl();
        return str;
    }
}
