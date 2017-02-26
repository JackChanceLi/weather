package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蚍蜉 on 2017/2/26.
 * 对应JSON序列的AQI字段，空气指数
 */

public class AQI {

    public AQICity city;

    public class AQICity{

        public String aqi;

        public String pm25;
    }
}
