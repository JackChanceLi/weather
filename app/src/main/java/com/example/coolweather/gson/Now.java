package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蚍蜉 on 2017/2/26.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{

        @SerializedName("txt")
        public String info;
    }
}
