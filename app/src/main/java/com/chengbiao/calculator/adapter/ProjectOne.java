package com.chengbiao.calculator.adapter;

/**
 * 项目名称：Calculator20180403
 * Created by Long on 2018/4/3.
 * 修改时间：2018/4/3 19:21
 */
//private String[] name={"序号","项目名称","计量单位","数量","综合单价"};
public class ProjectOne {
    private String serialNumber;
    private String price;
    private String  projectName,unit ,edit_num;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getEdit_num() {
        return edit_num;
    }

    public void setEdit_num(String edit_num) {
        this.edit_num = edit_num;
    }

    public ProjectOne(String serialNumber,String projectName,String unit,  String edit_num ,String price,String description) {
        this.serialNumber = serialNumber;
        this.price = price;
        this.projectName = projectName;
        this.unit = unit;
        this.edit_num = edit_num;
        this.description=description;
    }

    public ProjectOne(){

    }
}
