package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蚍蜉 on 2017/2/26.
 * 对GSON序列中的daily_forecast部分
 * 是一个数组，但是只要实现一天的天气实体类就OK了
 * 使用的时候，使用List集合就ok
 */

public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature{

        public  String max;

        public String min;
    }

    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
