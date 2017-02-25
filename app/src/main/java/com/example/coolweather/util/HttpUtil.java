package com.example.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by 蚍蜉 on 2017/2/17.
 * 服务器交互工具类，用于和服务器网络通信
 * 发起http请求的时候，只要调用sendOkHttpRequest就可以
 * 参数为，请求地址，注册一个回调响应
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        //1.创建一个OkHttpClient实例
        OkHttpClient client = new OkHttpClient();
        //2.创建一个Request实例，传入请求目的地址
        Request request = new Request.Builder().url(address).build();//连缀url方法
        //3.调用newCall方法创建一个Call对象，发起请求
        client.newCall(request).enqueue(callback);
    }
}