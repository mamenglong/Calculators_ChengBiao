package com.chengbiao.calculator.update.version;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * 项目名称：BingWallPaper
 * Created by Long on 2018/4/24.
 * 修改时间：2018/4/24 16:02
 */
public interface UpdateRequest_Interface {
    @GET("CheckUpdate.asmx/CheckUpdateInfor")
    Call<UpdateInfor> getCall();
    // 注解里传入 网络请求 的部分URL地址
    // getCall()是接受网络请求数据的方法
}
