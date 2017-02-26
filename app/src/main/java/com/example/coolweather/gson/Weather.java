package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 蚍蜉 on 2017/2/26.
 * 对之前的几个实体类进行整合，
 * 创建一个总的实体类来引用之前的各个类
 */

public class Weather {

    //记录返回数据是否成功
    public String status;
    //数据基础信息
    public Basic basic;
    //空气状况
    public AQI aqi;
    //当日的info
    public Now now;
    //天气建议
    public Suggestion suggestion;
    //一周天气预测，用一个泛型集合List来存储
    @SerializedName("daily_weather")
    public List<Forecast> forecastList;
}
