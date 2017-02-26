package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蚍蜉 on 2017/2/26.
 * 编写对应GSON序列的类，Basic，
 * 主要是基本信息
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
